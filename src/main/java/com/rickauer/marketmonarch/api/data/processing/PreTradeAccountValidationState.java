package com.rickauer.marketmonarch.api.data.processing;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rickauer.marketmonarch.MarketMonarch;
import com.rickauer.marketmonarch.api.data.AccountSummaryItem;

public class PreTradeAccountValidationState extends PreTradeState {

	private static Logger _tradeAccountValidationLogger = LogManager.getLogger(PreTradeAccountValidationState.class.getName());
	
	public PreTradeAccountValidationState(PreTradeContext context) {
		super(context);
	}

	@Override
	public void onEnter() {
		_tradeAccountValidationLogger.info("Entered Account Validation State.");
		_preTradeContext.getController().getSocket().reqAccountSummary(_preTradeContext.getController().getNextRequestId(), "All", "NetLiquidation,TotalCashValue,AccruedCash,BuyingPower,GrossPositionValue"); 			
	}
	
	
	public void processAccountSummary(String logMessage, int reqId, String account, String tag, String value, String currency) {
		_tradeAccountValidationLogger.info(logMessage);
		_preTradeContext.getAccountDetails().add(new AccountSummaryItem(reqId, account, tag, value, currency));
	}

	public void processAccountSummaryEnd(int reqId) {
		_preTradeContext.getController().getSocket().cancelAccountSummary(reqId);
		
		_tradeAccountValidationLogger.info("Accured Cash (unrealized): " + _preTradeContext.getGrossPosition());
		_tradeAccountValidationLogger.info("Total Market Exposure: " + _preTradeContext.getGrossPosition());
		_tradeAccountValidationLogger.info("Net Liquidation: " + _preTradeContext.getNetLiquidation());
		_tradeAccountValidationLogger.info("Total Cash: " + _preTradeContext.getTotalCash());
		_tradeAccountValidationLogger.info("Buying Power: " + _preTradeContext.getBuyingPower());
		
		validateAccount();
		_tradeAccountValidationLogger.fatal("Account validation succeeded. Changing state.");
		
		_preTradeContext.setState(new PreTradeMarketScanningState(_preTradeContext));
	}

	private void validateAccount() {
		if ( ( (long)Math.floor(_preTradeContext.getTotalCash()) ) < MarketMonarch.MINIMUM_ACCOUNT_BALANCE) {
			_tradeAccountValidationLogger.fatal("Account validation failed: Less than " + MarketMonarch.MINIMUM_ACCOUNT_BALANCE + " Euros in cash available. Exiting.");
			System.exit(0);
		} 
	}
}
