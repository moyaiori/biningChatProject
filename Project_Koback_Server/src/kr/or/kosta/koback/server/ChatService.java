package kr.or.kosta.koback.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.DefaultComboBoxModel;

import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.model.RoomManager;
import kr.or.kosta.koback.model.User;
import kr.or.kosta.koback.model.UserDao;
import kr.or.kosta.koback.util.Validator;
import kr.or.kosta.koback.view.UserModel;

/**
 * 채팅 클라이언트의 요청메시지를 수신하여 서비스 제공 핵심 기능 담당(모든 프로토콜 메세지를 송수신)
 * 
 * @author 유안상
 */
public class ChatService extends Thread {
	private Socket socket;
	private ChatServer chatServer;
	private DataInputStream in;
	private DataOutputStream out;
	private String requestUserId;
	private String nickName;
	private boolean stop;
	private UserDao userDao;
	private RoomManager roomManager;
	private boolean master;
	private String[] allUser;
	private String idList;

	protected UserModel userModel;
	protected DefaultComboBoxModel<String> cbModel;

	private ProtocolMethod protocolMethod;
	private int enterCount = 1;

	public ChatService() {
	}

	public ChatService(Socket socket, ChatServer chatServer, UserModel userModel, DefaultComboBoxModel<String> model,
			RoomManager roomManager) throws IOException {
		this.socket = socket;
		this.chatServer = chatServer;
		this.userModel = userModel;
		this.cbModel = model;
		this.roomManager = roomManager;
		protocolMethod = new ProtocolMethod(userModel, cbModel, roomManager, chatServer, chatServer.getIa());
		userDao = new UserDao();
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
	}

