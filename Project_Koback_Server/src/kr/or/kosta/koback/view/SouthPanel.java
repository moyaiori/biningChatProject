package kr.or.kosta.koback.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import kr.or.kosta.koback.common.MessageType;
import kr.or.kosta.koback.server.ChatServer;
/**
 * 서버프레임의 SouthPanel 구현
 * @author 유안상
 */
public class SouthPanel extends JPanel {
	private JTextField adminTF;
	private JComboBox<String> allUserCb;
	private JButton sendBTN;
	private CenterPanel centerPanel;
	
	
	DefaultComboBoxModel<String> model;
	
	public SouthPanel(CenterPanel centerPanel){
		adminTF = new JTextField();
		model = new DefaultComboBoxModel<String>();
		model.addElement("전체");
		
		
		allUserCb = new JComboBox<String>();
		allUserCb.setModel(model);
		sendBTN = new JButton("전 송");
//		sendBTN = new JButton();
//		sendBTN.setIcon(new ImageIcon("classes/test.png"));
		sendBTN.setContentAreaFilled(false);
		this.centerPanel = centerPanel;
		setComponents();
	}
	
	public void setComponents(){
		setLayout(new BorderLayout());
//		allUserCb.addItem("전체");
		add(allUserCb, BorderLayout.WEST);
		add(adminTF, BorderLayout.CENTER);
		add(sendBTN, BorderLayout.EAST);
	}
	
	public void addUser(){}
	
	public void removeUser(){}
	

	public JTextField getAdminTF() {
		return adminTF;
	}

	public void setAdminTF(JTextField adminTF) {
		this.adminTF = adminTF;
	}

	public JComboBox<String> getAllUserCb() {
		return allUserCb;
	}

	public void setAllUserCb(JComboBox<String> allUserCb) {
		this.allUserCb = allUserCb;
	}

	public JButton getSendBTN() {
		return sendBTN;
	}

	public void setSendBTN(JButton sendBTN) {
		this.sendBTN = sendBTN;
	}
	
	public DefaultComboBoxModel<String> getModel() {
		return model;
	}

	public void setModel(DefaultComboBoxModel<String> model) {
		this.model = model;
	}

	@Override
	public String toString() {
		return "SouthPanel [adminTF=" + adminTF + ", allUserCb=" + allUserCb + ", sendBTN=" + sendBTN + "]";
	}
}
