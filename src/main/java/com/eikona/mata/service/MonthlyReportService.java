package com.eikona.mata.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.eikona.mata.dto.MonthlyAttendanceDto;
import com.eikona.mata.dto.MonthlyReportDto;
import com.eikona.mata.dto.PaginationDto;



public interface MonthlyReportService {


	PaginationDto<MonthlyReportDto<MonthlyAttendanceDto>> searchByField(String date, String employeeId,
			String employeeName, String department, String designation, int pageno, String sortField, String sortDir);

	MonthlyReportDto<MonthlyAttendanceDto> calculateMonthlyReport(String date, String employeeId, String employeeName,
			String department, String designation);

	void excelGenerator(HttpServletResponse response, MonthlyReportDto<MonthlyAttendanceDto> monthlyDataList) throws FileNotFoundException, IOException;

}
