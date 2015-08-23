package kr.or.kosta.koback.view.wait.waitingroom;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.model.Room;
import kr.or.kosta.koback.view.chatroom.ChatUI;
import kr.or.kosta.koback.view.login.UserLoginPanel;

/*
 * 	클래스 명 : WaitingChatRoomPanel 
 *  클래스 역할 : 대기화면(대기실을 그리기위한 패널객체)
 *  화면 구현 : 이광용 (2015 02 19 (오후 2시))
 *  
 *  
 *  추가 구현 : 조현빈
 * 	  추가사항 (2015-08-22)
 *    1.  chatRoomOpenC()  -수정
 *         
 * 	 
 * */
public class WaitingChatRoomPanel extends JPanel {

	// 각 객체
	private static final int CHAT_ITEM_BODER_X = 20;
	private static final int CHAT_ITEM_BODER_Y = 20;

	private int roomNum = 0; // 방번호
	private JLabel roomTitleL = new JLabel(); // 방 제목
	private JLabel roomMasterL = new JLabel(); // 방장
	private JLabel roomPersonelL = new JLabel(); // 방 인원수
	private JButton roomEntryBtn = new JButton("입장하기"); // 방 입장
	private JPanel roomItemP;
	ChatUI chatUI;
	WaitRoomListPanel waitRoomPanel;
	Room room;

	ImageIcon icon;

	public WaitingChatRoomPanel() {

	}

	public WaitingChatRoomPanel(Room room, ChatUI chatUI, WaitRoomListPanel waitRoomPanel) {
		this.chatUI = chatUI;
		this.waitRoomPanel = waitRoomPanel;
		this.room = room;
		roomNum = room.getRoomNum(); // 방번호
		// if(room.getPasswd())
		if (room.getPasswd().equals("없음")) {
			roomTitleL = new JLabel("NO." + roomNum + ".[일반방]" + room.getRoomTitle()); // 방
																						// 제목
		} else {
			roomTitleL = new JLabel("NO." + roomNum + ". [비밀방]" + room.getRoomTitle()); // 방
																						// 제목
		}

		roomMasterL = new JLabel("방장 : " + room.getMaster()); // 방장
		roomPersonelL = new JLabel("입장 제한 인원 : " + room.getMaxUserNum()); // 방
																			// 인원수
		roomEntryBtn = new JButton("입장하기"); // 방 입장

		icon = new ImageIcon("classes/images/backGroundColor.jpg");

	}

	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
	}

	public void chatRoomOpenC() throws IOException {
		waitRoomPanel.getRoomNum(roomNum);
		String connecionId = UserLoginPanel.userId;
		int roomNum = room.getRoomNum();
		String roomTitle = room.getRoomTitle();
		// JOptionPane.showMessageDialog(null,
		// erreorMessage,"연결실패",JOptionPane.ERROR_MESSAGE);

		/* [212] 비밀 방 입장을 요청. */
		if (!(room.getPasswd().equals("없음"))) {
			do {
				String pass = JOptionPane.showInputDialog("비밀번호를 입력 해주세요");

				if (pass == null || (pass != null && ("".equals(pass)))) {
					// 비였을때
					String erreorMessage = "비밀번호를 입력해주세요.";
					JOptionPane.showMessageDialog(null, erreorMessage, "연결실패", JOptionPane.ERROR_MESSAGE);
					break;
				} else {
					// 입력 되었을때
					/* [212] 비밀 방 입장 요청 212|아이디|방번호|비밀번호|방제목 */
					chatUI.getChatClient()
							.sendMessage(MessageType.C_SECRET_ENTRY + MessageType.DELIMETER + connecionId
									+ MessageType.DELIMETER + roomNum + MessageType.DELIMETER + pass
									+ MessageType.DELIMETER + roomTitle);
					System.out.println(
							MessageType.C_SECRET_ENTRY + MessageType.DELIMETER + connecionId + MessageType.DELIMETER
									+ roomNum + MessageType.DELIMETER + pass + MessageType.DELIMETER + roomTitle);
					break;

				}
			} while (true);

		} else {
			/* [210] 일반 방 입장 요청 210|아이디|방번호|방제목 */
			chatUI.getChatClient().sendMessage(MessageType.C_ENTRY + MessageType.DELIMETER + connecionId
					+ MessageType.DELIMETER + roomNum + MessageType.DELIMETER + roomTitle);
		}
	}

	/**
	 * 대기실 방객체
	 */
	public JPanel chatRoomItem() {
		roomItemP = new JPanel();

		GridBagConstraints gc = new GridBagConstraints();
		GridBagLayout gl = new GridBagLayout();

		roomItemP.setPreferredSize(new Dimension(220 + CHAT_ITEM_BODER_X, 150 + CHAT_ITEM_BODER_Y));
		roomItemP.setLayout(gl);
		gc.fill = GridBagConstraints.BOTH;
		gc.insets = new Insets(5, 10, 5, 10);

		addGrid(gl, gc, roomTitleL, 0, 0, 4, 1, 0.1, 0.2, roomItemP);
		addGrid(gl, gc, roomMasterL, 0, 1, 4, 1, 0.1, 0.3, roomItemP);
		addGrid(gl, gc, roomPersonelL, 0, 2, 4, 1, 0.1, 0.2, roomItemP);
		addGrid(gl, gc, roomEntryBtn, 0, 3, 6, 1, 0.7, 0.3, roomItemP);

		roomEntryBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					chatRoomOpenC();
				} catch (IOException e1) {
					System.out.println("[ERROR] WaitingChatRoomPanel 클래스 방입장");
				}

			}
		});

		return roomItemP;
	}

	/**
	 * 그리드 백 구성 메서드
	 */
	private void addGrid(GridBagLayout gbl, GridBagConstraints gbc, Component c, int gridx, int gridy, int gridwidth,
			int gridheight, double weightx, double weighty, JPanel jp) {
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.gridwidth = gridwidth;
		gbc.gridheight = gridheight;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbl.setConstraints(c, gbc);
		jp.add(c);
	}

	public int getRoomNum() {
		return roomNum;
	}

	public void setRoomNum(int roomNum) {
		this.roomNum = roomNum;
	}

	public JLabel getRoomTitleL() {
		return roomTitleL;
	}

	public void setRoomTitleL(JLabel roomTitleL) {
		this.roomTitleL = roomTitleL;
	}

	public JLabel getRoomMasterL() {
		return roomMasterL;
	}

	public void setRoomMasterL(JLabel roomMasterL) {
		this.roomMasterL = roomMasterL;
	}

	public JLabel getRoomPersonelL() {
		return roomPersonelL;
	}

	public void setRoomPersonelL(JLabel roomPersonelL) {
		this.roomPersonelL = roomPersonelL;
	}

	public JButton getRoomEntryBtn() {
		return roomEntryBtn;
	}

	public void setRoomEntryBtn(JButton roomEntryBtn) {
		this.roomEntryBtn = roomEntryBtn;
	}

	public ChatUI getChatUI() {
		return chatUI;
	}

	public void setChatUI(ChatUI chatUI) {
		this.chatUI = chatUI;
	}

	public static int getChatItemBoderX() {
		return CHAT_ITEM_BODER_X;
	}

	public static int getChatItemBoderY() {
		return CHAT_ITEM_BODER_Y;
	}

	public JPanel getRoomItemP() {
		return roomItemP;
	}

	public void setRoomItemP(JPanel roomItemP) {
		this.roomItemP = roomItemP;
	}

}
