package kr.or.kosta.koback.view.login;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import kr.or.kosta.koback.Client.ChatClient;
import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.view.chatroom.ChatUI;
/**
 * 회원가입패널
 * @author 윤성훈
 * 
 * 추가 소스 : 조현빈 (2015 08 20 )
 *   -eventRegist() : 회원가입 버튼 클릭시, 서버에게 102 승인요청
 *   -genderCheck() : genderG 중 선택 한 성별 구분을 위한 메소드 
 *   -joinSend() : 사용자가 입력한 정보 받은후 서버에 send
 */
public class UserJoinPanel extends JPanel{
   JLabel memberTermsL, nameL, idL, passwdL, genderL,  nickNameL, ssnL,  emailL, phoneNumL;
   JTextArea memberTermsTA;
   JTextField nameTF, idTF, nickNameTF, ssnTF, emailTF, phonefrontNumTF, phonebackNumTF;
   JPasswordField passwdPF, ssnPF;
   JComboBox<String> phoneNumCB;
   JRadioButton manRB, womanRB, agreeRB , notAgreeRB;
   JButton okB, cancelB;
   ChatUI chatUI;
   GridBagLayout gridBagLayout;
   GridBagConstraints gridBagConstraints;
   ButtonGroup genderG;
   ChatClient chatClient;
   Image icon;
   Font font;
   
   public UserJoinPanel(ChatUI chatUI){
      this("회원가입",chatUI);
   }


   public UserJoinPanel(String title,ChatUI chatUI){
      
      this.chatUI = chatUI;
      font = new Font("나눔고딕", Font.BOLD, 12);
      memberTermsL = new JLabel("회원약관");
      memberTermsL.setFont(font);
      memberTermsTA = new JTextArea(5,20);
      memberTermsTA.setText("제1조 [목적]\r\n"
                     +"이 약관은 Koback Messeger(채팅서비스)에서 제공 \r\n"
                     + "하는 인터넷 관련 서비스 (이하 \"서비스\"라 한다)를 이\r\n"
                     + "용함에 있어 사이버몰과 이용자의 권리, 의무 및 책임\r\n"
                     + "사항을 규정함을 목적으로 합니다.\r\n"
                        + "\r\n"
                        + "제2조[정의]\r\n" 
                        +"이 약관에서 사용하는 용어의 정의는 다음 각 호와 같\r\n"
                        +"습니다.\r\n" 
                        +"1. 이용자: 본 약관에 따라 단체가 제공하는 서비스를\r\n"
                        + "받는 자\r\n"
                        +"2. 이용계약: 서비스 이용과 관련하여 단체와 이용자\r\n"
                        + "간에 체결하는 계약\r\n"
                        +"3. 가입 : 단체가 제공하는 신청서 양식에 해당 정보를\r\n"
                        + " 기입하고, 본 약관에 동의하여 서비스 이용계약을 완\r\n"
                        + "료시키는 행위\r\n"
                        +"4. 이용자번호(ID): 회원 식별과 회원의 서비스 이용\r\n"
                        + "을 위하여 이용자가 선정하고 단체가 승인하는 영문자\r\n"
                        + "와 숫자의 조합(하나의 주민등록번호에 하나의 ID만\r\n"
                        + " 발급 가능함\r\n"
                        +"5. 패스워드(PASSWORD): 회원의 정보 보호를 위해 \r\n"
                        + "이용자 자신이 설정한 영문자와 숫자\r\n"
                        +"6. 이용해지 : 단체 또는 회원이 서비스 이용이후 그\r\n"
                        + " 이용 계약을 종료시키는 의사표시");
      
      memberTermsTA.setEditable(false);
      
      chatClient = new ChatClient();
      
      ButtonGroup termsG = new ButtonGroup();
      agreeRB = new JRadioButton("동의");
      notAgreeRB = new JRadioButton("동의안함");
      termsG.add(agreeRB);
      termsG.add(notAgreeRB);
      
      nameL = new JLabel("이름");
      nameL.setFont(font);
      nameTF = new JTextField(20);
      
      genderL = new JLabel("성별");
      genderL.setFont(font);
      genderG = new ButtonGroup();   
      manRB = new JRadioButton ("남자",true);
      womanRB = new JRadioButton ("여자");
      genderG.add(manRB);
      genderG.add(womanRB);
      
      
      idL = new JLabel("아이디");
      idL.setFont(font);
      idTF = new JTextField(20);
      
      passwdL = new JLabel("비밀번호");
      passwdL.setFont(font);
      passwdPF = new JPasswordField(20);
      passwdPF.setEchoChar('●');
      
      nickNameL = new JLabel("대화명");
      nickNameL.setFont(font);
      nickNameTF = new JTextField(20);
      
      ssnL = new JLabel("주민등록번호");
      ssnL.setFont(font);
      ssnTF = new JTextField(10);
      ssnPF = new JPasswordField(10);
      ssnPF.setEchoChar('●');
      
      emailL = new JLabel("e-mail");
      emailL.setFont(font);
      emailTF = new JTextField(20);
      
      phoneNumL = new JLabel("연락처");
      phoneNumL.setFont(font);
      phoneNumCB = new JComboBox<String>();
      phoneNumCB.addItem("010");
      phoneNumCB.addItem("011");
      phoneNumCB.addItem("017");
      phoneNumCB.addItem("016");
      phoneNumCB.addItem("019");
      
      phonefrontNumTF = new JTextField(6);
      phonebackNumTF = new JTextField(6);
      
      okB = new JButton("확인");
      cancelB = new JButton("취소");
      
      nameTF.setEnabled(false);
      idTF.setEnabled(false);
      passwdPF.setEnabled(false);
      nickNameTF.setEnabled(false);
      manRB.setEnabled(false);
      womanRB.setEnabled(false);
      ssnTF.setEnabled(false);
      ssnPF.setEnabled(false);
      emailTF.setEnabled(false);
      phoneNumCB.setEnabled(false);
      phonefrontNumTF.setEnabled(false);
      phonebackNumTF.setEnabled(false);
      okB.setEnabled(false);
      cancelB.setEnabled(false);
      
      gridBagLayout = new GridBagLayout();
      gridBagConstraints = new GridBagConstraints();
      
      setComponents();
      eventRegist();
   }
   
