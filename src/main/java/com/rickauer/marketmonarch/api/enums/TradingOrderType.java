package com.rickauer.marketmonarch.api.enums;

public enum TradingOrderType {
	
	ALERT("ALERT", "Alert"),
	FUNARI("FUNARI", "Funari"),
	LIT("LII", "Limit If Touched"),
	LMT("LMT", "Limit"),							// BUY, SELL (take profit order)
	LMTTOB("LMTTOB", "Limit Or Better"),
	LOC("LOC", "Limit On Close"),
	LWOW("LWOW", "Limit With Or Withoug"),
	MIDPX("MIDPX", "Mid Price"),
	MIT("MIT", "Market If Touched"),
	MKT("MKT", "Market"),
	MKTPROT("MKTPROT", "MarketProtect"),
	MOC("MOC", "Market On Close"),
	MTL("MTL", "Market To Limit"),
	PASSVREL_PSVR("PASSVREL, PSVR", "Passive Relative"),
	PEGBENCH("PEGBENCH", "Peg Bench"),
	PEGMID("PEGMID", "Peg To Mid"),
	PEGMID2("PEGMID2","Peg To Mid2"),
	PEGMIDVOL("PEGMIDVOL", "Peg Mid Vol"),
	PEGMKT("PEGMKT", "Peg To Mkt"),
	PEGMKTVOL("PEGMKTVOL", "Peg Mkt Vol"),
	PEGPRMVOL("PEGPRMVOL", "Peg Prim Vol"),
	PEGSRFVOL("PEGSRFVOL", "Peg Surf Vol"),
	REL("REL", "Relative"),
	REL2MID("REL2MID", "Relative Up To Mid"),
	RELSTK("RELSTK", "Relative To Stock"),
	RFQ("RFQ", "Quote Request"),
	RPI("RPI", "Retail Price Improve"),
	SNAPMID("SNAPMID", "Snap To Mid Point"),
	SNAPMKT("SNAPMKT", "Snap To Market"),
	SNAPREL("SNAPREL", "Snap To Primary"),
	STP("STP", "Stop"),
	STPLMT("STPLMT", "Stop Limit"),					// SELL (stop loss order)
	STPPROT("STPPROT", "Stop Protect"),
	TRAIL("TRAIL", "Trailing Stop"),
	TRAILLIT("TRAILLIT", "Trailing Limit If Touched"),
	TRAILLMP("TRAILLMT", "Trailing Stop Limit"),
	TRAILMIT("TRAILMIT", "Trailing Market If Touched"),
	VOLAT("VOLAT", "Volat"),
	WOW("WOW", "With Or Without");
	
	String code;
	String meaning;
	
	private TradingOrderType(String code, String meaning) {
		this.code = code;
		this.meaning = meaning;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getMeaning() {
		return meaning;
	}
}
