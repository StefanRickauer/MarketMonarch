package com.rickauer.marketmonarch.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
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
		String insertion = "INSERT INTO trade (symbol, entry_price, exit_price, quantity, entry_time, exit_time, stop_loss, take_profit, order_efficiency_ratio) VALUES('%s', %f, %f, %d, '%s', '%s', %f, %f, %f)";
		
		DateTimeFormatter sqlFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		String formattedEntryTime = trade.getEntryTime().format(sqlFormatter);
		String formattedExitTime = trade.getExitTime().format(sqlFormatter);
		
		// Locale.US is necessary, because otherwise double-values cause Exception: Guess because they are being turned into 1,0 whereas the ',' is interpreted 
		// as a separator instead of a floating point!
		String query = String.format(
				Locale.US, 
				insertion, 
				trade.getSymbol(), 
				trade.getEntryPrice(), 
				trade.getExitPrice(), 
				trade.getQuantity(), 
				formattedEntryTime, 
				formattedExitTime, 
				trade.getStopLoss(), 
				trade.getTakeProfit(), 
				trade.getOrderEfficiencyRatio()
				); 
		
		return executeSqlUpdate(query);
	}
	
	public List<TradeDto> getAllTrades() {
		String query = "SELECT * FROM trade";
		
		List<TradeDto> trades = new ArrayList<>();
		TradeDto row = null;
		
		try (ResultSet allTrades = executeSqlQuery(query)) {
			
			while (allTrades.next()) {
				row = new TradeDto();

				row.setSymbol(allTrades.getString("symbol"));
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
