package kr.or.kosta.koback.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

import kr.or.kosta.koback.model.ClientsManager;
import kr.or.kosta.koback.model.RoomManager;
import kr.or.kosta.koback.server.ChatService;
import kr.or.kosta.koback.view.CenterPanel;
import kr.or.kosta.koback.view.ServerFrame;

/**
 * 채팅 관련 서버 제공
 * 
 * @author AS
 */
public class ChatServer {
	private ServerSocket serverSocket;
	private int port;
	private boolean stop;
	private ServerFrame serverFrame;
	private ClientsManager clientManager;
	private RoomManager roomManager;

	// public ChatServer(){
	// this(5500);
	// }
	public ChatServer(int port, ServerFrame serverFrame) {
		this.port = port;
		clientManager = new ClientsManager();
		roomManager = new RoomManager();
		this.serverFrame = serverFrame;
		
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
	 * 
	 * @throws IOException
	 */
	public void connectListening() throws IOException {
		while (!stop) {
			Socket socket = serverSocket.accept(); // 블락메소드, 클라이언트에서의 수신 대기
			InetAddress ia = socket.getInetAddress(); // socket(클라이언트)의
														// 정보(port,ip 등)을 알아내기
														// 위함
		
			serverFrame.getCenterPanel().getLogTA().append("[" + ia.getHostAddress() + "] 채팅 클라이언트가 접속\r\n");
			roomManager.addWatingRoom();
			ChatService chatService = new ChatService(socket, this, serverFrame.getCenterPanel().getUserModel(),
					serverFrame.getSouthPanel().getModel(), roomManager);
			chatService.start();
		}
	}

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

	public void sendWhisperMessage(String message, String[] allUser, String userId) {
//		Enumeration<ChatService> e = clientManager.getClients().elements();
		try {
			for (int i = 0; i < allUser.length; i++) {
				if(allUser[i].equals(userId)) clientManager.getClients().get(userId).sendMessage(message);
			}
		} catch (IOException e) {
			e.printStackTrace();
		};
	}
	
	// 해당 채팅방에 메시지 보내기
	public void sendChatRoomMessage(String message, Hashtable<String, ChatService> clients){
		Enumeration<ChatService> e = clientManager.getClients().elements();
		while (e.hasMoreElements()) {
			ChatService client = e.nextElement();
			try {
				client.sendMessage(message);
			} catch (IOException e1) {
				System.out.println("진짜 대단하고 엄청난 예외가 발생해쪄요 뿌우 : " + e1.getStackTrace());
			}
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
	@Override
	public String toString() {
		return "ChatServer [serverSocket=" + serverSocket + ", port=" + port + ", stop=" + stop + "]";
	}
}