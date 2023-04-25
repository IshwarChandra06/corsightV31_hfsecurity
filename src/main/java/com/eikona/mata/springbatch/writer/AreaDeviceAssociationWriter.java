package com.eikona.mata.springbatch.writer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eikona.mata.entity.Area;
import com.eikona.mata.entity.Device;
import com.eikona.mata.repository.DeviceRepository;
import com.eikona.mata.util.AreaObjectMap;
import com.eikona.mata.util.CorsightDeviceUtil;
@Component
public class AreaDeviceAssociationWriter implements ItemWriter<Area> {

	@Autowired
	private AreaObjectMap areaObjectMap;

	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private CorsightDeviceUtil corsightDeviceUtil;

	@Override
	public void write(List<? extends Area> areaList) throws Exception {
		Map<Long, Area> areaMap = areaObjectMap.getCorsightDeviceByIsDeletedFalseAndIsSyncTrue();
		String message = corsightDeviceUtil.corsightServerBasicInfo();
		if (null != message) {
			for (Area areaObj : areaList) {
				Area area = areaMap.get(areaObj.getId());
				List<Area> areaSearchList= new ArrayList<>();
				areaSearchList.add(area);
				
				List<Device> deviceList=deviceRepository.findByAreaAndIsDeletedFalseCustom(areaSearchList);
				    for(Device device:deviceList) {
				    	if(null!=device.getCameraId()) {
				    		List<String> watchlistIdList= corsightDeviceUtil.getWatchlistListFromCamera(device.getCameraId());
				    		if(!(watchlistIdList.contains(device.getArea().getWatchlistId())))
				    			corsightDeviceUtil.editCamera(device);
				    	}
				    	
				    }
			}
		}
	}
} 
