package com.rickauer.marketmonarch.api.enums;

public enum AlphaVantageServiceSentimentRequest {

	SENTIMENT("https://www.alphavantage.co/query?function=NEWS_SENTIMENT&tickers=%s&time_from=%s&apikey=%s");
	
	private String _request;
	
	private AlphaVantageServiceSentimentRequest(String request) {
		_request = request;
	}
	
	public String getServiceRequest() {
		return _request;
	}
}
