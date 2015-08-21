package kr.or.kosta.koback.model;
/**
 * 
 * @author 윤성훈
 *
 */
public class User {
	
	/** 유저의 속성 */
	private String id, name, nickName, passwd;
	private String ssn, email, phoneNumber;
	
	/** 생성자 오버로딩 */
	public User(){
		this(null, null, null, null, null, null, null);
	}
	public User(String id, String name, String nickName, String passwd, String ssn, String email, String phoneNumber){
		this.id= id;
		this.name = name;
		this.nickName= nickName;
		this.passwd = passwd;
		this.ssn = ssn;
		this.email = email;
		this.phoneNumber = phoneNumber;
	}
	
	/** setter, getter 메소드 정의 */
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getNickName() {
		return nickName;
	}
	public String getPasswd() {
		return passwd;
	}
	public String getSsn() {
		return ssn;
	}
	public String getEmail() {
		return email;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public void setSsn(String ssn) {
		this.ssn = ssn;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", nickName=" + nickName + ", passwd=" + passwd + ", ssn=" + ssn
				+ ", email=" + email + ", phoneNumber=" + phoneNumber + "]";
	}
	
	
	
}
