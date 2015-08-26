package kr.or.kosta.koback.view.chatroom;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import kr.or.kosta.koback.Client.ChatClient;
import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.view.login.UserJoinPanel;
import kr.or.kosta.koback.view.login.UserLoginPanel;
import kr.or.kosta.koback.view.wait.waitingroom.WaitingRoomTablePanel;

/*
 * 	클래스 명 : ChatUI 
 *  클래스 역할 :카드레이아웃을이용하여 프레임 구현 (로그인 - 대기실 - 채팅방)
 *  화면 구현 : 조현빈 (2015 02 19 (오후 2시))
 *  
 * */
public class ChatUI extends JFrame {
	
	private CardLayout cardLayout;
	private UserLoginPanel userLoginPanel;
	private ChatRoomPanel chatRoomPanel;
//	private WaitingChatRoomPanel waitRoomPanel;
//	private WaitRoomListPanel waitRoomListPanel;
	private WaitingRoomTablePanel waitingRoomTablePanel;
	

	private JPanel cardP;
	private ChatClient chatClient;
	
	private UserJoinPanel UserJoinPanel;
	
	
	
	private static final String serverIP = "192.168.0.41";	////192.168.0.28
//	private static final String serverIP = "192.168.88.1";	////192.168.0.28
	
	
//	private static final String serverIP = "121.134.239.82";	////안상오빠
//	private static final String serverIP = "192.168.0.54";	////성훈오빠
	
	
//	private static final String serverIP = "192.168.0.76";	////광용오빠
//	private static final String serverIP = "192.168.0.54";	////192.168.0.28
	private static final int serverPort = 5500;
//	private static final int serverPort = 5550;	//광용오빠
	
	/* 생성자 */
	public ChatUI(){
		this("KoBack채팅에 오신걸 환영합니다");
	}
	
	public ChatUI(String title){
		super(title); 
		cardLayout = new CardLayout();	
		
		userLoginPanel = new UserLoginPanel(this);
		chatRoomPanel = new ChatRoomPanel(this);
//		waitRoomListPanel = new WaitRoomListPanel(this);
		waitingRoomTablePanel = new WaitingRoomTablePanel(this);
		
		UserJoinPanel = new UserJoinPanel(this);
		
//		waitRoomPanel = new WaitingChatRoomPanel();
		cardP = new JPanel();
		
		setComponents();
		eventRegist();

	}
	
	
	/** 기능 - 화면 배치 **/
	public void setComponents(){	//프레임 디폴트 : 플로우레이아웃.센터
	
		cardP.setLayout(cardLayout);	//패널의 레이아웃을 카드레이아웃으로 변경.
		cardP.add(userLoginPanel, "login");
		cardP.add(chatRoomPanel, "chatRoom");
		cardP.add(waitingRoomTablePanel, "waitRoom");
		add(cardP,BorderLayout.CENTER); //카드레이아웃으로 되어있는 패널을 프레임에 추가.
	
	}
	

/* 이벤트 소스에 이벤트 핸들러 등록 */
	public void eventRegist(){
		
		/*첫 프로젝트 프레임 실행 될 때이벤트 처리  주석 : 0824 조현빈*/
		addWindowListener(
				new WindowAdapter() {	//이름없는 클래스 선언부

                    @Override
					public void windowClosing(WindowEvent e) {	//처리하고자 하는 이벤트 메소드.
							exit();
					};
					/* 추가 1)최초 서버와 연결*/
					public void windowOpened(WindowEvent e){
						chatClient = new ChatClient(serverPort,serverIP);
						chatClient.setUi(ChatUI.this);
						try {
							chatClient.connect();
							//최초 연결시 닉네임 전송.(100|*|아이디)
							getChatClient().receiveMessage();
							chatClient.sendMessage(MessageType.C_INITIAL_CONNECT+MessageType.DELIMETER+"connect");
						} catch (IOException ex) {
							String erreorMessage = "아래와 같은 예외가 발생하여 서버를 구동 할 수 없습니다.\r\n"+ex.toString();
							JOptionPane.showMessageDialog(null, erreorMessage,"연결실패",JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			);
		/*채팅방 나가기 버튼 이벤트 처리  주석 : 0824 조현빈*/
		chatRoomPanel.getOutB().addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					chatClient.sendMessage(MessageType.SC_EXIT+MessageType.DELIMETER +UserLoginPanel.userId+MessageType.DELIMETER+UserLoginPanel.userRoom);
					getChatRoomPanel().removeUser(UserLoginPanel.userId);
					UserLoginPanel.userRoom=0;
				} catch (IOException e1) {
				}

			}
		});

	}

	/*로그아웃, 프레임 창 닫을때 호출되는 exit() 메소드 
	 * 주석처리  : 0824 조현빈
	 * 구현 :조현빈
	 */
	public void exit(){	
		try {
			getChatClient().sendMessage(MessageType.C_LOGOUT+MessageType.DELIMETER+getUserLoginPanel().userId+MessageType.DELIMETER+userLoginPanel.userRoom);
			getChatClient().disConnect();
		} catch (IOException e) {
			System.out.println("[ERROR] -ChatUI클래스 exit() ");
		}
		dispose();
		setVisible(false);
		System.exit(0);
	}
	
	public void showCard(String cardId){
		cardLayout.show(cardP, cardId);
	}

	public CardLayout getCardLayout() {
		return cardLayout;
	}

	public void setCardLayout(CardLayout cardLayout) {
		this.cardLayout = cardLayout;
	}

	public UserLoginPanel getUserLoginPanel() {
		return userLoginPanel;
	}

	public void setUserLoginPanel(UserLoginPanel userLoginPanel) {
		this.userLoginPanel = userLoginPanel;
	}

	public ChatRoomPanel getChatRoomPanel() {
		return chatRoomPanel;
	}

	public void setChatRoomPanel(ChatRoomPanel chatRoomPanel) {
		this.chatRoomPanel = chatRoomPanel;
	}


	public JPanel getCardP() {
		return cardP;
	}

	public void setCardP(JPanel cardP) {
		this.cardP = cardP;
	}

	public ChatClient getChatClient() {
		return chatClient;
	}

	public void setChatClient(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	public static String getServerip() {
		return serverIP;
	}

	public static int getServerport() {
		return serverPort;
	}


	public UserJoinPanel getUserJoinPanel() {
		return UserJoinPanel;
	}

	public void setUserJoinPanel(UserJoinPanel userJoinPanel) {
		UserJoinPanel = userJoinPanel;
	}
	public WaitingRoomTablePanel getWaitingRoomTablePanel() {
		return waitingRoomTablePanel;
	}

	public void setWaitingRoomTablePanel(WaitingRoomTablePanel waitingRoomTablePanel) {
		this.waitingRoomTablePanel = waitingRoomTablePanel;
	}

}
