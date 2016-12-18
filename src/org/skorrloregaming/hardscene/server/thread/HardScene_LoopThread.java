package org.skorrloregaming.hardscene.server.thread;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.skorrloregaming.hardscene.server.HardScene;
import org.skorrloregaming.hardscene.server.event.ClientConnectEvent;
import org.skorrloregaming.hardscene.server.interfaces.Client;

public class HardScene_LoopThread implements Runnable {
	
	private String unsupportedAuthentication(Socket socket) throws Exception {
		String na = "na";
		boolean patternMatch = false;
		while (na.length() > 14 || na.length() < 4 || patternMatch) {
			socket.getOutputStream().write("Display Name: ".getBytes());
			socket.getOutputStream().flush();
			byte[] nameBytes = new byte[24];
			socket.getInputStream().read(nameBytes);
			na = new String(nameBytes, StandardCharsets.UTF_8).trim();
			na = na.replace("~!", "");
			Pattern pattern = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
			Matcher m = pattern.matcher(na.replace("_", ""));
			patternMatch = m.find();
			if (na.length() > 14 || na.length() < 4) {
				socket.getOutputStream().write((System.lineSeparator() + "Please specify a name with a length between 4 and 16.").getBytes());
				socket.getOutputStream().flush();
			} else if (patternMatch) {
				socket.getOutputStream().write((System.lineSeparator() + "Invalid display name syntax, please try again.").getBytes());
				socket.getOutputStream().flush();
			}
		}
		byte[] tokenBytes = new byte[24];
		long resolute = -1;
		while (resolute < 100) {
			if (socket.getInputStream().available() > 0) socket.getInputStream().read(tokenBytes);
			String line = "Auth Token: ";
			if (resolute > -1) line = System.lineSeparator() + line;
			socket.getOutputStream().write(line.getBytes());
			socket.getOutputStream().flush();
			long pastTime = System.currentTimeMillis();
			socket.getInputStream().read(tokenBytes);
			long newTime = System.currentTimeMillis();
			resolute = newTime - pastTime;	
		}
		na = na + "~!" + new String(tokenBytes, StandardCharsets.UTF_8).toString();
		return na;
	}

	@Override
	public void run() {
		Socket socket = null;
		while (HardScene.running) {
			try {
				socket = HardScene.server.accept();
			} catch (Exception ig) {
				break;
			}
			// START: Unsupported client support
			boolean unsupportedClient = false;
			String na = "na";
			try {
				Thread.sleep(200);
				if (socket.getInputStream().available() == 0) {
					unsupportedClient = true;
					na = unsupportedAuthentication(socket);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Random ran = new Random();
			byte[] messageBytes = new byte[24];
			String name = na;
			if (na.equals("na")) {
				try {
					if (socket.getInputStream().read(messageBytes) == -1) {
						System.out.println(HardScene.formatAddress(socket) + " closed its socket before it could be processed.");
					} else {
						name = new String(messageBytes, StandardCharsets.UTF_8);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// END: Unsupported client support
			try {
				String token = "";
				try {
					token = name.trim().split("~!")[1];
				} catch (Exception ig) {
				}
				Client client = new Client(socket, ran.nextInt(900) + 100, name.trim().split("~!")[0], token, unsupportedClient);
				if (HardScene.clients.size() > HardScene.config.maxClients) {
					System.out.println(client.address + " has been denied access to connect due to the max clients threshold.");
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
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}