	/**
	 * 일괄로 채팅클라이언트의 요청메세지 파싱 및 서비스
	 */
	public void handleRequest() throws IOException {
		setEvent();
		try {
			while (!stop) {
				String requestMessage = in.readUTF();
				String[] token = requestMessage.split(MessageType.DELIMETER);
				String requestCode = token[0];
				requestUserId = token[1];
				idList = chatServer.getManager().getAllId();

				switch (requestCode) {
				case MessageType.C_LOGIN: // [100] 로그인 요청
					String passwd = token[2];
					protocolMethod.login(requestUserId, passwd, this, userDao);
					break;
				case MessageType.C_JOIN: // [102] 회원가입 승인 요청
					String name = token[2];
					String nick = token[3];
					String pass = token[4];
					String ssn = token[5];
					String email = token[6];
					String phoneNum = token[7];
					if (joinCheck(requestUserId, name, nick, pass, ssn, email, phoneNum)) {
						userDao.addUser(new User(requestUserId, name, nick, pass, ssn, email, phoneNum));
						sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + true + MessageType.DELIMETER
								+ "정상적으로 완료 되었습니다.");
					}
					break;
				case MessageType.C_OPEN: // [200] 방 오픈 요청
					String roomName = token[2];
					int maxUserNum = Integer.parseInt(token[3]);
					protocolMethod.commonOpen(requestUserId, roomName, maxUserNum, this);
					break;
				case MessageType.C_SECRET_OPEN:
					String sRoomName = token[2];
					int sMaxUserNum = Integer.parseInt(token[3]);
					String sPass = token[4];
					protocolMethod.secretOpen(requestUserId, sRoomName, sMaxUserNum, sPass, this);
					break;
				case MessageType.C_ENTRY: // [210] 방 입장 요청
					int roomNum = Integer.parseInt(token[2]);
					String eRoomName = token[3];
					protocolMethod.entryRoom(requestUserId, roomNum, eRoomName, this);
					break;
				case MessageType.C_SECRET_ENTRY: // [212] 비밀 방 입장 요청
					int sRoomNum = Integer.parseInt(token[2]);
					String entryPass = token[3];
					String esRoomName = token[4];
					protocolMethod.entrySecretRoom(requestUserId, sRoomNum, esRoomName, entryPass, this);
					break;
				case MessageType.C_INVITE_ENTRY: // [214] 초대시 방입장
					int iRoomNum = Integer.parseInt(token[2]);
					String iRoomName = token[3];
					protocolMethod.entryInviteRoom(requestUserId, iRoomNum, iRoomName, this);
					break;
				case MessageType.C_LOGOUT: // [230] 클라이언트가 접속 종료
					int logoutRoomNum = Integer.parseInt(token[2]);
					if (logoutRoomNum == 0) {
						removeClient(this);
						roomManager.getRoomList().get(0).getClients().remove(requestUserId);
						userModel.logoutUser(requestUserId);
						cbModel.removeElement(requestUserId);
						String userList = protocolMethod.waitUserList();
						chatServer.sendWaitMessage(MessageType.S_REQUEST_WAITING_LIST + MessageType.DELIMETER
								+ requestUserId + MessageType.DELIMETER + userList);
					} else {
						protocolMethod.exitRoom(requestUserId, logoutRoomNum, this, false);
						removeClient(this);
						roomManager.getRoomList().get(0).getClients().remove(requestUserId);
						userModel.logoutUser(requestUserId);
						cbModel.removeElement(requestUserId);
						String userList = protocolMethod.waitUserList();
						chatServer.sendWaitMessage(MessageType.S_REQUEST_WAITING_LIST + MessageType.DELIMETER
								+ requestUserId + MessageType.DELIMETER + userList);
					}
					break;
				// 300번대(채팅 방 메시지관련)
				case MessageType.SC_CHAT_MESSAGE: // [300] 방에 있는 사람끼리 일반메시지 보내기
					int roomNumber = Integer.parseInt(token[2]);
					chatServer.sendChatRoomMessage(requestMessage, roomNumber);
					break;
				case MessageType.SC_CHAT_EMOTICON: // [301] 방에 있는 사람끼리 이모티콘 보내기
					roomNumber = Integer.parseInt(token[2]);
					chatServer.sendChatRoomMessage(requestMessage, roomNumber);
					break;
				case MessageType.SC_WHISPER: // [302] 귓속말 보내기
					int whisperRoomNum = Integer.parseInt(token[2]); // 귓속말주고받는
																		// 방 번호
					String whisperOpponent = token[3]; // 귓속말 받는 상대의 아이디
					String message = token[4]; // 귓속말 내용
					chatServer.commonWhisperMessage(requestMessage, whisperOpponent, whisperRoomNum, requestUserId);
					break;

				case MessageType.C_INVITE_USER: // [303] 대기실 인원 초대
					requestUserId = token[1]; // 초대를 요청하는 사람
					String inviteRoomNum = token[2];// 초대한사람의 방번호
					String inviteUser = token[3];// 초대받는사람이름
					String roomTitle = token[4];// 방 제목
					chatServer.sendInviteRequest(requestUserId, inviteRoomNum,
							roomManager.getRoomList().get(0).getClients().get(inviteUser));

					break;
				case MessageType.C_KICK: // [307] 방장이 해당유저를 강퇴시킴
					int kickRoomNum = Integer.parseInt(token[2]);
					String kickId = token[3];
					ChatService client = roomManager.getRoomList().get(kickRoomNum).getClients().get(kickId); // 강퇴당할
					boolean kick = true; // 아이의
					// chatService
					protocolMethod.exitRoom(kickId, kickRoomNum, client, kick);
					break;
				case MessageType.SC_EXIT: // [308] 해당방에서 나감
					int exitRoomNum = Integer.parseInt(token[2]);
					boolean checkKick = false;
					protocolMethod.exitRoom(requestUserId, exitRoomNum, this, checkKick);
					break;
				case MessageType.SC_REQUEST_WAITING_LIST: // [309] 대기실 유저 리스트 요청
					String waitUserList = protocolMethod.waitUserList();
					sendMessage(MessageType.SC_REQUEST_WAITING_LIST + MessageType.DELIMETER + requestUserId
							+ MessageType.DELIMETER + waitUserList); // 311
					break;
				case MessageType.SC_REQUEST_WAITING_ROOMLIST: // [310] 대기실 요청
					String roomList = protocolMethod.sendRoomList();
					sendMessage(MessageType.SC_REQUEST_WAITING_ROOMLIST + MessageType.DELIMETER + requestUserId
								+ MessageType.DELIMETER + roomList); // 312
					break; 
				case MessageType.C_INVITE_RESULT: // [315]초대에 대한 응답
					requestUserId = token[1]; // 초대를 받은 살마의 ID(C_INVITE_RESULT를
												// 보내는 사람의 ID)
					String masterId = token[2]; // 이 초대를 보낸 유저 ID
					boolean isInvite = Boolean.parseBoolean(token[3]); // 초대에 대한
																		// 승락 or
																		// 거절
					if (isInvite) {
						// 유저 수락
						chatServer.isSendInviteRequest(requestUserId, masterId, isInvite);
					} else {
						// 유저 거부
						chatServer.isSendInviteRequest(requestUserId, masterId, isInvite);
					}
					break;
				case MessageType.SC_REQUEST_ROOM_INFO:
					String waitUserList1 = protocolMethod.waitUserList();
					String roomNum_invite = null;
					roomTitle = null;

					Enumeration<Integer> e = roomManager.getRoomList().keys();
					while (e.hasMoreElements()) {
						Integer roomNum_inWhile = (Integer) e.nextElement();
						Enumeration<String> e1 = roomManager.getRoomList().get(roomNum_inWhile).getClients().keys();
						while (e1.hasMoreElements()) {
							String string = (String) e1.nextElement();
							if (string.equals(requestUserId)) {
								roomManager.getRoomList().get(roomNum_inWhile);
								roomNum_invite = roomNum_inWhile.toString();
								roomTitle = roomManager.getRoomList().get(roomNum_inWhile).getName();
							}
						}
					}
					sendMessage(MessageType.SC_REQUEST_ROOM_INFO + MessageType.DELIMETER + requestUserId
							+ MessageType.DELIMETER + waitUserList1 + MessageType.DELIMETER + roomNum_invite
							+ MessageType.DELIMETER + roomTitle);
					break;

				case MessageType.C_UPLOAD:
					String upRoomNumber = token[2];
					String upFileName = token[3];
					long upFileSize = Long.parseLong(token[4]);
					boolean upload = true;
					FileShareRoom uploadRoom = new FileShareRoom(this, true);

					int upPort = uploadRoom.getPort();
					String upIp = uploadRoom.getServerIp();
					chatServer.sendAllMessage(MessageType.S_UPLOAD_RESULT + MessageType.DELIMETER + "aa"
							+ MessageType.DELIMETER + upFileName + MessageType.DELIMETER + true + MessageType.DELIMETER
							+ upPort + MessageType.DELIMETER + upIp);

					uploadRoom.connectListening();
					uploadRoom.setFileName(upFileName);
					uploadRoom.setFileSize(upFileSize);
					uploadRoom.start();
					break;
				case MessageType.C_SHOW_FILE_LIST: // 404-405번
					Hashtable<String, Long> files = sendFileList();
					Enumeration<String> filename = files.keys();
					StringBuffer names = new StringBuffer();
					StringBuffer size = new StringBuffer();
					while (filename.hasMoreElements()) {
						String file = filename.nextElement();
						names.append(file + ",");
						size.append(files.get(file) + ",");
					}
					sendMessage(MessageType.S_SHOW_FILE_LIST + MessageType.DELIMETER + requestUserId
							+ MessageType.DELIMETER + names + MessageType.DELIMETER + size);
					break;
				case MessageType.C_DOWNLOAD: // 420번
					boolean download = false;
					String downFileName = token[2];
					FileShareRoom downloadRoom = new FileShareRoom(this, download);
					int downPort = downloadRoom.getPort();
					String downIp = downloadRoom.getServerIp();
					chatServer.sendAllMessage(MessageType.S_FILE_DOWNLOAD + MessageType.DELIMETER + "aa"
							+ MessageType.DELIMETER + downFileName + MessageType.DELIMETER + true
							+ MessageType.DELIMETER + downPort + MessageType.DELIMETER + downIp);
					downloadRoom.connectListening();
					downloadRoom.setFileName(downFileName);
					downloadRoom.start();
					break;
				}
			}
		} finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
			if (socket != null)
				socket.close();
		}
	}

	public void setEvent() {
		chatServer.getServerFrame().getSouthPanel().getAdminTF().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String noticeMessage = chatServer.getServerFrame().getSouthPanel().getAdminTF().getText().trim();
				String userId = (String) (chatServer.getServerFrame().getSouthPanel().getAllUserCb().getSelectedItem());
				if (noticeMessage.length() != 0) {
					if (chatServer.getServerFrame().getSouthPanel().getAllUserCb().getSelectedItem().equals("전체"))
						chatServer.sendAllMessage(MessageType.S_NOTICE + MessageType.DELIMETER + "공지사항"
								+ MessageType.DELIMETER + noticeMessage);
					else
						try {
							chatServer.getClientManager().getClients().get(userId)
									.sendMessage(MessageType.S_ADMIN_WHISPER + MessageType.DELIMETER + "Admin"
											+ MessageType.DELIMETER + noticeMessage);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
				}
				chatServer.getServerFrame().getSouthPanel().getAdminTF().setText(" ");
			}
		});
	}

	public void sendMessage(String message) throws IOException {
		out.writeUTF(message);
		out.flush();
	}

	/** 종료 클라이언트 제거 */
	public void removeClient(ChatService client) {
		chatServer.getManager().removeClient(client);
	}

	/**
	 * 메시지를 보낸 클라이언트가 접속한 채팅방의 접속자 리스트
	 * 
	 * @param client
	 */
	public Hashtable<String, ChatService> userByRoom(int roomNum) {
		return roomManager.getRoomList().get(roomNum).getClients();
	}

	@Override
	public void run() {
		try {
			handleRequest();
		} catch (IOException e) {
			// GUIUtil.showErrorMessage("상대방이 접속 종료.\n");
		}
	}

	/*
	 * 회원가입 유효성 검사 메소드 ID, 닉네임, 이름 , 비밀번호, 주민등록번호, email, 연락처
	 */
	public boolean joinCheck(String id, String name, String nick, String pass, String ssn, String email,
			String phoneNum) throws IOException {

		/** ID유효성검사 */
		if (id.equals("")) {
			sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + false + MessageType.DELIMETER
					+ MessageType.ISNULL_INPUT);
			return false;
		} else if (userDao.overlapId(id)) {
			sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + false + MessageType.DELIMETER
					+ MessageType.DUPLICATE_ID);
			return false;
		} else if (!Validator.validId(id)) {
			sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + false + MessageType.DELIMETER
					+ MessageType.LIMIT_LENGTH_ID);
			return false;

			/** 닉네임 유효성검사 */
		} else if (userDao.overlapNick(nick)) {
			sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + false + MessageType.DELIMETER
					+ MessageType.DUPLICATE_NICK);
			return false;
		} else if (!Validator.validNick(nick)) {
			sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + false + MessageType.DELIMETER
					+ MessageType.LIMIT_LENGTH_NICK);
			return false;
		} else if (nick.equals("")) {
			sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + false + MessageType.DELIMETER
					+ MessageType.ISNULL_INPUT);
			return false;

			/** 이름 유효성 검사 */
		} else if (name.equals("")) {
			sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + false + MessageType.DELIMETER
					+ MessageType.ISNULL_INPUT);
			return false;
		} else if (!Validator.validName(name)) {
			sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + false + MessageType.DELIMETER
					+ MessageType.LIMIT_LENGTH_NAME);
			return false;

			/** 비밀번호 유효성검사 */
		} else if (pass.equals("")) {
			sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + false + MessageType.DELIMETER
					+ MessageType.ISNULL_INPUT);
			return false;
		} else if (!Validator.validPass(pass)) {
			sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + false + MessageType.DELIMETER
					+ MessageType.LIMIT_LENGTH_PASS);
			return false;

			/** 주민등록번호 유효성검사 */
		} else if (ssn.equals("")) {
			sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + false + MessageType.DELIMETER
					+ MessageType.ISNULL_INPUT);
			return false;
		} else if (userDao.overlapSsn(ssn)) {
			sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + false + MessageType.DELIMETER
					+ MessageType.DUPLICATE_SSN);
			return false;
		} else if (!Validator.validSsn(ssn)) {
			sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + false + MessageType.DELIMETER
					+ MessageType.WRONG_SSN);
			return false;

			/** Email 유효성검사 */
		} else if (email.equals("")) {
			sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + false + MessageType.DELIMETER
					+ MessageType.ISNULL_INPUT);
			return false;
		} else if (userDao.overlapEmail(email)) {
			sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + false + MessageType.DELIMETER
					+ MessageType.DUPLICATE_EMAIL);
			return false;
		} else if (!Validator.validEmail(email)) {
			sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + false + MessageType.DELIMETER
					+ MessageType.WRONG_EMAIL);
			return false;

			/** 연락처 유효성검사 */
		} else if (phoneNum.equals("")) {
			sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + false + MessageType.DELIMETER
					+ MessageType.ISNULL_INPUT);
			return false;
		} else if (!Validator.validMobileNumber(phoneNum)) {
			sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + false + MessageType.DELIMETER
					+ MessageType.WRONG_PHONENUM);
			return false;
		}
		return true;
	}

	/**
	 * 로그인 유효성검사 메소드 로그인시 file에 저장되있는 ID 찾기, ID로 PASSWD 찾기
	 */
	public boolean loginCheck(String id, String pass) throws IOException {
		if (!userDao.onlyIdSearch(id)) {
			sendMessage(MessageType.S_LOGIN_RESULT + MessageType.DELIMETER + id + MessageType.DELIMETER + false
					+ MessageType.DELIMETER + MessageType.WRONG_ID + MessageType.DELIMETER + "dummyMessage");
			return false;
		} else if (id.equals("")) {
			sendMessage(MessageType.S_LOGIN_RESULT + MessageType.DELIMETER + id + MessageType.DELIMETER + false
					+ MessageType.DELIMETER + MessageType.ISNULL_INPUT + MessageType.DELIMETER + "dummyMessage");
			return false;
		} else if (pass.equals("")) {
			sendMessage(MessageType.S_LOGIN_RESULT + MessageType.DELIMETER + id + MessageType.DELIMETER + false
					+ MessageType.DELIMETER + MessageType.ISNULL_INPUT + MessageType.DELIMETER + "dummyMessage");
			return false;
		} else if (!userDao.idSearchPasswd(id).equals(pass)) {
			sendMessage(MessageType.S_LOGIN_RESULT + MessageType.DELIMETER + id + MessageType.DELIMETER + false
					+ MessageType.DELIMETER + MessageType.WRONG_LOGIN_PASS + MessageType.DELIMETER + "dummyMessage");
			return false;
		}
		return true;
	}

	public Hashtable<String, Long> sendFileList() throws IOException {
		File directory = new File(FileShareRoom.FILE_DRECTORY);
		// 베이스디렉토리가 존재하지 않을 경우 디렉토리 생성
		if (!directory.exists()) {
			directory.mkdir();
		}

		File[] list = directory.listFiles();
		Hashtable<String, Long> files = new Hashtable<String, Long>();

		File file2 = null;
		for (File file : list) {
			String name = file.getName();
			file2 = new File(FileShareRoom.FILE_DRECTORY, name);
			long fileSize = file2.length();
			files.put(name, fileSize);
		}

		return files;
	}

	public boolean filterMgr(String msg) {
		boolean flag = true;// false이면 금지어 아님
		String str[] = { "바보", "개새끼", "새끼", "자바", "java" };

		StringTokenizer st = new StringTokenizer(msg);
		String msgs[] = new String[st.countTokens()];
		for (int i = 0; i < msgs.length; i++) {
			msgs[i] = st.nextToken();
		}
		for (int i = 0; i < str.length; i++) {
			if (flag)
				break;
			for (int j = 0; j < msgs.length; j++) {
				if (str[i].equals(msgs[j])) {
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	public String getUserId() {
		return requestUserId;
	}

	public void setUserId(String userId) {
		this.requestUserId = userId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String[] getAllUser() {
		return allUser;
	}

	public void setAllUser(String[] allUser) {
		this.allUser = allUser;
	}

	public RoomManager getRoomManager() {
		return roomManager;
	}

	public void setRoomManager(RoomManager roomManager) {
		this.roomManager = roomManager;
	}

	public boolean getMaster() {
		return master;
	}

	public void setMaster(boolean master) {
		this.master = master;
	}

	public int getEnterCount() {
		return enterCount;
	}

	public void setEnterCount(int enterCount) {
		this.enterCount = enterCount;
	}

}