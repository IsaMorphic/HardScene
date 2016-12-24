package org.skorrloregaming.hardscene.server.thread;

import java.nio.charset.StandardCharsets;

import org.skorrloregaming.hardscene.server.HardScene;
import org.skorrloregaming.hardscene.server.event.ClientDisconnectEvent;
import org.skorrloregaming.hardscene.server.interfaces.Client;
import org.skorrloregaming.hardscene.server.interfaces.Logger;

public class HardScene_ListenThread implements Runnable {

	Client client = null;

	public HardScene_ListenThread(Client client) {
		this.client = client;
	}

	private int lastMessageSecond = 0;
	private int spamStrike = 0;

	@Override
	public void run() {
		while (HardScene.running) {
			try {
				byte[] messageBytes = new byte[512];
				int returnValue = client.socket.getInputStream().read(messageBytes, 0, messageBytes.length);
				if (returnValue == -1)
					break;
				String rawMessage = new String(messageBytes, StandardCharsets.UTF_8).trim();
				String message = rawMessage;
				if (lastMessageSecond == (int) (System.currentTimeMillis() / 500)) {
					spamStrike++;
					if (spamStrike >= 2) {
						client.sendMessage("You are not allowed to spam in the server chat.");
						client.closeTunnel();
					}
				} else {
					lastMessageSecond = (int) (System.currentTimeMillis() / 500);
					spamStrike = 0;
					if (client.unsupportedClient) {
						message = client.name + ": " + message;
					}
					if (returnValue != 0 && rawMessage.length() != 0) {
						Logger.info(client.address.toString() + " (" + client.id + "): " + message);
						HardScene.broadcast(message);
					}
				}
			} catch (Exception e) {
				break;
			}
		}
		try {
			new ClientDisconnectEvent(client);
		} catch (Exception ignored) {
		}
	}

}
