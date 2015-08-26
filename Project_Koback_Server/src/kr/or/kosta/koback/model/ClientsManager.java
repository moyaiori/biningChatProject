package kr.or.kosta.koback.model;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import kr.or.kosta.koback.server.ChatService;
/**
 * 메신저에 접속한 유저들을 관리하는 클래스 추상화
 * @author 유안상
 */
public class ClientsManager {
	private Hashtable<String, ChatService> clients;
	
	public ClientsManager(){
		clients = new Hashtable<>();
	}
	
	public void addClient(String userID, ChatService client){
		clients.put(userID, client);
	}
	public void removeClient(ChatService client){}
//	public List<User> getAllUserList(){}

	public Hashtable<String, ChatService> getClients() {
		return clients;
	}

	public void setClients(Hashtable<String, ChatService> clients) {
		this.clients = clients;
	}
	
	public String getAllId(){
		Enumeration<String> e = clients.keys();
		StringBuffer sb = new StringBuffer();
		while (e.hasMoreElements()) {
			String st = (String) e.nextElement();
			sb.append(st+",");
		}
		return sb.toString();
	}

}
