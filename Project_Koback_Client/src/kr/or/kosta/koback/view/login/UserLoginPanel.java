package kr.or.kosta.koback.view.login;


/*
 *    클래스 명 : UserLoginPanel 
 *  클래스 역할 : 로그인 패널 
 *  화면 구현 : 윤성훈 (2015 02 19 (오후 2시))
 *  
 * 추가 구현 : 조현빈
 *    추가사항 (2015-08-20)
 *    1. joinFrameOpen() 메소드
 *         - 추가 1)회원가입 프레임 보여주기.
 *  
 * */
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.util.GUIUtil;
import kr.or.kosta.koback.view.chatroom.ChatUI;


public class UserLoginPanel extends JPanel {
   
   JButton loginB, joinB;
   
   JLabel idL, passwdL;
   
   JTextField idTF;
   JPasswordField passwdTF;
   JPanel mainP;
   
   GridBagLayout gridBagLayout;
   GridBagConstraints gridBagConstraints;
   
   ImageIcon icon;
   ChatUI chatUI;
   UserJoinFrame userJoinFrame;
   public static String userId;
   public static int userRoom=0;
   
   /*21일 - 추가 (조현빈)*/
   public static int userEntry =-1;   //-1 초기화.
   
   public UserLoginPanel(ChatUI chatUI){
     this.chatUI = chatUI;
      mainP = new JPanel();

         
      idL = new JLabel("아이디");
      passwdL = new JLabel("비밀번호");
      idTF = new JTextField(20);
      passwdTF = new JPasswordField(20);
      gridBagLayout = new GridBagLayout();
      gridBagConstraints = new GridBagConstraints();
      
      loginB = new JButton("로 그 인");
      joinB =  new JButton("회원가입");
      
      icon = new ImageIcon("classes/images/loginP.jpg");
      
      
      setComponents();
      eventRegist();
   }
   
   @Override
   protected void paintComponent(Graphics g) {
      // TODO Auto-generated method stub
      super.paintComponent(g);
      g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
   }
   

   public void setComponents(){
      
      setLayout(gridBagLayout); // 패널의 레이아웃을 GridBagLayout으로 설정
      gridBagConstraints.fill = GridBagConstraints.BOTH;
      gridBagConstraints.insets = new Insets(3, 5, 3, 5);
      addComponent(new JLabel(" "),   0, 9, 1, 1, 0.5, 0);
      addComponent(idL,             1, 9, 1, 1, 0, 0);
      addComponent(idTF,             2, 9, 2, 1, 0, 0);
      addComponent(new JLabel(" "),   4, 9, 1, 1, 0.5, 0);
      
      addComponent(passwdL,    1, 11, 1, 1, 0, 0);
      addComponent(passwdTF,    2, 11, 2, 1, 1.0, 0);
      
      addComponent(loginB,    1, 13, 1, 1, 0, 0);
      addComponent(joinB,    2, 13, 1, 1, 0, 0);
      
      
            
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
      
      add(com); // 컴포넌트를 Panel에 부착한다.
   }
   
   public void eventRegist(){
      joinB.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
         joinFrameOpen();
         
      }
      });
      loginB.addActionListener(new ActionListener() {
         
         @Override
         public void actionPerformed(ActionEvent e) {
            
            String id = idTF.getText();
            String pw = new String(passwdTF.getPassword());
            
            if(id.isEmpty()){
               JOptionPane.showMessageDialog(null, "아이디를 입력해주세요");
            }else if(pw.isEmpty()){
               JOptionPane.showMessageDialog(null, "비밀번호를 입력해 주세요.");
            }else{
            StringBuffer sb = new StringBuffer();
            sb.append(passwdTF.getPassword());
//            System.out.println(MessageType.C_LOGIN);
            try {//100프로토콜 
               System.out.println(MessageType.C_LOGIN+MessageType.DELIMETER+idTF.getText()+MessageType.DELIMETER+sb);
               userId = idTF.getText();
               chatUI.getChatClient().sendMessage(MessageType.C_LOGIN+MessageType.DELIMETER+idTF.getText()+MessageType.DELIMETER+sb);
               userEntry = 0;    //대기실 입장 일경우 0번
            } catch (IOException e1) {
               e1.printStackTrace();
            }
            
            
            }
         }
      });
      
   }
   
   public void joinFrameOpen(){
         userJoinFrame = new UserJoinFrame(chatUI);
      userJoinFrame.setSize(345, 500);
      GUIUtil.setLookAndFeel(userJoinFrame,GUIUtil.THEME_NIMBUS);
      GUIUtil.setCenterScreen(userJoinFrame);

      userJoinFrame.setVisible(true);
   }

      
   }
   