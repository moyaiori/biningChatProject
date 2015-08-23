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
	private int fileIndex;
	private boolean stop;
	private String fileName;
	private long fileSize;
	private Hashtable<Integer, String> fileList;
	
	private DataInputStream in;
	private DataOutputStream out;
	private Socket shareSocket;
	private static final String FILE_DRECTORY = "classess/File";
	
	public FileShareRoom() throws IOException{
		portScanner();
	}
	
	public void uploadFile(String fileName) throws IOException{
		byte[] buffer = new byte[1024];
		int count = 0;
		int test = 0;
		while((count = in.read()) != -1){
			test = in.read(buffer, 0, count);
			System.out.println(test);
		}
		System.out.println(test);
//		File file = new File(FILE_DRECTORY, fileName);
//		FileInputStream fin = null;
//		try{
//			fin = new FileInputStream(file);
//			byte[] buffer = new byte[1024];
//			int count = 0;
//			int copySize = 0;
//			while((count=fin.read(buffer)) != -1){
//				out.write(buffer, 0, count);
//				copySize += count;
//				if(copySize == fileSize){
//					break;
//				}
//			}
//		}finally{
//			if(fin != null) fin.close();
//		}
	}
	
	public void downloadFile(String fileName){
		
	}
	
	public void portScanner(){
		for (int i = 5000; i < 65536; i++) {
			try {
				port = i;
				shareServerSocket = new ServerSocket(port);
//				GUIUtil.showMessage("공유서버가 열렸습니다.");
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
			uploadFile(fileName);
		} catch (IOException e) {
			e.printStackTrace();
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
