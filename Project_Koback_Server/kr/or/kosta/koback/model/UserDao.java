package kr.or.kosta.koback.model;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.annotation.Native;
import java.util.ArrayList;
import java.util.List;

import kr.or.kosta.koback.util.Validator;

/**
 * 
 * @author 윤성훈
 *
 */
public class UserDao {

	/** 저장파일 경로 */
	private static final String FILE_PATH = "users.dat";

	private RandomAccessFile randomAccessFile;

	/** 저장된 레코드수 */
	private int userCount = 0;

	/** 레코드수 저장을 위한 컬럼 사이즈 */
	private static final int RECORD_COUNT_LENGTH = 4;

	/** user 정보 저장을 위한 레코드 컬럼별 사이즈 설정 */
	private static final int USER_ID_LENGTH = 20;			//20바이트(최대 10글자)로 ID 저장
	private static final int USER_NAME_LENGTH = 10;			//10바이트(최대 5글자)로 이름 저장
	private static final int USER_NICKNAME_LENGTH = 12;		//12바이트(최대 6글자)로 닉네임 저장
	private static final int USER_PASSWORD_LENGTH = 24;		//24바이트(최대 12글자)로 비밀번호 저장	
	private static final int USER_SSN_LENGTH = 28;			//28바이트(-포함 14글자)로 주민번호 저장
	private static final int USER_EMAIL_LENGTH = 50;		//50바이트(총 25글자) EMAIL저장
	private static final int USER_PHONE_LENGTH = 26;		//26바이트(000-0000-0000 형식) 번호 저장

	public static final int RECORD_LENGTH = USER_ID_LENGTH + USER_NAME_LENGTH + USER_NICKNAME_LENGTH
			+ USER_PASSWORD_LENGTH + USER_SSN_LENGTH + USER_EMAIL_LENGTH + USER_PHONE_LENGTH;

	/** 처음 시작을 PASSWORD에서 시작하기위한 사이즈
	 * Password 찾기 */
	public static final int RECORD_PASS_LENGTH = USER_ID_LENGTH + USER_NAME_LENGTH + USER_NICKNAME_LENGTH;

	/** 처음 시작을 닉네임에서 시작하기위한 사이즈 
	 * 아이디로 검색해서 닉네임으로 출력위한 파일크기
	 * 닉네임 중복검사하기위한 파일크기*/
	public static final int RECORD_NICK_LENGTH = USER_ID_LENGTH + USER_NAME_LENGTH;
	
	/** 처음 시작을 주민등록번호에서 시작하기위한 사이즈	//   08/22 추가
	 *주민등록번호 중복검사하기위한 파일크기*/
	public static final int RECORD_SSN_LENGTH = USER_ID_LENGTH + USER_NAME_LENGTH + USER_NICKNAME_LENGTH + USER_PASSWORD_LENGTH;
	
	/** 처음 시작을 Email에서 시작하기위한 사이즈 //   08/22 추가
	 *이메일 중복검사하기위한 파일크기*/
	public static final int RECORD_EMAIL_LENGTH = USER_ID_LENGTH + USER_NAME_LENGTH + USER_NICKNAME_LENGTH + USER_PASSWORD_LENGTH + USER_SSN_LENGTH;
	

	
	public UserDao() throws IOException {
		randomAccessFile = new RandomAccessFile(FILE_PATH, "rw");
		if (randomAccessFile.length() != 0) {
			userCount = randomAccessFile.readInt(); // 저장되어있는 레코드의수
		}
	}

	public RandomAccessFile getRandomAccessFile() {
		return randomAccessFile;
	}

	public int getuserCount() {
		return userCount;
	}

	/* 신규 User 등록 메소드 */
	public void addUser(User user) throws IOException {
		randomAccessFile.seek((userCount * RECORD_LENGTH) + RECORD_COUNT_LENGTH);

		String userId = user.getId();
		String userName = user.getName();
		String userNickName = user.getNickName();
		String userPasswd = user.getPasswd();
		String userSsn = user.getSsn();
		String userEmaile = user.getEmail();
		String userPhoneNum = user.getPhoneNumber();

		int charCount = userId.length(); 
		for (int i = 0; i < (USER_ID_LENGTH / 2); i++) {
			randomAccessFile.writeChar((i < charCount ? userId.charAt(i) : ' '));
		}

		charCount = userName.length();
		for (int i = 0; i < (USER_NAME_LENGTH / 2); i++) {
			// EX) "윤성훈 " 이름이 3자가 되든 4자가되든 빈공간채워줌
			randomAccessFile.writeChar((i < charCount ? userName.charAt(i) : ' '));
		}

		charCount = userNickName.length();
		for (int i = 0; i < (USER_NICKNAME_LENGTH / 2); i++) {
			randomAccessFile.writeChar((i < charCount ? userNickName.charAt(i) : ' '));
		}

		charCount = userPasswd.length();
		for (int i = 0; i < (USER_PASSWORD_LENGTH / 2); i++) {
			randomAccessFile.writeChar((i < charCount ? userPasswd.charAt(i) : ' '));
		}

		charCount = userSsn.length();
		for (int i = 0; i < (USER_SSN_LENGTH / 2); i++) {
			randomAccessFile.writeChar((i < charCount ? userSsn.charAt(i) : ' '));
		}

		charCount = userEmaile.length();
		for (int i = 0; i < (USER_EMAIL_LENGTH / 2); i++) {
			randomAccessFile.writeChar((i < charCount ? userEmaile.charAt(i) : ' '));
		}

		charCount = userPhoneNum.length();
		for (int i = 0; i < (USER_PHONE_LENGTH / 2); i++) {
			randomAccessFile.writeChar((i < charCount ? userPhoneNum.charAt(i) : ' '));
		}
		randomAccessFile.seek(0); // 레코드 저장 후 파일포인터를 파일의 처음으로 이동시켜
		randomAccessFile.writeInt(++userCount); // 등록된 레코드 수 1 증가

	}

