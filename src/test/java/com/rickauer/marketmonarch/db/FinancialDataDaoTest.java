package com.rickauer.marketmonarch.db;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.rickauer.marketmonarch.configuration.DatabaseConnector;
import com.rickauer.marketmonarch.db.data.TradeDto;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class FinancialDataDaoTest {

	private static final String ID = "9999999";
	private static final String SYMBOL = "Test-Symbol";
	private static final String BUY_ID = "2";
	private static final String SELL_ID = "3";
	private static final String ENTRY_PRICE = "15.0";
	private static final String EXIT_PRICE = "20.0";
	private static final String QUANTITY = "100";
	private static final String ENTRY_TIME = "2025-04-23 21:16:35";
	private static final String EXIT_TIME = "2025-04-28 21:16:35";
	private static final String STOP_LOSS = "12.0";
	private static final String TAKE_PROFIT = "19.8";
	private static final String ORDER_EFFICIENCY_RATIO = "0.1";
	public static final String INSERTION_QUERY = String.format("INSERT INTO trade VALUES(%s, '%s', %s, %s, %s, %s, %s, '%s', '%s', %s, %s, %s)", 
			ID, SYMBOL, BUY_ID, SELL_ID, ENTRY_PRICE, EXIT_PRICE, QUANTITY, ENTRY_TIME, EXIT_TIME, STOP_LOSS, TAKE_PROFIT, ORDER_EFFICIENCY_RATIO);
	
	
	@Test
	void A_insertIntoDatabaseTest() {
		
		DatabaseConnector.INSTANCE.initializeDatabaseConnector();
		FinancialDataDao db = new FinancialDataDao(DatabaseConnector.INSTANCE.getUrlFinancialData(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		
		int result = db.executeSqlUpdate(INSERTION_QUERY);
		assertTrue(result != 0);		
		
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
			System.out.println(trade);
			if (trade.getId() != Integer.parseInt(ID)) {
				continue;		// skip other entries in case there is more data
			}

			assertEquals(Integer.parseInt(ID), trade.getId());
			assertEquals(SYMBOL, trade.getSymbol());
			assertEquals(Integer.parseInt(BUY_ID), trade.getBuyOrderId());
			assertEquals(Integer.parseInt(SELL_ID), trade.getSellOrderId());
			assertEquals(Double.parseDouble(ENTRY_PRICE), trade.getEntryPrice());
			assertEquals(Double.parseDouble(EXIT_PRICE), trade.getExitPrice());
			assertEquals(Integer.parseInt(QUANTITY), trade.getQuantity());
			assertEquals(LocalDateTime.parse(ENTRY_TIME.replace(" ", "T")), trade.getEntryTime());
			assertEquals(LocalDateTime.parse(EXIT_TIME.replace(" ", "T")), trade.getExitTime());
			assertEquals(Double.parseDouble(STOP_LOSS), trade.getStopLoss());
			assertEquals(Double.parseDouble(TAKE_PROFIT), trade.getTakeProfit());
			assertEquals(Double.parseDouble(ORDER_EFFICIENCY_RATIO), trade.getOrderEfficiencyRatio());
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
