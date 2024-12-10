package com.rickauer.marketmonarch.api.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ib.client.EClientSocket;
import com.ib.client.EReader;
import com.rickauer.marketmonarch.api.request.InteractiveBrokersApiRequestHandler;
import com.rickauer.marketmonarch.utils.Verifyable;
import com.rickauer.marketmonarch.utils.Visitor;

public final class InteractiveBrokersApiController implements Verifyable {
	
	enum TransactionType {
		BUY("BUY"), 
		SELL("SELL");
		
		private String action;
		
		private TransactionType(String action) {
			this.action = action;
		}
		
		public String getAction() {
			return action;
		}
	}
	
	private static Logger _ibApiControllerLogger = LogManager.getLogger(InteractiveBrokersApiController.class.getName());
	
	private InteractiveBrokersApiRequestHandler _requestHandler;
	private String _host;
	private int _port;
	
	public InteractiveBrokersApiController() {
		// Real money trading 				-> port 4001
		// Paper money trading (simulation)	-> port 4002
		this("127.0.0.1", 4002);
	}
	
	public InteractiveBrokersApiController(String host, int port) {
		_requestHandler = new InteractiveBrokersApiRequestHandler();
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
					System.out.println("Exception: " + e.getMessage());
				}
			}
		}).start();
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
		return (getOrderId() != -1);
	}
}
