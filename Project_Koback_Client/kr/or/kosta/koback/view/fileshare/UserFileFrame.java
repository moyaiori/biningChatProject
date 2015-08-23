package kr.or.kosta.koback.view.fileshare;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.model.FileDao;
import kr.or.kosta.koback.util.GUIUtil;
import kr.or.kosta.koback.view.chatroom.ChatUI;

public class UserFileFrame extends JFrame{
	
	private JTable printTB; //테이블
	private FileTableModel FilesTableModel; //테이블 모델

	private JButton btnUpload;
	private JButton btnFileFrameExit;
	private ArrayList<JButton> btnDownload;

	private GridBagConstraints gridBagConstraints;
	private GridBagLayout gridBagLayout;
	
	private String userId = "JO930408"; 
	FileDao dao;
	ChatUI chatUI;
	
	public UserFileFrame(ChatUI chatUI) {
		setTitle("파일 공유함");
		
		this.chatUI = chatUI;
		FilesTableModel = new FileTableModel();
		printTB = new JTable(FilesTableModel);

		btnUpload = new JButton("올리기");
		btnFileFrameExit = new JButton("나가기");
		btnDownload = new ArrayList<JButton>();
	
		gridBagConstraints = new GridBagConstraints();
		gridBagLayout =new GridBagLayout();
		

		setComponents();
		eventRegist();
	}
	
	public void setFile(){ //기능 메소드 2. 채팅 메시지 전송.
/*		String message= inputTF.getText();
		
		if(message==null || message.trim().length()==0){ //message.trim().length()==0 : 스페이스로 빈공만 있을경우.
			return;
		}
		
		try {
			//200|*|닉네임|*|채팅메세지
			chatClient.sendMessage(MessageType.SC_CHAT+MessageType.DELIMETER+getNickName()+MessageType.DELIMETER+message);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "네트워크 상태를 확인하여 주세요","네트워크 에러",JOptionPane.ERROR_MESSAGE);
		}
		inputTF.setText("");*/
	}
	
	
	public void setComponents(){
		setLayout(gridBagLayout);
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(5,8,10,8);
		
		add(new JScrollPane(printTB),0,0,3,1,0.8,1.3);
		JLabel jlabelEmpt = new JLabel("");
//		jlabelEmpt.setForeground(Color.RGBtoHSB(19,204, 4195, null));
		add(jlabelEmpt,	1,2,1,1,0.03,0.01);
		
		add(btnUpload,	0,3,1,1,0.02,0.1);
		add(btnFileFrameExit,2,3,1,1,0.02,0.1);
//		btnUpload.setBackground(Color.);
		
	}
	
	
	/*gridBagLayout에 컴포넌트 추가 메소드*/
	private void add(Component component, int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty){
		gridBagConstraints.gridx = gridx;
		gridBagConstraints.gridy = gridy;
		gridBagConstraints.gridwidth  = gridwidth;
		gridBagConstraints.gridheight  = gridheight;
		gridBagConstraints.weightx = weightx;
		gridBagConstraints.weighty = weighty;
		gridBagLayout.setConstraints(component, gridBagConstraints);
		add(component);
	}
	
	
	public void eventRegist(){
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});
		btnFileFrameExit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		btnUpload.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				fileUploadDialog();
			}
		});
	}

	public void fileUploadDialog(){

		JFileChooser fileChooser = new JFileChooser(".");	//경로 설정.
		int action = fileChooser.showOpenDialog(this);
		// 열기 버튼 선택시
		if(action == JFileChooser.APPROVE_OPTION){	//열기 였는지 확인 상수 1
			File selectedFile = fileChooser.getSelectedFile();
			dao = new FileDao(selectedFile.getAbsolutePath());
			
			try {
				chatUI.getChatClient().sendMessage(MessageType.C_UPLOAD+ MessageType.DELIMETER+"JO930408"+ MessageType.DELIMETER+1+MessageType.DELIMETER+dao.getFile().getName()+MessageType.DELIMETER+dao.getFileSize()+1234 );
//				chatUI.getChatClient().receiveMessage();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "열기 에러", JOptionPane.ERROR_MESSAGE);
			}

		}
	}
	
	public void exit() {
		setVisible(false);
		dispose();
	}

	public JTable getPrintTB() {
		return printTB;
	}
	public void setPrintTB(JTable printTB) {
		this.printTB = printTB;
	}
	public FileTableModel getFilesTableModel() {
		return FilesTableModel;
	}
	public void setFilesTableModel(FileTableModel filesTableModel) {
		FilesTableModel = filesTableModel;
	}
	public JButton getBtnUpload() {
		return btnUpload;
	}
	public void setBtnUpload(JButton btnUpload) {
		this.btnUpload = btnUpload;
	}

	public JButton getBtnFileFrameExit() {
		return btnFileFrameExit;
	}

	public void setBtnFileFrameExit(JButton btnFileFrameExit) {
		this.btnFileFrameExit = btnFileFrameExit;
	}
	public ArrayList<JButton> getBtnDownload() {
		return btnDownload;
	}

	public void setBtnDownload(ArrayList<JButton> btnDownload) {
		this.btnDownload = btnDownload;
	}

	public GridBagConstraints getGridBagConstraints() {
		return gridBagConstraints;
	}

	public void setGridBagConstraints(GridBagConstraints gridBagConstraints) {
		this.gridBagConstraints = gridBagConstraints;
	}

	public GridBagLayout getGridBagLayout() {
		return gridBagLayout;
	}

	public void setGridBagLayout(GridBagLayout gridBagLayout) {
		this.gridBagLayout = gridBagLayout;
	}

}
