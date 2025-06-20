package com.rickauer.marketmonarch.configuration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.rickauer.marketmonarch.utils.Verifyable;
import com.rickauer.marketmonarch.utils.Visitor;

public enum DatabaseConnector implements Verifyable {
	INSTANCE;
	
	private static final String CONFIGURATION_FILE = "Configuration.json";
	
	private Logger databaseConnectorLogger = LogManager.getLogger(DatabaseConnector.class.getName());
	
	private String urlTestDB;
	private String urlAPIKey;
	private String urlFinancialData;
	private String username;
	private String password;

	public boolean isSourceFilePresent() {
		return DatabaseConnector.class.getClassLoader().getResource("Configuration.json") != null;
	}
	
	public String getSourceFile() {
		return CONFIGURATION_FILE;
	}
	
	public String getUrlTestDB() {
		return urlTestDB;
	}

	public String getUrlAPIKey() {
		return urlAPIKey;
	}
	
	public String getUrlFinancialData() {
		return urlFinancialData;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	DatabaseConnector() {
		urlTestDB = "";
		urlAPIKey = "";
		urlFinancialData = "";
		username = "";
		password = "";
	}
	
	public void initializeDatabaseConnector() {
		databaseConnectorLogger.info("Reading database essentials.");
		readDatabaseConnectionEssentials();
	}

	private void readDatabaseConnectionEssentials() {

		try (InputStream in = DatabaseConnector.class.getClassLoader().getResourceAsStream(CONFIGURATION_FILE);
			 Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);) {

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

		databaseConnectorLogger.info("Flushed database essentials.");
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
