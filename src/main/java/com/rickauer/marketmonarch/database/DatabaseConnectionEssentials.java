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
	
	private static final String SOURCE_FILE = "./src/main/resources/DBessentials.json";
	private Logger dBEssentialsLogger = LogManager.getLogger(DatabaseConnectionEssentials.class.getName());
	
	private String urlTestDB;
	private String urlAPIKey;
	private String urlFinancialData;
	private String username;
	private String password;

	public String getUrlTestDB() {
		return urlTestDB;
	}

	public String getUrlAPIKey() {
		return urlAPIKey;
	}
	
	public String getFinancialData() {
		return urlFinancialData;
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

		String srcFile = SOURCE_FILE;
		try (Reader reader = new FileReader(srcFile);) {

			JSONParser parser = new JSONParser();

			Object jsonInput = parser.parse(reader);
			JSONObject jsonInputObject = (JSONObject) jsonInput;

			urlTestDB = (String) jsonInputObject.get("URL-test_db");
			urlAPIKey = (String) jsonInputObject.get("URL-api_key");
			urlFinancialData = (String) jsonInputObject.get("URL-financial_data");
			username = (String) jsonInputObject.get("Username");
			password = (String) jsonInputObject.get("Password");

		} catch (Exception e) {
			throw new RuntimeException("Error reading database credentials.", e);
		}
	}

	public void flushDatabaseConnectionEssentials() {
		urlTestDB = "";
		urlAPIKey = "";
		urlFinancialData = "";
		username = "";
		password = "";

		dBEssentialsLogger.info("Flushed database essentials.");
	}
}
