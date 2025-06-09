package com.rickauer.marketmonarch.constants;

public class TradingConstants {

	public static final long TWO_HOURS_TIMEOUT_MS = 7_200_000L;
	public static final long FIFTEEN_MINUTES_TIMEOUT_MS = 900_000L;
	public static final long FIVE_MINUTES_TIMEOUT_MS = 300_000L;
	
	public static final int MIN_NUMBER_OF_SHARES = 5_000_000;
	public static final int MAX_NUMBER_OF_SHARES = 20_000_000;
	
	public static final int MINIMUM_ACCOUNT_BALANCE = 500;
	public static final int MINIMUM_PROFIT_LOSS_IN_PERCENT = 10;
	
	public static final int MAXIMUM_NUMBER_OF_SCAN_RESULTS = 4;
	
	public static final double BUY_LIMIT_PUFFER = 1.02;
	public static final double PUFFER_FACTOR = 0.98;							// number * PUFFER = 2%
	public static final double TAKE_PROFIT_FACTOR = 1.05;						// number * TAKE_PROFIT = 5%
	public static final double STOP_LIMIT_STOP_PRICE_FACTOR = 0.9;				// number * STOP_PRICE = 90%  -> Triggered at 10% below
	public static final double STOP_LIMIT_LIMIT_PRICE_FACTOR = 0.89;			// number * LIMIT_PRICE = 89% -> Loss 11%
	
	public static final String ACCOUNT_SUMMARY_GROUP = "All";
	public static final String ACCOUNT_SUMMARY_TAGS = "NetLiquidation,TotalCashValue,AccruedCash,BuyingPower,GrossPositionValue,AvailableFunds";
	
	public static final String SCANNER_INSTRUMENT = "STK";
	public static final String SCANNER_LOCATION_CODE = "STK.US.MAJOR";
	public static final String SCANNER_SCAN_CODE = "TOP_PERC_GAIN";
	public static final String FILTER_TAG_PRICE_ABOVE = "priceAbove";
	public static final String FILTER_TAG_PRICE_BELOW = "priceBelow";
	public static final String FILTER_VALUE_PRICE_ABOVE = "2";
	public static final String FILTER_VALUE_PRICE_BELOW = "20";
	
	public static final String LOOKBACK_PERIOD_TWO_DAYS = "2 D";
	public static final String LOOKBACK_PERIOD_FOUR_HOURS_TEN_MINUTES_IN_SECONDS = "15000 S";
	public static final String LOOKBACK_PERIOD_TWO_HOURS_FIVE_MINUTES_IN_SECONDS = "7500 S";

	public static final String BARSIZE_SETTING_FIVE_MINUTES = "5 mins";
	public static final String BARSIZE_SETTING_FIVE_SECONDS = "5 secs";
	
	public static final String END_DATE_TIME_UNTIL_NOW = "";
	public static final String SHOW_TRADES = "TRADES";							// Prices based on real trades
	public static final String SHOW_MIDPOINT = "MIDPOINT";						// Prices based on average of ask and bid price
	public static final String SHOW_BID = "BID";								// Prices based on bid price. Bid: Max. price a buyer is willing to pay; Will return -1 volume (historical data) -> use "TRADES"
	public static final String SHOW_ASK = "ASK";								// Prices based on ask price. Ask: Min. price a seller is willing to sell his stocks for
	public static final int USE_REGULAR_TRADING_HOUR_DATA_INTEGER = 1;
	public static final boolean USE_REGULAR_TRADING_HOUR_DATA_BOOLEAN = true;
	public static final int FORMAT_DATE = 1;		// Docs won't tell much either 
	public static final boolean KEEP_UP_TO_DATE = false;
}
