package kr.or.kosta.koback.view.fileshare;

import java.awt.Color;
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
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.model.FileDao;
import kr.or.kosta.koback.view.chatroom.ChatUI;
import kr.or.kosta.koback.view.login.UserLoginPanel;

public class UserFileFrame extends JFrame{
	
	private JTable printTB; //테이블
	private FileTableModel filesTableModel; //테이블 모델

	private JButton btnUpload;
	private JButton btnFileFrameExit;
	private JButton btnDownload;

	private GridBagConstraints gridBagConstraints;
	private GridBagLayout gridBagLayout;
	
	FileDao dao;
	ChatUI chatUI;
	String save;
	
	public UserFileFrame(ChatUI chatUI) {
		setTitle("파일 공유함");
		
		this.chatUI = chatUI;
		filesTableModel = new FileTableModel();
		printTB = new JTable(filesTableModel);
//		URL url = getClass().getClassLoader().getResource("./images/fileColor.jpg");
//		this.setContentPane(new JLabel(new ImageIcon(url)));


		btnUpload = new JButton("올리기");
		btnFileFrameExit = new JButton("나가기");
		btnDownload = new JButton("다운로드");
	
		gridBagConstraints = new GridBagConstraints();
		gridBagLayout =new GridBagLayout();
		
		selectTableItem();
		setComponents();
		eventRegist();
	}
	
	public void setComponents(){
		setLayout(gridBagLayout);
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(5,8,10,8);
		JScrollPane scP = new JScrollPane(printTB);
		scP.getViewport().setBackground(Color.white);
		add(scP,			0,0,3,1,0.8,1.3);

		JLabel jlabelEmpt = new JLabel("");
//		jlabelEmpt.setForeground(Color.RGBtoHSB(19,204, 4195, null));
		add(jlabelEmpt,	1,2,1,1,0.03,0.01);
		add(btnDownload,1,3,1,1, 0.02, 0.01);
		add(btnUpload,	0,3,1,1,0.02,0.1);
		add(btnFileFrameExit,2,3,1,1,0.02,0.1);
		
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
				try {
					fileUploadDialog();
				} catch (IOException e1) {
				}
			}
		});
		btnDownload.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					fileDownLoading();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		
		
	}

	public void fileUploadDialog() throws IOException{
		JFileChooser fileChooser = new JFileChooser(".");	//경로 설정.
		int action = fileChooser.showOpenDialog(this);
		// 열기 버튼 선택시
		if(action == JFileChooser.APPROVE_OPTION){	//열기 였는지 확인 상수 1
			File selectedFile = fileChooser.getSelectedFile();
			dao = new FileDao(selectedFile.getAbsolutePath());
			chatUI.getChatClient().sendMessage(MessageType.C_UPLOAD+ MessageType.DELIMETER+UserLoginPanel.userId+ MessageType.DELIMETER+1+MessageType.DELIMETER+dao.getFile().getName()+MessageType.DELIMETER+dao.getFileSize());
		}
	}
	
	public void fileDownLoading() throws IOException{
			chatUI.getChatClient().sendMessage(MessageType.C_DOWNLOAD + MessageType.DELIMETER + UserLoginPanel.userId + MessageType.DELIMETER + save);
		}
	
	public void exit() {
		setVisible(false);
		dispose();
	}
	
	public String selectTableItem() {
		printTB.setCellSelectionEnabled(true);
	      ListSelectionModel cellSelectionModel = printTB.getSelectionModel();
	      cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	      cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
	         @Override
	         public void valueChanged(ListSelectionEvent e) {
	            int[] selectedRow = printTB.getSelectedRows();
	            int[] selectedColumns = printTB.getSelectedColumns();

	            for (int i = 0; i < selectedRow.length; i++) {
	               for (int j = 0; j < selectedColumns.length; j++) {
//	                  System.out.println("selectedRow : " + selectedRow[i] + selectedColumns[0]);
	            	   save = (String)printTB.getValueAt(selectedRow[i], selectedColumns[j]);
	               }
	            }
	         }
	      });
		return save;
	   }

	public JTable getPrintTB() {
		return printTB;
	}
	public void setPrintTB(JTable printTB) {
		this.printTB = printTB;
	}
	public FileTableModel getFilesTableModel() {
		return filesTableModel;
	}
	public void setFilesTableModel(FileTableModel filesTableModel) {
		this.filesTableModel = filesTableModel;
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

	public FileDao getDao() {
		return dao;
	}

	public void setDao(FileDao dao) {
		this.dao = dao;
	}

	public ChatUI getChatUI() {
		return chatUI;
	}

	public void setChatUI(ChatUI chatUI) {
		this.chatUI = chatUI;
	}
}
