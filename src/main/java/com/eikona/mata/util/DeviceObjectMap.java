package com.eikona.mata.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eikona.mata.entity.Device;
import com.eikona.mata.repository.DeviceRepository;
@Component
public class DeviceObjectMap {
	@Autowired
	private DeviceRepository deviceRepository;
	
	public Map<Long, Device> getDeviceByIsDeletedFalseAndIsSyncFalse(){
		List<Device> deviceList = deviceRepository.findAllByIsDeletedFalseAndIsSyncFalse();
		Map<Long, Device> deviceMap = new HashMap<Long, Device>();
		
		for(Device device: deviceList ) {
			deviceMap.put(device.getId(), device);
		}
		return deviceMap;
	}

	public Map<Long, Device> getCorsightDeviceByIsDeletedTrueAndIsSyncFalse() {
		List<Device> deviceList = deviceRepository.findAllByIsDeletedTrueAndIsSyncTrueCustom();
		Map<Long, Device> deviceMap = new HashMap<Long, Device>();
		
		for(Device device: deviceList ) {
			deviceMap.put(device.getId(), device);
		}
		return deviceMap;
	}
}
