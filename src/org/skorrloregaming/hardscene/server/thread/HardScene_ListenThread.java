package org.skorrloregaming.hardscene.server.thread;

import java.nio.charset.StandardCharsets;

import org.skorrloregaming.hardscene.server.HardScene;
import org.skorrloregaming.hardscene.server.event.ClientDisconnectEvent;
import org.skorrloregaming.hardscene.server.impl.ClientImpl;

public class HardScene_ListenThread implements Runnable{

	ClientImpl client = null;
	
	public HardScene_ListenThread(ClientImpl client){
		this.client = client;
	}
	
	@Override
	public void run() {
		while (HardScene.running){
				try {
					if (client.socket.getInputStream().read() == -1) break;
					byte[] messageBytes = new byte[client.socket.getInputStream().available()];
					client.socket.getInputStream().read(messageBytes, 0, messageBytes.length);
					String message = new String(messageBytes, StandardCharsets.UTF_8);
					System.out.println(client.address.toString()+" ("+client.id+"): "+message);
					HardScene.broadcast(new String(messageBytes), true);
				} catch (Exception e){
					break;
				}
		}
	    try {
			new ClientDisconnectEvent(client);
		} catch (Exception ignored) {}
	}
	
}
