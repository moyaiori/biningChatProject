package kr.or.kosta.koback.Client;

import kr.or.kosta.koback.view.chatroom.ChatRoomPanel;
import kr.or.kosta.koback.view.chatroom.ChatUI;
import kr.or.kosta.koback.view.login.UserLoginPanel;
import kr.or.kosta.koback.view.wait.waitingroom.WaitingRoomTablePanel;

//08 22 추가 (조현빈)
public class ReceiveMessageMethod {
	
	private ChatUI ui;
//	private WaitRoomListPanel waitRoomPanel;
	private WaitingRoomTablePanel waitingRoomTablePanel;
	private ChatRoomPanel chatRoomPanel;
	private UserLoginPanel userLoginPanel;
	
	public ReceiveMessageMethod(ChatUI ui) {
		this.ui = ui;
//		waitRoomPanel = ui.getWaitRoomPanel();
		waitingRoomTablePanel = ui.getWaitingRoomTablePanel();
		chatRoomPanel = ui.getChatRoomPanel();
		userLoginPanel = ui.getUserLoginPanel();
	}
	
	/* [101] 로그인결과응답 */
	public void sLoignResult(String connectionId,String waitingList,String chatRoomLists){
		/*대기방 리스트에 출력하기위해 호출.*/
		waitingRoomTablePanel.waitingList(waitingList); 
		/* 자기 자신 이외의 클라이언트가  접속 할경우*/
		if (userLoginPanel.userId.equals(connectionId)) { 
			/*"방이름|아이디|비밀번호|방번호|인원|방이름|아이디|비밀번호|방번호|인원"*/
			if (!chatRoomLists.equals(null)) {				
				//String roomLists = chatRoomLists;
				//System.out.println(roomLists);
				ui.getWaitingRoomTablePanel().roomList(chatRoomLists);
				ui.setTitle(connectionId+"님 KoBack채팅에 오신걸 환영합니다");
				ui.showCard("waitRoom");
			}
		}
	}
	
	   /* [101] 로그인결과응답 */
	public void sLogin(Boolean loginResult, String Message){
	       if(!loginResult){
	              ui.getUserLoginPanel().loginMessage(false,Message);
	              return;
	           }else{
	              ui.getUserLoginPanel().loginMessage(true,Message);
	           }
	 }
	
	/* [103] 회원가입 응답 결과 */
	public void sJoinResult(Boolean joinrResult,String Message){
        if(!joinrResult){
            ui.getUserJoinPanel().joinMessage(false,Message);
         }else{
            ui.getUserJoinPanel().joinMessage(true,Message);
         }
		
	}
	/* [201] 채팅방 개설 결과 응답 201|jo930408|방번호|제목|true*/
	public void sOpenResult(Boolean openResult,String roomTitle,String roomNum,String roomLists ){
		
		if (openResult) {	
			UserLoginPanel.userRoom= Integer.valueOf(roomNum);
			UserLoginPanel.userRoom= Integer.valueOf(roomNum);
			ui.getChatRoomPanel().chatRoomList(roomLists);
			ui.getChatRoomPanel().chatRoomOpenS(true,roomTitle,roomNum);

		}else{
			//채팅방이 개설이 되지 않을경우에 대한 예외처리/
		}
			
	
	}
	/* [203] 비밀방 개설 응답|203|jo930408|방번호|제목|인원|true */
	public void sSecretResult(String roomNum ,String roomTitle,Boolean openResult,String lists){
		if (openResult) {			

			UserLoginPanel.userRoom= Integer.valueOf(roomNum);
			ui.getChatRoomPanel().chatRoomList(lists);
			ui.getChatRoomPanel().chatRoomOpenS(true,roomTitle,roomNum);
		}else{
			//채팅방이 개설이 되지 않을경우에 대한 예외처리/
		}
	}
	
	public void errorMessage(Boolean RoomOpenResult,String messgae){
		 ui.getChatRoomPanel().chatRoomOpenS(RoomOpenResult,messgae);
	}
	
	
	/* [211] 채팅방 입장 결과 응답 */
	//인자값 : RoomOpenResult(tokens[2]), roomTitle(tokens[3]) , roomNum(tokens[4])
	public void sEntryResult(Boolean RoomOpenResult ,String roomTitle ,String roomNum){
		 UserLoginPanel.userRoom= Integer.valueOf(roomNum);
		 ui.getChatRoomPanel().chatRoomOpenS(RoomOpenResult,roomTitle,roomNum);
		
	}

	
	
	
	public void scExit(String connectionId,String waitingList,String chatRoomLists){
		/*대기방 리스트에 출력하기위해 호출.*/
		ui.getWaitingRoomTablePanel().waitingList(waitingList);
		/* 자기 자신 이외의 클라이언트가  접속 할경우*/
	//	if (userLoginPanel.userId.equals(connectionId)) { 
			/*"방이름|아이디|비밀번호|방번호|인원|방이름|아이디|비밀번호|방번호|인원"*/
			if (null != chatRoomLists) {				
				ui.getWaitingRoomTablePanel().roomList(chatRoomLists);
			}
			ui.setTitle(connectionId+"님 KoBack채팅에 오신걸 환영합니다");
			ui.showCard("waitRoom");
	//	}

		
		
	}

	/* [311] 대기자 목록 */	
	public void sRequestWaitingList(String waitingList){
		ui.getWaitingRoomTablePanel().waitingList(waitingList);
	}
	/* [312] 개설된 방 목록 */	
	public void sRequestWaitingRoomList(String waitingRoomLists){
		ui.getWaitingRoomTablePanel().roomList(waitingRoomLists);
	}

	
	
}
