package com.rickauer.marketmonarch.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.rickauer.marketmonarch.configuration.DatabaseConnector;
import com.rickauer.marketmonarch.db.data.TradeDto;
import com.rickauer.marketmonarch.utils.StockUtils;
import com.rickauer.marketmonarch.utils.Visitor;

public final class FinancialDataDao extends DatabaseDao {

	public FinancialDataDao(String dbUrl, String user, String password) {
		super(dbUrl, user, password);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	public List<TradeDto> getAllTrades() {
		DatabaseConnector.INSTANCE.initializeDatabaseConnector();
		FinancialDataDao db = new FinancialDataDao(DatabaseConnector.INSTANCE.getUrlFinancialData(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		
		String query = "SELECT * FROM trade";
		
		List<TradeDto> trades = new ArrayList<>();
		TradeDto row = null;
		
		try (ResultSet allTrades = db.executeSqlQuery(query)) {
			
			while (allTrades.next()) {
				row = new TradeDto();

				row.setId(allTrades.getInt("id"));
				row.setSymbol(allTrades.getString("symbol"));
				row.setBuyOrderId(allTrades.getInt("buy_order_id"));
				row.setSellOrderId(allTrades.getInt("sell_order_id"));
				row.setEntryPrice(allTrades.getDouble("entry_price"));
				row.setExitPrice(allTrades.getDouble("exit_price"));
				row.setStopLoss(allTrades.getDouble("stop_loss"));
				row.setQuantity(allTrades.getInt("quantity"));
				row.setEntryTime(StockUtils.timestampToLocalDateTime(allTrades.getTimestamp("entry_time")));
				row.setExitTime(StockUtils.timestampToLocalDateTime(allTrades.getTimestamp("exit_time")));
				row.setTakeProfit(allTrades.getDouble("take_profit"));
				row.setOrderEfficiencyRatio(allTrades.getDouble("order_efficiency_ratio"));
				
				trades.add(row);
			}
			
			return trades;
			
		} catch (Exception e) {
			throw new RuntimeException("Error executing query: '" + query + "'.", e);
		}
	}
}
