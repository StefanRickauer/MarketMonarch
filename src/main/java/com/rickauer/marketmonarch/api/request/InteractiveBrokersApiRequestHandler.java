package com.rickauer.marketmonarch.api.request;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

import com.ib.client.Bar;
import com.ib.client.CommissionReport;
import com.ib.client.Contract;
import com.ib.client.ContractDescription;
import com.ib.client.ContractDetails;
import com.ib.client.Decimal;
import com.ib.client.DeltaNeutralContract;
import com.ib.client.DepthMktDataDescription;
import com.ib.client.EClientSocket;
import com.ib.client.EJavaSignal;
import com.ib.client.EReaderSignal;
import com.ib.client.EWrapper;
import com.ib.client.EWrapperMsgGenerator;
import com.ib.client.Execution;
import com.ib.client.FamilyCode;
import com.ib.client.HistogramEntry;
import com.ib.client.HistoricalSession;
import com.ib.client.HistoricalTick;
import com.ib.client.HistoricalTickBidAsk;
import com.ib.client.HistoricalTickLast;
import com.ib.client.NewsProvider;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.PriceIncrement;
import com.ib.client.SoftDollarTier;
import com.ib.client.TickAttrib;
import com.ib.client.TickAttribBidAsk;
import com.ib.client.TickAttribLast;
import com.rickauer.marketmonarch.HealthChecker;
import com.rickauer.marketmonarch.MarketMonarch;
import com.rickauer.marketmonarch.api.data.AccountSummaryItem;
import com.rickauer.marketmonarch.api.data.CandleStick;
import com.rickauer.marketmonarch.api.data.processing.trade.TradeContext;
import com.rickauer.marketmonarch.api.response.ScannerResponse;
import com.rickauer.marketmonarch.utils.StockUtils;

public final class InteractiveBrokersApiRequestHandler implements EWrapper {

	private static Logger _ibRequestHandlerLogger = LogManager
			.getLogger(InteractiveBrokersApiRequestHandler.class.getName());
	
	private final static long TWO_MINUTES_IN_MILLIS = 120000L;
	
	private final Object lock = new Object();
	
	private EReaderSignal _readerSignal;
	private EClientSocket _clientSocket;
	private int _requestId;
	private int _orderId;					; // necessary? Because orderId will only be retrieved via API call

	; // initialize TradeMonitorState
	public InteractiveBrokersApiRequestHandler() {
		_readerSignal = new EJavaSignal();
		_clientSocket = new EClientSocket(this, _readerSignal);
		_requestId = 0;
		_orderId = -1;
	}
	
	public int getRequestId() {
		return _requestId;
	}
	
	public int getNextRequestId() {
		return ++_requestId;
	}

	public EReaderSignal getReaderSignal() {
		return _readerSignal;
	}

	public EClientSocket getClientSocket() {
		return _clientSocket;
	}

