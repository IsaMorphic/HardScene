package org.skorrloregaming.hardscene.server.interfaces;

import java.net.Socket;

public class Client {
	
	public Socket socket = null;
	public String address = "/0.0.0.0";
	public Integer id = 0;
	public String name = "unspecified";
	
	public Client(Socket socket, Integer id, String name){
		this.socket = socket;
		this.address = socket.getRemoteSocketAddress().toString().split(":")[0];
		this.id = id;
		this.name = name;
	}
	
}
