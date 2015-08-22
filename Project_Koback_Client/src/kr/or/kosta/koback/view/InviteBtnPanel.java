package kr.or.kosta.koback.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class InviteBtnPanel extends JFrame {

	JPanel inviteP; // 초대버튼을 누르면 방장에게 보여지는 패널
	JLabel choiceL; // 상단 "초대할 사람을 선택하세요"
	JList<String>userJList ; // 대기실 유저 리스트
	JButton inviteB, cancelB; // 초대버튼과 취소버튼
	DefaultListModel<String> model;

	GridBagLayout grid;
	GridBagConstraints cons;

	ChatUI chatUI;

	public InviteBtnPanel(ChatUI chatUI, ArrayList<String> userList) {
		this.chatUI = chatUI;
		inviteP = new JPanel();

		choiceL = new JLabel("초대할 사람을 선택하세요");
		inviteB = new JButton("초대");
		cancelB = new JButton("취소");

		model = new DefaultListModel<String>();
		chatUserList(userList);
		this.userJList = new JList<String>(model);

		grid = new GridBagLayout();
		cons = new GridBagConstraints();

		setComponents();
		eventRegist();
		
		
	}

	public void setComponents() {
		setLayout(grid);
		cons.fill = GridBagConstraints.BOTH;
	    cons.insets = new Insets(2,1,2,1);
	    JScrollPane sp = new JScrollPane(userJList);
		
	    add(choiceL,		1, 0, 5, 1, 0.0, 0.0);
	    add(new JLabel(""), 1, 2, 5, 1, 0.0, 0.0);
	    add(new JLabel(""), 0, 3, 1, 1, 0.0, 0.0);
	    add(sp, 			1, 3, 10, 5, 0.0, 0.0);
	    add(new JLabel(""), 9, 2, 5, 1, 0.0, 0.0);
	    add(inviteB,		1, 10, 1, 1, 0.05, 0.0);
	    add(new JLabel(""), 3, 10, 2, 1, 0.0, 0.0);
	    add(cancelB,		5, 10, 1, 1, 0.05, 0.0);
	  
	    
	}
	
		public void exit() {
			setVisible(false);
			dispose();
			System.exit(0);
		}
	   
	   public void eventRegist(){
		   
		   addWindowListener(new WindowAdapter() {
			   @Override
			public void windowClosing(WindowEvent e) {

				   exit();
			}
		});
		   
		   cancelB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				exit();
				
			}
		});
		   
		   
	   }

	public void chatUserList(List<String> userList) {

		userList.add("가승호(eifwljef)");
		userList.add("용용");
		userList.add("비닝이");

		for (String string : userList) {
			model.addElement(string);
		}
	}

	/* gridBagLayout에 컴포넌트 추가 메소드 */
	private void add(Component component, int gridx, int gridy, int gridwidth, int gridheight, double weightx,
			double weighty) {
		cons.gridx = gridx;
		cons.gridy = gridy;
		cons.gridwidth = gridwidth;
		cons.gridheight = gridheight;
		cons.weightx = weightx;
		cons.weighty = weighty;
		grid.setConstraints(component, cons);
		add(component);
	}

}