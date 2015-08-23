package kr.or.kosta.koback.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.model.FileDao;
import kr.or.kosta.koback.util.GUIUtil;
import kr.or.kosta.koback.view.chatroom.ChatRoomPanel;
import kr.or.kosta.koback.view.chatroom.ChatUI;
import kr.or.kosta.koback.view.login.UserLoginPanel;

public class ChatClient {

	private boolean stop;
	private String serverIp;
	private int port;
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	private ChatUI ui;
	// private ChatClient chatClient;
	private FileDao dao;
	private ReceiveMessageMethod receiveMessageMethod; // 08 22 추가 (조현빈)
	JPanel screen;

	public ChatClient() {

	}

	public ChatClient(int port, String ip) {
		this.port = port;
		this.serverIp = ip;
	}

	// 클라이언트 구동
	public void connect() throws IOException {
		socket = new Socket(serverIp, port);
		out = new DataOutputStream(socket.getOutputStream());
		in = new DataInputStream(socket.getInputStream());
		receiveMessageMethod = new ReceiveMessageMethod(ui); // 08 22 추가 (조현빈)
	}

	// 메세지 수신 ----아이디가 1번
	public void receiveMessage() throws IOException {
		new Thread() {
			@Override
			public void run() {
				try {

					while (!isStop()) {
						String responeMessage = null;
						while (responeMessage == null || "".equals(responeMessage)) {
							responeMessage = in.readUTF();
						}

						String[] tokens = responeMessage.split(MessageType.DELIMETER);
						String messageCode = tokens[0];

						/*------------------switch - case 문 시작 부분 --------------------------*/
						switch (messageCode) {

						/* [101] 로그인결과응답 */
						case MessageType.S_LOGIN_RESULT:
							System.out.println("[서버 답장 로그인결과응답 101] : " + responeMessage);
							String connectionId = tokens[1]; // 로그인 요청 한 클라이언트의
																// 닉네임
							String waitingList = tokens[3]; // 대기실에 접속 되어있는
															// 클라이언트 목록.
							String chatRoomLists = tokens[4];

							receiveMessageMethod.sLoignResult(connectionId, waitingList, chatRoomLists);
							break;

						/* [103] 회원가입 응답 결과 */
						case MessageType.S_JOIN_RESULT: // 103 회원가입응답

							break;

						/* [201] 채팅방 개설 결과 응답 201|jo930408|방번호|제목|true */
						case MessageType.S_OPEN_RESULT:
							// System.out.println("[[201] 채팅방 개설 결과 응답] "
							// +responeMessage);
							if (resultResult("true")) {
								ui.getWaitRoomPanel().roomResult(MessageType.S_OPEN_RESULT, responeMessage);
								UserLoginPanel.userRoom = Integer.valueOf(tokens[2]);
								ui.getChatRoomPanel().chatRoomList(tokens[1]);
								ui.getChatRoomPanel().chatRoomOpenS(true, tokens[3], tokens[2], UserLoginPanel.userId);
								// openChatRoom();
							}
							break;

						/* [203] 비밀방 개설 응답203|jo930408|방번호|제목|인원|true */
						case MessageType.S_SECRET_RESULT:
							//
							// System.out.println("[서버 답장 채팅방 개설203 비밀방] " +
							// responeMessage);
							if (resultResult("true")) {

								UserLoginPanel.userRoom = Integer.valueOf(tokens[2]);
								ui.getChatRoomPanel().chatRoomList(tokens[1]);
								// ui.getChatRoomPanel().chatRoomOpenS(true,tokens[3],UserLoginPanel.userRoom,tokens[5]);
								ui.getChatRoomPanel().chatRoomOpenS(true, tokens[3], tokens[2], UserLoginPanel.userId);
								// openChatRoom();
							}
							break;

						/* [211] 채팅방 입장 결과 응답 */
						case MessageType.S_ENTRY_RESULT:
							boolean chatRoomResult = Boolean.valueOf(tokens[2]);
							String roomTitle = tokens[3];

							// System.out.println("[[211] 채팅방 입장 결과
							// 응답]"+responeMessage);
							if (chatRoomResult) {
								String roomNum = tokens[4];
								UserLoginPanel.userRoom = Integer.valueOf(roomNum);
								ui.getChatRoomPanel().chatRoomOpenS(chatRoomResult, roomTitle, roomNum, tokens[5]);

							} else {
								String messgae = tokens[3];
								ui.getChatRoomPanel().chatRoomOpenS(chatRoomResult, messgae);
							}

							break;
						/* [213] 비밀방 입장 결과 응답 */
						case MessageType.S_SECRET_ENTRY_RESULT:
							boolean chatSecretRoomResult = Boolean.valueOf(tokens[2]);
							String secretRoomTitle = tokens[3];

							// System.out.println("[[213] 비밀방 입장 결과
							// 응답]"+responeMessage);
							if (chatSecretRoomResult) {
								String roomNum = tokens[4];
								UserLoginPanel.userRoom = Integer.valueOf(roomNum);
								ui.getChatRoomPanel().chatRoomOpenS(chatSecretRoomResult, secretRoomTitle, roomNum,
										tokens[5]);
							} else {
								String messgae = tokens[3];
								ui.getChatRoomPanel().chatRoomOpenS(chatSecretRoomResult, messgae);
							}
							break;

						/* [300]채팅메세지 수신 결과 응답 */
						case MessageType.SC_CHAT_MESSAGE:
							// System.out.println("[[300]채팅메세지 수신 결과 응답]" +
							// responeMessage);
							String messageId = tokens[1];
							String chatMessage = tokens[3];
							System.out.println(responeMessage);
							ui.getChatRoomPanel().setMessage("[" + messageId + "] : " + chatMessage);
							break;

						/* [301] 이모티콘 송수신 응답 */
						case MessageType.SC_CHAT_EMOTICON: //
							String Id = tokens[1];
							String filePath = tokens[3];
							// System.out.println(Id + filePath);
							ui.getChatRoomPanel().setEmoticon(filePath);
							break;

						/* [302] 귓속말 송수신 */
						case MessageType.SC_WHISPER:
							System.out.println("[[302] 귓속말 송수신 ]" + responeMessage);
							String id = tokens[1];
							String whisperMessage = tokens[4];
							System.out.println(responeMessage);
							ui.getChatRoomPanel().setMessage("[" + id + "] : " + whisperMessage);
							break;

						/* [304] 초대 응답 */
						case MessageType.S_INVITE_RESULT:
							ui.getChatRoomPanel().setMasterID(tokens[1]);
							ui.getChatRoomPanel().setRoomNum(tokens[2]);
							ui.getChatRoomPanel().showInvitePan();
							break;
							/* [305] 초대에 대한 응답 */
						case MessageType.C_INVITE_CONFIRM:
							String inviteUser = tokens[1];
							if (tokens[3] == "true") {
								ui.getChatRoomPanel().acceptInvite(inviteUser);
							}else{
								ui.getChatRoomPanel().acceptInvite(inviteUser);
							}
							break;
						/* [308] 아이디|비밀번호|개설된 방정보 목록|대기자 목록 */
						case MessageType.SC_EXIT:
							// System.out.println("[308 받은 메세지]" +
							// responeMessage);
							if (tokens.length <= 3) {
								receiveMessageMethod.scExit(UserLoginPanel.userId, tokens[3], tokens[4]);
							} else {
								if (!(tokens.length <= 3)) {
									receiveMessageMethod.scExit(UserLoginPanel.userId, tokens[3], tokens[4]);
								}
								receiveMessageMethod.scExit(UserLoginPanel.userId, tokens[3], null);
							}
							break;
						/* [309] 대기인 인원 요청 */
						case MessageType.SC_REQUEST_WAITING_LIST:
							System.out.println("[서버에게 받은 메세지]" + responeMessage);
							if (tokens.length <= 2) {
								// System.out.println("대기실 인원 없음");
								ui.getChatRoomPanel().InviteFrameisEmpty();
							} else {
								ui.getChatRoomPanel().InviteFrameOpen(tokens[2]);
							}
							break;

						/* [311] 대기자 목록 */
						case MessageType.S_REQUEST_WAITING_LIST:

							// System.out.println("[서버에게 받은 메세지]" +
							// responeMessage);
							if (tokens.length <= 2) {
								System.out.println("[311] 대기자 목록 대기실 인원 없음");
							} else {
								String waitingLists = tokens[2];
								receiveMessageMethod.sRequestWaitingList(waitingLists);
							}
							break;

						/* [312] 개설된 방정보 목록 */
						case MessageType.S_REQUEST_WAITING_ROOMLIST:
							// System.out.println("[서버에게 받은 메세지]" +
							// responeMessage);
							if (tokens.length < 2) {
								String waitingRoomLists = tokens[2];
								receiveMessageMethod.sRequestWaitingRoomList(waitingRoomLists);
							}
							break;

						/* [313] 채팅방 참여자 정보를 보내줌 - user가 나갈때 또는 들어올때 */
						case MessageType.S_SELECTED_ROOM_USERLIST:
							// System.out.println("[서버 [313] 채팅방 참여자 리스트]" +
							// responeMessage);
							String chatUserLists = tokens[2];
							ui.getChatRoomPanel().chatRoomList(chatUserLists);
							break;

						/* [403] 파일 업로드 결과 응답 */
						case MessageType.C_UPLOAD_RESULT: // 파일 업로드 결과
															// EX)[protocol][TRUE]
							System.out.println("[서버에게 받은 메세지]" + responeMessage);
							int fileServerPort = Integer.valueOf(tokens[4]);
							String fileServerIp = tokens[5];
							fileServerConnect(fileServerPort, fileServerIp);
							break;

						/* [500] 서버 공지 수신 결과 응답 */
						case MessageType.S_NOTICE:
							System.out.println(responeMessage);
							String admin = tokens[1];
							String adminMessage = tokens[2].trim();
							ui.getChatRoomPanel().noticeMessage("[" + admin + "] : " + adminMessage);
							break;

						/* [501] 관리자의 귓속말 결과 응답 */
						case MessageType.S_ADMIN_WHISPER: //
							System.out.println(responeMessage);
							String adminW = tokens[1];
							String adminWhiseper = tokens[2].trim();
							ui.getChatRoomPanel().noticeWhisperMessage("[" + adminW + "]님의 귓속말 : " + adminWhiseper);
							break;

						}
					}
				} catch (IOException e) {
					System.out.println("서버가 종료되었습니다.");
				}
			}
		}.start();

	}

