package com.eikona.mata.service.impl.corsight;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.CorsightDeviceConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.corsight.request.dto.Data;
import com.eikona.mata.corsight.request.dto.Watchlist;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.Transaction;
import com.eikona.mata.repository.DeviceRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.repository.TransactionRepository;
import com.eikona.mata.service.impl.model.SseServiceImpl;
import com.eikona.mata.util.BewardDeviceUtil;
import com.eikona.mata.util.ImageProcessingUtil;
import com.eikona.mata.util.SavingCropImageUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CorsightListenerServiceImpl {


	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private SseServiceImpl sseService;

	@Autowired
	private SavingCropImageUtil savingCropImageUtil;
	
	@Autowired
	private ImageProcessingUtil imageProcessingUtil;
	
	@Autowired
	private BewardDeviceUtil bewardDeviceUtil;

	/**
	 * In this function, each face transaction is coming as a String response. If
	 * the poi id of transaction matches with any employee then it is considered as
	 * Employee and transaction info is setting into the respective Transaction
	 * columns and save into mata database.
	 * 
	 * @param responseData -Transaction Info is coming as a String response.
	 */
	public void corsightHeartReportInfo(@RequestBody JSONObject responseData) { 
		try {

			Data eventData;
			Transaction transaction = null;
		
			if (CorsightDeviceConstants.APPEARANCE.equalsIgnoreCase(responseData.get(CorsightDeviceConstants.EVENT_TYPE).toString())) {
				try {
					eventData = objectMapper.readValue(responseData.toString(), Data.class);
					
//					if (eventData.getTrigger() == NumberConstants.ONE || eventData.getTrigger() == NumberConstants.TWO || eventData.getTrigger() == NumberConstants.THREE) {
						transaction = transactionRepository.findByAppearanceId((String) eventData.getAppearance_data().getAppearance_id());
//					}
					if (null == transaction) 
						transaction = new Transaction();

					setPunchDateAndTime(eventData, transaction);
					setTransactionDetails(eventData, transaction);
					transaction.setDeviceName(eventData.getCamera_data().getCamera_description());
					transaction.setSerialNo(eventData.getCamera_data().getCamera_id());
					setWatchListAndAreaToTransaction(eventData, transaction);
					setEmployeeDetailsToTransaction(eventData, transaction);

					transaction=transactionRepository.save(transaction);
					byte[] image = imageProcessingUtil.searchTransactionImage(transaction);
					transaction.setCropImageByte(image);
					sseService.processEvent(transaction);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			JSONObject response = new JSONObject();

			response.put(ApplicationConstants.STATUS, ApplicationConstants.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setPunchDateAndTime(Data eventData, Transaction transaction) {
		DateFormat dateFormat = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
		DateFormat timeFormat = new SimpleDateFormat(ApplicationConstants.TIME_FORMAT_24HR);
		Timestamp ts = new Timestamp((long) eventData.getCrop_data().getFrame_timestamp());
		Date date = new Date(ts.getTime() * NumberConstants.THOUSAND);
		String dateString = dateFormat.format(date);
		transaction.setPunchDateStr(dateString);
		transaction.setPunchDate(date);
		transaction.setPunchTimeStr(timeFormat.format(date));
		
	}

	public void setTransactionDetails(Data eventData, Transaction transaction) {

		setAppearanceDetailsToTransaction(eventData, transaction);

		if (CorsightDeviceConstants.NOT_MASKED.equalsIgnoreCase(transaction.getMaskStatus()))
			transaction.setWearingMask(true);
		else
			transaction.setWearingMask(false);

		String imagePath = savingCropImageUtil.saveCropImages(eventData.getCrop_data().getFace_crop_img(), transaction);

		transaction.setCropimagePath(imagePath);

		Device device = deviceRepository.findByCameraIdAndIsDeletedFalse(eventData.getCamera_data().getCamera_id());
		if (null != device) {
			transaction.setAccessType(device.getAccessType());
			transaction.setOrganization(device.getOrganization().getName());
		}

//		if (null != device) {
//			transaction.setAccessType(device.getAccessType());
//			if(null!=eventData.getMatch_data().getPoi_id() && !eventData.getMatch_data().getPoi_id().isEmpty()) {
//			  bewardDeviceUtil.openDoorControl(device);
//			}
//			
//		}
		transaction.setSync(false);
		transaction.setFailed(false);
		
	}

	public void setAppearanceDetailsToTransaction(Data eventData, Transaction transaction) {
		try {
			DecimalFormat dec = new DecimalFormat(ApplicationConstants.DECIMAL_FORMAT);
			transaction.setPoiConfidence(Double.parseDouble(dec.format(eventData.getMatch_data().getPoi_confidence())));
		}catch (NumberFormatException e) {
			transaction.setPoiConfidence(0.0);
		}
		
		transaction.setPoiId(eventData.getMatch_data().getPoi_id());
		transaction.setMaskedScore(eventData.getCrop_data().getMasked_score());
		transaction.setEventId(eventData.getEvent_id());
		transaction.setAppearanceId(eventData.getAppearance_data().getAppearance_id());
		transaction.setMaskStatus(eventData.getFace_features_data().getMask_outcome());
		transaction.setAge(eventData.getFace_features_data().getAge_group_outcome());
		transaction.setGender(eventData.getFace_features_data().getGender_outcome());
	}

	public void setEmployeeDetailsToTransaction(Data eventData, Transaction transaction) {
		if (null != eventData.getMatch_data().getPoi_id()) {
//			Employee employee = employeeRepository.findByEmpIdAndIsDeletedFalse(eventData.getMatch_data().getPoi_id());
			Employee employee = employeeRepository.findByEmpIdAndIsDeletedFalse(eventData.getMatch_data().getPoi_display_name());

			if (null != employee) {
				transaction.setEmployee(employee);
				if (null != employee.getDepartment())
					transaction.setDepartment(employee.getDepartment().getName());
				if (null != employee.getDesignation())
					transaction.setDesignation(employee.getDesignation().getName());
				if (null != employee.getBranch())
					transaction.setBranch(employee.getBranch().getName());
				transaction.setEmpId(employee.getEmpId());
				transaction.setName(employee.getName());
				transaction.setEmployeeCode(employee.getEmpId());
			}
//			else {
//				transaction.setEmployeeCode(eventDto.getMatch_data().poi_display_name);
//				
//			}
		}
	}

	public void setWatchListAndAreaToTransaction(Data eventData, Transaction transaction) {
		List<Watchlist> watchlistList = eventData.getMatch_data().getWatchlists();
		for (Watchlist watchlist : watchlistList) {
			transaction.setWatchlistId(watchlist.getWatchlist_id());
			transaction.setWatchlistName(watchlist.getDisplay_name());
			transaction.setArea(watchlist.getDisplay_name());
		}
		
	}

}