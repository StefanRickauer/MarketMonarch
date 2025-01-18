package com.rickauer.marketmonarch.api.enums;

public enum StockNewsServiceSentimentRequest {

	SENTIMENT("https://stocknewsapi.com/api/v1/stat?&tickers=%s&date=%s&page=1&token=%s");
	
	private String _request;
	
	private StockNewsServiceSentimentRequest(String request) {
		_request = request;
	}
	
	public String getServiceRequest() {
		return _request;
	}
}
