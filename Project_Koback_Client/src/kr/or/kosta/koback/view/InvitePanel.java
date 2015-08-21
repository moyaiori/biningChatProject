package kr.or.kosta.koback.view;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import kr.or.kosta.koback.util.GUIUtil;

public class InvitePanel extends JFrame {
	JButton okB,cancelB;
	JLabel fromL, toL, inviteL;
	JTextField fromTF, toTF;
	
	GridBagLayout gridBagLayout;
	GridBagConstraints gridBagConstraints;
	

	public InvitePanel(){
		okB = new JButton("수락");
		cancelB = new JButton("거절");
		
	    fromTF = new JTextField(5);
	    toTF = new JTextField(5);
	    
	    fromL = new JLabel("에서");
	    toL =  new JLabel("님이");
	    inviteL = new JLabel("초대하셨습니다");
	    
	    gridBagLayout= new GridBagLayout();
	    gridBagConstraints = new GridBagConstraints();
	    
	    
	}
	
	 public void setComponents(){
	      
	      setLayout(gridBagLayout); // 패널의 레이아웃을 GridBagLayout으로 설정
	      gridBagConstraints.fill = GridBagConstraints.BOTH;
	      gridBagConstraints.insets = new Insets(3, 5, 3, 5);
	      
	      
	      addComponent(new JLabel(" "),   	0, 0, 1, 1, 0.5, 0.0);
	      addComponent(fromTF, 				2, 1, 1, 1, 0.0, 0.0);
	      addComponent(fromL, 				3, 1, 1, 1, 0.0, 0.0);
	      
	      addComponent(toTF, 				2, 2, 1, 1, 0.0, 0.0);
	      addComponent(toL, 				3, 2, 1, 1, 0.0, 0.0);
	      
	      addComponent(inviteL, 			2, 3, 1, 1, 0.0, 0.0);
	      
	      addComponent(okB, 				2, 4, 1, 1, 0.1, 0.0);
	      addComponent(cancelB, 			3, 4, 1, 1, 0.8, 0.0);
	      addComponent(new JLabel(" "),   	4, 0, 1, 1, 0.5, 0.0);
	    
	      
	            
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
	
	
	public static void main(String[] args) {
		InvitePanel frame = new InvitePanel();
		frame.setSize(250,200);
		GUIUtil.setLookAndFeel(frame,GUIUtil.THEME_NIMBUS);
		GUIUtil.setCenterScreen(frame);
		frame.setComponents();
		frame.setVisible(true);
	}


	}


