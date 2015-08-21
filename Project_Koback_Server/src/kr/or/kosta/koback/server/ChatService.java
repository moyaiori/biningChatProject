package kr.or.kosta.koback.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.model.Room;
import kr.or.kosta.koback.model.RoomManager;
import kr.or.kosta.koback.model.User;
import kr.or.kosta.koback.model.UserDao;
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
	private String userId;
	private String nickName;
	private boolean stop;
	private UserDao userDao;
	private RoomManager roomManager;
	
	private String idList;
	private String[] allUser;
	
	private UserModel userModel;
	private DefaultComboBoxModel<String> cbModel;
	
	public ChatService(){}
	public ChatService(Socket socket, ChatServer chatServer, UserModel userModel,DefaultComboBoxModel<String> model, RoomManager roomManager) throws IOException {
		this.socket = socket;
		this.chatServer = chatServer;
		this.userModel = userModel;
		this.cbModel = model;
		userDao = new UserDao();
		this.roomManager = roomManager;
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
	}

	/**
	 * 일괄로 채팅클라이언트의 요청메세지 파싱 및 서비스
	 * @throws Exception 
	 */
	public void handleRequest() throws Exception {
		setEvent();
		try {
			while (!stop) {
				String requestMessage = in.readUTF();
				String[] token = requestMessage.split(MessageType.DELIMETER);
				String requestCode = token[0];
				
				switch (requestCode) {
				case MessageType.C_LOGIN:
					userId = token[1];
					String passwd = token[2];
					chatServer.getManager().addClient(userId, this);
					idList = chatServer.getManager().getAllId();
					allUser = idList.split(",");
					
					for (int i = 0; i < allUser.length; i++) {
						for (int j = 0; j < userModel.list.size(); j++) {
							if (userModel.list.get(j).equals(allUser[i]))
								userModel.logoutUser(allUser[i]);
						}
						userModel.addUser(allUser[i]);
					}
					for (int i = 0; i < allUser.length; i++) {
						for (int j = 0; j < cbModel.getSize(); j++) {
							if (cbModel.getElementAt(j).equals(allUser[i]))
								cbModel.removeElement(allUser[i]);
						}
						cbModel.addElement(allUser[i]);
					}
					
					roomManager.getRoomList().get(0).getClients().put(userId, this);
					StringBuffer roomInfo = new StringBuffer("대기실,0,0,0,0,");
					for(int i = 1; i < roomManager.getRoomList().size(); i++){
						roomInfo.append(roomManager.getRoomList().get(i).toString()+",");
					}
					
					// 대기방 목록만 따로 추가해야함
					chatServer.sendAllMessage(MessageType.S_LOGIN_RESULT+MessageType.DELIMETER+true+MessageType.DELIMETER+userId+MessageType.DELIMETER+idList+MessageType.DELIMETER+roomInfo.toString());
					break;
				case MessageType.C_JOIN:
					String id = token[1];
					String name = token[2];
					String nick = token[3];
					String pass = token[4];
					String ssn = token[5];
					String email = token[6];
					String phoneNum = token[7];
					
					List<User> gb = userDao.getAllUser();
					for (User user : gb) {
					if(id.equals("")){
						JOptionPane.showMessageDialog(null, "ID를 입력해 주십시오." );
					return;
					}else if(user.getId().equals(id)){
						JOptionPane.showMessageDialog(null, "해당 ID가 이미 중복됩니다.");
						return;								
					}
					
					}
					
					
					
					
					userDao.addUser(new User(id, name, nick, pass, ssn, email, phoneNum));
					
					sendMessage(MessageType.S_JOIN_RESULT+MessageType.DELIMETER+id+MessageType.DELIMETER+true);
					break;
				case MessageType.C_OPEN:
					String openUserId = token[1];
					String roomName = token[2];
					String maxUserNum = token[3];
					Room room = new Room(roomName, this, Integer.parseInt(maxUserNum));
					roomManager.addRoom(room);
					System.out.println("만들 때 방 번호" + room.getRoomNum());
					roomManager.getRoomList().get(roomManager.getRoom().getRoomNum()).getClients().put(openUserId, this);
					sendMessage(MessageType.S_OPEN_RESULT+MessageType.DELIMETER+openUserId+MessageType.DELIMETER+
								room.getRoomNum()+MessageType.DELIMETER+roomName+
							    MessageType.DELIMETER+maxUserNum+MessageType.DELIMETER+true);
					roomManager.getRoomList().get(0).getClients().remove(openUserId);
					break;
				case MessageType.C_SECRET_OPEN:
					String sOpenUserId = token[1];
					String sRoomName = token[2];
					String sMaxUserNum = token[3];
					String sPass = token[4];
					Room sRoom = new Room(sRoomName, this, sPass,Integer.parseInt(sMaxUserNum));
					roomManager.addSecretRoom(sRoom);
					roomManager.getRoomList().get(roomManager.getRoomNumber()).getClients().put(sOpenUserId, this);
					sendMessage(MessageType.S_SECRET_RESULT+MessageType.DELIMETER+sOpenUserId+MessageType.DELIMETER+
							    sRoom.getRoomNum()+MessageType.DELIMETER+sRoomName+
							    MessageType.DELIMETER+sMaxUserNum+MessageType.DELIMETER+sPass+MessageType.DELIMETER+true);
					roomManager.getRoomList().get(0).getClients().remove(sOpenUserId);
					break;
				case MessageType.C_ENTRY:
					userId = token[1];
					String roomNum = token[2];
					roomManager.getRoomList().get(0).getClients().remove(userId);
					roomManager.getRoomList().get(Integer.parseInt(roomNum)).getClients().put(userId, this);
					StringBuffer sendUser = new StringBuffer();
					Enumeration<String> e = roomManager.getRoomList().get(Integer.parseInt(roomNum)).getClients().keys();
					while (e.hasMoreElements()) {
						sendUser.append((String) e.nextElement()+",");
					}
					
					sendMessage(MessageType.S_ENTRY_RESULT+MessageType.DELIMETER+userId+MessageType.DELIMETER+roomNum+MessageType.DELIMETER+sendUser.toString());
					break;
				case MessageType.C_SECRET_ENTRY:
					userId = token[1];
					String sRoomNum = token[2];
					roomManager.getRoomList().get(0).getClients().remove(userId);
					roomManager.getRoomList().get(Integer.parseInt(sRoomNum)).getClients().put(userId, this);
					sendMessage(MessageType.S_SECRET_ENTRY_RESULT+MessageType.DELIMETER+userId+MessageType.DELIMETER+sRoomNum+MessageType.DELIMETER+roomManager.getRoomList().get(Integer.parseInt(sRoomNum)).getClients().keys());
					break;
				case MessageType.C_LOGOUT:
					removeClient(this);
					userModel.logoutUser(userId);
					cbModel.removeElement(userId);
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
					userId = token[1];
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
			} catch (Exception e) {
//				GUIUtil.showErrorMessage("상대방이 접속 종료.\n");
			}
		 
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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