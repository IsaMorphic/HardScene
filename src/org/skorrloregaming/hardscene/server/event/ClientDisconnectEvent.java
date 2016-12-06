package org.skorrloregaming.hardscene.server.event;

import java.io.IOException;

import org.skorrloregaming.hardscene.server.HardScene;
import org.skorrloregaming.hardscene.server.interfaces.Client;

public class ClientDisconnectEvent {

	public ClientDisconnectEvent(Client client) throws IOException {
		new ClientDisconnectEvent(client, false);
	}

	public ClientDisconnectEvent(Client client, boolean direct) throws IOException {
		try {
			client.socket.close();
		} catch (Exception ignored) {
		}
		if (!direct) {
			System.out.println(client.address + " has quit the server.");
			if (HardScene.clients.containsKey(client.id)) {
				HardScene.clients.remove(client.id);
			}
			String message = client.name + " has quit the server.";
			System.out.println(message);
			HardScene.broadcast(message);
		}
	}

}
