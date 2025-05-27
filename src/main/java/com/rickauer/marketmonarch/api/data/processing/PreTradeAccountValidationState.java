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
		_context.getIbController().getSocket().reqAccountSummary(_context.getIbController().getNextRequestId(), "All", "NetLiquidation,TotalCashValue,AccruedCash,BuyingPower,GrossPositionValue"); 			
	}
	
	
	public void processAccountSummary(String logMessage, int reqId, String account, String tag, String value, String currency) {
		_tradeAccountValidationLogger.info(logMessage);
		_context.getAccountDetails().add(new AccountSummaryItem(reqId, account, tag, value, currency));
	}

	public void processAccountSummaryEnd(int reqId) {
		_context.getIbController().getSocket().cancelAccountSummary(reqId);
		
		_tradeAccountValidationLogger.info("Accured Cash (unrealized): " + _context.getGrossPosition());
		_tradeAccountValidationLogger.info("Total Market Exposure: " + _context.getGrossPosition());
		_tradeAccountValidationLogger.info("Net Liquidation: " + _context.getNetLiquidation());
		_tradeAccountValidationLogger.info("Total Cash: " + _context.getTotalCash());
		_tradeAccountValidationLogger.info("Buying Power: " + _context.getBuyingPower());
		
		validateAccount();
		_tradeAccountValidationLogger.fatal("Account validation succeeded. Changing state.");
		
		_context.setState(new PreTradeDataFetchingState(_context));
	}

	private void validateAccount() {
		if ( ( (long)Math.floor(_context.getTotalCash()) ) < MarketMonarch.MINIMUM_ACCOUNT_BALANCE) {
			_tradeAccountValidationLogger.fatal("Account validation failed: Less than " + MarketMonarch.MINIMUM_ACCOUNT_BALANCE + " Euros in cash available. Exiting.");
			System.exit(0);
		} 
	}
}
