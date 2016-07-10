package org.skorrloregaming.hardscene.server.event;

import java.util.HashMap;
import org.skorrloregaming.hardscene.server.HardScene;
import org.skorrloregaming.hardscene.server.event.impl.ClientImpl;
import org.skorrloregaming.hardscene.server.event.impl.LoggerImpl;

public class CommandProcessEvent {

	@SuppressWarnings("unchecked")
	public CommandProcessEvent(String[] args, LoggerImpl logger){
		try{
			if (args[0].equalsIgnoreCase("help")){
				viewHelp(logger);
			}else if (args[0].equalsIgnoreCase("ban")){
				if (args.length >= 2){
					String address = args[1];
					boolean result = HardScene.suppressionHelper.suppress(address);
					HashMap<Integer, ClientImpl> array = ((HashMap<Integer, ClientImpl>) HardScene.clients.clone());
					for (ClientImpl c : array.values()){
						String clientAddress = c.address.split(":")[0];
						if (clientAddress.equals("/"+address)){
							new ClientDisconnectEvent(c, true);
							HardScene.clients.remove(c);
						}
					}
					if (result){
						logger.sendMessage("Success.");
					}else{
						logger.sendMessage("Failed. That IP was found to be already banned from the server.");
					}
				}else{
					logger.sendMessage("Failed. Syntax: '"+preCommandSyntax+"ban <ip>'");
				}
			}else if (args[0].equalsIgnoreCase("pardon")){
				if (args.length >= 2){
					String address = args[1];
					boolean result = HardScene.suppressionHelper.pardon(address);
					if (result){
						logger.sendMessage("Success.");
					}else{
						logger.sendMessage("Failed. That IP was found to be allowed to join the server already.");
					}
				}else{
					logger.sendMessage("Failed. Syntax: '"+preCommandSyntax+"pardon <ip>'");
				}
			}else if (args[0].equalsIgnoreCase("check")){
				if (HardScene.running){
					logger.sendMessage("The server socket is properly running.");
				}else{
					logger.sendMessage("The server socket is not properly running.");
				}
			}else if (args[0].equalsIgnoreCase("start")){
				if (HardScene.legacy){
					logger.sendMessage("Failed. Command disabled on legacy HardScene.");
					return;
				}
				if (HardScene.running){
					logger.sendMessage("Failed. The server socket is already running.");
					return;
				}else{
					logger.sendMessage("Starting server socket and child threads..");
					boolean result = HardScene.instance.startServer();
					if (result){
						logger.sendMessage("Success.");
					}else{
						logger.sendMessage("Failed. An internal error occurred, check logs for more information.");
					}
				}
			}else if (args[0].equalsIgnoreCase("stop")){
				if (!HardScene.running){
					logger.sendMessage("Failed. The server socket has already been stopped.");
					return;
				}
				logger.sendMessage("Terminating server socket..");
				try{
					HardScene.server.close();
					HardScene.running = false;
					logger.sendMessage("Terminating child threads..");
					HashMap<Integer, ClientImpl> array = ((HashMap<Integer, ClientImpl>) HardScene.clients.clone());
					for (ClientImpl c : array.values()){
						try {
							new ClientDisconnectEvent(c, true);
						} catch (Exception ignored) {}
					}
					HardScene.clients.clear();
				}catch (Exception ignored) {}
				logger.sendMessage("Success.");
			}else if (args[0].equalsIgnoreCase("kickall")){
				HashMap<Integer, ClientImpl> array = ((HashMap<Integer, ClientImpl>) HardScene.clients.clone());
				logger.sendMessage("Kicking all connected clients from the server forcibly..");
				for (ClientImpl c : array.values()){
					try {
						new ClientDisconnectEvent(c, true);
					} catch (Exception ignored) {}
				}
				HardScene.clients.clear();
				logger.sendMessage("Success.");
			}else if (args[0].equalsIgnoreCase("kick")){
				if (args.length >= 2){
						if (!HardScene.clients.containsKey(Integer.parseInt(args[1]))){
							logger.sendMessage("Failed. Could not find a client with the ID:"+args[1]+".");
							return;
						}
						ClientImpl c = HardScene.clients.get(Integer.parseInt(args[1]));
						logger.sendMessage("Kicking the specified client from the server..");
						try {
							new ClientDisconnectEvent(c, true);
							HardScene.clients.remove(c);
						} catch (Exception ignored) {}
						logger.sendMessage("Success.");
				}else{
					logger.sendMessage("Failed. Syntax: '"+preCommandSyntax+"kick <clientID>'");
				}
			}else if (args[0].equalsIgnoreCase("broadcast")){
				if (args.length >= 2){
					String message = "  [Broadcast]";
					for (int i = 1; i < args.length; i++){
						message += " " + args[i];
					}
					logger.sendMessage("Broadcasting message to all connected clients..");
					try {
						HardScene.broadcast(message, false);
					} catch (Exception ignored) {}
					logger.sendMessage("Success.");
				}
			}else if (args[0].equalsIgnoreCase("tell")){
				if (args.length >= 3){
						if (!HardScene.clients.containsKey(Integer.parseInt(args[1]))){
							logger.sendMessage("Could not find a client with the ID:"+args[1]+".");
							return;
						}
						ClientImpl c = HardScene.clients.get(Integer.parseInt(args[1]));
						String message = "  "+logger.getName()+" says..";
						for (int i = 2; i < args.length; i++){
							message += " " + args[i];
						}
						byte[] messageBytes = message.getBytes();
						try {
							c.socket.getOutputStream().write(messageBytes, 0, messageBytes.length);
							c.socket.getOutputStream().flush();
						} catch (Exception ignored) {}
						logger.sendMessage("[me > "+Integer.parseInt(args[1])+"] "+message);
						return;
				}else{
					logger.sendMessage("Failed. Syntax: '"+preCommandSyntax+"tell <clientID> <message>'");
				}
			}else if (args[0].equalsIgnoreCase("list")){
				if (args.length >= 2){
					if (args[1].equalsIgnoreCase("/a")){
						logger.sendMessage("Listing current clients in complete mode..");
						logger.sendMessage("[ Address | ID | Display Name ]");
						HashMap<Integer, ClientImpl> array = ((HashMap<Integer, ClientImpl>) HardScene.clients.clone());
						for (ClientImpl c : array.values()){
							logger.sendMessage(c.address.toString()+" - "+c.id+" - "+c.displayName);
						}
						logger.sendMessage("Success.");
						return;
					}else{
						logger.sendMessage("Failed. Syntax: '"+preCommandSyntax+"list [/a]'");
						return;
					}
				}
				logger.sendMessage("Listing current clients in safe mode..");
				logger.sendMessage("[ ID | Display Name ]");
				HashMap<Integer, ClientImpl> array = ((HashMap<Integer, ClientImpl>) HardScene.clients.clone());
				for (ClientImpl c : array.values()){
					logger.sendMessage(c.id+" - "+c.displayName);
				}
				logger.sendMessage("Success.");
			}else{
				logger.sendMessage("Failed. The specified sub-command, "+args[0]+", could not be found.");
			}	
		}catch (Exception ex){
			ex.printStackTrace();
			logger.sendMessage("Failed. An internal error has occurred, check logs for more information.");
		}
	}
	
	String preCommandSyntax = "";
	
	public void viewHelp(LoggerImpl logger){
		logger.sendMessage("HardScene - Commands");
		logger.sendMessage(""+preCommandSyntax+"help - Displays this listing.");
		logger.sendMessage(""+preCommandSyntax+"ban <ip> - Attemps to ban the IP from the server.");
		logger.sendMessage(""+preCommandSyntax+"pardon <ip> - Attempts to remove the IP from banned players.");
		logger.sendMessage(""+preCommandSyntax+"kick <clientID> - Attemps to kick the specified client from the server.");
		logger.sendMessage(""+preCommandSyntax+"kickall - Attemps to kick all connected clients from the server.");
		logger.sendMessage(""+preCommandSyntax+"check - Attempts to check if the server is running or not.");
		logger.sendMessage(""+preCommandSyntax+"stop - Attempts to stop the server.");
		logger.sendMessage(""+preCommandSyntax+"list [/a] - Attemps to list the connected clients on the server.");
		logger.sendMessage(""+preCommandSyntax+"tell <clientID> <message> - Sends a message to the specified client.");
		logger.sendMessage(""+preCommandSyntax+"broadcast <message> - Broadcasts a message to the server.");
	}
	
	
}
