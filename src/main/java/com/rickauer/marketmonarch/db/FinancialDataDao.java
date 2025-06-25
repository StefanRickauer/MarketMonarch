package com.rickauer.marketmonarch.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rickauer.marketmonarch.db.data.TradeDto;
import com.rickauer.marketmonarch.utils.Visitor;

public final class FinancialDataDao extends DatabaseDao {
	
	private static Logger _finDaoLogger = LogManager.getLogger(FinancialDataDao.class.getName());
	
	private String _url;
	private String _user;
	private String _password;
	
	public FinancialDataDao(String dbUrl, String user, String password) {
		super(dbUrl, user, password);
		_url = dbUrl;
		_user = user;
		_password = password;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	@SuppressWarnings("resource")
	public int insertRow(TradeDto trade) {
		String sql = "INSERT INTO trade (symbol, entry_price, exit_price, quantity, entry_time, exit_time, stop_loss, take_profit, order_efficiency_ratio) " + 
					 "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		Connection conn = getConnection(_url, _user, _password);
		
		try (PreparedStatement statement = conn.prepareStatement(sql)) {
			statement.setString(1, trade.getSymbol());
			statement.setDouble(2, trade.getEntryPrice());
			statement.setDouble(3, trade.getExitPrice());
			statement.setInt(4,  trade.getQuantity());
			statement.setTimestamp(5, Timestamp.valueOf(trade.getEntryTime()));
			statement.setTimestamp(6, Timestamp.valueOf(trade.getExitTime()));
			statement.setDouble(7, trade.getStopLoss());
			statement.setDouble(8, trade.getTakeProfit());
			statement.setDouble(9, trade.getOrderEfficiencyRatio());
			
			return statement.executeUpdate();
		} catch (SQLException e) {
			_finDaoLogger.error("Error inserting row.", e);
		}
		return 0;
	}
	
	public List<TradeDto> getAllTrades() {
		String query = "SELECT * FROM trade";
			
		try {
			List<TradeDto> trades = executeTradeQuery(query);
			return trades;
		} catch (Exception e) {
			throw new RuntimeException("Error executing query: '" + query + "'.", e);
		}
	}
}
