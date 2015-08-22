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

import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.model.Room;
import kr.or.kosta.koback.model.RoomManager;
import kr.or.kosta.koback.model.User;
import kr.or.kosta.koback.model.UserDao;
import kr.or.kosta.koback.util.GUIUtil;
import kr.or.kosta.koback.view.UserModel;

/**
 * 채팅 클라이언트의 요청메시지를 수신하여 서비스 제공 핵심 기능 담당(모든 프로토콜 메세지를 송수신)
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
	
	private String idList;
	private String[] allUser;
	
	protected UserModel userModel;
	protected DefaultComboBoxModel<String> cbModel;
	
	private ProtocolMethod protocolMethod;
	
	public ChatService(){}
	public ChatService(Socket socket, ChatServer chatServer, UserModel userModel,DefaultComboBoxModel<String> model, RoomManager roomManager) throws IOException {
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
				
				switch (requestCode) {
				case MessageType.C_LOGIN:
					String passwd = token[2];
					protocolMethod.login(requestUserId, passwd, this);
					break;
				case MessageType.C_JOIN:
					String name = token[2];
					String nick = token[3];
					String pass = token[4];
					String ssn = token[5];
					String email = token[6];
					String phoneNum = token[7];
					userDao.addUser(new User(requestUserId, name, nick, pass, ssn, email, phoneNum));
					sendMessage(MessageType.S_JOIN_RESULT+MessageType.DELIMETER+requestUserId+MessageType.DELIMETER+true);
					break;
				case MessageType.C_OPEN:
					String roomName = token[2];
					int maxUserNum = Integer.parseInt(token[3]);
					protocolMethod.commonOpen(requestMessage, roomName, maxUserNum, this);
					break;
				case MessageType.C_SECRET_OPEN:
					String sRoomName = token[2];
					int sMaxUserNum = Integer.parseInt(token[3]);
					String sPass = token[4];
					protocolMethod.secretOpen(requestUserId, sRoomName, sMaxUserNum, sPass, this);
					break;
				case MessageType.C_ENTRY:
					String roomNum = token[2];
					roomManager.getRoomList().get(0).getClients().remove(requestUserId);
					roomManager.getRoomList().get(Integer.parseInt(roomNum)).getClients().put(requestUserId, this);
					StringBuffer sendUser = new StringBuffer();
					Enumeration<String> e = roomManager.getRoomList().get(Integer.parseInt(roomNum)).getClients().keys();
					while (e.hasMoreElements()) {
						sendUser.append((String) e.nextElement()+",");
					}
					
					sendMessage(MessageType.S_ENTRY_RESULT+MessageType.DELIMETER+requestUserId+MessageType.DELIMETER+roomNum+MessageType.DELIMETER+sendUser.toString());
					break;
				case MessageType.C_SECRET_ENTRY:
					requestUserId = token[1];
					String sRoomNum = token[2];
					StringBuffer sendUser2 = new StringBuffer();
					Enumeration<String> e2 = roomManager.getRoomList().get(Integer.parseInt(sRoomNum)).getClients().keys();
					while (e2.hasMoreElements()) {
						sendUser2.append((String) e2.nextElement()+",");
					}
					roomManager.getRoomList().get(0).getClients().remove(requestUserId);
					roomManager.getRoomList().get(Integer.parseInt(sRoomNum)).getClients().put(requestUserId, this);
					sendMessage(MessageType.S_SECRET_ENTRY_RESULT+MessageType.DELIMETER+requestUserId+MessageType.DELIMETER+sRoomNum+MessageType.DELIMETER+sendUser2);
					break;
				case MessageType.C_LOGOUT:
					removeClient(this);
					userModel.logoutUser(requestUserId);
					cbModel.removeElement(requestUserId);
					// 다른 아이들에게 뿌려줘야함
					break;
					//--------------------- 300번대(채팅 방 메시지관련) -------------------------------
				case MessageType.SC_CHAT_MESSAGE:		// 일반메시지 보내기
					int roomNumber = Integer.parseInt(token[2]);
					chatServer.sendChatRoomMessage(requestMessage, userByRoom(roomNumber));
					break;
				case MessageType.SC_CHAT_EMOTICON:		// 이모티콘 보내기
					roomNumber = Integer.parseInt(token[2]);
					chatServer.sendChatRoomMessage(requestMessage, userByRoom(roomNumber));
					break;
				case MessageType.SC_WHISPER:		// 귓속말 보내기, 302
					String whisperId = token[1];
					String whisperRoomNum = token[2];
					String whisperOpponent = token[3];
					String message = token[4];
					chatServer.sendWhisperMessage(message, allUser, whisperId);
					break;
				case MessageType.C_INVITE_USER:		// 대기실 인원 초대, 303
					String masterId = token[1];		// 초대하고자하는사람
					String invateRoomNum = token[2];// 대기실인지 확인 -> 0으로 보내야한다;
					break;
				case MessageType.C_KICK:
					// EXIT 먼저하고 그 메소드 이용해서 마무리 해야함
					break;
				case MessageType.SC_EXIT:
					requestUserId = token[1];
					String exitRoomNum = token[2];
					// 위에 대기방 인원 목록 추가하고 여기 마무리해야함
					break;
					
				case MessageType.S_ADMIN_WHISPER:
					// 콤보박스에 접속 인원 추가해야함, 나갈때는 제거해야함
					// 거기에 맞는 귓속말 메소드 사용해서 보내줘야함(받는 쪽에서 컬러메세지)
					break;
//				case MessageType.C_UPLOAD:
//					System.out.println(requestMessage);
//					String roomNumber = token[2];
//					String fileName = token[3];
//					long fileSize = Long.parseLong(token[4]);
//					FileShareRoom shareRoom = new FileShareRoom();
//					int port = shareRoom.getPort();
//					String ip = shareRoom.getServerIp();
//					chatServer.sendAllMessage(MessageType.S_UPLOAD_RESULT+MessageType.DELIMETER+
//							                  userId+MessageType.DELIMETER+roomNumber+MessageType.DELIMETER+
//							                  "true"+MessageType.DELIMETER+port+MessageType.DELIMETER+ip);
//					shareRoom.connectListening();
//					System.out.println("babo");
//					shareRoom.setFileName(fileName);
//					shareRoom.setFileSize(fileSize);
//					
//					shareRoom.start();
//					
//					break;
				}
			}
		} finally {
			if (in != null)	in.close();
			if (out != null) out.close();
			if (socket != null)	socket.close();
		}
	}
	
	public void setEvent(){
		chatServer.getServerFrame().getSouthPanel().getAdminTF().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String noticeMessage = chatServer.getServerFrame().getSouthPanel().getAdminTF().getText().trim();
				String userId = (String)(chatServer.getServerFrame().getSouthPanel().getAllUserCb().getSelectedItem());
				if(noticeMessage.length() != 0) {
					if(chatServer.getServerFrame().getSouthPanel().getAllUserCb().getSelectedItem().equals("전체"))chatServer.sendAllMessage(MessageType.S_NOTICE+MessageType.DELIMETER+"공지사항"+MessageType.DELIMETER+noticeMessage);
					else
						try {
							chatServer.getClientManager().getClients().get(userId).sendMessage(MessageType.S_ADMIN_WHISPER+MessageType.DELIMETER+ "Admin"+MessageType.DELIMETER+noticeMessage);
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
	public void removeClient(ChatService client){
		chatServer.getManager().removeClient(client);
	}
	
	/**
	 * 메시지를 보낸 클라이언트가 접속한 채팅방의 접속자 리스트
	 * @param client
	 */
	public Hashtable<String, ChatService> userByRoom(int roomNum){
		// 현재 메시지를 보낸 방번호의 접속자들
		System.out.println("Room Number int fuc : " + roomManager.getRoomList().get(roomNum).getClients());
		return roomManager.getRoomList().get(roomNum).getClients();
	}
	
	@Override
	public void run() {
		try {
			handleRequest();
		} catch (IOException e) {
//			GUIUtil.showErrorMessage("상대방이 접속 종료.\n");
		}
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
}