package com.eikona.mata.service.impl.model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.EmployeeConstants;
import com.eikona.mata.constants.EmployeeShiftDailyAssociationConstants;
import com.eikona.mata.constants.MessageConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.dto.ShiftRosterValidationDto;
import com.eikona.mata.entity.EmployeeShiftDailyAssociation;
import com.eikona.mata.repository.EmployeeShiftDailyAssociationRepository;
import com.eikona.mata.service.EmployeeShiftDailyAssociationService;
import com.eikona.mata.util.CalendarUtil;
import com.eikona.mata.util.ExcelEmployeeShiftImport;
import com.eikona.mata.util.GeneralSpecificationUtil;

@Service
public class EmployeeShiftDailyAssociationServiceImpl implements EmployeeShiftDailyAssociationService {
	
	@Autowired
	private EmployeeShiftDailyAssociationRepository employeeShiftDailyAssociationRepository;
	
	@Autowired
	private ExcelEmployeeShiftImport excelEmployeeShiftImport;
	
	@Autowired
	private CalendarUtil calendarUtil;
	
	@Autowired
	private GeneralSpecificationUtil<EmployeeShiftDailyAssociation> generalSpecification;
	
	
	@Override
	public PaginationDto<EmployeeShiftDailyAssociation> searchByField(Long id, String sDate, String eDate,
			String employeeId, String employeeName, String department, String shift, int pageno, String sortField,
			String sortDir,String org) {
		Date startDate = null;
		Date endDate = null;
		if (!sDate.isEmpty() && !eDate.isEmpty()) {
			SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
			try {

				startDate = format.parse(sDate);
				endDate = format.parse(eDate);
				endDate = calendarUtil.getConvertedDate(endDate, NumberConstants.TWENTY_THREE, NumberConstants.FIFTY_NINE,  NumberConstants.FIFTY_NINE);
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
		Page<EmployeeShiftDailyAssociation> page = getEmployeeShiftAssignedPage(id, employeeId, employeeName,
				department, shift, pageno, sortField, sortDir, startDate, endDate,org);
        List<EmployeeShiftDailyAssociation> employeeShiftList =  page.getContent();
		
		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir))?ApplicationConstants.DESC:ApplicationConstants.ASC;
		PaginationDto<EmployeeShiftDailyAssociation> dtoList = new PaginationDto<EmployeeShiftDailyAssociation>(employeeShiftList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}


	private Page<EmployeeShiftDailyAssociation> getEmployeeShiftAssignedPage(Long id, String employeeId,
			String employeeName, String department, String shift, int pageno, String sortField, String sortDir,
			Date startDate, Date endDate, String organization) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);
		
