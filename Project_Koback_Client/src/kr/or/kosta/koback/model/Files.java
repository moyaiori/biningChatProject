package kr.or.kosta.koback.model;

import javax.swing.JButton;

public class Files {
	//410+아이디(없음)+파일번호+파일명+올린사람(없음)+날짜+다운받기
	int fileNum;
	String fileName;
	String fileSize;
	
	public Files(int fileNum, String fileName, String fileSize){
		this.fileNum = fileNum;
		this.fileName = fileName;
		this.fileSize = fileSize;
	}

	public int getFileNum() {
		return fileNum;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFileSize() {
		return fileSize;
	}
	
}