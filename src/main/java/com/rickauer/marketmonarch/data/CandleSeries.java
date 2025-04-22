package com.rickauer.marketmonarch.data;

import java.util.ArrayList;
import java.util.List;

import com.ib.client.Contract;

public class CandleSeries {

	private Contract _contract;
	private List<CandleStick> _series;
	
	public CandleSeries(Contract contract) {
		_contract = contract;
		_series = new ArrayList<>();
	}
	
	public Contract getContract() {
		return _contract;
	}
	
	public String getSymbol() {
		return _contract.symbol();
	}
	
	public List<CandleStick> getSeries() {
		return _series;
	}
	
	public void addCandleStick(CandleStick candle) {
		_series.add(candle);
	}
}
