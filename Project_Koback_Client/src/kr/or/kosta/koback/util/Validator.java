package kr.or.kosta.koback.util;
/**
 * 정규표현식을 활용한 유효성 검증 공통메소드
 * @author 김기정 
 *
 */
public class Validator {
	/**
	 * 입력필드 입력여부 검증
	 * @param value
	 * @return
	 */
	public static boolean isNull(String value){
		return value == null || value.trim().length() == 0;
	}

	/**
	 * 아이디 유효성 검증
	 * 영문과숫자조합으로 8~10자 아이디
	 * ex) bangry313
	 * @param id
	 * @return
	 */
	public static boolean validId(String id){
		return id.matches("[a-zA-Z0-9]{8,10}");
	}

	/**
	 * 이메일 유효성 검증
	 * ex) bangry313@gmail.com
	 * @param email
	 * @return
	 */
	public static boolean validEmail(String email) {
		return email.matches("^[a-zA-Z0-9]+@[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)+$");
	}
	
	/**
	 * 전화번호 유효성 검증
	 * 2~3자리숫자-3~4자리숫자-4자리숫자
	 * ex) 02-1234-5678
	 * @param number
	 * @return
	 */
	public static boolean validPhoneNumber(String number) {
		return number.matches("^0+([0-9]{1,2})-[0-9]{3,4}-[0-9]{4}+$");
	}
	
	/**
	 * 휴대폰번호 유효성 검증
	 * 010|011|016|017|018|019-3~4자리숫자-4자리숫자
	 * ex) 10-9179-8707
	 * @param number
	 * @return
	 */
	public static boolean validMobileNumber(String number) {
		return number.matches("^01(0|1|[6-9])-[0-9]{3,4}-[0-9]{4}+$");
	}
	
	/**
	 * 주민등록번호 유효성 검증
	 * 6자리숫자-1~8로 시작하는 7자리숫자
	 * ex) 680313-1234567
	 * @param ssn
	 * @return
	 */
	public static boolean validSSN(String ssn) {
		return ssn.matches("^[0-9]{6}-[1-8]+[0-9]{6}+$");
	}
	
	/**
	 * IP주소 유효성 검증
	 * 0~255.0~255.0~255.0~255
	 * ex) 192.168.0.28
	 * @param ip
	 * @return
	 */
	public static boolean validIP(String ip) {
		return ip.matches("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
	}
	
	/**
	 * 파일명 유효성 검증
	 * ex) sample.gif
	 * @param ip
	 * @return
	 */
	public static boolean validFile(String fileName) {
		return fileName.matches("[^\\s]+(\\.(?i)(jpg|png|gif|bmp))$");
	}

}
