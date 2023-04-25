package com.eikona.mata.util;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.constants.UnvDeviceConstants;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.repository.DeviceRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.service.ActionService;
import com.eikona.mata.service.DeviceSyncAbstractService;

@Component
//@EnableScheduling
public class UnvDeviceUtil {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private ActionService actionService;

	@Autowired
	private DeviceRepository deviceRepository;
	
	@Autowired
	private RequestExecutionUtil requestExecutionUtil;

	@Autowired
	@Qualifier(UnvDeviceConstants.SERVICE)
	private DeviceSyncAbstractService<Long> unvSyncServiceImpl;

	// 0 * * ? * *
	// @Scheduled(cron ="0 0 * ? * *")
	// @Scheduled(fixedDelay =300000)
	public void pullDataFromDeviceThroughScheduler() {
		try {
			List<Device> deviceList = deviceRepository.findAllByModelAndIsDeletedFalse(UnvDeviceConstants.MODEL_TYPE);

			for (Device device : deviceList) {

				unvSyncServiceImpl.pullEmployeeFromDeviceToMata(device);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * If device is updating area, then all the employees from the device is getting
	 * removed from the device and new area alloted employees will add into the
	 * device.
	 * 
	 * @param device
	 * @param token     -It represens as a flag which contains value like Create and  Edit
	 *                 
	 * @param principal
	 */
	public void addEmployeeToUNVDevice(Device device, String token, Principal principal) {

		List<Employee> employeeList = employeeRepository.findByArea(device.getArea());
		if (ApplicationConstants.CREATE.equalsIgnoreCase(token)) {

			for (Employee employee : employeeList) {
				actionService.employeeDeviceAction(device, employee, token, ApplicationConstants.ACCESS_TYPE_APP, ApplicationConstants.DELIMITER_EMPTY, principal);
			}

		} else if (ApplicationConstants.EDIT.equalsIgnoreCase(token)) {
			List<Employee> employeeListFromDevice = pullingEmployeeListFromDevice(device);

			for (Employee employee : employeeListFromDevice) {
					deleteFromDevice(device, employee);
			}

			for (Employee employee : employeeList) {
				JSONArray searchEmployee = query(device.getIpAddress(), employee.getEmpId());
				if (null == searchEmployee || searchEmployee.isEmpty())
					actionService.employeeDeviceAction(device, employee, token, ApplicationConstants.ACCESS_TYPE_APP, ApplicationConstants.DELIMITER_EMPTY, principal);
			}
		}
			
	}

	/**
	 * All the employee list present in respective device will come.
	 * 
	 * @param device
	 * 
	 */
	public List<Employee> pullingEmployeeListFromDevice(Device device) {

		try {
			String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH + UnvDeviceConstants.PEOPLE_API_SEARCH_INFO.formatted(device.getIpAddress());
			int offset = NumberConstants.ZERO;
			Long countEmployee = NumberConstants.LONG_ZERO;
			Long totalEmployee = NumberConstants.LONG_ZERO;
			JSONArray jsonArray = null;
			List<Employee> employeeList = new ArrayList<>();
			do {
				String myjson = UnvDeviceConstants.PEOPLE_INFO_JSON.formatted(offset);
				offset += NumberConstants.SIX;
				HttpPost request = getHttpPostRequest(myjson, myurl);

				String responeData = null;
				responeData = requestExecutionUtil.executeHttpPostRequest(request);

				JSONParser jsonParser = new JSONParser();
				JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
				JSONObject responseObj = (JSONObject) jsonResponse.get(UnvDeviceConstants.RESPONSE);
				JSONObject responseDataObj = (JSONObject) responseObj.get(UnvDeviceConstants.DATA);
				totalEmployee = (Long) responseDataObj.get(UnvDeviceConstants.TOTAL);
				JSONObject jsonDataPersonListObj = (JSONObject) responseDataObj.get(UnvDeviceConstants.PERSON_LIST);

				countEmployee += ((Long) jsonDataPersonListObj.get(UnvDeviceConstants.NUM));
				device.setPersonNo(countEmployee);

				jsonArray = (JSONArray) jsonDataPersonListObj.get(UnvDeviceConstants.PERSON_INFO_LIST);

				setEmployeeDetails(jsonArray, employeeList);

			} while ((totalEmployee - countEmployee) > NumberConstants.ZERO);

			return employeeList;
		} catch (Exception e) {
			System.out.println(e);
			// e.printStackTrace();
			return null;
		}
	}

	private void setEmployeeDetails(JSONArray jsonArray, List<Employee> employeeList) {
		for (int i = NumberConstants.ZERO; i < jsonArray.size(); i++) {

			JSONObject personalInfoObj = (JSONObject) jsonArray.get(i);

			Employee personnel = new Employee();

			personnel.setEmpId(String.valueOf(personalInfoObj.get(UnvDeviceConstants.PERSON_CODE)));
			personnel.setMobile(String.valueOf(personalInfoObj.get(UnvDeviceConstants.LAST_CHANGE)));

			employeeList.add(personnel);
		}
	}

	/**
	 * This function deletes the given employee from the respective device which are
	 * used as inputs.
	 * 
	 * @param device
	 * @param employee
	 */
	public void deleteFromDevice(Device device, Employee employee) {

		try {
			String myjson =UnvDeviceConstants.PEOPLE_SEARCH_JSON.formatted(employee.getEmpId());
			System.out.println(myjson);
			String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH + UnvDeviceConstants.PEOPLE_API_SEARCH_INFO.formatted(device.getIpAddress());
			HttpPost request = getHttpPostRequest(myjson, myurl);

			String responeData = requestExecutionUtil.executeHttpPostRequest(request);
			System.out.println(responeData);
			long personId = NumberConstants.ZERO;
			long lastChange = NumberConstants.ZERO;

			JSONArray jsonArray = getPeopleInfoArray(responeData);
			for (int i = NumberConstants.ZERO; i < jsonArray.size(); i++) {
				JSONObject personalInfoObj = (JSONObject) jsonArray.get(i);
				personId = (long) personalInfoObj.get(UnvDeviceConstants.PERSON_ID);
				lastChange = (long) personalInfoObj.get(UnvDeviceConstants.LAST_CHANGE);
			}

			String myDeleteurl =  UnvDeviceConstants.PEOPLE_API_DELETE.formatted(device.getIpAddress(), String.valueOf(personId), String.valueOf(lastChange));
			HttpDelete httpDeleterequest = new HttpDelete(myDeleteurl);

			requestExecutionUtil.executeHttpDeleteRequest(httpDeleterequest);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private HttpPost getHttpPostRequest(String myjson, String myurl) throws UnsupportedEncodingException {
		HttpPost request = new HttpPost(myurl);
		StringEntity entity = new StringEntity(myjson);
		request.setEntity(entity);
		return request;
	}

	private JSONArray getPeopleInfoArray(String responeData) throws ParseException {
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
		JSONObject responseObj = (JSONObject) jsonResponse.get(UnvDeviceConstants.RESPONSE);
		JSONObject responseDataObj = (JSONObject) responseObj.get(UnvDeviceConstants.DATA);
		JSONObject jsonDataPersonListObj = (JSONObject) responseDataObj.get(UnvDeviceConstants.PERSON_LIST);
		JSONArray jsonArray = (JSONArray) jsonDataPersonListObj.get(UnvDeviceConstants.PERSON_INFO_LIST);
		return jsonArray;
	}

	/**
	 * This function is used to push employee into unv device.
	 * @param url
	 * @param json
	 * 
	 */
	public Long create(String url, String json) {
		try {
			HttpPost request = getHttpPostRequest(json, url);
			String responeData =requestExecutionUtil.executeHttpPostRequest(request);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
			JSONObject responseObj = (JSONObject) jsonResponse.get(UnvDeviceConstants.RESPONSE);
			JSONObject responseDataObj = (JSONObject) responseObj.get(UnvDeviceConstants.DATA);

			JSONArray jsonDataPersonList = (JSONArray) responseDataObj.get(UnvDeviceConstants.PERSON_LIST);
			JSONObject jsonDataPerson = (JSONObject) jsonDataPersonList.get(NumberConstants.ZERO);

			JSONArray jsonFaceList = (JSONArray) jsonDataPerson.get(UnvDeviceConstants.FACE_LIST);
			JSONObject jsonDataFace = (JSONObject) jsonFaceList.get(NumberConstants.ZERO);

			Long resultCode = (Long) jsonDataFace.get(UnvDeviceConstants.RESULT_CODE);

			return resultCode;
		} catch (Exception e) {
			return null;
		}

	}
	/**
	 * This function is used to edit employee in unv device.
	 * @param url
	 * @param json
	 * 
	 */
	public Long edit(String url, String json) throws Exception {
		HttpPut request = getHttpPutRequest(url, json);
		String responeData =requestExecutionUtil.executeHttpPutRequest(request);
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
		JSONObject responseObj = (JSONObject) jsonResponse.get(UnvDeviceConstants.RESPONSE);
		JSONObject responseDataObj = (JSONObject) responseObj.get(UnvDeviceConstants.DATA);

		JSONArray jsonDataPersonList = (JSONArray) responseDataObj.get(UnvDeviceConstants.PERSON_LIST);
		JSONObject jsonDataPerson = (JSONObject) jsonDataPersonList.get(NumberConstants.ZERO);

		JSONArray jsonFaceList = (JSONArray) jsonDataPerson.get(UnvDeviceConstants.FACE_LIST);
		JSONObject jsonDataFace = (JSONObject) jsonFaceList.get(NumberConstants.ZERO);

		Long resultCode = (Long) jsonDataFace.get(UnvDeviceConstants.RESULT_CODE);

		return resultCode;
	}

	private HttpPut getHttpPutRequest(String url, String json) throws UnsupportedEncodingException {
		HttpPut request = new HttpPut(url);
		request.setHeader(ApplicationConstants.HEADER_CONTENT_TYPE, ApplicationConstants.APPLICATION_JSON);
		StringEntity entity = new StringEntity(json);
		request.getRequestLine();
		request.setEntity(entity);
		return request;
	}
/**
 * This function is used to search the employee in the respective device which ip Address is passed as a input.
 * 
 * @param ipAddress  -Device Ip Address
 * @param employeeId -Employee's unique id
 * 
 */
	public JSONArray query(String ipAddress, String employeeId) {
		try {
			String myjson = UnvDeviceConstants.PEOPLE_SEARCH_JSON.formatted(employeeId);
			System.out.println(myjson);
			String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH +UnvDeviceConstants.PEOPLE_API_SEARCH_INFO.formatted(ipAddress);
			HttpPost request = getHttpPostRequest(myjson, myurl);

			String responeData=requestExecutionUtil.executeHttpPostRequest(request);
			System.out.println(responeData);

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
			JSONObject responseObj = (JSONObject) jsonResponse.get(UnvDeviceConstants.RESPONSE);
			JSONObject responseDataObj = (JSONObject) responseObj.get(UnvDeviceConstants.DATA);
			JSONObject jsonDataPersonListObj = (JSONObject) responseDataObj.get(UnvDeviceConstants.PERSON_LIST);
			JSONArray jsonArray = (JSONArray) jsonDataPersonListObj.get(UnvDeviceConstants.PERSON_INFO_LIST);
			return jsonArray;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
