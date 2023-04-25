package com.eikona.mata.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.HFDeviceConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.entity.Area;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.Image;
import com.eikona.mata.repository.DeviceRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.repository.ImageRepository;

@Component
public class HFSecurityDeviceUtil {

	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private ImageProcessingUtil imageProcessingUtil;

	@Value("${hf.server.url}")
	private String hfServerIp;

	@Value("${hf.server.secret}")
	private String hfServerSecret;

	@Autowired
	private RequestExecutionUtil requestExecutionUtil;

	public void addAndUpdateEmployeeInHF() {

		List<Employee> empList = new ArrayList<>();
		List<Employee> employeeList = employeeRepository.findAllByIsDeletedFalseAndIsSyncFalse();
		for (Employee employee : employeeList) {
			if (null != employee.getArea()) {
				List<Device> deviceList = deviceRepository.findByAreaAndIsDeletedFalseCustom(employee.getArea());

				for (Device device : deviceList) {

					if ("HF-Security".equalsIgnoreCase(device.getModel())) {
						try {
							boolean success = addEmployeeToHFDevice(employee, device);
							if (success) {
								employee.setSync(true);
								empList.add(employee);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		employeeRepository.saveAll(empList);
	}

	public void addEmployeeFaceInHF() {

		List<Employee> empList = new ArrayList<>();
		List<Employee> employeeList = employeeRepository.findAllByIsDeletedFalseAndIsSyncTrueAndIsFaceSyncFalse();
		for (Employee employee : employeeList) {
			if (null != employee.getArea()) {
				List<Device> deviceList = deviceRepository.findByAreaAndIsDeletedFalseCustom(employee.getArea());

				for (Device device : deviceList) {
					if ("HF-Security".equalsIgnoreCase(device.getModel())) {
						try {
							boolean success = addEmployeeFaceToHFDevice(employee, device);
							if (success) {
								employee.setFaceSync(true);
								empList.add(employee);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		employeeRepository.saveAll(empList);
	}

	public void deleteEmployeeFromHF() {
		List<Employee> employeeList = employeeRepository.findAllByIsDeletedTrueAndIsSyncTrue();
		for (Employee employee : employeeList) {
			if (null != employee.getArea()) {
				List<Device> deviceList = deviceRepository.findByAreaAndIsDeletedFalseCustom(employee.getArea());

				for (Device device : deviceList) {

					if ("HF-Security".equalsIgnoreCase(device.getModel())) {
						try {
							deleteEmployeeFromHFDevice(employee.getEmpId(), device);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

//Delete Employee From HF Device
	public boolean deleteEmployeeFromHFDevice(String empId, Device device) {

		String deviceKey = device.getSerialNo();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH + hfServerIp + ApplicationConstants.DELIMITER_COLON
				+ NumberConstants.EIGHT_THOUSAND_ONE_HUNDRED_NINTY + "/api/person/del";
		HttpPost request = new HttpPost(myurl);
		request.setHeader(ApplicationConstants.HEADER_CONTENT_TYPE, ApplicationConstants.X_WWW_FORM_URLENCODED);

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("deviceKey", deviceKey));
		postParameters.add(new BasicNameValuePair("secret", hfServerSecret));
		postParameters.add(new BasicNameValuePair("personId", empId));

		boolean success = false;
		try {
			request.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
			getSuccessResponse(request, success);
		} catch (Exception e) {
			success = false;
		}
		return success;
	}

//Search Employee In HF Device	
	public boolean searchEmployeeFromHFDevice(String empId, Device device) {

		String deviceKey = device.getSerialNo();

		String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH + hfServerIp + ApplicationConstants.DELIMITER_COLON
				+ NumberConstants.EIGHT_THOUSAND_ONE_HUNDRED_NINTY + "/api/person/find" + "?secret=" + hfServerSecret
				+ "&deviceKey=" + deviceKey + "&personId=" + empId;

		HttpPost request = new HttpPost(myurl);
		request.setHeader(ApplicationConstants.HEADER_CONTENT_TYPE, ApplicationConstants.APPLICATION_JSON);

		boolean success = false;
		try {
			getSuccessResponse(request, success);
		} catch (Exception e) {
			success = false;
		}
		return success;
	}

	// Delete Employee Face From Device
	public boolean deleteEmployeeFaceFromHF(String empId, Device device) {
		String deviceKey = device.getSerialNo();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH + hfServerIp + ApplicationConstants.DELIMITER_COLON
				+ NumberConstants.EIGHT_THOUSAND_ONE_HUNDRED_NINTY + "/api/face/clear";
		HttpPost request = new HttpPost(myurl);
		request.setHeader(ApplicationConstants.HEADER_CONTENT_TYPE, ApplicationConstants.X_WWW_FORM_URLENCODED);

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("deviceKey", deviceKey));
		postParameters.add(new BasicNameValuePair("secret", hfServerSecret));
		postParameters.add(new BasicNameValuePair("personId", empId));

		boolean success = false;
		try {
			request.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
			getSuccessResponse(request, success);
		} catch (Exception e) {
			success = false;
		}
		return success;
	}

//Pull All Employee From HF Security	
	public void pullAllEmployeeFromHFDevice(Device device) {
		try {
			int index = NumberConstants.ZERO;
			Long countEmployee = NumberConstants.LONG_ZERO;
			Long totalEmployee = NumberConstants.LONG_ZERO;
			do {

				String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH + hfServerIp
						+ ApplicationConstants.DELIMITER_COLON + NumberConstants.EIGHT_THOUSAND_ONE_HUNDRED_NINTY
						+ "/api/person/list/find" + "?secret=" + hfServerSecret + "&deviceKey=" + device.getSerialNo()
						+ "&index=" + index + "&length=" + 500;

				HttpGet request = new HttpGet(myurl);
				String responeData = requestExecutionUtil.executeHttpGetRequest(request);

				JSONParser jsonParser = new JSONParser();
				JSONObject jsonResponse = null;
				try {
					jsonResponse = (JSONObject) jsonParser.parse(responeData);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				JSONObject responseDataObj = (JSONObject) jsonResponse.get(HFDeviceConstants.DATA);

				JSONObject pageInfoObj = (JSONObject) responseDataObj.get(HFDeviceConstants.PAGE_INFO);
				totalEmployee = (Long) pageInfoObj.get(HFDeviceConstants.TOTAL);

				JSONArray jsonArray = (JSONArray) responseDataObj.get(HFDeviceConstants.RECORDS);
				countEmployee += jsonArray.size();
				saveEmployeeFromHFDevice(device, jsonArray);

				index += NumberConstants.ONE;

			} while ((totalEmployee - countEmployee) > NumberConstants.ZERO);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void saveEmployeeFromHFDevice(Device device, JSONArray jsonArray) {
		List<Employee> employeeList = new ArrayList<Employee>();
		for (int i = NumberConstants.ZERO; i < jsonArray.size(); i++) {
			JSONObject personalInfoObj = (JSONObject) jsonArray.get(i);
			Employee personnelData = employeeRepository
					.findByEmpIdAndIsDeletedFalse((String) personalInfoObj.get(HFDeviceConstants.ID));

			Employee employee = new Employee();
			if (null == personnelData) {
				employee.setEmpId((String) personalInfoObj.get(HFDeviceConstants.ID));
				employee.setName((String) personalInfoObj.get(ApplicationConstants.NAME));
				employee.setCardNo((String) personalInfoObj.get(HFDeviceConstants.ID_CARD_NO));
				employee.setBranch(device.getBranch());
				List<Area> areaList = new ArrayList<>();
				areaList.add(device.getArea());
				employee.setArea(areaList);
				employee.setSyncFromDevice(true);
				employeeList.add(employee);
			}
		}
		employeeRepository.saveAll(employeeList);
	}

//Pull All Employee Image From HF Security
	public void pullAllEmployeeFaceFromHFDevice(String deviceKey) {
		List<Employee> employeeList = employeeRepository.findAllByIsDeletedFalseAndIsSyncFromDeviceTrue();
		List<Employee> savedList = new ArrayList<>();
		for (Employee employee : employeeList) {
			pullParticularEmployeeFaceFromHF(employee, deviceKey);
			employee.setFaceSyncFromDevice(true);
			savedList.add(employee);
		}
		employeeRepository.saveAll(employeeList);
	}

	public void pullParticularEmployeeFaceFromHF(Employee employee, String deviceKey) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH + hfServerIp + ApplicationConstants.DELIMITER_COLON
				+ NumberConstants.EIGHT_THOUSAND_ONE_HUNDRED_NINTY + "/api/face/find";
		HttpPost request = new HttpPost(myurl);
		request.setHeader(ApplicationConstants.HEADER_CONTENT_TYPE, ApplicationConstants.X_WWW_FORM_URLENCODED);

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("deviceKey", deviceKey));
		postParameters.add(new BasicNameValuePair("secret", hfServerSecret));
		postParameters.add(new BasicNameValuePair("personId", employee.getEmpId()));
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonResponse = null;
		try {
			request.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
			String response = requestExecutionUtil.executeHttpPostRequest(request);
			System.out.println(response);
			jsonResponse = (JSONObject) jsonParser.parse(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONArray imageArray = (JSONArray) jsonResponse.get(HFDeviceConstants.DATA);
		byte[] imageByte = getByteArrayFromJsonArray(imageArray);
		saveImage(imageByte, employee);
	}

	private byte[] getByteArrayFromJsonArray(JSONArray imageArray) {
		byte[] imageByte = null;
		for (int j = NumberConstants.ZERO; j < imageArray.size(); j++) {
			JSONObject imageInfoObj = (JSONObject) imageArray.get(j);
			String encodedImage = (String) imageInfoObj.get(HFDeviceConstants.IMG_BASE64);
			imageByte = Base64.decodeBase64(encodedImage);
		}
		return imageByte;
	}

	public void saveImage(byte[] bytes, Employee employee) {

		try {
			List<Image> imageList = new ArrayList<>();
			String fileName = employee.getEmpId() + ApplicationConstants.EXTENSION_JPG;

			List<Employee> employeeList = new ArrayList<Employee>();
			employeeList.add(employee);
			InputStream is = new ByteArrayInputStream(bytes);
			BufferedImage originalImage = ImageIO.read(is);

			String[] imagePath = imageProcessingUtil.imageProcessing(originalImage, fileName);
			Image imageObj = imageRepository.findByOriginalPath(imagePath[NumberConstants.ZERO]);
			if (null == imageObj) {
				Image imageNewObj = new Image();
				imageNewObj.setEmployee(employeeList);
				imageNewObj.setOriginalPath(imagePath[NumberConstants.ZERO]);
				imageNewObj.setThumbnailPath(imagePath[NumberConstants.ONE]);
				imageNewObj.setResizePath(imagePath[NumberConstants.TWO]);

				imageList.add(imageNewObj);
			}
			imageRepository.saveAll(imageList);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//Restart the device
	public boolean resetHFDevice(String deviceKey) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH + hfServerIp + ApplicationConstants.DELIMITER_COLON
				+ NumberConstants.EIGHT_THOUSAND_ONE_HUNDRED_NINTY + "/api/device/reset";
		HttpPost request = new HttpPost(myurl);
		request.setHeader(ApplicationConstants.HEADER_CONTENT_TYPE, ApplicationConstants.X_WWW_FORM_URLENCODED);

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("deviceKey", deviceKey));
		postParameters.add(new BasicNameValuePair("secret", hfServerSecret));

		boolean success = false;
		try {
			request.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
			getSuccessResponse(request, success);
		} catch (Exception e) {
			success = false;
		}
		return success;
	}

//Restart the device
	public boolean rebootHFDevice(String deviceKey) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH + hfServerIp + ApplicationConstants.DELIMITER_COLON
				+ NumberConstants.EIGHT_THOUSAND_ONE_HUNDRED_NINTY + "/api/device/reboot";
		HttpPost request = new HttpPost(myurl);
		request.setHeader(ApplicationConstants.HEADER_CONTENT_TYPE, ApplicationConstants.X_WWW_FORM_URLENCODED);

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("deviceKey", deviceKey));
		postParameters.add(new BasicNameValuePair("secret", hfServerSecret));

		boolean success = false;
		try {
			request.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
			getSuccessResponse(request, success);
		} catch (Exception e) {
			success = false;
		}
		return success;
	}

//set Time Configuration in hf security	
	public boolean setTimeInHFDevice(String deviceKey) {
		Date dte = new Date();
		long milliSeconds = dte.getTime();
		String strLong = Long.toString(milliSeconds);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH + hfServerIp + ApplicationConstants.DELIMITER_COLON
				+ NumberConstants.EIGHT_THOUSAND_ONE_HUNDRED_NINTY + "/api/device/setTime";
		HttpPost request = new HttpPost(myurl);
		request.setHeader(ApplicationConstants.HEADER_CONTENT_TYPE, ApplicationConstants.X_WWW_FORM_URLENCODED);

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("deviceKey", deviceKey));
		postParameters.add(new BasicNameValuePair("secret", hfServerSecret));
		postParameters.add(new BasicNameValuePair("timestamp", strLong));

		boolean success = false;
		try {
			request.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
			getSuccessResponse(request, success);
		} catch (Exception e) {
			success = false;
		}
		return success;
	}

	// Add and Update Employee In HF Device
	public boolean addEmployeeToHFDevice(Employee employee, Device device) {

		Optional<Employee> personnelOptional = employeeRepository.findById(employee.getId());
		Employee personnel = personnelOptional.get();
		String deviceKey = device.getSerialNo();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

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
		boolean success = false;
		try {
			request.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
			success=getSuccessResponse(request, success);
		} catch (Exception e) {
			success = false;
		}

		return success;

	}

//Add Employee Face In HF Device	
	public boolean addEmployeeFaceToHFDevice(Employee employee, Device device) {
		Optional<Employee> personnelOptional = employeeRepository.findById(employee.getId());
		Employee personnel = personnelOptional.get();
		String deviceKey = device.getSerialNo();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH + hfServerIp + ApplicationConstants.DELIMITER_COLON
				+ NumberConstants.EIGHT_THOUSAND_ONE_HUNDRED_NINTY + "/api/face/add";

		HttpPost request = new HttpPost(myurl);
		request.setHeader(ApplicationConstants.HEADER_CONTENT_TYPE, ApplicationConstants.X_WWW_FORM_URLENCODED);
		String imgBase64 = employeeImageConversionToBase64(personnel);

		boolean success = false;
		if (!imgBase64.isEmpty()) {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("deviceKey", deviceKey));
			postParameters.add(new BasicNameValuePair("secret", hfServerSecret));
			postParameters.add(new BasicNameValuePair("personId", personnel.getEmpId()));
			postParameters.add(new BasicNameValuePair("imgBase64", imgBase64));
			try {
				request.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
				success = getSuccessResponse(request, success);
			} catch (Exception e) {
				success = false;
			}
		}
		return success;
	}

	private boolean getSuccessResponse(HttpPost request, boolean success) throws Exception, ParseException {
		String response = requestExecutionUtil.executeHttpPostRequest(request);
		System.out.println(response);
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonResponse = (JSONObject) jsonParser.parse(response);
		success = (Boolean) jsonResponse.get("success");
		return success;
	}

	public String employeeImageConversionToBase64(Employee personnel) {
		String encodedImage = "";
		try {
		List<Image> imageList = imageRepository.findByEmployee(personnel);
		Image resizeImage = imageList.get(NumberConstants.ZERO);
		String imagePath = resizeImage.getResizePath();
		
		
			ByteArrayOutputStream baos = getByteArrayOutputStreamFromPath(imagePath);
			byte[] bytes = baos.toByteArray();
			encodedImage = Base64.encodeBase64String(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return encodedImage;
	}

	private ByteArrayOutputStream getByteArrayOutputStreamFromPath(String imagePath) throws IOException {

		BufferedImage resizeimage = ImageIO.read(new File(imagePath));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(resizeimage, ApplicationConstants.FORMAT_JPG, baos);
		return baos;
	}

}
