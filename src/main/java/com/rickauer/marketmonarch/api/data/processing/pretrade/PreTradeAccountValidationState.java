package com.rickauer.marketmonarch.api.data.processing.pretrade;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ib.client.ContractDetails;
import com.rickauer.marketmonarch.api.data.AccountSummaryItem;
import com.rickauer.marketmonarch.constants.TradingConstants;

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
		if ( ( (long)Math.floor(_context.getTotalCash()) ) < TradingConstants.MINIMUM_ACCOUNT_BALANCE) {
			_tradeAccountValidationLogger.fatal("Account validation failed: Less than " + TradingConstants.MINIMUM_ACCOUNT_BALANCE + " Euros in cash available. Exiting.");
			System.exit(0);
		} 
	}

	@Override
	public void processScannerData(int reqId, int rank, ContractDetails contractDetails, String distance,
			String benchmark, String projection, String legsStr) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void processDataEnd(int reqId) {
		// TODO Auto-generated method stub
	}

}
