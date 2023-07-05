package com.eikona.mata.service;

import java.util.List;

import com.eikona.mata.dto.ExceptionSummaryDto;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.DailyAttendance;

public interface DailyAttendanceService {
	/**
	 * This function will return list of all employee daily report.
	 */
     List<DailyAttendance> findAll();
    
	List<DailyAttendance> generateDailyAttendance(String sDate, String eDate);

	List<DailyAttendance> dailyReportDataTable(String sDate, String eDate);

	List<ExceptionSummaryDto> exceptionDailyAttendanceSummaryData(String sDate, String eDate, String company);

	PaginationDto<DailyAttendance> searchPresent(Long id, String date, String organization, String employeeId, String employeeName,
			String office, String department, String designation, int pageno, String sortField, String sortDir);

	PaginationDto<DailyAttendance> searchInNoMask(Long id, String date, String organization, String employeeId, String employeeName,
			String office, String department, String designation, int pageno, String sortField, String sortDir);

	PaginationDto<DailyAttendance> searchOutNoMask(Long id, String date, String organization, String employeeId,
			String employeeName, String office, String department, String designation, int pageno, String sortField,
			String sortDir);

	PaginationDto<DailyAttendance> searchMissedOutPunch(Long id, String date, String organization, String employeeId,
			String employeeName, String office, String department, String designation, int pageno, String sortField, String sortDir);

	PaginationDto<DailyAttendance> searchOverTime(Long id, String date, String organization, String employeeId,
			String employeeName, String office, String department, String designation, int pageno, String sortField, String sortDir);

	PaginationDto<DailyAttendance> searchInAbnormalTemp(Long id, String date, String organization, String employeeId,
			String employeeName, String office, String department, String designation, int pageno, String sortField, String sortDir);

	PaginationDto<DailyAttendance> searchOutAbnormalTemp(Long id, String date, String organization, String employeeId,
			String employeeName, String office, String department, String designation, int pageno, String sortField, String sortDir);

	PaginationDto<DailyAttendance> searchLateComing(Long id, String date, String organization, String employeeId,
			String employeeName, String office, String department, String designation, int pageno, String sortField, String sortDir);

	PaginationDto<DailyAttendance> searchEarlyGoing(Long id, String date, String organization, String employeeId,
			String employeeName, String office, String department, String designation, int pageno, String sortField, String sortDir);

	PaginationDto<DailyAttendance> searchLessTime(Long id, String date, String organization, String employeeId,
			String employeeName, String office, String department, String designation, int pageno, String sortField, String sortDir);


	PaginationDto<DailyAttendance> searchByField(Long id, String sDate, String eDate, String employeeId,
			String employeeName, String office, String department, String designation,String status, int pageno, String sortField,
			String sortDir, String orgName);

	PaginationDto<ExceptionSummaryDto> search(String sDate, String eDate, String company, int pageno,
			String sortField, String sortDir);

}

