package com.rickauer.marketmonarch.db;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import com.rickauer.marketmonarch.configuration.ConfigReader;

public class ApiKeyAccessTest {

	@Test
	void queryDatabaseTest() {
		ConfigReader.INSTANCE.initializeConfigReader();
		ApiKeyAccess db = new ApiKeyAccess(ConfigReader.INSTANCE.getUrlAPIKey(), ConfigReader.INSTANCE.getUsername(), ConfigReader.INSTANCE.getPassword());
		
		try (ResultSet result = db.executeSqlQuery("SELECT token FROM credentials where provider = 'mailtrap'")) {
			
			// If db.executeSqlQuery finds nothing, result.next() will be false and the else-brach will be executed (verified by querying a non existent database).
			if (result.next())
				assertTrue(true);
			else 
				assertTrue(false);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		ConfigReader.INSTANCE.flushDatabaseConnectionEssentials();
	}

}
