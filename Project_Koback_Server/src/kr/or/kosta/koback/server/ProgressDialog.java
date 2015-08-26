package kr.or.kosta.koback.server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.TitledBorder;

/**
 * 다운로드 진행 상태를 추상화한 클래스
 * @author 유안상
 */
public class ProgressDialog extends JDialog{
	JPanel progressPanel;
	JProgressBar progressBar;
	JPanel buttonPanel;
	JButton confirmB;
	String fileName;
	
	public ProgressDialog(Frame owner, String fileName) {
		super(owner, "파일 다운로드");
		this.fileName = fileName;
		progressPanel = new JPanel();
		progressBar = new JProgressBar(0, 100);
		progressBar.setPreferredSize(new Dimension(350, 20));
		progressBar.setStringPainted(true);
		progressPanel.setBorder(new TitledBorder(fileName+" 파일 업로드중"));
		buttonPanel = new JPanel();
		confirmB = new JButton("확인");
		confirmB.setEnabled(false);
	}

	public void setComponents(){
		progressPanel.add(progressBar);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(confirmB);
		add(progressPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		setAlwaysOnTop(true);
	}
	
	/**
	 * 프로그레스바 진행 처리
	 * @param value
	 */
	public void setProgressValue(int value){
		progressBar.setValue(value);
		progressBar.setString(value + "% 업로드중");
		if(value == 100){
			progressPanel.setBorder(new TitledBorder(fileName+" 파일보관함에 업로드 완료"));
			progressBar.setString("업로드 완료");
			confirmB.setEnabled(true);
		}
	}
	
	/**
	 * 종료 처리
	 */
	private void exit(){
		setVisible(false);
		dispose();
		System.exit(0);
	}
	
	/**
	 * 이벤트소스에 이벤트리스너 등록
	 */
	public void eventRegist(){
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});
		
		confirmB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				setVisible(false);
			}
		});
	}
}
