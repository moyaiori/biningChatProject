package kr.or.kosta.koback.view.wait.waitingroom;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.model.Room;
import kr.or.kosta.koback.util.GUIUtil;
import kr.or.kosta.koback.view.chatroom.ChatUI;
import kr.or.kosta.koback.view.chatroom.UserRoomCreationFrame;
import kr.or.kosta.koback.view.login.UserLoginPanel;

public class WaitingRoomTablePanel extends JPanel {

	public JPanel centerP; // 중앙 패널(리스트와 상단 레이블)

	// --------------- 대기실 Right 컴포넌트--------------
	private JPanel rightP; // 우측 패널(대기자 리스트, 방만들기, 로그아웃)
	private JList<String> waitingListL; // 채팅대기유저 리스트
	private DefaultListModel<String> listModel; // 우측 채팅방 대기실 인원 리스트 모델
	private JButton openRoomBtn; // 방 만들기 버튼
	private JButton logoutBtn; // 로그아웃 버튼
	private JLabel topL; // 상단 "채팅대기회원목록"
	private JButton joinBtn; // 참가 버튼

	private JPanel btnsetP; // 우측 하단의 버튼 패널

	private ChatUI chatUI;
	int callNum;

	WaitingRoomModel waitingRoomModel;
	JTable waitingRoomTable;
	
	int roomNum;
	String roomTitle;
	String roomPass;

	ArrayList<Room> roomArrayList;
	
	int selectRow;		// 테이블에서 선택한 가로 번호

	public WaitingRoomTablePanel(ChatUI chatUI) {
//		System.out.println("테이블 패널");
		this.chatUI = chatUI;

		centerP = new JPanel();

		rightP = new JPanel();
		listModel = new DefaultListModel<String>();
		waitingListL = new JList<String>(listModel);

		openRoomBtn = new JButton("방만들기");
		logoutBtn = new JButton("로그아웃");
		topL = new JLabel("채팅 대기 회원 목록");
		btnsetP = new JPanel();
		joinBtn = new JButton("참 가");
		waitingRoomModel = new WaitingRoomModel();
		waitingRoomTable = new JTable(waitingRoomModel);

		ArrayList<Room> roomArrayList = new ArrayList<Room>();
		
		setComponents();
		eventRegist();
		selectTableItem();
		// roomDraw();
	}

	/**
	 * 대기실 유저 리스트, 우측 리스트에 넣기
	 */
	public void waitingList(String waitingList) {
		String users = waitingList;
		String user[] = users.split(",");
		listModel.clear();
		for (String nickName : user) {
			listModel.addElement(nickName);
		}
	}

	/**
	 * 화면 배치
	 */
	public void setComponents() {
		setLayout(new BorderLayout());
		setRightPanel();
		centerP.add(new JScrollPane(waitingRoomTable));
		add(centerP, BorderLayout.CENTER);
		add(rightP, BorderLayout.EAST);
	}

	/**
	 * 우측 패널(대기실유저 리스트, 방만들기 버튼, 로그아웃 버튼
	 */
	public void setRightPanel() {
		rightP.setLayout(new BorderLayout());
		// 우측 패널 대기자 리스트 및 버튼
		btnsetP.setLayout(new BorderLayout());
		btnsetP.add(openRoomBtn, BorderLayout.NORTH);
		btnsetP.add(joinBtn, BorderLayout.CENTER);
		btnsetP.add(logoutBtn, BorderLayout.SOUTH);

		rightP.add(topL, BorderLayout.NORTH);
		rightP.add(new JScrollPane(waitingListL), BorderLayout.CENTER);
		rightP.add(btnsetP, BorderLayout.SOUTH);
	}

	public void RoomCreationFrameOpen() {// 방만들기 버튼
		UserRoomCreationFrame frame = new UserRoomCreationFrame(chatUI);
		frame.setSize(350, 250);
		GUIUtil.setLookAndFeel(frame, GUIUtil.THEME_NIMBUS);
		GUIUtil.setCenterScreen(frame);
		frame.setVisible(true);
	}

