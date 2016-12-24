package org.skorrloregaming.hardscene.server.interfaces;

public class LegacyCommandSender {

	public void sendMessage(String message) {
		Logger.info(message);
	}

	public String getName() {
		return "Server";
	}

}
