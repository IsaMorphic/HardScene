package me.skorrloregaming.hardscene.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.zip.ZipException;

import me.skorrloregaming.hardscene.HardScene;
import me.skorrloregaming.hardscene.interfaces.Logger;

@SuppressWarnings("unused")
public class WebServer implements Runnable {

	private Socket socket;

	private final String[] header;
	private final String infoHeader;
	private final String type;
	private final String resource;

	public WebServer(Socket socket, String[] header) {
		this.socket = socket;
		this.header = header;
		this.infoHeader = header[0];
		this.type = infoHeader.split("/")[0].replace(" ", "").toLowerCase();
		this.resource = "/" + infoHeader.split("/")[1].replace(" HTTP", "").toLowerCase();
	}

	public void bind() {
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			Logger.info("WebServer (" + HardScene.formatAddress(socket) + "): " + infoHeader);
			out.writeBytes("HTTP/1.1 200 OK\r\n");
			out.writeBytes("Content-Type: text/html\r\n\r\n");
			InputStream in = getClass().getResourceAsStream("www.html");
			try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
				String str;
				while ((str = br.readLine()) != null) {
					if (str.contains("Auth Token")) {
						str = "<p><input style=\"width: 200px; border-radius: 5px;\" type=\"password\" id=\"name\" name=\"token\" value=\"\" placeholder=\"Auth Token\" disabled></p>";
					}
					if (str.contains("<script>") && type.equals("post")) {
						for (int i = 0; i < header.length; i++) {
							if (header[i].equals("")) {
								String match = header[i + 1];
								str = str + "\r\n var name = '" + match.split("=")[1].split("&")[0] + "';";
								try {
									str = str + "\r\n var token = '" + match.split("=")[2].split("&")[0] + "';";
								} catch (Exception ig) {
									str = str + "\r\n var token = '';";
								}
							}
						}
					}
					try {
						out.writeBytes(str + "\r\n");
					} catch (IOException e) {
					}
				}
			} catch (ZipException e) {
				Logger.info("Failed to retrieve default website template, possible memory leak ?");
				out.writeBytes("Failed to retrieve default website template, possible memory leak ?");
			}
			out.flush();
			socket.close();
		} catch (Exception e) {
			Logger.info(HardScene.formatAddress(socket) + " closed its socket before it could be processed.");
			try {
				socket.close();
			} catch (IOException e1) {
			}
		}
	}

}
