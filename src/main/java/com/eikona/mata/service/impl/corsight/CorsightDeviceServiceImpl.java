package com.eikona.mata.service.impl.corsight;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.CorsightDeviceConstants;
import com.eikona.mata.constants.MessageConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.entity.Action;
import com.eikona.mata.entity.ActionDetails;
import com.eikona.mata.entity.Area;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.Image;
import com.eikona.mata.entity.Transaction;
import com.eikona.mata.repository.DeviceRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.repository.ImageRepository;
import com.eikona.mata.repository.TransactionRepository;
import com.eikona.mata.service.DeviceSyncAbstractService;
import com.eikona.mata.service.EmployeeService;
import com.eikona.mata.util.ActionAndActionDetailUtil;
import com.eikona.mata.util.CalendarUtil;
import com.eikona.mata.util.CorsightDeviceUtil;
import com.eikona.mata.util.RequestExecutionUtil;
import com.eikona.mata.util.SavingCropImageUtil;

@Service
@Qualifier(CorsightDeviceConstants.SERVICE)
public class CorsightDeviceServiceImpl implements DeviceSyncAbstractService<String> {


	@Autowired
	private CorsightDeviceUtil corsightDeviceUtil;

	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private EmployeeService employeeService;


	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private ActionAndActionDetailUtil actionAndActionDetailUtil;

	@Autowired
	private DeviceRepository deviceRepository;
	
	@Autowired
	private SavingCropImageUtil savingCropImageUtil;
	
	@Autowired
	private CalendarUtil calendarUtil; 
	
	@Autowired
	private RequestExecutionUtil requestExecutionUtil;
	
	@Value("${corsight.host.url}")
    private String corsightHost;
	
	@Value("${corsight.poi.port}")
    private String poiPort;
	
	@Value("${corsight.camera.port}")
    private String cameraPort;
	
	@Value("${corsight.history.port}")
    private String historyPort;

