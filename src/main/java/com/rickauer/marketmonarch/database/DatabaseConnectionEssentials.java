package com.rickauer.marketmonarch.database;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public enum DatabaseConnectionEssentials {
	INSTANCE;

	private Logger dBEssentialsLogger = LogManager.getLogger(DatabaseConnectionEssentials.class.getName());
	
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

	DatabaseConnectionEssentials() {
		dBEssentialsLogger.info("Reading database essentials.");
		readDatabaseConnectionEssentials();
	}

	private void readDatabaseConnectionEssentials() {

		String srcFile = System.getProperty("user.dir") + "/src/main/resources/DBessentials.json";
		try (Reader reader = new FileReader(srcFile);) {

			JSONParser parser = new JSONParser();

			Object jsonInput = parser.parse(reader);
			JSONObject jsonInputObject = (JSONObject) jsonInput;

			url = (String) jsonInputObject.get("URL");
			username = (String) jsonInputObject.get("Username");
			password = (String) jsonInputObject.get("Password");

		} catch (Exception e) {
			throw new RuntimeException("Error reading database credentials.", e);
		}
	}

	public void flushDatabaseConnectionEssentials() {
		url = "";
		username = "";
		password = "";

		dBEssentialsLogger.info("Flushed database essentials.");
	}
}
