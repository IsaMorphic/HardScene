package me.skorrloregaming.hardscene.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import me.skorrloregaming.hardscene.HardScene;

public class PropertyManager {

	File file;

	public PropertyManager(File file) {
		this.file = file;
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean addProperty(String key, String value) {
		if (HardScene.config.development) return false;
		if (propertyExists(key))
			return false;
		String addonProperty = '\n' + key + "=" + value;
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileWriter writer = null;
		try {
			writer = new FileWriter(file, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
		try {
			bufferedWriter.write(addonProperty, 0, addonProperty.length());
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean removeProperty(String key) {
		if (HardScene.config.development) return false;
		if (!propertyExists(key))
			return false;
		String body = "";
		Scanner scan = null;
		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		boolean found = false;
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			if (!line.split("=")[0].equals(key)) {
				body += line;
			} else {
				if (!found) {
					found = true;
				} else {
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
		return true;
	}
	
	public String getProperty(String key){
		Scanner scan = null;
		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			if (line.split("=")[0].equals(key)) {
				scan.close();
				return line.split("=")[1];
			}
		}
		scan.close();
		return "";
	}

	public boolean propertyExists(String key) {
		if (HardScene.config.development) return false;
		Scanner scan = null;
		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			if (line.split("=")[0].equals(key)) {
				scan.close();
				return true;
			}
		}
		scan.close();
		return false;
	}

}
