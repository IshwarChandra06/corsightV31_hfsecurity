package com.eikona.mata.sync;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.CorsightDeviceConstants;
import com.eikona.mata.constants.DefaultConstants;
import com.eikona.mata.constants.MessageConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.entity.Area;
import com.eikona.mata.entity.Branch;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Organization;
import com.eikona.mata.repository.AreaRepository;
import com.eikona.mata.repository.DeviceRepository;
import com.eikona.mata.util.RequestExecutionUtil;

@Component
public class DeviceSync {

	@Autowired
	private DeviceRepository cameraRepository;
	
	@Autowired
	private AreaRepository areaRepository;
	
	@Autowired
	private RequestExecutionUtil requestExecutionUtil;
	
	@Value("${corsight.host.url}")
    private String corsightHost;
	
	@Value("${corsight.camera.port}")
    private String cameraPort;
    
	public String syncDevice(Organization organization) {
		try {

			String url = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH+corsightHost+ApplicationConstants.DELIMITER_COLON+cameraPort;
			String addUrl = CorsightDeviceConstants.CAMERA_API;

			String responeData = requestExecutionUtil.executeHttpsGetRequest(url, addUrl);
 
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
			JSONObject jsonResponseData = (JSONObject) jsonResponse.get(CorsightDeviceConstants.DATA);
			JSONArray jsonArray = (JSONArray) jsonResponseData.get(CorsightDeviceConstants.CAMERAS);
			List<Device> cameraList = new ArrayList<>();
			
			for (int i = NumberConstants.ZERO; i < jsonArray.size(); i++) {
				JSONObject jsonData = (JSONObject) jsonArray.get(i);
				Device cameraObj = new Device();
				Device camera = cameraRepository.findByCameraIdAndIsDeletedFalse((String) jsonData.get(CorsightDeviceConstants.CAMERA_ID));
				if (null == camera) {

					cameraObj.setCameraId((String) jsonData.get(CorsightDeviceConstants.CAMERA_ID));

				JSONObject jsonCongigData = (JSONObject) jsonData.get(CorsightDeviceConstants.CONFIG);

				JSONArray jsonWatchList = (JSONArray) jsonCongigData.get(CorsightDeviceConstants.WATCHLISTS);
				
				setAreaDetails(cameraObj, jsonWatchList);
				
				cameraObj.setModel(CorsightDeviceConstants.MODEL_TYPE);
				cameraObj.setSerialNo((String) jsonData.get(CorsightDeviceConstants.CAMERA_ID));
				cameraObj.setName((String) jsonData.get(CorsightDeviceConstants.DESCRIPTION));
				cameraObj.setIpAddress((String) jsonData.get(CorsightDeviceConstants.DISPLAY_RTSP_ADDRESS));
				cameraObj.setOrganization(organization);
				cameraList.add(cameraObj);

			}
		}
			cameraRepository.saveAll(cameraList);
			return MessageConstants.SYNC_SUCCESSFULLY;
		} catch (Exception e) {
			e.printStackTrace();
			return MessageConstants.SYNC_FAILED;
		}
	}


	@SuppressWarnings(ApplicationConstants.NULL)
	private void setAreaDetails(Device cameraObj, JSONArray jsonWatchList) {
		if(null != jsonWatchList || !(jsonWatchList.isEmpty())) {
			for (int k = NumberConstants.ZERO; k < jsonWatchList.size(); k++) {
				JSONObject jsonWatchListObj = (JSONObject) jsonWatchList.get(k);
				Area areaObj = areaRepository.findByWatchlistIdAndIsDeletedFalse((String) jsonWatchListObj.get(CorsightDeviceConstants.WATCHLIST_ID));
				
				String watchlistId = (String) jsonWatchListObj.get(CorsightDeviceConstants.WATCHLIST_ID);
				
				if(null == areaObj) {
					Area area = new Area();
					area.setName(ApplicationConstants.DEFAULT);
					area.setWatchlistId(watchlistId);
					Branch branch = new Branch();
					branch.setId(Long.valueOf(DefaultConstants.DEFAULT_BRANCH_ID));
					area.setBranch(branch);
					areaRepository.save(area);
					cameraObj.setArea(area);
					cameraObj.setBranch(branch);
				}else {
					cameraObj.setArea(areaObj);
				}

			}
			
		}
	}

}
