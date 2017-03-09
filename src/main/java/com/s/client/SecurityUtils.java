package com.s.client;

import java.util.Base64;
import java.util.StringJoiner;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class shows the HMAC calculation
 */
public class SecurityUtils {
	private static final String hmacAlgo = "HmacSHA256";
	
	public static String calculateHMAC(final String serviceId,
			                           final String subServiceId,
			                           final String customerId,
			                           final long timestamp,
			                           final String transactionId,
			                           final String apiKey) throws Exception {
		
		final String key2Hash = new StringJoiner(":").add(serviceId)
                                                     .add(subServiceId)
                                                     .add(customerId)
                                                     .add(String.valueOf(timestamp))
                                                     .add(transactionId)
                                                     .toString();
		
		final SecretKeySpec secretKeySpec = new SecretKeySpec(apiKey.getBytes(), hmacAlgo);
		final Mac hmac = Mac.getInstance(hmacAlgo);
		hmac.init(secretKeySpec);
		
		final byte[] hmacAsBytes = hmac.doFinal(key2Hash.getBytes("UTF-8"));
		final String hmacAsB64 = Base64.getEncoder().encodeToString(hmacAsBytes);
		
		return hmacAsB64;
	}
	
	public static String createUID(final String prefix) {
		final String suffix = Long.toString(Math.abs(UUID.randomUUID().getLeastSignificantBits()), Character.MAX_RADIX);
		return (prefix+"-"+suffix).toUpperCase();
	}
}
