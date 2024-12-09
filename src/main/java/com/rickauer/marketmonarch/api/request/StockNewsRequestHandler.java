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

public class StockNewsRequestHandler {

	public static String sendRequest(String request) {

		URL singleTicker;
		HttpURLConnection conn;
		StringBuilder responseText = new StringBuilder();
		
		try {
			singleTicker = new URI(request).toURL();
			conn = (HttpURLConnection) singleTicker.openConnection();
			conn.setRequestProperty("accept", "application/json");
			InputStream responseStream = conn.getInputStream();
			
			try (Reader reader = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {
				int c = 0;
				while ((c = reader.read()) != -1) {
					responseText.append((char) c);
				}
			}
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}

		return responseText.toString();
	}
}
