package org.skorrloregaming.hardscene.server.interfaces;

public class LegacyCommandSender {

	public void sendMessage(String message) {
		System.out.println(message);
	}

	public String getName() {
		return "Server";
	}

}
