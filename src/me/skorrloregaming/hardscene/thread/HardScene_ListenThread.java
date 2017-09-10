package me.skorrloregaming.hardscene.thread;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import me.skorrloregaming.hardscene.HardScene;
import me.skorrloregaming.hardscene.event.ClientDisconnectEvent;
import me.skorrloregaming.hardscene.interfaces.Client;
import me.skorrloregaming.hardscene.interfaces.Logger;

public class HardScene_ListenThread implements Runnable {

	Client client = null;

	public HardScene_ListenThread(Client client) {
		this.client = client;
	}

	private int lastMessageSecond = 0;
	private int spamStrike = 0;

	@Override
	public void run() {
		// START: Authentication system (3.17.2017)
		if (HardScene.config.doRequireInfo) {
			HardScene_AuthThread authThread = new HardScene_AuthThread(client);
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
					client.socket.close();
				} catch (IOException e1) {
				}
				try {
					new ClientDisconnectEvent(client);
				} catch (Exception ignored) {
				}
				return;
			}
		}
		// END: Authentication system (3.17.2017)
		while (HardScene.running) {
			try {
				byte[] messageBytes = new byte[512];
				int returnValue = client.socket.getInputStream().read(messageBytes, 0, messageBytes.length);
				if (returnValue == -1)
					break;
				String rawMessage = new String(messageBytes, StandardCharsets.UTF_8).trim();
				String message = rawMessage;
				if (HardScene.config.enableSwearFilter)
					message = HardScene.instance.processAntiSwear(client, message);
				if (HardScene.config.colorCodes) {
					message = message.replace("&", "§");
				} else {
					message.replace("§", "");
				}
				if (message.startsWith("/") && HardScene.config.doRequireInfo) {
					HardScene_AuthThread.handleCommand(client, message);
				} else {
					if (lastMessageSecond == (int) (System.currentTimeMillis() / 1000)) {
						spamStrike++;
						if (spamStrike >= 3) {
							client.sendMessage("You are not allowed to spam in the server chat.");
							client.closeTunnel();
						}
					} else {
						lastMessageSecond = (int) (System.currentTimeMillis() / 1000);
						spamStrike = 0;
					}
					if (returnValue != 0 && rawMessage.length() != 0) {
						Logger.info(client.address.toString() + " (" + client.id + "): " + client.name + ": " + message);
						message = HardScene.config.messageFormat.replace("{client}", client.name).replace("{message}", message);
						message = message.replace("Â", "");
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
