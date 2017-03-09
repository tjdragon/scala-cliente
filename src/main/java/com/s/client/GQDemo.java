package com.s.client;

import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;

public class GQDemo {
	public static void main(String[] args) throws Exception {
		// 1. Builds the RESTful client
		final CloseableHttpClient restfulClient = W3Utils.buildHTTPClient(true);
		
		// 2. Builds the GET request
		final HttpRequestBase gqRequest = W3Utils.buildHTTPReq(HTTPReqType.GET, "<GQ Fully Qualified URL>", true);
		
		// 3. Calculate the HMAC data
		final String customerId = "<Customer Id Here>";
		final String apiKey = "<Your API Key Here>";
		final long timestamp = System.currentTimeMillis();
		final String transactionId = SecurityUtils.createUID(customerId);
		
		final String hmacBase64 = SecurityUtils.calculateHMAC("<Quotes Service Here>", 
                                                              "<Sub Quotes Service Here>", 
                                                              customerId,
                                                              timestamp, 
                                                              transactionId, 
                                                              apiKey);
		// 4. Sets the HMAC fields in the HTTP Headers
		W3Utils.populateHTTPHeaders(gqRequest, timestamp, hmacBase64, transactionId);
		
		// 5. Execute the GET call
		final CloseableHttpResponse gqResponse = restfulClient.execute(gqRequest);
		final HttpEntity entity = gqResponse.getEntity();
		System.out.println("Response received " + IOUtils.toString(entity.getContent(), Charset.forName("UTF-8")));
	}
}
