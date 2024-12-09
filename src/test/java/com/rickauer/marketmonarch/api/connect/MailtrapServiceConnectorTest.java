package com.rickauer.marketmonarch.api.connect;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.rickauer.marketmonarch.api.connect.MailtrapServiceConnector;
import com.rickauer.marketmonarch.configuration.ConfigReader;
import com.rickauer.marketmonarch.db.ApiKeyAccess;

public class MailtrapServiceConnectorTest {

	@Test
	void isOperationalTrueTest() {
		ConfigReader.INSTANCE.initializeConfigReader();
		ApiKeyAccess key = new ApiKeyAccess(ConfigReader.INSTANCE.getUrlAPIKey(), ConfigReader.INSTANCE.getUsername(), ConfigReader.INSTANCE.getPassword());
		ConfigReader.INSTANCE.flushDatabaseConnectionEssentials();
		MailtrapServiceConnector conn = new MailtrapServiceConnector("mailtrap", key.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'mailtrap'", "token"));
		assertTrue(conn.isOperational());
	}

	@Test
	void isOperationalFalseTest() {
		MailtrapServiceConnector conn = new MailtrapServiceConnector("mailtrap", "");
		assertFalse(conn.isOperational());
	}

}
