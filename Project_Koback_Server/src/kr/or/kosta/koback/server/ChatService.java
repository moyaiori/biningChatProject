package kr.or.kosta.koback.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.DefaultComboBoxModel;

import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;

import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.model.Room;
import kr.or.kosta.koback.model.RoomManager;
import kr.or.kosta.koback.model.User;
import kr.or.kosta.koback.model.UserDao;
import kr.or.kosta.koback.util.GUIUtil;
import kr.or.kosta.koback.view.UserModel;
import kr.or.kosta.koback.view.roomTableModel;

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

	private String[] allUser;
	private String idList;

	protected UserModel userModel;
	protected DefaultComboBoxModel<String> cbModel;

	private ProtocolMethod protocolMethod;

	public ChatService() {
	}

	public ChatService(Socket socket, ChatServer chatServer, UserModel userModel, DefaultComboBoxModel<String> model,
			RoomManager roomManager) throws IOException {
		this.socket = socket;
		this.chatServer = chatServer;
		this.userModel = userModel;
		this.cbModel = model;
		this.roomManager = roomManager;
		protocolMethod = new ProtocolMethod(userModel, cbModel, roomManager, chatServer);
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
				String idList = chatServer.getManager().getAllId();

				switch (requestCode) {
				case MessageType.C_LOGIN: // [100] 로그인 요청
					String passwd = token[2];
					protocolMethod.login(requestUserId, passwd, this);
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
						sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + requestUserId
								+ MessageType.DELIMETER + true);
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
					removeClient(this);
					roomManager.getRoomList().get(0).getClients().remove(requestUserId);
					userModel.logoutUser(requestUserId);
					cbModel.removeElement(requestUserId);
					String userList = protocolMethod.waitUserList();
					chatServer.sendWaitMessage(MessageType.S_REQUEST_WAITING_LIST + MessageType.DELIMETER + userList); // 나가는
																														// 아이
																														// 알려줌
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
					chatServer.commonWhisperMessage(message, whisperOpponent, whisperRoomNum);
					break;
				case MessageType.C_INVITE_USER: // [303] 대기실 인원 초대
					String masterId = token[1]; // 초대하고자하는사람
					String inviteRoomNum = token[2];// 대기실인지 확인 -> 0으로 보내야한다;
					String inviteUser = token[3];// 대기실인지 확인 -> 0으로 보내야한다;
					chatServer.sendInviteRequest(masterId, inviteRoomNum,
							roomManager.getRoomList().get(0).getClients().get(inviteUser));
					break;

				case MessageType.S_INVITE_RESULT: // [304]초대에 대한 응답
					inviteUser = token[1]; // 초대에 대한 응답한 유저 ID
					masterId = token[2]; // 이 초대를 보낸 유저 ID
					String isInvite = token[3]; // 초대에 대한 승락 or 거절
					if (isInvite.equals("true")) {
						// 유저 수락
						isInvite = "true";
						chatServer.isSendInviteRequest(inviteUser, masterId, isInvite);
					} else {
						isInvite = "false";
						chatServer.isSendInviteRequest(inviteUser, masterId, isInvite);
					}
					break;
				case MessageType.C_KICK: // [307] 방장이 해당유저를 강퇴시킴
					int kickRoomNum = Integer.parseInt(token[2]);
					String kickId = token[3];
					ChatService client = roomManager.getRoomList().get(kickRoomNum).getClients().get(kickId); // 강퇴당할
																												// 아이의
																												// chatService
					protocolMethod.exitRoom(kickId, kickRoomNum, client);
					break;
				case MessageType.SC_EXIT: // [308] 해당방에서 나감
					int exitRoomNum = Integer.parseInt(token[2]);
					protocolMethod.exitRoom(requestUserId, exitRoomNum, this);
					break;
				case MessageType.SC_REQUEST_WAITING_LIST: // [309] 대기실 유저 리스트 요청
					String waitUserList = protocolMethod.waitUserList();
					sendMessage(MessageType.SC_REQUEST_WAITING_LIST + MessageType.DELIMETER + requestUserId
							+ MessageType.DELIMETER + waitUserList);
					break;
				case MessageType.SC_REQUEST_WAITING_ROOMLIST: // [310] 대기실 요청
					String roomList = protocolMethod.sendRoomList();
					sendMessage(MessageType.SC_REQUEST_WAITING_ROOMLIST + MessageType.DELIMETER + requestUserId
							+ MessageType.DELIMETER + roomList);
					break;
				// case MessageType.C_UPLOAD:
				// System.out.println(requestMessage);
				// String roomNumber = token[2];
				// String fileName = token[3];
				// long fileSize = Long.parseLong(token[4]);
				// FileShareRoom shareRoom = new FileShareRoom();
				// int port = shareRoom.getPort();
				// String ip = shareRoom.getServerIp();
				// chatServer.sendAllMessage(MessageType.S_UPLOAD_RESULT+MessageType.DELIMETER+
				// userId+MessageType.DELIMETER+roomNumber+MessageType.DELIMETER+
				// "true"+MessageType.DELIMETER+port+MessageType.DELIMETER+ip);
				// shareRoom.connectListening();
				// System.out.println("babo");
				// shareRoom.setFileName(fileName);
				// shareRoom.setFileSize(fileSize);
				//
				// shareRoom.start();
				//
				// break;
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
		out.size();
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
		System.out.println("Room Number int fuc : " + roomManager.getRoomList().get(roomNum).getClients());
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
			/** 닉네임 유효성검사 */
		} else if (userDao.overlapNick(nick)) {

			sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + false + MessageType.DELIMETER
					+ MessageType.DUPLICATE_NICK);
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
			/** 비밀번호 유효성검사 */
		} else if (pass.equals("")) {
			sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + false + MessageType.DELIMETER
					+ MessageType.ISNULL_INPUT);
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
			/** Email 유효성검사 */
		} else if (email.equals("")) {
			sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + false + MessageType.DELIMETER
					+ MessageType.ISNULL_INPUT);
			return false;
		} else if (userDao.overlapEmail(email)) {
			sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + false + MessageType.DELIMETER
					+ MessageType.DUPLICATE_EMAIL);
			return false;
			/** 연락처 유효성검사 */
		} else if (phoneNum.equals("")) {
			sendMessage(MessageType.S_JOIN_RESULT + MessageType.DELIMETER + false + MessageType.DELIMETER
					+ MessageType.ISNULL_INPUT);
			return false;
		}
		return true;
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
}