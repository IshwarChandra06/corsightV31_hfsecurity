package com.eikona.mata.springbatch.writer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.repository.DeviceRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.util.EmployeeObjectMap;
import com.eikona.mata.util.HFSecurityDeviceUtil;

@Component
public class EmployeeSyncToHFSecurityWriter  implements ItemWriter<Employee> {
	
	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private EmployeeObjectMap employeeObjectMap;
	
	@Autowired
	private HFSecurityDeviceUtil hfSecurityDeviceUtil; 
	
	@Override
	public void write(List<? extends Employee> employeeList) throws Exception {
		
		 List<Employee> empList= new ArrayList<>();
		 Map<Long, Employee> employeeMap = employeeObjectMap.getEmployeeByIsDeletedFalseAndIsSyncFalse();
		for (Employee employee : employeeList) {
			
			Employee empObj=employeeMap.get(employee.getId());
			
			if (null != empObj.getArea()) {
				List<Device> deviceList = deviceRepository.findByAreaAndIsDeletedFalseCustom(empObj.getArea());

				for (Device device : deviceList) {

					if ("HF-Security".equalsIgnoreCase(device.getModel())) {
						boolean success=hfSecurityDeviceUtil.addEmployeeToHFDevice(empObj,device);
						if(success) {
							empObj.setSync(true);
							empList.add(empObj);
						}
						
					} 
				}
			}
		}
		employeeRepository.saveAll(empList);
	}

}
