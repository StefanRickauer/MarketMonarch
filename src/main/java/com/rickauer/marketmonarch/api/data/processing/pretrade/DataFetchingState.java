package com.rickauer.marketmonarch.api.data.processing.pretrade;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.ib.client.Bar;
import com.ib.client.ContractDetails;
import com.rickauer.marketmonarch.MarketMonarch;
import com.rickauer.marketmonarch.api.controller.FmpRequestController;
import com.rickauer.marketmonarch.utils.FileSupplier;

public class DataFetchingState extends PreTradeState {

	private static Logger _dataFetchingLogger = LogManager.getLogger(DataFetchingState.class.getName());
	
	public DataFetchingState(PreTradeContext context) {
		super(context);
	}

	@Override
	public void onEnter() {
		_dataFetchingLogger.info("Entered Data Fetching State.");
		_dataFetchingLogger.info("Requesting all company free floats.");

		String companyFloats = "";

		DateTime today = DateTime.now();
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");

		String todaysBackupFileName = MarketMonarch.COMPANY_FLOATS_BACKUP_FOLDER + today.toString(formatter);
		File todaysBackupFile = new File(todaysBackupFileName);

		if (todaysBackupFile.exists()) {
			companyFloats = FileSupplier.readFile(todaysBackupFileName);
		} else {
			try {
				companyFloats = _context.getFmpController().requestAllShareFloat();
				if (companyFloats.length() < 1000000) {
					_dataFetchingLogger.error("Received incomplete response.");
					companyFloats = "";
					throw new RuntimeException("Incomplete response won't be saved.");
				}
				FileSupplier.writeFile(todaysBackupFileName, companyFloats);
				_dataFetchingLogger.info("Saved company floats to '" + todaysBackupFileName + "'.");
			} catch (Exception e) {
				_dataFetchingLogger.error("Could not fetch data.");
			}
		}

		if (companyFloats.equals("")) {
			_dataFetchingLogger.warn(
					"No backups today and received an empty or incomplete response from FMP. Attempting to restore latest save point.");

			File backupFolder = new File(MarketMonarch.COMPANY_FLOATS_BACKUP_FOLDER);
			File[] backupFiles = backupFolder.listFiles();

			if (backupFiles.length > 0) {
				String latestBackup = backupFiles[backupFiles.length - 1].getAbsolutePath();
				companyFloats = FileSupplier.readFile(latestBackup);
				_dataFetchingLogger.info("Loaded latest save file: '" + latestBackup + "'.");
			} 
		}
		
		_context.getAllCompanyFloats().putAll(
				FmpRequestController.convertResponseToMap(companyFloats)
				);

		validateCompanyFloats();
		_dataFetchingLogger.info("Received all company free floats. Changing state.");
		
		_context.setState(new MarketScanningState(_context));
	}

	@Override
	public void processAccountSummary(String logMessage, int reqId, String account, String tag, String value,
			String currency) {
		// intentionally left blank
	}

	@Override
	public void processAccountSummaryEnd(int reqId) {
		// intentionally left blank
	}
	
	private void validateCompanyFloats() {
		if (_context.getAllCompanyFloats().isEmpty()) {
			_dataFetchingLogger.fatal("Could not fetch company free floats. Exiting.");
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
