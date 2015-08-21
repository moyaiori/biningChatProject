package kr.or.kosta.koback.view;

import java.util.Vector;

import javax.swing.AbstractListModel;

/**
 * JList의 뷰+컨트롤러(델리게이트)가 사용하는 데이터 저장을 위한 모델클래스
 * @author AS
 */
public class UserModel extends AbstractListModel{
	
	/** 데이터 저장을 위한 모델(콜렉션 중 하나 선택)*/
	public Vector<String> list;
	
	public UserModel(){
		list = new Vector<String>();
	}
	
	@Override
	// View와 Controller에 의해 자동 호출되는 콜백메소드
	public int getSize() {
		return list.size();
	}

	@Override
	// View와 Controller에 의해 자동 호출되는 콜백메소드
	// 실제 해당하는 데이터를 보여주기 위한 메소드
	public Object getElementAt(int index) {
		return list.elementAt(index);
	}

	/**
	 * 접속자 등록 기능
	 */
	public void addUser(String user){
		// 뷰하고, 컨트롤러에게 데이터 변경을 통보
		/** list의 내용을 리스트(0)부터 리스트사이즈(끝)까지 */
		list.addElement(user);
		fireContentsChanged(list, 0, list.size()); // reflash 효과 0번부터 list.size()끝까지 갱신
	}
	
	/**
	 * 접속자 퇴장 기능
	 */
	public void logoutUser(String user){
		list.removeElement(user);
//		list.clear();
		// 뷰하고, 컨트롤러에게 데이터 변경을 통보
		fireContentsChanged(list, 0, list.size());
	}
	
	/**
	 * 수정 기능 추가
	 */
	public void updateName(String name, int index){
//		System.out.println(index + name);
		list.setElementAt(name, index);
		// 뷰하고, 컨트롤러에게 데이터 변경을 통보
		fireContentsChanged(list, 0, list.size());
	}
}