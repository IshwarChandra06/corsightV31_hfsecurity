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
import com.eikona.mata.entity.Organization;
import com.eikona.mata.repository.AreaRepository;
import com.eikona.mata.util.RequestExecutionUtil;

@Component
public class WatchListSync {

	@Autowired
	private AreaRepository areaDatatableRepository;

	@Autowired
	private RequestExecutionUtil requestExecutionUtil;
	
	@Value("${corsight.host.url}")
    private String corsightHost;
	
	@Value("${corsight.poi.port}")
    private String poiPort;

	public String syncWatchlist(Organization organization) {
		try {

			String poiUrl = ApplicationConstants.HTTPS_COLON_DOUBLE_SLASH + corsightHost
					+ ApplicationConstants.DELIMITER_COLON + poiPort;
			String addPoiUrl = CorsightDeviceConstants.POI_API_WATCHLIST_SYNC;

			String responeData = requestExecutionUtil.executeHttpsGetRequest(poiUrl, addPoiUrl);

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
			JSONObject jsonResponseData = (JSONObject) jsonResponse.get(CorsightDeviceConstants.DATA);
			JSONArray jsonArray = (JSONArray) jsonResponseData.get(CorsightDeviceConstants.WATCHLISTS);
			List<Area> listWatchList = new ArrayList<>();
			for (int i = NumberConstants.ZERO; i < jsonArray.size(); i++) {
				JSONObject currentData = (JSONObject) jsonArray.get(i);

				setAreaObjectFromResponse(listWatchList, currentData,organization);
			}
			areaDatatableRepository.saveAll(listWatchList);
			return MessageConstants.SYNC_SUCCESSFULLY;
		} catch (Exception e) {
			e.printStackTrace();
			return MessageConstants.SYNC_FAILED;
		}
	}

	private void setAreaObjectFromResponse(List<Area> listWatchList, JSONObject jsonData, Organization organization) {
		Area area = areaDatatableRepository
				.findByWatchlistIdAndIsDeletedFalse((String) jsonData.get(CorsightDeviceConstants.WATCHLIST_ID));
		if (null == area) {
			Area areaObj = new Area();
			areaObj.setName((String) jsonData.get(CorsightDeviceConstants.DISPLAY_NAME));
			areaObj.setWatchlist((String) jsonData.get(CorsightDeviceConstants.WATCHLIST_TYPE));
			areaObj.setWatchlistId((String) jsonData.get(CorsightDeviceConstants.WATCHLIST_ID));
			Branch branch = new Branch();
			branch.setId(Long.valueOf(DefaultConstants.DEFAULT_BRANCH_ID));
			areaObj.setBranch(branch);
			areaObj.setOrganization(organization);
			listWatchList.add(areaObj);

		}
	}

}
