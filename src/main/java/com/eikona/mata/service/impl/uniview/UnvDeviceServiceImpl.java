package com.eikona.mata.service.impl.uniview;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.constants.UnvDeviceConstants;
import com.eikona.mata.entity.ActionDetails;
import com.eikona.mata.entity.Area;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.Image;
import com.eikona.mata.repository.DeviceRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.repository.ImageRepository;
import com.eikona.mata.service.ActionService;
import com.eikona.mata.service.DeviceSyncAbstractService;
import com.eikona.mata.service.EmployeeService;
import com.eikona.mata.util.ImageProcessingUtil;
import com.eikona.mata.util.RequestExecutionUtil;
import com.eikona.mata.util.UnvDeviceUtil;

@Service
//@EnableScheduling
@Qualifier(UnvDeviceConstants.SERVICE)
public class UnvDeviceServiceImpl implements DeviceSyncAbstractService<Long> {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private ImageProcessingUtil imageProcessingUtil;

	@Autowired
	private UnvDeviceUtil unvDeviceUtil;

	@Autowired
	private ActionService actionService;

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private RequestExecutionUtil requestExecution;

	@Override
	public Long deviceBasicInfo(String ipAddress) {
		Long responseCode = null;
		try {
			String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH + UnvDeviceConstants.DEVICE_API_BASIC_INFO.formatted(ipAddress);
			HttpGet request = new HttpGet(myurl);

			String responeData = requestExecution.executeHttpGetRequest(request);

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
			JSONObject responseObj = (JSONObject) jsonResponse.get(UnvDeviceConstants.RESPONSE);

			responseCode = (Long) responseObj.get(UnvDeviceConstants.RESPONSE_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseCode;
	}

	@Override
	public void pullEmployeeFromDeviceToMata(Device device) {
		try {

			String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH + UnvDeviceConstants.PEOPLE_API_SEARCH_INFO.formatted(device.getIpAddress());

			int offset = NumberConstants.ZERO;
			Long countEmployee = NumberConstants.LONG_ZERO;
			Long totalEmployee = NumberConstants.LONG_ZERO;
			do {

				String myjson = UnvDeviceConstants.PEOPLE_INFO_JSON.formatted(offset);

				HttpPost request = new HttpPost(myurl);
				StringEntity entity = new StringEntity(myjson);
				request.setEntity(entity);
				String responeData = requestExecution.executeHttpPostRequest(request);

				JSONParser jsonParser = new JSONParser();
				JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
				JSONObject responseObj = (JSONObject) jsonResponse.get(UnvDeviceConstants.RESPONSE);

				JSONObject responseDataObj = (JSONObject) responseObj.get(UnvDeviceConstants.DATA);
				totalEmployee = (Long) responseDataObj.get(UnvDeviceConstants.TOTAL);
				JSONObject jsonDataPersonListObj = (JSONObject) responseDataObj.get(UnvDeviceConstants.PERSON_LIST);

				countEmployee += ((Long) jsonDataPersonListObj.get(UnvDeviceConstants.NUM));
				device.setPersonNo(countEmployee);

				JSONArray jsonArray = (JSONArray) jsonDataPersonListObj.get(UnvDeviceConstants.PERSON_INFO_LIST);

				saveEmployeeFromUnvDevice(device, jsonArray);

				offset += NumberConstants.SIX;

			} while ((totalEmployee - countEmployee) > NumberConstants.ZERO);

			deviceRepository.save(device);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveEmployeeFromUnvDevice(Device device, JSONArray jsonArray) {
		for (int i = NumberConstants.ZERO; i < jsonArray.size(); i++) {
			JSONObject personalInfoObj = (JSONObject) jsonArray.get(i);
			Employee personnelData = employeeRepository
					.findByEmpIdAndIsDeletedFalse((String) personalInfoObj.get(UnvDeviceConstants.PERSON_CODE));

			JSONArray imageArray = (JSONArray) personalInfoObj.get(UnvDeviceConstants.IMAGE_LIST);

			byte[] imageByte = getByteArrayFromJsonArray(imageArray);

			Employee employee = new Employee();
			if (null == personnelData) {
				employee.setEmpId((String) personalInfoObj.get(UnvDeviceConstants.PERSON_CODE));
				employee.setName((String) personalInfoObj.get(UnvDeviceConstants.PERSON_NAME));
				List<Area> areaList = new ArrayList<>();
				areaList.add(device.getArea());
				employee.setArea(areaList);
				Employee personnelSave = employeeRepository.save(employee);
				saveImage(imageByte, personnelSave);
			}
		}
	}

	private byte[] getByteArrayFromJsonArray(JSONArray imageArray) {
		byte[] imageByte = null;
		for (int j = NumberConstants.ZERO; j < imageArray.size(); j++) {
			JSONObject imageInfoObj = (JSONObject) imageArray.get(j);
			String encodedImage = (String) imageInfoObj.get(UnvDeviceConstants.DATA);
			imageByte = Base64.decodeBase64(encodedImage);
		}
		return imageByte;
	}

	public void saveImage(byte[] bytes, Employee employee) {

		try {
			List<Image> imageList = new ArrayList<>();
			//String fileName = "default_" + employee.getEmpId() + IMAGE_JPG_EXTENSION;
			String fileName =  employee.getEmpId() + ApplicationConstants.EXTENSION_JPG;

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

	@Override
	public Long deleteSingleEmployeeFromDevice(ActionDetails actionDetails) {
		try {
			String ipAddress = actionDetails.getDevice().getIpAddress();
			String myjson = UnvDeviceConstants.PEOPLE_SEARCH_JSON.formatted(actionDetails.getAction().getEmployee().getEmpId());

			String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH +  UnvDeviceConstants.PEOPLE_API_SEARCH_INFO.formatted(ipAddress);

			HttpPost request = new HttpPost(myurl);
			StringEntity entity = new StringEntity(myjson);
			request.setEntity(entity);

			String responeData = requestExecution.executeHttpPostRequest(request);
			

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);

			deleteEmployee(ipAddress, jsonResponse);

			return 200l;
		} catch (Exception e) {
			e.printStackTrace();
			return -1l;
		}

	}

	private void deleteEmployee(String ipAddress, JSONObject jsonResponse) {
		JSONObject responseObj = (JSONObject) jsonResponse.get( UnvDeviceConstants.RESPONSE);
		JSONObject responseDataObj = (JSONObject) responseObj.get( UnvDeviceConstants.DATA);
		JSONObject jsonDataPersonListObj = (JSONObject) responseDataObj.get( UnvDeviceConstants.PERSON_LIST);
		JSONArray jsonArray = (JSONArray) jsonDataPersonListObj.get( UnvDeviceConstants.PERSON_INFO_LIST);

		long personId = NumberConstants.ZERO;
		long lastChange = NumberConstants.ZERO;
		for (int i = NumberConstants.ZERO; i < jsonArray.size(); i++) {
			JSONObject personalInfoObj = (JSONObject) jsonArray.get(i);
			personId = (long) personalInfoObj.get( UnvDeviceConstants.PERSON_ID);
			lastChange = (long) personalInfoObj.get( UnvDeviceConstants.LAST_CHANGE);
		}

		String deleteUrl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH
				+  UnvDeviceConstants.PEOPLE_API_DELETE.formatted(ipAddress, String.valueOf(personId), String.valueOf(lastChange));

		HttpDelete deleterequest = new HttpDelete(deleteUrl);

		requestExecution.executeHttpDeleteRequest(deleterequest);
	}

	@SuppressWarnings(ApplicationConstants.DEPRECATION)
	@Override
	public Long addEmployeeToDevice(ActionDetails actionDetails) {
		Optional<Employee> personnelOptional = employeeRepository.findById(actionDetails.getAction().getEmployee().getId());
		Long statusCode = -NumberConstants.LONG_ONE;
		try {
			Employee employee = personnelOptional.get();

			Date now = new Date();
			Long lastChange = new Long(now.getTime() / NumberConstants.THOUSAND);

			List<Image> imageList = imageRepository.findByEmployee(employee);

			if (!imageList.isEmpty()) {
				Image resizeImage = imageList.get(NumberConstants.ZERO);
				String imagePath = resizeImage.getResizePath();
				BufferedImage resizeimage = ImageIO.read(new File(imagePath));
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(resizeimage, ApplicationConstants.FORMAT_JPG, baos);
				byte[] bytes = baos.toByteArray();
				String encodedImage = Base64.encodeBase64String(bytes);

				String myjson = UnvDeviceConstants.PEOPLE_ADD_AND_UPDATE_JSON.formatted(employee.getId(), lastChange, employee.getEmpId(),
						employee.getName(), employee.getName(), encodedImage.length(), encodedImage);

				String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH
						+  UnvDeviceConstants.PEOPLE_API_ADD_AND_UPDATE.formatted(actionDetails.getDevice().getIpAddress());

				statusCode = getStatusCode(actionDetails, employee, myjson, myurl);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusCode;
	}

	private Long getStatusCode(ActionDetails actionDetails, Employee employee, String myjson, String myurl)
			throws Exception {
		Long statusCode = null;
		if (ApplicationConstants.EDIT.equalsIgnoreCase(actionDetails.getAction().getType())) {
			JSONArray jsonArray = unvDeviceUtil.query(actionDetails.getDevice().getIpAddress(), employee.getEmpId());
			if (null == jsonArray || jsonArray.isEmpty())
				statusCode = unvDeviceUtil.create(myurl, myjson);
			else
				statusCode = unvDeviceUtil.edit(myurl, myjson);
		} else {
			JSONArray jsonArray = unvDeviceUtil.query(actionDetails.getDevice().getIpAddress(), employee.getEmpId());
			if (null == jsonArray || jsonArray.isEmpty())
				statusCode = unvDeviceUtil.create(myurl, myjson);
			else
				statusCode = unvDeviceUtil.edit(myurl, myjson);
		}
		return statusCode;
	}

	@Override
	public void saveAsArea(Employee employee, Principal principal) {

		try {
			Employee empObj = employeeService.getById(employee.getId());

			List<Area> newAreaList = employee.getArea();
			List<Area> oldAreaList = empObj.getArea();

			addAreaToEmployee(employee, principal, newAreaList, oldAreaList);

			removeEmployeeFromArea(employee, principal, newAreaList, oldAreaList);

			empObj.setArea(newAreaList);
			employeeRepository.save(empObj);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void removeEmployeeFromArea(Employee employee, Principal principal, List<Area> newAreaList,
			List<Area> oldAreaList) {
		for (Area area : oldAreaList) {

			if (!newAreaList.contains(area)) {
				List<Area> areaList = new ArrayList<Area>();
				areaList.add(area);
				List<Device> deviceList = deviceRepository.findByAreaAndIsDeletedFalseCustom(areaList);
				Device device = deviceList.get(NumberConstants.ZERO);
				actionService.employeeDeviceAction(device, employee, ApplicationConstants.DELETE, ApplicationConstants.ACCESS_TYPE_APP, ApplicationConstants.DELIMITER_EMPTY, principal);
			}
		}
	}

	private void addAreaToEmployee(Employee employee, Principal principal, List<Area> newAreaList,
			List<Area> oldAreaList) {
		for (Area area : newAreaList) {

			if (!oldAreaList.contains(area)) {
				List<Area> areaList = new ArrayList<Area>();
				areaList.add(area);
				List<Device> deviceList = deviceRepository.findByAreaAndIsDeletedFalseCustom(areaList);
				Device device = deviceList.get(NumberConstants.ZERO);
				actionService.employeeDeviceAction(device, employee, ApplicationConstants.CREATE, ApplicationConstants.ACCESS_TYPE_APP, ApplicationConstants.DELIMITER_EMPTY, principal);
			}
		}
	}

	@Override
	public Long editEmployeeInDevice(ActionDetails actionDetails) {
		return null;
	}

	@Override
	public Long deleteAllEmployeeFromDevice(Device device) {
		return null;
	}

	@Override
	public Long queryFromDevice(Device device, Employee employee) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getTransactionByDate(Device device, Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
