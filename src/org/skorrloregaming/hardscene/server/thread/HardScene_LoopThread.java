package org.skorrloregaming.hardscene.server.thread;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.skorrloregaming.hardscene.server.HardScene;
import org.skorrloregaming.hardscene.server.event.ClientConnectEvent;
import org.skorrloregaming.hardscene.server.event.ClientDisconnectEvent;
import org.skorrloregaming.hardscene.server.impl.ClientImpl;

public class HardScene_LoopThread implements Runnable{
	
	@Override
	public void run() {
		Socket socket = null;
		while (HardScene.running){
			try {
				socket = HardScene.server.accept();
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			Random random = new Random();
			byte[] messageBytes = null;
			try {
				messageBytes = new byte[24];
				int returnConnectivity = -1;
				try{
					returnConnectivity = socket.getInputStream().read(messageBytes, 0, messageBytes.length);
				}catch (Exception ignored){}
				if (returnConnectivity == -1){
					System.out.println(socket.getRemoteSocketAddress().toString() + " closed its socket before it could be processed.");
				}else{
					String name = new String(messageBytes, StandardCharsets.UTF_8);
					ClientImpl client = new ClientImpl(socket, random.nextInt(10000), name.trim());
					if (HardScene.clients.size() > HardScene.config.maxClients){
						System.out.println(client.address +" has been denied access to connect due to the max clients threshold.");
						try {
							new ClientDisconnectEvent(client, true);
						} catch (Exception ignored) {}
					}else if (HardScene.bannedManager.propertyExists(client.address)){
						System.out.println(client.address +" has been denied access to connect due to being banned.");
						try {
							new ClientDisconnectEvent(client, true);
						} catch (Exception ignored) {}
					}else{
						try {
							new ClientConnectEvent(client);
						} catch (IOException e) {
							e.printStackTrace();
						}
						Thread acceptIncomingSignals = new Thread(new HardScene_ListenThread(client));
						acceptIncomingSignals.start();
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}