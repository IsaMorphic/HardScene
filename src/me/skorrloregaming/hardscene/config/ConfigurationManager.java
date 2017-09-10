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
	public boolean colorCodes = true;
	public boolean doRequireInfo = true;
	public boolean translationFeatures = true;
	public boolean enableSwearFilter = true;

	public boolean development = false;

	public ConfigurationManager(File file) throws IOException {
		if (System.getenv("development") != null) {
			development = true;
			Logger.info("Notice: You are running in development mode, some features are disabled.");
			return;
		}
		Properties p = new Properties();
		if (file.exists()) {
			try (FileReader reader = new FileReader(file)) {
				p.load(reader);
				boolean problem = false;
				try {
					port = Integer.parseInt(p.getProperty("port"));
				} catch (Exception ex) {
					problem = true;
				}
				try {
					log = Boolean.parseBoolean(p.getProperty("log"));
				} catch (Exception ex) {
					problem = true;
				}
				try {
					allowSameNameClients = Boolean.parseBoolean(p.getProperty("allowSameNameClients"));
				} catch (Exception ex) {
					problem = true;
				}
				try {
					messageFormat = String.valueOf(p.getProperty("messageFormat")).replace("&", "§");
				} catch (Exception ex) {
					problem = true;
				}
				try {
					colorCodes = Boolean.parseBoolean(p.getProperty("colorCodes"));
				} catch (Exception ex) {
					problem = true;
				}
				try {
					doRequireInfo = Boolean.parseBoolean(p.getProperty("doRequireInfo"));
				} catch (Exception ex) {
					problem = true;
				}
				try {
					translationFeatures = Boolean.parseBoolean(p.getProperty("translationFeatures"));
				} catch (Exception ex) {
					problem = true;
				}
				try {
					enableSwearFilter = Boolean.parseBoolean(p.getProperty("enableSwearFilter"));
				} catch (Exception ex) {
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
			writer.println("colorCodes=" + colorCodes);
			writer.println("translationFeatures=" + translationFeatures);
			writer.println("enableSwearFilter=" + enableSwearFilter);
			writer.close();
		}
	}

}
