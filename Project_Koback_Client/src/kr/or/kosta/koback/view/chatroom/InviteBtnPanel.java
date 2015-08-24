package kr.or.kosta.koback.view.chatroom;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.model.Room;

public class InviteBtnPanel extends JFrame {

	JPanel inviteP; // 초대버튼을 누르면 방장에게 보여지는 패널
	JLabel choiceL; // 상단 "초대할 사람을 선택하세요"
	JList<String> userJList; // 대기실 유저 리스트
	JButton inviteB, cancelB; // 초대버튼과 취소버튼
	DefaultListModel<String> model;

	GridBagLayout grid;
	GridBagConstraints cons;

	ChatUI chatUI;

	int roomNum;

	public InviteBtnPanel(ChatUI chatUI, String userList) {
		super("채팅방 초대");
		this.chatUI = chatUI;
		this.setContentPane(new JLabel(new ImageIcon("classes/images/popupColor.jpg")));

		inviteP = new JPanel();

		choiceL = new JLabel("초대할 사람을 선택하세요");
		inviteB = new JButton("초대");
		cancelB = new JButton("취소");

		model = new DefaultListModel<String>();
		System.out.println("userList : " + userList);
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
		cons.insets = new Insets(2, 1, 2, 1);
		JScrollPane sp = new JScrollPane(userJList);

		add(choiceL, 1, 0, 5, 1, 0.0, 0.0);
		add(new JLabel(""), 1, 2, 5, 1, 0.0, 0.0);
		add(new JLabel(""), 0, 3, 1, 1, 0.0, 0.0);
		add(sp, 1, 3, 10, 5, 0.0, 0.0);
		add(new JLabel(""), 9, 2, 5, 1, 0.0, 0.0);
		add(inviteB, 1, 10, 1, 1, 0.05, 0.0);
		add(new JLabel(""), 3, 10, 2, 1, 0.0, 0.0);
		add(cancelB, 5, 10, 1, 1, 0.05, 0.0);

	}

	public void exit() {
		setVisible(false);
		dispose();
	}

	public void eventRegist() {

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				exit();
			}
		});

		inviteB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectInviteUser();
			}
		});

		cancelB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});

	}

	// 유저 초대 하기
	public void selectInviteUser(){
		JOptionPane.showMessageDialog(this, userJList.getSelectedValue() + "님께 초대를 요청하였습니다.", "초대 요청", JOptionPane.INFORMATION_MESSAGE);
		StringBuilder sb = new StringBuilder();
		sb.append("");
		sb.append(roomNum);
		String roomNum_string = sb.toString();
		try {
			chatUI.getChatClient().sendMessage(MessageType.C_INVITE_USER + MessageType.DELIMETER + chatUI.getUserLoginPanel().userId + MessageType.DELIMETER + roomNum_string + MessageType.DELIMETER + userJList.getSelectedValue());
//			chatUI.getChatClient().sendMessage(MessageType.C_INVITE_USER + MessageType.DELIMETER + chatUI.getUserLoginPanel().userId + MessageType.DELIMETER + roomNum_string + MessageType.DELIMETER + userJList.getSelectedValue() + MessageType.DELIMETER + );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		exit();
	}

	// 대기실 유저 리스트
	public void chatUserList(String userList) {
		String users = userList;
		String user[] = users.split(",");
		model.clear();
		for (String nickName : user) {
			model.addElement(nickName);
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