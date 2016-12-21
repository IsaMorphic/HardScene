package org.skorrloregaming.hardscene.server.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.skorrloregaming.hardscene.server.HardScene;
import org.skorrloregaming.hardscene.server.interfaces.Client;

@SuppressWarnings("unused")
public class WebServer implements Runnable {

	private Socket socket;

	private final String[] completeHeader;
	private final String header;
	private final String type;
	private final String resource;

	public WebServer(Socket socket, String[] completeHeader) {
		this.socket = socket;
		Thread thread = new Thread(this);
		thread.start();
		this.completeHeader = completeHeader;
		this.header = completeHeader[0];
		this.type = header.split("/")[0].replace(" ", "").toLowerCase();
		this.resource = "/" + header.split("/")[1].replace(" HTTP", "").toLowerCase();
	}

	@Override
	public void run() {
		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			System.out.println("WebServer: HTTP/1.1 200 OK");
			out.writeBytes("HTTP/1.1 200 OK\r\n");
			out.writeBytes("Content-Type: text/html\r\n\r\n");
			InputStream in = getClass().getResourceAsStream("www.html");
			try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			    String str;
			    while ((str = br.readLine()) != null) {
			    	if (str.contains("<script>") && header.contains("POST")) {
						for (int i = 0; i < completeHeader.length; i++) {
							if (completeHeader[i].equals("")) {
								String match = completeHeader[i + 1];
								str = str + "\r\n var name = '" + match.split("=")[1].split("&")[0] + "';";
								str = str + "\r\n var token = '" + match.split("=")[2].split("&")[0] + "';";
							}
						}
					}
					try {
						out.writeBytes(str + "\r\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
			    }
			}
			out.flush();
			socket.close();
		} catch (Exception e) {
			System.out.println(HardScene.formatAddress(socket) + " closed its socket before it could be processed.");
		}
	}

}
