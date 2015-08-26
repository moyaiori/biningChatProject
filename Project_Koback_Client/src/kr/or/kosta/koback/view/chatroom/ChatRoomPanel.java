package kr.or.kosta.koback.view.chatroom;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.model.EmoticonButtons;
import kr.or.kosta.koback.util.GUIUtil;
import kr.or.kosta.koback.view.fileshare.UserFileFrame;
import kr.or.kosta.koback.view.login.UserLoginPanel;

/*
 *    클래스 명 : ChatRoomPanel 
 *  클래스 역할 : 채팅방 패널.
 *  화면 구현 : 가승호
 *  
 *  
 *  추가 구현 : 조현빈
 *  
 *  
 * */
public class ChatRoomPanel extends JPanel {

	JLabel headerL, choiceL, titleL;
	JList<String> userList;
	JTextPane chatTextTP;
	MyCellRenderer render;
	private JButton inviteB;
	private JButton sendB;
	private JButton emoticonB;
	private JButton outB;
	private JButton kickB;
	private JButton fileB;
	DefaultListModel<String> model;
	GridBagLayout gridBagLayout;
	GridBagConstraints gridBagConstraints;
	JComboBox<String> choiceCB;


	JTextField messageTF;
	ChatUI chatUI;
	UserFileFrame userFileFrame;

	StyledDocument doc;
	Style style;

	InviteBtnPanel invitePopUp;
	kickBtnPanel kickPopUp;

	StringBuilder sb;
	Image icon;

	EmoticonFrame emoticon = new EmoticonFrame(this);
	EmoticonButtons emoticonButtons;

	ArrayList<String> waitingList;
	
	DefaultComboBoxModel<String> choiceCBmodel;

	boolean toggle = false;
//	boolean master = false;
	String waitingListPerson; // 대기실 인원

	String roomNum; // 현재 켜진 방의 번호
	String masterID; // 메시지 보낸 사람의 ID
	String roomTitle; // 초대 보낸사람의 방 제목 %
	Font font;
	Image background;

	public static boolean  master = false;//%


	public ChatRoomPanel(ChatUI chatUI) {

		this.chatUI = chatUI;
		font = new Font("나눔고딕", Font.BOLD, 12);
		model = new DefaultListModel<String>();
		render = new MyCellRenderer();
		userList = new JList<String>(model);
		userList.setCellRenderer(render);
		headerL = new JLabel();
		userFileFrame = new UserFileFrame(chatUI);
		URL url = getClass().getResource("/images/TPcolor.png");
		
		background = new ImageIcon(url).getImage();
		chatTextTP = new JTextPane(){
			public void paintComponent(Graphics g) {
				setOpaque(false);
				setBackground(new Color(0, 0, 0, 0));
				g.drawImage(background, 0, 0, getWidth(), getHeight(),this);
				super.paintComponent(g);
				
			};
		};

		headerL = new JLabel("현재 접속자");
		headerL.setFont(font);

		inviteB = new JButton("초대");
		kickB = new JButton("강퇴");
		fileB = new JButton("파일공유함");
		outB = new JButton("나가기");
		emoticonB = new JButton("이모티콘");
		sendB = new JButton("전송");
		choiceL = new JLabel("메세지를 보내고자 하시는 분을 선택해주세요");
		choiceL.setFont(font);
		titleL = new JLabel("채팅방 내용");
		titleL.setFont(font);
		choiceCB = new JComboBox<String>();
		messageTF = new JTextField();

		choiceCB = new JComboBox<String>();
		choiceCBmodel = new DefaultComboBoxModel<String>();
		choiceCB.setModel(choiceCBmodel);
		chatTextTP.setEditable(false);
	

		doc = chatTextTP.getStyledDocument();
		style = chatTextTP.addStyle("style", null);

		sb = new StringBuilder();
		gridBagLayout = new GridBagLayout();
		gridBagConstraints = new GridBagConstraints();

		waitingList = new ArrayList<String>();
		inviteB.setEnabled(false);
		kickB.setEnabled(false);
		kickPopUp = new kickBtnPanel(chatUI);
		setComponents();
		eventRegist();
	}
	@Override
	protected void paintComponent(Graphics g) {
		URL bUrl = getClass().getResource("/images/backGroundColor.jpg");
//		icon = new ImageIcon(bUrl);
		icon = Toolkit.getDefaultToolkit().getImage(bUrl);
		g.drawImage(icon, 0, 0, getWidth(), getHeight(), this);
	}

