package com.rickauer.marketmonarch.api.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rickauer.marketmonarch.api.response.ScannerResponse;

public class InteractiveBrokersApiControllerTest {

	ScannerResponse _responses;
	InteractiveBrokersApiController _ibController;
	
	@BeforeEach
	void initializeData() {
		_responses = new ScannerResponse(new Object());
		_ibController = new InteractiveBrokersApiController(_responses);		
	}
	
	@Test
	void getRequestIdValidTest() {
		for (int i = 1; i <= 11; i++) {
			assertEquals(i, _ibController.getNextRequestId());
		}
	}

	@Test
	void getRequestIdInvalidTest() {
		assertTrue(1 == _ibController.getNextRequestId());
	}

}
