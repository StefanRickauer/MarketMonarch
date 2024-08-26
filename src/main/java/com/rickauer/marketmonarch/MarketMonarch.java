package com.rickauer.marketmonarch;

public class MarketMonarch {

	private static final String PROGRAM	= "MarketMonarch";
	private static final String VERSION	= "0.01";
	
	public static void main(String[] args) {
		try {
			// TODO: Logger is supposed to log to console as well as log file.
			System.out.println("Starting " + PROGRAM + " (version " + VERSION + ")...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
