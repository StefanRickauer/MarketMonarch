package com.rickauer.marketmonarch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.ta4j.core.BarSeries;

import com.ib.client.Contract;
import com.ib.client.ScannerSubscription;
import com.ib.client.TagValue;
import com.ib.client.Types.TimeInForce;
import com.ib.controller.AccountSummaryTag;
import com.rickauer.marketmonarch.api.connect.AlphaVantageConnector;
import com.rickauer.marketmonarch.api.connect.FmpConnector;
import com.rickauer.marketmonarch.api.connect.MailtrapServiceConnector;
import com.rickauer.marketmonarch.api.connect.StockNewsConnector;
import com.rickauer.marketmonarch.api.controller.FmpRequestController;
import com.rickauer.marketmonarch.api.controller.InteractiveBrokersApiController;
import com.rickauer.marketmonarch.api.data.AccountSummaryItem;
import com.rickauer.marketmonarch.api.data.CandleSeries;
import com.rickauer.marketmonarch.api.data.CandleStick;
import com.rickauer.marketmonarch.api.data.StockMetrics;
import com.rickauer.marketmonarch.api.data.processing.StrategyExecutor;
import com.rickauer.marketmonarch.api.data.processing.pretrade.AccountValidationState;
import com.rickauer.marketmonarch.api.data.processing.pretrade.PreTradeContext;
import com.rickauer.marketmonarch.api.data.processing.pretrade.RequestHistoricalDataState;
import com.rickauer.marketmonarch.api.data.processing.trade.EntryScanningState;
import com.rickauer.marketmonarch.api.data.processing.trade.TradeInactiveState;
import com.rickauer.marketmonarch.api.data.processing.trade.TradeContext;
import com.rickauer.marketmonarch.api.enums.FmpServiceRequest;
import com.rickauer.marketmonarch.api.response.ScannerResponse;
import com.rickauer.marketmonarch.configuration.DatabaseConnector;
import com.rickauer.marketmonarch.constants.TradingConstants;
import com.rickauer.marketmonarch.db.ApiKeyDao;
import com.rickauer.marketmonarch.db.FinancialDataDao;
import com.rickauer.marketmonarch.reporting.LineChartCreator;
import com.rickauer.marketmonarch.utils.FileSupplier;
import com.rickauer.marketmonarch.utils.StockUtils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.*;

public final class MarketMonarch {

	public static final String PROGRAM = "MarketMonarch";
	private static final String VERSION = "0.93";
	public static final String PROGRAM_AND_VERSION = PROGRAM + " " + VERSION;

	private static Logger _marketMonarchLogger = LogManager.getLogger(MarketMonarch.class.getName());

	public static final String COMPANY_FLOATS_BACKUP_FOLDER= FileSupplier.getBackupFolder() + "\\Company Floats\\";
	public static final String SESSION_STORAGE_FOLDER = FileSupplier.getSessionFolder();
	public static String CURRENT_SESSION_STORAGE_FOLDER = "";
	
	private static HealthChecker _healthChecker = new HealthChecker();
	public static ApiKeyDao _apiAccess;
	public static FinancialDataDao _finAccess;
	private static FmpConnector _fmpConnector;
	private static FmpRequestController _financialModellingPrepController;
	private static AlphaVantageConnector _alphaVantage;
	public static MailtrapServiceConnector _mailtrapService;
	private static InteractiveBrokersApiController _interactiveBrokersController;

	public static PreTradeContext _preTradeContext;
	public static TradeContext _tradingContext;

