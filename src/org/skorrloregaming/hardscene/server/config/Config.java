package org.skorrloregaming.hardscene.server.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

public class Config {

	public int protocolVersion = 1;
	public int port = 28894;
	public int maxClients = 50;
	public String ip = "127.0.0.1";
	public String hash = "8/0/2/7";
	
	public Config() throws IOException{
		    File file = new File("server.properties");
		    Properties p = new Properties();
		    if (file.exists()){
		    	try (FileReader reader = new FileReader(file)){
		    		p.load(reader);
		    		protocolVersion = Integer.parseInt(p.getProperty("protocolVersion"));
		    		ip = p.getProperty("ip");
		    		port = Integer.parseInt(p.getProperty("port"));
		    		maxClients = Integer.parseInt(p.getProperty("maxClients"));
		    		hash = p.getProperty("hash");
		    	}
		    }else{
		        PrintWriter writer = null;
		        try {
		            writer = new PrintWriter("server.properties", "UTF-8");
		        } catch (Exception ex) {
		        	ex.printStackTrace();
		        	System.out.println("Failed. An internal error has occured whilist creating server config.");
					System.exit(-1);
		        }
		        writer.println("protocolVersion=1");
		        writer.println("ip=127.0.0.1");
		        writer.println("port=28894");
		        writer.println("maxClients=50");
		        writer.println("hash=8/0/2/7");
		        writer.close();
	    	}
	}
	
}
