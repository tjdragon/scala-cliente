package com.s.client;

import org.junit.Test;

public class HMACTest {

	@Test
	public void testHMAC() throws Exception {
		final String serviceId = "quotes-service";
		final String subServiceId = "get-quotes";
		final String customerId = "customer-id";
		final long timestamp = System.currentTimeMillis();
		final String transactionId = SecurityUtils.createUID(customerId);
		final String apiKey = "my api key";
		
		final String hmac = SecurityUtils.calculateHMAC(serviceId, 
				                                        subServiceId, 
				                                        customerId, 
				                                        timestamp, 
				                                        transactionId, apiKey);
		System.out.println(hmac);
	}

}
