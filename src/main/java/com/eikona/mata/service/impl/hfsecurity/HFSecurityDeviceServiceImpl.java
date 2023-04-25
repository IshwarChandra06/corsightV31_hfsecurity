package com.eikona.mata.service.impl.hfsecurity;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.entity.ActionDetails;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.service.DeviceSyncAbstractService;
import com.eikona.mata.util.RequestExecutionUtil;
@Service
@Qualifier("hfsecurityDeviceService")
public class HFSecurityDeviceServiceImpl implements DeviceSyncAbstractService<String>{
	
	@Value("${hf.server.url}")
	private String hfServerIp;

	@Value("${hf.server.secret}")
	private String hfServerSecret;
	
	@Autowired
	private RequestExecutionUtil requestExecutionUtil;
	

	@Override
	public String deviceBasicInfo(String ipAddress) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void pullEmployeeFromDeviceToMata(Device device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String deleteSingleEmployeeFromDevice(ActionDetails actionDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deleteAllEmployeeFromDevice(Device device) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String addEmployeeToDevice(ActionDetails actionDetails) {

		Employee personnel = actionDetails.getAction().getEmployee();
		String deviceKey = actionDetails.getDevice().getSerialNo();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		String returnCode = addEmployeeDetailsToDevice(personnel, deviceKey);
		
		return returnCode;

	}

	private String addEmployeeDetailsToDevice(Employee personnel, String deviceKey) {
		String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH + hfServerIp + ApplicationConstants.DELIMITER_COLON
				+ NumberConstants.EIGHT_THOUSAND_ONE_HUNDRED_NINTY + "/api/person/add";

		HttpPost request = new HttpPost(myurl);
		request.setHeader(ApplicationConstants.HEADER_CONTENT_TYPE, ApplicationConstants.X_WWW_FORM_URLENCODED);

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("deviceKey", deviceKey));
		postParameters.add(new BasicNameValuePair("secret", hfServerSecret));
		postParameters.add(new BasicNameValuePair("id", personnel.getEmpId()));
		postParameters.add(new BasicNameValuePair("name", personnel.getName()));
		postParameters.add(new BasicNameValuePair("idcardNum", personnel.getEmpId()));
		String returnCode = "";
		try {
			request.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
			returnCode=getResponse(request, returnCode);
		} catch (Exception e) {
			returnCode = "Failed";
		}
		return returnCode;
	}
	private String getResponse(HttpPost request, String returnCode) throws Exception, ParseException {
		String response = requestExecutionUtil.executeHttpPostRequest(request);
		System.out.println(response);
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonResponse = (JSONObject) jsonParser.parse(response);
		returnCode = (String) jsonResponse.get("code");
		return returnCode;
	}
	@Override
	public String editEmployeeInDevice(ActionDetails actionDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String queryFromDevice(Device device, Employee employee) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTransactionByDate(Device device, Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveAsArea(Employee employee, Principal principal) {
		// TODO Auto-generated method stub
		
	}

}
