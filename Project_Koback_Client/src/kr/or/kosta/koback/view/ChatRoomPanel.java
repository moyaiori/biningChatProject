package kr.or.kosta.koback.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

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
   JButton inviteB, kickB, fileB, outB, emoticonB, sendB;
   DefaultListModel<String> model;
   GridBagLayout gridBagLayout;
   GridBagConstraints gridBagConstraints;
   JComboBox<String> choiceCB;
   JTextField messageTF;
   ChatUI chatUI;
   UserFileFrame userFileFrame;
   
   InviteBtnPanel invitePopUp;
   kickBtnPanel kickPopUp;
   
   StringBuilder sb;

   
   EmoticonFrame emoticon = new EmoticonFrame(this);
   EmoticonButtons emoticonButtons;
   
   boolean toggle = false;
   
   public ChatRoomPanel(ChatUI chatUI) {
      
     this.chatUI = chatUI;
      model =       new DefaultListModel<String>();
      userList =    new JList<String>(model);
      headerL =    new JLabel();
      chatTextTP=    new JTextPane();
      headerL =    new JLabel("현재 접속자");
      
      inviteB =    new JButton("초대");
      kickB =       new JButton("강퇴");
      fileB =       new JButton("파일공유함");
      outB =       new JButton("나가기");
      emoticonB =    new JButton("이모티콘");
      sendB =       new JButton("전송");
      choiceL =    new JLabel("메세지를 보내고자 하시는 분을 선택해주세요");
      titleL =       new JLabel("채팅방 내용");
      choiceCB =    new JComboBox<String>();
      messageTF =    new JTextField();
 
    
      
      
      sb = new StringBuilder();
      gridBagLayout = new GridBagLayout();
      gridBagConstraints = new GridBagConstraints();
      
      
      setComponents();
      eventRegist();
   }
   
   
   
   
   /** 화면 배치 */
   public void setComponents(){
      
      setLayout(gridBagLayout);
     
      gridBagConstraints.fill = GridBagConstraints.BOTH;
      gridBagConstraints.insets = new Insets(2,1,2,1);
      JScrollPane sp = new JScrollPane(userList);
      
      add(titleL,         0,0,1,1,0,0);      //채팅창 제목 라벨
      add(chatTextTP,       0,1,4,3, 1.5, 1.0);   //채팅창
      
      add(new JLabel(""),   4,0,1,1,0,0);
      add(headerL,          5,0,1,1,0,0);      //리스트 헤더
      add(sp,            5,1,3,1,0,0.05);      //방 접속 리스트
      
      add(inviteB,         5,2,1,1,0.01,0);   //초대버튼
      add(kickB,          6,2,1,1,0.01,0);   //강퇴버튼
      add(fileB,         5,3,1,1,0,0);      //파일공유함 버튼
      add(new JLabel(""),   7,2,1,1,0,0);      //공백 라벨
      add(outB,            6,3,1,1,0,0);      //나가기 버튼
      
      add(messageTF,      0,5,4,1,0,0);      //글 입력 필드
      add(choiceCB,         0,4,1,1,0.01,0);   //귓속말 등 선택 콤보박스
      add(choiceL,         1,4,2,1,0.05,0);   //콤보박스 설명 라벨
      add(emoticonB,      5,5,1,1,0,0);      //이모티콘 버튼
      add(sendB,         6,5,1,1,0,0);      //전송 버튼 
      
//    일단 명시적으로 써놓음. 추후 LIST 로 받아와야함 
      

      
//      chatUserList();
   }
   
   /** 채팅방 유저 리스트  */
   public void chatUserList(String chatUserList){
      String users = chatUserList;
      
      String user[] = users.split(",");
      model.clear();
      for (String nickName : user) {
         model.addElement(nickName);
      }
   }
   
   /*
   public void chatUserList(){
      String[] list = {"가승호(eifwljef)", "용용", "비닝이"};
      for (String string : list) {
         model.addElement(string);
      }
   }
   */
   
   
   /*gridBagLayout에 컴포넌트 추가 메소드*/
   private void add(Component component, int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty){
      gridBagConstraints.gridx = gridx;
      gridBagConstraints.gridy = gridy;
      gridBagConstraints.gridwidth  = gridwidth;
      gridBagConstraints.gridheight  = gridheight;
      gridBagConstraints.weightx = weightx;
      gridBagConstraints.weighty = weighty;
      gridBagLayout.setConstraints(component, gridBagConstraints);
      add(component);
   }
   
   
   /** 채팅 메시지 송신 기능 */
   public void setChatMessage(){
      String message = messageTF.getText();
      if (message == null || message.trim().length() == 0) {
         return;
      }
      //userId
      messageTF.setText("");
     try {
        String id = chatUI.getUserLoginPanel().userId;
        
        //300ㆌㅱ아이디ㆌㅱ방번호ㆌㅱ메시지
//        System.out.println(message);
//       chatUI.getChatClient().sendMessage(MessageType.SC_CHAT_MESSAGE + MessageType.DELIMETER + "가승호" + MessageType.DELIMETER + 1 + MessageType.DELIMETER + message);
       chatUI.getChatClient().sendMessage(MessageType.SC_CHAT_MESSAGE + MessageType.DELIMETER + id + MessageType.DELIMETER + 1 + MessageType.DELIMETER + message);
     } catch (IOException e) {
      JOptionPane.showMessageDialog(this, "네트워크 상태를 확인해주세요." , "네트워크 에러", JOptionPane.ERROR_MESSAGE);
     }
      
   }
   
   
   
   /** 일반 메시지 출력 */
   public void setMessage(String message){
      System.out.println("셋메시지 부분 : " + message);

      sb.append(message+"\r\n");
      chatTextTP.setText(sb.toString()); 
   }
   
   
   
   /** 이모티콘 송신 기능 */
   public void setChatEmoticon(String filePath){
//      System.out.println(filePath);
//      String filePath = chatUI.getChatRoomPanel().emoticonButtons.getFilePath();
	   
      try {
         String id = chatUI.getUserLoginPanel().userId;
         chatUI.getChatClient().sendMessage(MessageType.SC_CHAT_EMOTICON + MessageType.DELIMETER + id + MessageType.DELIMETER + 1+ MessageType.DELIMETER + filePath);
      } catch (IOException e) {
         JOptionPane.showMessageDialog(this, "이모티콘을 보내지 못하였습니다. 관리자 문의." , "네트워크 에러", JOptionPane.ERROR_MESSAGE);
      }
   }
   
   
   /** 화면에 이모티콘 출력 */
   public void setEmoticon(String filePath){
	   
	   String id = chatUI.getUserLoginPanel().userId;
	   System.out.println(id);
	   System.out.println("룸패널 셋이모티콘 부분 " + filePath);
      ImageIcon icon = new ImageIcon(filePath);
//      JLabel image = new JLabel(icon + "\n");
//      chatTextTP.insertComponent(new JLabel(icon));
      chatTextTP.insertComponent(new JLabel(id));
      chatTextTP.insertIcon(icon);
      setEndIcon();
   }
   
   /** 이모티콘 출력시 다음줄에 출력하게 하는 메소드 */
  private void setEndIcon(){
	 setEndLine();
	 chatTextTP.replaceSelection("\n");
	 setEndLine();
  }
  
  //문장의 끝으로 이동하게 하기.
  private void setEndLine(){
	  chatTextTP.selectAll();
	  chatTextTP.setSelectionStart(chatTextTP.getSelectionEnd());
  }
   
   
   /**공지사항 메시지 출력 */
   public void noticeMessage(String notice){
	   StyledDocument doc = chatTextTP.getStyledDocument();
	      Style style = chatTextTP.addStyle("style", null);
	      StyleConstants.setForeground(style, Color.RED);
	      StyleConstants.setBold(style, true);
	      StyleConstants.setFontSize(style, 15);
	     
	      try {
	         doc.insertString(doc.getLength(), notice + "\r\n" , style);
	      } catch (BadLocationException e) {
	         e.printStackTrace();
	      }
	      
   }
   
   /** 관리자의 귓속말  */
   public void noticeWhisperMessage(String noticeWhisper){
	   StyledDocument doc = chatTextTP.getStyledDocument();
	      Style style = chatTextTP.addStyle("style", null);
	      StyleConstants.setForeground(style, Color.YELLOW);
	      StyleConstants.setBold(style, true);
	     
	      try {
	         doc.insertString(doc.getLength(), noticeWhisper + "\r\n" , style);
	      } catch (BadLocationException e) {
	         e.printStackTrace();
	      }
	      
   }
   
   
   /** 사용자 선택 */
   public void selectUser(){
      String user = (String)userList.getSelectedValue();
   }
   
   
   /** 선택한 사용자 강퇴(방장고유기능)*/
   public void kickUser(){
      
   }
   
   
   public void eventRegist(){
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
      
      /** 전송버튼을 누르지 않고 엔터치면 메시지 전송*/
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
               }else{
                  toggle = true;
                  EmoticonOpen(emoticon);
               }
               
         }
      });
      
      /** 강퇴 버튼을 누르면 강퇴확인 다이얼로그 출력 예시*/
      
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
            InviteFrameOpen();
         }
      });
      
      
      
      
   }
   
   /** 채팅창 내 버튼을 누르면 화면에 보여지는
    *  프레임들을 불러오는 메소드들 
    */
   
   public void KickFramOpen(){
	   kickPopUp = new kickBtnPanel(chatUI);
	   GUIUtil.setLookAndFeel(kickPopUp, GUIUtil.THEME_NIMBUS);
       GUIUtil.setCenterScreen(kickPopUp);
       kickPopUp.setSize(300, 300);
       kickPopUp.setVisible(true);
   }
   
   
   public void InviteFrameOpen(){
       invitePopUp = new InviteBtnPanel(chatUI);
       GUIUtil.setLookAndFeel(invitePopUp, GUIUtil.THEME_NIMBUS);
       GUIUtil.setCenterScreen(invitePopUp);
       invitePopUp.setSize(300,300);
       invitePopUp.setVisible(true);
   
   }
   
   public void FileFrameOpen(){
      userFileFrame = new UserFileFrame(chatUI);
      GUIUtil.setLookAndFeel(userFileFrame,GUIUtil.THEME_NIMBUS);
      GUIUtil.setCenterScreen(userFileFrame);
      userFileFrame.setSize(600, 400);
      userFileFrame.setVisible(true);
   }
   
   /** 이모티콘 프레임을 열기 위한 메소드 */
   public void EmoticonOpen(EmoticonFrame frame){
      GUIUtil.setLookAndFeel(frame,GUIUtil.THEME_NIMBUS);
      frame.setLocation(500, 600);
//      frame.setLocation(1450, 600);
      frame.setSize(200,300);
      frame.setVisible(true);
   }

}