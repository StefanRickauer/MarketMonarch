package com.rickauer.marketmonarch.api.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class StockNewsRequestHandler {
	
	private StockNewsRequestHandler() {
		throw new UnsupportedOperationException();
	}

	public static String sendRequest(String request) {

		URL apiURL;
		HttpURLConnection connection;
		StringBuilder response = new StringBuilder();
		
		try {
			apiURL = new URI(request).toURL();
			connection = (HttpURLConnection) apiURL.openConnection();
			connection.setRequestProperty("accept", "application/json");
			InputStream responseStream = connection.getInputStream();
			
			try (Reader reader = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {
				int c = 0;
				while ((c = reader.read()) != -1) {
					response.append((char) c);
				}
			}
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}

		return response.toString();
	}
}
