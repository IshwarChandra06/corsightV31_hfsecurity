package com.eikona.mata.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.CorsightDeviceConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CorsightAuth {
	@Value("${corsight.host.url}")
    private String corsightHost;
	
	@Value("${corsight.user.port}")
    private String userPort;
	
	@Value("${corsight.host.username}")
    private String userName;
	
	@Value("${corsight.host.password}")
    private String password;
	

	@SuppressWarnings(ApplicationConstants.UNCHECKED)
	public String getToken() throws IOException, NoSuchAlgorithmException, KeyManagementException {
		
		String httpsURL = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH+corsightHost+ApplicationConstants.DELIMITER_COLON+userPort+CorsightDeviceConstants.USER_API_LOGIN;//?username=mataadmin&password=eikona2020 

		String query = ApplicationConstants.USERNAME_EQUAL_TO+URLEncoder.encode(userName,ApplicationConstants.UTF8); 
		query += ApplicationConstants.DELIMITER_AMPERSAND;
		query += ApplicationConstants.PASSWORD_EQUAL_TO+URLEncoder.encode(password,ApplicationConstants.UTF8) ;
		
		
		TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
			@Override
			public X509Certificate[] getAcceptedIssuers() {return null;}
			
			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}
			
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}
			}
		};

    // Install the all-trusting trust manager
    SSLContext sc = SSLContext.getInstance(ApplicationConstants.SSL);
    sc.init(null, trustAllCerts, new java.security.SecureRandom());
    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

    // Create all-trusting host name verifier
    HostnameVerifier allHostsValid = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    // Install the all-trusting host verifier
    	HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		
		URL myurl = new URL(httpsURL);
		HttpsURLConnection con = (HttpsURLConnection)myurl.openConnection();
		//con.setHostnameVerifier(getHostnameVerifier());
		con.setRequestMethod(ApplicationConstants.POST);
		
		con.setRequestProperty(ApplicationConstants.HEADER_CONTENT_LENGTH, String.valueOf(query.length())); 
		con.setRequestProperty(ApplicationConstants.HEADER_CONTENT_TYPE,ApplicationConstants.APPLICATION_URL_ENCODED); 
		con.setDoOutput(true); 
		
		try {
			DataOutputStream output = new DataOutputStream(con.getOutputStream());  
			
			output.writeBytes(query);
			
			output.close();
			
			InputStream inputStreamObject =  con.getInputStream(); 
			
			ObjectMapper mapper = new ObjectMapper();
			Map<String,Object> map = mapper.readValue(inputStreamObject,Map.class);
			
			return map.get(ApplicationConstants.ACCESS_TOKEN).toString();
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println(e);
			return ApplicationConstants.FAILED;
		}
	}
	
}
