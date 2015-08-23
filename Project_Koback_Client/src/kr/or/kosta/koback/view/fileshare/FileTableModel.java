package kr.or.kosta.koback.view.fileshare;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/*
 * 	클래스 명 : ChatUI 
 *  클래스 역할 :카드레이아웃을이용하여 프레임 구현 (로그인 - 대기실 - 채팅방)
 *  화면 구현 : 조현빈 (2015 02 19 (오후 2시))
 *  
 *  추가 구현 : 조현빈
 *    추가사항 (2015-08-20)
 *    1. eventRegist() 메소드
 *         - 추가 1)최초 서버와 연결
 *  
 * */
public class FileTableModel extends AbstractTableModel{

	Vector<String> headerNames;
	Vector<String> cellDatas;		//Student : row에 해당함.
	
	public FileTableModel() {
//		headerNames 초기화
		headerNames = new Vector<String>();
		headerNames.addElement("파일번호");
		headerNames.addElement("파일명");
		headerNames.addElement("올린사람");
		headerNames.addElement("날짜");
		headerNames.addElement("다운받기");
//		cellDatas 타입 Instrument 선언하여 인스턴스 생성.
		cellDatas = new Vector<String>();
	}
	
	@Override
	/*Row의 사이즈를 리턴*/
	public int getRowCount() {
		return cellDatas.size();
	}

	@Override
	/*headerName의 사이즈를 리턴*/
	public int getColumnCount() {
		return headerNames.size();
	}
	
	@Override
	/*테이블에 보여줄 데이터*/
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object cellData = null;

		switch(columnIndex){
			case 0:
				break;
			case 1:
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				break;
		}
		return cellData;
	}
	@Override
	public String getColumnName(int column) {
		return headerNames.elementAt(column);
	}
	
	public Class getColumnClass(int columnIndex) {
		return getValueAt(0, columnIndex).getClass();
	}


}





