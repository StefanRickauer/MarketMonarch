package com.rickauer.marketmonarch.api.connect;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.rickauer.marketmonarch.configuration.ConfigReader;
import com.rickauer.marketmonarch.db.ApiKeyAccess;

public class AlphaVantageConnectorTest {

	@Test
	void isOperationalTrueTest() {
		ConfigReader.INSTANCE.initializeConfigReader();
		ApiKeyAccess key = new ApiKeyAccess(ConfigReader.INSTANCE.getUrlAPIKey(), ConfigReader.INSTANCE.getUsername(), ConfigReader.INSTANCE.getPassword());
		ConfigReader.INSTANCE.flushDatabaseConnectionEssentials();
		AlphaVantageConnector conn = new AlphaVantageConnector("alphavantageapi", key.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'alphavantage'", "token"));
		assertTrue(conn.isOperational());
	}
	
	@Test
	void isOperationalFalseTest() {
		AlphaVantageConnector conn = new AlphaVantageConnector("alphavantageapi", "");
		assertFalse(conn.isOperational());
	}
}
