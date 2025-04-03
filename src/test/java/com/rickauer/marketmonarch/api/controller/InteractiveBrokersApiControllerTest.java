package com.rickauer.marketmonarch.api.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rickauer.marketmonarch.api.response.ScannerResponse;

public class InteractiveBrokersApiControllerTest {

	ScannerResponse _responses;
	InteractiveBrokersApiController _ibController;
	
	// getRequestId() will increment the requestId. Therefore, in order to ensure the requestId starts from 0 at each new test, a new object of 
	// the class InteractiveBrokersApiController must be instantiated. 
	
	@BeforeEach
	void initializeData() {
		_responses = new ScannerResponse(new Object());
		_ibController = new InteractiveBrokersApiController(_responses);		
	}
	
	@Test
	void getRequestIdValidTest() {
		for (int i = 0; i <= 10; i++) {
			assertEquals(i, _ibController.getNextRequestId());
		}
	}

	@Test
	void getRequestIdInvalidTest() {
		assertFalse(0 != _ibController.getNextRequestId());
	}

}
