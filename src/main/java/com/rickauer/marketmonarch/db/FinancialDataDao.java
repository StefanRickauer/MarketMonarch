package com.rickauer.marketmonarch.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
	
	public int insertRow(TradeDto trade) {
		String insertion = "INSERT INTO trade VALUES(%d, '%s', %d, %d, %f, %f, %d, '%s', '%s', %f, %f, %f)";
		
		// Locale.US is necessary, because otherwise double-values cause Exception: Guess because they are being turned into 1,0 whereas the ',' is interpreted 
		// as a separator instead of a floating point!
		String query = String.format(Locale.US, insertion, trade.getId(), trade.getSymbol(), trade.getBuyOrderId(),
				trade.getSellOrderId(), trade.getEntryPrice(), trade.getExitPrice(), trade.getQuantity(), trade.getEntryTime(),
				trade.getExitTime(), trade.getStopLoss(), trade.getTakeProfit(), trade.getOrderEfficiencyRatio()); 
		
		return executeSqlUpdate(query);
	}
	
	public List<TradeDto> getAllTrades() {
		String query = "SELECT * FROM trade";
		
		List<TradeDto> trades = new ArrayList<>();
		TradeDto row = null;
		
		try (ResultSet allTrades = executeSqlQuery(query)) {
			
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
