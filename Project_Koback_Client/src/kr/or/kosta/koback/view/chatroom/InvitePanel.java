package kr.or.kosta.koback.view.chatroom;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import kr.or.kosta.koback.common.MessageType;

public class InvitePanel extends JFrame {
	JButton okB,cancelB;
	JLabel fromL, toL, inviteL;
//	JTextField fromTF, toTF;
	
	GridBagLayout gridBagLayout;
	GridBagConstraints gridBagConstraints;
	
	ChatUI chatUI;
	String masterID;
	String roomNum;
	String roomTitle;
	

	public InvitePanel(String inviteUser, String roomNum, ChatUI chatUI, String masterID, String roomTitle){
		super("채팅방 초대");
		this.chatUI = chatUI;
		this.masterID = masterID;
//		URL url = getClass().getResource("/images/popupColor.jpg");
//		this.setContentPane(new JLabel(new ImageIcon(url)));
		this.roomNum = roomNum;
		this.roomTitle = roomTitle;
		
		okB = new JButton("수락");
		cancelB = new JButton("거절");
		
	    fromL = new JLabel(inviteUser + " 님께서");
	    toL =  new JLabel(roomNum + ". " +  roomTitle + " 방에서");
//	    toL =  new JLabel(roomNum + ".  방에서");
	    inviteL = new JLabel("초대하셨습니다");
	    
	    gridBagLayout= new GridBagLayout();
	    gridBagConstraints = new GridBagConstraints();
	    setComponents();
	}
	
	 public void setComponents(){
	      
	      setLayout(gridBagLayout); // 패널의 레이아웃을 GridBagLayout으로 설정
	      gridBagConstraints.fill = GridBagConstraints.BOTH;
	      gridBagConstraints.insets = new Insets(3, 5, 3, 5);
	      
	      
	      addComponent(new JLabel(" "),   	0, 0, 1, 1, 0.5, 0.0);
	      addComponent(fromL, 				2, 1, 1, 1, 0.0, 0.0);
	      addComponent(toL, 				2, 2, 1, 1, 0.0, 0.0);
	      addComponent(inviteL, 			2, 3, 1, 1, 0.0, 0.0);
	      addComponent(okB, 				2, 4, 1, 1, 0.1, 0.0);
	      addComponent(cancelB, 			3, 4, 1, 1, 0.8, 0.0);
	      addComponent(new JLabel(" "),   	4, 0, 1, 1, 0.5, 0.0);
	    
	      // 수락
	      okB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String id = chatUI.getUserLoginPanel().userId;
				
				try {
					chatUI.getChatClient().sendMessage(MessageType.C_INVITE_RESULT + MessageType.DELIMETER + id + MessageType.DELIMETER + masterID + MessageType.DELIMETER  + "true");
					chatUI.getChatClient().sendMessage(MessageType.C_INVITE_ENTRY + MessageType.DELIMETER + id + MessageType.DELIMETER + roomNum + MessageType.DELIMETER + roomTitle);
//					chatUI.getChatRoomPanel().chatRoomOpenS(true, id, id, lists);
					// 여기에 입장하기 구현
					exit();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	      
	      // 거절
	      cancelB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String id = chatUI.getUserLoginPanel().userId;
				try {
					chatUI.getChatClient().sendMessage(MessageType.S_INVITE_RESULT + MessageType.DELIMETER + id + MessageType.DELIMETER + masterID + MessageType.DELIMETER  + "false");
					exit();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	            
	   }
	 public void exit(){	
			dispose();
			setVisible(false);
		}
	   
	   private void addComponent(Component com, int gridx, int gridy, int gridWidth, int gridHeight, double weightx,
	         double weighty) {
	      gridBagConstraints.gridx = gridx;
	      gridBagConstraints.gridy = gridy;
	      gridBagConstraints.gridwidth = gridWidth;
	      gridBagConstraints.gridheight = gridHeight;
	      gridBagConstraints.weightx = weightx;
	      gridBagConstraints.weighty = weighty;
	      
	      gridBagLayout.setConstraints(com, gridBagConstraints);
	      
	      add(com); // 컴포넌트를 Panel에 부착한다.
	   }
	}


