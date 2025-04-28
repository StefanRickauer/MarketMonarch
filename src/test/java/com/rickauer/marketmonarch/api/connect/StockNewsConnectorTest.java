package com.rickauer.marketmonarch.api.connect;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.rickauer.marketmonarch.api.connect.StockNewsConnector;
import com.rickauer.marketmonarch.configuration.DatabaseConnector;
import com.rickauer.marketmonarch.db.ApiKeyDao;

public class StockNewsConnectorTest {

	// StockNews-API key and account were deleted. Refresh to use the StockNews-API. Until then, all tests will fail.
	
	@Test
	void isOperationalTrueTest() {
		DatabaseConnector.INSTANCE.initializeConfigReader();
		ApiKeyDao key = new ApiKeyDao(DatabaseConnector.INSTANCE.getUrlAPIKey(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		DatabaseConnector.INSTANCE.flushDatabaseConnectionEssentials();
		StockNewsConnector conn = new StockNewsConnector("stocknewsapi", key.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'mailtrap'", "token"));
		assertTrue(conn.isOperational());
	}

	@Test
	void isOperationalFalseTest() {
		StockNewsConnector conn = new StockNewsConnector("stocknewsapi", "");
		assertFalse(conn.isOperational());
	}

}
