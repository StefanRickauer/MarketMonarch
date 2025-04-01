package com.rickauer.marketmonarch.api.controller;

import java.io.Reader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.rickauer.marketmonarch.api.enums.FmpServiceRequest;
import com.rickauer.marketmonarch.api.request.RequestHandler;

public class FmpRequestController {

	private static Logger _fmpRequestLogger = LogManager.getLogger(FmpRequestController.class.getName());
	private String _token;
	private FmpServiceRequest _serviceRequest;

	@Deprecated
	public FmpRequestController(String token) {
		_token = token;
		_serviceRequest = FmpServiceRequest.COMPANY_SHARE_FLOAT;
	}

	public FmpRequestController(String token, FmpServiceRequest serviceRequest) {
		_token = token;
		_serviceRequest = serviceRequest;
	}

	@Deprecated
	public Long requestCompanyShareFloat(String symbol) {
		String request = String.format(_serviceRequest.getServiceRequest(), symbol, _token);

		return requestFloat(request, symbol);
	}

	private Long requestFloat(String request, String symbol) {
		_fmpRequestLogger.info("Requesting company share float for symbol: '" + symbol + "'.");
		String response = RequestHandler.sendRequest(request);

		Long companyShareFloat = -1L;

		if (response.contains("Exclusive Endpoint") || response.equals("")) {
			_fmpRequestLogger.error("Invalid request. Received:\n'" + response + "'.");
			return companyShareFloat;
		}

		try {
			Object responseObject = new JSONParser().parse(response);
			JSONArray array = (JSONArray) responseObject;
			JSONObject dataObject = (JSONObject) array.get(0);

			companyShareFloat = (Long) dataObject.get("floatShares");

		} catch (Exception e) {
			_fmpRequestLogger.error(e.getMessage());
		}
		return companyShareFloat;
	}

	public String requestAllShareFloat() {
		_fmpRequestLogger.info("Requesting all shares float...");
		String request = String.format(_serviceRequest.getServiceRequest(), _token);
		String response = "";
		
		response = RequestHandler.sendRequest(request);
		
		if (response.contains("Exclusive Endpoint") || response.equals("")) {
			_fmpRequestLogger.error("Invalid request. Received:\n'" + response + "'.");
			throw new RuntimeException("Invalid request.");
		}
		
		_fmpRequestLogger.info("Received all shares float.");
		return response;
	}
	
	public static Map<String, Long> convertResponseToMap(String response) {
		_fmpRequestLogger.info("Saving responses to map in order to increase performance...");
		
		HashMap<String, Long> convertedResponses = new HashMap<>();
		
		String sym = "";
		long fl = 0L;
		
		try {
			Object responseObject = new JSONParser().parse(response);
			JSONArray array = (JSONArray) responseObject;

			for (int i = 0; i < array.size(); i++) {
				JSONObject dataObject = (JSONObject) array.get(i);
				sym = (String) dataObject.get("symbol");
				
				if (dataObject.get("floatShares") == null) {
					continue;
				} else if (dataObject.get("floatShares").toString().contains(".")) {
					Double doubleFl = (Double) dataObject.get("floatShares");
					fl = doubleFl.longValue();
				} else {
					fl = (Long) dataObject.get("floatShares");
				}
				
				convertedResponses.put(sym , fl);
			}
		} catch (Exception e ) {
			_fmpRequestLogger.error("Error converting response:\n" + e);
		}
		
		_fmpRequestLogger.info("Saved responses to map.");
		
		return convertedResponses;
	}
}
