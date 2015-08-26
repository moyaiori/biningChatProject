package kr.or.kosta.koback.view;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import kr.or.kosta.koback.model.Room;


/**
 * 테이블의 Model을 담당하는 클래스
 * 테이블 내의 데이터를 저장 및 관리한다.
 * @author 유안상
 */
public class roomTableModel extends AbstractTableModel {
	Vector<String> headerNames;
	Vector<Room> roomList;
	
	public roomTableModel(){
		/** 컬럼의 1행에 항목들을(헤더이름) 표시해주는 Vector */
		headerNames = new Vector<String>();
		headerNames.addElement("번호");
		headerNames.addElement("제목");
		headerNames.addElement("방장");
		roomList = new Vector<Room>();
	}
	
	/** 규약 메소드들은 반드시 오버라이딩 해야한다. */
	public Vector getRoomList(){
		return roomList;
	}
	
	@Override
	public int getRowCount() {
		return roomList.size();
	}

	@Override
	public int getColumnCount() {
		return headerNames.size();
	}
	
	/** Column의 번호와 Row의 번호에 따라
	 *  출력되는 값을 다르게 하기 위해서(Account의 값에 따라서)
	 *  만들어진 메소드
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object cellData = null;
		switch (columnIndex) {
			/** 방 번호 */
			case 0:
				cellData = roomList.elementAt(rowIndex).getRoomNum();
				break;
			/** 방 제목 */
			case 1:
				cellData = roomList.elementAt(rowIndex).getName();
				break;
			/** 방장 이름 */
			case 2:
				cellData = roomList.elementAt(rowIndex).getMaster().getUserId();
				break;
		}
		return cellData;
	}
	
	@Override
	/** 추상 메소드는 아니지만 "헤더이름"이 나오기위해서는 반드시 재정의를 해야한다 */
	public String getColumnName(int column) {
		return headerNames.elementAt(column);
	}

	public void setRoomList(Vector<Room> roomList) {
		this.roomList = roomList;
	}
	
	
}