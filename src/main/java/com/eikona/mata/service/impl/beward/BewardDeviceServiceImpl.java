package com.eikona.mata.service.impl.beward;



import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpPost;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.BewardDeviceConstants;
import com.eikona.mata.constants.MessageConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.entity.ActionDetails;
import com.eikona.mata.entity.Area;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.Transaction;
import com.eikona.mata.repository.DeviceRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.repository.TransactionRepository;
import com.eikona.mata.service.ActionService;
import com.eikona.mata.service.DeviceSyncAbstractService;
import com.eikona.mata.service.EmployeeService;
import com.eikona.mata.util.BewardDeviceUtil;
import com.eikona.mata.util.RequestExecutionUtil;

@Service
@Qualifier(BewardDeviceConstants.SERVICE)
public class BewardDeviceServiceImpl implements DeviceSyncAbstractService<String>{
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private DeviceRepository deviceRepository;
	
	@Autowired
	private BewardDeviceUtil bewardDeviceUtil;
	
	@Autowired
	private RequestExecutionUtil requestExecutionUtil;
	
	@Autowired
	private ActionService actionService;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Value("${beward.login.username}")
    private String username;
	
	@Value("${beward.login.password}")
    private String password;
	
	@Value("${beward.device.port}")
    private String port;
	

	@Override
	public String deviceBasicInfo(String ipAddress) {
			String myurl=ApplicationConstants.HTTP_COLON_DOUBLE_SLASH+ipAddress+ApplicationConstants.DELIMITER_COLON+
					port+BewardDeviceConstants.CAMERA_API_BASIC_INFO;
			HttpPost request = setHttpPostRequestWithoutBody(myurl);
			String responeData=null;
            try {
            	responeData = requestExecutionUtil.executeHttpPostRequest(request);
				
				return responeData;
            }
		 catch (Exception e ) {
			e.printStackTrace();
			return responeData;
		}
	}
	
	private HttpPost setHttpPostRequestWithoutBody(String myurl) {
		String userName = username;
		String pass = password;
		
		HttpPost request = new HttpPost(myurl);
		request.setHeader(ApplicationConstants.HEADER_CONTENT_TYPE, ApplicationConstants.APPLICATION_JSON);
		
		String auth = userName + ApplicationConstants.DELIMITER_COLON + pass;
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = ApplicationConstants.BASIC_AUTH + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		return request;
	}
	