	@Override
	public String deviceBasicInfo(String cameraId) {
		String msg = null;
		try {
			String deviceUrl = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH + corsightHost + ApplicationConstants.DELIMITER_COLON + cameraPort;
			String addDeviceUrl = CorsightDeviceConstants.CAMERA_API + cameraId;
 
			String responeData = requestExecutionUtil.executeHttpsGetRequest(deviceUrl, addDeviceUrl);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
			JSONObject jsonMetaData = (JSONObject) jsonResponse.get(CorsightDeviceConstants.METADATA);
			msg = (String) jsonMetaData.get(CorsightDeviceConstants.MSG);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}

	
	@Override
	public String deleteSingleEmployeeFromDevice(ActionDetails actionDetails) {
		boolean msg = false;
		String message = null;
		try {
			JSONObject requestObj = getPoiDeleteRequest(actionDetails);

			String poiUrl = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH + corsightHost + ApplicationConstants.DELIMITER_COLON + poiPort;
			String addPoiUrl = CorsightDeviceConstants.POI_API_DELETE_PERSON;

			String responeData = requestExecutionUtil.executeHttpsPostRequest(requestObj,poiUrl, addPoiUrl);

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
			JSONObject jsonMetaData = (JSONObject) jsonResponse.get(CorsightDeviceConstants.METADATA);
			JSONArray jsonMessageArray = (JSONArray) jsonMetaData.get(CorsightDeviceConstants.SUCCESS_LIST);
			for (int j = NumberConstants.ZERO; j < jsonMessageArray.size(); j++) {
				JSONObject currobj = (JSONObject) jsonMessageArray.get(j);
				msg = (boolean) currobj.get(CorsightDeviceConstants.MSG);
			}
			message = String.valueOf(msg);
			return message;
			
		} catch (Exception e) {
			e.printStackTrace();
			return message;
		}
		
	}
	@SuppressWarnings(ApplicationConstants.UNCHECKED)
	private JSONObject getPoiDeleteRequest(ActionDetails actionDetails) {
		JSONObject requestObj = new JSONObject();
		JSONArray poiArray = new JSONArray();

		Employee employee = actionDetails.getAction().getEmployee();

		poiArray.add(employee.getPoi());

		requestObj.put(CorsightDeviceConstants.POIS, poiArray);
		return requestObj;
	}

	@Override
	public String deleteAllEmployeeFromDevice(Device device) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public String addEmployeeToDevice(ActionDetails actionDetails) {

		String pioId = null;
		try {
			JSONObject requestObj = getAddPoiRequest(actionDetails);

			String poiUrl = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH + corsightHost + ApplicationConstants.DELIMITER_COLON + poiPort;
			String addPoiUrl =CorsightDeviceConstants.POI_API_ADD_PERSON;

			String responeData = requestExecutionUtil.executeHttpsPostRequest(requestObj, poiUrl, addPoiUrl);

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
			JSONObject jsonResponseData = (JSONObject) jsonResponse.get(CorsightDeviceConstants.DATA);
			JSONArray jsonArray = (JSONArray) jsonResponseData.get(CorsightDeviceConstants.POIS);
			System.out.println(jsonArray);

			for (int i = NumberConstants.ZERO; i < jsonArray.size(); i++) {
				JSONObject currobj = (JSONObject) jsonArray.get(i);
				pioId = (String) currobj.get(CorsightDeviceConstants.POI_ID);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pioId;

	}

	@SuppressWarnings(ApplicationConstants.UNCHECKED)
	private JSONObject getAddPoiRequest(ActionDetails actionDetails) throws IOException {
		JSONObject requestObj = new JSONObject();
		JSONArray poiArray = new JSONArray();

		JSONObject poiObj = new JSONObject();
		JSONObject faceObj = new JSONObject();
		JSONObject imgPayloadObj = new JSONObject();
		Employee employee = actionDetails.getAction().getEmployee();
		
		String encodedImage = getEncodedImageFromEmployee(employee);

		poiObj.put(CorsightDeviceConstants.DISPLAY_IMG, encodedImage);
		poiObj.put(CorsightDeviceConstants.DISPLAY_NAME, employee.getEmpId());
		
		faceObj.put(CorsightDeviceConstants.FORCE, false);
		faceObj.put(CorsightDeviceConstants.SAVE_CROP, true);
		
		imgPayloadObj.put(CorsightDeviceConstants.IMG, encodedImage);
		faceObj.put(CorsightDeviceConstants.IMAGE_PAYLOAD,imgPayloadObj);
		poiObj.put(CorsightDeviceConstants.FACE, faceObj);
		poiArray.add(poiObj);

		requestObj.put(CorsightDeviceConstants.POIS, poiArray);
		return requestObj;
	}


	private String getEncodedImageFromEmployee(Employee employee) throws IOException {
		List<Image> imageList = imageRepository.findByEmployee(employee);
		Image image = imageList.get(NumberConstants.ZERO);
		String imagePath = image.getOriginalPath();

		BufferedImage resizeimage = ImageIO.read(new File(imagePath));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(resizeimage, ApplicationConstants.FORMAT_JPG, baos);
		byte[] bytes = baos.toByteArray();
		String encodedImage = Base64.encodeBase64String(bytes);
		return encodedImage;
	}

	
	@Override
	public String editEmployeeInDevice(ActionDetails actionDetails) {

		String msg = null;
		try {

			Employee employee = actionDetails.getAction().getEmployee();

			String encodedImage = getEncodedImageFromEmployee(employee);
			
			JSONObject requestObj = getPoiEditRequest(employee, encodedImage);

			String poiUrl = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH + corsightHost + ApplicationConstants.DELIMITER_COLON + poiPort;
			String addPoiUrl = CorsightDeviceConstants.POI_API_ADD_PERSON + employee.getPoi() + ApplicationConstants.DELIMITER_FORWARD_SLASH;

			String responeData = requestExecutionUtil.executeHttpsPostRequest(requestObj, poiUrl, addPoiUrl);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);

			JSONObject jsonMetaData = (JSONObject) jsonResponse.get(CorsightDeviceConstants.METADATA);

			msg = (String) jsonMetaData.get(CorsightDeviceConstants.MSG);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}

	@SuppressWarnings(ApplicationConstants.UNCHECKED)
	private JSONObject getPoiEditRequest(Employee employee, String encodedImage) {
		JSONObject requestObj = new JSONObject();

		requestObj.put(CorsightDeviceConstants.DISPLAY_IMG, encodedImage);
		requestObj.put(CorsightDeviceConstants.DISPLAY_NAME, employee.getEmpId());
		return requestObj;
	}

	
	@Override
	public String queryFromDevice(Device device, Employee employee) {

		String poiId=null;
		try {
			JSONObject requestObj = getPoiSearchRequestObject(employee);

			String poiUrl = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH + corsightHost + ApplicationConstants.DELIMITER_COLON + poiPort;
			String addPoiUrl = CorsightDeviceConstants.POI_API_SEARCH_PERSON;

			String responeData = requestExecutionUtil.executeHttpsPostRequest(requestObj, poiUrl, addPoiUrl);

			System.out.println(responeData);

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
			JSONObject jsonMetaData = (JSONObject) jsonResponse.get(CorsightDeviceConstants.DATA);
			JSONArray jsonMessageArray = (JSONArray) jsonMetaData.get(CorsightDeviceConstants.POIS);
			JSONObject jsonPoi=(JSONObject) jsonMessageArray.get(NumberConstants.ZERO);
			 poiId=(String) jsonPoi.get(CorsightDeviceConstants.POI_ID);
			 return poiId;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	@SuppressWarnings(ApplicationConstants.UNCHECKED)
	private JSONObject getPoiSearchRequestObject(Employee employee) {
		JSONObject requestObj = new JSONObject();

		requestObj.put(CorsightDeviceConstants.DISPLAY_NAME, employee.getEmpId());
		requestObj.put(CorsightDeviceConstants.MAX_MATCHES, NumberConstants.ONE);
		return requestObj;
	}

	@Override
	public String getTransactionByDate(Device device, Date sDate, Date eDate) {
		try {
			String afterId = ApplicationConstants.DELIMITER_EMPTY;
			syncCorsightHistory(afterId,device,sDate,eDate);
			return MessageConstants.TRANSACTION_SUCCESS;
		}catch (Exception e) {
			return MessageConstants.TRANSACTION_FAILED;
		}
		
		
	}
	
	
	private void syncCorsightHistory(String afterId,Device device,Date sDateStr, Date eDateStr) {
		try {
			JSONObject requestObj = getHistorySyncRequestObject(device, sDateStr, eDateStr);

			String historyUrl = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH + corsightHost + ApplicationConstants.DELIMITER_COLON + historyPort;
			String addHistoryUrl = null;
			if (afterId.isEmpty())
				addHistoryUrl = CorsightDeviceConstants.HISTORY_API_SYNC;
			else
				addHistoryUrl = CorsightDeviceConstants.HISTORY_API_SYNC_AFTER_ID + afterId;

			JSONArray matchArray = getHttpsPOSTArray(requestObj, historyUrl, addHistoryUrl);
			
			List<Transaction> eventList = new ArrayList<>();
			for (int i = NumberConstants.ZERO; i < matchArray.size(); i++) {
				JSONObject currobj = (JSONObject) matchArray.get(i);
				JSONObject appearanceDataObject = (JSONObject) currobj.get(CorsightDeviceConstants.APPEARANCE_DATA);
				
				DateFormat dateFormat = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
				DateFormat timeFormat = new SimpleDateFormat(ApplicationConstants.TIME_FORMAT_24HR);
				Timestamp ts = new Timestamp((long) ((double) appearanceDataObject.get(CorsightDeviceConstants.UTC_TIME_STARTED)));
				Date punchDate = new Date(ts.getTime() * NumberConstants.THOUSAND);
				
				JSONObject matchDataObject = (JSONObject) currobj.get(CorsightDeviceConstants.MATCH_DATA);
				String empId=(String) matchDataObject.get(CorsightDeviceConstants.POI_DISPLAY_NAME);
				
				
				Transaction eventReport = transactionRepository.findByEmpIdAndPunchDate(empId,punchDate);
				if (eventReport == null) {
					Transaction event = new Transaction();

					event.setAppearanceId((String) appearanceDataObject.get(CorsightDeviceConstants.APPEARANCE_ID));

					setTransactionDate(dateFormat, timeFormat, punchDate, event);

					JSONObject cameraDataObject = (JSONObject) currobj.get(CorsightDeviceConstants.CAMERA_DATA);
					event.setDeviceId((String) cameraDataObject.get(CorsightDeviceConstants.CAMERA_ID));
					event.setDeviceName((String) cameraDataObject.get(CorsightDeviceConstants.CAMERA_DESCRIPTION));

					setWatchlistDetails(matchDataObject, event);
					
					setApperanceData(currobj, matchDataObject, event);
					
					eventList.add(event);
					
			}

				}
			
			transactionRepository.saveAll(eventList);
			if (!eventList.isEmpty()) {
				afterId = eventList.get(eventList.size() - 1).getAppearanceId();

				syncCorsightHistory(afterId,device,sDateStr,eDateStr);

			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JSONArray getHttpsPOSTArray(JSONObject requestObj, String historyUrl,
			String addHistoryUrl) throws ParseException, KeyManagementException, NoSuchAlgorithmException, IOException {
		
		String responeData = requestExecutionUtil.executeHttpsPostRequest(requestObj, historyUrl, addHistoryUrl);

		JSONParser jsonParser = new JSONParser();
		JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
		JSONObject dataObject = (JSONObject) jsonResponse.get(CorsightDeviceConstants.DATA);
		JSONArray jsonArray = (JSONArray) dataObject.get(CorsightDeviceConstants.MATCHES);
		return jsonArray;
	}
	@SuppressWarnings(ApplicationConstants.UNCHECKED)
	private JSONObject getHistorySyncRequestObject(Device device, Date sDate, Date eDate) {
		JSONObject requestObj = new JSONObject();
		try {
			JSONArray cameraArray = new JSONArray();
			JSONArray watchListArray = new JSONArray();

			JSONObject matchedObj = new JSONObject();
			
			Calendar fromCal = calendarUtil.dateToCalendar(sDate);

			Calendar toCal = calendarUtil.dateToCalendar(eDate);

			long from = fromCal.getTimeInMillis() / NumberConstants.THOUSAND;
			long to = toCal.getTimeInMillis() / NumberConstants.THOUSAND;

			cameraArray.add(device.getCameraId());

			matchedObj.put(CorsightDeviceConstants.MATCH_OUTCOME, CorsightDeviceConstants.MATCHED);
			matchedObj.put(CorsightDeviceConstants.WATCHLIST_ID, device.getArea().getWatchlistId());

			watchListArray.add(matchedObj);

			requestObj.put(CorsightDeviceConstants.CAMERAS, cameraArray);
			requestObj.put(CorsightDeviceConstants.FROM, from);
			requestObj.put(CorsightDeviceConstants.TILL, to);
			requestObj.put(CorsightDeviceConstants.WATCHLISTS, watchListArray);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return requestObj;
		
	}

	private void setApperanceData(JSONObject currobj, JSONObject matchDataObject, Transaction event) {
		
		
		setEmployeeDetails(matchDataObject, event);

		setTransactionImage(currobj, event);
		
		if (null != matchDataObject.get(CorsightDeviceConstants.POI_ID)) {
			event.setPoiId((String) matchDataObject.get(CorsightDeviceConstants.POI_ID));
		}
		
		event.setPoiConfidence((double) matchDataObject.get(CorsightDeviceConstants.POI_CONFIDENCE));
		
		event.setEventId((String) currobj.get(CorsightDeviceConstants.EVENT_ID));

		JSONObject faceDataObject = (JSONObject) currobj.get(CorsightDeviceConstants.FACE_FEATURE_DATA);

		event.setMaskStatus((String) faceDataObject.get(CorsightDeviceConstants.MASK_OUTCOME));
		event.setGender((String) faceDataObject.get(CorsightDeviceConstants.GENDER_OUTCOME));
		event.setAge((String) faceDataObject.get(CorsightDeviceConstants.AGE_OUTCOME));

		if (CorsightDeviceConstants.NOT_MASKED.equalsIgnoreCase(event.getMaskStatus()))
			event.setWearingMask(true);
		else
			event.setWearingMask(false);
	}

	private void setTransactionImage(JSONObject currobj, Transaction event) {
		JSONObject cropDataObject = (JSONObject) currobj.get(CorsightDeviceConstants.CROP_DATA);

		if (null != cropDataObject) {
			String imagePath = savingCropImageUtil.saveCropImages((String) cropDataObject.get(CorsightDeviceConstants.FACE_CROP_IMG), event);
			event.setCropimagePath(imagePath);
		}
	}

	private void setEmployeeDetails(JSONObject matchDataObject, Transaction event) {
		if (null != matchDataObject.get(CorsightDeviceConstants.POI_DISPLAY_NAME)) {
			Employee employee = employeeRepository.findByEmpIdAndIsDeletedFalse((String) matchDataObject.get(CorsightDeviceConstants.POI_DISPLAY_NAME));

			if (null != employee) {
				event.setName(employee.getName());	
				if (null != employee.getDepartment())
					event.setDepartment(employee.getDepartment().getName());

				if (null != employee.getDesignation())
					event.setDesignation(employee.getDesignation().getName());

				if (null != employee.getBranch())
					event.setBranch(employee.getBranch().getName());
			}
			event.setEmpId((String) matchDataObject.get(CorsightDeviceConstants.POI_DISPLAY_NAME));
		}
	}

	private void setWatchlistDetails(JSONObject matchDataObject, Transaction event) {
		JSONArray watchArray = (JSONArray) matchDataObject.get(CorsightDeviceConstants.WATCHLISTS);
		for (int j = NumberConstants.ZERO; j < watchArray.size(); j++) {
			JSONObject watchobj = (JSONObject) watchArray.get(j);
			event.setWatchlistId((String) watchobj.get(CorsightDeviceConstants.WATCHLIST_ID));
			event.setWatchlistName((String) watchobj.get(CorsightDeviceConstants.DISPLAY_NAME));
		}
	}

	private void setTransactionDate(DateFormat dateFormat, DateFormat timeFormat, Date punchDate, Transaction event) {
		String dateString = dateFormat.format(punchDate);
		event.setPunchDateStr(dateString);
		event.setPunchDate(punchDate);
		event.setPunchTimeStr(timeFormat.format(punchDate));
	}

	@Override
	public void saveAsArea(Employee employee, Principal principal) {
		try {
			Action action = actionAndActionDetailUtil.saveAction(employee, principal, ApplicationConstants.DELIMITER_EMPTY, 0l);

			Employee empObj = employeeService.getById(employee.getId());

			List<Area> newAreaList = employee.getArea();
			List<Area> oldAreaList = empObj.getArea();

			empObj.setArea(newAreaList);

			String status = ApplicationConstants.NEW;
			for (Area area : newAreaList) {
				if (!oldAreaList.contains(area)) {
					List<Area> areaList = new ArrayList<Area>();
					areaList.add(area);
					List<Device> deviceList = deviceRepository.findByAreaAndIsDeletedFalseCustom(areaList);
					Device device = deviceList.get(NumberConstants.ZERO);
					String presentPoi = queryFromDevice(device, empObj);
					if (ApplicationConstants.FALSE.equalsIgnoreCase(presentPoi)) {//null == empObj.getPoi() &&
						ActionDetails actionDetails = new ActionDetails();
						Action actionObj = new Action();
						actionObj.setEmployee(empObj);
						actionDetails.setAction(actionObj);
						String poi = addEmployeeToDevice(actionDetails);
						empObj.setPoi(poi);
					}
					String msg = corsightDeviceUtil.addPoiToWatchList(area, empObj);

					if (!ApplicationConstants.ERROR.equalsIgnoreCase(status)) {
						if (ApplicationConstants.SUCCESS.equalsIgnoreCase(msg)) {
							status = ApplicationConstants.COMPLETED;
						} else {
							status = ApplicationConstants.ERROR;

						}
					}
					actionAndActionDetailUtil.saveActionDetail(action, device, msg, msg);
				}
			}

			for (Area area : oldAreaList) {
				if (!newAreaList.contains(area)) {
					// add
					List<Area> areaList = new ArrayList<Area>();
					areaList.add(area);
					List<Device> deviceList = deviceRepository.findByAreaAndIsDeletedFalseCustom(areaList);
					Device device = deviceList.get(NumberConstants.ZERO);
					if (CorsightDeviceConstants.MODEL_TYPE.equalsIgnoreCase(device.getModel())) {
						String msg = corsightDeviceUtil.removePoiFromWatchList(area, empObj);

						if (!ApplicationConstants.ERROR.equalsIgnoreCase(status)) {
							if (ApplicationConstants.SUCCESS.equalsIgnoreCase(msg)) {
								status = ApplicationConstants.COMPLETED;
							} else {
								status = ApplicationConstants.ERROR;
							}
						}
						actionAndActionDetailUtil.saveActionDetail(action, device, msg, msg);
					}
				}
			}

			empObj.setArea(newAreaList);
			employeeRepository.save(empObj);

			actionAndActionDetailUtil.saveAction(employee, principal, status, action.getId());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void pullEmployeeFromDeviceToMata(Device device) {
		// TODO Auto-generated method stub

	}
}
