package kr.or.kosta.koback.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;

import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.model.ClientsManager;
import kr.or.kosta.koback.model.RoomManager;
import kr.or.kosta.koback.server.ChatService;
import kr.or.kosta.koback.view.ServerFrame;

/**
 * 채팅 관련 서버 제공
 * @author 유안상
 */
public class ChatServer {
	private ServerSocket serverSocket;
	private int port;
	private boolean stop;
	private ServerFrame serverFrame;
	private ClientsManager clientManager;
	private RoomManager roomManager;
	private InetAddress ia;
	
	public ChatServer(int port, ServerFrame serverFrame) {
		this.port = port;
		clientManager = new ClientsManager();
		this.serverFrame = serverFrame;
		roomManager = new RoomManager();
	}

	public void startUp() throws IOException {
		serverSocket = new ServerSocket(port);
		roomManager.addWatingRoom();
	}

	public void shutDown() throws IOException {
		if (serverSocket != null)
			serverSocket.close();
	}

	/**
	 * 클라이언트와 연결 기능
	 * @throws IOException
	 */
	public void connectListening() throws IOException {
		while (!stop) {
			Socket socket = serverSocket.accept(); // 블락메소드, 클라이언트에서의 수신 대기
			ia = socket.getInetAddress(); // socket(클라이언트)의 정보(port,ip 등)을 알아내기 위함
			serverFrame.getCenterPanel().getLogTA().append(String.format("%1$tF %1$tT", Calendar.getInstance()) + " : [" + ia.getHostAddress() + "] 채팅 클라이언트가 접속\r\n");
			ChatService chatService = new ChatService(socket, this, serverFrame.getCenterPanel().getUserModel(),
					serverFrame.getSouthPanel().getModel(), roomManager);
			chatService.start();
		}
	}
	
	/** 모든 유저들에게 보내는 메세지*/
	public void sendAllMessage(String message) {
		Enumeration<ChatService> e = clientManager.getClients().elements();
		while (e.hasMoreElements()) {
			ChatService client = e.nextElement();
			try {
				client.sendMessage(message);
			} catch (IOException e1) {
				
			}
		}
	}
	
	/** 대기실에 있는 아이들에게만 보내는 메세지*/
	public void sendWaitMessage(String message){
		Enumeration<ChatService> e = clientManager.getClients().elements();
		while (e.hasMoreElements()) {
			ChatService waitClient = e.nextElement();
			int i = 0;
			if(waitClient.getRoomManager().getRoomList().get(i).getRoomNum() == 0){
				try {
					waitClient.sendMessage(message);
				} catch (IOException e1) {
//					e1.printStackTrace();
				}
			}
			i++;
		}
	}
	
	/** 운영자가 해당 유저에게 보내는 메소드 */
	public void sendWhisperMessage(String message, String[] allUser, String userId) {
		try {
			for (int i = 0; i < allUser.length; i++) {
				if(allUser[i].equals(userId)) clientManager.getClients().get(userId).sendMessage(message);
			}
		} catch (IOException e) {
//			e.printStackTrace();
		};
	}
	
	/** 일반유저끼리의 귓속말*/
	public void commonWhisperMessage(String message, String whisperId, int roomNum, String userId){
		Enumeration<String> e = roomManager.getRoomList().get(roomNum).getClients().keys();
		while (e.hasMoreElements()) {
			try {
				String name = e.nextElement();
				if(name.equals(whisperId)){
					roomManager.getRoomList().get(roomNum).getClients().get(name).sendMessage(message);
					roomManager.getRoomList().get(roomNum).getClients().get(userId).sendMessage(message);
				}
			} catch (IOException e1) {
//				e1.printStackTrace();
			}
		}
	}
	// 해당 채팅방에 메시지 보내기
	public void sendChatRoomMessage(String message, int roomNum){
		if(null !=  roomManager.getRoomList().get(roomNum)){
			Enumeration<String> e = roomManager.getRoomList().get(roomNum).getClients().keys();
			while (e.hasMoreElements()) {
				try {
					String name = e.nextElement();
					roomManager.getRoomList().get(roomNum).getClients().get(name).sendMessage(message);
				} catch (IOException e1) {
//					e1.printStackTrace();
				}
			}
		}
	}
	
	//초대 요청
	public void sendInviteRequest(String masterId, String inviteRoomNum, ChatService inviteUser){
		try {
			inviteUser.sendMessage(MessageType.S_INVITE_RESULT + MessageType.DELIMETER + masterId
					+ MessageType.DELIMETER + inviteRoomNum + MessageType.DELIMETER);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// 초대 요청받은사람의 응답
	public void isSendInviteRequest(String inviteUser, String masterId, boolean isInvite) {
		String message = MessageType.C_INVITE_CONFIRM + MessageType.DELIMETER + inviteUser + MessageType.DELIMETER
				+ masterId + MessageType.DELIMETER + isInvite;
		ChatService client = clientManager.getClients().get(masterId);
		try {
			client.sendMessage(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ServerSocket getServerSocket() {
		return serverSocket; 
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public ClientsManager getManager() {
		return clientManager;
	}

	public void setManager(ClientsManager manager) {
		this.clientManager = manager;
	}

	public ClientsManager getClientManager() {
		return clientManager;
	}

	public void setClientManager(ClientsManager clientManager) {
		this.clientManager = clientManager;
	}

	public RoomManager getRoomManager() {
		return roomManager;
	}

	public void setRoomManager(RoomManager roomManager) {
		this.roomManager = roomManager;
	}
	
	public ServerFrame getServerFrame() {
		return serverFrame;
	}

	public void setServerFrame(ServerFrame serverFrame) {
		this.serverFrame = serverFrame;
	}
	
	
	public InetAddress getIa() {
		return ia;
	}

	public void setIa(InetAddress ia) {
		this.ia = ia;
	}

	@Override
	public String toString() {
		return "ChatServer [serverSocket=" + serverSocket + ", port=" + port + ", stop=" + stop + "]";
	}
}