package kr.or.kosta.koback.model;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import kr.or.kosta.koback.server.ChatService;

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
			System.out.println(st);
			sb.append(st+",");
		}
		return sb.toString();
	}

}