		Specification<EmployeeShiftDailyAssociation> idSpc = generalSpecification.longSpecification(id, ApplicationConstants.ID);
    	Specification<EmployeeShiftDailyAssociation> dateSpc = generalSpecification.dateSpecification(startDate,endDate,EmployeeShiftDailyAssociationConstants.DATE);
    	Specification<EmployeeShiftDailyAssociation> employeeIdSpc = generalSpecification.foreignKeyStringSpecification(employeeId,EmployeeShiftDailyAssociationConstants.EMPLOYEE,EmployeeConstants.EMPID);
    	Specification<EmployeeShiftDailyAssociation> employeeNameSpc = generalSpecification.foreignKeyStringSpecification(employeeName,EmployeeShiftDailyAssociationConstants.EMPLOYEE,ApplicationConstants.NAME); 
    	Specification<EmployeeShiftDailyAssociation> departmentSpc = generalSpecification.foreignKeyDoubleObjectStringSpecification(department,EmployeeShiftDailyAssociationConstants.EMPLOYEE,EmployeeConstants.DEPARTMENT,ApplicationConstants.NAME); 
    	Specification<EmployeeShiftDailyAssociation>  shiftSpc = generalSpecification.foreignKeyStringSpecification(shift, EmployeeShiftDailyAssociationConstants.SHIFT,ApplicationConstants.NAME);
    	Specification<EmployeeShiftDailyAssociation> orgSpc = generalSpecification.foreignKeyDoubleObjectStringSpecification(organization, "employee","organization", ApplicationConstants.NAME);
    	Page<EmployeeShiftDailyAssociation> page = employeeShiftDailyAssociationRepository.findAll(idSpc.and(dateSpc).and(employeeIdSpc).and(employeeNameSpc).and(departmentSpc).and(shiftSpc).and(orgSpc),pageable);
		return page;
	}


	@Override
	public ShiftRosterValidationDto storeEmployeeShiftData(MultipartFile file) {
		ShiftRosterValidationDto validationMsg=new ShiftRosterValidationDto();
		try {
			 validationMsg=excelEmployeeShiftImport.parseExcelFileEmployeeShiftList(file.getInputStream());
			 return validationMsg;
		} catch (IOException e) {
			throw new RuntimeException(MessageConstants.FAILED_MESSAGE + e.getMessage()+validationMsg);
		}
		
	}
	
	@Override
	public PaginationDto<EmployeeShiftDailyAssociation> searchByFieldUnassigned(Long id, String sDate, String eDate,
			String employeeId, String employeeName, String department, int pageno, String sortField,
			String sortDir) {

		Date startDate = null;
		Date endDate = null;
		SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
		if (!sDate.isEmpty() && !eDate.isEmpty()) {
			try {
				startDate = format.parse(sDate);
				endDate = format.parse(eDate);
				endDate = calendarUtil.getConvertedDate(endDate, NumberConstants.TWENTY_THREE, NumberConstants.FIFTY_NINE,  NumberConstants.FIFTY_NINE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		

		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = EmployeeShiftDailyAssociationConstants.DATE_STR;
		}
		Page<EmployeeShiftDailyAssociation> page = getEmployeeShiftUnassignedPage(id, employeeId, employeeName,
				department, pageno, sortField, sortDir, startDate, endDate);
    	 List<EmployeeShiftDailyAssociation> employeeUnassignedList =  page.getContent();
		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir))?ApplicationConstants.DESC:ApplicationConstants.ASC;
		PaginationDto<EmployeeShiftDailyAssociation> dtoList = new PaginationDto<EmployeeShiftDailyAssociation>(employeeUnassignedList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	
	}


	private Page<EmployeeShiftDailyAssociation> getEmployeeShiftUnassignedPage(Long id, String employeeId,
			String employeeName, String department, int pageno, String sortField, String sortDir, Date startDate,
			Date endDate) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);
		
		
		Specification<EmployeeShiftDailyAssociation> shiftIsNull = generalSpecification.isNullSpecification(EmployeeShiftDailyAssociationConstants.SHIFT);
		Specification<EmployeeShiftDailyAssociation> idSpc = generalSpecification.longSpecification(id, ApplicationConstants.ID);
    	Specification<EmployeeShiftDailyAssociation> dateSpc = generalSpecification.dateSpecification(startDate,endDate,EmployeeShiftDailyAssociationConstants.DATE);
    	Specification<EmployeeShiftDailyAssociation> employeeIdSpc = generalSpecification.foreignKeyStringSpecification(employeeId,EmployeeShiftDailyAssociationConstants.EMPLOYEE,EmployeeConstants.EMPID);
    	Specification<EmployeeShiftDailyAssociation> employeeNameSpc = generalSpecification.foreignKeyStringSpecification(employeeName,EmployeeShiftDailyAssociationConstants.EMPLOYEE,ApplicationConstants.NAME); 
    	Specification<EmployeeShiftDailyAssociation> departmentSpc = generalSpecification.foreignKeyDoubleObjectStringSpecification(department,EmployeeShiftDailyAssociationConstants.EMPLOYEE,EmployeeConstants.DEPARTMENT,ApplicationConstants.NAME); 
    	
    	Page<EmployeeShiftDailyAssociation> page = employeeShiftDailyAssociationRepository.findAll(idSpc.and(dateSpc).and(shiftIsNull).and(employeeIdSpc).and(departmentSpc).and(employeeNameSpc), pageable);
		return page;
	}
}
