package com.eikona.mata.service;

import org.springframework.web.multipart.MultipartFile;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.dto.ShiftRosterValidationDto;
import com.eikona.mata.entity.EmployeeShiftDailyAssociation;

public interface EmployeeShiftDailyAssociationService {
	PaginationDto<EmployeeShiftDailyAssociation> searchByField(Long id, String sDate, String eDate, String employeeId,String employeeName, String department,String shift,  int pageno, String sortField, String sortDir, String orgName);

	ShiftRosterValidationDto storeEmployeeShiftData(MultipartFile file);

	PaginationDto<EmployeeShiftDailyAssociation> searchByFieldUnassigned(Long id, String sDate, String eDate,
			String employeeId, String employeeName, String department, int pageno, String sortField,
			String sortDir);
}
