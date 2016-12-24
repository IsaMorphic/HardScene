package org.skorrloregaming.hardscene.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.skorrloregaming.hardscene.server.config.ConfigurationManager;
import org.skorrloregaming.hardscene.server.config.LocalizationManager;
import org.skorrloregaming.hardscene.server.event.CommandProcessEvent;
import org.skorrloregaming.hardscene.server.interfaces.Client;
import org.skorrloregaming.hardscene.server.interfaces.LegacyCommandSender;
import org.skorrloregaming.hardscene.server.interfaces.Logger;
import org.skorrloregaming.hardscene.server.thread.HardScene_LoopThread;

public class HardScene {

	public static ConcurrentMap<Integer, Client> clients = new ConcurrentHashMap<>();

	public static boolean running = false;
	public static ServerSocket server = null;
	public static ConfigurationManager config = null;

	public static LocalizationManager bannedManager = null;

	public static HardScene instance = null;

	public static void main(String[] args) {
		instance = new HardScene();
		instance.onEnable();
	}

	public void onEnable() {
		bannedManager = new LocalizationManager(new File("hardscene_banned.properties"));
		startServer();
	}

	public static String formatAddress(Socket socket) {
		String addr = socket.getRemoteSocketAddress().toString().split(":")[0].replace("/", "");
		if (addr.equals("0"))
			addr = "127.0.0.1";
		return addr;
	}

	public boolean startServer() {
		if (running)
			return false;
		running = true;
		try {
			config = new ConfigurationManager();
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		Logger.info("Starting bind on port " + config.port + "..");
		try {
			server = new ServerSocket(config.port);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.info("Failed to bind HardScene to port " + config.port + ".");
			running = false;
			return false;
		}
		Thread acceptIncomingConnections = new Thread(new HardScene_LoopThread());
		acceptIncomingConnections.start();
		Logger.info("Server started, waiting for incoming connections..");
		Scanner scanner = new Scanner(System.in);
		while (running) {
			System.out.print("> ");
			String input = scanner.nextLine();
			new CommandProcessEvent(input.split(" "), new LegacyCommandSender());
		}
		scanner.close();
		return true;
	}

	public static String trim(String str) {
		String output = "";
		boolean foundStart = false;
		for (int i = 0; i < str.length(); i++) {
			if (!(str.charAt(i) == (char) ' ')) {
				if (!foundStart)
					foundStart = true;
				output += str.charAt(i);
			} else {
				if (foundStart) {
					output += str.charAt(i);
				}
			}
		}
		return output;
	}

	public static void log(String message) {
		if (config.log) {
			try {
				String time = "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]";
				String logMessage = '\n' + time + " " + trim(message);
				File file = new File("hardscene_log.txt");
				if (!file.exists())
					file.createNewFile();
				FileWriter writer = new FileWriter(file, true);
				writer.append(logMessage);
				writer.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void broadcast(String message) throws IOException {
		log(message);
		for (Client c : clients.values()) {
			String format = message;
			if (c.unsupportedClient)
				format = format + '\r' + '\n';
			c.sendMessage(format);
		}
	}

}
