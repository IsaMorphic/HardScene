package org.skorrloregaming.hardscene.server.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class LocalizationManager {

	File file;

	public LocalizationManager(File file) {
		this.file = file;
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean addProperty(String property) {
		if (propertyExists(property))
			return false;
		String addonProperty = '\n' + property;
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

	public boolean removeProperty(String property) {
		if (!propertyExists(property))
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
			if (!line.equals(property)) {
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

	public boolean propertyExists(String property) {
		Scanner scan = null;
		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			if (line.equals(property)) {
				scan.close();
				return true;
			}
		}
		scan.close();
		return false;
	}
}