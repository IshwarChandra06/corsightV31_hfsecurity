package com.eikona.mata.util;

import static com.eikona.mata.constants.ApplicationConstants.BEARER;
import static com.eikona.mata.constants.ApplicationConstants.DELIMITER_SPACE;
import static com.eikona.mata.constants.NumberConstants.FIFTEEN;
import static com.eikona.mata.constants.NumberConstants.TEN;
import static com.eikona.mata.constants.NumberConstants.THOUSAND;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

import javax.net.ssl.SSLException;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.netty.http.client.HttpClient;

@Component
public class RequestExecutionUtil {
	
	@Autowired
	private CorsightAuth corsightLoginAuth;

	public String executeHttpPostRequest(HttpPost request) throws Exception {
		String responeData = null;
	
			int timeout = THOUSAND;
			RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout * THOUSAND)
					.setConnectionRequestTimeout(timeout * THOUSAND).setSocketTimeout(timeout * THOUSAND).build();
			CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

			HttpResponse response = httpclient.execute(request);
			responeData = EntityUtils.toString(response.getEntity());
		return responeData;
	}
	
	public String executeHttpPutRequest(HttpPut request) {
		String responeData = null;
		try {
			int timeout = TEN;
			RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout * THOUSAND)
					.setConnectionRequestTimeout(timeout * THOUSAND).setSocketTimeout(timeout * THOUSAND).build();
			CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

			HttpResponse response = httpclient.execute(request);
			responeData = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responeData;
	}

	public String executeHttpGetRequest(HttpGet request) {
		String responeData = null;
		try {
			int timeout = FIFTEEN;
			RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout * THOUSAND)
					.setConnectionRequestTimeout(timeout * THOUSAND).setSocketTimeout(timeout * THOUSAND).build();
			CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

			HttpResponse response = httpclient.execute(request);
			responeData = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responeData;

	}

	public String executeHttpDeleteRequest(HttpDelete request) {
		String responeData = null;
		try {
			int timeout = FIFTEEN;
			RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout * THOUSAND)
					.setConnectionRequestTimeout(timeout * THOUSAND).setSocketTimeout(timeout * THOUSAND).build();
			CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

			HttpResponse response = httpclient.execute(request);
			responeData = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responeData;
	}
	
	public String executeHttpsGetRequest( String baseUrl, String addRestUrl)
			throws KeyManagementException, NoSuchAlgorithmException, IOException {
		String authHeader = BEARER + corsightLoginAuth.getToken();
		SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
				.build();

		HttpClient httpclient = HttpClient.create().responseTimeout(Duration.ofSeconds(3))
				.secure(t -> t.sslContext(sslContext));

		WebClient webClient = WebClient.builder().baseUrl(baseUrl + addRestUrl)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeader(HttpHeaders.AUTHORIZATION, authHeader)
				.clientConnector(new ReactorClientHttpConnector(httpclient)).build();

		ResponseEntity<JSONObject> response = webClient.get().retrieve().toEntity(JSONObject.class).block();
		String responeData =response.getBody().toString();
		return responeData;
	}
	
	public String executeHttpsPostRequest(JSONObject requestObj,String baseUrl, String addRestUrl)
			throws KeyManagementException, NoSuchAlgorithmException, IOException {
		String authHeader = BEARER + corsightLoginAuth.getToken();
		SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
				.build();

		HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

		WebClient webClient = WebClient.builder().baseUrl(baseUrl + addRestUrl)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeader(HttpHeaders.AUTHORIZATION, authHeader)
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.build();

		ResponseEntity<JSONObject> response = webClient.post().bodyValue(requestObj).retrieve().toEntity(JSONObject.class).block();
//		webClient.delete();
		
		String responeData =response.getBody().toString();
		return responeData;
	}
	
	public String executeHttpsPutRequest(JSONObject requestObj, String httpsUrl, String putUrl)
			throws SSLException {
		
		String responeData = null;
		try {
			String authHeader = BEARER + corsightLoginAuth.getToken();
			SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
					.build();

			HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

			WebClient webClient = WebClient.builder().baseUrl(httpsUrl + putUrl)
					.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
					.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
					.defaultHeader(HttpHeaders.AUTHORIZATION, authHeader)
					.clientConnector(new ReactorClientHttpConnector(httpClient))
					.build();

			ResponseEntity<JSONObject> response = webClient.put().bodyValue(requestObj).retrieve()
					.toEntity(JSONObject.class).block();
			
			response.getBody().toString();
			
			webClient.delete();
			
		} catch (KeyManagementException | NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
		
		return responeData;
	}
	
	public String executeHttpsDeleteRequest(String httpsUrl, String deleteUrl)
			throws SSLException {
		
		String responeData = null;
		try {
			String authHeader = BEARER + DELIMITER_SPACE + corsightLoginAuth.getToken();
			SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
					.build();

			HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

			WebClient webClient = WebClient.builder().baseUrl(httpsUrl + deleteUrl)
					.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
					.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
					.defaultHeader(HttpHeaders.AUTHORIZATION, authHeader)
					.clientConnector(new ReactorClientHttpConnector(httpClient))
					.build();

			ResponseEntity<JSONObject> response = webClient.delete().retrieve()
					.toEntity(JSONObject.class).block();
			
			response.getBody().toString();
			
			webClient.delete();
			
		} catch (KeyManagementException | NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
		
		return responeData;
	}
	
}
