package org.skorrloregaming.hardscene.server.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.skorrloregaming.hardscene.server.event.impl.ClientImpl;

public class SuppressionHelper {
	
    File file;
	
	public SuppressionHelper(File file){
		if (!file.exists()){
			try{
				file.createNewFile();
			}catch (Exception ignored){
				file = null;
			}
		}
		this.file = file;
	}
	
	public boolean validIP(String ip) {
	    if (ip == null || ip.isEmpty()) return false;
	    ip = ip.trim();
	    if ((ip.length() < 6) & (ip.length() > 15)) return false;
	    try {
	        Pattern pattern = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
	        Matcher matcher = pattern.matcher(ip);
	        return matcher.matches();
	    } catch (PatternSyntaxException ex) {
	        return false;
	    }
	}
	
	public boolean pardon(String address){
		address = "/" + address;
		if (!resolve(address)) return false;
        String body = "";
		Scanner scan = null;
		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        boolean found = false;
        while(scan.hasNextLine()){
            String line = scan.nextLine();
            if (!line.equals(address)){
                body += line;
            }else{
                if (!found){
                    found = true;
                }else{
                    body += line;
                }
            }
        }
        scan.close();
        PrintWriter writer = null;
		try {
			writer = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        writer.write(body);
        writer.close();
        if (found){
        	return true;
        }else{
        	return false;
        }
	}
	
	public boolean suppress(String address){
		if (file == null) return false;
		if (resolve("/"+address)) return false;
		if (!validIP(address)) return false;
		address = "/" + address + '\n';
        FileWriter writer;
		try {
			writer = new FileWriter(file, true);
	        BufferedWriter bufferedWriter = new BufferedWriter(writer);
	        bufferedWriter.write(address, 0, address.length());
	        bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean suppress(ClientImpl client){
		return suppress(client.address.split(":")[0]);
	}
	
	public boolean resolve(String address){
		if (file == null) return false;
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (scanner.hasNextLine()){
			String line = scanner.nextLine();
			if (line.charAt(0) == '/'){
				if (line.equals(address)){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean resolve(ClientImpl client){
		return resolve(client.address.split(":")[0]);
	}

}
