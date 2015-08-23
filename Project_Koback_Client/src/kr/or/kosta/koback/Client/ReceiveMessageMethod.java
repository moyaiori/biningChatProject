package kr.or.kosta.koback.Client;

import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.view.chatroom.ChatRoomPanel;
import kr.or.kosta.koback.view.chatroom.ChatUI;
import kr.or.kosta.koback.view.login.UserLoginPanel;
import kr.or.kosta.koback.view.wait.waitingroom.WaitRoomListPanel;

//08 22 추가 (조현빈)
public class ReceiveMessageMethod {
	
	private ChatUI ui;
	private WaitRoomListPanel waitRoomPanel;
	private ChatRoomPanel chatRoomPanel;
	private UserLoginPanel userLoginPanel;
	
	public ReceiveMessageMethod(ChatUI ui) {
		this.ui = ui;
		waitRoomPanel = ui.getWaitRoomPanel();
		chatRoomPanel = ui.getChatRoomPanel();
		userLoginPanel = ui.getUserLoginPanel();
	}
	
	/* [101] 로그인결과응답 */
	public void sLoignResult(String connectionId,String waitingList,String chatRoomLists){
		/*대기방 리스트에 출력하기위해 호출.*/
		waitRoomPanel.waitingList(waitingList); 
		/* 자기 자신 이외의 클라이언트가  접속 할경우*/
		if (userLoginPanel.userId.equals(connectionId)) { 
			/*"방이름|아이디|비밀번호|방번호|인원|방이름|아이디|비밀번호|방번호|인원"*/
			if (!chatRoomLists.equals(null)) {				
				//String roomLists = chatRoomLists;
				//System.out.println(roomLists);
				ui.getWaitRoomPanel().roomList(chatRoomLists);
				ui.setTitle(connectionId+"님 KoBack채팅에 오신걸 환영합니다");
				ui.cardOpen("waitRoom");
			}
		}

		
		
	}

	public void scExit(String connectionId,String waitingList,String chatRoomLists){
		/*대기방 리스트에 출력하기위해 호출.*/
		waitRoomPanel.waitingList(waitingList); 
		/* 자기 자신 이외의 클라이언트가  접속 할경우*/
	//	if (userLoginPanel.userId.equals(connectionId)) { 
			/*"방이름|아이디|비밀번호|방번호|인원|방이름|아이디|비밀번호|방번호|인원"*/
			if (null != chatRoomLists) {				
				//String roomLists = chatRoomLists;
				//System.out.println(roomLists);
				ui.getWaitRoomPanel().roomList(chatRoomLists);
				
			}
			ui.setTitle(connectionId+"님 KoBack채팅에 오신걸 환영합니다");
			ui.cardOpen("waitRoom");
	//	}

		
		
	}

	/* [311] 대기자 목록 */	
	public void sRequestWaitingList(String waitingList){
		ui.getWaitRoomPanel().waitingList(waitingList);
	}
	/* [312] 개설된 방 목록 */	
	public void sRequestWaitingRoomList(String waitingRoomLists){
		System.out.println("method 안 "+waitingRoomLists);
	
		ui.getWaitRoomPanel().roomList(waitingRoomLists);

//		ui.revalidate();
//		ui.firePropertyChange(propertyName, oldValue, newValue);  fireTableStructureChanged();
	}

	
	
}
