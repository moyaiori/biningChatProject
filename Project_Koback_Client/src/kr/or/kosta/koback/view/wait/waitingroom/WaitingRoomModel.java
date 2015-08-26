package kr.or.kosta.koback.view.wait.waitingroom;

import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import kr.or.kosta.koback.model.Room;



public class WaitingRoomModel extends AbstractTableModel{
	
	Vector<String> waitingRoomHeader;
	Vector<Room> waitingRoomRoomList;
	
	
	int roomNum;
	String password;
	String roomTitle;

	public WaitingRoomModel() {
		waitingRoomHeader = new Vector<String>();
		waitingRoomHeader.addElement("방번호");
		waitingRoomHeader.addElement("방장");
		waitingRoomHeader.addElement("제목");
		waitingRoomHeader.addElement("최대정원");
		
		waitingRoomRoomList = new Vector<Room>();
	}
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return waitingRoomHeader.size();
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return waitingRoomRoomList.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object cellData = null;
		password = waitingRoomRoomList.get(rowIndex).getPasswd();
		roomNum = waitingRoomRoomList.get(rowIndex).getRoomNum();
		roomTitle = waitingRoomRoomList.get(rowIndex).getRoomTitle();
		switch(columnIndex){
		case 0:		// 방번호
			cellData = waitingRoomRoomList.get(rowIndex).getRoomNum();
			break;
		case 1:		// 방장
			cellData = waitingRoomRoomList.get(rowIndex).getMaster();
			break;
		case 2:		// 제목
			cellData = waitingRoomRoomList.get(rowIndex).getRoomTitle();
			break;
		case 3:		// 최대정원
			cellData = waitingRoomRoomList.get(rowIndex).getMaxUserNum();
			break;
		}
		return cellData;
	}
	
	@Override
	//추상 메소드는 아니지만 헤더이름이 나오기위해서는
	// 반드시 재정의 필요
	public String getColumnName(int column) {
		return waitingRoomHeader.elementAt(column);
	}
	
	// 필수적으로 재정의해야 하는 메소드는 아니지만
	// 테이블의 각각의 셀에 원하는 데이터유형을 보여주고자 한다면 ...
//	@Override
//	public Class getColumnClass(int columnIndex) {
//		return getValueAt(0, columnIndex).getClass();
//	}
	
	/**
	 * 일련번호 검색 결과
	 */
	public void setWaitingRoomList(List<Room> list){
//		System.out.println("갱신했습니다.");
		waitingRoomRoomList.clear();
		for (Room room : list) {
			waitingRoomRoomList.add(room);
		}
		fireTableStructureChanged();
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	

}
