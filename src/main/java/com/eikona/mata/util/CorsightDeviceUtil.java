package com.eikona.mata.util;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.CorsightDeviceConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.entity.Action;
import com.eikona.mata.entity.ActionDetails;
import com.eikona.mata.entity.Area;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.repository.AreaRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.service.DeviceSyncAbstractService;

@Component
public class CorsightDeviceUtil {
	
	@Value("${corsight.host.url}")
	private String host;
	
	@Value("${corsight.poi.port}")
	private String portPoi;
	
	@Value("${corsight.history.port}")
	private String portHistory;
	
	@Value("${corsight.user.port}")
	private String portUser;
	
	@Value("${corsight.controller.port}")
	private String portController;
	
	@Value("${corsight.camera.port}")
	private String portCamera;
	

	@Autowired
	private AreaRepository areaDatatableRepository;

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private RequestExecutionUtil requestExecutionUtil;

	@Autowired
	@Qualifier(CorsightDeviceConstants.SERVICE)
	private DeviceSyncAbstractService<String> corsightSyncService;

	public String corsightServerBasicInfo() {
		String msg = null;
		try {
			String httpsUrl = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH + host + ApplicationConstants.DELIMITER_COLON+ portController;
			String getUrl =  CorsightDeviceConstants.SERVER_STATUS_API;

			String responeData = requestExecutionUtil.executeHttpsGetRequest(httpsUrl, getUrl);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
			JSONObject jsonMetaData = (JSONObject) jsonResponse.get(CorsightDeviceConstants.METADATA);
			msg = (String) jsonMetaData.get(CorsightDeviceConstants.MSG);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}

	public String createCamera(Device entity) {
		try {
			String cameraId = null;
			
			JSONObject requestObj = getJsonObjectForAddCamera(entity);

			String url = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH + host +ApplicationConstants.DELIMITER_COLON+ portCamera;
			String subUrl = CorsightDeviceConstants.CAMERA_API;


			String responeData = requestExecutionUtil.executeHttpsPostRequest(requestObj,url,subUrl);

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
			JSONObject jsonResponseData = (JSONObject) jsonResponse.get(CorsightDeviceConstants.DATA);
			cameraId = (String) jsonResponseData.get(CorsightDeviceConstants.CAMERA_ID);
			return cameraId;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings(ApplicationConstants.UNCHECKED)
	private JSONObject getJsonObjectForAddCamera(Device entity) {
		JSONObject requestObj = new JSONObject();
		JSONArray watchListArray = new JSONArray();
		

		JSONObject configObj = new JSONObject();
		JSONObject captureConfigObj = new JSONObject();

		JSONObject watchListObj = new JSONObject();
		watchListObj.put(CorsightDeviceConstants.WATCHLIST_ID, entity.getArea().getWatchlistId());
		watchListArray.add(watchListObj);

		captureConfigObj.put(CorsightDeviceConstants.MODE, CorsightDeviceConstants.VIDEO);
		captureConfigObj.put(CorsightDeviceConstants.MIN_DETECTION_WIDTH, NumberConstants.THIRTY);
		captureConfigObj.put(CorsightDeviceConstants.CAPTURE_ADDRESS, entity.getUserName());
		
		configObj.put(CorsightDeviceConstants.FACE_RECOGNITION_THRESHOLD, NumberConstants.SIXTY);
		configObj.put(CorsightDeviceConstants.WATCHLISTS, watchListArray);

		
		requestObj.put(CorsightDeviceConstants.DESCRIPTION, entity.getName());
		requestObj.put(CorsightDeviceConstants.CAPTURE_CONFIG, captureConfigObj);
		requestObj.put(CorsightDeviceConstants.CONFIG, configObj);
		
		return requestObj;
	}

	public String editCamera(Device entity) {
		String cameraId = null;
		try {

			JSONObject requestObj = getJsonObjectForEditCamera(entity);

			String httpsUrl = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH + host + ApplicationConstants.DELIMITER_COLON + portCamera;
			String putUrl = CorsightDeviceConstants.CAMERA_API + entity.getCameraId() + ApplicationConstants.DELIMITER_FORWARD_SLASH;

			String responeData = requestExecutionUtil.executeHttpsPutRequest(requestObj, httpsUrl, putUrl);

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
			JSONObject jsonResponseData = (JSONObject) jsonResponse.get(CorsightDeviceConstants.DATA);
			cameraId = (String) jsonResponseData.get(CorsightDeviceConstants.CAMERA_ID);
			return cameraId;
		} catch (Exception e) {
			e.printStackTrace();
			return cameraId;
		}
	}

	@SuppressWarnings(ApplicationConstants.UNCHECKED)
	private JSONObject getJsonObjectForEditCamera(Device entity) {
		JSONObject requestObj = new JSONObject();
		JSONArray watchListArray = new JSONArray();
		

		JSONObject configObj = new JSONObject();
		JSONObject captureConfigObj = new JSONObject();

		JSONObject watchListObj = new JSONObject();
		watchListObj.put(CorsightDeviceConstants.WATCHLIST_ID, entity.getArea().getWatchlistId());
		watchListArray.add(watchListObj);

		captureConfigObj.put(CorsightDeviceConstants.MODE, CorsightDeviceConstants.VIDEO);
		captureConfigObj.put(CorsightDeviceConstants.MIN_DETECTION_WIDTH, NumberConstants.THIRTY);
		captureConfigObj.put(CorsightDeviceConstants.CAPTURE_ADDRESS, entity.getUserName());
		
		configObj.put(CorsightDeviceConstants.FACE_RECOGNITION_THRESHOLD, NumberConstants.SIXTY);
		configObj.put(CorsightDeviceConstants.WATCHLISTS, watchListArray);

		
		requestObj.put(CorsightDeviceConstants.DESCRIPTION, entity.getName());
		requestObj.put(CorsightDeviceConstants.CAPTURE_CONFIG, captureConfigObj);
		requestObj.put(CorsightDeviceConstants.CONFIG, configObj);
		return requestObj;
	}

	public String removeCamera(Device entity) {
		String responeData = null;
		try {
			String httpsUrl = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH + host + ApplicationConstants.DELIMITER_COLON + portCamera;
			String deleteUrl = CorsightDeviceConstants.CAMERA_API + entity.getCameraId() + ApplicationConstants.DELIMITER_FORWARD_SLASH;

			responeData = requestExecutionUtil.executeHttpsDeleteRequest(httpsUrl, deleteUrl);
			return responeData;
		} catch (Exception e) {
			e.printStackTrace();
			return responeData;
		}

	}

	public String removeWatchList(Area entity) {
		String responeData = null;
		try {

			String httpsUrl = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH + host + ApplicationConstants.DELIMITER_COLON + portPoi;
			String deleteUrl = CorsightDeviceConstants.POI_API_GET_WATCHLIST + entity.getWatchlistId() + ApplicationConstants.DELIMITER_FORWARD_SLASH;

			responeData = requestExecutionUtil.executeHttpsDeleteRequest(httpsUrl, deleteUrl);

			return responeData;

		} catch (Exception e) {
			e.printStackTrace();
			return responeData;
		}
	}

	public String createAndEditWatchList(Area entity, String flag) {
		String watchListId = null;
		try {

			JSONObject requestObj = getJsonObjectToCreateAndEditWatchList(entity);

			String httpsUrl = null;
			String postUrl = null;
			if (flag.equalsIgnoreCase(ApplicationConstants.UPDATE)) {
				httpsUrl = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH + host + ApplicationConstants.DELIMITER_COLON + portPoi;
				postUrl = CorsightDeviceConstants.POI_API_GET_WATCHLIST + entity.getWatchlistId() + ApplicationConstants.DELIMITER_FORWARD_SLASH;
			} else {
				httpsUrl = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH + host + ApplicationConstants.DELIMITER_COLON + portPoi;
				postUrl = CorsightDeviceConstants.POI_API_GET_WATCHLIST;
			}

			String responeData = requestExecutionUtil.executeHttpsPostRequest(requestObj, httpsUrl, postUrl);

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
			JSONObject jsonResponseData = (JSONObject) jsonResponse.get(CorsightDeviceConstants.DATA);
			watchListId = (String) jsonResponseData.get(CorsightDeviceConstants.WATCHLIST_ID);

			return watchListId;

		} catch (Exception e) {
			e.printStackTrace();
			return watchListId;
		}

	}

	@SuppressWarnings(ApplicationConstants.UNCHECKED)
	private JSONObject getJsonObjectToCreateAndEditWatchList(Area entity) {
		JSONObject requestObj = new JSONObject();
		JSONObject watchListObj = new JSONObject();

		watchListObj.put(CorsightDeviceConstants.DISPLAY_COLOR, CorsightDeviceConstants.DISPLAY_COLOR_BLUE);
		watchListObj.put(CorsightDeviceConstants.DISPLAY_NAME, entity.getName());
		watchListObj.put(CorsightDeviceConstants.THRESHOLD_DELTA, NumberConstants.ZERO);
		watchListObj.put(CorsightDeviceConstants.WATCHLIST_TYPE, entity.getWatchlist());
		return requestObj;
	}

	public String queryDevice(String cameraId) {
		try {

			String httpsUrl = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH + host + ApplicationConstants.DELIMITER_COLON + portCamera;
			String getUrl = CorsightDeviceConstants.CAMERA_API + cameraId + ApplicationConstants.DELIMITER_FORWARD_SLASH;

			String responeData = requestExecutionUtil.executeHttpsGetRequest(httpsUrl, getUrl);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
			JSONObject jsonResponseData = (JSONObject) jsonResponse.get(CorsightDeviceConstants.METADATA);
			String msg = (String) jsonResponseData.get(CorsightDeviceConstants.MSG);
			return msg;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String queryWatchlist(String watchlistId) {
		try {

			String httpsUrl = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH + host + ApplicationConstants.DELIMITER_COLON + portPoi;
			String getUrl = CorsightDeviceConstants.POI_API_GET_WATCHLIST + watchlistId + ApplicationConstants.DELIMITER_FORWARD_SLASH;

			String responeData = requestExecutionUtil.executeHttpsGetRequest(httpsUrl, getUrl);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
			JSONObject jsonResponseData = (JSONObject) jsonResponse.get(CorsightDeviceConstants.METADATA);
			String msg = (String) jsonResponseData.get(CorsightDeviceConstants.MSG);
			return msg;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String addPoiToWatchList(Area entity, Employee employee) {
		try {

			JSONObject requestObj = getJsonObjectToAddPoiToWatchList(entity, employee);

			String httpsUrl = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH + host + ApplicationConstants.DELIMITER_COLON + portPoi;
			String postUrl = CorsightDeviceConstants.POI_API_WATCHLIST_ADD;

			String responeData = requestExecutionUtil.executeHttpsPostRequest(requestObj, httpsUrl, postUrl);
			System.out.println(responeData);
			return ApplicationConstants.COMPLETED;

		} catch (Exception e) {
			e.printStackTrace();
			return CorsightDeviceConstants.EMP_NOT_ADDED_TO_WATCHLIST;
		}
	}

	@SuppressWarnings(ApplicationConstants.UNCHECKED)
	private JSONObject getJsonObjectToAddPoiToWatchList(Area entity, Employee employee) {
		JSONObject requestObj = new JSONObject();
		JSONArray poiArray = new JSONArray();

		poiArray.add( employee.getPoi());

		requestObj.put(CorsightDeviceConstants.POIS, poiArray);
		requestObj.put(CorsightDeviceConstants.WATCHLIST_ID, entity.getWatchlistId());
		return requestObj;
	}

	public String removePoiFromWatchList(Area area, Employee employee) {
		try {

			JSONObject requestObj = getJsonObjectForRemovePoiFromWatchList(area, employee);

			String httpsUrl = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH + host + ApplicationConstants.DELIMITER_COLON + portPoi;
			String postUrl = CorsightDeviceConstants.POI_API_WATCHLIST_REMOVE;

			String responeData = requestExecutionUtil.executeHttpsPostRequest(requestObj, httpsUrl, postUrl);
			System.out.println(responeData);
			return ApplicationConstants.COMPLETED;
		} catch (Exception e) {
			e.printStackTrace();
			return CorsightDeviceConstants.EMP_NOT_REMOVE_FROM_WATCHLIST;
		}

	}

	@SuppressWarnings(ApplicationConstants.UNCHECKED)
	private JSONObject getJsonObjectForRemovePoiFromWatchList(Area area, Employee employee) {
		JSONObject requestObj = new JSONObject();
		JSONArray poiArray = new JSONArray();

		poiArray.add(employee.getPoi());

		requestObj.put(CorsightDeviceConstants.POIS, poiArray);
		requestObj.put(CorsightDeviceConstants.WATCHLIST_ID, area.getWatchlistId());
		return requestObj;
	}

	public List<String> getWatchlistFromPoiDetails(String poi) {
		try {

			JSONObject requestObj = getJsonObjectToGetWAtchListFromPoiDetails(poi);

			String httpsUrl = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH + host + ApplicationConstants.DELIMITER_COLON + portPoi;
			String postUrl = CorsightDeviceConstants.POI_API_GET;

			String responeData = requestExecutionUtil.executeHttpsPostRequest(requestObj, httpsUrl, postUrl);
			
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
			JSONObject jsonResponseData = (JSONObject) jsonResponse.get(CorsightDeviceConstants.DATA);
			JSONArray jsonpoisArray = (JSONArray) jsonResponseData.get(CorsightDeviceConstants.POIS);
			List<String> watchlistlist = new ArrayList<>();

			for (int i = NumberConstants.ZERO; i < jsonpoisArray.size(); i++) {
				JSONObject employeeObj = (JSONObject) jsonpoisArray.get(i);

				JSONArray watchlistArray = (JSONArray) employeeObj.get(CorsightDeviceConstants.POI_WATCHLISTS);
				for (int j = NumberConstants.ZERO; j < watchlistArray.size(); j++) {
					String watchlist = (String) watchlistArray.get(j);
					watchlistlist.add(watchlist);

				}
			}
			return watchlistlist;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@SuppressWarnings(ApplicationConstants.UNCHECKED)
	private JSONObject getJsonObjectToGetWAtchListFromPoiDetails(String poi) {
		JSONObject requestObj = new JSONObject();
		JSONArray poiArray = new JSONArray();

		poiArray.add(poi);

		requestObj.put(CorsightDeviceConstants.POIS, poiArray);
		return requestObj;
	}

	public void addEmployeeToCorsightDevice(Device device) {

		if (null == device.getArea().getWatchlistId()) {
			String watchlistId = createAndEditWatchList(device.getArea(), ApplicationConstants.CREATE);
			device.getArea().setWatchlistId(watchlistId);
			areaDatatableRepository.save(device.getArea());
		}

		List<Employee> employeeList = employeeRepository.findByArea(device.getArea());
		for (Employee employee : employeeList) {
			if (null == employee.getPoi()) {
				ActionDetails actionDetails = new ActionDetails();
				Action action = new Action();
				action.setEmployee(employee);
				actionDetails.setAction(action);
				String poi = corsightSyncService.addEmployeeToDevice(actionDetails);
				employee.setPoi(poi);

				employeeRepository.save(employee);
			}
			addPoiToWatchList(device.getArea(), employee);
		}
	}

	public List<String> getWatchlistListFromCamera(String cameraId) {
		try {

			String httpsUrl = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH + host + ApplicationConstants.DELIMITER_COLON + portCamera;
			String getUrl = CorsightDeviceConstants.CAMERA_API + cameraId + ApplicationConstants.DELIMITER_FORWARD_SLASH;


			String responeData = requestExecutionUtil.executeHttpsGetRequest(httpsUrl, getUrl);
			
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
			JSONObject jsonResponseData = (JSONObject) jsonResponse.get(CorsightDeviceConstants.DATA);
			JSONObject jsonConfigData = (JSONObject) jsonResponseData.get(CorsightDeviceConstants.CONFIG);
			JSONArray jsonWatchlistArray = (JSONArray) jsonConfigData.get(CorsightDeviceConstants.WATCHLISTS);

			List<String> watchlistList = new ArrayList<>();
			for (int i = 0; i < jsonWatchlistArray.size(); i++) {
				JSONObject watchlistInfoObj = (JSONObject) jsonWatchlistArray.get(i);
				String watchlist = (String) watchlistInfoObj.get(CorsightDeviceConstants.WATCHLIST_ID);
				watchlistList.add(watchlist);

			}

			return watchlistList;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings(ApplicationConstants.UNCHECKED)
	public String searchImageInHistory(String base64) {
		try {
			JSONObject requestObj = new JSONObject();
			JSONObject imagePayloadObj = new JSONObject();

			imagePayloadObj.put(CorsightDeviceConstants.IMG, base64);
			
			requestObj.put(CorsightDeviceConstants.MAX_MATCHES, NumberConstants.ONE);
			requestObj.put(CorsightDeviceConstants.MIN_CONFIDENCE, NumberConstants.SEVENTY);
			requestObj.put(CorsightDeviceConstants.IMAGE_PAYLOAD, imagePayloadObj);

			String httpsUrl = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH + host + ApplicationConstants.DELIMITER_COLON + portHistory;
			String postUrl = CorsightDeviceConstants.HISTORY_API_SEARCH_IMG;
			

			String responeData = requestExecutionUtil.executeHttpsPostRequest(requestObj, httpsUrl, postUrl);
			
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
			JSONObject jsonResponseData = (JSONObject) jsonResponse.get(CorsightDeviceConstants.DATA);
			JSONArray jsonMatchesData = (JSONArray) jsonResponseData.get(CorsightDeviceConstants.MATCHES);
			JSONObject poiInfoObj = (JSONObject) jsonMatchesData.get(0);
			String poiId=(String) poiInfoObj.get(CorsightDeviceConstants.PERSON_ID);
			
			return poiId;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	
	@SuppressWarnings(ApplicationConstants.UNCHECKED)
	public String getPoiDetailsByPoiId(String poiId) {

		try {
			JSONObject requestObj = new JSONObject();
			JSONArray poiArray = new JSONArray();
			JSONObject poiObj = new JSONObject();

			poiObj.put(CorsightDeviceConstants.POI_ID, poiId);
			poiArray.add(poiObj);
			requestObj.put(CorsightDeviceConstants.POIS,poiArray);

			String httpsUrl = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH + host + ApplicationConstants.DELIMITER_COLON + portPoi;
			String postUrl = CorsightDeviceConstants.POI_API_GET;

			String responeData = requestExecutionUtil.executeHttpsPostRequest(requestObj, httpsUrl, postUrl);

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
			JSONObject jsonResponseData = (JSONObject) jsonResponse.get(CorsightDeviceConstants.DATA);
			JSONArray poiResponseArray=(JSONArray) jsonResponseData.get(CorsightDeviceConstants.POIS);
			JSONObject poiIdObj=(JSONObject) poiResponseArray.get(0);
			String poi_id=(String) poiIdObj.get(CorsightDeviceConstants.POI_ID);
			String msg=null;
			if(null!=poi_id && !(poi_id.isEmpty()))
				msg=ApplicationConstants.SUCCESS;
			return msg;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings(ApplicationConstants.UNCHECKED)
	public boolean startCamera(Device device) {

		boolean msg = false;
		try {
			JSONObject requestObj = new JSONObject();
			requestObj.put(CorsightDeviceConstants.ANALYZE, true);
			
			String httpsUrl = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH + host + ApplicationConstants.DELIMITER_COLON + portCamera;
			String postUrl = CorsightDeviceConstants.CAMERA_API + device.getCameraId() + CorsightDeviceConstants.SERVER_START_API;
			

			String responeData = requestExecutionUtil.executeHttpsPostRequest(requestObj, httpsUrl, postUrl);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
			JSONObject jsonMetaData = (JSONObject) jsonResponse.get(CorsightDeviceConstants.DATA);
			
			msg = (boolean)jsonMetaData.get(CorsightDeviceConstants.ANALYZE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	
	public String analysisCamera(Device device) {

		String mode =ApplicationConstants.DELIMITER_EMPTY;
		try {
			String httpsUrl = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH + host + ApplicationConstants.DELIMITER_COLON + portCamera;
			String getUrl = CorsightDeviceConstants.CAMERAS_API_ANALYSIS;

			String responeData = requestExecutionUtil.executeHttpsGetRequest(httpsUrl, getUrl);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
			JSONObject jsonMetaData = (JSONObject) jsonResponse.get(CorsightDeviceConstants.DATA);
			
			mode = (String)jsonMetaData.get(CorsightDeviceConstants.MODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mode;
	}
}
