package com.eikona.mata.service.impl.model;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.BewardDeviceConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.constants.TransactionConstants;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Transaction;
import com.eikona.mata.repository.TransactionRepository;
import com.eikona.mata.service.DeviceSyncAbstractService;
import com.eikona.mata.service.TransactionService;
import com.eikona.mata.util.CalendarUtil;
import com.eikona.mata.util.GeneralSpecificationUtil;

@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	@Qualifier(BewardDeviceConstants.SERVICE)
	private DeviceSyncAbstractService<String> bewardSyncServiceImpl;

	@Autowired
	private GeneralSpecificationUtil<Transaction> generalSpecification;

	@Autowired
	private CalendarUtil calendarUtil;



	@Override
	public PaginationDto<Transaction> searchByField(Long id, String sDate, String eDate, String employeeId,
			String employeeName, String office, String device, String department, String designation, int pageno,
			String sortField, String sortDir,String organization) {
		Date startDate = null;
		Date endDate = null;
		if (!sDate.isEmpty() && !eDate.isEmpty()) {
			SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
			try {

				startDate = format.parse(sDate);
				endDate = format.parse(eDate);
				endDate = calendarUtil.getConvertedDate(endDate, NumberConstants.TWENTY_THREE, NumberConstants.FIFTY_NINE, NumberConstants.FIFTY_NINE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}

		Page<Transaction> page = getTransactionBySpecification(id, employeeId, employeeName, office, device, department,
				designation, pageno, sortField, sortDir, startDate, endDate,organization);
		List<Transaction> employeeShiftList = page.getContent();

		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
		PaginationDto<Transaction> dtoList = new PaginationDto<Transaction>(employeeShiftList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir,
				ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<Transaction> getTransactionBySpecification(Long id, String employeeId, String employeeName,
			String office, String device, String department, String designation, int pageno, String sortField,
			String sortDir, Date startDate, Date endDate, String organization) {

		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno -NumberConstants.ONE , NumberConstants.TEN, sort);

		
		Specification<Transaction> idSpec = generalSpecification.longSpecification(id, ApplicationConstants.ID);
		Specification<Transaction> dateSpec = generalSpecification.dateSpecification(startDate, endDate,
				TransactionConstants.PUNCH_DATE);
		Specification<Transaction> empIdSpec = generalSpecification.stringSpecification(employeeId, TransactionConstants.EMP_ID);
		Specification<Transaction> empNameSpec = generalSpecification.stringSpecification(employeeName, ApplicationConstants.NAME);
		Specification<Transaction> offSpec = generalSpecification.stringSpecification(office, TransactionConstants.BRANCH);
		Specification<Transaction> devSpec = generalSpecification.stringSpecification(device, TransactionConstants.DEVICE_NAME);
		Specification<Transaction> deptSpec = generalSpecification.stringSpecification(department,
				TransactionConstants.DEPARTMENT);
		Specification<Transaction> desiSpec = generalSpecification.stringSpecification(designation,
				TransactionConstants.DESIGNATION);
		
		Specification<Transaction> orgSpc = generalSpecification.stringSpecification(organization, "organization");

		Page<Transaction> page = transactionRepository.findAll(idSpec.and(dateSpec).and(empIdSpec).and(empNameSpec).and(orgSpc)
				.and(offSpec).and(devSpec).and(deptSpec).and(desiSpec), pageable);
		return page;
	}

	@Override
	public PaginationDto<Transaction> searchEventSummary(String date, String sTime, String eTime, int pageno,
			String sortField, String sortDir) {

		Date starTime = null;
		Date endTime = null;
		if (!sTime.isEmpty() && !eTime.isEmpty()) {
			DateFormat timeFormat = new SimpleDateFormat(ApplicationConstants.TIME_FORMAT_24HR);
			try {
				starTime = timeFormat.parse(sTime);
				endTime = timeFormat.parse(eTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}

		Page<Transaction> page = getTransactionByDateAndTimeSpecification(date, pageno, sortField, sortDir, starTime,
				endTime);
		List<Transaction> dataList = page.getContent();

		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
		PaginationDto<Transaction> dtoList = new PaginationDto<Transaction>(dataList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir,
				ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<Transaction> getTransactionByDateAndTimeSpecification(String date, int pageno, String sortField,
			String sortDir, Date starTime, Date endTime) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TWENTY_THREE, sort);

		Specification<Transaction> dateSpec = generalSpecification.stringSpecification(date, TransactionConstants.PUNCH_DATE_STR);
		Specification<Transaction> timeSpec = generalSpecification.dateSpecification(starTime, endTime, TransactionConstants.PUNCH_TIME);

		Page<Transaction> page = transactionRepository.findAll(dateSpec.and(timeSpec), pageable);
		return page;
	}

}
