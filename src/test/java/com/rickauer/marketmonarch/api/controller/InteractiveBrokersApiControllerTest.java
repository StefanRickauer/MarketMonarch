package com.rickauer.marketmonarch.api.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.rickauer.marketmonarch.MarketMonarch;
import com.rickauer.marketmonarch.api.request.InteractiveBrokersApiRequestHandler;
import com.rickauer.marketmonarch.api.response.ScannerResponse;

public class InteractiveBrokersApiControllerTest {

	

	@Test
	void getOrderIdTest() {
		
		InteractiveBrokersApiController ibController = new InteractiveBrokersApiController();	
		
		
		int id = ibController.getOrderId();
		System.out.println("Current order id: " + id);
		assertFalse(-1 == id);
		
		for (int i = 1; i <= 11; i++) {
			assertEquals(i, ibController.getNextRequestId());
		}
	}
}
