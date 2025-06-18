package com.rickauer.marketmonarch.api.connect;

import com.rickauer.marketmonarch.utils.Verifyable;

public abstract class ApiServiceConnector implements Verifyable {
	private String _provider;
	private String _token;
	
	public ApiServiceConnector(final String provider, final String token) {
		_provider = provider;
		_token = token;
	}
	
	public String getProvider() {
		return _provider;
	}
	
	public String getToken() {
		return _token;
	}
	
	public boolean isOperational() {
		return (!_token.equals("")) && (_token != null); 
	}

}
