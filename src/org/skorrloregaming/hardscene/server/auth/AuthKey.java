package org.skorrloregaming.hardscene.server.auth;

public class AuthKey {
	
	public byte[] hash;
	
	public AuthKey(byte[] keyArray){
		this.hash = keyArray;
	}
	
}
