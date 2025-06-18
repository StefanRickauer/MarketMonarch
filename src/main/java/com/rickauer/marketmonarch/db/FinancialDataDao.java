package com.rickauer.marketmonarch.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.rickauer.marketmonarch.db.data.TradeDto;
import com.rickauer.marketmonarch.utils.Visitor;

public final class FinancialDataDao extends DatabaseDao {
	
	public FinancialDataDao(String dbUrl, String user, String password) {
		super(dbUrl, user, password);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	public int insertRow(TradeDto trade) {
		String sql = "INSERT INTO trade (symbol, entry_price, exit_price, quantity, entry_time, exit_time, stop_loss, take_profit, order_efficiency_ratio) " + 
					 "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		Connection conn = getConnection();
		
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
			//
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
