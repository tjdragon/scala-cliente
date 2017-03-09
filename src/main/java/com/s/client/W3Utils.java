package com.s.client;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;

/**
 * Defines utility functions needed to build the HTTP invoker
 * A typical usage would be that a customer use SSL and is behind a company firewall
 */
public class W3Utils {
	private static final String httpProxyHost = "<Your Proxy IP Host here>";
	private static final int httpProxyPort = -1; // Your Proxy IP Port here
	
	public static CloseableHttpClient buildHTTPClient(final boolean useSSL) throws Exception {
		final CloseableHttpClient closeableHttpClient = useSSL ? httpsClient() : httpClient();
		return closeableHttpClient;
	}
	
	/**
	 * Creates a plain HTTP Client
	 */
	public static CloseableHttpClient httpClient() throws Exception {
		final SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
		sslContextBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
		final SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
				sslContextBuilder.build(),
				new HostnameVerifier() {
					public boolean verify(final String hostname, final SSLSession session) {
						return true; // accept all
					}
				});
		return HttpClients.custom()
				          .setSSLSocketFactory(sslConnectionSocketFactory)
				          .build();		
	}
	
	/**
	 * Creates a plain HTTPS Client
	 * 
	 * How to import a .pfx into a JKS
	 * 
	 * jdk/bin/keytool -importkeystore 
	 *                 -srckeystore mypfxfile.pfx 
	 *                 -srcstoretype pkcs12 
	 *                 -destkeystore clientcert.jks 
	 *                 -deststoretype JKS
	 */
	public static CloseableHttpClient httpsClient() throws Exception {
		final String keystorePassword = "<Your KeyStore Password Here>";
		final String keyPassword = "<Your Key Password Here>";
		final String keystorePath = "<Path to the KeyStore.jks>";
		
		final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		final FileInputStream fileInputStream = new FileInputStream(new File(keystorePath));
		keyStore.load(fileInputStream, keystorePassword.toCharArray());
		fileInputStream.close();
		
		final TrustStrategy trustStrategy = new TrustStrategy() {	
			public boolean isTrusted(final X509Certificate[] x509Certs, final String s) throws CertificateException {
				return true;
			}
		};
		
		final SSLContext sslContext = SSLContexts.custom()
				                                 .loadKeyMaterial(keyStore, keyPassword.toCharArray())
				                                 .loadTrustMaterial(keyStore, trustStrategy)
				                                 .build();
		final SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext);
		return HttpClients.custom()
				          .setSSLSocketFactory(sslConnectionSocketFactory)
				          .build();		
	}
	
	/**
	 * Builds a GET or POST Request
	 */
	public static HttpRequestBase buildHTTPReq(final HTTPReqType reqType, final String url, final boolean useProxy) throws Exception {
		final HttpRequestBase httpReq = reqType == HTTPReqType.GET ? new HttpGet(url) : new HttpPost(url);
		
		if (useProxy) {
			final HttpHost proxyHost = new HttpHost(httpProxyHost, httpProxyPort);
			final RequestConfig requestConfig = RequestConfig.custom()
					                                         .setProxy(proxyHost)
					                                         .build();
			httpReq.setConfig(requestConfig);
		}
		
		return httpReq;
	}
	
	public static void populateHTTPHeaders(final HttpRequestBase request, final long timestamp, final String hmac, final String txId) {
		request.addHeader("Content-Type", "application/json");
		
		request.addHeader("<TIMESTAMP HEADER NAME HERE>", String.valueOf(timestamp));
		request.addHeader("<HMAC HEADER NAME HERE>", hmac);
		request.addHeader("<TX ID HEADER NAME HERE>", txId);
	}
}
