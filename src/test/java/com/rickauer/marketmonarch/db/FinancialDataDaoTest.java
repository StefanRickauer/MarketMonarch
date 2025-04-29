package com.rickauer.marketmonarch.db;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDateTime;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.rickauer.marketmonarch.configuration.DatabaseConnector;
import com.rickauer.marketmonarch.db.data.TradeDto;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class FinancialDataDaoTest {

	private static final String id = "9999999";
	private static final String symbol = "APPL";
	private static final String buyId = "2";
	private static final String sellId = "3";
	private static final String entry = "15.0";
	private static final String exit = "20.0";
	private static final String quan = "100";
	private static final String entryTime = "2025-04-23 21:16:35";
	private static final String exitTime = "2025-04-28 21:16:35";
	private static final String stopLoss = "12.0";
	public static final String query = String.format("INSERT INTO trade VALUES(%s, '%s', %s, %s, %s, %s, %s, '%s', '%s', %s)", 
			id, symbol, buyId, sellId, entry, exit, quan, entryTime, exitTime, stopLoss);
	
	@Test
	void A_insertIntoDatabaseTest() {
		DatabaseConnector.INSTANCE.initializeDatabaseConnector();
		FinancialDataDao db = new FinancialDataDao(DatabaseConnector.INSTANCE.getUrlFinancialData(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		
		int rows = 0;
		rows = db.executeSqlUpdate(query);
		
		assertTrue(rows != 0);
	}

	@Test
	void B_queryDatabaseTest() {
		DatabaseConnector.INSTANCE.initializeDatabaseConnector();
		FinancialDataDao db = new FinancialDataDao(DatabaseConnector.INSTANCE.getUrlFinancialData(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		
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
	void C_getAllTrades() {
		DatabaseConnector.INSTANCE.initializeDatabaseConnector();
		FinancialDataDao db = new FinancialDataDao(DatabaseConnector.INSTANCE.getUrlFinancialData(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		
		List<TradeDto> trades = db.getAllTrades();
		
		for (TradeDto trade : trades) {
			
			if (trade.getId() != Integer.parseInt(id)) {
				continue;		// skip other entries in case there is more data
			}

			assertEquals(symbol, trade.getSymbol());
			assertEquals(Integer.parseInt(buyId), trade.getBuyOrderId());
			assertEquals(Integer.parseInt(sellId), trade.getSellOrderId());
			assertEquals(Double.parseDouble(entry), trade.getEntryPrice());
			assertEquals(Double.parseDouble(exit), trade.getExitPrice());
			assertEquals(Integer.parseInt(quan), trade.getQuantity());
			assertEquals(LocalDateTime.parse(entryTime.replace(" ", "T")), trade.getEntryTime());
			assertEquals(LocalDateTime.parse(exitTime.replace(" ", "T")), trade.getExitTime());
		}
	}
	
	@Test
	void D_deleteFromDatabaseTest() {
		DatabaseConnector.INSTANCE.initializeDatabaseConnector();
		FinancialDataDao db = new FinancialDataDao(DatabaseConnector.INSTANCE.getUrlFinancialData(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		
		int rows = 0;
		rows = db.executeSqlUpdate("DELETE FROM trade WHERE id = 9999999");
		
		assertTrue(rows != 0);
		
		DatabaseConnector.INSTANCE.flushDatabaseConnectionEssentials();
	}
}
