package com.eikona.mata.springbatch.writer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eikona.mata.entity.Area;
import com.eikona.mata.entity.Device;
import com.eikona.mata.repository.AreaRepository;
import com.eikona.mata.util.AreaObjectMap;
import com.eikona.mata.util.CorsightDeviceUtil;

@Component
public class AreaDeleteFromCorsightWriter implements ItemWriter<Area> {
	@Autowired
	private AreaObjectMap areaObjectMap;

	@Autowired
	private AreaRepository areaRepository;

	@Autowired
	private CorsightDeviceUtil corsightDeviceUtil;

	@Override
	public void write(List<? extends Area> areaList) throws Exception {
		List<Area> watchlistList = new ArrayList<>();
		Map<Long, Area> areaMap = areaObjectMap.getCorsightDeviceByIsDeletedTrueAndIsSyncTrue();
		String message = corsightDeviceUtil.corsightServerBasicInfo();
		if(null!=message) {
			for (Area area : areaList) { 
				
				Area areaObj = areaMap.get(area.getId());
				
				if ((null != areaObj.getWatchlistId()) || (!areaObj.getWatchlistId().isEmpty())) {
					
					String msg = corsightDeviceUtil.queryWatchlist(area.getWatchlistId());
					if (null != area.getWatchlistId() && "Success".equalsIgnoreCase(msg)) {
						
						String response =corsightDeviceUtil.removeWatchList(areaObj);
						
                        if(null!=response) {
							List<Device> devicelist = new ArrayList<>();
							areaObj.setDevice(devicelist);
							areaObj.setSync(true);
							watchlistList.add(areaObj);
                        }
					}

				}
			}
			areaRepository.saveAll(watchlistList);
		}
		
	}
}
