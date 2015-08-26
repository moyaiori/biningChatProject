package kr.or.kosta.koback.bin;

import java.io.IOException;

import kr.or.kosta.koback.server.ChatServer;
import kr.or.kosta.koback.util.GUIUtil;
import kr.or.kosta.koback.view.ServerFrame;

public class Koback{

	public static void main(String[] args) {
		ServerFrame serverFrame = new ServerFrame();
		ChatServer server = new ChatServer(5500, serverFrame);
		
		try {
			server.startUp();
			server.connectListening();
		} catch (IOException e) {
			GUIUtil.showErrorMessage("아래와 같은 예외가 발생했습니다.\n"+ e.toString());
		}
	}
}
