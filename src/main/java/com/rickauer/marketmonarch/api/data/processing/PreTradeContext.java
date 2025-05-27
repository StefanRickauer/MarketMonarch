package com.rickauer.marketmonarch.api.data.processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rickauer.marketmonarch.api.controller.FmpRequestController;
import com.rickauer.marketmonarch.api.controller.InteractiveBrokersApiController;
import com.rickauer.marketmonarch.api.data.AccountSummaryItem;

public class PreTradeContext {

	PreTradeState _state;
	private final InteractiveBrokersApiController _ibController;
	private final FmpRequestController _fmpController;
	private List<AccountSummaryItem> _accountSummary;
	private static Map<String, Long> _allCompanyFloats;
	
	public PreTradeContext(InteractiveBrokersApiController ibController, FmpRequestController fmpController) {
		_ibController = ibController;
		_fmpController = fmpController;
		_accountSummary = new ArrayList<>();
		_allCompanyFloats = new HashMap<>();
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
	
	public void setState(PreTradeState state) {
		_state = state;
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
}
