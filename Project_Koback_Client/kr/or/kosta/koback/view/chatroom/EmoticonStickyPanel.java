package kr.or.kosta.koback.view.chatroom;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import kr.or.kosta.koback.model.EmoticonButtons;

public class EmoticonStickyPanel extends JPanel {

	ArrayList<JButton> emoticonList;
	String[] filePath = new String[16];

	// 버튼의 개수
	public static final int EMOTICION_NUM = 16;

	
	EmoticonFrame owner;
	
	
	// 버튼 생성
	public EmoticonStickyPanel(EmoticonFrame owner) {
		this.owner = owner;
		
		emoticonList = new ArrayList<JButton>();

		filePath[0] = "classes/images/Untitled-1.jpg";
		filePath[1] = "classes/images/sticky-01.jpg";
		filePath[2] = "classes/images/sticky-02.jpg";
		filePath[3] = "classes/images/sticky-03.jpg";
		filePath[4] = "classes/images/sticky-04.jpg";
		filePath[5] = "classes/images/sticky-05.jpg";
		filePath[6] = "classes/images/sticky-06.jpg";
		filePath[7] = "classes/images/sticky-07.jpg";
		filePath[8] = "classes/images/sticky-08.jpg";
		filePath[9] = "classes/images/sticky-09.jpg";
		filePath[10] = "classes/images/sticky-10.jpg";
		filePath[11] = "classes/images/sticky-11.jpg";
		filePath[12] = "classes/images/sticky-12.jpg";
		filePath[13] = "classes/images/sticky-13.jpg";
		filePath[14] = "classes/images/sticky-14.jpg";
		filePath[15] = "classes/images/sticky-15.jpg";
		

		for (int i = 0; i < EMOTICION_NUM; i++) {
			EmoticonButtons btn = new EmoticonButtons(filePath[i]);
			JButton button = btn.getEmoticonB();
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String iconPath = btn.getFilePath();
//					owner.owner.setEmoticon(iconPath);
					owner.owner.setChatEmoticon(iconPath);
//					System.out.println(iconPath);
				}
			});
			emoticonList.add(button);
		}

		setComponents();
	}

	public void setComponents() {
		setLayout(new GridLayout(4, 4));
		for (JButton button : emoticonList) {
			add(button);
		}
	}

}
