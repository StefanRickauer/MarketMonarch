package com.rickauer.marketmonarch.api.data.processing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class StockAnalysisManagerTest {

	private static StockAnalysisManager sam;
	
	@BeforeAll
	public static void initializeData() {
		sam = new StockAnalysisManager();
	}
	
	@Test
	void updateSymbolLookupTableAddTest() {
		sam.updateSymbolLookupTable(0, "Zero");
		assertTrue(sam.getSymbolLookupTable().containsKey(0));
	}
	
	@Test
	void updateSymbolLookupTableReplaceTest() {
		sam.updateSymbolLookupTable(1, "Zero");
		assertFalse(sam.getSymbolLookupTable().containsKey(0));
		assertTrue(sam.getSymbolLookupTable().containsKey(1));
	}

}
