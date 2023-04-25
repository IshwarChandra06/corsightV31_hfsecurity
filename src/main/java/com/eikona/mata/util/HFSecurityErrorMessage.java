package com.eikona.mata.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
@Component
public class HFSecurityErrorMessage {
	
	public Map<String, String> errormap = new HashMap<>(); 
	
	public  HFSecurityErrorMessage() {
		super();
		errormap.put("000", "Successful request");
		errormap.put("400", "Request parameter verification failed");
		errormap.put("401", "Signature verification failed");
		errormap.put("403", "Permission setting reason denied request");
		errormap.put("404", "Device not connected with network");
		errormap.put("408", "Middleware or device not connected with network");
		errormap.put("500", "The system is busy, please try again later");
		errormap.put("1010", "Device restart failed, un root");
		errormap.put("2000", "Abnormal face creation");
		errormap.put("2010", "Failed to delete face");
		
	}

}
