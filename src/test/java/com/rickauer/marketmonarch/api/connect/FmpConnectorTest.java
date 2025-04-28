package com.rickauer.marketmonarch.api.connect;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.rickauer.marketmonarch.configuration.DatabaseConnector;
import com.rickauer.marketmonarch.db.ApiKeyDao;

public class FmpConnectorTest {

	@Test
	void isOperationalTrueTest() {
		DatabaseConnector.INSTANCE.initializeDatabaseConnector();
		ApiKeyDao key = new ApiKeyDao(DatabaseConnector.INSTANCE.getUrlAPIKey(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		DatabaseConnector.INSTANCE.flushDatabaseConnectionEssentials();
		FmpConnector conn = new FmpConnector("fmp", key.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'FMP'", "token"));
		assertTrue(conn.isOperational());
	}
	
	@Test
	void isOperationalFalseTest() {
		FmpConnector conn = new FmpConnector("fmp", "");
		assertFalse(conn.isOperational());
	}
}
