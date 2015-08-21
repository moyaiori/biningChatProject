package kr.or.kosta.koback.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import kr.or.kosta.koback.server.ChatService;

/**
 * Hashtable을 이용한 방 목록 관리 추상화
 * 
 * @author 유안상
 */

public class RoomManager {
	public static int ROOM_NUMBER = 0;
	
	final int MAX_ROOM_COUNT = 50;

	private Hashtable<Integer, Room> roomList;
	private Room room;
	private int totalRoomCount = 0;
	
	public int openRoomNum = 0;

	public RoomManager() {
		roomList = new Hashtable<Integer, Room>();
	}

	public void addRoom(Room room) {
		roomList.put(room.getRoomNum(), room);
	}

	public void addSecretRoom(Room room) {
		roomList.put(room.getRoomNum(), room);
	}

	public void addWatingRoom() {
		room = new Room("대기실", 10000000);
		roomList.put(0, room);
	}

	public void removeRoom(String name) {
		name = room.getName(); // getName메소드를 이용하여 방 제목을 가져옴.
		for (int i = 0; i < ROOM_NUMBER; i++) {
			if (roomList.keys().equals(name)) { // 만약 roomList의 key값과 매개변수로 받은
												// name의 이름이 같으면
				roomList.remove(name); // 해당 방이름을 가진 방을 삭제함
			}
		}
		totalRoomCount--;
	}

	public List<Room> getAllRoomList() {
		Enumeration<Integer> e = roomList.keys();
		List<Room> list = new ArrayList<Room>();
		while (e.hasMoreElements()) {
			list.add(roomList.get(e));
			int roomNum = (int) e.nextElement();
			if (roomNum == 0)
				continue;
		}
		return list;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public int getTotalRoomCount() {
		return totalRoomCount;
	}

	public Hashtable<Integer, Room> getRoomList() {
		return roomList;
	}

	public void setRoomList(Hashtable<Integer, Room> roomList) {
		this.roomList = roomList;
	}

	public void setTotalRoomCount(int totalRoomCount) {
		this.totalRoomCount = totalRoomCount;
	}

	public int getRoomNumber() {
		return ROOM_NUMBER;
	}

	public void setRoomNumber(int roomNumber) {
		ROOM_NUMBER = roomNumber;
	}
	
	public int getOpenRoomNum() {
		return openRoomNum;
	}
}
