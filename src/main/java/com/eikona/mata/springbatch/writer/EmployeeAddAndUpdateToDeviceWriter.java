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
public class EmployeeAddAndUpdateToDeviceWriter implements ItemWriter<Employee> {

	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	@Qualifier("corsightDeviceService")
	private DeviceSyncAbstractService<String> corsightSyncServiceImpl;

	@Autowired
	@Qualifier("bewardDeviceService")
	private DeviceSyncAbstractService<String> bewardSyncService;
	
	@Autowired
	@Qualifier("unvDeviceService")
	private DeviceSyncAbstractService<Long> unvSyncService;
	
	@Autowired
	private EmployeeObjectMap employeeObjectMap;
	
	@Autowired
	private CorsightDeviceUtil corsightDeviceUtil;
 

	@Override
	public void write(List<? extends Employee> employeeList) throws Exception {
		
		 List<Employee> empList= new ArrayList<>();
		 Map<Long, Employee> employeeMap = employeeObjectMap.getEmployeeByIsDeletedFalseAndIsSyncFalse();
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
						String poid=addEmployeeToCorsight(actionDetails, empObj, device);
						if(null!=poid) {
							empObj.setPoi(poid);
							empObj.setSync(true);
							empList.add(empObj);
						}
						
					} else if ("BFRC".equalsIgnoreCase(device.getModel())) {
						String response = addEmployeeToBeward(actionDetails, empObj, device);
						
						if(!("Failed".equalsIgnoreCase(response))){
							empObj.setSync(true);
							empList.add(empObj);
						}
					}
					else if ("FRWT".equalsIgnoreCase(device.getModel())) {
						
						Long statusCode = addEmployeeToUnv(actionDetails, empObj, device);
						
						if(statusCode !=-1l) {
							empObj.setSync(true);
							empList.add(empObj);
						}
					}
				}
			}
		}
		employeeRepository.saveAll(empList);
	}

	private Long addEmployeeToUnv(ActionDetails actionDetails, Employee employee, Device device) {
		Long responseCode = unvSyncService.deviceBasicInfo(device.getIpAddress());
		Long statusCode =-1l;
		if (responseCode == 0l) {
			 statusCode =unvSyncService.addEmployeeToDevice(actionDetails);
		}
		return statusCode;
		
	}

	private String addEmployeeToCorsight(ActionDetails actionDetails, Employee employee, Device device) {
		try {
			String poid = null;
			String message = corsightDeviceUtil.corsightServerBasicInfo();
			if(null!=message) {
				String poiId=corsightSyncServiceImpl.queryFromDevice(device, employee);
				if (null == employee.getPoi() && null==poiId) {
					poid = corsightSyncServiceImpl.addEmployeeToDevice(actionDetails);
				} else {
					actionDetails.getAction().getEmployee().setPoi(poiId);
					 corsightSyncServiceImpl.editEmployeeInDevice(actionDetails);
					 poid=employee.getPoi();
				}
			}
			return poid;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	private String addEmployeeToBeward(ActionDetails actionDetails, Employee employee, Device device) {
		try {
			String response=null;
			String responseData = bewardSyncService.deviceBasicInfo(device.getIpAddress());
			if (null != responseData) {
				String pullRespone = bewardSyncService.queryFromDevice(device, employee);
	
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonResponse = (JSONObject) jsonParser.parse(pullRespone);
				Long StatusCode = (Long) jsonResponse.get("code");
				if (null != StatusCode) {
					response=bewardSyncService.addEmployeeToDevice(actionDetails);
				} else {
					response=bewardSyncService.editEmployeeInDevice(actionDetails);
				}
			}
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			return "Failed";
		}
	}

}