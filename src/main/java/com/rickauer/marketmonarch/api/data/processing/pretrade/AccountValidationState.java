package com.rickauer.marketmonarch.api.data.processing.pretrade;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ib.client.Bar;
import com.ib.client.ContractDetails;
import com.rickauer.marketmonarch.api.data.AccountSummaryItem;
import com.rickauer.marketmonarch.constants.TradingConstants;

public class AccountValidationState extends PreTradeState {

	private static Logger _tradeAccountValidationLogger = LogManager.getLogger(AccountValidationState.class.getName());
	
	public AccountValidationState(PreTradeContext context) {
		super(context);
	}

	@Override
	public void onEnter() {
		_tradeAccountValidationLogger.info("Started pre-trading phase.");
		_tradeAccountValidationLogger.info("Entered Account Validation State.");

		int requestId = _context.getIbController().getNextRequestId();
		
		synchronized (_context.getAccountDetails()) {
			try {
				_context.getIbController().getSocket().reqAccountSummary(requestId, TradingConstants.ACCOUNT_SUMMARY_GROUP, TradingConstants.ACCOUNT_SUMMARY_TAGS); 			
				_context.getAccountDetails().wait(TradingConstants.FIVE_MINUTES_TIMEOUT_MS);
			} catch (InterruptedException e) {
				throw new RuntimeException("Error fetching account summary.");
			}
		}
		
		if (_hasReceivedApiResponse == true) {
			_tradeAccountValidationLogger.info("Accured Cash (unrealized): " + _context.getGrossPosition());
			_tradeAccountValidationLogger.info("Total Market Exposure: " + _context.getGrossPosition());
			_tradeAccountValidationLogger.info("Net Liquidation: " + _context.getNetLiquidation());
			_tradeAccountValidationLogger.info("Total Cash: " + _context.getTotalCash());
			_tradeAccountValidationLogger.info("Buying Power: " + _context.getBuyingPower());
			_tradeAccountValidationLogger.info("Available Funds: " + _context.getAvailableFunds());
			
			validateAccount();
			_tradeAccountValidationLogger.fatal("Account validation succeeded. Changing state.");
			_context.setState(new DataFetchingState(_context));
		} else {			
			_context.getIbController().getSocket().cancelAccountSummary(requestId);
			_tradeAccountValidationLogger.warn("Timeout reached. Did not receive API response. Repeating state.");
			_context.setState(new AccountValidationState(_context));
		}
	}
	
	
	public void processAccountSummary(String logMessage, int reqId, String account, String tag, String value, String currency) {
		_tradeAccountValidationLogger.info(logMessage);
		_context.getAccountDetails().add(new AccountSummaryItem(reqId, account, tag, value, currency));
	}

	public void processAccountSummaryEnd(int reqId) {
		synchronized (_context.getAccountDetails()) {
			_context.getIbController().getSocket().cancelAccountSummary(reqId);
			_hasReceivedApiResponse = true;
			_context.getAccountDetails().notify();
		}
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
		// intentionally left blank
	}
	
	@Override
	public void processDataEnd(int reqId) {
		// intentionally left blank
	}

	@Override
	public void processHistoricalData(int reqId, Bar bar) {
		// intentionally left blank
	}

	@Override
	public void processHistoricalDataEnd(int reqId, String startDateStr, String endDateStr) {
		// intentionally left blank
	}
}
