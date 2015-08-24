package kr.or.kosta.koback.server;

import java.io.IOException;
import java.util.Enumeration;

import javax.swing.DefaultComboBoxModel;

import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.model.Room;
import kr.or.kosta.koback.model.RoomManager;
import kr.or.kosta.koback.util.GUIUtil;
import kr.or.kosta.koback.view.UserModel;
/**
 * ChatService클래스의 프로토콜관련 기능을 한 곳에 모아둔 클래스
 * @author 유안상
 */
public class ProtocolMethod {
	UserModel userModel;
	DefaultComboBoxModel<String> cbModel;
	RoomManager roomManager;
	ChatServer chatServer;
	
	public ProtocolMethod(UserModel userModel,DefaultComboBoxModel<String> cbModel, RoomManager roomManager, ChatServer chatServer){
		this.userModel = userModel;
		this.cbModel = cbModel;
		this.roomManager = roomManager;
		this.chatServer = chatServer;
	}
	
	// 로그인 요청 처리 100-101번
	public void login(String userId, String pass, ChatService client){
		chatServer.getManager().addClient(userId, client);
		String idList = chatServer.getManager().getAllId();
		String [] allUser = idList.split(",");
		
		roomManager.getRoomList().get(0).getClients().put(userId, client);
		StringBuffer waitUser = new StringBuffer();
		Enumeration<String> e =roomManager.getRoomList().get(0).getClients().keys();
		while (e.hasMoreElements()) {
			waitUser.append(e.nextElement()+",");
		}
		
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
		roomManager.getRoomList().get(0).getClients().put(userId, client);
		
		StringBuffer roomInfo = new StringBuffer();
		for(int i = 0; i < roomManager.getRoomList().size(); i++){
			roomInfo.append(roomManager.getRoomList().get(i).toString()+",");
		}
		
		chatServer.sendAllMessage(MessageType.S_LOGIN_RESULT+MessageType.DELIMETER+userId+MessageType.DELIMETER+true+MessageType.DELIMETER+waitUser+MessageType.DELIMETER+roomInfo.toString());
	}
	
	// 일반방 개설 요청(200-201번)
	public void commonOpen(String masterId,String roomName, int maxUserNum, ChatService client){
		Room room = new Room(roomName, client, maxUserNum);
		roomManager.addRoom(room);
		roomManager.getRoomList().get(roomManager.getRoom().getRoomNum()).getClients().put(masterId, client);
		roomManager.getRoomList().get(0).getClients().remove(masterId);
		try {
			client.sendMessage(MessageType.S_OPEN_RESULT+MessageType.DELIMETER+masterId+MessageType.DELIMETER+
								room.getRoomNum()+MessageType.DELIMETER+roomName+
								MessageType.DELIMETER+maxUserNum+MessageType.DELIMETER+true);
			/** 대기실 인원에게 모든 방 정보+ 대기실유저리스트*/
			String waitUserList = waitUserList(); 
			String roomList = sendRoomList();
			chatServer.sendWaitMessage(MessageType.S_REQUEST_WAITING_LIST+MessageType.DELIMETER+masterId+MessageType.DELIMETER+" "+waitUserList);
			chatServer.sendWaitMessage(MessageType.S_REQUEST_WAITING_ROOMLIST+MessageType.DELIMETER+masterId+MessageType.DELIMETER+" "+roomList);
		} catch (IOException e) {
			GUIUtil.showErrorMessage("방을 만들 수 없습니다.");
		}
        chatServer.getServerFrame().getCenterPanel().getRoomModel().getRoomList().addElement(room);
		chatServer.getServerFrame().getCenterPanel().getRoomModel().fireTableDataChanged();
	}
	
	// 비밀방 개설 요청
	public void secretOpen(String masterId,String roomName, int maxUserNum, String pass, ChatService client){
		Room room = new Room(roomName, client, pass, maxUserNum);
		roomManager.addSecretRoom(room);
		roomManager.getRoomList().get(roomManager.getRoomNumber()).getClients().put(masterId, client);
		roomManager.getRoomList().get(0).getClients().remove(masterId);
		try {
			client.sendMessage(MessageType.S_SECRET_RESULT+MessageType.DELIMETER+masterId+MessageType.DELIMETER+
								room.getRoomNum()+MessageType.DELIMETER+roomName+MessageType.DELIMETER+maxUserNum+MessageType.DELIMETER+pass+MessageType.DELIMETER+true);
			/** 대기실 인원에게 모든 방 정보+ 대기실유저리스트*/
			String waitUserList = waitUserList(); 
			String roomList = sendRoomList();
			chatServer.sendWaitMessage(MessageType.S_REQUEST_WAITING_LIST+MessageType.DELIMETER+masterId+MessageType.DELIMETER+" "+waitUserList);
			chatServer.sendWaitMessage(MessageType.S_REQUEST_WAITING_ROOMLIST+MessageType.DELIMETER+masterId+MessageType.DELIMETER+" "+roomList);
		} catch (IOException e) {
			GUIUtil.showErrorMessage("비밀방을 만들 수 없습니다.");
		}
		chatServer.getServerFrame().getCenterPanel().getRoomModel().getRoomList().addElement(room);
		chatServer.getServerFrame().getCenterPanel().getRoomModel().fireTableDataChanged();
	}
	
