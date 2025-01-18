package com.rickauer.marketmonarch.api.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.rickauer.marketmonarch.api.enums.AlphaVantageServiceSentimentRequest;
import com.rickauer.marketmonarch.api.request.AlphaVantageRequestHandler;

public final class AlphaVantageRequestController {
	
	private static Logger _alphaVantageRequestControllerLogger = LogManager.getLogger(AlphaVantageRequestController.class.getName());
	private String _token;
	
	public AlphaVantageRequestController(String token) {
		_token = token;
	}
	
	public double requestSentimentScore(String symbol) {
		
		String request = String.format(AlphaVantageServiceSentimentRequest.SENTIMENT.getServiceRequest(), symbol, getFormattedTimeForToday(), _token);
		
		return requestSentiment(request, symbol);
	}
	
	private String getFormattedTimeForToday() {
		DateTime now = DateTime.now();
		// time format = YYYYMMDDTHHMM 
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
		return now.toString(fmt) + "T0000";
	}
	
	private double requestSentiment(String request, String symbol) {
		_alphaVantageRequestControllerLogger.info("Requesting sentiment for symbol: '" + symbol + "'.");
		String response = AlphaVantageRequestHandler.sendRequest(request);
		
		int numberOfItems;

		try {
			Object responseObject = new JSONParser().parse(response);
			JSONObject jo = (JSONObject) responseObject;
			String items = (String) jo.get("items");

			numberOfItems = Integer.parseInt(items);
			double averageSentimentScore = -1.0;

			if (numberOfItems > 0) {

				double totalSentimentScore = 0.0;
				int numberOfOccurrences = 0;

				JSONArray feed = (JSONArray) jo.get("feed");
				for (int i = 0; i < numberOfItems; i++) {
					JSONObject newsItem = (JSONObject) feed.get(i);
					JSONArray tickerSentiment = (JSONArray) newsItem.get("ticker_sentiment");

					int numberOfSentiments = tickerSentiment.size();

					for (int j = 0; j < numberOfSentiments; j++) {
						JSONObject sentiment = (JSONObject) tickerSentiment.get(j);
						if (sentiment.get("ticker").equals(symbol)) {
							double score = Double.parseDouble((String) sentiment.get("ticker_sentiment_score"));
							totalSentimentScore += score;
							numberOfOccurrences++;
							System.out.println("Sentiment Score: " + score);
						}
					}
				}
				
				averageSentimentScore = (totalSentimentScore / numberOfOccurrences);
				_alphaVantageRequestControllerLogger.info("Found sentiment: " + averageSentimentScore + " (" + evaluateSentiment(averageSentimentScore) + ")");
			} else {
				_alphaVantageRequestControllerLogger.warn("Could not find sentiment.");
			}
			
			return averageSentimentScore;

		} catch (Exception e) {
			_alphaVantageRequestControllerLogger.warn("Error processing Alpha Vantage response.");
			throw new RuntimeException("Error processing Alpha Vantage response.");
		}
	}
	
	private String evaluateSentiment(double sentiment) {
		if (sentiment <= -0.35)
			return "Bearish";
		else if (sentiment > -0.35 && sentiment <= -0.15)
			return "Somehwhat-Bearish";
		else if (sentiment > -0.15 && sentiment < 0.15)
			return "Neutral";
		else if (sentiment >= 0.15 && sentiment < 0.35)
			return "Somewhat-Bullish";
		else if (sentiment >= 0.35)
			return "Bullish";
		else
			return "Invalid";
	}
}