	public void chatRoomList(String chatRoomList) {
		 // 방입장 사용자 정보
		 //닉네임|방장여부,닉네임|방장여부,닉네임|방장여부
		 String[] users = chatRoomList.split(",");
		 model.clear();
		 choiceCBmodel.removeAllElements();
		 choiceCBmodel.addElement("전  체");
		 for (String user : users) {
			 String[] attrs = user.split("\\|");
			 String userId = attrs[0];
			 String captain = attrs[1];
			 if(userId.equals(UserLoginPanel.userId)){
				 /*수정사항 0824 조현빈*/
				 if(new Boolean(captain)){
						inviteB.setEnabled(true);
						kickB.setEnabled(true);
				 }
			 }
			 // JList 설정
			 model.addElement(user);
			 // JCombobox 설정
			 if(!(UserLoginPanel.userId.equals(userId)))choiceCBmodel.addElement(userId);		
		 }
		 /*수정사항 0824 조현빈*/
		 kickPopUp.kickUserList(chatRoomList);
		 
	}

	/** 화면 배치 */
	public void setComponents() {

		setLayout(gridBagLayout);
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(2, 1, 2, 1);
		JScrollPane sp = new JScrollPane(userList);
		JScrollPane scrollPane = new JScrollPane(chatTextTP);

		
		add(titleL, 0, 0, 1, 1, 0, 0); // 채팅창 제목 라벨
		add(scrollPane, 0, 1, 4, 3, 1.5, 1.0); // 채팅창

		add(new JLabel(""), 4, 0, 1, 1, 0, 0);
		add(headerL, 5, 0, 1, 1, 0, 0); // 리스트 헤더
		add(sp, 5, 1, 3, 1, 0, 0.05); // 방 접속 리스트

		add(inviteB, 5, 2, 1, 1, 0.01, 0); // 초대버튼
		add(kickB, 6, 2, 1, 1, 0.01, 0); // 강퇴버튼
		add( fileB, 5, 3, 1, 1, 0, 0); // 파일공유함 버튼
		add(new JLabel(""), 7, 2, 1, 1, 0, 0); // 공백 라벨
		add(outB, 6, 3, 1, 1, 0, 0); // 나가기 버튼

		add(messageTF, 0, 5, 4, 1, 0, 0); // 글 입력 필드
		add(choiceCB, 0, 4, 1, 1, 0.01, 0); // 귓속말 등 선택 콤보박스
		add(choiceL, 1, 4, 2, 1, 0.05, 0); // 콤보박스 설명 라벨
		add(emoticonB, 5, 5, 1, 1, 0, 0); // 이모티콘 버튼
		add(sendB, 6, 5, 1, 1, 0, 0); // 전송 버튼
		

	}

	/* gridBagLayout에 컴포넌트 추가 메소드 */
	private void add(Component component, int gridx, int gridy, int gridwidth, int gridheight, double weightx,
			double weighty) {
		gridBagConstraints.gridx = gridx;
		gridBagConstraints.gridy = gridy;
		gridBagConstraints.gridwidth = gridwidth;
		gridBagConstraints.gridheight = gridheight;
		gridBagConstraints.weightx = weightx;
		gridBagConstraints.weighty = weighty;
		gridBagLayout.setConstraints(component, gridBagConstraints);
		add(component);
	}

	/** 채팅 메시지 송신 기능 */
	public void setChatMessage() {
		String message = messageTF.getText();
		if (message == null || message.trim().length() == 0) {
			return;
		}
		messageTF.setText("");

		String id = chatUI.getUserLoginPanel().userId;
		if (choiceCB.getSelectedItem().equals("전  체")) {
			try {
				chatUI.getChatClient().sendMessage(MessageType.SC_CHAT_MESSAGE + MessageType.DELIMETER + id
						+ MessageType.DELIMETER + UserLoginPanel.userRoom + MessageType.DELIMETER + message);
			} catch (IOException e) {
			}
		} else {
			try {
				chatUI.getChatClient()
						.sendMessage(MessageType.SC_WHISPER + MessageType.DELIMETER + id + MessageType.DELIMETER
								+ UserLoginPanel.userRoom + MessageType.DELIMETER + choiceCB.getSelectedItem()
								+ MessageType.DELIMETER + message);
			} catch (IOException e1) {
			}
		}
	}

	/** 일반 메시지 출력 */
	public void setMessage(String message) {

		try {
			Font tpFont = new Font("나눔고딕코딩", Font.PLAIN, 14);
			chatTextTP.setFont(tpFont);
			doc.insertString(doc.getLength(), message + "\r\n", style);
			setEndLine();
			setEndIcon();
		} catch (BadLocationException e) {
		}
	}

