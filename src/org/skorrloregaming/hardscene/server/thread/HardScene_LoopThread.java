package org.skorrloregaming.hardscene.server.thread;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.skorrloregaming.hardscene.server.HardScene;
import org.skorrloregaming.hardscene.server.event.ClientConnectEvent;
import org.skorrloregaming.hardscene.server.http.WebServer;
import org.skorrloregaming.hardscene.server.http.WebSocket;
import org.skorrloregaming.hardscene.server.interfaces.Client;

public class HardScene_LoopThread implements Runnable {

	private String unsupportedAuthentication(Socket socket) {
		try {
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
					socket.getOutputStream().write(("Please specify a name with a length between 4 and 16." + System.lineSeparator()).getBytes());
					socket.getOutputStream().flush();
				} else if (patternMatch) {
					socket.getOutputStream().write(("Invalid display name syntax, please try again." + System.lineSeparator()).getBytes());
					socket.getOutputStream().flush();
				}
			}
			byte[] tokenBytes = new byte[24];
			long resolute = -1;
			while (resolute < 100) {
				if (socket.getInputStream().available() > 0)
					socket.getInputStream().read(tokenBytes);
				String line = "Auth Token: ";
				if (resolute > -1)
					line = System.lineSeparator() + line;
				socket.getOutputStream().write(line.getBytes());
				socket.getOutputStream().flush();
				long pastTime = System.currentTimeMillis();
				socket.getInputStream().read(tokenBytes);
				long newTime = System.currentTimeMillis();
				resolute = newTime - pastTime;
			}
			na = na + "~!" + new String(tokenBytes, StandardCharsets.UTF_8).toString();
			return na;
		} catch (Exception ex) {
			return "na";
		}
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
				Thread.sleep(500);
				if (socket.getInputStream().available() == 0) {
					unsupportedClient = true;
					na = unsupportedAuthentication(socket);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Random ran = new Random();
			byte[] messageBytes = new byte[2048];
			String name = na;
			boolean webClient = false;
			boolean webServer = false;
			Integer clientID = ran.nextInt(900) + 100;
			if (na.equals("na")) {
				try {
					if (socket.getInputStream().read(messageBytes) == -1) {
						System.out.println(HardScene.formatAddress(socket) + " closed its socket before it could be processed.");
					} else {
						name = new String(messageBytes, StandardCharsets.UTF_8).trim();
						String res = name.split("\\r?\\n")[0];
						if (res.contains("HTTP/1.1")) {
							String header = res.split("/")[1].replace(" HTTP", "").toLowerCase();
							if (header.equals("websocket")) {
								WebSocket ws = new WebSocket(socket, name.split("\\r?\\n"), clientID);
								ws.bind();
								Thread.sleep(500);
								if (!ws.readLogin()) {
									System.out.println(HardScene.formatAddress(socket) + " closed its socket before it could be processed.");
									webServer = true;
								} else {
									name = ws.getWebSocketClient().name + "~!" + ws.getWebSocketClient().token;
									ws.start();
									webClient = true;
								}
							} else if (header.equals("")) {
								new WebServer(socket, name.split("\\r?\\n"));
								webServer = true;
							} else {
								DataOutputStream out = new DataOutputStream(socket.getOutputStream());
								System.out.println("WebServer: HTTP/1.1 200 OK");
								out.writeBytes("HTTP/1.1 200 OK\r\n");
								out.writeBytes("Content-Type: text/html\r\n\r\n");
								out.writeBytes("<h1>Not Found</h1>\r\n<p>The requested URL /" + header + " was not found on this server.</p>\r\n");
								out.writeBytes("<p>Additionally, a 404 Not Found error was encountered while trying to use an ErrorDocument to handle the request.</p>");
								out.flush();
								socket.close();
								webServer = true;
							}
						}
					}
				} catch (Exception e) {
					System.out.println(HardScene.formatAddress(socket) + " closed its socket before it could be processed.");
				}
			}
			if (!webServer && name.length() < 100 && !name.equals("na")) {
				// END: Unsupported client support
				try {
					String token = "";
					try {
						token = name.trim().split("~!")[1];
					} catch (Exception ig) {
					}
					Client client = new Client(socket, clientID, name.trim().split("~!")[0], token, unsupportedClient, webClient);
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
						if (!webClient) {
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
}