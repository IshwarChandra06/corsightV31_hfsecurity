package com.eikona.mata.util;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.CosecDeviceConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.entity.Transaction;

@Component
@EnableScheduling
public class CosecTransactionUtil {
	@Value("${cosec.host.url}")
    private String host;
	
	@Value("${cosec.host.username}")
    private String username;
	
	@Value("${cosec.host.password}")
    private String password;
	
	@Autowired
	private RequestExecutionUtil requestExecutionUtil;
	
	//@Scheduled(fixedDelay = 3000)
	public String transactionSendToCosec(Transaction transaction) {
		try {
			String userName = username;
			String pass = password;
			String cosecHostUrl = host;
			
			int accessType=NumberConstants.ZERO;
			
			if(ApplicationConstants.ACCESS_TYPE_CHECK_OUT.equalsIgnoreCase(transaction.getAccessType())) 
				accessType=NumberConstants.ONE;
			else 
				accessType=NumberConstants.ZERO;
			
            SimpleDateFormat dateFormat= new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_INDIA_WITHOUT_DELIMITER);
            String datetime=dateFormat.format(transaction.getPunchDate());
			String myurl=ApplicationConstants.HTTP_COLON_DOUBLE_SLASH+cosecHostUrl+CosecDeviceConstants.TRANSACTION_API_SYNC.formatted(datetime,transaction.getEmpId(),accessType);
            HttpGet request = getHttpGetRequest(userName, pass, myurl);
			String responeData=requestExecutionUtil.executeHttpGetRequest(request);
				return responeData;
           
		} catch (Exception e) {
			e.printStackTrace();
			return ApplicationConstants.FAILED;
		}
	}
	
	//@Scheduled(fixedDelay = 3000)
	public String getEmployeeFromCosec() {
		try {
			String userName = username;
			String pass = password;
			String cosecHostUrl = host;
			
			String empId=null;
			
			String myurl=ApplicationConstants.HTTP_COLON_DOUBLE_SLASH+cosecHostUrl+CosecDeviceConstants.EMPLOYEE_API_SEARCH.formatted(empId);
			 HttpGet request = getHttpGetRequest(userName, pass, myurl);
			String responeData=requestExecutionUtil.executeHttpGetRequest(request);
				return responeData;
		} catch (Exception e) {
			e.printStackTrace();
			return ApplicationConstants.FAILED;
		}
		
	}
	
	private HttpGet getHttpGetRequest(String userName, String password, String myurl) {
		HttpGet request = new HttpGet(myurl);
		String auth = userName + ApplicationConstants.DELIMITER_COLON + password;
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = ApplicationConstants.BASIC_AUTH + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		return request;
	}
}