   @Override
   protected void paintComponent(Graphics g) {
	      URL url = getClass().getResource("/images/backgroundcolor.jpg");
	      icon = Toolkit.getDefaultToolkit().getImage(url);
	      g.drawImage(icon, 0, 0, getWidth(), getHeight(),this);
	      super.paintComponent(g);
     
   }
   
   
   public void setComponents(){
      
      setLayout(gridBagLayout);
      gridBagConstraints.fill = GridBagConstraints.BOTH;
      gridBagConstraints.insets = new Insets(3, 5, 3, 5);
      
      addComponent(memberTermsL,    1, 0, 1, 1, 0.0, 0.0);
      addComponent(new JScrollPane(memberTermsTA), 1, 1, 4, 1, 1.0, 0.5);
      
      addComponent(agreeRB,      3, 4, 1, 1, 0.0, 0.0);
      addComponent(notAgreeRB,   4, 4, 1, 1, 0.0, 0.0);
      addComponent(nameL,       1, 5, 1, 1, 0.0, 0.0);
      addComponent(nameTF,       2, 5, 4, 1, 0.0, 0.0);
      
      addComponent(genderL,       1, 6, 1, 1, 0.0, 0.0);
      addComponent(manRB,       2, 6, 1, 1, 0.0, 0.0);
      addComponent(womanRB,       3, 6, 1, 1, 0.0, 0.0);
      
      addComponent(idL,          1, 7, 1, 1, 0.0, 0.0);
      addComponent(idTF,          2, 7, 4, 1, 0.0, 0.0);
      
      addComponent(passwdL,       1, 8, 1, 1, 0.0, 0.0);
      addComponent(passwdPF,       2, 8, 4, 1, 0.0, 0.0);

      addComponent(nickNameL,    1, 9, 1, 1, 0.0, 0.0);
      addComponent(nickNameTF,    2, 9, 4, 1, 0.0, 0.0);
      
      addComponent(ssnL,          1, 10, 1, 1, 0.0, 0.0);
      addComponent(ssnTF,       2, 10, 1, 1, 0.5, 0.0);
      addComponent(ssnPF,       3, 10, 2, 1, 0.5, 0.0);
      
      addComponent(emailL,       1, 11, 1, 1, 0.0, 0.0);
      addComponent(emailTF,       2, 11, 4, 1, 0.0, 0.0);
      
      addComponent(phoneNumL,       1, 12, 1, 1, 0.0, 0.0);
      addComponent(phoneNumCB,       2, 12, 1, 1, 0.0, 0.0);
      addComponent(phonefrontNumTF,    3, 12, 1, 1, 0.7, 0.0);
      addComponent(phonebackNumTF,    4, 12, 1, 1, 0.3, 0.0);
      
      addComponent(okB,          2, 13, 1, 1, 0.0, 0.0);
      addComponent(cancelB,       3, 13, 1, 1, 0.0, 0.0);
            
   }
   
