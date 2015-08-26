package kr.or.kosta.koback.common;
/**
 * 해당 메신저에서 사용할 프로토콜
 * @author 유안상
 */
public interface MessageType {
   // 해당 메신저의 구분자
   String DELIMETER = "ㆌㅱ";
   
   // 해당 메신저의 프로토콜
   String C_INITIAL_CONNECT = "99";
   String C_LOGIN = "100";
   String S_LOGIN_RESULT = "101";
   String C_JOIN = "102";
   String S_JOIN_RESULT = "103";
   String C_OPEN = "200";
   String S_OPEN_RESULT = "201";
   String C_SECRET_OPEN = "202";
   String S_SECRET_RESULT = "203";
   String C_ENTRY = "210";
   String S_ENTRY_RESULT = "211";
   String C_SECRET_ENTRY = "212";
   String S_SECRET_ENTRY_RESULT = "213";
   String C_INVITE_ENTRY = "214";
   String C_LOGOUT = "230";
   String SC_CHAT_MESSAGE = "300";
   String SC_CHAT_EMOTICON = "301";
   String SC_WHISPER = "302";
   String C_INVITE_USER = "303";
   String S_INVITE_RESULT = "304";
   String C_INVITE_CONFIRM = "305";
   String C_KICK = "306";
   String S_KICK = "307";
   String SC_EXIT = "308";
   String SC_REQUEST_WAITING_LIST = "309";
   String SC_REQUEST_WAITING_ROOMLIST = "310";
   String S_REQUEST_WAITING_LIST = "311";
   String S_REQUEST_WAITING_ROOMLIST = "312";
   String S_SELECTED_ROOM_USERLIST = "313";
   String S_NEXT_MASTER = "314";
   String C_INVITE_RESULT = "315";
   String SC_REQUEST_ROOM_INFO = "316";
   String C_UPLOAD = "400";
   String S_UPLOAD_FAIL = "401";
   String S_UPLOAD_ADD = "402";
   String S_UPLOAD_RESULT = "403";
   String C_SHOW_FILE_LIST = "404";
   String S_SHOW_FILE_LIST = "405";
   String S_ALLFILE_LIST = "410";
   String C_DOWNLOAD = "420";
   String S_DOWNLOAD_FAIL = "421";
   String S_FILE_DOWNLOAD = "422";
   String S_NOTICE = "500";
   String S_ADMIN_WHISPER = "501";
   String S_ERROR_MESSAGE = "600";
   String S_INVITE_ENTRY = "215";
   
   String NO_RESPONSE = "서버에 응답이 없습니다.";
   String WRONG_LOGIN_PASS = "비밀번호가 맞지 않습니다.";
   String WRONG_ID = "존재하지 않는 아이디입니다.";
   String DUPLICATE_ID = "중복된 아이디입니다.";
   String WRONG_SSN = "유효하지 않는 주민등록번호입니다.";
   String DUPLICATE_SSN = "중복된 주민등록번호입니다.";
   String WRONG_NICK = "유효하지 않는 닉네임입니다.";
   String DUPLICATE_NICK = "중복된 닉네임입니다.";
   String DUPLICATE_EMAIL = "중복된 이메일입니다.";
   String OVER_ROOMTITLE = "방 제목은 20자 이하만 가능합니다.";
   String OVER_MAXROOM = "더 이상 채팅방을 생성할 수 없습니다.";
   String OVER_MAXUSER = "방이 꽉찼습니다.";
   String GONE_CHAT_ROOM = "입장하려는 채팅방이 존재하지 않습니다.";
   String ONLY_NUMBER_PASS = "채팅방 비밀번호는 숫자만 가능합니다.";
   String WRONG_ROOM_PASS = "유효한 비밀번호가 아닙니다.";
   String NOT_EXIST_USER = "존재하지 않는 상대방입니다.";
   String REJECT_INVITE = "초대받은 상대가 거절하였습니다.";
   String OVER_FILESIZE = "100MB 이내의 파일만 업로드 가능합니다.";
   String ISNULL_INPUT = "입력되지 않았습니다";
   
   String LIMIT_LENGTH_ID = "ID를 한글을 제외한 , 5자 이상 , 10자 이하로 설정해주세요";   
   String LIMIT_LENGTH_PASS = "비밀번호를 7자 이상 , 12자 이하로 설정해주세요";
   String LIMIT_LENGTH_NICK = "닉네임을 3자 이상 , 10자 이하로 설정해주세요.";   
   String WRONG_EMAIL = "유효하지 않는 이메일입니다.";
   String WRONG_PHONENUM = "유효하지 않는 연락처입니다.";
   String LIMIT_LENGTH_NAME = "이름을 제대로 입력해주세요.";
}