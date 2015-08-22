package kr.or.kosta.koback.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.model.Room;
import kr.or.kosta.koback.util.Validator;

/*
 * 	클래스 명 : WaitingChatRoomPanel 
 *  클래스 역할 : 대기화면(대기실을 그리기위한 패널객체)
 *  화면 구현 : 이광용 (2015 02 19 (오후 2시))
 *  
 *  
 *  추가 구현 : 조현빈
 *    추가사항 (2015-08-20)
 *    1.  chatRoomOpen()
 *         - 추가 1)채팅방 열기.
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

	public WaitingChatRoomPanel(Room room, ChatUI chatUI, WaitRoomListPanel waitRoomPanel) {
		this.chatUI = chatUI;
		this.waitRoomPanel = waitRoomPanel;
		this.room = room;
		roomNum = room.getRoomNum(); // 방번호
		// System.out.println("[ WaitingChatRoomPanel 클래스 방생성.]"+roomNum+"방번호");

		roomTitleL = new JLabel(roomNum + ". " + room.getRoomTitle()); // 방 제목
		roomMasterL = new JLabel("방장 : " + room.getMaster()); // 방장
		roomPersonelL = new JLabel("입장 제한 인원 : " + room.getMaxUserNum()); // 방
																			// 인원수
		roomEntryBtn = new JButton("입장하기"); // 방 입장

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
	}

	public void chatRoomOpenC() throws IOException {
		waitRoomPanel.getRoomNum(roomNum); // 이거지워도되는건지...??
		String connecionId = UserLoginPanel.userId;
		int roomNum = room.getRoomNum();
		// JOptionPane.showMessageDialog(null,
		// erreorMessage,"연결실패",JOptionPane.ERROR_MESSAGE);
		System.out.println("방번호" + room.getRoomNum() + "  " + room.getPasswd());
		/* [212] 비밀 방 입장을 요청. */
		// if(!(room.getPasswd().equals(null))){

		if (!(room.getPasswd().equals("없음"))) {
			String roomPass = JOptionPane.showInputDialog("방 비밀번호를 입력해주세요.");
			/* 212|아이디|방번호|비밀번호 */
			/*
			 * chatUI.getChatClient().sendMessage(MessageType.C_SECRET_ENTRY +
			 * MessageType.DELIMETER +
			 * connecionId+MessageType.DELIMETER+roomNum+MessageType.DELIMETER+
			 * roomPass);
			 */

		} else {
			/* [210] 일반 방 입장 요청 210|아이디|방번호|비밀번호 */
			chatUI.getChatClient().sendMessage(
					MessageType.C_SECRET_ENTRY + MessageType.DELIMETER + connecionId + MessageType.DELIMETER + roomNum);
		}

	}

	public void chatRoomOpenS(boolean roomOpenResult) {
		if (roomOpenResult) {// roomOpenResult: true이면 채팅방 열기
			/* 이 두줄이 화면을 열어주는 부분. */
			chatUI.getChatRoomPanel().titleL.setText(room.getRoomNum() + "번 방 " + room.getRoomTitle());
			chatUI.cardOpen("chatRoom");
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
