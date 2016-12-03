package org.skorrloregaming.hardscene.server.event;

import java.io.IOException;

import org.skorrloregaming.hardscene.server.HardScene;
import org.skorrloregaming.hardscene.server.impl.ClientImpl;

public class ClientConnectEvent {
	
	public ClientConnectEvent(ClientImpl client) throws IOException{
		new ClientConnectEvent(client, false);
	}
	
	public ClientConnectEvent(ClientImpl client, boolean direct) throws IOException{
		if (!direct){
			if (!HardScene.clients.containsKey(client.id)){
				HardScene.clients.put(client.id, client);	
			}
		}
		System.out.println(client.address + " has joined the server.");
		String message = client.displayName + " has joined the server.";
		System.out.println(message);
		HardScene.broadcast(message);
	}
	
}
