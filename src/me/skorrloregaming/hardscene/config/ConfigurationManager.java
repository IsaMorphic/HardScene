package me.skorrloregaming.hardscene.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import me.skorrloregaming.hardscene.interfaces.Logger;

public class ConfigurationManager {

	public int port = 28894;
	public boolean log = true;
	public boolean allowSameNameClients = false;
	public String messageFormat = "{client}: {message}";

	public ConfigurationManager() throws IOException {
		if (System.getenv("development") != null)
			return;
		File file = new File("hardscene.properties");
		Properties p = new Properties();
		if (file.exists()) {
			try (FileReader reader = new FileReader(file)) {
				p.load(reader);
				port = Integer.parseInt(p.getProperty("port"));
				log = Boolean.parseBoolean(p.getProperty("log"));
				allowSameNameClients = Boolean.parseBoolean(p.getProperty("allowSameNameClients"));
				messageFormat = String.valueOf(p.getProperty("messageFormat"));
			}
		} else {
			PrintWriter writer = null;
			try {
				writer = new PrintWriter("hardscene.properties", "UTF-8");
			} catch (Exception ex) {
				ex.printStackTrace();
				Logger.info("Failed. An internal error has occured whilist creating server config.");
				System.exit(-1);
			}
			writer.println("port=" + port);
			writer.println("log=" + log);
			writer.println("allowSameNameClients=" + allowSameNameClients);
			writer.println("messageFormat=" + messageFormat);
			writer.close();
		}
	}

}
