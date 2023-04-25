package com.eikona.mata.springbatch.writer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.eikona.mata.entity.Action;
import com.eikona.mata.entity.ActionDetails;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.repository.DeviceRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.service.DeviceSyncAbstractService;
import com.eikona.mata.util.CorsightDeviceUtil;
import com.eikona.mata.util.EmployeeObjectMap;

@Component
public class EmployeeDeleteFromDeviceWriter implements ItemWriter<Employee> {

	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	@Qualifier("corsightDeviceService")
	private DeviceSyncAbstractService<String> corsightSyncServiceImpl;

	@Autowired
	@Qualifier("bewardDeviceService")
	private DeviceSyncAbstractService<String> bewardSyncServiceImpl;

	@Autowired
	@Qualifier("unvDeviceService")
	private DeviceSyncAbstractService<Long> unvSyncServiceImpl;
	
	@Autowired
	private EmployeeObjectMap employeeObjectMap;
	
	@Autowired
	private CorsightDeviceUtil corsightDeviceUtil;

	@Override
	public void write(List<? extends Employee> employeeList) throws Exception {
		
        List<Employee> empList= new ArrayList<>();
        Map<Long, Employee> employeeMap = employeeObjectMap.getDeletedEmployee();
        
		for (Employee employee : employeeList) {
			
			Employee empObj=employeeMap.get(employee.getId());
			
			if (null != empObj.getArea()) {
				List<Device> deviceList = deviceRepository.findByAreaAndIsDeletedFalseCustom(empObj.getArea());

				for (Device device : deviceList) {
					ActionDetails actionDetails = new ActionDetails();
					Action action = new Action();
					action.setEmployee(empObj);
					actionDetails.setAction(action);
					actionDetails.setDevice(device);
					if ("Corsight".equalsIgnoreCase(device.getModel())) {
						try {
							String message = corsightDeviceUtil.corsightServerBasicInfo();
							if (null != message) {
								if (null != employee.getPoi() && "true"
										.equalsIgnoreCase(corsightSyncServiceImpl.queryFromDevice(device, empObj))) {

									String response = corsightSyncServiceImpl
											.deleteSingleEmployeeFromDevice(actionDetails);
									if (!("success".equalsIgnoreCase(response))) {
										empObj.setSync(true);
										empObj.setPoi(null);
										empList.add(empObj);
									}

								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else if ("BFRC".equalsIgnoreCase(device.getModel())) {
						try {
							String responseData = bewardSyncServiceImpl.deviceBasicInfo(device.getIpAddress());
							if (null != responseData) {
								String pullRespone = bewardSyncServiceImpl.queryFromDevice(device, empObj);

								JSONParser jsonParser = new JSONParser();
								JSONObject jsonResponse = (JSONObject) jsonParser.parse(pullRespone);
								Long StatusCode = (Long) jsonResponse.get("code");
								if (null == StatusCode) {

									String response = bewardSyncServiceImpl
											.deleteSingleEmployeeFromDevice(actionDetails);

									if (!("Failed".equalsIgnoreCase(response))) {

										empObj.setSync(true);
										empList.add(empObj);
									}

								}

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if ("FRWT".equalsIgnoreCase(device.getModel())) {
						try {
							Long responseCode = unvSyncServiceImpl.deviceBasicInfo(device.getIpAddress());

							if (responseCode == 0l) {

								Long statusCode = unvSyncServiceImpl.deleteSingleEmployeeFromDevice(actionDetails);

								if (statusCode == 200l) {
									empObj.setSync(true);
									empList.add(empObj);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
			}
		}
		employeeRepository.saveAll(empList);
	}
}
