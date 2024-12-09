package com.rickauer.marketmonarch.api.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StockNewsRequestControllerTest {

	@Test
	void test() {
		StockNewsRequestController controller = new StockNewsRequestController("oysmwnahbww41g6io6hlspu2sloee6phmffbioa6");
		System.out.println(controller.requestSentimentScoreForToday("AAPL", SentimentFilterPeriod.TODAY));
	}

}
