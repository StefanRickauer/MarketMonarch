package com.rickauer.marketmonarch.api.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ib.client.EClientSocket;
import com.ib.client.EReader;
import com.rickauer.marketmonarch.MarketMonarch;
import com.rickauer.marketmonarch.api.request.InteractiveBrokersApiRequestHandler;
import com.rickauer.marketmonarch.api.response.ScannerResponse;
import com.rickauer.marketmonarch.utils.Verifyable;
import com.rickauer.marketmonarch.utils.Visitor;

public final class InteractiveBrokersApiController implements Verifyable {
	
	private static Logger _ibApiControllerLogger = LogManager.getLogger(InteractiveBrokersApiController.class.getName());
	
	private InteractiveBrokersApiRequestHandler _requestHandler;
	private String _host;
	private int _port;
	
	public InteractiveBrokersApiController(ScannerResponse result) {
		// Real money trading 				-> port 4001
		// Paper money trading (simulation)	-> port 4002
		this("127.0.0.1", 4002, result);
	}

	public InteractiveBrokersApiController(int port, ScannerResponse result) {
		// Real money trading 				-> port 4001
		// Paper money trading (simulation)	-> port 4002
		this("127.0.0.1", port, result);
	}

	public InteractiveBrokersApiController(boolean isSimulatedTrading, ScannerResponse result) {
		// Real money trading 				-> port 4001
		// Paper money trading (simulation)	-> port 4002
		this("127.0.0.1", isSimulatedTrading ? 4002 : 4001, result);
	}
	
	private InteractiveBrokersApiController(String host, int port, ScannerResponse result) {
		_requestHandler = new InteractiveBrokersApiRequestHandler(result);
		_host = host;
		_port = port;

		_requestHandler.getClientSocket().eConnect(_host, _port, 2);
		
		_ibApiControllerLogger.info("Connection established. Host=" + _host + ", port=" + _port + ".");

		final EReader reader = new EReader(_requestHandler.getClientSocket(), _requestHandler.getReaderSignal());
		reader.start();
		new Thread(() -> {
			while (_requestHandler.getClientSocket().isConnected()) {
				_requestHandler.getReaderSignal().waitForSignal();
				try {
					reader.processMsgs();
				} catch (Exception e) {
					_ibApiControllerLogger.error(e.getMessage());
				}
			}
		}).start();
	}

	public int getRequestId() {
		return _requestHandler.getNextRequestId();
	}
	
	public int getOrderId() {
		return _requestHandler.getCurrentOrderId();
	}
	
	public EClientSocket getSocket() {
		return _requestHandler.getClientSocket();
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	public boolean isOperational() {
		getSocket().reqIds(-1);
		return (getOrderId() != -1);
	}
}
