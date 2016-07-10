package org.skorrloregaming.hardscene.server.thread;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.skorrloregaming.hardscene.server.HardScene;
import org.skorrloregaming.hardscene.server.event.ClientConnectEvent;
import org.skorrloregaming.hardscene.server.event.ClientDisconnectEvent;
import org.skorrloregaming.hardscene.server.event.impl.ClientImpl;

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
			String arrayString = " [*] Welcome, this server runs on "+HardScene.frameName;
			byte[] array = arrayString.getBytes();
			try {
				socket.getOutputStream().write(array, 0, array.length);
				socket.getOutputStream().flush();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			Random random = new Random();
			byte[] messageBytes = null;
			try {
				messageBytes = new byte[18];
				if (socket.getInputStream().read() == -1){
					System.out.println(socket.getRemoteSocketAddress().toString() + ": Socket was closed before it could be read.");
				}else{
					socket.getInputStream().read(messageBytes, 0, messageBytes.length);
					String name = new String(messageBytes, StandardCharsets.UTF_8);
					ClientImpl client = new ClientImpl(socket, random.nextInt(10000), name);
					if (HardScene.clients.size() > HardScene.config.maxClients){
						System.out.println(client.address +" has been denied access to connect due to the max clients threshold.");
						try {
							new ClientDisconnectEvent(client, true);
						} catch (Exception ignored) {}
					}else if (HardScene.suppressionHelper.resolve(client)){
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
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}

}
