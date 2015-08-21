package kr.or.kosta.koback.model;

/**
 * 대기실 방정보를 가지고있는 방객체
 * 
 * @author 이광용
 *
 */
public class Room {

	private String roomTitle; // 방 이름
	private String master; // 방장닉네임
	private String passwd; // 비밀방 비밀번호, 비밀번호 유무에 따라 일반방, 비밀방 구분
	private int roomNum; // 방고유번호
	private int maxUserNum; // 방 최대인원
	private int nowUserNum=0; // 방 현재인원

	public Room(String roomTitle, String master, String passwd, int roomNum, int maxUserNum, int nowUserNum) {
		super();
		this.roomTitle = roomTitle;
		this.master = master;
		this.passwd = passwd;
		this.roomNum = roomNum;
		this.maxUserNum = maxUserNum;
		this.nowUserNum = nowUserNum;
	}

	public Room(String roomTitle, String master, String passwd, int roomNum, int maxUserNum) {
		super();
		this.roomTitle = roomTitle;
		this.master = master;
		this.passwd = passwd;
		this.roomNum = roomNum;
		this.maxUserNum = maxUserNum;
	}

	public String getRoomTitle() {
		return roomTitle;
	}

	public void setRoomTitle(String roomTitle) {
		this.roomTitle = roomTitle;
	}

	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public int getRoomNum() {
		return roomNum;
	}

	public void setRoomNum(int roomNum) {
		this.roomNum = roomNum;
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

	@Override
	public String toString() {
		return "Room [roomTitle=" + roomTitle + ", master=" + master + ", passwd=" + passwd + ", roomNum=" + roomNum
				+ ", maxUserNum=" + maxUserNum + "]";
	}

}
