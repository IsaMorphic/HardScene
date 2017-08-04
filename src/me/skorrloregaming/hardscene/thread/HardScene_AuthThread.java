package me.skorrloregaming.hardscene.thread;

import java.nio.charset.Charset;

import javax.xml.bind.DatatypeConverter;

import me.skorrloregaming.hardscene.HardScene;
import me.skorrloregaming.hardscene.event.ClientAuthenticateEvent;
import me.skorrloregaming.hardscene.http.WebSocketClient;
import me.skorrloregaming.hardscene.interfaces.Client;

public class HardScene_AuthThread implements Runnable {

	private Client client;

	public boolean isComplete = false;

	public boolean incomplete = false;

	public HardScene_AuthThread(Client client) {
		this.client = client;
	}
	
	public static String encodePassword(String password) {
		return DatatypeConverter.printBase64Binary(password.getBytes());
	}

	public static boolean checkPassword(Client client, String password) {
		if (HardScene.authManager.propertyExists(client.name)) {
			String correctPassword = HardScene.authManager.getProperty(client.name);
			if (encodePassword(password).replace("=", "").equals(correctPassword)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean handleCommand(Client client, String message) {
		String[] args = new String[0];
		if (message.contains(" ")) args = message.substring(message.indexOf(" ")).split(" ");
		String label = message.split(" ")[0];
		if (message.startsWith("/changepassword") || message.startsWith("/cp")) {
			if (args.length < 2) {
				client.sendMessage("Syntax: " + label + " (currentPassword) (newPassword)");
			} else {
				String currentPassword = args[1];
				String newPassword = args[2];
				if (checkPassword(client, currentPassword)) {
					if (newPassword.length() < 4 || newPassword.length() > 16) {
						client.sendMessage("You must specify a password with a length between 4 and 16." + '\r' + '\n');
					} else {
						HardScene.authManager.removeProperty(client.name);
						HardScene.authManager.addProperty(client.name, encodePassword(newPassword));
						client.sendMessage("You have successfully changed your existing password." + '\r' + '\n');
					}
				} else {
					client.sendMessage("The specified password is incorrect for this account, please try again." + '\r' + '\n');
				}
			}
			return true;
		} else if (message.startsWith("/unregister") || message.startsWith("/unreg")) {
			if (args.length == 0) {
				client.sendMessage("Syntax: " + label + " (currentPassword)");
			} else {
				String currentPassword = args[1];
				if (checkPassword(client, currentPassword)) {
					HardScene.authManager.removeProperty(client.name);
					client.sendMessage("You have successfully unregistered your self from the server." + '\r' + '\n');
					client.closeTunnel();
				} else {
					client.sendMessage("The specified password is incorrect for this account, please try again." + '\r' + '\n');
				}
			}
			return true;
		} else if (message.startsWith("/list")) {
			StringBuilder sb = new StringBuilder();
			for (Client c : HardScene.instance.clients.values()) {
				sb.append(c.name + " (" + c.id + "), ");
			}
			String append = sb.toString();
			append = append.substring(0, append.lastIndexOf(", "));
			client.sendMessage("Connected (" + HardScene.instance.clients.values().size() + "): " + append);
			return true;
		} else if (message.startsWith("/help")) {
			client.sendMessage("HardScene - User command dictionary" + '\r' + '\n');
			client.sendMessage("1. /changepassword -> Change your existing account password" + '\r' + '\n');
			client.sendMessage("2. /unregister -> Unregister your account from the server" + '\r' + '\n');
			client.sendMessage("3. /list -> Shows all the people that are connected" +  + '\r' + '\n');
			return true;
		} else {
			client.sendMessage("The specified command was not recognized as a valid command." + '\r' + '\n');
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		try {
			if (HardScene.authManager.propertyExists(client.name)) {
				Runnable loginLoop = new Runnable() {

					public int loop = 0;

					public void run() {
						while (loop < 5) {
							loop++;
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							client.sendMessage("Please log into your account using /login (password)" + '\r' + '\n');
						}
						incomplete = true;
						isComplete = true;
						client.closeTunnel();
					}
				};
				Thread loginLoopThread = new Thread(loginLoop);
				loginLoopThread.start();
				byte[] messageBytes = new byte[512];
				String message = "0";
				while (!isComplete && (!message.startsWith("/login") || message.split(" ").length < 2 || !checkPassword(client, message.split(" ")[1]))) {
					messageBytes = new byte[512];
					client.sendMessage("Please log into your account using /login (password)" + '\r' + '\n');
					if (client.webBased) {
						message = new WebSocketClient(client.socket, client.id, client.name, client.token).readMessage();
						message = message.replace("\r", "").replace("\n", "").trim();
						if (message.equals("-1") || message.equals("null")) {
							incomplete = true;
							isComplete = true;
							return;
						}
					} else {
						int response = client.socket.getInputStream().read(messageBytes);
						message = new String(messageBytes, Charset.forName("UTF-8"));
						message = message.replace("\r", "").replace("\n", "").trim();
						if (response == -1) {
							incomplete = true;
							isComplete = true;
							return;
						}
					}
					if (message.split(" ").length > 1 && !checkPassword(client, message.split(" ")[1])) {
						client.sendMessage("The specified password is incorrect for this account, please try again." + '\r' + '\n');
					}
				}
				client.sendMessage("You have successfully logged into the server." + '\r' + '\n');
				isComplete = true;
				loginLoopThread.stop();
				new ClientAuthenticateEvent(client);
			} else {
				Runnable registerLoop = new Runnable() {

					public int loop = 0;

					public void run() {
						while (loop < 5) {
							loop++;
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							client.sendMessage("Please register your account using /register (password) (password)" + '\r' + '\n');
						}
						incomplete = true;
						isComplete = true;
						client.closeTunnel();
					}
				};
				Thread registerLoopThread = new Thread(registerLoop);
				registerLoopThread.start();
				byte[] messageBytes = new byte[512];
				String message = "0";
				while (!isComplete && (!message.startsWith("/register") || message.split(" ").length < 3 || !message.split(" ")[1].equals(message.split(" ")[2])) || (message.split(" ")[1].length() < 4 || message.split(" ")[1].length() > 16)) {
					messageBytes = new byte[512];
					client.sendMessage("Please register your account using /register (password) (password)" + '\r' + '\n');
					if (client.webBased) {
						message = new WebSocketClient(client.socket, client.id, client.name, client.token).readMessage();
						message = message.replace("\r", "").replace("\n", "").trim();
						if (message.equals("-1") || message.equals("null")) {
							incomplete = true;
							isComplete = true;
							return;
						}
					} else {
						int response = client.socket.getInputStream().read(messageBytes);
						message = new String(messageBytes, Charset.forName("UTF-8"));
						message = message.replace("\r", "").replace("\n", "").trim();
						if (response == -1) {
							incomplete = true;
							isComplete = true;
							return;
						}
					}
					if (message.split(" ").length > 2 && !message.split(" ")[1].equals(message.split(" ")[2])) {
						client.sendMessage("The specified passwords did not match each-other, please try again." + '\r' + '\n');
					} else if (message.split(" ").length > 2 && message.split(" ")[1].length() < 4 || message.split(" ")[1].length() > 16) {
						client.sendMessage("You must specify a password with a length between 4 and 16." + '\r' + '\n');
					}
				}
				client.sendMessage("You have successfully registered with the server." + '\r' + '\n');
				HardScene.authManager.addProperty(client.name, encodePassword(message.split(" ")[1]).replace("=", ""));
				isComplete = true;
				registerLoopThread.stop();
				new ClientAuthenticateEvent(client);
			}
		} catch (Exception e) {
			incomplete = true;
			isComplete = true;
		}
	}

}
