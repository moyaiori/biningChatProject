package kr.or.kosta.koback.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class InviteBtnPanel extends JFrame {

	JPanel inviteP; //초대버튼을 누르면 방장에게 보여지는 패널
	JLabel choiceL; //상단 "초대할 사람을 선택하세요"
	JList<String> userList; // 대기실 유저 리스트
	JButton inviteB, cancelB; //초대버튼과 취소버튼
	DefaultListModel<String> model;
	
	GridBagLayout grid;
	GridBagConstraints cons;
	
	ChatUI chatUI;
	
	
	
	public InviteBtnPanel(ChatUI chatUI){
		this.chatUI = chatUI;
		inviteP = new JPanel();
		
		
		choiceL = new JLabel("초대할 사람을 선택하세요");
		inviteB = new JButton("초대");
		cancelB = new JButton("취소");

		model = new DefaultListModel<String>();
		userList = new JList<String>(model);
		
		grid = new GridBagLayout();
		cons = new GridBagConstraints();
		
		setComponents();
		
		
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
	    add(inviteB,		1, 10, 1, 1, 0.05, 0.0);
	    add(new JLabel(""), 3, 10, 2, 1, 0.0, 0.0);
	    add(cancelB,		5, 10, 1, 1, 0.05, 0.0);
	  
	    chatUserList();
	}
	
	 public void chatUserList(){
	      String[] list = {"가승호(eifwljef)", "용용", "비닝이"};
	      for (String string : list) {
	         model.addElement(string);
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
	
	
	
    
}