package com.rickauer.marketmonarch.api.connect;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.rickauer.marketmonarch.configuration.ConfigReader;
import com.rickauer.marketmonarch.db.ApiKeyAccess;

public class FmpConnectorTest {

	@Test
	void isOperationalTrueTest() {
		ConfigReader.INSTANCE.initializeConfigReader();
		ApiKeyAccess key = new ApiKeyAccess(ConfigReader.INSTANCE.getUrlAPIKey(), ConfigReader.INSTANCE.getUsername(), ConfigReader.INSTANCE.getPassword());
		ConfigReader.INSTANCE.flushDatabaseConnectionEssentials();
		FmpConnector conn = new FmpConnector("fmp", key.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'FMP'", "token"));
		assertTrue(conn.isOperational());
	}
	
	@Test
	void isOperationalFalseTest() {
		FmpConnector conn = new FmpConnector("fmp", "");
		assertFalse(conn.isOperational());
	}
}
