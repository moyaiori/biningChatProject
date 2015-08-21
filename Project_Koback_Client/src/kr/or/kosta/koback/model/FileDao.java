package kr.or.kosta.koback.model;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 텍스트 파일 영속성 처리 클래스 캡슐화
 */
public class FileDao {
	private File file;
	private String filePath;
	private Long fileSize;
	
	public FileDao(String filePath){
		file = new File(filePath);
		this.filePath = filePath;
		this.fileSize = file.length();
	}
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * 텍스트 파일 읽기
	 */
	public String read() throws IOException{
	
		if(!file.exists()){
			throw new FileNotFoundException("읽고자 하는 파일이 존재하지 않습니다.");
		}
		StringBuilder sb = new StringBuilder();
		BufferedReader in = null;
		try{
			in = new BufferedReader(new FileReader(file));
			String txt = null;
			while((txt = in.readLine()) != null){
				sb.append(txt + "\r\n");			
			}
			sb.delete(sb.length()-2, sb.length());
		}catch(IOException e){
			throw new IOException();
		}finally{
			if(in != null){
				in.close();
			}
		}
		
		return sb.toString();		
	}
	
	/**
	 * 텍스트 파일 저장
	 * @throws IOException 
	 */
	public void save(String txt) throws IOException{
		File file = new File(filePath);
		if(file.exists()){
			throw new IOException("이미 파일이 존재합니다. 다른 이름으로 저장하여 주세요!");
		}
		PrintWriter out = null;
		try{
			out = new PrintWriter(file);
			out.print(txt);
		}catch(IOException e){
			throw new IOException();
		}finally{
			if(out != null){
				out.close();
			}
		}
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
}
