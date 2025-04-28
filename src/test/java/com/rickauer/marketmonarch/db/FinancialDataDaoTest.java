package com.rickauer.marketmonarch.db;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.rickauer.marketmonarch.configuration.DatabaseConnector;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class FinancialDataDaoTest {

	@Test
	void A_insertIntoDatabaseTest() {
		DatabaseConnector.INSTANCE.initializeDatabaseConnector();
		FinancialDataDao db = new FinancialDataDao(DatabaseConnector.INSTANCE.getUrlFinancialData(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		
		int rows = 0;
		rows = db.executeSqlUpdate("INSERT INTO trade VALUES(9999999, 2, 3, 15.0, 20.0, 100, '2025-04-23 21:16:35', '2025-04-28 21:16:35', 12.0)");
		
		assertTrue(rows != 0);
	}

	@Test
	void B_queryDatabaseTest() {
		DatabaseConnector.INSTANCE.initializeDatabaseConnector();
		ApiKeyDao db = new ApiKeyDao(DatabaseConnector.INSTANCE.getUrlFinancialData(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		
		try (ResultSet result = db.executeSqlQuery("SELECT entry_price FROM trade where id = 9999999")) {
			
			// If db.executeSqlQuery finds nothing, result.next() will be false and the else-brach will be executed (verified by querying a non existent database).
			if (result.next()) {
				int entryPrice = (int)result.getDouble("entry_price");
				assertEquals(15, entryPrice);
			}
			else 
				assertTrue(false);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void C_deleteFromDatabaseTest() {
		DatabaseConnector.INSTANCE.initializeDatabaseConnector();
		FinancialDataDao db = new FinancialDataDao(DatabaseConnector.INSTANCE.getUrlFinancialData(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		
		int rows = 0;
		rows = db.executeSqlUpdate("DELETE FROM trade WHERE id = 9999999");
		
		assertTrue(rows != 0);
		
		DatabaseConnector.INSTANCE.flushDatabaseConnectionEssentials();
	}
}
