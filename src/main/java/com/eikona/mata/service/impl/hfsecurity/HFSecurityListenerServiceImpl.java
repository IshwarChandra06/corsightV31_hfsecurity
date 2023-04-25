package com.eikona.mata.service.impl.hfsecurity;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.Transaction;
import com.eikona.mata.repository.DeviceRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.repository.TransactionRepository;
import com.eikona.mata.util.SavingCropImageUtil;

@Component
public class HFSecurityListenerServiceImpl {

	@Autowired
	private DeviceRepository deviceRepository;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private SavingCropImageUtil savingCropImageUtil;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	public void saveDeviceInfoFromResponse(String ipAddress, String deviceKey, String time, String version,
			String faceCount, String personCount) {
		
		Device device = deviceRepository.findBySerialNoAndIsDeletedFalse(deviceKey);
		Date lastOnline=new Date(Long.valueOf(time));
		if(null!=device) {
			device.setFaceNo(Long.valueOf(faceCount));
			device.setPersonNo(Long.valueOf(personCount));
			device.setLastOnline(lastOnline);
		}else {
			device = new Device();
			device.setIpAddress(ipAddress);
			device.setSerialNo(deviceKey);
			device.setPersonNo(Long.valueOf(personCount));
			device.setFaceNo(Long.valueOf(faceCount));
			device.setLastOnline(lastOnline);
			device.setRefId(version);
			device.setModel("HF-Security");
		}
		deviceRepository.save(device);
		
	}

	public void saveTransactionInfoFromResponse(String ipAddress, String deviceKey, String time, String type,
			 String searchScore, String livenessScore, String mask,
			String imgBase64, String personId) {
		
		Transaction transaction = new Transaction();
		Employee employee = employeeRepository.findByEmpId(personId);
		Device device = deviceRepository.findBySerialNoAndIsDeletedFalse(deviceKey);
		
		setEmployeeDetails(transaction, employee);
		
		setDeviceDetails(transaction, device);
		
		setBasicTransactionDetails(time, type,searchScore, livenessScore, imgBase64,mask, transaction);
		
		transactionRepository.save(transaction);
	}

	private void setBasicTransactionDetails(String time, String type,  String searchScore,
			String livenessScore,  String imgBase64, String mask,
			Transaction transaction) {
		SimpleDateFormat dateformat=new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
		SimpleDateFormat timeformat=new SimpleDateFormat(ApplicationConstants.TIME_FORMAT_24HR);
		transaction.setAccessType(type);
		transaction.setSearchScore(searchScore);
		transaction.setLivenessScore(livenessScore);
		transaction.setPunchDate(new Date(Long.valueOf(time)));
		String dateStr=dateformat.format(transaction.getPunchDate());
		String timeStr=timeformat.format(transaction.getPunchDate());
		transaction.setPunchDateStr(dateStr);
		transaction.setPunchTimeStr(timeStr);
		String cropImagePath=savingCropImageUtil.saveCropImages(imgBase64, transaction);
		transaction.setCropimagePath(cropImagePath);
		
		setMaskStatus(mask, transaction);
			
	}

	private void setMaskStatus(String mask, Transaction transaction) {
		if("-1".equalsIgnoreCase(mask)) {
			transaction.setMaskStatus("Unopened");
		    transaction.setWearingMask(false);
		}else if("0".equalsIgnoreCase(mask)) {
			transaction.setMaskStatus("Not wearing a mask");
		    transaction.setWearingMask(false);
		}else {
			transaction.setMaskStatus("Wear a mask");
		    transaction.setWearingMask(true);
		}
	}

	private void setDeviceDetails(Transaction transaction, Device device) {
		if(null!=device) {
			transaction.setDeviceName(device.getName());
			transaction.setSerialNo(device.getSerialNo());
			if(null!=device.getArea())
				transaction.setArea(device.getArea().getName());
		}
	}

	private void setEmployeeDetails(Transaction transaction, Employee employee) {
		if(null!=employee) {
			transaction.setEmployee(employee);
			if(null!=employee.getDepartment())
				transaction.setDepartment(employee.getDepartment().getName());
			
			if(null!=employee.getDesignation())
				transaction.setDesignation(employee.getDesignation().getName());
			
			if(null!=employee.getBranch())
				transaction.setBranch(employee.getBranch().getName());
			
		}
	}

}
