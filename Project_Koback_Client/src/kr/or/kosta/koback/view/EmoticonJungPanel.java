package kr.or.kosta.koback.view;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

import kr.or.kosta.koback.model.EmoticonButtons;

public class EmoticonJungPanel extends JPanel {

	ArrayList<JButton> emoticonList;
	String[] filePath = new String[16];

	//버튼의 개수
	public static final int EMOTICION_NUM = 16;
	
	EmoticonFrame owner;

	// 버튼 생성
	public EmoticonJungPanel(EmoticonFrame owner) {
		this.owner = owner;

		emoticonList = new ArrayList<JButton>();

		filePath[0] = "classes/images/jung-00.jpg";
		filePath[1] = "classes/images/jung-01.jpg";
		filePath[2] = "classes/images/jung-02.jpg";
		filePath[3] = "classes/images/jung-03.jpg";
		filePath[4] = "classes/images/jung-04.jpg";
		filePath[5] = "classes/images/jung-05.jpg";
		filePath[6] = "classes/images/jung-06.jpg";
		filePath[7] = "classes/images/jung-07.jpg";
		filePath[8] = "classes/images/jung-08.jpg";
		filePath[9] = "classes/images/jung-09.jpg";
		filePath[10] = "classes/images/jung-10.jpg";
		filePath[11] = "classes/images/jung-11.jpg";
		filePath[12] = "classes/images/jung-12.jpg";
		filePath[13] = "classes/images/jung-13.jpg";
		filePath[14] = "classes/images/jung-14.jpg";
		filePath[15] = "classes/images/jung-15.jpg";

		for (int i = 0; i < EMOTICION_NUM; i++) {
			EmoticonButtons btn = new EmoticonButtons(filePath[i]);
			JButton button = btn.getEmoticonB();
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String iconPath = btn.getFilePath();
//					owner.owner.setEmoticon(iconPath);
					owner.owner.setChatEmoticon(iconPath);
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