	/** 이모티콘 송신 기능 */
	public void setChatEmoticon(String filePath) {

		try {
			String id = chatUI.getUserLoginPanel().userId;
			chatUI.getChatClient().sendMessage(MessageType.SC_CHAT_EMOTICON + MessageType.DELIMETER + id
					+ MessageType.DELIMETER + UserLoginPanel.userRoom + MessageType.DELIMETER + filePath);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "이모티콘을 보내지 못하였습니다. 관리자 문의.", "네트워크 에러", JOptionPane.ERROR_MESSAGE);
		}
	}

	/** 화면에 이모티콘 출력 */
	public void setEmoticon(String filePath,String sendId) {
		Font tpFont = new Font("나눔고딕코딩", Font.PLAIN, 14);
		chatTextTP.setFont(tpFont);
		ImageIcon icon = new ImageIcon(filePath);
		setEndIcon();
		chatTextTP.insertComponent(new JLabel("["+sendId +"] :"));
		setEndLine();
		chatTextTP.insertIcon(icon);
		setEndIcon();
		try {
			doc.insertString(doc.getLength(), "\r\n", style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/** 이모티콘 출력시 다음줄에 출력하게 하는 메소드 */
	private void setEndIcon() {
		setEndLine();
		chatTextTP.replaceSelection("\n");
		setEndLine();
	}

	// 문장의 끝으로 이동하게 하기.
	private void setEndLine() {
		chatTextTP.selectAll();
		chatTextTP.setSelectionStart(chatTextTP.getSelectionEnd());
	}

	/** 공지사항 메시지 출력 */
	public void noticeMessage(String notice) {
		StyledDocument doc = chatTextTP.getStyledDocument();
		Style style = chatTextTP.addStyle("style", null);
		StyleConstants.setForeground(style, Color.RED);
		StyleConstants.setBold(style, true);
		StyleConstants.setFontSize(style, 15);

		try {
			doc.insertString(doc.getLength(), notice + "\r\n", style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

	/** 관리자의 귓속말 */
	public void noticeWhisperMessage(String noticeWhisper) {
		StyledDocument doc = chatTextTP.getStyledDocument();
		Style style = chatTextTP.addStyle("style", null);
		StyleConstants.setForeground(style, Color.pink);
		StyleConstants.setBold(style, true);

		try {
			doc.insertString(doc.getLength(), noticeWhisper + "\r\n", style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

	/** 채팅방 유저 리스트 출력 */
	public void chatUserList(String chatUserList) {
		String users = chatUserList;

		String user[] = users.split(",");
		model.clear();
		for (String nickName : user) {
			model.addElement(nickName);
		}
	}

	/** 사용자 선택 */
	public void selectUser() {
		String user = (String) userList.getSelectedValue();

	}

	public void removeUser(String chatUser) {

		for (int i = 0; i < model.size(); i++) {
			if (model.get(i).equals(chatUser)) {
				model.removeElementAt(i);
			}
		}

	}

	public String getRoomNum() {
		return roomNum;
	}

	public void setRoomNum(String roomNum) {
		this.roomNum = roomNum;
	}

	public String getMasterID() {
		return masterID;
	}

	public void setMasterID(String masterID) {
		this.masterID = masterID;
	}

	public void eventRegist() {
		 fileB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileFrameOpen();

			}
		});

		/** 전송 버튼 눌렀을 경우 채팅 메시지 전송되는 이벤트 */
		sendB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setChatMessage();
			}
		});

		/** 전송버튼을 누르지 않고 엔터치면 메시지 전송 */
		messageTF.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setChatMessage();
			}
		});

		/** 이모티콘 버튼을 누르면 이모티콘 프레임 활성화 */

		emoticonB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (toggle) {
					toggle = false;
					emoticon.setVisible(false);
					emoticon.dispose();
				} else {
					toggle = true;
					EmoticonOpen(emoticon);
				}

			}
		});

		/** 강퇴 버튼을 누르면 강퇴확인 다이얼로그 출력 예시 */

		kickB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				KickFramOpen();
			}
		});

		/** 초대 버튼을 누르면 나오는 대기실 유저 리스트 출력 이벤트 */
		inviteB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				InviteFrameMsgSend();
			}
		});

	}

	/**
	 * 채팅창 내 버튼을 누르면 화면에 보여지는 프레임들을 불러오는 메소드들
	 */

	public void KickFramOpen() {
		
		GUIUtil.setLookAndFeel(kickPopUp, GUIUtil.THEME_NIMBUS);
		GUIUtil.setCenterScreen(kickPopUp);
		kickPopUp.setSize(300, 300);
		kickPopUp.setVisible(true);
	}

	// 대기실 리스트 요청
	public void InviteFrameMsgSend() {
		String id = chatUI.getUserLoginPanel().userId;
		try {
			chatUI.getChatClient().sendMessage(MessageType.SC_REQUEST_ROOM_INFO + MessageType.DELIMETER + id);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 방장이 초대 하고자하는사람 화면
	public void InviteFrameOpen(String waitList, String roomNum, String roomTitle) {
		invitePopUp = new InviteBtnPanel(chatUI, waitList, roomNum, roomTitle);
		GUIUtil.setLookAndFeel(invitePopUp, GUIUtil.THEME_NIMBUS);
		GUIUtil.setCenterScreen(invitePopUp);
		invitePopUp.setSize(300, 300);
		invitePopUp.setVisible(true);
	}

	// 초대받은사람이 수락, 거절 프레임
	public void showInvitePan() {
		String id = chatUI.getUserLoginPanel().userId;
		InvitePanel invitePanel = new InvitePanel(id, roomNum, chatUI, masterID, roomTitle);
		GUIUtil.setCenterScreen(invitePanel);
		invitePanel.setSize(200, 170);
		invitePanel.setVisible(true);
	}
	/* 초대를 수락한 경우 */
	public void acceptInvite(String inviteUser) {
		String message = inviteUser + "님께서 초대를 수락하셧습니다.";
		JOptionPane.showMessageDialog(null, message);
	}

	/* 초대를 거절한 경우 */
	public void refusalInvite(String inviteUser) {
		String message = inviteUser + "님께서 초대를 거절하셧습니다.";
		JOptionPane.showMessageDialog(null, message);
	}

	// 대기실 인원 0 일경우
	public void InviteFrameisEmpty() {
		String erreorMessage = "초대할수있는 사람이 없습니다.\r\n";
		JOptionPane.showMessageDialog(null, erreorMessage, "초대 실패", JOptionPane.ERROR_MESSAGE);
	}

	public void FileFrameOpen() {
		try {
			chatUI.getChatClient().sendMessage(MessageType.C_SHOW_FILE_LIST+ MessageType.DELIMETER+UserLoginPanel.userId);
		} catch (IOException e) {
//			e.printStackTrace();
		}
		GUIUtil.setLookAndFeel(userFileFrame, GUIUtil.THEME_NIMBUS);
		userFileFrame.setSize(600, 400);
		GUIUtil.setCenterScreen(userFileFrame);

		userFileFrame.setVisible(true);
	}

	/** 이모티콘 프레임을 열기 위한 메소드 */
	public void EmoticonOpen(EmoticonFrame frame) {
		GUIUtil.setLookAndFeel(frame, GUIUtil.THEME_NIMBUS);
		int width = frame.getWidth();
		int[] location = frame.emoticonLocation(width);
		frame.setLocation(location[0], location[1]);
		frame.setSize(200, 300);
		frame.setVisible(true);
	}

	public void chatRoomOpenS(boolean roomOpenResult, String roomTitle, String roomNum) {

		chatUI.getChatRoomPanel().titleL.setText(roomNum + "번 방 >> " + roomTitle);
		chatUI.showCard("chatRoom");

	}

	public void chatRoomOpenS(boolean roomOpenResult, String message) {

		String erreorMessage = message;
		JOptionPane.showMessageDialog(null, erreorMessage, "입장실패", JOptionPane.ERROR_MESSAGE);

	}

	public ChatUI getChatUI() {
		return chatUI;
	}

	public void setChatUI(ChatUI chatUI) {
		this.chatUI = chatUI;
	}
	
	public void setWaitingList(ArrayList<String> waitingList) {
		this.waitingList = waitingList;
	}
	
	public ArrayList<String> getWaitingList() {
		return waitingList;
	}

	public String getWaitingListPerson() {
		return waitingListPerson;
	}

	public void setWaitingListPerson(String waitingListPerson) {
		this.waitingListPerson = waitingListPerson;
	}
	public JButton getOutB() {
		return outB;
	}

	public void setOutB(JButton outB) {
		this.outB = outB;
	}


	
	public JButton getInviteB() {
		return inviteB;
	}

	public void setInviteB(JButton inviteB) {
		this.inviteB = inviteB;
	}

	public JButton getKickB() {
		return kickB;
	}

	public void setKickB(JButton kickB) {
		this.kickB = kickB;
	}

	public JButton getSendB() {
		return sendB;
	}

	public void setSendB(JButton sendB) {
		this.sendB = sendB;
	}

	public JButton getEmoticonB() {
		return emoticonB;
	}

	public void setEmoticonB(JButton emoticonB) {
		this.emoticonB = emoticonB;
	}

	public UserFileFrame getUserFileFrame() {
		return userFileFrame;
	}

	public void setUserFileFrame(UserFileFrame userFileFrame) {
		this.userFileFrame = userFileFrame;
	}
	
	
}
