package kr.or.kosta.koback.view.wait.waitingroom;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.model.Room;
import kr.or.kosta.koback.util.GUIUtil;
import kr.or.kosta.koback.view.chatroom.ChatUI;
import kr.or.kosta.koback.view.chatroom.UserRoomCreationFrame;

/*
 * 	클래스 명 : WaitRoomPanel 
 *  클래스 역할 : 대기실 채팅방(대기실을 그리기위한 패널객체)
 *  화면 구현 : 이광용 (2015 02 19 (오후 2시))
 *  
 *  추가 구현 : 조현빈
 *  
 *    1.  roomResult()	:2015-08-20
 *         - 추가 1)방 개설
 * 	  2. roomList()		:2015-08-21
 * */
public class WaitRoomListPanel extends JPanel {

	private static final int CHAT_ITEM_BODER_X = 20;
	private static final int CHAT_ITEM_BODER_Y = 20;

	WaitRoomListPanel waitRoomListPanel;
	// --------------- 대기실 Center 컴포넌트--------------
	private JPanel centerP; // 중앙 패널(리스트와 상단 레이블)
	private JPanel roomP; // 리스트에 각각의 방
	private JLabel northL; // 상단에 "현재 개설되어있는 채팅방"

	// --------------- 대기실 Right 컴포넌트--------------
	private JPanel rightP; // 우측 패널(대기자 리스트, 방만들기, 로그아웃)
	private JList<String> waitingListL; // 채팅대기유저 리스트
	private DefaultListModel<String> listModel; // 우측 채팅방 대기실 인원 리스트 모델
	private JButton openRoomBtn; // 방 만들기 버튼
	private JButton logoutBtn; // 로그아웃 버튼
	private JLabel topL; // 상단 "채팅대기회원목록"
	private JPanel btnsetP; // 우측 하단의 버튼 패널
	private ChatUI chatUI;
	private ArrayList<JPanel> watingRoomList;

	public WaitRoomListPanel(ChatUI chatUI) {
		
		this.chatUI = chatUI;
		waitRoomListPanel = this;
		centerP = new JPanel();

		roomP = new JPanel();
		northL = new JLabel("현재 개설되어있는 채팅방");

		rightP = new JPanel();

		listModel = new DefaultListModel<String>();
		waitingListL = new JList<String>(listModel);

		openRoomBtn = new JButton("방만들기");
		logoutBtn = new JButton("로그아웃");
		topL = new JLabel("채팅 대기 회원 목록");
		btnsetP = new JPanel();
		watingRoomList = new ArrayList<JPanel>();

		setComponents();
		eventRegist();
	}
	/**
	 * 대기실 유저 리스트
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
		setCenterPanel();

	//	add(centerP, BorderLayout.CENTER);
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
		btnsetP.add(logoutBtn, BorderLayout.SOUTH);

		rightP.add(topL, BorderLayout.NORTH);
		rightP.add(new JScrollPane(waitingListL), BorderLayout.CENTER);
		rightP.add(btnsetP, BorderLayout.SOUTH);
	}

	/**
	 * 가운대 패널(상단 라벨, 채팅방 리스트)
	 */
	public void setCenterPanel() {
		// 상단에 글자 패널
		centerP.setLayout(new BorderLayout());
//		centerP.setLayout(new GridLayout(2,1));
		centerP.setBorder(BorderFactory.createLineBorder(Color.black));
//		JPanel topP = new JPanel();
//		topP.add(northL);
//		topP.setBorder(BorderFactory.createLineBorder(Color.black));
//		centerP.add(topP);
//		centerP.add(topP, BorderLayout.NORTH);
		System.out.println("how do you do");
		add(centerP);
//		add(centerP,BorderLayout.CENTER);
	}



	public void roomCreateAdd(Room room) {
		// 대기실 리스트
		ArrayList<Room> roomList = new ArrayList<Room>();
		roomList.add(room);
		roomCreate(roomList);
	}