	/** 등록된 전체리스트 반환 */
	public List<User> getAllUser() throws Exception {
		ArrayList<User> list = new ArrayList<User>();
		for (int i = 0; i < userCount; i++) {
			User user = getRecord(i);
			list.add(user);
		}
		return list;
	}

	/**
	 * 반복문을 사용해서 String에 붙여 넣는다 코드를 복잡하게 할 가능성이 있으니 메소드화해서 호출하기만 한다
	 */
	public String read(int length) throws IOException {
		String argString = "";
		for (int i = 0; i < length / 2; i++) {
			argString += randomAccessFile.readChar();
		}
		argString = argString.trim();
		return argString;
	}

	public void write(String args, int length) throws IOException {
		int charCount = args.length();
		for (int i = 0; i < length / 2; i++) {
			randomAccessFile.writeChar((i < charCount ? args.charAt(i) : ' '));
		}
	}

	/** read, write 메소드를 이용하여
	 * user 삭제 메소드 작성*/
	public boolean removeUser(String getId) throws IOException {
		String id = "";
		String name = "";
		String nickName = "";
		String passwd = "";
		String ssn = "";
		String email = "";
		String phone = "";

		for (int i = 0; i < userCount; i++) {
			randomAccessFile.seek((i * RECORD_LENGTH) + RECORD_COUNT_LENGTH);

			id = read(USER_ID_LENGTH);

			if (id.equals(getId)) {
				for (int j = i; j < userCount - 1; j++) {
					randomAccessFile.seek(((j + 1) * RECORD_LENGTH) + RECORD_COUNT_LENGTH);
					id = read(USER_ID_LENGTH);
					name = read(USER_NAME_LENGTH);
					nickName = read(USER_NICKNAME_LENGTH);
					passwd = read(USER_PASSWORD_LENGTH);
					ssn = read(USER_SSN_LENGTH);
					email = read(USER_EMAIL_LENGTH);
					phone = read(USER_PHONE_LENGTH);

					randomAccessFile.seek((j * RECORD_LENGTH) + RECORD_COUNT_LENGTH);
					write(id, USER_ID_LENGTH);
					write(name, USER_NAME_LENGTH);
					write(nickName, USER_NICKNAME_LENGTH);
					write(passwd, USER_PASSWORD_LENGTH);
					write(ssn, USER_SSN_LENGTH);
					write(email, USER_EMAIL_LENGTH);
					write(phone, USER_PHONE_LENGTH);
				}
				randomAccessFile.seek(0);
				randomAccessFile.writeInt(--userCount);
				return true;
			}
			continue;
		}
		return false;
	}

	/** ID로 검색하여 해당 User정보 출력 */
	public User getUser(String getId) throws IOException {
		ArrayList<User> list = new ArrayList<User>();
		for (int i = 0; i < userCount; i++) {
			User user = getRecord(i);
			if (user.getId().equals(getId)) {
				return user;
			}
		}
		return null;
	}

	/** 원하는 ID로 ID 검색 */
	public boolean onlyIdSearch(String getId) throws IOException {
		String id = "";
		for (int i = 0; i < userCount; i++) {
			randomAccessFile.seek((i * RECORD_LENGTH) + RECORD_COUNT_LENGTH);
			id = read(USER_ID_LENGTH);

			if (id.equals(getId)) {
				return true;
			}
			continue;
		}
		return false;
	}

	/** 원하는 비밀번호만 검색 */
	public boolean onlyPasswdSearch(String getPass) throws IOException {
		String passwd = "";

		for (int i = 0; i < userCount; i++) {

			randomAccessFile.seek((i * RECORD_LENGTH) + RECORD_COUNT_LENGTH + RECORD_PASS_LENGTH);

			passwd = read(USER_PASSWORD_LENGTH);
			if (passwd.equals(getPass)) {
				return true;
			}
			continue;
		}
		return false;
	}