	@Override
	public void tickPrice(int tickerId, int field, double price, TickAttrib attrib) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tickSize(int tickerId, int field, Decimal size) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tickOptionComputation(int tickerId, int field, int tickAttrib, double impliedVol, double delta,
			double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tickGeneric(int tickerId, int tickType, double value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tickString(int tickerId, int tickType, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints,
			double impliedFuture, int holdDays, String futureLastTradeDate, double dividendImpact,
			double dividendsToLastTradeDate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void orderStatus(int orderId, String status, Decimal filled, Decimal remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld, double mktCapPrice) {	
		String message = EWrapperMsgGenerator.orderStatus( orderId, status, filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld, mktCapPrice);
		if (MarketMonarch._tradingContext.getState() != null) {
			MarketMonarch._tradingContext.getState().processOrderStatus(message, status, filled, remaining, avgFillPrice);
		}
	}										

	@Override
	public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {
		String message = EWrapperMsgGenerator.openOrder(orderId, contract, order, orderState);
		if (MarketMonarch._tradingContext.getState() != null) {
			MarketMonarch._tradingContext.getState().processOpenOrder(message, orderId, contract, order, orderState);
		}
	}

	@Override
	public void openOrderEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAccountValue(String key, String value, String currency, String accountName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updatePortfolio(Contract contract, Decimal position, double marketPrice, double marketValue,
			double averageCost, double unrealizedPNL, double realizedPNL, String accountName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAccountTime(String timeStamp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void accountDownloadEnd(String accountName) {
		// TODO Auto-generated method stub

	}

	; // If called with more than one Controller active, orderId will be reset. Make sure only one Controller is active.
	public int waitForNextOrderId() {

        synchronized (lock) {
            _clientSocket.reqIds(-1); 
            try {
				lock.wait(TWO_MINUTES_IN_MILLIS);
			} catch (InterruptedException e) {
				_ibRequestHandlerLogger.error("Timeout reached. Error requesting order ID.");
			} 
            return _orderId;
        }
    }
	
	// Will be invoked automatically upon successful API client connection, or after call to EClient.reqIds.
	@Override
	public void nextValidId(int orderId) {
		synchronized (lock) {
			_ibRequestHandlerLogger.info(EWrapperMsgGenerator.nextValidId(orderId));
            _orderId = orderId;
            lock.notifyAll();  
        }
	}

	@Override
	public void contractDetails(int reqId, ContractDetails contractDetails) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bondContractDetails(int reqId, ContractDetails contractDetails) {
		// TODO Auto-generated method stub

	}

	@Override
	public void contractDetailsEnd(int reqId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void execDetails(int reqId, Contract contract, Execution execution) {
		// TODO Auto-generated method stub

	}

	@Override
	public void execDetailsEnd(int reqId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateMktDepth(int tickerId, int position, int operation, int side, double price, Decimal size) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price,
			Decimal size, boolean isSmartDepth) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange) {
		// TODO Auto-generated method stub

	}

	@Override
	public void managedAccounts(String accountsList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveFA(int faDataType, String xml) {
		// TODO Auto-generated method stub

	}

	@Override
	public void historicalData(int reqId, Bar bar) {
		MarketMonarch._preTradeContext.getState().processHistoricalData(reqId, bar);																														
		MarketMonarch._tradingContext.getState().processHistoricalData(reqId, StockUtils.stringToZonedDateTime(bar.time()), bar.open(), bar.high(), bar.low(), bar.close(), Double.parseDouble(bar.volume().toString()));
	}

	@Override
	public void scannerParameters(String xml) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr) {
		MarketMonarch._preTradeContext.getState().processScannerData(reqId, rank, contractDetails, distance, benchmark, projection, legsStr);
	}

	@Override
	public void scannerDataEnd(int reqId) {
		MarketMonarch._preTradeContext.getState().processDataEnd(reqId);
	}

	@Override                                                         	                       
	public void realtimeBar(int reqId, long time, double open, double high, double low, double close, Decimal volume, Decimal wap, int count) {
		MarketMonarch._tradingContext.getState().processRealtimeBar(reqId, StockUtils.longToZonedDateTime(time, MarketMonarch._tradingContext.getStockAnalysisManager().getZoneIdByRequestId(reqId)), open, high, low, close, volume, wap, count);
		
		
		; // Code unten nach Test löschen!
		System.out.println("DEBUG: " + StockUtils.longToZonedDateTime(time, MarketMonarch._tradingContext.getStockAnalysisManager().getZoneIdByRequestId(reqId)));
	}

	@Override
	public void currentTime(long time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fundamentalData(int reqId, String data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deltaNeutralValidation(int reqId, DeltaNeutralContract deltaNeutralContract) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tickSnapshotEnd(int reqId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void marketDataType(int reqId, int marketDataType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void commissionReport(CommissionReport commissionReport) {
		// TODO Auto-generated method stub

	}

	@Override
	public void position(String account, Contract contract, Decimal pos, double avgCost) {
		// TODO Auto-generated method stub

	}

	@Override
	public void positionEnd() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void accountSummary(int reqId, String account, String tag, String value, String currency) {
		String logMessage = EWrapperMsgGenerator.accountSummary(reqId, account, tag, value, currency);
		MarketMonarch._preTradeContext.getState().processAccountSummary(logMessage, reqId, account, tag, value, currency);
	}

	@Override
	public void accountSummaryEnd(int reqId) {
		MarketMonarch._preTradeContext.getState().processAccountSummaryEnd(reqId);
	}

	@Override
	public void verifyMessageAPI(String apiData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void verifyCompleted(boolean isSuccessful, String errorText) {
		// TODO Auto-generated method stub

	}

	@Override
	public void verifyAndAuthMessageAPI(String apiData, String xyzChallenge) {
		// TODO Auto-generated method stub

	}

	@Override
	public void verifyAndAuthCompleted(boolean isSuccessful, String errorText) {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayGroupList(int reqId, String groups) {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayGroupUpdated(int reqId, String contractInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void error(Exception e) {
		_ibRequestHandlerLogger.error(e);
	}

	@Override
	public void error(String str) {
		_ibRequestHandlerLogger.error(str);
	}

	@Override
	public void error(int id, int errorCode, String errorMsg, String advancedOrderRejectJson) {
		if (advancedOrderRejectJson == null) {
			advancedOrderRejectJson = "";
		}
		_ibRequestHandlerLogger.error("RequestID: " + id + ", Error Code: " + errorCode + ", Error Message: " + errorMsg + "Advanced Order Reject Json:\n" + advancedOrderRejectJson);
	}

	@Override
	public void connectionClosed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectAck() {
		// TODO Auto-generated method stub

	}

	@Override
	public void positionMulti(int reqId, String account, String modelCode, Contract contract, Decimal pos,
			double avgCost) {
		// TODO Auto-generated method stub

	}

	@Override
	public void positionMultiEnd(int reqId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void accountUpdateMulti(int reqId, String account, String modelCode, String key, String value,
			String currency) {
		// TODO Auto-generated method stub

	}

	@Override
	public void accountUpdateMultiEnd(int reqId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void securityDefinitionOptionalParameter(int reqId, String exchange, int underlyingConId,
			String tradingClass, String multiplier, Set<String> expirations, Set<Double> strikes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void securityDefinitionOptionalParameterEnd(int reqId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void softDollarTiers(int reqId, SoftDollarTier[] tiers) {
		// TODO Auto-generated method stub

	}

	@Override
	public void familyCodes(FamilyCode[] familyCodes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void symbolSamples(int reqId, ContractDescription[] contractDescriptions) {
		// TODO Auto-generated method stub

	}

	
	@Override
	public void historicalDataEnd(int reqId, String startDateStr, String endDateStr) {
		MarketMonarch._preTradeContext.getState().processHistoricalDataEnd(reqId, startDateStr, endDateStr);
		MarketMonarch._tradingContext.getState().processHistoricalDataEnd(reqId, startDateStr, endDateStr);
	}

	@Override
	public void mktDepthExchanges(DepthMktDataDescription[] depthMktDataDescriptions) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tickNews(int tickerId, long timeStamp, String providerCode, String articleId, String headline,
			String extraData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void smartComponents(int reqId, Map<Integer, Entry<String, Character>> theMap) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tickReqParams(int tickerId, double minTick, String bboExchange, int snapshotPermissions) {
		// TODO Auto-generated method stub

	}

	@Override
	public void newsProviders(NewsProvider[] newsProviders) {
		// TODO Auto-generated method stub

	}

	@Override
	public void newsArticle(int requestId, int articleType, String articleText) {
		// TODO Auto-generated method stub

	}

	@Override
	public void historicalNews(int requestId, String time, String providerCode, String articleId, String headline) {
		// TODO Auto-generated method stub

	}

	@Override
	public void historicalNewsEnd(int requestId, boolean hasMore) {
		// TODO Auto-generated method stub

	}

	@Override
	public void headTimestamp(int reqId, String headTimestamp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void histogramData(int reqId, List<HistogramEntry> items) {
		// TODO Auto-generated method stub

	}

	@Override
	public void historicalDataUpdate(int reqId, Bar bar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rerouteMktDataReq(int reqId, int conId, String exchange) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rerouteMktDepthReq(int reqId, int conId, String exchange) {
		// TODO Auto-generated method stub

	}

	@Override
	public void marketRule(int marketRuleId, PriceIncrement[] priceIncrements) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pnl(int reqId, double dailyPnL, double unrealizedPnL, double realizedPnL) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pnlSingle(int reqId, Decimal pos, double dailyPnL, double unrealizedPnL, double realizedPnL,
			double value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void historicalTicks(int reqId, List<HistoricalTick> ticks, boolean done) {
		// TODO Auto-generated method stub

	}

	@Override
	public void historicalTicksBidAsk(int reqId, List<HistoricalTickBidAsk> ticks, boolean done) {
		// TODO Auto-generated method stub

	}

	@Override
	public void historicalTicksLast(int reqId, List<HistoricalTickLast> ticks, boolean done) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tickByTickAllLast(int reqId, int tickType, long time, double price, Decimal size,
			TickAttribLast tickAttribLast, String exchange, String specialConditions) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tickByTickBidAsk(int reqId, long time, double bidPrice, double askPrice, Decimal bidSize,
			Decimal askSize, TickAttribBidAsk tickAttribBidAsk) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tickByTickMidPoint(int reqId, long time, double midPoint) {
		// TODO Auto-generated method stub

	}

	@Override
	public void orderBound(long orderId, int apiClientId, int apiOrderId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void completedOrder(Contract contract, Order order, OrderState orderState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void completedOrdersEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void replaceFAEnd(int reqId, String text) {
		// TODO Auto-generated method stub

	}

	@Override
	public void wshMetaData(int reqId, String dataJson) {
		// TODO Auto-generated method stub

	}

	@Override
	public void wshEventData(int reqId, String dataJson) {
		// TODO Auto-generated method stub

	}

	@Override
	public void historicalSchedule(int reqId, String startDateTime, String endDateTime, String timeZone,
			List<HistoricalSession> sessions) {
		// TODO Auto-generated method stub

	}

	@Override
	public void userInfo(int reqId, String whiteBrandingId) {
		// TODO Auto-generated method stub

	}

}
