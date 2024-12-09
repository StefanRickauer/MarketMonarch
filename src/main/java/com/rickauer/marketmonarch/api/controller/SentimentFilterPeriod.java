package com.rickauer.marketmonarch.api.controller;

public enum SentimentFilterPeriod {
	// sentiment filter periods: today, yesterday, last7days, last30days, last60days, last90days, yeartodate
	TODAY("today"),
	YESTERDAY("yesterday"),
	LAST_SEVEN_DAYS("last7days"),
	LAST_THIRTY_DAYS("last30days"),
	LAST_SIXTY_DAYS("last60days"),
	LAST_NINETY_DAYS("last60days"),
	YEAR_TO_DATE("yeartodate");
	
	private String filterPeriod;
	
	private SentimentFilterPeriod(String period) {
		filterPeriod = period;
	}
	
	public String getFilterPeriod() {
		return filterPeriod;
	}
}
