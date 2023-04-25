package com.eikona.mata.springbatch.writer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.eikona.mata.entity.Device;
import com.eikona.mata.repository.DeviceRepository;
import com.eikona.mata.service.DeviceSyncAbstractService;
import com.eikona.mata.util.CorsightDeviceUtil;
import com.eikona.mata.util.DeviceObjectMap;

@Component
public class DeviceDeleteFromCorsighteWriter implements ItemWriter<Device> {
	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private CorsightDeviceUtil corsightDeviceUtil;

	@Autowired
	private DeviceObjectMap deviceObjectMap;

	@Autowired
	@Qualifier("corsightDeviceService")
	private DeviceSyncAbstractService<String> corsightSyncService;

	@Override
	public void write(List<? extends Device> deviceList) throws Exception {
		List<Device> cameraList = new ArrayList<>();
		Map<Long, Device> deviceMap = deviceObjectMap.getCorsightDeviceByIsDeletedTrueAndIsSyncFalse();
		for (Device device : deviceList) {
			Device deviceObj = deviceMap.get(device.getId());
			String message = corsightDeviceUtil.corsightServerBasicInfo();
			if(null!=message) {

				if (null != deviceObj.getCameraId() && "Success".equalsIgnoreCase(message)) {
						String response=corsightDeviceUtil.removeCamera(deviceObj);
						if(null!=response) {
							deviceObj.setSync(true);
							cameraList.add(deviceObj);
						}
					}
				}

			}
		deviceRepository.saveAll(cameraList);
	}
}