	public void addWaitingRoomList(List<Room> list) {
		waitingRoomModel.setWaitingRoomList(list);
	}

	// 대기실 메시지를 받아서 룸정보를 나눈후에 Room List로 바꾸어서 넣어준다.
	public List<Room> roomList(String roomList) {
		List<Room> roomArrayList = new ArrayList<Room>(); // 룸정보 가져오는 리스트
		String[] room = roomList.split(",");
		int messageSize = 5;
//		System.out.println("roomList" + roomList);

		for (int j = 1; j < room.length / messageSize; j++) { // 룸객체 갯수
			String roomTitle = room[0 + (messageSize * j)];
			String master = room[1 + (messageSize * j)];
			String passwd = room[2 + (messageSize * j)];
			int roomNum = Integer.parseInt(room[3 + (messageSize * j)]);
			int maxUserNum = Integer.parseInt(room[4 + (messageSize * j)]);
			roomArrayList.add(new Room(roomTitle, master, passwd, roomNum, maxUserNum));
		}

//		roomArrayList.add(new Room("방번호 1", "마스터 방장1", "", 0, 5, 2));
//		roomArrayList.add(new Room("방번호 2", "마스터 방장2", "비밀번호", 0, 10, 3));
//		roomArrayList.add(new Room("방번호 3", "마스터 방장3", "비밀번호", 0, 15, 4));
//		roomArrayList.add(new Room("방번호 4", "마스터 방장4", "비밀번호", 0, 20, 5));
//		roomArrayList.add(new Room("방번호 4", "마스터 방장4", "비밀번호", 0, 20, 5));
//		roomArrayList.add(new Room("방번호 4", "마스터 방장4", "비밀번호", 0, 20, 5));
//		roomArrayList.add(new Room("방번호 4", "마스터 방장4", "비밀번호", 0, 20, 5));

//		System.out.println("callNum++ : " + ++callNum);

		addWaitingRoomList(roomArrayList);
		return roomArrayList;
	}

	public void selectTableItem() {
		waitingRoomTable.setCellSelectionEnabled(true);
		ListSelectionModel cellSelectionModel = waitingRoomTable.getSelectionModel();
		cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		cellSelectionModel.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
//				System.out.println("벨류 체인지");
				String selectedData = null;

				int[] selectedRow = waitingRoomTable.getSelectedRows();
				int[] selectedColumns = waitingRoomTable.getSelectedColumns();

				for (int i = 0; i < selectedRow.length; i++) {
					for (int j = 0; j < selectedColumns.length; j++) {
//						System.out.println("i & 0 : " + waitingRoomTable.getValueAt(selectedRow[i], selectedColumns[0]));
						roomNum = waitingRoomModel.roomNum;
						roomTitle = waitingRoomModel.roomTitle;
						roomPass = (String)waitingRoomModel.password;
					}
				}
			}
		});
	}
	
	public void chatRoomOpenC() throws IOException {
		String connecionId = UserLoginPanel.userId;

		/* [212] 비밀 방 입장을 요청. */
		if (!(roomPass.equals("없음"))) {
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
									+ MessageType.DELIMETER + roomNum + MessageType.DELIMETER + roomPass
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
	 * 이벤트 추가
	 */
	public void eventRegist() {

		joinBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					System.out.println("참가");
					chatRoomOpenC();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
//				ui.getChatRoomPanel().chatRoomList(roomLists);
//				ui.getChatRoomPanel().chatRoomOpenS(true, roomTitle, roomNum);
			}
		});

		openRoomBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RoomCreationFrameOpen();
			}
		});

		logoutBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chatUI.exit();
			}
		});
	}

	public WaitingRoomTablePanel getWaitRoomPanel() {
		return this;
	}

}
