package com.rickauer.marketmonarch.api.controller;

import com.rickauer.marketmonarch.api.request.StockNewsRequestHandler;

public class StockNewsRequestController {

	String _token;

	public StockNewsRequestController(String token) {
		_token = token;
	}
	
	public String requestSentimentScoreForToday(String symbol, SentimentFilterPeriod period) {
		
		String request = "https://stocknewsapi.com/api/v1/stat?&tickers=%s&date=%s&page=1&token=%s";
		request = String.format(request, symbol, period.getFilterPeriod(), _token);
		
		return StockNewsRequestHandler.sendRequest(request);
	}
}
