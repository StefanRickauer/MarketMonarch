package com.rickauer.marketmonarch.db;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.rickauer.marketmonarch.configuration.DatabaseConnector;

public class ApiKeyDaoTest {

	@Test
	void queryDatabaseTest() {
		DatabaseConnector.INSTANCE.initializeDatabaseConnector();
		ApiKeyDao db = new ApiKeyDao(DatabaseConnector.INSTANCE.getUrlAPIKey(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		
		try {
			String result = db.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'mailtrap'", "token");
			// If db.executeSqlQuery finds nothing, result.next() will be false and the else-brach will be executed (verified by querying a non existent database).
			if (result != null)
				assertTrue(true);
			else 
				assertTrue(false);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		DatabaseConnector.INSTANCE.flushDatabaseConnectionEssentials();
	}

}
