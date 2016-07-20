package org.skorrloregaming.hardscene.server.auth;

public class AuthUnit {
	
	private AuthKey key = null;
	
	public AuthUnit(AuthKey key){
		this.key = key;
	}
	
	public boolean checkAuth(AuthKey masterKey){
		int masterUp = masterKey.hash[0] + masterKey.hash[1] + masterKey.hash[2] + masterKey.hash[3];
		int keyUp = key.hash[0] + key.hash[1] + masterKey.hash[2] + masterKey.hash[3];
		if (masterUp == keyUp) return true;
		return false;
	}
	
}