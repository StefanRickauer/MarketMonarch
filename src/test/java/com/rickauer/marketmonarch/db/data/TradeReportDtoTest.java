package com.rickauer.marketmonarch.db.data;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.rickauer.marketmonarch.configuration.DatabaseConnector;
import com.rickauer.marketmonarch.db.FinancialDataDao;

public class TradeReportDtoTest {

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
	void constructorCallTest() {
		List<TradeDto> allTrades = new ArrayList<>();
		
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
		
		allTrades = db.getAllTrades();
		
		TradeReportDto tr = new TradeReportDto(allTrades, testData);
		
		assertEquals(5, (int)tr.getProfitPerStock());
	}

}
