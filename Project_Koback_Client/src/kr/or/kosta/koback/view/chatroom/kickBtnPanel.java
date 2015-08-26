package kr.or.kosta.koback.view.chatroom;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.rmi.CORBA.Util;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.util.GUIUtil;
import kr.or.kosta.koback.view.login.UserLoginPanel;

public class kickBtnPanel extends JFrame {

	private JPanel kickP; //초대버튼을 누르면 방장에게 보여지는 패널
	private JLabel choiceL; //상단 "초대할 사람을 선택하세요"
	private JList<String> userList; // 대기실 유저 리스트
	private JButton kickB;
	private JButton cancelB; //초대버튼과 취소버튼
	private DefaultListModel<String> kickListmodel;
	
	GridBagLayout grid;
	GridBagConstraints cons;
	
	ChatUI chatUI;
	
	
	
	public kickBtnPanel(ChatUI chatUI){
		this.chatUI = chatUI;
		kickP = new JPanel();
//		URL url = getClass().getClassLoader().getResource("./images/popupColor.jpg");
//		this.setContentPane(new JLabel(new ImageIcon(url)));
				
		choiceL = new JLabel("강퇴 하시겠습니까?");
		kickB = new JButton("확인");
		cancelB = new JButton("취소");
		/*08 24 조현빈 수정*/
		kickListmodel = new DefaultListModel<String>();
		userList = new JList<String>(kickListmodel);
		
		grid = new GridBagLayout();
		cons = new GridBagConstraints();
		
		setComponents();
		eventRegist();
		
	}
	
	public void eventRegist(){
		kickB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String kickId = userList.getSelectedValue();			
				try {
/*					System.out.println("[306 보내는 부분]"+MessageType.C_KICK + MessageType.DELIMETER + UserLoginPanel.userId + MessageType.DELIMETER
							+ UserLoginPanel.userRoom + MessageType.DELIMETER + kickId);*/
					chatUI.getChatClient()
					.sendMessage(MessageType.C_KICK + MessageType.DELIMETER + UserLoginPanel.userId + MessageType.DELIMETER
							+ UserLoginPanel.userRoom + MessageType.DELIMETER + kickId);
					String message = kickId+"님 강퇴를 성공하였습니다.";
					GUIUtil.showMessage(message);
					exit();
					
				} catch (IOException e1) {
				}
			}
		});
		
		cancelB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
					exit();
				
				
			}
		});
	}
	
	public void exit(){
		dispose();
		setVisible(false);
	}
	
	public void setComponents(){
		setLayout(grid);
		cons.fill = GridBagConstraints.BOTH;
	    cons.insets = new Insets(2,1,2,1);
	    JScrollPane sp = new JScrollPane(userList);
		
	    add(choiceL,		1, 0, 5, 1, 0.0, 0.0);
	    add(new JLabel(""), 1, 2, 5, 1, 0.0, 0.0);
	    add(new JLabel(""), 0, 3, 1, 1, 0.0, 0.0);
	    add(sp, 			1, 3, 10, 5, 0.0, 0.0);
	    add(new JLabel(""), 9, 2, 5, 1, 0.0, 0.0);
	    add(kickB,		1, 10, 1, 1, 0.05, 0.0);
	    add(new JLabel(""), 3, 10, 2, 1, 0.0, 0.0);
	    add(cancelB,		5, 10, 1, 1, 0.05, 0.0);
	  
	    
	    
	}
	 /*수정사항 0824 조현빈*/
	public void kickUserList(String chatRoomList){
		
//		채팅방에 있는 유저 리스트 출력 
		 String users = chatRoomList;
		 String user[] = users.split(",");
		 kickListmodel.clear();
		 for (String nickName : user) {	 
			 String[] attrs = nickName.split("\\|");
			 for(int i =0; i< (attrs.length); i++){
//				 System.out.println(attrs[i]);
				 if((i+1)%2==1){	//짝
					 System.out.println("홀:"+attrs[i]);
					 if(!(new Boolean(attrs[i]))){
						 System.out.println(attrs[i]);
						 kickListmodel.addElement(attrs[i]);
					 }	 
				 }				 
			} 
		}
	
	 }

	 /*gridBagLayout에 컴포넌트 추가 메소드*/
	   private void add(Component component, int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty){
	      cons.gridx = gridx;
	      cons.gridy = gridy;
	      cons.gridwidth  = gridwidth;
	      cons.gridheight  = gridheight;
	      cons.weightx = weightx;
	      cons.weighty = weighty;
	      grid.setConstraints(component, cons);
	      add(component);
	   }





	public JPanel getKickP() {
		return kickP;
	}





	public void setKickP(JPanel kickP) {
		this.kickP = kickP;
	}





	public JLabel getChoiceL() {
		return choiceL;
	}





	public void setChoiceL(JLabel choiceL) {
		this.choiceL = choiceL;
	}





	public JList<String> getUserList() {
		return userList;
	}





	public void setUserList(JList<String> userList) {
		this.userList = userList;
	}





	public JButton getKickB() {
		return kickB;
	}





	public void setKickB(JButton kickB) {
		this.kickB = kickB;
	}





	public JButton getCancelB() {
		return cancelB;
	}





	public void setCancelB(JButton cancelB) {
		this.cancelB = cancelB;
	}





	public DefaultListModel<String> getModel() {
		return kickListmodel;
	}





	public void setModel(DefaultListModel<String> kickListmodel) {
		this.kickListmodel = kickListmodel;
	}





	public GridBagLayout getGrid() {
		return grid;
	}





	public void setGrid(GridBagLayout grid) {
		this.grid = grid;
	}





	public GridBagConstraints getCons() {
		return cons;
	}





	public void setCons(GridBagConstraints cons) {
		this.cons = cons;
	}





	public ChatUI getChatUI() {
		return chatUI;
	}





	public void setChatUI(ChatUI chatUI) {
		this.chatUI = chatUI;
	}
	
	
	
    
}