	public void openChatRoom() {
		ui.setSize(1000, 600);
		GUIUtil.setCenterScreen(ui);

		ui.cardOpen("chatRoom");
	}

	public void fileServerConnect(int fileServerPort, String fileServerIp) {
		serverIp = fileServerIp;
		port = fileServerPort;
		try {
			connect();
			fileDownload();

		} catch (IOException e) {
			String erreorMessage = "아래와 같은 예외가 발생하여 서버를 구동 할 수 없습니다.\r\n" + e.toString();
			JOptionPane.showMessageDialog(null, erreorMessage, "연결실패", JOptionPane.ERROR_MESSAGE);
		}
	}

	public boolean resultResult(String result) {
		if (result.equals("true"))
			return true;
		return false;
	}

	public void fileDownload() throws IOException {
		File file = new File(dao.getFilePath(), dao.getFile().getName()); // 파일
																			// 객체만들기.
		long fileSize = file.length();
		// 다운로드 받고자 하는 파일의 용량 출력 -다운로드 창을 이용하기위함.
		out.writeLong(fileSize);

		/*
		 * FileOutputStream fos = null; fos = new FileOutputStream(file);
		 */
		byte[] buffer = new byte[1024];
		int count = 0;
		int copySize = 0;
		System.out.println("testt");
		System.out.println("testt--" + in.read(buffer));

		while ((count = in.read(buffer)) != -1) {
			// fos.write(buffer, 0, count);
			out.write(buffer, 0, count);
			copySize += count;
			System.out.println("copySize---" + copySize);
			if (copySize == fileSize) {
				System.out.println("완료");
			}
		}
	}

	public void sendMessage(String message) throws IOException { // chat
		out.writeUTF(message);
	}

	// 클라이언트 종료
	public void disConnect() throws IOException {
		if (socket != null)
			socket.close();
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public DataInputStream getIn() {
		return in;
	}

	public void setIn(DataInputStream in) {
		this.in = in;
	}

	public DataOutputStream getOut() {
		return out;
	}

	public void setOut(DataOutputStream out) {
		this.out = out;
	}

	public ChatUI getUi() {
		return ui;
	}

	public void setUi(ChatUI ui) {
		this.ui = ui;
	}

}
