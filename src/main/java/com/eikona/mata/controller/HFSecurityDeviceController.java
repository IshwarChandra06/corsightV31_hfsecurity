package com.eikona.mata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eikona.mata.entity.Device;
import com.eikona.mata.service.DeviceService;
import com.eikona.mata.util.HFSecurityDeviceUtil;

@RestController
@RequestMapping("/hfsecurity")
public class HFSecurityDeviceController {
	
	@Autowired
	private HFSecurityDeviceUtil hfSecurityDeviceUtil; 
	
	@Autowired
	private DeviceService deviceService;
	
	@GetMapping("/sync-employee")
	public void syncEmployeeToHf() {
		hfSecurityDeviceUtil.addAndUpdateEmployeeInHF();
	}
	
	@GetMapping("/delete-employee")
	public void deleteEmployeeFromHf() {
		hfSecurityDeviceUtil.deleteEmployeeFromHF();
	}
	
	@GetMapping("/sync-employee-image")
	public void syncEmployeeImageToHf() {
		hfSecurityDeviceUtil.addEmployeeFaceInHF();
	}
	@GetMapping("/delete-employee-image/{empId}/{id}")
	public void deleteEmployeeImageFromHf(@PathVariable(value = "empId") String empId,@PathVariable(value = "id") long id) {
		Device device = deviceService.getById(id);
		hfSecurityDeviceUtil.deleteEmployeeFaceFromHF(empId,device);
	}
	@GetMapping("/sync-employee-to-mata/{id}")
	public void syncEmployeeToMata(@PathVariable(value = "id") long id) {
		Device device = deviceService.getById(id);
		hfSecurityDeviceUtil.pullAllEmployeeFromHFDevice(device);
		hfSecurityDeviceUtil.pullAllEmployeeFaceFromHFDevice(device.getSerialNo());
	}
	
	@GetMapping("/restart-device/{serialNo}")
	public String restartHFDevice(@PathVariable(value = "serialNo") String serialNo) {
		boolean success=hfSecurityDeviceUtil.rebootHFDevice(serialNo);
		if(success)
			return "Reboot Successfully!";
		else
			return "Reboot Failed!";
	}
	@GetMapping("/reset-device/{serialNo}")
	public String resetHFDevice(@PathVariable(value = "serialNo") String serialNo) {
		boolean success=hfSecurityDeviceUtil.resetHFDevice(serialNo);
		if(success)
			return "Reset Successfully!";
		else
			return "Reset Failed!";
	}
	@GetMapping("/set-time/{serialNo}")
	public String setTimeInHFDevice(@PathVariable(value = "serialNo") String serialNo) {
		boolean success=hfSecurityDeviceUtil.setTimeInHFDevice(serialNo);
		if(success)
			return "Time set Successfully!";
		else
			return "Time set Failed!";
	}
}
