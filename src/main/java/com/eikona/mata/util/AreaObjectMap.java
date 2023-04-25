package com.eikona.mata.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eikona.mata.entity.Area;
import com.eikona.mata.repository.AreaRepository;

@Component
public class AreaObjectMap {
	
	@Autowired
	private AreaRepository areaRepository;
	
	public Map<Long, Area> getCorsightDeviceByIsDeletedFalseAndIsSyncFalse(){
		List<Area> areaList = areaRepository.findAllByIsDeletedFalseAndIsSyncFalse();
		Map<Long, Area> areaMap = new HashMap<Long, Area>();
		
		for(Area area: areaList ) {
			areaMap.put(area.getId(), area);
		}
		return areaMap;
	}

	public Map<Long, Area> getCorsightDeviceByIsDeletedTrueAndIsSyncTrue() {
		List<Area> areaList = areaRepository.findAllByIsDeletedTrueAndIsSyncTrue();
		Map<Long, Area> areaMap = new HashMap<Long, Area>();
		
		
		for(Area area: areaList ) {
			areaMap.put(area.getId(), area);
		}
		return areaMap;
	}

	public Map<Long, Area> getCorsightDeviceByIsDeletedFalseAndIsSyncTrue() {
		List<Area> areaList = areaRepository.findAllByIsDeletedFalseAndIsSyncTrue();
		Map<Long, Area> areaMap = new HashMap<Long, Area>();
		
		for(Area area: areaList ) {
			areaMap.put(area.getId(), area);
		}
		return areaMap;
	}
}
