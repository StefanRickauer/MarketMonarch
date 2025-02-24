package com.rickauer.marketmonarch.data;

import org.joda.time.DateTime;

import com.ib.client.Decimal;
import com.rickauer.marketmonarch.utils.StockUtils;

public class CandleStick {

	private DateTime _date;
	private double _open;
	private double _close;
	private double _low;
	private double _high;
	private Decimal _volume;
	
	public CandleStick(String date, double open, double close, double high, double low, Decimal volume) {
		_date = StockUtils.convertStringToDateTime(date);
		_open = open;
		_close = close;
		_low = low;
		_high = high;
		_volume = volume;
	}
	
	public DateTime getDate() {
		return _date;
	}
	
	public String getDateAsString() {
		return StockUtils.FORMATTER.print(_date);
	}
	
	public double getOpen() {
		return _open;
	}
	
	public double getClose() {
		return _close;
	}
	
	public double getLow() {
		return _low;
	}

	public double high() {
		return _high;
	}
	
	public Decimal getVolume() {
		return _volume;
	}
	
	public long getVolumeAsLong() {
		return _volume.longValue();
	}
}
