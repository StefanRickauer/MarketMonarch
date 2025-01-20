package com.rickauer.marketmonarch.api.controller;

import java.io.Reader;
import java.io.IOException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.rickauer.marketmonarch.api.enums.FmpServiceRequest;
import com.rickauer.marketmonarch.api.request.FmpRequestHandler;

public class FmpRequestController {

	private static Logger _fmpRequestLogger = LogManager.getLogger(FmpRequestController.class.getName());
	private String _token;
	
	public FmpRequestController(String token) {
		_token = token;
	}
	
	public Long requestCompanyShareFloat(String symbol) {
		String request = String.format(FmpServiceRequest.COMPANY_SHARE_FLOAT.getServiceRequest(), symbol, _token);
		
		return requestFloat(request, symbol); 
	}
	
	private Long requestFloat(String request, String symbol) {
		_fmpRequestLogger.info("Requesting company share float for symbol: '" + symbol + "'.");
		String response = FmpRequestHandler.sendRequest(request);
		
		Long companyShareFloat = -1L;
		
		if (response.contains("Exclusive Endpoint") || response.equals("")) {
			_fmpRequestLogger.error("Invalid request. Received:\n'" + response + "'.");
			return companyShareFloat;
		}
		
		try {
			Object responseObject = new JSONParser().parse(response.toString());
			JSONArray array = (JSONArray) responseObject;
			JSONObject dataObject = (JSONObject) array.get(0);
			
			companyShareFloat = (Long) dataObject.get("floatShares");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return companyShareFloat;
	}
}
