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
			// SEND THE HASH CODE : SECURITY FEATURE
			try {
				String[] hashBytesOrig = HardScene.config.hash.split("/");
				byte[] hashBytes = new byte[4];
				for (int i = 0; i < hashBytesOrig.length; i++){
					hashBytes[i] = Byte.parseByte(hashBytesOrig[i]);
				}
				socket.getOutputStream().write(hashBytes, 0, hashBytes.length);
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
				messageBytes = new byte[24];
				if (socket.getInputStream().read() == -1){
					System.out.println(socket.getRemoteSocketAddress().toString() + ": Socket was closed before it could be read.");
				}else{
					socket.getInputStream().read(messageBytes, 0, messageBytes.length);
					String name = new String(messageBytes, StandardCharsets.UTF_8);
					ClientImpl client = null;
					try{
						client = new ClientImpl(socket, random.nextInt(10000), name.split("\\$")[1]);	
					}catch (Exception ignored){
						System.out.println("Debug : Incoming client fails to meet the new protocol requirements, reverting..");
						client = new ClientImpl(socket, random.nextInt(10000), name);
					}
					// GET THE CLIENT TOKEN : SECURITY FEATURE
					String returnToken = messageBytes[0] + "/"+ messageBytes[1] + "/"+ messageBytes[2] + "/"+ messageBytes[3];
					if (HardScene.clients.size() > HardScene.config.maxClients){
						System.out.println(client.address +" has been denied access to connect due to the max clients threshold.");
						try {
							new ClientDisconnectEvent(client, true);
						} catch (Exception ignored) {}
					}else if (!returnToken.equals(HardScene.config.hash)){
						System.out.println(client.address +" has been denied access to connect due to an invalid token : "+returnToken);
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