package me.skorrloregaming.hardscene.event;

import java.io.IOException;

import me.skorrloregaming.hardscene.HardScene;
import me.skorrloregaming.hardscene.interfaces.Client;
import me.skorrloregaming.hardscene.interfaces.Logger;

public class ClientAuthenticateEvent {

	public ClientAuthenticateEvent(Client client) throws IOException {
		Logger.info(client.address + " has authenticated with the server.");
		String message = client.name + " has authenticated with the server.";
		Logger.info(message);
		HardScene.broadcast(message);
	}

}
