package com.rickauer.marketmonarch.api.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.rickauer.marketmonarch.api.enums.SentimentFilterPeriod;
import com.rickauer.marketmonarch.api.enums.StockNewsServiceRequest;
import com.rickauer.marketmonarch.api.request.InteractiveBrokersApiRequestHandler;
import com.rickauer.marketmonarch.api.request.StockNewsRequestHandler;

public final class StockNewsRequestController {
	
	private static Logger _stockNewsRequestControllerLogger = LogManager.getLogger(StockNewsRequestController.class.getName());
	private String _token;

	public StockNewsRequestController(String token) {
		_token = token;
	}
	
	// The Sentiment Score ranges from -1.5 (Negative) to +1.5 (Positive) and is based on the # of positive and negative news on a specific time frame
	public double requestSentimentScore(String symbol, SentimentFilterPeriod period) {
		
		String request = String.format(StockNewsServiceRequest.SENTIMENT.getServiceRequest(), symbol, period.getFilterPeriod(), _token);
		
		return requestSentiment(request, symbol);
	}
	
	private double requestSentiment(String request, String symbol) {
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
				return 0.0;
			} else {
				JSONObject data = (JSONObject) jsonObj.get("total");
				JSONObject stockData = (JSONObject) data.get(symbol);
				double sentiment = (double) stockData.get("Sentiment Score");
				
				return sentiment;
			}
		} catch (ParseException e) {
			throw new RuntimeException("Error processing answer.", e);
		}
	}
}
