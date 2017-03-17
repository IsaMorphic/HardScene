package me.skorrloregaming.hardscene.http;

import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class WebSocketClient {

	public Socket socket;
	public Integer id;
	public String name = "WebClient";
	public String token = "";

	public WebSocketClient(Socket socket, Integer id) {
		this.socket = socket;
		this.id = id;
	}
	
	public WebSocketClient(Socket socket, Integer id, String name, String token) {
		this.socket = socket;
		this.id = id;
		this.name = name;
		this.token = token;
	}

	public String readMessage() {
		try {
			byte[] b = new byte[512];
			int len = socket.getInputStream().read(b);
			if (len == -1)
				return "-1";
			byte rLength = 0;
			int rMaskIndex = 2;
			int rDataStart = 0;
			byte data = b[1];
			byte op = (byte) 127;
			rLength = (byte) (data & op);
			if (rLength == (byte) 126)
				rMaskIndex = 4;
			if (rLength == (byte) 127)
				rMaskIndex = 10;
			byte[] masks = new byte[4];
			int j = 0;
			int i = 0;
			for (i = rMaskIndex; i < (rMaskIndex + 4); i++) {
				masks[j] = b[i];
				j++;
			}
			rDataStart = rMaskIndex + 4;
			int messLen = len - rDataStart;
			byte[] message = new byte[messLen];
			for (i = rDataStart, j = 0; i < len; i++, j++) {
				message[j] = (byte) (b[i] ^ masks[j % 4]);
			}
			return new String(message, StandardCharsets.UTF_8);
		} catch (Exception ex) {
			return "null";
		}
	}

	public boolean sendMessage(String msg) {
		try {
			byte[] response;
			byte[] bytesRaw = msg.getBytes(Charset.forName("UTF-8"));
			byte[] frame = new byte[10];
			int indexStartRawData = -1;
			int length = bytesRaw.length;
			frame[0] = (byte) 129;
			if (length <= 125) {
				frame[1] = (byte) length;
				indexStartRawData = 2;
			} else if (length >= 126 && length <= 65535) {
				frame[1] = (byte) 126;
				frame[2] = (byte) ((length >> 8) & 255);
				frame[3] = (byte) (length & 255);
				indexStartRawData = 4;
			} else {
				frame[1] = (byte) 127;
				frame[2] = (byte) ((length >> 56) & 255);
				frame[3] = (byte) ((length >> 48) & 255);
				frame[4] = (byte) ((length >> 40) & 255);
				frame[5] = (byte) ((length >> 32) & 255);
				frame[6] = (byte) ((length >> 24) & 255);
				frame[7] = (byte) ((length >> 16) & 255);
				frame[8] = (byte) ((length >> 8) & 255);
				frame[9] = (byte) (length & 255);
				indexStartRawData = 10;
			}
			response = new byte[indexStartRawData + length];
			int i, reponseIdx = 0;
			for (i = 0; i < indexStartRawData; i++) {
				response[reponseIdx] = frame[i];
				reponseIdx++;
			}
			for (i = 0; i < length; i++) {
				response[reponseIdx] = bytesRaw[i];
				reponseIdx++;
			}
			socket.getOutputStream().write(response);
			socket.getOutputStream().flush();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

}
