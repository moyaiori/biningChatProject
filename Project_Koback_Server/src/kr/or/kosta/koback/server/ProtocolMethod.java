package kr.or.kosta.koback.server;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.model.Room;
import kr.or.kosta.koback.model.RoomManager;
import kr.or.kosta.koback.model.UserDao;
import kr.or.kosta.koback.util.GUIUtil;
import kr.or.kosta.koback.view.ServerFrame;
import kr.or.kosta.koback.view.UserModel;

/**
 * ChatService클래스의 프로토콜관련 기능을 한 곳에 모아둔 클래스
 * 
 * @author 유안상
 */
public class ProtocolMethod {
	UserModel userModel;
	DefaultComboBoxModel<String> cbModel;
	RoomManager roomManager;
	ChatServer chatServer;
	InetAddress ia;

	public ProtocolMethod(UserModel userModel, DefaultComboBoxModel<String> cbModel, RoomManager roomManager,
			ChatServer chatServer, InetAddress ia) {
		this.userModel = userModel;
		this.cbModel = cbModel;
		this.roomManager = roomManager;
		this.chatServer = chatServer;
		this.ia = ia;
	}

	// 로그인 요청 처리 100-101번
	public void login(String userId, String pass, ChatService client, UserDao userDao) throws IOException {
		/* 로그인 유효성검사 실행 */
		 if(client.loginCheck(userId, pass) == true){
		 userDao.onlyIdSearch(userId);
		 }else if(!(userDao.idSearchPasswd(userId).equals(pass))){
		 return;
		 }

		chatServer.getManager().addClient(userId, client);
		String idList = chatServer.getManager().getAllId();
		String[] allUser = idList.split(",");
		roomManager.getRoomList().get(0).getClients().put(userId, client);
		StringBuffer waitUser = new StringBuffer();
		Enumeration<String> e = roomManager.getRoomList().get(0).getClients().keys();
		while (e.hasMoreElements()) {
			waitUser.append(e.nextElement() + ",");
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
		for (int i = 0; i < roomManager.getRoomList().size(); i++) {
			if (null != roomManager.getRoomList().get(i))
				roomInfo.append(roomManager.getRoomList().get(i).toString() + ",");
		}

		chatServer.sendAllMessage(MessageType.S_LOGIN_RESULT + MessageType.DELIMETER + userId + MessageType.DELIMETER
				+ true + MessageType.DELIMETER + waitUser + MessageType.DELIMETER + roomInfo.toString());
	}

	// 일반방 개설 요청(200-201번)
	public void commonOpen(String masterId, String roomName, int maxUserNum, ChatService client) {
		Room room = new Room(roomName, client, maxUserNum);
		client.setMaster(true);
		// master = true;

		int roomNum = room.getRoomNum();

		roomManager.addRoom(room);
		roomManager.getRoomList().get(roomManager.getRoom().getRoomNum()).getClients().put(masterId, client);
		roomManager.getRoomList().get(0).getClients().remove(masterId);
		

		List<Boolean> master = new ArrayList<Boolean>();
		List<String> user = new ArrayList<String>();
		if (null != roomManager.getRoomList().get(roomNum)) {
			Enumeration<String> e2 = roomManager.getRoomList().get(roomNum).getClients().keys();
			while (e2.hasMoreElements()) {
				master.add(roomManager.getRoomList().get(roomNum).getClients().get(e2.nextElement()).getMaster());
			}

			Enumeration<String> e = roomManager.getRoomList().get(roomNum).getClients().keys();
			while (e.hasMoreElements()) {
				user.add(e.nextElement());
			}
		}
		StringBuffer sendUser = new StringBuffer(); // 방에 있는 유저 목록
		for (int i = 0; i < master.size(); i++) {
			sendUser.append(user.get(i) + "|" + master.get(i) + ",");
		}
		String waitUserList = waitUserList();
		String roomList = sendRoomList();
		
		
		try {
			client.sendMessage(MessageType.S_OPEN_RESULT + MessageType.DELIMETER + masterId + MessageType.DELIMETER
					+ room.getRoomNum() + MessageType.DELIMETER + roomName + MessageType.DELIMETER + maxUserNum
					+ MessageType.DELIMETER + true + MessageType.DELIMETER + sendUser);
			/** 대기실 인원에게 모든 방 정보+ 대기실유저리스트 */
			chatServer.sendAllMessage(MessageType.S_REQUEST_WAITING_LIST + MessageType.DELIMETER + masterId
					+ MessageType.DELIMETER + " " + waitUserList);
			chatServer.sendAllMessage(MessageType.S_REQUEST_WAITING_ROOMLIST + MessageType.DELIMETER + masterId
					+ MessageType.DELIMETER + " " + roomList);
			
		} catch (IOException e1) {
			GUIUtil.showErrorMessage("방을 만들 수 없습니다.");
		}

		chatServer.getServerFrame().getCenterPanel().getLogTA().append(
				String.format("%1$tF %1$tT", Calendar.getInstance()) + " : [" + roomName + "] 채팅방이 개설되었습니다.\r\n");
		chatServer.getServerFrame().getCenterPanel().getRoomModel().getRoomList().addElement(room);
		chatServer.getServerFrame().getCenterPanel().getRoomModel().fireTableDataChanged();
	}

	// 비밀방 개설 요청
	public void secretOpen(String masterId, String roomName, int maxUserNum, String pass, ChatService client) {
		Room room = new Room(roomName, client, pass, maxUserNum);
		client.setMaster(true);
		int roomNum = room.getRoomNum();

		roomManager.addSecretRoom(room);
		roomManager.getRoomList().get(roomManager.getRoomNumber()).getClients().put(masterId, client);
		roomManager.getRoomList().get(0).getClients().remove(masterId);

		List<Boolean> master = new ArrayList<Boolean>();
		List<String> user = new ArrayList<String>();
		if (null != roomManager.getRoomList().get(roomNum)) {
			Enumeration<String> e2 = roomManager.getRoomList().get(roomNum).getClients().keys();
			while (e2.hasMoreElements()) {
				master.add(roomManager.getRoomList().get(roomNum).getClients().get(e2.nextElement()).getMaster());
			}

			Enumeration<String> e = roomManager.getRoomList().get(roomNum).getClients().keys();
			while (e.hasMoreElements()) {
				user.add(e.nextElement());
			}
		}
		StringBuffer sendUser = new StringBuffer(); // 방에 있는 유저 목록
		for (int i = 0; i < master.size(); i++) {
			sendUser.append(user.get(i) + "|" + master.get(i) + ",");
		}
		
		String waitUserList = waitUserList();
		String roomList = sendRoomList();
		System.out.println("디버깅 : " + roomList);
		try {
			client.sendMessage(MessageType.S_SECRET_RESULT + MessageType.DELIMETER + masterId + MessageType.DELIMETER
					+ room.getRoomNum() + MessageType.DELIMETER + roomName + MessageType.DELIMETER + maxUserNum
					+ MessageType.DELIMETER + pass + MessageType.DELIMETER + true + MessageType.DELIMETER + sendUser);
			/** 대기실 인원에게 모든 방 정보+ 대기실유저리스트 */

			chatServer.sendWaitMessage(MessageType.S_REQUEST_WAITING_LIST + MessageType.DELIMETER + masterId
					+ MessageType.DELIMETER + " " + waitUserList);
			chatServer.sendWaitMessage(MessageType.S_REQUEST_WAITING_ROOMLIST + MessageType.DELIMETER + masterId
					+ MessageType.DELIMETER + " " + roomList);
		} catch (IOException e) {
			GUIUtil.showErrorMessage("비밀방을 만들 수 없습니다.");
		}

		chatServer.getServerFrame().getCenterPanel().getLogTA().append(
				String.format("%1$tF %1$tT", Calendar.getInstance()) + " : [" + roomName + "] 비밀방이 개설되었습니다.\r\n");
		chatServer.getServerFrame().getCenterPanel().getRoomModel().getRoomList().addElement(room);
		chatServer.getServerFrame().getCenterPanel().getRoomModel().fireTableDataChanged();
	}

	// 방 입장 요청시 처리하는 메소드(210번)
	public void entryRoom(String id, int roomNum, String roomName, ChatService client) {
		roomManager.getRoomList().get(0).getClients().remove(id);
		roomManager.getRoomList().get(roomNum).getClients().put(id, client);
		String userList = waitUserList();

		// 방에 들어온 순서를 알아냄
		int enterCount = roomManager.getRoomList().get(roomNum).getClients().size();
		roomManager.getRoomList().get(roomNum).getClients().get(id).setEnterCount(enterCount);

		List<Boolean> master = new ArrayList<Boolean>();
		Enumeration<String> e2 = roomManager.getRoomList().get(roomNum).getClients().keys();
		while (e2.hasMoreElements()) {
			master.add(roomManager.getRoomList().get(roomNum).getClients().get(e2.nextElement()).getMaster());
		}

		List<String> user = new ArrayList<String>();
		Enumeration<String> e = roomManager.getRoomList().get(roomNum).getClients().keys();
		while (e.hasMoreElements()) {
			user.add(e.nextElement());
		}

		StringBuffer sendUser = new StringBuffer(); // 방에 있는 유저 목록
		for (int i = 0; i < master.size(); i++) {
			sendUser.append(user.get(i) + "|" + master.get(i) + ",");
		}

		try {
			if (roomManager.getRoomList().get(roomNum).enterRoom()) {
				client.sendMessage(MessageType.S_ENTRY_RESULT + MessageType.DELIMETER + id + MessageType.DELIMETER
						+ true + MessageType.DELIMETER + roomName + MessageType.DELIMETER + roomNum
						+ MessageType.DELIMETER + sendUser.toString());
				chatServer.sendWaitMessage(MessageType.S_REQUEST_WAITING_LIST + MessageType.DELIMETER + userList); // 대기실에
																													// 있는
																													// 아이들에게
																													// 들어온아이
																													// 알려줌
				chatServer.sendChatRoomMessage(MessageType.S_SELECTED_ROOM_USERLIST + MessageType.DELIMETER + id
						+ MessageType.DELIMETER + sendUser, roomNum); // 313번 -
																		// 채팅방에
																		// 있는
																		// 아이들에게
																		// 나간아이를
																		// 알려줌
			} else {
				client.sendMessage(MessageType.S_ENTRY_RESULT + MessageType.DELIMETER + id + MessageType.DELIMETER
						+ false + MessageType.DELIMETER + MessageType.OVER_MAXUSER);
			}
		} catch (IOException e1) {
			GUIUtil.showErrorMessage("방에 입장 할 수 없습니다.");
		}
	}

	// 비밀방 입장 요청시 처리하는 메소드(212번)
	public void entrySecretRoom(String id, int roomNum, String roomName, String pass, ChatService client) {
		roomManager.getRoomList().get(0).getClients().remove(id);
		roomManager.getRoomList().get(roomNum).getClients().put(id, client);

		// 방에 들어오는 순서를 알아냄
		int enterCount = roomManager.getRoomList().get(roomNum).getClients().size();
		roomManager.getRoomList().get(roomNum).getClients().get(id).setEnterCount(enterCount);

		String userList = waitUserList();

		List<Boolean> master = new ArrayList<Boolean>();
		Enumeration<String> e2 = roomManager.getRoomList().get(roomNum).getClients().keys();
		while (e2.hasMoreElements()) {
			master.add(roomManager.getRoomList().get(roomNum).getClients().get(e2.nextElement()).getMaster());
		}

		List<String> user = new ArrayList<String>();
		Enumeration<String> e = roomManager.getRoomList().get(roomNum).getClients().keys();
		while (e.hasMoreElements()) {
			user.add(e.nextElement());
		}
		String roomList = sendRoomList();
		StringBuffer sendUser = new StringBuffer(); // 방에 있는 유저 목록
		for (int i = 0; i < master.size(); i++) {
			sendUser.append(user.get(i) + "|" + master.get(i) + ",");
		}
		try {
			if (roomManager.getRoomList().get(roomNum).getPasswd().equals(pass)
					&& roomManager.getRoomList().get(roomNum).enterRoom()) {
				client.sendMessage(MessageType.S_SECRET_ENTRY_RESULT + MessageType.DELIMETER + id
						+ MessageType.DELIMETER + true + MessageType.DELIMETER + roomName + MessageType.DELIMETER
						+ roomNum + MessageType.DELIMETER + sendUser);
				chatServer.sendWaitMessage(MessageType.S_REQUEST_WAITING_LIST + MessageType.DELIMETER + id
						+ MessageType.DELIMETER + userList);
				chatServer.sendWaitMessage(MessageType.S_REQUEST_WAITING_ROOMLIST + MessageType.DELIMETER + id
						+ MessageType.DELIMETER + roomList);
				chatServer.sendChatRoomMessage(MessageType.S_SELECTED_ROOM_USERLIST + MessageType.DELIMETER + id
						+ MessageType.DELIMETER + sendUser, roomNum);
			} else if (!(roomManager.getRoomList().get(roomNum).getPasswd().equals(pass))) {
				client.sendMessage(MessageType.S_SECRET_ENTRY_RESULT + MessageType.DELIMETER + id
						+ MessageType.DELIMETER + false + MessageType.DELIMETER + MessageType.WRONG_ROOM_PASS);
			} else {
				client.sendMessage(MessageType.S_SECRET_ENTRY_RESULT + MessageType.DELIMETER + id
						+ MessageType.DELIMETER + false + MessageType.DELIMETER + MessageType.OVER_MAXUSER);
			}
		} catch (IOException e1) {
			GUIUtil.showErrorMessage("방에 입장 할 수 없습니다.");
		}
	}

	// 초대시 방입장(비밀방, 일반방 구분없이 입장)
	public void entryInviteRoom(String id, int roomNum, String roomName, ChatService client) {
		roomManager.getRoomList().get(0).getClients().remove(id);
		roomManager.getRoomList().get(roomNum).getClients().put(id, client);
		String userList = waitUserList();

		// 방에 들어온 순서를 알아냄
		int enterCount = roomManager.getRoomList().get(roomNum).getClients().size();
		roomManager.getRoomList().get(roomNum).getClients().get(id).setEnterCount(enterCount);

		List<Boolean> master = new ArrayList<Boolean>();
		Enumeration<String> e2 = roomManager.getRoomList().get(roomNum).getClients().keys();
		while (e2.hasMoreElements()) {
			master.add(roomManager.getRoomList().get(roomNum).getClients().get(e2.nextElement()).getMaster());
		}

		List<String> user = new ArrayList<String>();
		Enumeration<String> e = roomManager.getRoomList().get(roomNum).getClients().keys();
		while (e.hasMoreElements()) {
			user.add(e.nextElement());
		}

		StringBuffer sendUser = new StringBuffer(); // 방에 있는 유저 목록
		for (int i = 0; i < master.size(); i++) {
			sendUser.append(user.get(i) + "|" + master.get(i) + ",");
		}

		try {
			if (roomManager.getRoomList().get(roomNum).enterRoom()) {
				client.sendMessage(MessageType.S_ENTRY_RESULT + MessageType.DELIMETER + id + MessageType.DELIMETER
						+ true + MessageType.DELIMETER + roomName + MessageType.DELIMETER + roomNum
						+ MessageType.DELIMETER + sendUser.toString());
				chatServer.sendWaitMessage(MessageType.S_REQUEST_WAITING_LIST + MessageType.DELIMETER + userList); // 대기실에
																													// 있는
																													// 아이들에게
																													// 들어온아이
																													// 알려줌
				chatServer.sendChatRoomMessage(MessageType.S_SELECTED_ROOM_USERLIST + MessageType.DELIMETER + id
						+ MessageType.DELIMETER + sendUser, roomNum);
			} else {
				client.sendMessage(MessageType.S_ENTRY_RESULT + MessageType.DELIMETER + id + MessageType.DELIMETER
						+ false + MessageType.DELIMETER + MessageType.OVER_MAXUSER);
			}
		} catch (IOException e1) {
			GUIUtil.showErrorMessage("방에 입장 할 수 없습니다.");
		}
	}

	// 308번 방 나가기 요청 메소드
	public void exitRoom(String id, int roomNum, ChatService client, boolean checkKick) {
		int enterCount = 1;

		String roomName = roomManager.getRoomList().get(roomNum).getName();

		// 방에 있는 아이들중에서 누가 가장 먼저 들어왔나를 알아냄
		if (roomManager.getRoomList().get(roomNum).getClients().size() != 0) {
			enterCount = roomManager.getRoomList().get(roomNum).getClients().get(id).getEnterCount();
		}
		boolean master2 = client.getMaster();

		int count = roomManager.getRoomList().get(roomNum).leaveRoom();
		roomManager.getRoomList().get(roomNum).getClients().remove(id);
		roomManager.getRoomList().get(0).getClients().put(id, client);

		int minNum = 500;
		String name = "";
		Enumeration<String> e = roomManager.getRoomList().get(roomNum).getClients().keys();
		while (e.hasMoreElements()) {
			name = e.nextElement();
			enterCount = roomManager.getRoomList().get(roomNum).getClients().get(name).getEnterCount();
			minNum = minNumSearch(enterCount, roomManager.getRoomList().get(roomNum).getClients().size());
		}

		ChatService nextMaster = null;
		if (roomManager.getRoomList().get(roomNum).getClients().size() != 0) {
			if (minNum == roomManager.getRoomList().get(roomNum).getClients().get(name).getEnterCount()) {
				nextMaster = roomManager.getRoomList().get(roomNum).getClients().get(name);
				nextMaster.setMaster(true);
			}
		}

		if (count == 0) { // 만약 마지막애가 방에서 나간다면
			chatServer.getServerFrame().getCenterPanel().getLogTA().append(
					String.format("%1$tF %1$tT", Calendar.getInstance()) + " : [" + roomName + "] 채팅방이 페쇄되었습니다.\r\n");
			roomManager.getRoomList().remove(roomNum,roomManager.getRoomList().get(roomNum));
			roomManager.getRoomList().get(0).getClients().put(name, client);
			chatServer.getServerFrame().getCenterPanel().getRoomModel().getRoomList().remove(roomManager.getRoomList().get(roomNum));
			chatServer.getServerFrame().getCenterPanel().getRoomModel().fireTableDataChanged();
		}
		String waitUserList = waitUserList();
		String roomList = sendRoomList();
		
		List<Boolean> master = new ArrayList<Boolean>();
		;
		List<String> user = new ArrayList<String>();
		StringBuffer chatUserList = new StringBuffer();

		if (null != roomManager.getRoomList().get(roomNum)) {
			Enumeration<String> e2 = roomManager.getRoomList().get(roomNum).getClients().keys();
			while (e2.hasMoreElements()) {
				master.add(roomManager.getRoomList().get(roomNum).getClients().get(e2.nextElement()).getMaster());
			}

			Enumeration<String> e3 = roomManager.getRoomList().get(roomNum).getClients().keys();
			while (e3.hasMoreElements()) {
				user.add(e3.nextElement());
			}
			for (int i = 0; i < master.size(); i++) {
				chatUserList.append(user.get(i) + "|" + master.get(i) + ",");
			}
		}

		try {
			// 방장이 나가는게 아니면
			if (!master2) {
				if (!checkKick) { // 강퇴아닌 프로토콜 전송
					client.sendMessage(MessageType.SC_EXIT + MessageType.DELIMETER + id + MessageType.DELIMETER
							+ roomNum + MessageType.DELIMETER + waitUserList + MessageType.DELIMETER + roomList); 
					client.sendMessage(MessageType.S_REQUEST_WAITING_LIST + MessageType.DELIMETER + id
							+ MessageType.DELIMETER + waitUserList); 
					client.sendMessage(MessageType.S_REQUEST_WAITING_ROOMLIST + MessageType.DELIMETER + id
							+ MessageType.DELIMETER + roomList);
					chatServer.sendChatRoomMessage(MessageType.S_SELECTED_ROOM_USERLIST + MessageType.DELIMETER + id
							+ MessageType.DELIMETER + chatUserList.toString(), roomNum);
					chatServer.sendWaitMessage(MessageType.S_REQUEST_WAITING_LIST + MessageType.DELIMETER + id
							+ MessageType.DELIMETER + waitUserList);
					chatServer.sendWaitMessage(MessageType.S_REQUEST_WAITING_ROOMLIST + MessageType.DELIMETER + id
							+ MessageType.DELIMETER + roomList);
				} else { // 강퇴인 프로토콜 전송
					client.sendMessage(MessageType.S_KICK + MessageType.DELIMETER + id + MessageType.DELIMETER + roomNum
							+ MessageType.DELIMETER + waitUserList + MessageType.DELIMETER + roomList);
					client.sendMessage(MessageType.S_REQUEST_WAITING_LIST + MessageType.DELIMETER + id
							+ MessageType.DELIMETER + waitUserList); // 방을 나가는	아이에게 현재방 정보를알려줌
					client.sendMessage(MessageType.S_REQUEST_WAITING_ROOMLIST + MessageType.DELIMETER + id
							+ MessageType.DELIMETER + roomList); // 방을 나가는 아이에게 대기실에 있는 유저리스트를 알려줌
					chatServer.sendChatRoomMessage(MessageType.S_SELECTED_ROOM_USERLIST + MessageType.DELIMETER + id
							+ MessageType.DELIMETER + chatUserList.toString(), roomNum);
					chatServer.sendWaitMessage(MessageType.S_REQUEST_WAITING_LIST + MessageType.DELIMETER + id
							+ MessageType.DELIMETER + waitUserList);
					chatServer.sendWaitMessage(MessageType.S_REQUEST_WAITING_ROOMLIST + MessageType.DELIMETER + id
							+ MessageType.DELIMETER + roomList);
				}
				// 방장이 나간다면
			} else {
				client.sendMessage(MessageType.SC_EXIT + MessageType.DELIMETER + id + MessageType.DELIMETER + roomNum
						+ MessageType.DELIMETER + waitUserList + MessageType.DELIMETER + roomList);
				client.sendMessage(MessageType.S_REQUEST_WAITING_LIST + MessageType.DELIMETER + id
						+ MessageType.DELIMETER + waitUserList); // 방을 나가는 아이에게
																	// 현재방 정보를
																	// 알려줌
				client.sendMessage(MessageType.S_REQUEST_WAITING_ROOMLIST + MessageType.DELIMETER + id
						+ MessageType.DELIMETER + roomList); // 방을 나가는 아이에게 대기실에
																// 있는 유저리스트를 알려줌
				chatServer.sendChatRoomMessage(MessageType.S_SELECTED_ROOM_USERLIST + MessageType.DELIMETER + id
						+ MessageType.DELIMETER + chatUserList.toString(), roomNum);
				chatServer.sendWaitMessage(MessageType.S_REQUEST_WAITING_LIST + MessageType.DELIMETER + id
						+ MessageType.DELIMETER + waitUserList);
				chatServer.sendWaitMessage(MessageType.S_REQUEST_WAITING_ROOMLIST + MessageType.DELIMETER + id
						+ MessageType.DELIMETER + roomList);
			}
		} catch (IOException e3) {
			// e.printStackTrace();
		}
	}

	// 모든방의 정보를 보내준다
	public String sendRoomList() {
		StringBuffer roomInfo = new StringBuffer();
		for (int i = 0; i < roomManager.getRoomList().size(); i++) {
			if (null != roomManager.getRoomList().get(i)) {
				roomInfo.append(roomManager.getRoomList().get(i).toString() + ",");
			} else {
				continue;
			}
		}
		return roomInfo.toString();
	}

	// 대기실에 접속한 모든유저를 준다.
	public String waitUserList() {
		StringBuffer roomUser = new StringBuffer();
		Enumeration<String> e3 = roomManager.getRoomList().get(0).getClients().keys();
		while (e3.hasMoreElements()) {
			roomUser.append(e3.nextElement() + ",");
		}
		if (roomUser.length() == 0)
			return "";
		return roomUser.toString();
	}

	public int minNumSearch(int enterCount, int count) {
		int temp = Integer.MAX_VALUE;
		for (int i = 0; i < count; i++) {
			if (temp > enterCount) {
				temp = enterCount;
			}
		}
		return temp;
	}
}
