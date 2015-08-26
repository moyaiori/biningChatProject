package kr.or.kosta.koback.view.login;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import kr.or.kosta.koback.view.chatroom.ChatUI;
/*
 * 	클래스 명 : UserJoinFrame 
 *  클래스 역할 : 회원 가입
 *  화면 구현 : 윤성훈 (2015 02 19 (오후 2시))
 *  
 *  
 * */
public class UserJoinFrame extends JFrame {
	UserJoinPanel userJoinPanel;
		ImageIcon icon;
	
	
	public UserJoinFrame(ChatUI chatUI) {
		this("회원가입",chatUI);
	}
	public UserJoinFrame(String title,ChatUI chatUI) {
		super(title);
		userJoinPanel = new UserJoinPanel(chatUI);
		
		setComponent();
		
	}
	public void setComponent(){
		add(userJoinPanel);
	
	}	

	public void exit(){	
		dispose();
		setVisible(false);

	}

}