   public void eventRegist(){notAgreeRB.addActionListener(new ActionListener() {
	      
	      @Override
	      public void actionPerformed(ActionEvent e) {
	         if(notAgreeRB.isSelected()){
	            nameTF.setEnabled(false);
	            idTF.setEnabled(false);
	            passwdPF.setEnabled(false);
	            nickNameTF.setEnabled(false);
	            ssnTF.setEnabled(false);
	            ssnPF.setEnabled(false);
	            emailTF.setEnabled(false);
	            phoneNumCB.setEnabled(false);
	            phonefrontNumTF.setEnabled(false);
	            phonebackNumTF.setEnabled(false);
	            okB.setEnabled(false);
	            cancelB.setEnabled(false);
	            manRB.setEnabled(false);
	            womanRB.setEnabled(false);
	            
	         }
	         
	      }
	   });
	      
	      agreeRB.addActionListener(new ActionListener() {
	      
	      @Override
	      public void actionPerformed(ActionEvent e) {
	         // TODO Auto-generated method stub
	         if(agreeRB.isSelected()){
	            nameTF.setEnabled(true);
	            idTF.setEnabled(true);
	            passwdPF.setEnabled(true);
	            nickNameTF.setEnabled(true);
	            ssnTF.setEnabled(true);
	            ssnPF.setEnabled(true);
	            emailTF.setEnabled(true);
	            phoneNumCB.setEnabled(true);
	            phonefrontNumTF.setEnabled(true);
	            phonebackNumTF.setEnabled(true);
	            okB.setEnabled(true);
	            cancelB.setEnabled(true);
	            manRB.setEnabled(true);
	            womanRB.setEnabled(true);
	            
	         }
	      }
	   });
	   
	   
	   
	   
      okB.addActionListener(new ActionListener()  {
         
         @Override
         public void actionPerformed(ActionEvent e) {
             //유효성 검사를 위한 각 텍스트필드별 변수 선언
            String name = nameTF.getText();
            String id = idTF.getText();
            String pw = new String(passwdPF.getPassword());
            String nick = nickNameTF.getText();
            String ssnT = ssnTF.getText();
            String ssnP = new String(ssnPF.getPassword());
//            String ssnP = ssnBTF.getText();
            String email = emailTF.getText();
            String phoneF = phonefrontNumTF.getText();
            String phoneB = phonebackNumTF.getText();
            
            if(name.isEmpty()){
               JOptionPane.showMessageDialog(null, "이름을 입력해주십시오.");
            }else if(id.isEmpty()){
               JOptionPane.showMessageDialog(null, "아이디 입력해 주십시오.");
            }else if(pw.isEmpty()){
               JOptionPane.showMessageDialog(null, "비밀번호를 입력해 주십시오.");
            }else if(ssnT.isEmpty()){
               JOptionPane.showMessageDialog(null, "주민등록번호를 입력해 주십시오.");
            }else if(ssnP.isEmpty()){
               JOptionPane.showMessageDialog(null, "주민등록번호를 입력해 주십시오.");
            }else if (email.isEmpty()){
               JOptionPane.showMessageDialog(null, "이메일 주소를 입력해 주십시오.");
            }else if(phoneF.isEmpty()){
               JOptionPane.showMessageDialog(null, "연락처를 입력해 주십시오.");
            }else if(phoneB.isEmpty()){
               JOptionPane.showMessageDialog(null, "연락처를 입력해 주십시오.");
            }else{
               joinSend();

            }
         }
      });
            
            
            
            
            
      cancelB.addActionListener(new ActionListener() {
         
         @Override
         public void actionPerformed(ActionEvent e) {
            
            chatUI.getUserLoginPanel().userJoinFrame.exit();
         }
      });
   }

   private void addComponent(Component com, int gridx, int gridy, int gridWidth, int gridHeight, double weightx,
      double weighty) {
      gridBagConstraints.gridx = gridx;
      gridBagConstraints.gridy = gridy;
      gridBagConstraints.gridwidth = gridWidth;
      gridBagConstraints.gridheight = gridHeight;
      gridBagConstraints.weightx = weightx;
      gridBagConstraints.weighty = weighty;
      gridBagLayout.setConstraints(com, gridBagConstraints);
      add(com); 
   }

   
   public void joinSend(){
      
      /*사용자가 입력한 정보 받기*/
      StringBuffer ssnBuffer = new StringBuffer();
      StringBuffer pass = new StringBuffer();
      String tel = phoneNumCB.getSelectedItem()+phonefrontNumTF.getText()+ phonebackNumTF.getText();
      String ssn = ssnTF.getText()+ssnBuffer.append(ssnPF.getPassword());
      pass.append(passwdPF.getPassword());
      
      /*102프로토콜 - 회원 가입 승인요청.*/
      try {
         
         String message = MessageType.C_JOIN+MessageType.DELIMETER+idTF.getText()+MessageType.DELIMETER+nameTF.getText()+MessageType.DELIMETER+nickNameTF.getText()+MessageType.DELIMETER+pass+MessageType.DELIMETER+ssn+MessageType.DELIMETER+emailTF.getText()+MessageType.DELIMETER+tel;

         /* 서버에게 send*/
         chatUI.getChatClient().sendMessage(MessageType.C_JOIN+MessageType.DELIMETER+idTF.getText()+MessageType.DELIMETER+
               nameTF.getText()+MessageType.DELIMETER+nickNameTF.getText()+MessageType.DELIMETER+pass+
               MessageType.DELIMETER+ssn+MessageType.DELIMETER+emailTF.getText()+MessageType.DELIMETER+tel);
      } catch (IOException e1) {
         e1.printStackTrace();
      }
   }
   
   /* 회원가입 완료 팝업창 출력*/
   public void joinMessage(boolean joinFail, String message){
       if(joinFail){
          JOptionPane.showMessageDialog(null, "회원가입이 완료되었습니다");
          chatUI.getUserLoginPanel().userJoinFrame.exit();
       }else{
          JOptionPane.showMessageDialog(null, message);
       }
       
   
   
}
}