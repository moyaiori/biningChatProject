package kr.or.kosta.koback.view;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import kr.or.kosta.koback.server.ChatServer;
import kr.or.kosta.koback.server.ChatService;
import kr.or.kosta.koback.util.GUIUtil;
/**
 * 
 * @author 유안상
 */
public class ServerFrame extends JFrame {
	CenterPanel centerPanel;
	SouthPanel southPanel;
	public ChatServer chatServer;
	
	public ServerFrame(){
		this("서버관리 프로그램");
	}
	
	ServerFrame(String title){
		super(title);
		centerPanel = new CenterPanel();
		southPanel = new SouthPanel(centerPanel);
		
		setSize(1000, 500);
		setResizable(false);
		GUIUtil.setCenterScreen(this);
		GUIUtil.setLookAndFeel(this, GUIUtil.THEME_NIMBUS);
//	    serverFrame.setUndecorated(true);
		getCenterPanel().getLogTA().append("KobackServer Start!!+\r\n");
	    setVisible(true);
		setComponents();
		setEvent();
	}
	
	public void setComponents(){
		setLayout(new BorderLayout());
		add(centerPanel, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
		
	}
	
	public void setEvent(){
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
			@Override
			public void windowOpened(WindowEvent e) {
				southPanel.getAdminTF().requestFocus();
			}
		});
		
	}
//	 /** 종료 메소드 */
	public void exit(){
		setVisible(false);
		dispose();
		System.exit(0);
	}

	public CenterPanel getCenterPanel() {
		return centerPanel;
	}

	public void setCenterPanel(CenterPanel centerPanel) {
		this.centerPanel = centerPanel;
	}

	public SouthPanel getSouthPanel() {
		return southPanel;
	}

	public void setSouthPanel(SouthPanel southPanel) {
		this.southPanel = southPanel;
	}
	


	@Override
	public String toString() {
		return "ServerFrame [centerPanel=" + centerPanel + ", southPanel=" + southPanel + "]";
	}
	
	
}
