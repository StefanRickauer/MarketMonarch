package com.rickauer.marketmonarch.configuration;

import java.io.File;
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

import com.rickauer.marketmonarch.utils.Verifyable;
import com.rickauer.marketmonarch.utils.Visitor;

public enum ConfigReader implements Verifyable {
	INSTANCE;
	
	private static final String SOURCE_FILE = "./src/main/resources/Configuration.json";
	private Logger configReaderLogger = LogManager.getLogger(ConfigReader.class.getName());
	
	private String urlTestDB;
	private String urlAPIKey;
	private String urlFinancialData;
	private String username;
	private String password;

	public boolean isSourceFilePresent() {
		return new File(SOURCE_FILE).exists();
	}
	
	public String getSourceFile() {
		return SOURCE_FILE;
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

	ConfigReader() {
		urlTestDB = "";
		urlAPIKey = "";
		urlFinancialData = "";
		username = "";
		password = "";
	}
	
	public void initializeConfigReader() {
		configReaderLogger.info("Reading database essentials.");
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

		configReaderLogger.info("Flushed database essentials.");
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
