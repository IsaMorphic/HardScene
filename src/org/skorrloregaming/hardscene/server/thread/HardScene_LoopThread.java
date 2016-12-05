package org.skorrloregaming.hardscene.server.thread;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.skorrloregaming.hardscene.server.HardScene;
import org.skorrloregaming.hardscene.server.event.ClientConnectEvent;
import org.skorrloregaming.hardscene.server.interfaces.Client;

public class HardScene_LoopThread implements Runnable {

	@Override
	public void run() {
		Socket socket = null;
		while (HardScene.running) {
			try {
				socket = HardScene.server.accept();
			} catch (Exception ig) {
				break;
			}
			Random ran = new Random();
			byte[] messageBytes = new byte[24];
			try {
				if (socket.getInputStream().read(messageBytes) == -1) {
					System.out.println(socket.getRemoteSocketAddress().toString()
							+ " closed its socket before it could be processed.");
				} else {
					String name = new String(messageBytes, StandardCharsets.UTF_8);
					String token = "";
					try {
						token = name.trim().split("~!")[1];
					} catch (Exception ig) {
					}
					Client client = new Client(socket, ran.nextInt(900) + 100, name.trim().split("~!")[0], token);
					if (HardScene.clients.size() > HardScene.config.maxClients) {
						System.out.println(client.address
								+ " has been denied access to connect due to the max clients threshold.");
						client.closeTunnel();
					} else if (HardScene.bannedManager.propertyExists(client.address)) {
						System.out.println(client.address + " has been denied access to connect due to being banned.");
						client.closeTunnel();
					} else {
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