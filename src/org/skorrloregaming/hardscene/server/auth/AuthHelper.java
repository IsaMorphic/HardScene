package org.skorrloregaming.hardscene.server.auth;

public class AuthHelper {

	public AuthKey hash(int authPortal){
		try{
			byte byte0 = (byte) Integer.parseInt((authPortal / 25) + "");
			byte byte1 = (byte) Integer.parseInt((authPortal / 20) + "");
			byte byte2 = (byte) Integer.parseInt((authPortal / 10) + "");
			byte byte3 = (byte) Integer.parseInt((authPortal / 5) + "");
			return new AuthKey(new byte[] { byte0, byte1, byte2, byte3 });
		}catch (Exception ignored){
			System.out.println("Portal hash failed, portal must be divisible by 25, 20, 10, and 5.");
			System.out.println("Portal hash failed, reverting to portal 100.");
			return hash(100);
		}
	}
	
}
