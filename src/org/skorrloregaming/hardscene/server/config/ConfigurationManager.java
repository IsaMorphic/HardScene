package org.skorrloregaming.hardscene.server.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

public class ConfigurationManager {

	public int port = 28894;
	public int maxClients = 50;
	public boolean log = true;
	
	public ConfigurationManager() throws IOException{
		    File file = new File("hardscene.properties");
		    Properties p = new Properties();
		    if (file.exists()){
		    	try (FileReader reader = new FileReader(file)){
		    		p.load(reader);
		    		port = Integer.parseInt(p.getProperty("port"));
		    		maxClients = Integer.parseInt(p.getProperty("maxClients"));
		    		log = Boolean.parseBoolean(p.getProperty("log"));
		    	}
		    }else{
		        PrintWriter writer = null;
		        try {
		            writer = new PrintWriter("hardscene.properties", "UTF-8");
		        } catch (Exception ex) {
		        	ex.printStackTrace();
		        	System.out.println("Failed. An internal error has occured whilist creating server config.");
					System.exit(-1);
		        }
		        writer.println("port=28894");
		        writer.println("maxClients=50");
		        writer.println("log=true");
		        writer.close();
	    	}
	}
	
}
