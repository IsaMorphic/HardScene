package org.skorrloregaming.hardscene.server.thread;

import java.nio.charset.StandardCharsets;

import org.skorrloregaming.hardscene.server.HardScene;
import org.skorrloregaming.hardscene.server.event.ClientDisconnectEvent;
import org.skorrloregaming.hardscene.server.interfaces.Client;

public class HardScene_ListenThread implements Runnable {

	Client client = null;

	public HardScene_ListenThread(Client client) {
		this.client = client;
	}

	@Override
	public void run() {
		while (HardScene.running) {
			try {
				byte[] messageBytes = new byte[512];
				int returnValue = client.socket.getInputStream().read(messageBytes, 0, messageBytes.length);
				if (returnValue == -1)
					break;
				String message = new String(messageBytes, StandardCharsets.UTF_8).trim();
				if (client.unsupportedClient) {
					message = client.name + ": " + message;
				}
				if (returnValue == 0 || message != "") {
					System.out.println(client.address.toString() + " (" + client.id + "): " + message);
					HardScene.broadcast(message);
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
