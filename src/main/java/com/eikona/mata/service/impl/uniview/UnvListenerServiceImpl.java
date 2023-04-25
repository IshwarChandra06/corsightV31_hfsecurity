package com.eikona.mata.service.impl.uniview;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.constants.UnvDeviceConstants;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.Transaction;
import com.eikona.mata.repository.DeviceRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.repository.TransactionRepository;
import com.eikona.mata.service.impl.model.SseServiceImpl;
import com.eikona.mata.util.SavingCropImageUtil;

@Service
public class UnvListenerServiceImpl {
	
	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private SseServiceImpl sseService;
	
	@Autowired
	private SavingCropImageUtil savingCropImageUtil;

	public Device saveDeviceInfo(String response, String ipAddress) {

		SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_US);
		JSONObject jsonHeartReportInfo = new JSONObject(response);

		Device device = deviceRepository.findBySerialNoAndIsDeletedFalse(jsonHeartReportInfo.getString(UnvDeviceConstants.DEVICE_CODE));
		String dateStr = jsonHeartReportInfo.getString(UnvDeviceConstants.TIME);
		try {
			if (null != device) {
				device.setLastOnline(format.parse(dateStr));
				device.setIpAddress(ipAddress);
				return deviceRepository.save(device);
			} else {
				Device deviceNew = new Device();
				deviceNew.setSync(false);
				deviceNew.setRefId(jsonHeartReportInfo.getString(UnvDeviceConstants.REF_ID));
				deviceNew.setLastOnline(format.parse(dateStr));
				deviceNew.setSerialNo(jsonHeartReportInfo.getString(UnvDeviceConstants.DEVICE_CODE));
				deviceNew.setIpAddress(ipAddress);
				deviceNew.setModel(UnvDeviceConstants.MODEL_TYPE);
				deviceNew.setDeviceType(jsonHeartReportInfo.getLong(UnvDeviceConstants.DEVICE_TYPE));
				return deviceRepository.save(deviceNew);
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void saveTransactionInfo(String response) {

		try {
			JSONObject jsonPersonVerification = new JSONObject(response);

			Transaction transaction = new Transaction();

			transaction.setSerialNo(jsonPersonVerification.getString(UnvDeviceConstants.DEVICE_CODE));
			
			JSONArray libMatarray = jsonPersonVerification.getJSONArray(UnvDeviceConstants.LIB_MAT_INFO_LIST);
			
			setEmployeeDetailsInTransactionFromListener(jsonPersonVerification, transaction, libMatarray);

			sseService.processEvent(transaction);
		
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setEmployeeDetailsInTransactionFromListener(JSONObject jsonPersonVerification, Transaction transaction,
			JSONArray libMatarray) {
		if (libMatarray.length() > NumberConstants.ZERO) {
			JSONObject empObj = libMatarray.getJSONObject(NumberConstants.ZERO).getJSONObject(UnvDeviceConstants.MATCH_PERSON_INFO);
			
			Employee employee = employeeRepository.findByEmpId(empObj.getString(UnvDeviceConstants.PERSON_CODE));
			Device deviceObj = deviceRepository.findBySerialNoAndIsDeletedFalse(jsonPersonVerification.getString(UnvDeviceConstants.DEVICE_CODE));
		
			transaction.setEmployeeCode(empObj.getString(UnvDeviceConstants.PERSON_CODE));
			transaction.setName(empObj.getString(UnvDeviceConstants.PERSON_NAME));
			transaction.setAppearanceId(transaction.getEmployeeCode());
			transaction.setPoiId(transaction.getEmployeeCode());
			
			if(null!=employee) {
				transaction.setEmployee(employee);
				if(null!=employee.getDepartment())
					transaction.setDepartment(employee.getDepartment().getName());
				
				if(null!=employee.getDesignation())
					transaction.setDesignation(employee.getDesignation().getName());
				
				if(null!=employee.getBranch())
					transaction.setBranch(employee.getBranch().getName());
				
			}
			
			if(null!=deviceObj) {
				transaction.setDeviceName(deviceObj.getName());
				transaction.setAccessType(deviceObj.getAccessType());
				transaction.setSerialNo(deviceObj.getSerialNo());
				if(null!=deviceObj.getArea())
					transaction.setArea(deviceObj.getArea().getName());
			}
		}
		
		setBioDetailsInTransactionFromListener(jsonPersonVerification, transaction);
	}

	private void setBioDetailsInTransactionFromListener(JSONObject jsonPersonVerification, Transaction transaction) {
		SimpleDateFormat dateformat=new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
		SimpleDateFormat timeformat=new SimpleDateFormat(ApplicationConstants.TIME_FORMAT_24HR);
		
		JSONArray facearray = jsonPersonVerification.getJSONArray(UnvDeviceConstants.FACE_INFO_LIST);
		if (facearray.length() > NumberConstants.ZERO) {
			JSONObject faceObj = facearray.getJSONObject(NumberConstants.ZERO);
			transaction.setWearingMask(faceObj.getInt(UnvDeviceConstants.MASK_FLAG) == NumberConstants.ONE);
			
			transaction.setPunchDate(new Date(faceObj.getLong(UnvDeviceConstants.TIME_STAMP) * NumberConstants.THOUSAND));
			String dateStr=dateformat.format(transaction.getPunchDate());
			String timeStr=timeformat.format(transaction.getPunchDate());
			transaction.setPunchDateStr(dateStr);
			transaction.setPunchTimeStr(timeStr);
			
			transaction.setTemperature(String.valueOf(faceObj.getFloat(UnvDeviceConstants.TEMPERATURE)));
			JSONObject faceImage=(JSONObject) faceObj.get(UnvDeviceConstants.FACE_IMAGE);
			String base64 = (String) faceImage.get(UnvDeviceConstants.DATA);
			String cropImagePath=savingCropImageUtil.saveCropImages(base64, transaction);
			transaction.setCropimagePath(cropImagePath);
		}
		
		transactionRepository.save(transaction);
	}
}
