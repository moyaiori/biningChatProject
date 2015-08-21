package kr.or.kosta.koback.view;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 * 서버프레임의 CenterPanel 구현
 * @author 유안상
 */
public class CenterPanel extends JPanel{
	private JTextArea logTA;
	private JTable roomTable;
	private JList<String> userList;
	
	private JScrollPane logScroll;
	
	private roomTableModel roomModel;
	private UserModel userModel;
	
	private GridBagLayout gridBagLayout;
	private GridBagConstraints gridBagConstraints;
	
	public CenterPanel(){
		logTA = new JTextArea();
		logTA.setEditable(false);
		roomModel = new roomTableModel();
		userModel = new UserModel();
		roomTable = new JTable(roomModel);
		userList = new JList<String>();
		userList.setModel(userModel);
		logScroll = new JScrollPane(logTA);
		gridBagLayout = new GridBagLayout();
		gridBagConstraints = new GridBagConstraints();
		
		setComponents();
		setTable();
	}
	
	public void setComponents(){
		setLayout(gridBagLayout);
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(2, 1, 2, 3);
		
		
		addComponent(logScroll, 0, 0, 1, 2, 0.7, 0.7);
		addComponent(new JLabel("접속 인원"), 1, 0, 1, 1, 0.0, 0.0);
		addComponent(userList, 1, 1, 1, 2, 0, 0.5);
		addComponent(new JScrollPane(roomTable), 0, 2, 1, 1, 0.0, 0.5);
		
	}
	
	/** 
	 *  GriBag레이아웃의 원하는 위치에 컴포넌트를 설정하기 위해서
	 *  설정한 메소드이다.
	 */
	public void addComponent(Component com, int gridx, int gridy, int gridWidth, int gridHeight, double weightx,
			double weighty) {
		gridBagConstraints.gridx = gridx;
		gridBagConstraints.gridy = gridy;
		gridBagConstraints.gridwidth = gridWidth;
		gridBagConstraints.gridheight = gridHeight;
		gridBagConstraints.weightx = weightx;
		gridBagConstraints.weighty = weighty;
		// GridBagLayout의 설정을 현재 Constraints(위에서 설정)한 값으로 적용한다
		gridBagLayout.setConstraints(com, gridBagConstraints);
		add(com); // 컴포넌트를 Panel에 부착한다.
	}
	
	public void setTable() {
		/**
		 * 테이블 설정 메소드 테이블의 Row(열) 크기 0행과 1행의 테이블 길이 설정
		 */
		roomTable.setRowHeight(10);
		TableColumnModel tcm = roomTable.getColumnModel();
		tcm.getColumn(0).setPreferredWidth(0);
		tcm.getColumn(1).setPreferredWidth(350);
		tcm.getColumn(2).setPreferredWidth(30);

		/** 테이블 조정 금지 */
		JTableHeader header = roomTable.getTableHeader();
		header.setResizingAllowed(false);
		header.setReorderingAllowed(false);
	}
	
	/** 일반메세지 출력 */
	public void setMessage(String message){
		logScroll.getVerticalScrollBar().setValue(logScroll.getVerticalScrollBar().getMaximum());
		logTA.append(message + "\r\n");
	}

	public JTextArea getLogTA() {
		return logTA;
	}

	public void setLogTA(JTextArea logTA) {
		this.logTA = logTA;
	}

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}
	
}
