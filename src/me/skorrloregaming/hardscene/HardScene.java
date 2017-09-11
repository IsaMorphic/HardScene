package me.skorrloregaming.hardscene;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import me.skorrloregaming.hardscene.config.ConfigurationManager;
import me.skorrloregaming.hardscene.config.LocalizationManager;
import me.skorrloregaming.hardscene.config.PropertyManager;
import me.skorrloregaming.hardscene.event.CommandProcessEvent;
import me.skorrloregaming.hardscene.interfaces.Client;
import me.skorrloregaming.hardscene.interfaces.LegacyCommandSender;
import me.skorrloregaming.hardscene.interfaces.Logger;
import me.skorrloregaming.hardscene.thread.HardScene_LoopThread;

public class HardScene {

	public ConcurrentMap<Integer, Client> clients = new ConcurrentHashMap<>();

	public static boolean running = false;
	public static ServerSocket server = null;
	public static ConfigurationManager config = null;

	public static LocalizationManager operatorManager = null;
	public static LocalizationManager bannedManager = null;
	public static PropertyManager authManager = null;

	public static HardScene instance = null;

	public static File configFile = null;

	private ArrayList<String> swearWords = new ArrayList<String>();

	public static Date getLastCompilationTime() {
		Date d = null;
		Class<?> currentClass = new Object() {
		}.getClass().getEnclosingClass();
		URL resource = currentClass.getResource(currentClass.getSimpleName() + ".class");
		if (resource != null) {
			if (resource.getProtocol().equals("file")) {
				try {
					d = new Date(new File(resource.toURI()).lastModified());
				} catch (URISyntaxException ignored) {
				}
			} else if (resource.getProtocol().equals("jar")) {
				String path = resource.getPath();
				d = new Date(new File(path.substring(5, path.indexOf("!"))).lastModified());
			} else if (resource.getProtocol().equals("zip")) {
				String path = resource.getPath();
				File jarFileOnDisk = new File(path.substring(0, path.indexOf("!")));
				try (JarFile jf = new JarFile(jarFileOnDisk)) {
					ZipEntry ze = jf.getEntry(path.substring(path.indexOf("!") + 2));
					long zeTimeLong = ze.getTime();
					Date zeTimeDate = new Date(zeTimeLong);
					d = zeTimeDate;
				} catch (IOException | RuntimeException ignored) {
				}
			}
		}
		return d;
	}

	public static void main(String[] args) {
		configFile = new File("hardscene.properties");
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		System.out.println("Last compilation time: " + df.format(getLastCompilationTime()));
		instance = new HardScene();
		instance.onEnable();
	}

	public void onEnable() {
		swearWords.add("fuck");
		swearWords.add("nigga");
		swearWords.add("nigger");
		swearWords.add("bitch");
		swearWords.add("dick");
		swearWords.add("cunt");
		swearWords.add("crap");
		swearWords.add("shit");
		swearWords.add("whore");
		swearWords.add("twat");
		swearWords.add("arse");
		swearWords.add("ass");
		swearWords.add("horny");
		swearWords.add("aroused");
		swearWords.add("hentai");
		swearWords.add("slut");
		swearWords.add("slag");
		swearWords.add("boob");
		swearWords.add("pussy");
		swearWords.add("vagina");
		swearWords.add("faggot");
		swearWords.add("bugger");
		swearWords.add("bastard");
		swearWords.add("anal");
		swearWords.add("wanker");
		swearWords.add("rape");
		swearWords.add("rapist");
		swearWords.add("cock");
		swearWords.add("titt");
		swearWords.add("piss");
		swearWords.add("spunk");
		swearWords.add("milf");
		swearWords.add("anus");
		swearWords.add("dafuq");
		operatorManager = new LocalizationManager(new File("hardscene_operators.properties"));
		authManager = new PropertyManager(new File("hardscene_auth.properties"));
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
			config = new ConfigurationManager(HardScene.configFile);
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

	public String processAntiSwear(Client client, String message) {
		char[] messageChars = message.toCharArray();
		boolean detectedSwearing = false;
		for (String swear : swearWords) {
			int beginIndex = message.indexOf(swear);
			if (beginIndex > -1) {
				int endIndex = beginIndex + swear.length();
				for (int i = beginIndex; i < endIndex; i++) {
					messageChars[i] = '*';
				}
				detectedSwearing = true;
			}
		}
		String modifiedMessage = new String(messageChars);
		if (detectedSwearing) {
			try {
				broadcast("§o" + message, true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Logger.info(message);
			client.sendMessage("Please do not swear, otherwise action will be taken.");
		}
		return modifiedMessage;
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
		if (config.log && !config.development) {
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

	public static void broadcast(String message, boolean staffOnly) throws IOException {
		log(message);
		for (Client c : instance.clients.values()) {
			String format = message;
			if (c.unsupportedClient)
				format = format + '\r' + '\n';
			if (staffOnly) {
				if (operatorManager.propertyExists(c.name)) {
					c.sendMessage(format);
				}
			} else {
				c.sendMessage(format);
			}
		}
	}

	public static void broadcast(String message) throws IOException {
		broadcast(message, false);
	}

}
