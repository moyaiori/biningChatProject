package kr.or.kosta.koback.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
/*
 * 2015 02 19 (오후 2시)
 * 틀 구현 : 조현빈
 * */

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.model.FileDao;
import kr.or.kosta.koback.util.GUIUtil;
import kr.or.kosta.koback.view.ChatUI;
import kr.or.kosta.koback.view.WaitRoomPanel;

public class ChatClient {

	private boolean stop;
	private String serverIp;
	private int port;
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	private ChatUI ui;
	private ChatClient chatClient;
	private FileDao dao;
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
		out = new DataOutputStream(socket.getOutputStream()); // 예외처리는 throws로
																// 서버에서 관리
																// 하도록한다.
		in = new DataInputStream(socket.getInputStream());

	}

	// 메세지 수신
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
						/* [403] 파일 업로드 결과 응답 */
						case MessageType.C_UPLOAD_RESULT: // 파일 업로드 결과
															// EX)[protocol][TRUE]
							System.out.println("[서버에게 받은 메세지]" + responeMessage);
							int fileServerPort = Integer.valueOf(tokens[4]);
							String fileServerIp = tokens[5];

							fileServerConnect(fileServerPort, fileServerIp);

							break;

						/* [103] 회원가입 응답 결과 */
						case MessageType.S_JOIN_RESULT: // 103 회원가입응답
							// System.out.println(responeMessage);

							break;

						/* [201] 채팅방 개설 결과 응답 */
						case MessageType.S_OPEN_RESULT:
							// System.out.println("[서버 답장 채팅방 개설 201]
							// "+responeMessage);
							if (resultResult("true")) {
								ui.getWaitRoomPanel().roomResult(MessageType.S_OPEN_RESULT, responeMessage);
								openChatRoom();
							}
							break;

						/* [203] 비밀방 개설 응답 */
						case MessageType.S_SECRET_RESULT:
							System.out.println("[서버 답장 채팅방 개설203 비밀방] " + responeMessage);
							System.out.println("ㅌ스트");
							if (resultResult("true")) {
								// ui.getWaitRoomPanel().roomResult(MessageType.S_OPEN_RESULT,responeMessage);
								openChatRoom();
							}
							break;
						/**
						 * 입장 결과 쪽 211 , 213 할것 1) WaitingChatRoomPanel 클래스를
						 * 접근하여 chatRoomOpenS()메소드 호출 2) chatRoomOpenS()에서
						 */

						/* [211] 채팅방 입장 결과 응답 */
						case MessageType.S_ENTRY_RESULT:
							 String chatRoomResult;
							 System.out.println("[[211] 채팅방 입장 결과 응답]"+responeMessage);
							 openChatRoom();
							// ui.getWaitRoomPanel().chtRoomOpenS(결과응답값); //결과
							// 응답이 true이면 채팅화면을 열기위해 이메소드를 호출!!!
							break;
						/* [213] 비밀방 입장 결과 응답 */
						case MessageType.S_SECRET_ENTRY_RESULT:
							// System.out.printl n("[[213] 비밀방 입장 결과 응답]
							// "+responeMessage);
							// sys
							break;
						/* [101] 로그인결과응답 */
						case MessageType.S_LOGIN_RESULT:

							String connectionId = tokens[2]; // 로그인 요청 한 클라이언트의
																// 닉네임
							String waitingList = tokens[3]; // 대기실에 접속 되어있는
															// 클라이언트 목록.
							String chatRoomLists = tokens[4];
							System.out.println("[서버 답장 로그인결과응답 101] : " + responeMessage);

							ui.getWaitRoomPanel().waitingList(waitingList); // 대기방
																			// 리스트에
																			// 출력하기위해
																			// 호출.

							if (ui.getUserLoginPanel().userId.equals(connectionId)) { // 자기
																						// 자신
																						// 이외의
																						// 클라이언트가
																						// 접속
																						// 할경우
								if (!chatRoomLists.equals(null)) {
									// String roomLists =
									// "첫번째방,jo930408,1234,1,5,다들어와,비닝이,null,2,10,놀쟈,비닝이,null,2,10";
									// //tokens[4]
									String roomLists = chatRoomLists;
									System.out.println(roomLists);
									ui.getWaitRoomPanel().roomList(roomLists);
									ui.cardOpen("waitRoom");
								}
							}
							break;

						/* [300]채팅메세지 수신 결과 응답 */
						case MessageType.SC_CHAT_MESSAGE:
							String messageId = tokens[1];
							String chatMessage = tokens[3];
							ui.getChatRoomPanel().setMessage("[" + messageId + "] : " + chatMessage);
							break;

						/* [301] 이모티콘 송수신 응답 */
						case MessageType.SC_CHAT_EMOTICON: //
							String Id = tokens[1];
							String filePath = tokens[3];
							System.out.println(Id + filePath);
							ui.getChatRoomPanel().setEmoticon("[" + Id + "] : " + filePath);
							break;

						case MessageType.S_NOTICE: // [500] 서버 공지 수신
							System.out.println(responeMessage);
							String admin = tokens[1];
							String adminMessage = tokens[2].trim();
							ui.getChatRoomPanel().noticeMessage("[" + admin + "] : " + adminMessage);
							break;

						case MessageType.S_ADMIN_WHISPER: // [501] 관리자의 귓속말
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
		ui.setSize(1000, 700);
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
