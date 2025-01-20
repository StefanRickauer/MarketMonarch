package com.rickauer.marketmonarch.api.request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class AlphaVantageRequestHandler {
	
	private static Logger _alphaVantageRequestHandlerLogger = LogManager.getLogger(AlphaVantageRequestHandler.class.getName());
	
	private AlphaVantageRequestHandler() {
		throw new UnsupportedOperationException();
	}
	
	public static String sendRequest(String request) {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URI(request).toURL();
			Reader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			
			int c = 0;
			while ( (c = reader.read()) != -1) {
				response.append((char)c);
			}
		} catch (Exception e) {
			_alphaVantageRequestHandlerLogger.error(e.getMessage());
		}
		return response.toString();
	}
}