	// 방 입장 요청시 처리하는 메소드(210번)
	public void entryRoom(String id, int roomNum, String roomName, ChatService client){
		roomManager.getRoomList().get(0).getClients().remove(id);
		roomManager.getRoomList().get(roomNum).getClients().put(id, client);
		
		String userList = waitUserList();
		
		StringBuffer sendUser = new StringBuffer(); // 방에 있는 유저 목록
		Enumeration<String> e = roomManager.getRoomList().get(roomNum).getClients().keys();
		while (e.hasMoreElements()) {
			sendUser.append((String) e.nextElement()+",");
		}
		
		try {
			if(roomManager.getRoomList().get(roomNum).enterRoom()){
				client.sendMessage(MessageType.S_ENTRY_RESULT+MessageType.DELIMETER+id+MessageType.DELIMETER+true+MessageType.DELIMETER+roomName+MessageType.DELIMETER+roomNum+MessageType.DELIMETER+sendUser.toString());
				chatServer.sendWaitMessage(MessageType.S_REQUEST_WAITING_LIST+MessageType.DELIMETER+userList); // 대기실에 있는 아이들에게 들어온 아이 알려줌
				chatServer.sendChatRoomMessage(MessageType.S_SELECTED_ROOM_USERLIST+MessageType.DELIMETER+id+MessageType.DELIMETER+sendUser, roomNum); // 313번 - 채팅방에 있는 아이들에게 나간 아이를 알려줌
			}else{
				client.sendMessage(MessageType.S_ENTRY_RESULT+MessageType.DELIMETER+id+MessageType.DELIMETER+false+MessageType.DELIMETER+MessageType.OVER_MAXUSER);
			}
		} catch (IOException e1) {
			GUIUtil.showErrorMessage("방에 입장 할 수 없습니다.");
		}
	}
	
	// 비밀방 입장 요청시 처리하는 메소드(212번)
	public void entrySecretRoom(String id, int roomNum, String roomName, String pass, ChatService client){
		roomManager.getRoomList().get(0).getClients().remove(id);
		roomManager.getRoomList().get(roomNum).getClients().put(id, client);
		String userList = waitUserList();
		StringBuffer sendUser = new StringBuffer();
		Enumeration<String> e = roomManager.getRoomList().get(roomNum).getClients().keys();
		while (e.hasMoreElements()) {
			sendUser.append((String) e.nextElement()+",");
		}
		try {
			if(roomManager.getRoomList().get(roomNum).getPasswd().equals(pass) && roomManager.getRoomList().get(roomNum).enterRoom()){
				client.sendMessage(MessageType.S_SECRET_ENTRY_RESULT+MessageType.DELIMETER+id+MessageType.DELIMETER+true+MessageType.DELIMETER+roomName+MessageType.DELIMETER+roomNum+MessageType.DELIMETER+sendUser);
				chatServer.sendWaitMessage(MessageType.S_REQUEST_WAITING_LIST+MessageType.DELIMETER+id+MessageType.DELIMETER+userList); // 대기실에 있는 아이들에게 들어온 아이 알려줌
				chatServer.sendChatRoomMessage(MessageType.S_SELECTED_ROOM_USERLIST+MessageType.DELIMETER+id+MessageType.DELIMETER+sendUser, roomNum); // 채팅방에 있는 아이들에게 나간 아이를 알려줌
			}else if(!(roomManager.getRoomList().get(roomNum).getPasswd().equals(pass))){
				client.sendMessage(MessageType.S_SECRET_ENTRY_RESULT+MessageType.DELIMETER+id+MessageType.DELIMETER+false+MessageType.DELIMETER+MessageType.WRONG_ROOM_PASS);
			}else{
				client.sendMessage(MessageType.S_SECRET_ENTRY_RESULT+MessageType.DELIMETER+id+MessageType.DELIMETER+false+MessageType.DELIMETER+MessageType.OVER_MAXUSER);
			}
		} catch (IOException e1) {
			GUIUtil.showErrorMessage("방에 입장 할 수 없습니다.");
		}
	}
	
