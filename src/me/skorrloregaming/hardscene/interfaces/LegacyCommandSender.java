package me.skorrloregaming.hardscene.interfaces;

public class LegacyCommandSender {
	
	Client client = null;
	public String preCommandSyntax = "";
	
	public LegacyCommandSender(Client client, String preCommandSyntax) {
		this.client = client;
		this.preCommandSyntax = preCommandSyntax;
	}
	
	public LegacyCommandSender() {}

	public void sendMessage(String message) {
		if (client == null) {
			Logger.info(message);
		} else {
			client.sendMessage("== " + message);
		}
	}

	public String getName() {
		if (client == null) {
			return "Server";
		} else {
			return client.name;
		}
	}

}
