package com.eikona.mata.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class UnvErrorMessage {
	public Map<Long, String> errormap = new HashMap<>(); 
	
	
	public  UnvErrorMessage() {
		super();
		errormap.put(1000l, "Algorithm initialization fails.");
		errormap.put(1001l, "Face detection fails.");
		errormap.put(1002l, "No face is found in the picture.");
		errormap.put(1003l, "The JPEG picture fails to be decoded.");
		errormap.put(1004l, "The face picture does not meet quality requirements.");
		errormap.put(1005l, "The picture fails to be zoomed.");
		errormap.put(1006l, "The intelligence function is disabled.");
		errormap.put(1007l, "The imported picture is undersized.");
		errormap.put(1008l, "The imported picture is oversized.");
		errormap.put(1009l, "The resolution of the imported picture exceeds 1920x1080.");
		errormap.put(1010l, "The imported picture does not exist.");
		errormap.put(1011l, "The number of face elements reaches the upper limit.");
		errormap.put(1012l, "The intelligent algorithm model does not match.");
		errormap.put(1013l, "The certificate ID of the member whose face photo is to be imported into the library is invalid.");
		errormap.put(1014l, "The picture format of the member whose face photo is to be imported into the library is incorrect.");
		errormap.put(1015l, "The channel arming has reached the upper limit of the device.");
		errormap.put(1016l, "Another client is operating the face library.");
		errormap.put(1017l, "The face library file is being updated.");
		errormap.put(1018l, "JSON deserialization fails.");
		errormap.put(1019l, "Base64 decoding fails.");
		errormap.put(1020l, "The size of the encoded face photo is different from the photo size in the request.Check whether the size is correct.");
		
	}
}