	/** ID검색해서 닉네임 가져오기 */
	public String idSearchNickName(String getId) throws IOException {
		String id = "";
		String nick = "";

		for (int i = 0; i < userCount; i++) {
			randomAccessFile.seek((i * RECORD_LENGTH) + RECORD_COUNT_LENGTH);
			id = read(USER_ID_LENGTH);

			if (id.equals(getId)) {
				randomAccessFile.seek((i * RECORD_LENGTH) + RECORD_NICK_LENGTH);
				nick = read(USER_NICKNAME_LENGTH);
			}
			continue;
		}
		return nick;
	}

	/** 비밀번호로 검색 */
	public User getUserPass(String getPasswd) throws IOException {
		ArrayList<User> list = new ArrayList<User>();
		for (int i = 0; i < userCount; i++) {
			User user = getRecord(i);

			if (user.getPasswd().equals(getPasswd)) {
				return user;
			}
		}
		return null;
	}
	

	private User getRecord(int index) throws IOException {
		User user = null;

		String id = "";
		randomAccessFile.seek((index * RECORD_LENGTH) + RECORD_COUNT_LENGTH);
		for (int i = 0; i < (USER_ID_LENGTH / 2); i++) {
			id += randomAccessFile.readChar();
		}
		id = id.trim();

		String name = "";
		for (int i = 0; i < (USER_NAME_LENGTH / 2); i++) {
			name += randomAccessFile.readChar();
		}
		name = name.trim();

		String nickName = "";
		for (int i = 0; i < (USER_NICKNAME_LENGTH / 2); i++) {
			nickName += randomAccessFile.readChar();
		}
		nickName = nickName.trim();

		String passwd = "";
		for (int i = 0; i < (USER_PASSWORD_LENGTH / 2); i++) {
			passwd += randomAccessFile.readChar();
		}
		passwd = passwd.trim();

		String ssn = "";
		for (int i = 0; i < (USER_SSN_LENGTH / 2); i++) {
			ssn += randomAccessFile.readChar();
		}
		ssn = ssn.trim();
		
		String email = "";
		for (int i = 0; i < (USER_EMAIL_LENGTH / 2); i++) {
			email += randomAccessFile.readChar();
		}
		email = email.trim();
		
		String phone = "";
		for (int i = 0; i < (USER_PHONE_LENGTH / 2); i++) {
			phone += randomAccessFile.readChar();
		}
		phone = phone.trim();
		
		user = new User(id, name, nickName, passwd, ssn, email, phone);
		return user;
	}

	/** ID 중복 검사 */
	public boolean overlapId(String getId) throws IOException {
		String id = "";
		for (int i = 0; i < userCount; i++) {
			randomAccessFile.seek((i * RECORD_LENGTH) + RECORD_COUNT_LENGTH);
			id = read(USER_ID_LENGTH);
			if (id.equals(getId)) {
				return true;
			}
			continue;
		}
		return false;
	}

	/** nickName 중복 검사 */
	public boolean overlapNick(String getNick) throws IOException {
		String nick = "";
		for (int i = 0; i < userCount; i++) {
			randomAccessFile.seek((i * RECORD_LENGTH) + RECORD_NICK_LENGTH);
			nick = read(USER_NICKNAME_LENGTH);
			if (nick.equals(getNick)) {
				return true;
			}
			continue;
		}
		return false;
	}
	
	/** 주민등록번호 중복 검사 */					//   08/22 추가
	public boolean overlapSsn(String getSsn) throws IOException {
		String ssn = "";
		for (int i = 0; i < userCount; i++) {
			randomAccessFile.seek((i * RECORD_LENGTH) + RECORD_SSN_LENGTH);
			ssn = read(USER_SSN_LENGTH);
			if (ssn.equals(getSsn)) {
				return true;
			}
			continue;
		}
		return false;
	}
	
	/** 이메일 중복 검사 */						//   08/22 추가
	public boolean overlapEmail(String getEmail) throws IOException {
		String email = "";
		for (int i = 0; i < userCount; i++) {
			randomAccessFile.seek((i * RECORD_LENGTH) + RECORD_EMAIL_LENGTH);
			email = read(USER_EMAIL_LENGTH);
			if (email.equals(getEmail)) {
				return true;
			}
			continue;
		}
		return false;
	}
	

	/** Validator(정규표현식)을 이용한 ID 형식 및 길이제한 메소드 */
	public boolean idLimitLength(String Id) {
		if (Validator.validId(Id)) {
			return true;
		} else {
			return false;
		}
	}

	/** 닉네임 길이제한 */
	public boolean nickLimitLength(String nick) {
		if (Validator.validNick(nick)) {
			return true;
		} else {
			return false;
		}
	}

	/** 주민등록번호 검사 */
	public boolean ssnChecked(String ssn) {
		if (Validator.validSSN(ssn)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean passwdChecked(String pass) {
		if (Validator.validPass(pass)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean PhoneNumChecked(String phoneNum) {
		if (Validator.validMobileNumber(phoneNum)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean emailChecked(String email) {							//   08/22 추가
		if (Validator.validEmail(email)) {
			return true;
		} else {
			return false;
		}
	}
	
	public void close() {
		try {
			if (randomAccessFile != null)
				randomAccessFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
}

	
