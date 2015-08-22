package kr.or.kosta.koback.server;

import java.io.IOException;

import javax.swing.DefaultComboBoxModel;

import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.model.Room;
import kr.or.kosta.koback.model.RoomManager;
import kr.or.kosta.koback.util.GUIUtil;
import kr.or.kosta.koback.view.UserModel;

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
	
	public void login(String userId, String pass, ChatService client){
		chatServer.getManager().addClient(userId, client);
		String idList = chatServer.getManager().getAllId();
		String [] allUser = idList.split(",");
		
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
		StringBuffer roomInfo = new StringBuffer("대기실,0,0,0,0,");
		for(int i = 1; i < roomManager.getRoomList().size(); i++){
			roomInfo.append(roomManager.getRoomList().get(i).toString()+",");
		}
		chatServer.sendAllMessage(MessageType.S_LOGIN_RESULT+MessageType.DELIMETER+true+MessageType.DELIMETER+userId+MessageType.DELIMETER+idList+MessageType.DELIMETER+roomInfo.toString());
	}
	
	public void commonOpen(String masterId,String roomName, int maxUserNum, ChatService client){
		Room room = new Room(roomName, client, maxUserNum);
		roomManager.addRoom(room);
		roomManager.getRoomList().get(roomManager.getRoom().getRoomNum()).getClients().put(masterId, client);
		try {
			client.sendMessage(MessageType.S_OPEN_RESULT+MessageType.DELIMETER+masterId+MessageType.DELIMETER+
						room.getRoomNum()+MessageType.DELIMETER+roomName+
					    MessageType.DELIMETER+maxUserNum+MessageType.DELIMETER+true);
		} catch (IOException e) {
			GUIUtil.showErrorMessage("방을 만들 수 없습니다.");
		}
		roomManager.getRoomList().get(0).getClients().remove(masterId);
	}
	
	public void secretOpen(String masterId,String roomName, int maxUserNum, String pass, ChatService client){
		Room sRoom = new Room(masterId, client, pass, maxUserNum);
		roomManager.addSecretRoom(sRoom);
		roomManager.getRoomList().get(roomManager.getRoomNumber()).getClients().put(masterId, client);
		try {
			client.sendMessage(MessageType.S_SECRET_RESULT+MessageType.DELIMETER+masterId+MessageType.DELIMETER+
					    sRoom.getRoomNum()+MessageType.DELIMETER+masterId+
					    MessageType.DELIMETER+maxUserNum+MessageType.DELIMETER+pass+MessageType.DELIMETER+true);
		} catch (IOException e) {
			GUIUtil.showErrorMessage("비밀방을 만들 수 없습니다.");
		}
		roomManager.getRoomList().get(0).getClients().remove(masterId);
	}
	
	
	
	
	
	
	
	
	
	
}
