package kr.or.kosta.koback.view.chatroom;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
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

//		URL url1 = getClass().getClassLoader().getResource("./images/jung-00.png");
//		URL url2 = getClass().getClassLoader().getResource("./images/jung-01.png");
//		URL url3 = getClass().getClassLoader().getResource("./images/jung-02.png");
//		URL url4 = getClass().getClassLoader().getResource("./images/jung-03.png");
//		URL url5 = getClass().getClassLoader().getResource("./images/jung-04.png");
//		URL url6 = getClass().getClassLoader().getResource("./images/jung-05.png");
//		URL url7 = getClass().getClassLoader().getResource("./images/jung-06.png");
//		URL url8 = getClass().getClassLoader().getResource("./images/jung-07.png");
//		URL url9 = getClass().getClassLoader().getResource("./images/jung-08.png");
//		URL url10 = getClass().getClassLoader().getResource("./images/jung-09.png");
//		URL url11 = getClass().getClassLoader().getResource("./images/jung-10.png");
//		URL url12 = getClass().getClassLoader().getResource("./images/jung-11.png");
//		URL url13 = getClass().getClassLoader().getResource("./images/jung-12.png");
//		URL url14 = getClass().getClassLoader().getResource("./images/jung-13.png");
//		URL url15 = getClass().getClassLoader().getResource("./images/jung-14.png");
//		URL url16 = getClass().getClassLoader().getResource("./images/jung-15.png");
//		
		
		
		
		
		
		
		filePath[0] = "classes/images/jung-00.png";
		filePath[1] = "classes/images/jung-01.png";
		filePath[2] = "classes/images/jung-02.png";
		filePath[3] = "classes/images/jung-03.png";
		filePath[4] = "classes/images/jung-04.png";
		filePath[5] = "classes/images/jung-05.png";
		filePath[6] = "classes/images/jung-06.png";
		filePath[7] = "classes/images/jung-07.png";
		filePath[8] = "classes/images/jung-08.png";
		filePath[9] = "classes/images/jung-09.png";
		filePath[10] = "classes/images/jung-10.png";
		filePath[11] = "classes/images/jung-11.png";
		filePath[12] = "classes/images/jung-12.png";
		filePath[13] = "classes/images/jung-13.png";
		filePath[14] = "classes/images/jung-14.png";
		filePath[15] = "classes/images/jung-15.png";

		for (int i = 0; i < EMOTICION_NUM; i++) {
			EmoticonButtons btn = new EmoticonButtons(filePath[i]);
			JButton button = btn.getEmoticonB();
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String iconPath = btn.getFilePath();
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

