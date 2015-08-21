package kr.or.kosta.koback.view;

import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

public class CbUserModel extends DefaultComboBoxModel<String>{

	/** 데이터 저장을 위한 모델(콜렉션 중 하나 선택)*/
	private Vector<String> userCB;
	 
	public CbUserModel(){
		userCB = new Vector<String>();
		userCB.addElement("전체");
		userCB.addElement("멍청이");
	}
	
	public Vector<String> getUserCB() {
		return userCB;
	}
	public void setUserCB(Vector<String> userCB) {
		this.userCB = userCB;
	}
	
	@Override
	public int getSize() {
		return userCB.size();
	}
	
	@Override
	public String getElementAt(int index) {
		return userCB.elementAt(index);
	}
	
	@Override
	public Object getSelectedItem() {
		return userCB.elementAt(0);
	}
	
	/**
	 * 접속자 등록 기능
	 */
	public void addUser(String user){
		// 뷰하고, 컨트롤러에게 데이터 변경을 통보
		/** list의 내용을 리스트(0)부터 리스트사이즈(끝)까지 */
		userCB.addElement(user);
		fireContentsChanged(userCB, 0, userCB.size()); // reflash 효과 0번부터 list.size()끝까지 갱신
	}
	
	/**
	 * 접속자 퇴장 기능
	 */
	public void logoutUser(String user){
		userCB.removeElement(user);
		// 뷰하고, 컨트롤러에게 데이터 변경을 통보
		fireContentsChanged(userCB, 0, userCB.size());
	}
	
}
