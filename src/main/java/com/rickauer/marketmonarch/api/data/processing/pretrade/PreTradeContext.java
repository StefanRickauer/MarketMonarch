package com.rickauer.marketmonarch.api.data.processing.pretrade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.ib.client.Contract;
import com.rickauer.marketmonarch.api.controller.FmpRequestController;
import com.rickauer.marketmonarch.api.controller.InteractiveBrokersApiController;
import com.rickauer.marketmonarch.api.data.AccountSummaryItem;
import com.rickauer.marketmonarch.api.data.StockMetrics;
import com.rickauer.marketmonarch.constants.TradingConstants;
import com.rickauer.marketmonarch.utils.StockUtils;

public class PreTradeContext {

	PreTradeState _state;
	private InteractiveBrokersApiController _ibController;
	private FmpRequestController _fmpController;
	private List<AccountSummaryItem> _accountSummary;
	private Map<String, Long> _allCompanyFloats;
	private Map<Integer, Contract> _scanResult;
	private Map<Integer, StockMetrics> _historicalData;		
	private double _exchangeRateEurToUsd;
	
	public PreTradeContext(InteractiveBrokersApiController ibController, FmpRequestController fmpController) {
		_ibController = ibController;
		_fmpController = fmpController;
		_accountSummary = new ArrayList<>();
		_allCompanyFloats = new HashMap<>();
		_scanResult = new TreeMap<>();
		_historicalData = new HashMap<>();
		_exchangeRateEurToUsd = 0;
	}
	
	public InteractiveBrokersApiController getIbController() {
		return _ibController;
	}
	
	public FmpRequestController getFmpController() {
		return _fmpController;
	}
	
	public PreTradeState getState() {
		return _state;
	}
	
	public void setState(PreTradeState newState) {
		_state = newState;
		newState.onEnter();
	}
	
	public List<AccountSummaryItem> getAccountDetails() {
		return _accountSummary;
	}
	
	public double getTotalCashInEur() {
		for (AccountSummaryItem item : _accountSummary) {
			if (item.getTag().equals("TotalCashValue")) {
				return item.getValueAsDouble();
			}
		}
		return -1.0;
	}
	
	public double getTotalCashBufferedInUsd() {
		return StockUtils.roundValueDown((getTotalCashInEur() - TradingConstants.BALANCE_IN_EUR_PUFFER) * getExchangeRate()); 
	}
	
	public double getNetLiquidation() {
		for (AccountSummaryItem item : _accountSummary) {
			if (item.getTag().equals("NetLiquidation")) {
				return item.getValueAsDouble();
			}
		}
		return -1.0;
	}
	
	public double getGrossPosition() {
		for (AccountSummaryItem item : _accountSummary) {
			if (item.getTag().equals("GrossPositionValue")) {
				return item.getValueAsDouble();
			}
		}
		return -1.0;
	}

	public double getAccruedCash() {
		for (AccountSummaryItem item : _accountSummary) {
			if (item.getTag().equals("AccruedCash")) {
				return item.getValueAsDouble();
			}
		}
		return -1.0;
	}

	public double getBuyingPower() {
		for (AccountSummaryItem item : _accountSummary) {
			if (item.getTag().equals("BuyingPower")) {
				return item.getValueAsDouble();
			}
		}
		return -1.0;
	}
	
	public double getAvailableFunds() {
		for (AccountSummaryItem item: _accountSummary) {
			if (item.getTag().equals("AvailableFunds")) {
				return item.getValueAsDouble();
			}
		}
		return -1.0;
	}
	
	public Map<String, Long> getAllCompanyFloats() {
		return _allCompanyFloats;
	}
	
	public Long getCompanyFloatForSymbol(String symbol) {
		return _allCompanyFloats.get(symbol.replace(" ", "-"));
	}
	
	public Map<Integer, Contract> getScanResult() {
		return _scanResult;
	}
	
	public void setScanResult(Map<Integer, Contract> newResult) {
		_scanResult = new TreeMap<>(newResult);
	}
	
	public Map<Integer, StockMetrics> getHistoricalData() {
		return _historicalData;
	}
	
	public void setExchangeRate(double rate) {
		_exchangeRateEurToUsd = rate;
	}
	
	public double getExchangeRate() {
		return _exchangeRateEurToUsd;
	}
	
	public void clearDataForNextRun() {
		_accountSummary.clear();
		_allCompanyFloats.clear();
		_scanResult.clear();
		_historicalData.clear();
		_exchangeRateEurToUsd = 0;
	}
}
