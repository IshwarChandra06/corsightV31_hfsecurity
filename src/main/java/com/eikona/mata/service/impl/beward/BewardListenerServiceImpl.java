package com.eikona.mata.service.impl.beward;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.transaction.Transactional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.BewardDeviceConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.Transaction;
import com.eikona.mata.repository.DeviceRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.repository.TransactionRepository;
import com.eikona.mata.service.impl.model.SseServiceImpl;
import com.eikona.mata.util.BewardDeviceUtil;
import com.eikona.mata.util.CorsightDeviceUtil;
import com.eikona.mata.util.SavingCropImageUtil;

@Service
@Transactional
public class BewardListenerServiceImpl {
	
	@Autowired
	private DeviceRepository deviceRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private SseServiceImpl sseService;
	
	@Autowired
	private SavingCropImageUtil savingCropImageUtil ;
	
	@Autowired
	private CorsightDeviceUtil corsightDeviceUtil;
	
	@Autowired
	private BewardDeviceUtil bewardDeviceUtil;
	
	/**
	 * In this function, device heart beat response returns the last online time, ip address and the serial no of the device.
	 *All the device info are saved to mata database and getting updated, when the response is coming to listener.
	 * @param response -Device heart beat Info is coming as a String response.
	 */
	public void getDeviceInfoFromResponse(String response) {

		SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_US_SEPARATED_BY_T);
		JSONObject jsonHeartReportInfo = new JSONObject(response);

		JSONObject jsonDeviceInfo = (JSONObject) jsonHeartReportInfo.get(BewardDeviceConstants.INFO);
		
		saveDeviceInfoInDB(format, jsonDeviceInfo);

	}
	private void saveDeviceInfoInDB(SimpleDateFormat format, JSONObject jsonDeviceInfo) {
		long deviceId = jsonDeviceInfo.getLong(BewardDeviceConstants.DEVICE_ID);
		Device device = deviceRepository.findBySerialNoAndIsDeletedFalse(String.valueOf(deviceId));
		String dateStr = jsonDeviceInfo.getString(BewardDeviceConstants.TIME);
		String ipAddress = jsonDeviceInfo.getString(BewardDeviceConstants.IP);
		try {
			if (null != device) {
				device.setLastOnline(format.parse(dateStr));
				device.setIpAddress(ipAddress);
				 deviceRepository.save(device);
			} else {
				Device savedDevice = new Device();
				savedDevice.setSync(false);
				savedDevice.setLastOnline(format.parse(dateStr));
				savedDevice.setIpAddress(ipAddress);
				savedDevice.setModel(BewardDeviceConstants.MODEL_TYPE);
				savedDevice.setSerialNo(String.valueOf(deviceId));
				 deviceRepository.save(savedDevice);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	/** 
	 * In this function, each employee transaction is coming as a String response.
	 * Employee's transaction info is setting into the respective Transaction columns and save into mata database.
	 * @param response -Transaction Info is coming as a String response.
	 */
	public void saveTransactionInfo(String response) {
		 
		try {
		 	JSONObject jsonVerifyRequest = new JSONObject(response);
		 	JSONObject jsonVerifyInfo = (JSONObject) jsonVerifyRequest.get(BewardDeviceConstants.INFO);
	
		 	int deviceId = (int) jsonVerifyInfo.get(BewardDeviceConstants.DEVICE_ID);
		 	
		 	Employee employee = employeeRepository.findByEmpId((String) jsonVerifyInfo.get(BewardDeviceConstants.ID_CARD));
		 	Device deviceObj = deviceRepository.findBySerialNoAndIsDeletedFalse(String.valueOf(deviceId));
//			if (null == employee) {
//				Employee employeeCreate = new Employee();
//				employeeCreate.setEmpId((String) jsonVerifyInfo.get("IdCard"));
//				employeeCreate.setName(jsonVerifyInfo.getString("Name"));
//				employeeRepository.save(employeeCreate);
//			}
		 	Transaction transaction = new Transaction();
			 	setEmployeeDetailsInTransaction(jsonVerifyInfo, employee, deviceObj, transaction);
			 	setDateTimeInTransaction(jsonVerifyInfo, transaction);
			 	
			 	String cropImage=(String) jsonVerifyRequest.get(BewardDeviceConstants.SNAP_PIC);
				String base64=cropImage.split(ApplicationConstants.DELIMITER_COMMA)[NumberConstants.ONE];
				setApprearanceDetailsInTransaction(base64, jsonVerifyInfo, transaction);
			
			String poiId=corsightDeviceUtil.searchImageInHistory(base64);
			String msg=corsightDeviceUtil.getPoiDetailsByPoiId(poiId);
			if("Success".equalsIgnoreCase(msg))
				bewardDeviceUtil.openDoorControl(deviceObj);
		 	
		 	transactionRepository.save(transaction);
		 	
		 	sseService.processEvent(transaction);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void setApprearanceDetailsInTransaction(String base64, JSONObject jsonVerifyInfo,
			Transaction transaction) {
		double temperature = jsonVerifyInfo.getDouble(BewardDeviceConstants.TEMPERATURE);
		transaction.setTemperature(String.valueOf(temperature));
		transaction.setWearingMask((int)jsonVerifyInfo.get(BewardDeviceConstants.IS_NO_MASK)==NumberConstants.ONE);
		transaction.setAppearanceId(transaction.getEmpId());
		transaction.setPoiId(transaction.getEmpId());
		
		String imagePath= savingCropImageUtil.saveCropImages(base64,transaction);
		
		transaction.setCropimagePath(imagePath);
	}
	private void setDateTimeInTransaction(JSONObject jsonVerifyInfo, Transaction transaction) {
		SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_US_SEPARATED_BY_T);
		String date = jsonVerifyInfo.getString(BewardDeviceConstants.CREATE_TIME);
		try {
			transaction.setPunchDate(format.parse(date));
			SimpleDateFormat dateformat=new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
			SimpleDateFormat timeformat=new SimpleDateFormat(ApplicationConstants.TIME_FORMAT_24HR);
			
			String dateStr=dateformat.format(transaction.getPunchDate());
			String timeStr=timeformat.format(transaction.getPunchDate());
			transaction.setPunchDateStr(dateStr);
			transaction.setPunchTimeStr(timeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	} 
	private void setEmployeeDetailsInTransaction(JSONObject jsonVerifyInfo, Employee employee, Device deviceObj,
			Transaction transaction) {
		transaction.setEmpId((String) jsonVerifyInfo.get(BewardDeviceConstants.ID_CARD));
		transaction.setName(jsonVerifyInfo.getString(BewardDeviceConstants.NAME));
		if(null!=employee) {
			transaction.setEmployee(employee);
		    if(null!=employee.getDepartment())
		    	transaction.setDepartment(employee.getDepartment().getName());
		    
			if(null!=employee.getDesignation())
				transaction.setDesignation(employee.getDesignation().getName());
			
			if(null!=employee.getBranch())
				transaction.setBranch(employee.getBranch().getName());
			
			if(null!=deviceObj) {
					transaction.setDeviceName(deviceObj.getName());
					transaction.setAccessType(deviceObj.getAccessType());
					transaction.setSerialNo(deviceObj.getSerialNo());
				if(null!=deviceObj.getArea())
					transaction.setArea(deviceObj.getArea().getName());
				
			}
		}
	}
	
	
	
}
