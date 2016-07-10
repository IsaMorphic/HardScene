package org.skorrloregaming.hardscene.server.event.impl;

import java.net.Socket;

public class ClientImpl {
	
	public Socket socket = null;
	public String address = "/0.0.0.0";
	public Integer id = 0;
	public String displayName = "unspecified";
	
	public ClientImpl(Socket socket, Integer id, String name){
		this.socket = socket;
		this.address = socket.getRemoteSocketAddress().toString();
		this.id = id;
		this.displayName = name;
	}
	
}
