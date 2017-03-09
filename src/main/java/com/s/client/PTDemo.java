package com.s.client;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

public class PTDemo {
	public static void main(String[] args) throws Exception {
		// 1. Builds the RESTful client
		final CloseableHttpClient restfulClient = W3Utils.buildHTTPClient(true);
		
		// 2. Builds the POST request
		final HttpRequestBase ptRequest = W3Utils.buildHTTPReq(HTTPReqType.POST, "<GQ Fully Qualified URL>", true);
		
		// 3. Calculate the HMAC data
		final String customerId = "<Customer Id Here>";
		final String apiKey = "<Your API Key Here>";
		final long timestamp = System.currentTimeMillis();
		final String transactionId = SecurityUtils.createUID(customerId);
		
		final String hmacBase64 = SecurityUtils.calculateHMAC("<Transactions Service Here>", 
                                                              "<Sub Transactions Service Here>", 
                                                              customerId,
                                                              timestamp, 
                                                              transactionId, 
                                                              apiKey);
		// 4. Sets the HMAC fields in the HTTP Headers
		W3Utils.populateHTTPHeaders(ptRequest, timestamp, hmacBase64, transactionId);
		
		// 5. Sets the body content - let's assume the data provided is in a post-tx.json file
		final String contentBody = new String(Files.readAllBytes(Paths.get("./post-tx.json")));
		((HttpPost)ptRequest).setEntity(new StringEntity(contentBody));
		
		// 6. Execute the POST call
		final CloseableHttpResponse ptResponse = restfulClient.execute(ptRequest);
		final HttpEntity entity = ptResponse.getEntity();
		System.out.println("Response received " + IOUtils.toString(entity.getContent(), Charset.forName("UTF-8")));
	}
}
