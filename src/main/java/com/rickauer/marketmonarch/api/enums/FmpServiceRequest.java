package com.rickauer.marketmonarch.api.enums;

public enum FmpServiceRequest {

	COMPANY_SHARE_FLOAT("https://financialmodelingprep.com/api/v4/shares_float?symbol=%s&apikey=%s");
	
	private String _request;
	
	private FmpServiceRequest(String request) {
		_request = request;
	}
	
	public String getServiceRequest() {
		return _request;
	}
}
