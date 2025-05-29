package com.rickauer.marketmonarch.api.data.processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ib.client.Contract;
import com.rickauer.marketmonarch.api.controller.FmpRequestController;
import com.rickauer.marketmonarch.api.controller.InteractiveBrokersApiController;
import com.rickauer.marketmonarch.api.data.AccountSummaryItem;

public class PreTradeContext {

	PreTradeState _state;
	private InteractiveBrokersApiController _ibController;
	private List<AccountSummaryItem> _accountSummary;
	private Map<String, Long> _allCompanyFloats;
	private Map<Integer, Contract> _scanResult;
	
	public PreTradeContext(InteractiveBrokersApiController controller) {
		_ibController = controller;
		_accountSummary = new ArrayList<>();
		_allCompanyFloats = new HashMap<>();
		_scanResult = new HashMap<>();
	}
	
	public InteractiveBrokersApiController getIbController() {
		return _ibController;
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
	
	public double getTotalCash() {
		for (AccountSummaryItem item : _accountSummary) {
			if (item.getTag().equals("TotalCashValue")) {
				return item.getValueAsDouble();
			}
		}
		return -1.0;
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
	
	public Map<String, Long> getAllCompanyFloats() {
		return _allCompanyFloats;
	}
	
	public Long getCompanyFloatForSymbol(String symbol) {
		return _allCompanyFloats.get(symbol.replace(" ", "-"));
	}
	
	public Map<Integer, Contract> getScanResult() {
		return _scanResult;
	}
}
