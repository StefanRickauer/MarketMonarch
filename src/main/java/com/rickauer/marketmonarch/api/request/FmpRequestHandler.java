package com.rickauer.marketmonarch.api.request;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

public final class FmpRequestHandler {

	private FmpRequestHandler() {
		throw new UnsupportedOperationException();
	}
	
	public static String sendRequest(String request) {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URI(request).toURL();
			Reader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			
			int c = 0;
			while ( (c = reader.read()) != -1 ) {
				response.append((char)c);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response.toString();
	}
}
