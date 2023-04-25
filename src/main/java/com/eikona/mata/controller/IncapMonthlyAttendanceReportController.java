package com.eikona.mata.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eikona.mata.dto.IncapMonthlyAttendanceDto;
import com.eikona.mata.dto.MonthlyReportDto;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.service.impl.incap.IncapMonthlyAttendanceReportServiceImpl;

@Controller
public class IncapMonthlyAttendanceReportController {
	@Autowired
	private IncapMonthlyAttendanceReportServiceImpl incapMonthlyAttendanceReportServiceImpl;
	
	@GetMapping("/monthly-attendance/report")
	@PreAuthorize("hasAuthority('monthlyattendance_view')")
	public String attendanceReportPage() {
		return "IncapReport/monthlyAttendanceReport";
	}
	
	
	@GetMapping("/api/monthly/attendance/export-to-file")
	@PreAuthorize("hasAuthority('monthlyattendance_export')")
	public void excelGenerateIncapAttendance(HttpServletResponse response, String sDate) {
		response.setContentType("application/octet-stream");
		DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
		String currentDateTime = dateFormat.format(new Date());
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Employee_Shift" + currentDateTime + ".xlsx";
		response.setHeader(headerKey, headerValue);
		
		try {
			
			MonthlyReportDto<IncapMonthlyAttendanceDto> ss = incapMonthlyAttendanceReportServiceImpl.calculateMonthlyReport(sDate);
			incapMonthlyAttendanceReportServiceImpl.excelGenerator(response, ss);
			
		} catch (Exception  e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/api/search/incap/monthly-attendance-report", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('monthlyattendance_view')")
	public @ResponseBody PaginationDto<MonthlyReportDto<IncapMonthlyAttendanceDto>> searchMonthlyReport(String date, int pageno, String sortField, String sortDir) {
		
		PaginationDto<MonthlyReportDto<IncapMonthlyAttendanceDto>> dtoList = incapMonthlyAttendanceReportServiceImpl.search(date, pageno, sortField, sortDir);
		
		return dtoList;
	}
}
