package com.rickauer.marketmonarch.api;

import java.sql.SQLException;

import com.rickauer.marketmonarch.db.DBAccess;
import com.rickauer.marketmonarch.utils.Verifyable;

public abstract class ApiServiceConnector implements Verifyable {
	protected String _provider;
	protected String _token;
	
	public ApiServiceConnector(final String provider, final String token) {
		_provider = provider;
		_token = token;
	}
	
	public boolean isOperational() {
		return (!_token.equals("")) && (_token != null); 
	}

}
