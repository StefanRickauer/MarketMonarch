package com.rickauer.marketmonarch.api.connect;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.rickauer.marketmonarch.configuration.DatabaseConnector;
import com.rickauer.marketmonarch.db.ApiKeyDao;

public class AlphaVantageConnectorTest {

	@Test
	void isOperationalTrueTest() {
		DatabaseConnector.INSTANCE.initializeConfigReader();
		ApiKeyDao key = new ApiKeyDao(DatabaseConnector.INSTANCE.getUrlAPIKey(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		DatabaseConnector.INSTANCE.flushDatabaseConnectionEssentials();
		AlphaVantageConnector conn = new AlphaVantageConnector("alphavantageapi", key.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'alphavantage'", "token"));
		assertTrue(conn.isOperational());
	}
	
	@Test
	void isOperationalFalseTest() {
		AlphaVantageConnector conn = new AlphaVantageConnector("alphavantageapi", "");
		assertFalse(conn.isOperational());
	}
}
