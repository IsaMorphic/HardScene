package org.skorrloregaming.hardscene.server.event;

import java.io.IOException;

import org.skorrloregaming.hardscene.server.HardScene;
import org.skorrloregaming.hardscene.server.interfaces.Client;
import org.skorrloregaming.hardscene.server.interfaces.Logger;

public class ClientConnectEvent {

	public ClientConnectEvent(Client client) throws IOException {
		new ClientConnectEvent(client, false);
	}

	public ClientConnectEvent(Client client, boolean direct) throws IOException {
		if (!direct) {
			if (!HardScene.clients.containsKey(client.id)) {
				HardScene.clients.put(client.id, client);
			}
		}
		Logger.info(client.address + " has joined the server.");
		String message = client.name + " has joined the server.";
		Logger.info(message);
		HardScene.broadcast(message);
	}

}
