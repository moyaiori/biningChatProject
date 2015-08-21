package kr.or.kosta.koback.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import kr.or.kosta.koback.view.ChatRoomPanel;
import kr.or.kosta.koback.view.ChatUI;

public class EmoticonButtons {

	String filePath;
//	ChatUI chatui = new ChatUI();
//	ChatRoomPanel chatRoomPanel = new ChatRoomPanel(chatui);

	public EmoticonButtons(String filePath) {
		this.filePath = filePath;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	private Icon createIcon(String path) {
		return new ImageIcon(path);
	}

	public JButton getEmoticonB() {
		JButton btn;
		btn = new JButton(createIcon(filePath));
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
//				chatRoomPanel.setEmoticon(filePath);
			}
		});
		return btn;
	}
	

	//
	// public String getFilePath() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//

}
