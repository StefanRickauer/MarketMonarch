package com.rickauer.marketmonarch.db;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.rickauer.marketmonarch.configuration.DatabaseConnector;
import com.rickauer.marketmonarch.db.data.TradeDto;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class FinancialDataDaoTest {

	private static final int ID = 1;
	private static final String SYMBOL = "Test-Symbol";
	private static final double ENTRY_PRICE = 15.0;
	private static final double EXIT_PRICE = 20.0;
	private static final int QUANTITY = 100;
	private static final LocalDateTime ENTRY_TIME = ZonedDateTime.now().toLocalDateTime();
	private static final LocalDateTime EXIT_TIME = ZonedDateTime.now().plusMinutes(5).toLocalDateTime();
	private static final double STOP_LOSS = 12.0;
	private static final double TAKE_PROFIT = 19.8;
	private static final double ORDER_EFFICIENCY_RATIO = 0.1;
	
	@Test
	void A_insertIntoDatabaseTest() {
		TradeDto testData = new TradeDto();
		testData.setSymbol(SYMBOL);
		testData.setEntryPrice(ENTRY_PRICE);
		testData.setExitPrice(EXIT_PRICE);
		testData.setQuantity(QUANTITY);
		testData.setEntryTime(ENTRY_TIME);
		testData.setExitTime(EXIT_TIME);
		testData.setStopLoss(STOP_LOSS);
		testData.setTakeProfit(TAKE_PROFIT);
		testData.setOrderEfficiencyRatio(ORDER_EFFICIENCY_RATIO);
		
		DatabaseConnector.INSTANCE.initializeDatabaseConnector();
		FinancialDataDao db = new FinancialDataDao(DatabaseConnector.INSTANCE.getUrlFinancialData(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		
		int result = db.insertRow(testData);  
		assertTrue(result != 0);		
		
	}

	@Test
	void B_queryDatabaseTest() {
		DatabaseConnector.INSTANCE.initializeDatabaseConnector();
		FinancialDataDao db = new FinancialDataDao(DatabaseConnector.INSTANCE.getUrlFinancialData(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		
		try {
			List<TradeDto> trades = db.executeTradeQuery("SELECT * FROM trade where symbol = 'Test-Symbol'");
			// If db.executeSqlQuery finds nothing, result.next() will be false and the else-brach will be executed (verified by querying a non existent database).
			for (TradeDto trade : trades) {
				int entryPrice = (int)trade.getEntryPrice();
				assertEquals(15, entryPrice);
			}
			
			if (trades.isEmpty()) {
				assertTrue(false);
			}
			
		} catch (Exception e) {
//			assertTrue(false);
			e.printStackTrace();
		}
	}
	
	@Test
	void C_getAllTrades() {
		DatabaseConnector.INSTANCE.initializeDatabaseConnector();
		FinancialDataDao db = new FinancialDataDao(DatabaseConnector.INSTANCE.getUrlFinancialData(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		
		List<TradeDto> trades = db.getAllTrades();
		
		for (TradeDto trade : trades) {
			
			if (trade.getId() != ID) {
				continue;		// skip other entries in case there is more data
			}

			assertEquals(ID, trade.getId());
			assertEquals(SYMBOL, trade.getSymbol());
			assertEquals(ENTRY_PRICE, trade.getEntryPrice());
			assertEquals(EXIT_PRICE, trade.getExitPrice());
			assertEquals(QUANTITY, trade.getQuantity());
			assertEquals(ENTRY_TIME, trade.getEntryTime());
			assertEquals(EXIT_TIME, trade.getExitTime());
			assertEquals(STOP_LOSS, trade.getStopLoss());
			assertEquals(TAKE_PROFIT, trade.getTakeProfit());
			assertEquals(ORDER_EFFICIENCY_RATIO, trade.getOrderEfficiencyRatio());
		}
	}
	
	@Test
	void D_deleteFromDatabaseTest() {
		DatabaseConnector.INSTANCE.initializeDatabaseConnector();
		FinancialDataDao db = new FinancialDataDao(DatabaseConnector.INSTANCE.getUrlFinancialData(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		
		int rows = 0;
		rows = db.executeSqlUpdate("DELETE FROM trade WHERE symbol = 'Test-Symbol'");
		
		assertTrue(rows != 0);
		
		DatabaseConnector.INSTANCE.flushDatabaseConnectionEssentials();
	}
}
