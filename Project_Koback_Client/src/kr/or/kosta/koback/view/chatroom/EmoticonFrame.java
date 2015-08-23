package kr.or.kosta.koback.view.chatroom;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

public class EmoticonFrame extends JFrame {

	JTabbedPane emoticonPane;
	
	EmoticonStickyPanel sticky;
	EmoticonJungPanel jung;
	
	ChatRoomPanel owner;
	
	
	//이모티콘 프레임에 탭 만들기
	public EmoticonFrame(ChatRoomPanel owner){
		this.owner = owner;
		
		setTitle("이모티콘");
		
		sticky = new EmoticonStickyPanel(this);
		jung = new EmoticonJungPanel(this);
		emoticonPane = new JTabbedPane();
		emoticonPane.addTab("낢", jung);
		emoticonPane.addTab("스티키", sticky);
		setUndecorated(true);
		setComponents();
		eventRegist();
	}
	
	public void setComponents(){
		add(emoticonPane, BorderLayout.CENTER);
	}
	
	
	public void exit() {
		setVisible(false);
		dispose();
		System.exit(0);
	}
	
	public void eventRegist() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});
	}

	public int[] emoticonLocation(int width){
		int[] location = new int[2];
		location[0] = owner.getChatUI().getX() + owner.getChatUI().getWidth() - width;
		location[1] = owner.getChatUI().getY() + owner.getChatUI().getHeight();
		return location;
	}
	
	/*
	public static void main(String[] args) {
		EmoticonFrame frame = new EmoticonFrame();
		frame.setComponents();
//		frame.setLocation(700, 500);
		frame.setSize(400,400);
		frame.setVisible(true);
		frame.eventRegist();
		
	}
	*/
	
	
}
