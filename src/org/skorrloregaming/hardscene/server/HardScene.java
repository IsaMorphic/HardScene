package org.skorrloregaming.hardscene.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Scanner;

import org.skorrloregaming.hardscene.server.config.Config;
import org.skorrloregaming.hardscene.server.config.SuppressionHelper;
import org.skorrloregaming.hardscene.server.event.CommandProcessEvent;
import org.skorrloregaming.hardscene.server.event.impl.ClientImpl;
import org.skorrloregaming.hardscene.server.event.impl.LoggerImpl;
import org.skorrloregaming.hardscene.server.thread.HardScene_LoopThread;

public class HardScene {
	
	public static boolean running = false;
	public static HashMap<Integer, ClientImpl> clients = new HashMap<>();
	public static ServerSocket server = null;
	public static Config config = null;
	public static boolean insecure = false;
	
	public static SuppressionHelper suppressionHelper = null;
	
	public static String frameName = "HardScene";
	
	public static HardScene instance = null;
	
	public static boolean legacy = true;
	
	public static void main(String[] args){
		instance = new HardScene();
		instance.onEnable();
	}
	
	public void onEnable(){
    	suppressionHelper = new SuppressionHelper(new File("banned-clients.o"));
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
		System.out.println(frameName+", based on JServ (Open Source).");
		System.out.println("Starting bind on port "+config.port+"..");
		try {
			server = new ServerSocket(config.port);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("#################################################");
			System.out.println("###################################################");
			System.out.println("# Failed to bind HardScene to port "+config.port+".");
			System.out.println("###################################################");
			System.out.println("#################################################");
			running = false;
			return false;
		}
		Thread acceptIncomingConnections = new Thread(new HardScene_LoopThread());
		acceptIncomingConnections.start();
		System.out.println("Server started, waiting for incoming connections..");
	    Scanner scanner = new Scanner(System.in);
 		while (running){
 		    String input = scanner.nextLine();
 		    new CommandProcessEvent(input.split(" "), new LoggerImpl());
 		}
 	    scanner.close();
 	    return true;
	}
	
	public static void broadcast(String message, boolean direct) throws IOException{
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