	static {
		_interactiveBrokersController = new InteractiveBrokersApiController();

		DatabaseConnector.INSTANCE.initializeDatabaseConnector();

		_apiAccess = new ApiKeyDao(DatabaseConnector.INSTANCE.getUrlAPIKey(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		_finAccess = new FinancialDataDao(DatabaseConnector.INSTANCE.getUrlAPIKey(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		_mailtrapService = new MailtrapServiceConnector("mailtrap", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'mailtrap'", "token"));
		_fmpConnector = new FmpConnector("fmp", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'FMP'", "token"));
		_financialModellingPrepController = new FmpRequestController(_fmpConnector.getToken(), FmpServiceRequest.ALL_SHARES_FLOAT);
		_alphaVantage = new AlphaVantageConnector("alphavantageapi", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'alphavantage'", "token"));
		DatabaseConnector.INSTANCE.flushDatabaseConnectionEssentials();

		_preTradeContext = new PreTradeContext(_interactiveBrokersController, _financialModellingPrepController);
		_tradingContext = new TradeContext(_interactiveBrokersController);
	}

	
	public static void main(String[] args) {
		try {
			Thread.currentThread().setName(PROGRAM + " -> Main Thread");
			_marketMonarchLogger.info("Starting " + PROGRAM + " (version " + VERSION + ").");
			
			ensureOperationalReadiness();
			setUpWorkingEnvironment();
			
			while (_tradingContext.getRestartSession()) {

				_marketMonarchLogger.info("Checking if program is running within valid time window.");

				if (!StockUtils.isWithinTradingWindow(ZonedDateTime.now(ZoneId.systemDefault()))) {
					_marketMonarchLogger.warn("Pausing execution: Current time is outside the defined operational window (10:15–13:30 NY time).");
					long sleepDuration = StockUtils.millisUntilTradingWindowNYSE(10, 15);
					String formattedSleepDuration = StockUtils.formatMillis(sleepDuration);
					_marketMonarchLogger.warn("Waiting for scheduled time - resuming in " + formattedSleepDuration);
					Thread.sleep(sleepDuration);
					_marketMonarchLogger.warn("Scheduled time reached. Proceeding with execution.");
				}				
				
				_tradingContext.setState(new TradeInactiveState(_tradingContext));
				_preTradeContext.setState(new AccountValidationState(_preTradeContext));				
				
				
				// DEBUG ONLY: Remove before going live =======================================
				for (StockMetrics metric : _preTradeContext.getHistoricalData().values()) {
					_marketMonarchLogger.debug("Symbol: " + metric.getSymbol() + ", Relative volume: " + metric.getRelativeVolume() + ", Profit loss: " + metric.getProfitLossChange() 
					+ ", Company Share Float: " + _preTradeContext.getAllCompanyFloats().get(metric.getSymbol()));
				}
				// DEBUG ONLY END =============================================================
				
				
				_tradingContext.setState(new EntryScanningState(_tradingContext));
			}
			
			_marketMonarchLogger.info("Done trading. Add code to save data to database. Create report and send via mail");
			_marketMonarchLogger.info("Goodbye!");
			System.exit(0);
			
		} catch (Throwable t) {
			// Workaround because usage of t will throw exception.
			String stackTrace = ExceptionUtils.getStackTrace(t);
			_marketMonarchLogger.error(stackTrace);
		} finally {
			_apiAccess.close();
			_finAccess.close();
		}
	}

	
	private static void ensureOperationalReadiness() {
		_marketMonarchLogger.info("Preparing for operational readiness check...");

		_healthChecker.add(DatabaseConnector.INSTANCE);
		_healthChecker.add(_apiAccess);
		_healthChecker.add(_finAccess);
		_healthChecker.add(_mailtrapService);
		_healthChecker.add(_fmpConnector);
		_healthChecker.add(_alphaVantage);
		_healthChecker.add(_interactiveBrokersController);

		_marketMonarchLogger.info("Checking operational readiness...");
		_healthChecker.runHealthCheck();
		_marketMonarchLogger.info("Checked operational readiness.");

		_marketMonarchLogger.info("Evaluating check results...");
		_healthChecker.analyseCheckResults();
		_marketMonarchLogger.info("Evaluated check results.");
	}
	
	private static void setUpWorkingEnvironment() {
		_marketMonarchLogger.info("Setting up working environment...");
		
		File companyFloatBackupFolder = new File(COMPANY_FLOATS_BACKUP_FOLDER);
		if (!companyFloatBackupFolder.exists()) {
			companyFloatBackupFolder.mkdir();
			_marketMonarchLogger.info("Created company floats backup folder.");
		}
		
		File sessionStorageFolder = new File(SESSION_STORAGE_FOLDER);
		if (!sessionStorageFolder.exists()) {
			sessionStorageFolder.mkdir();
			_marketMonarchLogger.info("Created session storage folder.");
		}
		
		java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String timeStamp = ZonedDateTime.now().format(formatter);
		CURRENT_SESSION_STORAGE_FOLDER = SESSION_STORAGE_FOLDER + "\\" + timeStamp;
		File sessionStorageSubFolder = new File(CURRENT_SESSION_STORAGE_FOLDER);
		if (!sessionStorageSubFolder.exists()) {
			sessionStorageSubFolder.mkdir();
			_marketMonarchLogger.info("Created session storage folder.");
		}
		_marketMonarchLogger.info("Set up environment.");
	}
}
