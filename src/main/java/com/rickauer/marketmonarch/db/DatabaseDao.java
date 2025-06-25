package com.rickauer.marketmonarch.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rickauer.marketmonarch.db.data.TradeDto;
import com.rickauer.marketmonarch.utils.StockUtils;
import com.rickauer.marketmonarch.utils.Verifyable;

public abstract class DatabaseDao implements Verifyable {

	private static Logger _dbLogger = LogManager.getLogger(DatabaseDao.class.getName()); 
	
	private Connection _connect;
	
	public DatabaseDao() {
		_connect = null;
	}
	
	public DatabaseDao(final String dbUrl, final String user, final String password) {
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			_connect = DriverManager.getConnection(dbUrl, user, password);
		} catch (ClassNotFoundException | SQLException e) {
			throw new RuntimeException("Error creating object.", e);
		}
	}
	
	public Connection getConnection(String url, String user, String password) {
		try {
			if (_connect == null || _connect.isClosed() || !_connect.isValid(2)) {
				_dbLogger.warn("Connection is invalid. Reconnecting...");
				_connect = DriverManager.getConnection(url, user, password);
			}
		} catch (Exception e) {
			_dbLogger.error("Could not establish connection.", e);
		}
		return _connect;
	}
	
	public boolean isReadyForOperation(int timeout) {
		
		if (_connect != null) {
			try {
				return _connect.isValid(timeout);
			} catch (SQLException e) {
				throw new RuntimeException("Could not connect to database.", e);
			}
		}
		
		return false;
	}

	public ResultSet executeSqlQuery(String query) {
		try (Statement statement = _connect.createStatement(); 
			 ResultSet resultSet = statement.executeQuery(query)){
			return resultSet;
		} catch (SQLException e) {
			throw new RuntimeException("Error executing query: '" + query + "'.", e);
		}
	}

	public List<TradeDto> executeTradeQuery(String query) {
		try (Statement statement = _connect.createStatement(); 
				ResultSet resultSet = statement.executeQuery(query)){
			
			List<TradeDto> trades = new ArrayList<>();
			
			while (resultSet.next()) {
				TradeDto row = new TradeDto();
				
				row.setSymbol(resultSet.getString("symbol"));
				row.setEntryPrice(resultSet.getDouble("entry_price"));
				row.setExitPrice(resultSet.getDouble("exit_price"));
				row.setStopLoss(resultSet.getDouble("stop_loss"));
				row.setQuantity(resultSet.getInt("quantity"));
				row.setEntryTime(StockUtils.timestampToLocalDateTime(resultSet.getTimestamp("entry_time")));
				row.setExitTime(StockUtils.timestampToLocalDateTime(resultSet.getTimestamp("exit_time")));
				row.setTakeProfit(resultSet.getDouble("take_profit"));
				row.setOrderEfficiencyRatio(resultSet.getDouble("order_efficiency_ratio"));
				
				trades.add(row);
			}
			
			return trades;
			
		} catch (SQLException e) {
			throw new RuntimeException("Error executing query: '" + query + "'.", e);
		}
	}
	
	public String executeSqlQueryAndGetFirstResultAsString(String query, String column) {
		try (Statement statement = _connect.createStatement();
			 ResultSet resultSet = statement.executeQuery(query)){
			
			while (resultSet.next()) {
				return resultSet.getString(column);
			} 
			return "";
		} catch (Exception e) {
			throw new RuntimeException("Error executing '" + query + "'.");
		}
	}
	
	public int executeSqlUpdate(String query) {
		try (Statement statement = _connect.createStatement()) {
			return statement.executeUpdate(query);
		} catch (Exception e) {
			System.out.println(e);
			throw new RuntimeException("Error executing '" + query + "'.");
		}
	}
	
	public void close() {
		try {
			if (_connect != null) {
				_dbLogger.info("Closing: " + _connect.getClass().getName());
			}
			
		} finally { 
			try { if (_connect != null) _connect.close(); } catch (Exception e) { /*Ignore*/ } 
		}
	}
}
