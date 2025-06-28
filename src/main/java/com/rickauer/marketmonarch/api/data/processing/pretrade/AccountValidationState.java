package com.rickauer.marketmonarch.api.data.processing.pretrade;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ib.client.Bar;
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.rickauer.marketmonarch.api.data.AccountSummaryItem;
import com.rickauer.marketmonarch.constants.TradingConstants;
import com.rickauer.marketmonarch.utils.StockUtils;

public class AccountValidationState extends PreTradeState {

	private static Logger _tradeAccountValidationLogger = LogManager.getLogger(AccountValidationState.class.getName());
	
	public AccountValidationState(PreTradeContext context) {
		super(context);
	}

	@Override
	public void onEnter() {
		_tradeAccountValidationLogger.info("Started pre-trading phase.");
		_tradeAccountValidationLogger.info("Entered Account Validation State.");
		_tradeAccountValidationLogger.info("Requesting EUR to USD exchange rate to calculate total cash in USD.");

		int requestId = _context.getIbController().getNextRequestId();
		
		Contract fx = new Contract();
        fx.symbol("EUR");
        fx.secType("CASH");
        fx.currency("USD");
        fx.exchange("IDEALPRO");
		
		synchronized (_context) {
			try {
				_context.getIbController().getSocket().reqHistoricalData(
						requestId, 
						fx, 
						TradingConstants.END_DATE_TIME_UNTIL_NOW, 
						TradingConstants.LOOKBACK_PERIOD_TWO_HOURS_FIVE_MINUTES_IN_SECONDS, 
						TradingConstants.BARSIZE_SETTING_TWO_HOURS, 
						TradingConstants.SHOW_MIDPOINT, 
						TradingConstants.USE_REGULAR_TRADING_HOUR_DATA_INTEGER, 
						TradingConstants.FORMAT_DATE,
						TradingConstants.KEEP_UP_TO_DATE, 
						null
						);
				_context.wait(TradingConstants.TWO_MINUTES_TIMEOUT_MS);
			} catch (InterruptedException e) {
				throw new RuntimeException("Error fetching data.", e);
			}
		}
		
		if (_hasReceivedApiResponse == false) {
			_tradeAccountValidationLogger.warn("Timeout reached. Did not receive API response. Repeating state.");
			_context.setState(new AccountValidationState(_context));
		} 
		
		_tradeAccountValidationLogger.info("Requesting account details in to validate account.");
		
		_hasReceivedApiResponse = false;
		requestId = _context.getIbController().getNextRequestId();
		
		synchronized (_context.getAccountDetails()) {
			try {
				_context.getIbController().getSocket().reqAccountSummary(requestId, TradingConstants.ACCOUNT_SUMMARY_GROUP, TradingConstants.ACCOUNT_SUMMARY_TAGS); 			
				_context.getAccountDetails().wait(TradingConstants.ONE_MINUTE_TIMEOUT_MS);
			} catch (InterruptedException e) {
				throw new RuntimeException("Error fetching account summary.");
			}
		}
		
		if (_hasReceivedApiResponse == true) {
			_tradeAccountValidationLogger.info("Accured Cash (unrealized): " + _context.getGrossPosition());
			_tradeAccountValidationLogger.info("Total Market Exposure: " + _context.getGrossPosition());
			_tradeAccountValidationLogger.info("Net Liquidation: " + _context.getNetLiquidation());
			_tradeAccountValidationLogger.info("Buying Power: " + _context.getBuyingPower());
			_tradeAccountValidationLogger.info("Available Funds: " + _context.getAvailableFunds());
			_tradeAccountValidationLogger.info("Total Cash (EUR): " + _context.getTotalCash());
			_tradeAccountValidationLogger.info("Total Cash (USD): " + _context.getTotalCashInUsd());
			
			validateAccount();
			_tradeAccountValidationLogger.info("Account validation succeeded. Changing state.");
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
		double exchangeRate = StockUtils.roundValueDown(bar.close());
		_context.setExchangeRate(exchangeRate - TradingConstants.EXCHANGE_PUFFER);
	}

	@Override
	public void processHistoricalDataEnd(int reqId, String startDateStr, String endDateStr) {
		synchronized (_context) {
			_hasReceivedApiResponse = true;
			_context.notify();
		}
	}
}
