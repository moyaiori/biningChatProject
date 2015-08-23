package kr.or.kosta.koback.util;
/**
 * 정규표현식을 활용한 유효성 검증 공통메소드
 * @author 윤성훈
 */
public class Validator {
	/**
	 * 입력필드 입력여부 검증
	 */
	public static boolean isNull(String value){
		return value == null || value.trim().length() == 0;
	}

	/**
	 * 아이디 유효성 검증
	 * 영문과숫자조합으로 5~10자 아이디
	 */
	public static boolean validId(String id){
		return id.matches("[a-zA-Z0-9가-힣]{5,10}");
	}
	
	/**
	 * 닉네임 유효성 검증
	 * 영문과숫자 한글 조합으로 3~10자 아이디
	 */
	public static boolean validNick(String nick){
		return nick.matches("[a-zA-Z0-9가-힣]{3,10}");
	}
	
	
	/**
	 * 비밀번호 유효성 검증
	 * 영문과숫자조합으로 7~12자 아이디
	 */
	public static boolean validPass(String passwd){
		return passwd.matches("[a-zA-Z0-9가-힣]{7,12}");
	}
	
	/**
	 * 이메일 유효성 검증
	 */
	public static boolean validEmail(String email) {
		return email.matches("^[a-zA-Z0-9]+@[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)+$");
	}
	
	/**
	 * 휴대폰번호 유효성 검증
	 * 010|011|016|017|018|019-3~4자리숫자-4자리숫자
	 * ex) 010-9179-8707
	 */
	public static boolean validMobileNumber(String number) {
		return number.matches("^01(0|1|[6-9])-[0-9]{3,4}-[0-9]{4}+$");
	}
	
	/**
	 * 주민등록번호 유효성 검증
	 * 6자리숫자-1~8로 시작하는 7자리숫자
	 * ex) 880313-1234567
	 */
	public static boolean validSSN(String ssn) {
		return ssn.matches("^[0-9]{6}-[1-8]+[0-9]{6}+$");
	}
	
}
