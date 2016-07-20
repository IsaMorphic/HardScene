package org.skorrloregaming.hardscene.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import org.skorrloregaming.hardscene.server.config.Config;
import org.skorrloregaming.hardscene.server.config.Properties;
import org.skorrloregaming.hardscene.server.event.CommandProcessEvent;
import org.skorrloregaming.hardscene.server.impl.ClientImpl;
import org.skorrloregaming.hardscene.server.impl.LoggerImpl;
import org.skorrloregaming.hardscene.server.thread.HardScene_LoopThread;

public class HardScene {
	
	public static boolean running = false;
	public static HashMap<Integer, ClientImpl> clients = new HashMap<>();
	public static ServerSocket server = null;
	public static Config config = null;
	public static boolean insecure = false;
	
	public static Properties bannedManager = null;
	public static Properties opManager = null;
	
	public static String frameName = "HardScene";
	
	public static HardScene instance = null;
	
	public static boolean legacy = true;
	
	public static void main(String[] args){
		instance = new HardScene();
		instance.onEnable();
	}
	
	public void onEnable(){
		opManager = new Properties(new File("hardscene_operators.properties"));
    	bannedManager = new Properties(new File("hardscene_banned.properties"));
		startServer();
	}
	
	public boolean startServer(){
		if (running) return false;
    	running = true;
		try {
			config = new Config();
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		System.out.println("Starting bind on port "+config.port+"..");
		try {
			server = new ServerSocket(config.port);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to bind HardScene to port "+config.port+".");
			running = false;
			return false;
		}
		Thread acceptIncomingConnections = new Thread(new HardScene_LoopThread());
		acceptIncomingConnections.start();
		System.out.println("Server started, waiting for incoming connections..");
	    Scanner scanner = new Scanner(System.in);
 		while (running){
 			System.out.print("> ");
 		    String input = scanner.nextLine();
 		    new CommandProcessEvent(input.split(" "), new LoggerImpl());
 		}
 	    scanner.close();
 	    return true;
	}
	
	public static String trim(String str){
		String output = "";
		boolean foundStart = false;
		for (int i = 0; i < str.length(); i++){
			if (!(str.charAt(i) == (char)' ')){
				if (!foundStart) foundStart = true;
				output += str.charAt(i);
			}else{
				if (foundStart){
					output += str.charAt(i);
				}
			}
		}
		return output;
	}
	
	public static void log(String message){
		if (config.log){
			try{
				String time = "["+new SimpleDateFormat("HH:mm:ss").format(new Date())+"]";
				String logMessage = '\n' + time + " " + trim(message);
				File file = new File("hardscene_log.txt");
	            if (!file.exists()) file.createNewFile();
	            FileWriter writer = new FileWriter(file, true);
	            BufferedWriter bufferedWriter = new BufferedWriter(writer);
	            bufferedWriter.write(logMessage, 0, logMessage.length());
	            bufferedWriter.close();
			}catch (Exception ex){ex.printStackTrace();}
		}
	}
	
	public static void broadcast(String message, boolean direct) throws IOException{
		log(message);
		byte[] messageBytes = message.getBytes();
		for (ClientImpl c : clients.values()){
			if (direct){
				c.socket.getOutputStream().write(messageBytes, 0, messageBytes.length);
			}else{
				messageBytes[0] = (byte)' ';
				messageBytes[1] = (byte)' ';
				c.socket.getOutputStream().write(messageBytes, 0, messageBytes.length);	
			}
			c.socket.getOutputStream().flush();
		}
	}
	
}
