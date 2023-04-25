package com.eikona.mata.util;



import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.BewardDeviceConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.entity.Area;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.Image;
import com.eikona.mata.repository.DeviceRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.repository.ImageRepository;
import com.eikona.mata.service.ActionService;
import com.eikona.mata.service.DeviceSyncAbstractService;

@Component
//@EnableScheduling
public class BewardDeviceUtil {

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private ActionService actionService;
	
	@Autowired
	private ImageRepository imageRepository;
	
	@Autowired
	private ImageProcessingUtil imageProcessingUtil;
	
	@Autowired
	private DeviceRepository deviceRepository;
	
	@Autowired
	private RequestExecutionUtil requestExecutionUtil;
	
	@Autowired
	@Qualifier(BewardDeviceConstants.SERVICE) 
	private DeviceSyncAbstractService<String> bewardSyncServiceImpl;
	
	@Value("${beward.login.username}")
    private String username;
	
	@Value("${beward.login.password}")
    private String password;
	
	@Value("${beward.device.port}")
    private String port;
	
	 // 0 * * ? * *
	// @Scheduled(cron ="0 0 * ? * *")
	// @Scheduled(fixedDelay =300000)
	public void pullDataFromDeviceThroughScheduler() {
		try {
			List<Device> deviceList = deviceRepository.findAllByModelAndIsDeletedFalse(BewardDeviceConstants.MODEL_TYPE);

			for (Device device : deviceList) {

				bewardSyncServiceImpl.pullEmployeeFromDeviceToMata(device);
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
	public void addEmployeeToBewardDevice(Device device, String token, Principal principal) {
			
			List<Employee> employeeList = employeeRepository.findByArea(device.getArea());
			
			if (ApplicationConstants.CREATE.equalsIgnoreCase(token)) {
				for (Employee employee : employeeList) {
					actionService.employeeDeviceAction(device, employee, token, ApplicationConstants.ACCESS_TYPE_APP, ApplicationConstants.DELIMITER_EMPTY, principal);
				}
			}else
				if(ApplicationConstants.EDIT.equalsIgnoreCase(token)) {
				bewardSyncServiceImpl.deleteAllEmployeeFromDevice(device);
				for(Employee employee :employeeList) {
					actionService.employeeDeviceAction(device,employee, token, ApplicationConstants.ACCESS_TYPE_APP, ApplicationConstants.DELIMITER_EMPTY, principal);
				}
			}
			
			
		}
	public String searchAll(Device device) {
			
			String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH+device.getIpAddress()+ApplicationConstants.DELIMITER_COLON+port+BewardDeviceConstants.PERSON_API_SEARCH_ALL;
			
			String myjson = BewardDeviceConstants.SEARCH_ALL_PERSON_JSON.formatted(device.getSerialNo());
			
			String responseData = executeBewardHttpRequest(myurl , myjson);
			
			return responseData;
		}

		public String executeBewardHttpRequest(String myurl , String myjson) {
			String userName =username;
			String pass =password;
			try {
				HttpPost request = setHttpPostRequestWithBody(userName,pass,myurl, myjson);
				String responeData=null;
		        try {
		        	responeData = requestExecutionUtil.executeHttpPostRequest(request);
					return responeData;
		        }catch (Exception e) {
		        	e.printStackTrace();
		        	return ApplicationConstants.FAILED;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return ApplicationConstants.FAILED;
			}
		}
		private HttpPost setHttpPostRequestWithBody(String userName,String password,String myurl, String myjson) throws UnsupportedEncodingException {
			HttpPost request = new HttpPost(myurl);
			request.setHeader(ApplicationConstants.HEADER_CONTENT_TYPE, ApplicationConstants.APPLICATION_JSON);
			StringEntity entity = new StringEntity(myjson);
			request.getRequestLine();
			request.setEntity(entity);
			
			String auth = userName + ApplicationConstants.DELIMITER_COLON + password;
			byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
			String authHeader = ApplicationConstants.BASIC_AUTH + new String(encodedAuth);
			request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
			return request;
		}
		

		
		public String employeeImageConversionToBase64(Employee personnel) throws IOException {
			
			List<Image> imageList=imageRepository.findByEmployee(personnel);
			Image resizeImage=imageList.get(NumberConstants.ZERO);
			String imagePath = resizeImage.getResizePath();
			
			ByteArrayOutputStream baos = getByteArrayOutputStreamFromPath(imagePath);
	        
	        byte[] bytes = baos.toByteArray();
	        String encodedImage = Base64.encodeBase64String(bytes);
			return encodedImage;
		}

		private ByteArrayOutputStream getByteArrayOutputStreamFromPath(String imagePath) throws IOException {
			
			BufferedImage resizeimage = ImageIO.read(new File(imagePath));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ImageIO.write(resizeimage, ApplicationConstants.FORMAT_JPG, baos);
			return baos;
		}
		public void addToDataBase(int begin, int end, Device device ) throws ParseException, IOException {
			
			String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH+device.getIpAddress()+ApplicationConstants.DELIMITER_COLON+port+BewardDeviceConstants.PERSON_API_GET_ALL_PERSON;
			
			String myjson = BewardDeviceConstants.GET_PERSON_LIST_JSON.formatted(device.getSerialNo(),begin,end);
			
			String responeData = executeBewardHttpRequest(myurl , myjson);
			
			saveBewardEmployeeListInDB(device, responeData);
			
			//return employeeList;
		}
		private void saveBewardEmployeeListInDB(Device device, String responeData) throws ParseException {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
			JSONObject responseObj = (JSONObject) jsonResponse.get(BewardDeviceConstants.INFO);
			JSONArray jsonDataPersonList = (JSONArray) responseObj.get(BewardDeviceConstants.LIST);
			
			//List<Employee> employeeList = new ArrayList<>();
			for (int i = 0; i < jsonDataPersonList.size(); i++) {
				JSONObject personalInfoObj = (JSONObject) jsonDataPersonList.get(i);
				
				long custimizeId = (long) personalInfoObj.get(BewardDeviceConstants.CUSTOMIZE_ID);
//					Employee employee = employeeRepository.findById(custimizeId).get();
				
				
				String idCard = (String) personalInfoObj.get(BewardDeviceConstants.ID_CARD);
				
				Long employeeId = employeeRepository.findByEmpIdAndIsDeletedFalseCustom(idCard);
				if(null == employeeId) {
					
					saveEmployeeWithImage(device, personalInfoObj, custimizeId, idCard);
					
					//employeeList.add(employeeObj);
					
				}
			}
		}
		private void saveEmployeeWithImage(Device device, JSONObject personalInfoObj, long custimizeId, String idCard) {
			Employee employeeObj = new Employee();
			employeeObj.setDeleted(false);
			employeeObj.setEmpId(idCard);
			employeeObj.setGender(((long) personalInfoObj.get(BewardDeviceConstants.GENDER) == 1)?ApplicationConstants.GENDER_FEMALE:ApplicationConstants.GENDER_MALE);
			employeeObj.setId(custimizeId);
			employeeObj.setName((String) personalInfoObj.get(BewardDeviceConstants.NAME));
			List<Area> areaList=new ArrayList<>();
			areaList.add(device.getArea());
			employeeObj.setArea(areaList);
			
			Employee employeeSaved=employeeRepository.save(employeeObj);
			
			String encodedImage=(String) personalInfoObj.get(BewardDeviceConstants.PIC_INFO);
			String base64=encodedImage.split(ApplicationConstants.DELIMITER_COMMA)[1];
			byte[] imageByte=Base64.decodeBase64(base64);
			saveImage(imageByte, employeeSaved);
		}
		public void saveImage(byte[] bytes, Employee employee) {
			InputStream is=null ;
			try {
				List<Image> imageList = new ArrayList<>();
				String fileName = ApplicationConstants.DEFAULT_UNDERSCORE + employee.getEmpId() + ApplicationConstants.EXTENSION_JPG;

				List<Employee> employeeList = new ArrayList<Employee>();
				employeeList.add(employee);
				 is = new ByteArrayInputStream(bytes);
				BufferedImage originalImage = ImageIO.read(is);

				String[] imagePath = imageProcessingUtil.imageProcessing(originalImage, fileName);
				Image imageObj = imageRepository.findByOriginalPath(imagePath[NumberConstants.ZERO]);
				if (null == imageObj) {
					Image imageNewObj = new Image();
					imageNewObj.setEmployee(employeeList);
					imageNewObj.setOriginalPath(imagePath[NumberConstants.ZERO]);
					imageNewObj.setThumbnailPath(imagePath[NumberConstants.TWO]);
					imageNewObj.setResizePath(imagePath[NumberConstants.ONE]);

					imageList.add(imageNewObj);
				}

				imageRepository.saveAll(imageList);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void openDoorControl(Device device) {
			String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH+device.getIpAddress()+ApplicationConstants.DELIMITER_COLON+port+BewardDeviceConstants.OPEN_DOOR_API;
			
			String myjson = BewardDeviceConstants.OPEN_DOOR_JSON.formatted(device.getSerialNo());
			
			executeBewardHttpRequest(myurl , myjson);
		}
		
		
		
}
