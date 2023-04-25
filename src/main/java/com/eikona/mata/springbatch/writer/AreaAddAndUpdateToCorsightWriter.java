package com.eikona.mata.springbatch.writer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eikona.mata.entity.Area;
import com.eikona.mata.repository.AreaRepository;
import com.eikona.mata.util.AreaObjectMap;
import com.eikona.mata.util.CorsightDeviceUtil;

@Component
public class AreaAddAndUpdateToCorsightWriter implements ItemWriter<Area> {

	@Autowired
	private AreaObjectMap areaObjectMap;

	@Autowired
	private AreaRepository areaRepository;

	@Autowired
	private CorsightDeviceUtil corsightDeviceUtil;

	@Override
	public void write(List<? extends Area> areaList) throws Exception {
		List<Area> watchlistList = new ArrayList<>();
		Map<Long, Area> areaMap = areaObjectMap.getCorsightDeviceByIsDeletedFalseAndIsSyncFalse();
		
		String message = corsightDeviceUtil.corsightServerBasicInfo();
		if (null != message) {
			for (Area areaObj : areaList) {
				Area area = areaMap.get(areaObj.getId());
				String watchlistId = null;

				if ((null != area.getWatchlist()) || (!area.getWatchlist().isEmpty())) {
					if (null != area.getWatchlistId()) {
						String msg = corsightDeviceUtil.queryWatchlist(area.getWatchlistId());
						if ("Success".equalsIgnoreCase(msg)) {
							watchlistId =corsightDeviceUtil.createAndEditWatchList(area, "Update");
							if(null!=watchlistId)
								area.setSync(true);

						} else {
							watchlistId = corsightDeviceUtil.createAndEditWatchList(area, "Create");
							if(null!=watchlistId) {
								area.setWatchlistId(watchlistId);
								area.setSync(true);
							}
						}
						watchlistList.add(area);
					}

				}
//				else {
//					watchlistId = corsightDeviceUtil.createAndUpdateWatchList(area, "Create");
//					area.setWatchlistId(watchlistId);
//					area.setSync(true);
//				}
				areaRepository.saveAll(watchlistList);
			}

		}
	}
}
