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
	public String messageFormat = "&1{client}&r: {message}";
	public String loginFormat = "&1{client}&r has joined the server.";
	public String authenticateFormat = "&1{client}&r has authenticated with the server.";
	public String leaveFormat = "&1{client}&r has quit the server.";
	public boolean colorCodes = true;
	public boolean doRequireInfo = true;
	public boolean translationFeatures = true;
	public boolean enableSwearFilter = true;

	public boolean development = false;

	public ConfigurationManager(File file) throws IOException {
		if (System.getenv("development") != null) {
			development = true;
			Logger.info("Notice: You are running in development mode, some features are disabled.");
			messageFormat = messageFormat.replace("&", "§");
			loginFormat = loginFormat.replace("&", "§");
			authenticateFormat = authenticateFormat.replace("&", "§");
			leaveFormat = leaveFormat.replace("&", "§");
			return;
		}
		Properties p = new Properties();
		if (file.exists()) {
			try (FileReader reader = new FileReader(file)) {
				p.load(reader);
				boolean problem = false;
				if (p.containsKey("port")) {
					port = Integer.parseInt(p.getProperty("port"));
				} else {
					problem = true;
				}
				if (p.containsKey("log")) {
					log = Boolean.parseBoolean(p.getProperty("log"));
				} else {
					problem = true;
				}
				if (p.containsKey("allowSameNameClients")) {
					allowSameNameClients = Boolean.parseBoolean(p.getProperty("allowSameNameClients"));
				} else {
					problem = true;
				}
				if (p.containsKey("messageFormat")) {
					messageFormat = String.valueOf(p.getProperty("messageFormat")).replace("&", "§");
				} else {
					problem = true;
				}
				if (p.containsKey("loginFormat")) {
					loginFormat = String.valueOf(p.getProperty("loginFormat")).replace("&", "§");
				} else {
					problem = true;
				}
				if (p.containsKey("authenticateFormat")) {
					authenticateFormat = String.valueOf(p.getProperty("authenticateFormat")).replace("&", "§");
				} else {
					problem = true;
				}
				if (p.containsKey("leaveFormat")) {
					leaveFormat = String.valueOf(p.getProperty("leaveFormat")).replace("&", "§");
				} else {
					problem = true;
				}
				if (p.containsKey("colorCodes")) {
					colorCodes = Boolean.parseBoolean(p.getProperty("colorCodes"));
				} else {
					problem = true;
				}
				if (p.containsKey("doRequireInfo")) {
					doRequireInfo = Boolean.parseBoolean(p.getProperty("doRequireInfo"));
				} else {
					problem = true;
				}
				if (p.containsKey("translationFeatures")) {
					translationFeatures = Boolean.parseBoolean(p.getProperty("translationFeatures"));
				} else {
					problem = true;
				}
				if (p.containsKey("enableSwearFilter")) {
					enableSwearFilter = Boolean.parseBoolean(p.getProperty("enableSwearFilter"));
				} else {
					problem = true;
				}
				if (problem) {
					Logger.info("Could not successfully load all configuration options.");
					Logger.info("It is recommended in this case to delete the config and restart.");
				}
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
			writer.println("doRequireInfo=" + doRequireInfo);
			writer.println("allowSameNameClients=" + allowSameNameClients);
			writer.println("messageFormat=" + messageFormat);
			writer.println("loginFormat=" + loginFormat);
			writer.println("authenticateFormat=" + authenticateFormat);
			writer.println("leaveFormat=" + leaveFormat);
			writer.println("colorCodes=" + colorCodes);
			writer.println("translationFeatures=" + translationFeatures);
			writer.println("enableSwearFilter=" + enableSwearFilter);
			writer.close();
		}
	}

}