	/**
	 * 이벤트 추가
	 */
	public void eventRegist() {

		openRoomBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				RoomCreationFrameOpen();

			}
		});
		/* 21일 - 추가 (조현빈) */
		logoutBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				chatUI.exit();

			}
		});
		/*-------------------------*/

	}

	public void RoomCreationFrameOpen() {//방만들기 버튼

		UserRoomCreationFrame frame = new UserRoomCreationFrame(chatUI);
		frame.setSize(350, 250);
		GUIUtil.setLookAndFeel(frame, GUIUtil.THEME_NIMBUS);
		GUIUtil.setCenterScreen(frame);
		frame.setVisible(true);
	}

	public WaitRoomListPanel getWaitRoomPanel() {
		return this;
	}

	public void setUi(WaitRoomListPanel waitRoomListPanel) {
		this.waitRoomListPanel = waitRoomListPanel;
	}
	
	public void getRoomNum(int roomNum){
		System.out.println(roomNum);
		
	}

	/*
	 * 룸 개설 true를 받을 경우
	 */
	public void roomResult(String roomType, String massage) {

		if (roomType.equals(MessageType.S_OPEN_RESULT)) {
			System.out.println("일반방 개설.");
			String responeMessage = massage;

			String[] tokens = responeMessage.split(MessageType.DELIMETER);
			String master = tokens[1];
			String passwd = "없음";
			String roomTitle = tokens[3];
			int roomNum = Integer.valueOf(tokens[2]);
			int maxUserNum = Integer.valueOf(tokens[4]);
			int nowUserNum = 1;

			roomCreateAdd(new Room(roomTitle, master, passwd, roomNum, maxUserNum, nowUserNum));
		}else if(roomType.equals(MessageType.S_SECRET_RESULT)){
			System.out.println("비밀방 개설.");
			String responeMessage = massage;

			String[] tokens = responeMessage.split(MessageType.DELIMETER);
			String master = tokens[1];
			String roomTitle = tokens[3];
//			String roomTitle = "비밀방>>"+tokens[3];
			int roomNum = Integer.valueOf(tokens[2]);
			int maxUserNum = Integer.valueOf(tokens[4]);
			String passwd = tokens[5];
			int nowUserNum = 1;

			roomCreateAdd(new Room(roomTitle, master, passwd, roomNum, maxUserNum, nowUserNum));
			
		}

	}
	/*---------------21일 - 추가 및 수정사항 (조현빈)------------*/
	/**
	 * 대기실에서의 전체 채팅방 목록
	 */
	public void roomList(String roomLists) { //roomLists =첫번째방,jo930408,1234,1,5,두번째방,비닝이,-1,2,10
		String[] room = roomLists.split(",");
		int messageSize = 5;

		ArrayList<Room> roomDataList = new ArrayList<Room>();
		
		for (int j = 0; j <room.length / messageSize; j++) { // 룸객체 갯수
			
			String roomTitle= null;
			String master=null;
			String passwd=null;
			int roomNum=0;
			int maxUserNum=0;
			
			
			roomTitle =room[0 + (messageSize * j)];
			master = room[1 + (messageSize * j)];
			passwd = room[2 + (messageSize * j)];
			
			roomNum = Integer.parseInt(room[3 + (messageSize * j)]);
			maxUserNum = Integer.parseInt(room[4 + (messageSize * j)]);

			if(!roomTitle.equals("대기실")){
				System.out.println("방제목 확인 : "+roomTitle);
				roomDataList.add(new Room(roomTitle, master, passwd, roomNum, maxUserNum));
			}
			
		}
		roomCreate(roomDataList);	//대기실에 채팅방 그리기위하여 호출.
	}

	public void roomCreate(ArrayList<Room> roomList) {

		// 대기실 패널
		JPanel roomListP = new JPanel();
		roomListP.setLayout(new WrapLayout());

		for (int i = 0; i < roomList.size(); i++) {
			WaitingChatRoomPanel roomPanel = new WaitingChatRoomPanel(roomList.get(i), chatUI, this);
		
			watingRoomList.add(roomPanel.chatRoomItem());
		}

		for (JPanel waitingRoom : watingRoomList) {
			waitingRoom.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
			roomListP.add(waitingRoom);
			
		}

		JScrollPane sp = new JScrollPane(roomListP, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		System.out.println("뭐하니");
		centerP.add(sp, BorderLayout.CENTER);
	}
	/*-----------------------------------------------------------------*/
	public JButton getLogoutBtn() {
		return logoutBtn;
	}
	public void setLogoutBtn(JButton logoutBtn) {
		this.logoutBtn = logoutBtn;
	}

}
