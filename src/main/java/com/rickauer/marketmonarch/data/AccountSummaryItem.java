package com.rickauer.marketmonarch.data;

public class AccountSummaryItem {

	private int _requestId;
	private String _account;
	private String _tag;
	private String _value;
	private String _currency;
	
	public AccountSummaryItem(final int requestId, final String account, final String tag, final String value, final String currency) {
		_requestId = requestId;
		_account = account;
		_tag = tag;
		_value = value;
		_currency = currency;
	}
	
	public int getRequestId() {
		return _requestId;
	}
	
	public String getAccount() {
		return _account;
	}
	
	public String getTag() {
		return _tag;
	}
	
	public double getValueAsDouble() {
		return Double.parseDouble(_value);
	}
	
	public String getCurrency() {
		return _currency;
	}
}
