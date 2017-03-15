package me.skorrloregaming.hardscene.event;

import java.io.IOException;

import me.skorrloregaming.hardscene.HardScene;
import me.skorrloregaming.hardscene.interfaces.Client;
import me.skorrloregaming.hardscene.interfaces.Logger;

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
			Logger.info(client.address + " has quit the server.");
			if (HardScene.instance.clients.containsKey(client.id)) {
				HardScene.instance.clients.remove(client.id);
			}
			String message = client.name + " has quit the server.";
			Logger.info(message);
			HardScene.broadcast(message);
		}
	}

}