	// 초대시 방입장(비밀방, 일반방 구분없이 입장)
	public void entryInviteRoom(String id, int roomNum, String roomName, ChatService client){
		roomManager.getRoomList().get(0).getClients().remove(id);
		roomManager.getRoomList().get(roomNum).getClients().put(id, client);
		
		String userList = waitUserList();
		
		StringBuffer sendUser = new StringBuffer(); // 방에 있는 유저 목록
		Enumeration<String> e = roomManager.getRoomList().get(roomNum).getClients().keys();
		while (e.hasMoreElements()) {
			sendUser.append((String) e.nextElement()+",");
		}
		
		try {
			if(roomManager.getRoomList().get(roomNum).enterRoom()){
				client.sendMessage(MessageType.S_ENTRY_RESULT+MessageType.DELIMETER+id+MessageType.DELIMETER+true+MessageType.DELIMETER+roomName+MessageType.DELIMETER+roomNum+MessageType.DELIMETER+sendUser.toString());
				chatServer.sendWaitMessage(MessageType.S_REQUEST_WAITING_LIST+MessageType.DELIMETER+userList); // 대기실에 있는 아이들에게 들어온 아이 알려줌
				chatServer.sendChatRoomMessage(MessageType.S_SELECTED_ROOM_USERLIST+MessageType.DELIMETER+id+MessageType.DELIMETER+sendUser, roomNum); // 313번 - 채팅방에 있는 아이들에게 나간 아이를 알려줌
			}else{
				client.sendMessage(MessageType.S_ENTRY_RESULT+MessageType.DELIMETER+id+MessageType.DELIMETER+false+MessageType.DELIMETER+MessageType.OVER_MAXUSER);
			}
		} catch (IOException e1) {
			GUIUtil.showErrorMessage("방에 입장 할 수 없습니다.");
		}
		System.out.println("초대를 수락한 유저가 들어갔는지 확인 : " + roomManager.getRoomList().get(roomNum).getClients());
		
	}
	
	// 308번 방 나가기 요청 메소드
	public void exitRoom(String id, int roomNum, ChatService client){
		int count = roomManager.getRoomList().get(roomNum).leaveRoom();
		roomManager.getRoomList().get(roomNum).getClients().remove(id);
		roomManager.getRoomList().get(0).getClients().put(id, client);
		if(count == 0){
			Room room = roomManager.getRoomList().get(roomNum);
			roomManager.getRoomList().remove(roomNum);
			chatServer.getServerFrame().getCenterPanel().getRoomModel().getRoomList().remove(room);
			chatServer.getServerFrame().getCenterPanel().getRoomModel().fireTableDataChanged();
		}
		
		String userList = waitUserList();
		String roomList = sendRoomList();
		
		StringBuffer chatUserList = new StringBuffer();
		if(null != roomManager.getRoomList().get(roomNum)){
			Enumeration<String> chatUser = roomManager.getRoomList().get(roomNum).getClients().keys();
			while (chatUser.hasMoreElements()) {
				chatUserList.append(chatUser.nextElement()+",");
			}
		}
		try {
			client.sendMessage(MessageType.SC_EXIT+MessageType.DELIMETER+id+MessageType.DELIMETER+roomNum+MessageType.DELIMETER+userList+MessageType.DELIMETER+roomList);
			client.sendMessage(MessageType.S_REQUEST_WAITING_LIST+MessageType.DELIMETER+id+MessageType.DELIMETER+userList); // 방을 나가는 아이에게 현재방 정보를 알려줌
			client.sendMessage(MessageType.S_REQUEST_WAITING_ROOMLIST+MessageType.DELIMETER+id+MessageType.DELIMETER+roomList); // 방을 나가는 아이에게 대기실에 있는 유저리스트를 알려줌
			chatServer.sendChatRoomMessage(MessageType.S_SELECTED_ROOM_USERLIST+MessageType.DELIMETER+id+MessageType.DELIMETER+chatUserList.toString(), roomNum);
		} catch (IOException e) {
//			e.printStackTrace();
		}
	}
	
	
	// 모든방의 정보를 보내준다
	// 에러가 있을지도 모르는 코드
	public String sendRoomList(){
		StringBuffer roomInfo = new StringBuffer();
		for(int i = 0; i < roomManager.getRoomList().size()-1; i++){
			roomInfo.append(roomManager.getRoomList().get(i).toString()+",");
		}
		return roomInfo.toString();
	}
	
	// 대기실에 접속한 모든유저를 준다.
	public String waitUserList(){
		StringBuffer roomUser = new StringBuffer();
		Enumeration<String> e3 = roomManager.getRoomList().get(0).getClients().keys();
		while (e3.hasMoreElements()) {
			roomUser.append(e3.nextElement() + ",");
		}
		if(roomUser.length() == 0)
			return "";
		return roomUser.toString();
	}
}