	@Override
	public void pullEmployeeFromDeviceToMata(Device device){
		try {
		String responeData = bewardDeviceUtil.searchAll(device);
		
		JSONParser jsonParser = new JSONParser();
		Object obj = jsonParser.parse(responeData);
		
		System.out.println(obj.toString());
		JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
		JSONObject responseObj = (JSONObject) jsonResponse.get(BewardDeviceConstants.INFO);
		
		long totalRecord = (long) responseObj.get(BewardDeviceConstants.PERSON_NUM);
		device.setPersonNo(totalRecord);
		
		int next = NumberConstants.ZERO;
		
	//	List<Employee> employeeList = new ArrayList<>();
		for(int i=NumberConstants.ZERO; i< totalRecord ; ) {
			
			next = (totalRecord < NumberConstants.TEN) ? (int) totalRecord :i+NumberConstants.TEN;
			
			 bewardDeviceUtil.addToDataBase(i,next, device);
			
			i = (totalRecord < NumberConstants.TEN)? (int) totalRecord : i+NumberConstants.TEN;
			
//			employeeList.addAll(employee);
//			if(employeeList.size()==100) {
//				employeeRepository.saveAll(employeeList);
//				employeeList.clear();
//			}
//		}
//		if(employeeList.size()>0) {
//			employeeRepository.saveAll(employeeList);
//		}
		}
		deviceRepository.save(device);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String deleteSingleEmployeeFromDevice(ActionDetails actionDetails) {
		Long deviceId=Long.valueOf(actionDetails.getDevice().getSerialNo()) ;
		
		Optional<Employee> personnelOptional = employeeRepository.findById(actionDetails.getAction().getEmployee().getId());
		Employee  personnel = personnelOptional.get();
		
		String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH+actionDetails.getDevice().getIpAddress()+ApplicationConstants.DELIMITER_COLON+port+BewardDeviceConstants.PERSON_API_DELETE;
		
		String myjson = BewardDeviceConstants.DELETE_PERSON_JSON.formatted(deviceId,personnel.getEmpId());
		
		String responeData = bewardDeviceUtil.executeBewardHttpRequest(myurl , myjson);
		return responeData;
	}

	@Override
	public String queryFromDevice(Device device, Employee employee) {
		
		String url = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH+device.getIpAddress()+ApplicationConstants.DELIMITER_COLON+BewardDeviceConstants.PERSON_API_SEARCH;
		String myJson = BewardDeviceConstants.SEARCH_PERSON_JSON.formatted(device.getSerialNo(),employee.getEmpId());
		String responseData = bewardDeviceUtil.executeBewardHttpRequest(url, myJson);
		
		return responseData;
	}

	@Override
	public String deleteAllEmployeeFromDevice(Device device) {
		
		String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH+device.getIpAddress()+ApplicationConstants.DELIMITER_COLON+port+BewardDeviceConstants.PERSON_API_DELETE_ALL;
		
		String myjson =BewardDeviceConstants.DELETE_ALL_PERSON_JSON;
		
		String responeData = bewardDeviceUtil.executeBewardHttpRequest(myurl , myjson);
		
		System.out.println(responeData);
		return responeData;
	}

	@Override
	public String addEmployeeToDevice(ActionDetails actionDetails) {
		try {
		
		Optional<Employee> personnelOptional = employeeRepository.findById(actionDetails.getAction().getEmployee().getId());
		Employee  personnel = personnelOptional.get();
		
		int gender = NumberConstants.ZERO;
		if(ApplicationConstants.GENDER_FEMALE.equalsIgnoreCase(personnel.getGender()))
			gender=NumberConstants.ONE;
		Long deviceId=Long.valueOf(actionDetails.getDevice().getSerialNo()) ;
		
		
		String encodedImage = bewardDeviceUtil.employeeImageConversionToBase64(personnel);
		 
		String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH+actionDetails.getDevice().getIpAddress()+ApplicationConstants.DELIMITER_COLON+port+BewardDeviceConstants.PERSON_API_ADD;
		
		String myjson =BewardDeviceConstants.ADD_PERSON_JSON.formatted(deviceId,personnel.getName(),gender,personnel.getEmpId(),personnel.getId(),personnel.getId(),
				personnel.getEmpId(),encodedImage);
		
		String responeData = bewardDeviceUtil.executeBewardHttpRequest(myurl , myjson);
		return responeData;
		}
		catch (Exception e) {
			e.printStackTrace();
			return ApplicationConstants.FAILED;
		}
	}

	

	@Override
	public String editEmployeeInDevice(ActionDetails actionDetails) {
		try {
		Optional<Employee> personnelOptional = employeeRepository.findById(actionDetails.getAction().getEmployee().getId());
		Employee  personnel = personnelOptional.get();
		
		int gender = NumberConstants.ZERO;
		if(ApplicationConstants.GENDER_FEMALE.equalsIgnoreCase(personnel.getGender()))
			gender=NumberConstants.ONE;
		
		Long deviceId=Long.valueOf(actionDetails.getDevice().getSerialNo()) ;
		String encodedImage = bewardDeviceUtil.employeeImageConversionToBase64(personnel);
		
		String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH+actionDetails.getDevice().getIpAddress()+
				ApplicationConstants.DELIMITER_COLON+port+BewardDeviceConstants.PERSON_API_EDIT;


		String myjson = BewardDeviceConstants.EDIT_PERSON_JSON.formatted(deviceId,personnel.getName(),gender,personnel.getEmpId(),personnel.getId(),personnel.getId(),
				personnel.getEmpId(),encodedImage);
		
		String responeData = bewardDeviceUtil.executeBewardHttpRequest(myurl , myjson);
		return responeData;
		}
		catch (Exception e) {
			e.printStackTrace();
			return ApplicationConstants.FAILED;
		}
	}
	@Override
	public String getTransactionByDate(Device device, Date startDate, Date endDate) {
		  SimpleDateFormat formatSearch = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_US_SEPARATED_BY_T);
			
		String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH+device.getIpAddress()+ApplicationConstants.DELIMITER_COLON+port+BewardDeviceConstants.TRANSACTION_API_SEARCH;
			
			String sDate = formatSearch.format(startDate);
			String eDate = formatSearch.format(endDate);
			String myjson = BewardDeviceConstants.SEARCH_TRANSACTION_JSON.formatted(device.getSerialNo(),sDate,eDate);
			
			String responeData = bewardDeviceUtil.executeBewardHttpRequest(myurl , myjson);
			
			String message = ApplicationConstants.DELIMITER_EMPTY;
			List<Transaction> transactionList= new ArrayList<Transaction>();
			try {
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonVerifyRequest = (JSONObject) jsonParser.parse(responeData);
				JSONObject jsonVerifyInfo = (JSONObject) jsonVerifyRequest.get(BewardDeviceConstants.INFO);
				JSONArray jsonArray = (JSONArray) jsonVerifyInfo.get(BewardDeviceConstants.SEARCH_INFO);

				for (int i = NumberConstants.ZERO; i < jsonArray.size(); i++) {

					Transaction transaction = new Transaction();
					JSONObject personalInfoObj = (JSONObject) jsonArray.get(i);

					setAppearanceInfo(jsonVerifyInfo, transaction, personalInfoObj);
					
					// set punch date
					String date = (String)personalInfoObj.get(BewardDeviceConstants.TIME);
					setDateAndTime(transaction, date);
					// set employee details
					setEmployeeDetails(jsonVerifyInfo, transaction, personalInfoObj);

					// set device and area
					setDeviceAndArea(jsonVerifyInfo, transaction);

					Transaction transactionObj = transactionRepository.findByEmpIdAndPunchDate(transaction.getEmpId(),
							transaction.getPunchDate());
					if (null == transactionObj) {
						transactionList.add(transaction);
					} 

				}
				transactionRepository.saveAll(transactionList);
				message = MessageConstants.TRANSACTION_SUCCESS;
			} catch (Exception e) {
				message = MessageConstants.TRANSACTION_FAILED;
			}
			return message;
		}

	private void setAppearanceInfo(JSONObject jsonVerifyInfo, Transaction transaction, JSONObject personalInfoObj) {
		int deviceId = (int) jsonVerifyInfo.get(BewardDeviceConstants.DEVICE_ID);
		transaction.setDeviceId(String.valueOf(deviceId));
		transaction.setSerialNo(String.valueOf(deviceId));

		transaction.setEmpId((String) personalInfoObj.get(BewardDeviceConstants.ID_CARD));
		transaction.setName((String)personalInfoObj.get(BewardDeviceConstants.NAME));
		
		double temperature =(Double) personalInfoObj.get(BewardDeviceConstants.TEMPERATURE);
		transaction.setTemperature(String.valueOf(temperature));
	}
	private void setDeviceAndArea(JSONObject jsonVerifyInfo, Transaction transaction) {
		Device deviceObj = deviceRepository
				.findBySerialNoAndIsDeletedFalse(String.valueOf((int) jsonVerifyInfo.get(BewardDeviceConstants.DEVICE_ID)));
		if (null != deviceObj) {
			transaction.setDeviceName(deviceObj.getName());
			transaction.setArea(deviceObj.getArea().getName());
			transaction.setSerialNo(deviceObj.getSerialNo());
		}
	}

	private void setEmployeeDetails(JSONObject jsonVerifyInfo, Transaction transaction, JSONObject personalInfoObj) {
		Employee employee = employeeRepository.findByEmpIdAndIsDeletedFalse((String) personalInfoObj.get(BewardDeviceConstants.ID_CARD));

		if (null != employee) {
			
			if (null != employee.getDepartment())
				transaction.setDepartment(employee.getDepartment().getName());

			if (null != employee.getDesignation())
				transaction.setDesignation(employee.getDesignation().getName());

			if (null != employee.getBranch())
				transaction.setBranch(employee.getBranch().getName());
		}
		
	}

	private void setDateAndTime(Transaction transaction, String date) {
		SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_US_SEPARATED_BY_T);
		try {
			transaction.setPunchDate(format.parse(date));
			SimpleDateFormat dateformat = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_INDIA);
			SimpleDateFormat timeformat = new SimpleDateFormat(ApplicationConstants.TIME_FORMAT_24HR);

			String dateStr = dateformat.format(transaction.getPunchDate());
			String timeStr = timeformat.format(transaction.getPunchDate());
			transaction.setPunchDateStr(dateStr);
			transaction.setPunchTimeStr(timeStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void saveAsArea(Employee employee, Principal principal) {

		try {
		Employee empObj = employeeService.getById(employee.getId());
		
		List<Area> newAreaList = employee.getArea();
		List<Area> oldAreaList = empObj.getArea();
		
		assignNewAreaToEmployee(employee, principal, newAreaList, oldAreaList);
		
		removeOldAreaFromEmployee(employee, principal, newAreaList, oldAreaList); 
		
		employeeRepository.save(empObj);
		
	}catch (Exception e) {
		e.printStackTrace();
	}
	
		
	}

	private void removeOldAreaFromEmployee(Employee employee, Principal principal, List<Area> newAreaList,
			List<Area> oldAreaList) {
		for(Area area: oldAreaList) {
			
			if(!newAreaList.contains(area)) {
				List<Area> areaList = new ArrayList<Area>();
				areaList.add(area);
				List<Device> deviceList = deviceRepository.findByAreaAndIsDeletedFalseCustom(areaList);
				Device device = deviceList.get(NumberConstants.ZERO);
				actionService.employeeDeviceAction(device, employee, ApplicationConstants.DELETE, ApplicationConstants.ACCESS_TYPE_APP, ApplicationConstants.DELIMITER_EMPTY, principal);
			}
		}
	}

	private void assignNewAreaToEmployee(Employee employee, Principal principal, List<Area> newAreaList,
			List<Area> oldAreaList) {
		for(Area area : newAreaList) {
			
			if(!oldAreaList.contains(area)) {
					List<Area> areaList = new ArrayList<Area>();
				areaList.add(area);
				List<Device> deviceList = deviceRepository.findByAreaAndIsDeletedFalseCustom(areaList);
				Device device = deviceList.get(NumberConstants.ZERO);
				actionService.employeeDeviceAction(device, employee,ApplicationConstants.EDIT , ApplicationConstants.ACCESS_TYPE_APP, ApplicationConstants.DELIMITER_EMPTY, principal);
			}
		}
	}
	
	
		
}