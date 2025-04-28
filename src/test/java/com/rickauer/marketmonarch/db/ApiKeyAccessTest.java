package com.rickauer.marketmonarch.db;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import com.rickauer.marketmonarch.configuration.DatabaseConnector;

public class ApiKeyAccessTest {

	@Test
	void queryDatabaseTest() {
		DatabaseConnector.INSTANCE.initializeConfigReader();
		ApiKeyDao db = new ApiKeyDao(DatabaseConnector.INSTANCE.getUrlAPIKey(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		
		try (ResultSet result = db.executeSqlQuery("SELECT token FROM credentials where provider = 'mailtrap'")) {
			
			// If db.executeSqlQuery finds nothing, result.next() will be false and the else-brach will be executed (verified by querying a non existent database).
			if (result.next())
				assertTrue(true);
			else 
				assertTrue(false);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		DatabaseConnector.INSTANCE.flushDatabaseConnectionEssentials();
	}

}
