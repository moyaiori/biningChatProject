package kr.or.kosta.koback.bin;

import kr.or.kosta.koback.util.GUIUtil;
import kr.or.kosta.koback.view.ChatUI;

public class Koback {

	public static void main(String[] args) {
		
		ChatUI frame = new ChatUI();
		frame.setSize(650,500);
		GUIUtil.setLookAndFeel(frame,GUIUtil.THEME_NIMBUS);
		GUIUtil.setCenterScreen(frame);
		frame.setVisible(true);
		System.out.println("클라이언트 구동");
	}
}
