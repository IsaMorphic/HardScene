package me.skorrloregaming.hardscene.http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;

import javax.xml.bind.DatatypeConverter;

import me.skorrloregaming.hardscene.HardScene;
import me.skorrloregaming.hardscene.event.ClientDisconnectEvent;
import me.skorrloregaming.hardscene.interfaces.Client;
import me.skorrloregaming.hardscene.interfaces.Logger;
import me.skorrloregaming.hardscene.thread.HardScene_AuthThread;

@SuppressWarnings("unused")
public class WebSocket implements Runnable {

	private Socket socket;

	private final String[] header;
	private final String infoHeader;
	private final String type;
	private final String resource;
	private WebSocketClient wsc;

	private int lastMessageSecond = 0;
	private int spamStrike = 0;

	public WebSocket(Socket socket, String[] header, Integer id) {
		this.socket = socket;
		this.header = header;
		this.infoHeader = header[0];
		this.type = infoHeader.split("/")[0].replace(" ", "").toLowerCase();
		this.resource = "/" + infoHeader.split("/")[1].replace(" HTTP", "").toLowerCase();
		this.wsc = new WebSocketClient(socket, id);
	}

	public WebSocketClient getWebSocketClient() {
		return this.wsc;
	}

	public Client getClientAlternative() {
		return new Client(socket, wsc.id, wsc.name, wsc.token, false, true);
	}

	public boolean bind() {
		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeBytes("HTTP/1.1 101 Switching Protocols\r\n");
			out.writeBytes("Upgrade: websocket\r\n");
			out.writeBytes("Connection: Upgrade\r\n");
			String key = "";
			for (String str : header) {
				if (str.split(":")[0].equals("Sec-WebSocket-Key")) {
					key = str.split(":")[1].replaceFirst(" ", "");
				}
			}
			String value = "Sec-WebSocket-Accept: " + DatatypeConverter.printBase64Binary(MessageDigest.getInstance("SHA-1").digest((key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")));
			Logger.info("WebSocket (" + HardScene.formatAddress(socket) + "): " + value);
			out.writeBytes(value + "\r\n\r\n");
			out.flush();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public boolean readLogin() {
		String ret = wsc.readMessage();
		if (ret.equals("null") || ret.equals("-1")) {
			Logger.info(HardScene.formatAddress(socket) + " closed its socket before it could be processed.");
			return false;
		} else {
			String name = ret.split("~!")[0];
			String token = "";
			try {
				token = ret.split("~!")[1];
			} catch (Exception ig) {
			}
			this.wsc = new WebSocketClient(socket, wsc.id, name, token);
			return true;
		}
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		// START: Authentication system (3.17.2017)
		HardScene_AuthThread authThread = new HardScene_AuthThread(getClientAlternative());
		Thread authThreadObj = new Thread(authThread);
		authThreadObj.start();
		while (!authThread.isComplete) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (authThread.incomplete) {
			try {
				socket.close();
			} catch (IOException e1) {
			}
			try {
				new ClientDisconnectEvent(getClientAlternative());
			} catch (Exception ignored) {
			}
			return;
		}
		// END: Authentication system (3.17.2017)
		try {
			while (HardScene.running) {
				String rawMessage = wsc.readMessage();
				if (rawMessage.length() == 2 && rawMessage.getBytes()[0] == 3 && rawMessage.getBytes()[1] == 63)
					break;
				if (rawMessage.equals("null") || rawMessage.equals("-1"))
					break;
				if (HardScene.config.colorCodes) {
					rawMessage = rawMessage.replace("&", "§");
				} else {
					rawMessage.replace("§", "");
				}
				if (lastMessageSecond == (int) (System.currentTimeMillis() / 500)) {
					spamStrike++;
					if (spamStrike >= 2) {
						wsc.sendMessage("You are not allowed to spam in the server chat.");
						socket.close();
					}
				} else {
					lastMessageSecond = (int) (System.currentTimeMillis() / 500);
					spamStrike = 0;
					Logger.info(HardScene.formatAddress(socket) + " (" + wsc.id + "): " + rawMessage);
					rawMessage = HardScene.config.messageFormat.replace("{client}", wsc.name).replace("{message}", rawMessage);
					rawMessage = rawMessage.replace("Â","");
					HardScene.broadcast(rawMessage);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			socket.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			new ClientDisconnectEvent(getClientAlternative());
		} catch (Exception ignored) {
		}
	}
}
