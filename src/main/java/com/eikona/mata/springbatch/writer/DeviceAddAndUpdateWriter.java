package com.eikona.mata.springbatch.writer;

import java.security.Principal;
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
import com.eikona.mata.util.BewardDeviceUtil;
import com.eikona.mata.util.CorsightDeviceUtil;
import com.eikona.mata.util.DeviceObjectMap;
import com.eikona.mata.util.UnvDeviceUtil;

@Component
public class DeviceAddAndUpdateWriter implements ItemWriter<Device> {
	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private CorsightDeviceUtil corsightDeviceUtil;

	@Autowired
	private UnvDeviceUtil unvDeviceUtil;

	@Autowired
	private BewardDeviceUtil bewardDeviceUtil;

	@Autowired
	private DeviceObjectMap deviceObjectMap;

	@Autowired
	@Qualifier("corsightDeviceService")
	private DeviceSyncAbstractService<String> corsightSyncService;

	@Autowired
	@Qualifier("bewardDeviceService")
	private DeviceSyncAbstractService<String> bewardSyncService;

	@Autowired
	@Qualifier("unvDeviceService")
	private DeviceSyncAbstractService<Long> unvSyncService;

	@Override
	public void write(List<? extends Device> deviceList) throws Exception {
		List<Device> cameraList = new ArrayList<>();
		Map<Long, Device> deviceMap = deviceObjectMap.getDeviceByIsDeletedFalseAndIsSyncFalse();

		Principal principal = new Principal() {

			@Override
			public String getName() {
				return "System";
			}
		};
		
		for (Device device : deviceList) {
			
			String cameraId = null;
			Device deviceObj = deviceMap.get(device.getId());
			if ("Create".equalsIgnoreCase(deviceObj.getFlag())) {

				if ("FRWT".equalsIgnoreCase(deviceObj.getModel())) {
					Long responseCode = unvSyncService.deviceBasicInfo(device.getIpAddress());
					if (responseCode == 0l) {
						unvDeviceUtil.addEmployeeToUNVDevice(deviceObj, deviceObj.getFlag(), principal);
					}
				}

				else if ("BFRC".equalsIgnoreCase(deviceObj.getModel())) {
					String responseData = bewardSyncService.deviceBasicInfo(device.getIpAddress());
					if (null != responseData) {
						bewardDeviceUtil.addEmployeeToBewardDevice(deviceObj, deviceObj.getFlag(), principal);
					}
				}

				else {
					String message = corsightDeviceUtil.corsightServerBasicInfo();
					if(null!=message) {
						cameraId = corsightDeviceUtil.createCamera(deviceObj);
						if (null != cameraId) {
							deviceObj.setCameraId(cameraId);
							deviceObj.setSync(true);
						}
					}
					
				}
			} else {
				if ("FRWT".equalsIgnoreCase(deviceObj.getModel())) {
					Long responseCode = unvSyncService.deviceBasicInfo(device.getIpAddress());
					if (responseCode == 0l) {
						unvDeviceUtil.addEmployeeToUNVDevice(deviceObj, deviceObj.getFlag(), principal);
					}
				}

				else if ("BFRC".equalsIgnoreCase(deviceObj.getModel())) {
					String responseData = bewardSyncService.deviceBasicInfo(device.getIpAddress());
					if (null != responseData) {
						bewardDeviceUtil.addEmployeeToBewardDevice(deviceObj, deviceObj.getFlag(), principal);
					}
				} else {
					String message = corsightDeviceUtil.corsightServerBasicInfo();
					
					if(null!=message) {
						corsightDeviceUtil.addEmployeeToCorsightDevice(deviceObj);

						if (null != deviceObj.getCameraId()) {
							if ("Success".equalsIgnoreCase(message)) {
								 cameraId=corsightDeviceUtil.editCamera(device);
								 if (null != cameraId) {
								    deviceObj.setSync(true);
								 }
							} else {
								cameraId = corsightDeviceUtil.createCamera(device);
								if (null != cameraId) {
									deviceObj.setCameraId(cameraId);
									deviceObj.setSync(true);
								}

							}
						} else {
							cameraId = corsightDeviceUtil.createCamera(deviceObj);
							if (null != cameraId) {
								deviceObj.setCameraId(cameraId);
								deviceObj.setSync(true);
							}

						}
					}

				}

			}

			cameraList.add(deviceObj);
		}
		deviceRepository.saveAll(cameraList);
	}
}
