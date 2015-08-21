package kr.or.kosta.koback.model;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

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
	private static final int USER_ID_LENGTH = 20;
	private static final int USER_NAME_LENGTH = 10;
	private static final int USER_NICKNAME_LENGTH = 12;
	private static final int USER_PASSWORD_LENGTH = 24;
	private static final int USER_SSN_LENGTH = 28;
	private static final int USER_EMAIL_LENGTH = 50;
	private static final int USER_PHONE_LENGTH = 26;

	public static final int RECORD_LENGTH = USER_ID_LENGTH + USER_NAME_LENGTH + USER_NICKNAME_LENGTH
			+ USER_PASSWORD_LENGTH + USER_SSN_LENGTH + USER_EMAIL_LENGTH + USER_PHONE_LENGTH;

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

	/* 신규 등록 */
	public void addUser(User user) throws IOException {
		// 새로운 레코드 추가
		randomAccessFile.seek((userCount * RECORD_LENGTH) + RECORD_COUNT_LENGTH);

		String userId = user.getId();
		String userName = user.getName();
		String userNickName = user.getNickName();
		String userPasswd = user.getPasswd();
		String userSsn = user.getSsn();
		String userEmaile = user.getEmail();
		String userPhoneNum = user.getPhoneNumber();

		int charCount = userId.length(); // 20바이트(10글자)로 ID 저장
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

	/** user 삭제 */
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

	/** id로검색 */
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

	/** passwd로검색 */
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
