package com.rickauer.marketmonarch.api.enums;

public enum FmpServiceRequest {

	@Deprecated
	COMPANY_SHARE_FLOAT("https://financialmodelingprep.com/api/v4/shares_float?symbol=%s&apikey=%s"),						// API subscription required
	
	ALL_SHARES_FLOAT("https://financialmodelingprep.com/stable/shares-float-all?apikey=%s");	// no API subscription required
	
	private String _request;
	
	private FmpServiceRequest(String request) {
		_request = request;
	}
	
	public String getServiceRequest() {
		return _request;
	}
}
