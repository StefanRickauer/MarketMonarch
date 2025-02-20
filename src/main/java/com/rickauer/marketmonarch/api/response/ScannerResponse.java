package com.rickauer.marketmonarch.api.response;

import java.util.HashMap;
import java.util.Map;

import com.ib.client.Contract;

public class ScannerResponse {

	Map<Integer, Contract> _ranking;
	Object _lock;
	
	public ScannerResponse(Object lock) {
		_ranking = new HashMap<>();
		_lock = lock;
	}
	
	; // add einbauen
}
