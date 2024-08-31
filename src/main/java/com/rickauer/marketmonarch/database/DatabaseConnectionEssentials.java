package com.rickauer.marketmonarch.database;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

; // TODO: Als Singleton implementieren

public class DatabaseConnectionEssentials {
	
	private String url;
	private String username;
	private String password;
	
	public String getUrl() {
		return url;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	protected static DatabaseConnectionEssentials readEssentials() {
		try {
			String srcFile = System.getProperty("user.dir") + "/src/main/resources/DBessentials.json";
			JSONParser parser = new JSONParser();
			Reader reader = new FileReader(srcFile);
			
			Object jsonInput = parser.parse(reader);
			JSONObject jsonInputObject = (JSONObject) jsonInput;
			
			DatabaseConnectionEssentials essentials = new DatabaseConnectionEssentials();
			
			essentials.url = (String) jsonInputObject.get("URL");
			essentials.username = (String) jsonInputObject.get("Username");
			essentials.password = (String) jsonInputObject.get("Password");
			
			return essentials;
			
		} catch (Exception e) {
			throw new RuntimeException("Error reading database credentials.", e);
		}
	}
}
