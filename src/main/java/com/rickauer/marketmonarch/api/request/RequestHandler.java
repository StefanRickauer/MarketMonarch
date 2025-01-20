package com.rickauer.marketmonarch.api.request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RequestHandler {

	private static Logger _requestHandlerLogger = LogManager.getLogger(RequestHandler.class.getName());

	private RequestHandler() {
		throw new UnsupportedOperationException();
	}

	public static String sendRequest(String request) {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URI(request).toURL();
			Reader reader = new BufferedReader(new InputStreamReader(url.openStream()));

			int c = 0;
			while ((c = reader.read()) != -1) {
				response.append((char) c);
			}
		} catch (Exception e) {
			_requestHandlerLogger.error(e.getMessage());
		}
		return response.toString();
	}
}
