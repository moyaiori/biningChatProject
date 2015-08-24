package kr.or.kosta.koback.util;

import java.awt.Container;
import java.awt.Toolkit;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * GUI 관련된 공통 유틸리티 메소드
 * @author 유안상
 */
public class GUIUtil {
	/** 화면 중앙 배치 */
	public static void setCenterScreen(Container con) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		int x = (toolkit.getScreenSize().width - con.getWidth()) / 2;; 
		int y = (toolkit.getScreenSize().height - con.getHeight()) / 2;;
		
		con.setLocation(x, y); // 프레임의 위치염
	}
	
	/** 화면 풀 배치 */
	public static void setFullScreen(Container con){
		Toolkit toolkit = Toolkit.getDefaultToolkit();
//		Dimension dimension = toolkit.getScreenSize();
		con.setSize(toolkit.getScreenSize());; // 프레임의 위치염
	}
	
	public static final String THEME_SWING = "javax.swing.plaf.metal.MetalLookAndFeel";
	public static final String THEME_WINDOW = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	public static final String THEME_UNIX = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
	public static final String THEME_NIMBUS = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public static final String THEME_OS = "UIManager.getSystemLookAndFeelClassName()";
	
	/** Look&Feel 설정*/
	public static void setLookAndFeel(Container component,String className){
		try {
			UIManager.setLookAndFeel(className);
		} catch (Exception e) {
			e.printStackTrace();
		}
		SwingUtilities.updateComponentTreeUI(component);
	}
	
	/** 일반 메세지 출력 메소드 */
	public static void showMessage(String message) {
		JOptionPane.showMessageDialog(null, message, "알 림", JOptionPane.DEFAULT_OPTION);
	}

	/** 에러 메세지 출력 메소드 */
	public static void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(null, message, "경 고", JOptionPane.ERROR_MESSAGE);
	}
}
