package com.rickauer.marketmonarch.api.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.rickauer.marketmonarch.api.request.InteractiveBrokersApiRequestHandler;
import com.rickauer.marketmonarch.api.request.StockNewsRequestHandler;

public final class StockNewsRequestController {
	
	private static Logger _stockNewsRequestControllerLogger = LogManager.getLogger(StockNewsRequestController.class.getName());
	String _token;

	public StockNewsRequestController(String token) {
		_token = token;
	}
	
	public String requestSentimentScore(String symbol, SentimentFilterPeriod period) {
		
		String request = "https://stocknewsapi.com/api/v1/stat?&tickers=%s&date=%s&page=1&token=%s";
		request = String.format(request, symbol, period.getFilterPeriod(), _token);
		
		return requestSentiment(request, symbol);
	}
	
	private String requestSentiment(String request, String symbol) {
		_stockNewsRequestControllerLogger.info("Requesting data for symbol: '" + symbol + "'.");
		String response = StockNewsRequestHandler.sendRequest(request);
		
		try {
			Object object = new JSONParser().parse(response);
			JSONObject jsonObj = (JSONObject) object;
			Object unknownObject = jsonObj.get("total");
			
			// If response is empty, "total" will be of type JSONArray, if not it will be of type JSONObject
			boolean isEmpty = unknownObject instanceof JSONArray;
			
			if (isEmpty) {
				_stockNewsRequestControllerLogger.warn("No data found for '" + symbol + "' for requested period.");
				return "";
			} else {
				JSONObject data = (JSONObject) jsonObj.get("total");
				JSONObject stockData = (JSONObject) data.get(symbol);
				double sentiment = (double) stockData.get("Sentiment Score");
				
				return Double.toString(sentiment);
			}
		} catch (ParseException e) {
			throw new RuntimeException("Error processing answer.", e);
		}
	}
}
