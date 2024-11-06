package com.rickauer.marketmonarch.api;

import java.sql.SQLException;

import com.rickauer.marketmonarch.db.DBAccess;
import com.rickauer.marketmonarch.utils.Verifyable;

public abstract class ApiServiceConnector implements Verifyable {
	private String _provider;
	private String _token;
	private String _query;
	
	public ApiServiceConnector(String provider, String token, String query) {
		_provider = provider;
		_token = token;
		_query = query;
	}
	
	
}
