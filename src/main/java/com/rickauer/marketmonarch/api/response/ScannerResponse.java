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
	
	public void addItem(Integer rank, Contract contract) {
		_ranking.put(rank, contract);
		synchronized(_lock) {
			; // TODO: Find better solution 
			if (_ranking.size() >= 50) {
				_lock.notify();
			}
		}
	}
	
	public Map<Integer, Contract> getRankings() {
		return _ranking;
	}
}
