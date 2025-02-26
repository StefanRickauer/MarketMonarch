package com.rickauer.marketmonarch.api.enums;

public enum TradingTime {

	NINE_THIRTY(570),
	TEN(600),
	TEN_THIRTY(630),
	ELEVEN(660),
	ELEVEN_THIRTY(690),
	TWELVE(720),
	TWELVE_THIRTY(750),
	THIRTEEN(780),
	THIRTEEN_THIRTY(810),
	FOURTEEN(840),
	FOURTEEN_THIRTY(870),
	FIFTEEN(900),
	FIFTEEN_THIRTY(930),
	SIXTEEN(960);
	
	private int _minuteOfDay;
	
	private TradingTime(int minuteOfDay) {
		_minuteOfDay = minuteOfDay;
	}
	
	public int toMinutes() {
		return _minuteOfDay;
	}
}
