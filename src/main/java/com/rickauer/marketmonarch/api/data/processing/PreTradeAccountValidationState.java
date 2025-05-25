package com.rickauer.marketmonarch.api.data.processing;

import java.util.List;

import com.rickauer.marketmonarch.api.data.AccountSummaryItem;

public class PreTradeAccountValidationState extends PreTradeState {

	public PreTradeAccountValidationState(PreTradeContext context) {
		super(context);
	}

	@Override
	public void onEnter() {
		List<AccountSummaryItem> cash = _preTradeContext.getController().getAccountSummary("TotalCashValue"); 
		_preTradeContext.getAccountDetails().addAll(cash);
		List<AccountSummaryItem> grossPosition = _preTradeContext.getController().getAccountSummary("GrossPositionValue");
		_preTradeContext.getAccountDetails().addAll(grossPosition);
	}
	
	; // Don't forget to implement status change!

}
