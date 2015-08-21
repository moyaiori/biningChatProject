package kr.or.kosta.koback.view;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.util.GUIUtil;

public class UserRoomCreationFrame extends JFrame{
	
	private GridBagConstraints gridBagConstraints;
	private GridBagLayout gridBagLayout;
	
	private JButton btnRoomCreation;
	private JButton btnRoomCancel;
	private JTextField roomTitleJF;
	private JTextField roomPassJF;
	private JLabel roomPassJL;
	private JLabel roomTitleJL;
	private JLabel roomMemberJL;
	JRadioButton member2RB, member5RB,member10RB,member15RB;
	ChatUI chatUI;
	ButtonGroup memberG;
	
	
	public UserRoomCreationFrame(ChatUI chatUI) {
		setTitle("방 만들기");
		this.chatUI =chatUI;
		gridBagConstraints = new GridBagConstraints();
		gridBagLayout =new GridBagLayout();
		btnRoomCreation = new JButton("방만들기");
		btnRoomCancel = new JButton("취소");
		roomTitleJF = new JTextField();
		roomPassJF= new JTextField();
		roomPassJL = new JLabel("비밀번호");
		roomMemberJL = new JLabel("제한인원");
		roomTitleJL = new JLabel("방 제목");
		
		memberG = new ButtonGroup();	
		member2RB = new JRadioButton (" 2 명",true);
		member5RB = new JRadioButton (" 5 명");
		member10RB = new JRadioButton (" 10 명");
		member15RB = new JRadioButton (" 15 명");
		memberG.add(member2RB);
		memberG.add(member5RB);
		memberG.add(member10RB);
		memberG.add(member15RB);

		setComponents();
		eventRegist();
	}

	
	
	
	public void setComponents(){
		setLayout(gridBagLayout);
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(5,8,10,8);
		add(roomTitleJL,0,0,1,1,0,0);
		add(roomTitleJF,1,0,3,1,0,0);
		add(roomPassJL,0,1,1,1,0,0);
		add(roomPassJF,1,1,3,1,0,0);
		add(roomMemberJL,0,2,1,1,0,0);
		
		add(member2RB,0,3,1,1,0,0);
		add(member5RB,1,3,1,1,0,0);
		add(member10RB,2,3,1,1,0,0);
		add(member15RB,3,3,1,1,0,0);
		
		add(btnRoomCreation,1,4,1,1,0,0);
		add(btnRoomCancel,2,4,1,1,0,0);
		
	}
	
	
	/*gridBagLayout에 컴포넌트 추가 메소드*/
	private void add(Component component, int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty){
		gridBagConstraints.gridx = gridx;
		gridBagConstraints.gridy = gridy;
		gridBagConstraints.gridwidth  = gridwidth;
		gridBagConstraints.gridheight  = gridheight;
		gridBagConstraints.weightx = weightx;
		gridBagConstraints.weighty = weighty;
		gridBagLayout.setConstraints(component, gridBagConstraints);
		add(component);
	}

	public void eventRegist(){
		/*방만들기 버튼 클릭시 */
		btnRoomCreation.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						String roomPass = roomPassJF.getText().trim();
						roomKind(roomPass);
						
/*						String roomTitle = roomTitleJF.getText();
//						String roomPass = roomPassJF.getText().trim();
						String id = chatUI.getUserLoginPanel().userId;
						try {//102 - 회원가입 승인요청		
							chatUI.getChatClient().sendMessage(MessageType.C_OPEN+MessageType.DELIMETER+id+MessageType.DELIMETER
														+roomTitle+MessageType.DELIMETER+memberCheck());
						} catch (IOException e1) {
							System.out.println("[ERROR]- UserRoomCreationFrame클래스 eventRegist()");
						}*/
						
						exit();
					}
				});
		
		btnRoomCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		
	}
	public void roomKind(String pass){
		String roomTitle = roomTitleJF.getText();
		String id = chatUI.getUserLoginPanel().userId;
		/*[200] [202] 방 개설 용청*/
		try {//102 - 회원가입 승인요청		
			

			if(pass.length()==0){	//일반방 개설     200|id|방이름|제한인원
				System.out.println("[[200] 일반방 개설 ]"+MessageType.C_OPEN+MessageType.DELIMETER+id+MessageType.DELIMETER
						+roomTitle+MessageType.DELIMETER+memberCheck());
				chatUI.getChatClient().sendMessage(MessageType.C_OPEN+MessageType.DELIMETER+id+MessageType.DELIMETER
						+roomTitle+MessageType.DELIMETER+memberCheck());
			}else {	//비밀 방 개설.       202|id|방이름|제한인원|비밀번호
				System.out.println("[[200] 일반방 개설 ]"+MessageType.C_SECRET_OPEN+MessageType.DELIMETER+id+MessageType.DELIMETER
						+roomTitle+MessageType.DELIMETER+memberCheck()+MessageType.DELIMETER+pass);
				chatUI.getChatClient().sendMessage(MessageType.C_SECRET_OPEN+MessageType.DELIMETER+id+MessageType.DELIMETER
						+roomTitle+MessageType.DELIMETER+memberCheck()+MessageType.DELIMETER+pass);
			}

		} catch (IOException e1) {
			System.out.println("[ERROR]- UserRoomCreationFrame클래스 eventRegist()");
		}
		
		
		exit();
		
	}
	
	public String memberCheck(){
		String user=null;
		if(member2RB.isSelected()){user ="2";
		}else if(member5RB.isSelected()){user ="5";	
		}else if(member10RB.isSelected()){user ="10";
		}else if(member15RB.isSelected()){user ="15";
		}
		return user;
	}
	
	
	public void exit() {

		setVisible(false);
		dispose();
	}
	
}
