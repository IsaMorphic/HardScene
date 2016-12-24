package org.skorrloregaming.hardscene.server.interfaces;

import java.io.IOException;
import java.net.Socket;

import org.skorrloregaming.hardscene.server.http.WebSocketClient;

public class Client {

	public Socket socket = null;
	public String address = "0.0.0.0";
	public Integer id = 0;
	public String name = "unspecified";
	public String token = "unspecified";
	public boolean unsupportedClient = false;
	public boolean webBased = false;

	public Client(Socket socket, Integer id, String name, String token, boolean unsupportedClient, boolean webBased) {
		this.socket = socket;
		this.address = socket.getRemoteSocketAddress().toString().split(":")[0].replace("/", "");
		if (this.address.equals("0")) this.address = "127.0.0.1";
		this.id = id;
		this.name = name;
		this.token = token;
		this.unsupportedClient = unsupportedClient;
		this.webBased = webBased;
	}

	public boolean sendMessage(String msg) {
		if (webBased) {
			WebSocketClient wsc = new WebSocketClient(socket, id, name, token);
			return wsc.sendMessage(msg);
		}
		try {
			socket.getOutputStream().write(msg.getBytes());
			socket.getOutputStream().flush();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public boolean closeTunnel() {
		try {
			socket.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
