package kr.or.kosta.koback.model;

import java.util.Hashtable;

import kr.or.kosta.koback.server.ChatService;
/**
 * 채팅방(대기실) 객체 추상화
 * @author 유안상
 */
public class Room {
	
	final int MAX_ROOM = 50;
	
	private String roomName;
	private ChatService master;
	
	private String passwd;
	private int maxUserNum;
	private int nowUserNum=0;
	private int roomNum;
	
	private Hashtable<String, ChatService> clients;
	
	/** 대기실 생성자*/
	public Room(String waitName, int maxUserNum){
		clients = new Hashtable<String, ChatService>();
		master = new ChatService();
		master.setUserId("Admin");
		roomName = waitName;
		this.maxUserNum = maxUserNum;
		roomNum = 0;
	}

	/** 일반방 생성자*/
	public Room(String name, ChatService master, int maxUserNum){
		nowUserNum++;
		roomNum = ++RoomManager.ROOM_NUMBER;
		clients = new Hashtable<String, ChatService>();
		this.roomName = name;
		this.master = master;
		this.maxUserNum = maxUserNum;
		clients.put(master.getUserId(), master);
	}
	
	/** 비밀방 생성자 */
	public Room(String name,ChatService master, String passwd, int maxUserNum){
		nowUserNum++;
		roomNum = ++RoomManager.ROOM_NUMBER;
		clients = new Hashtable<String, ChatService>();
		this.roomName = name;
		this.master = master;
		this.passwd = passwd;
		this.maxUserNum = maxUserNum;
		clients.put(master.getUserId(), master);
	}
	
	
	public boolean enterRoom(){
		nowUserNum++;
		if(nowUserNum > maxUserNum){
			nowUserNum--;
			return false; // 방 입장 불가
		}
		return true; // 방 입장 허용
	}
	public int leaveRoom(){
		return --nowUserNum;
	}
	
	/** getter/setter 메소드 */
	public String getName() {
		return roomName;
	}
	public void setName(String name) {
		this.roomName = name;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public int getMaxUserNum() {
		return maxUserNum;
	}
	public void setMaxUserNum(int maxUserNum) {
		this.maxUserNum = maxUserNum;
	}
	public int getNowUserNum() {
		return nowUserNum;
	}
	public void setNowUserNum(int nowUserNum) {
		this.nowUserNum = nowUserNum;
	}
	public Hashtable<String, ChatService> getClients() {
		return clients;
	}
	public void setClients(Hashtable<String, ChatService> clients) {
		this.clients = clients;
	}
	
	public ChatService getMaster() {
		return master;
	}

	public void setMaster(ChatService master) {
		this.master = master;
	}

	public int getRoomNum() {
		return roomNum;
	}

	public void setRoomNum(int roomNum) {
		this.roomNum = roomNum;
	}

	@Override
	public String toString() {
		if(passwd==null){
			return roomName + "," + master.getUserId() + "," + "없음" + ","+ roomNum + "," + maxUserNum;
		}else{
			return roomName + "," + master.getUserId() + "," + passwd + ","+ roomNum + "," + maxUserNum;
		}
			
	}
}