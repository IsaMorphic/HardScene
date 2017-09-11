package me.skorrloregaming.hardscene.event;

import java.io.IOException;

import me.skorrloregaming.hardscene.HardScene;
import me.skorrloregaming.hardscene.interfaces.Client;
import me.skorrloregaming.hardscene.interfaces.Logger;

public class ClientConnectEvent {

	public ClientConnectEvent(Client client) throws IOException {
		new ClientConnectEvent(client, false);
	}

	public ClientConnectEvent(Client client, boolean direct) throws IOException {
		if (!direct) {
			if (!HardScene.instance.clients.containsKey(client.id)) {
				HardScene.instance.clients.put(client.id, client);
			}
		}
		Logger.info(client.address + " has joined the server.");
		String message = HardScene.config.loginFormat.replace("{client}", client.name);
		message = message.replace("", "");
		Logger.info(message);
		HardScene.broadcast(message);
	}

}
