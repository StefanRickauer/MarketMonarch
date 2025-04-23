package com.rickauer.marketmonarch.data;

import java.time.ZonedDateTime;

import org.joda.time.DateTime;

import com.ib.client.Decimal;
import com.rickauer.marketmonarch.utils.StockUtils;

public class CandleStick {

	private DateTime _dateTime;
	private ZonedDateTime _zonedDateTime;
	private double _open;
	private double _close;
	private double _low;
	private double _high;
	private Decimal _volume;
	
	public CandleStick(String date, double open, double close, double high, double low, Decimal volume) {
		_dateTime = StockUtils.convertStringToDateTime(date);
		_zonedDateTime = StockUtils.toZonedDateTime(date);
		_open = open;
		_close = close;
		_low = low;
		_high = high;
		_volume = volume;
	}
	
	public DateTime getDate() {
		return _dateTime;
	}
	
	public ZonedDateTime getZonedDateTime() {
		return _zonedDateTime;
	}
	
	public String getDateAsString() {
		return StockUtils.FORMATTER.print(_dateTime);
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

	public double getHigh() {
		return _high;
	}
	
	public Decimal getVolume() {
		return _volume;
	}
	
	public Double getVolumeAsDouble() {
		return Double.parseDouble(_volume.toString());
	}
	
	public long getVolumeAsLong() {
		return _volume.longValue();
	}
}
