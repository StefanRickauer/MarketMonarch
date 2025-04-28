package com.rickauer.marketmonarch.api.connect;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.rickauer.marketmonarch.api.connect.MailtrapServiceConnector;
import com.rickauer.marketmonarch.configuration.DatabaseConnector;
import com.rickauer.marketmonarch.db.ApiKeyDao;

public class MailtrapServiceConnectorTest {

	@Test
	void isOperationalTrueTest() {
		DatabaseConnector.INSTANCE.initializeConfigReader();
		ApiKeyDao key = new ApiKeyDao(DatabaseConnector.INSTANCE.getUrlAPIKey(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		DatabaseConnector.INSTANCE.flushDatabaseConnectionEssentials();
		MailtrapServiceConnector conn = new MailtrapServiceConnector("mailtrap", key.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'mailtrap'", "token"));
		assertTrue(conn.isOperational());
	}

	@Test
	void isOperationalFalseTest() {
		MailtrapServiceConnector conn = new MailtrapServiceConnector("mailtrap", "");
		assertFalse(conn.isOperational());
	}

}
