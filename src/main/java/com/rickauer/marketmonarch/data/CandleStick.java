package com.rickauer.marketmonarch.data;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.ib.client.Decimal;
import com.rickauer.marketmonarch.utils.StockUtils;

public class CandleStick {

	private ZonedDateTime _zonedDateTime;
	private double _open;
	private double _close;
	private double _low;
	private double _high;
	private Decimal _volume;
	
	public CandleStick(String date, double open, double close, double high, double low, Decimal volume) {
		_zonedDateTime = StockUtils.toZonedDateTime(date);
		_open = open;
		_close = close;
		_low = low;
		_high = high;
		_volume = volume;
	}
	
	public DateTime getJodaDateTime() {

		Instant instant = _zonedDateTime.toInstant();
		DateTime jodaDateTime = new DateTime(instant.toEpochMilli(), DateTimeZone.forID(_zonedDateTime.getZone().getId()));
		
		return jodaDateTime;
	}
	
	/**
	 * Returns an object of Timestamp in order to be saved in a database.
	 * @return	The time of the Candlestick
	 */
	public Timestamp getTimeStamp() {
		return Timestamp.valueOf(getLocalDateTime());
	}
	
	public LocalDateTime getLocalDateTime() {
		return _zonedDateTime.toLocalDateTime();
	}
	
	public ZonedDateTime getZonedDateTime() {
		return _zonedDateTime;
	}
	
	public String getDateAsString() {
		return StockUtils.FORMATTER.print(getJodaDateTime());
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
