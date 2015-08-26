package kr.or.kosta.koback.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;

import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.util.GUIUtil;

/**
 * 파일공유방 서버를 추상화
 * 공유 파일이 올라오면 새로운 공유서버를 만듦
 * @author 유안상
 */
public class FileShareRoom extends Thread{
	private ServerSocket shareServerSocket;
	private String serverIp;
	private int port;
	private String fileName;
	private long fileSize;
	
	private DataInputStream in;
	private DataOutputStream out;
	private Socket shareSocket;
	public static String FILE_DRECTORY = "classes/";
	private boolean upAndDown = false;
	ChatService client;
	public static int fileIndex = 1;
	
	public FileShareRoom(ChatService client, boolean upAndDown) throws IOException{
		portScanner();
		this.upAndDown = upAndDown;
		this.client = client;
	}
	
	public void uploadFile(String fileName) throws IOException{
		File file = new File(FILE_DRECTORY, fileName);
		FileOutputStream fos = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		int count = 0;
		int copySize = 0;
		int copyPer = 0;
		
		ProgressDialog dialog = new ProgressDialog(null, fileName);
		dialog.setComponents();
		dialog.setSize(350, 120);
		dialog.eventRegist();
		GUIUtil.setCenterScreen(dialog);
		GUIUtil.setLookAndFeel(dialog, GUIUtil.THEME_NIMBUS);
		dialog.setVisible(true);
		dialog.setProgressValue(50);
		
		while((count = in.read(buffer)) != -1){
			fos.write(buffer, 0, count);
			copySize += count;
			copyPer = (int)(((double)copySize/fileSize) * 100);
			dialog.setProgressValue(copyPer);
			if(copyPer == 100) break;
		}
		fileSize = file.length();
		String name = client.getUserId();
		out.writeUTF(MessageType.S_ALLFILE_LIST+MessageType.DELIMETER+name+MessageType.DELIMETER+fileIndex+MessageType.DELIMETER+fileName+MessageType.DELIMETER+fileSize);
		fileIndex++;
//		if(fos!=null) fos.close();
//		if(in!=null) in.close();
//		if(out!=null) out.close();
	}
	
	public void downloadFile(String fileName) throws IOException{
		File file = new File(FILE_DRECTORY, fileName); // 파일 객체만들기.
		FileInputStream fin = null;
		out.writeLong(file.length());
		try{
			fin = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int count = 0;
			int size = 0;
			while((count=fin.read(buffer)) != -1){
				size += count;
				out.write(buffer, 0, count);
			}
		}finally{
//			if(fin != null) fin.close();
		}
	}
	
	public void portScanner(){
		for (int i = 6000; i < 65536; i++) {
			try {
				port = i;
				shareServerSocket = new ServerSocket(port);
				break;
			} catch (IOException e) {
//				GUIUtil.showErrorMessage("포트가 이미 사용중입니다.");
				continue;
			}
		}
	}
	
	public void connectListening() throws IOException{
		shareSocket = shareServerSocket.accept();
		in = new DataInputStream(shareSocket.getInputStream());
		out = new DataOutputStream(shareSocket.getOutputStream());
	}
	
	@Override
	public void run() {
		try {
			if(upAndDown == true){
				 uploadFile(fileName);
			}else{
				downloadFile(fileName);
			}		
		} catch (IOException e) {
//			e.printStackTrace();
		}finally{
			try {
//				if(in != null) in.close();
//				if(out != null) out.close();
//				if(shareSocket != null) shareSocket.close();
			} catch (Exception e) {}
		}
	}

	public DataInputStream getIn() {
		return in;
	}

	public void setIn(DataInputStream in) {
		this.in = in;
	}

	public DataOutputStream getOut() {
		return out;
	}

	public void setOut(DataOutputStream out) {
		this.out = out;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getServerIp() {
		InetAddress ia = null;
		try {
			ia = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		serverIp = ia.getHostAddress();
		return serverIp;
	}

	public void setServerIp(String serverIp) throws UnknownHostException {
		this.serverIp = serverIp;
	}
}
