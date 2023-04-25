package com.eikona.mata.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eikona.mata.dto.MonthlyAttendanceDto;
import com.eikona.mata.dto.MonthlyReportDto;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.service.MonthlyReportService;


@Controller
public class MonthlyReportController {
	
	
	@Autowired
	private MonthlyReportService monthlyReportService;
	
	@GetMapping("/monthly/report")
	@PreAuthorize("hasAuthority('monthlyattendance_view')")
	public String getMonthlyReportPage(Model model) {
		return "monthlyreport/monthlydetailreport";
	}
	
	@GetMapping("/api/monthly/report/export-to-file")
	@PreAuthorize("hasAuthority('monthlyattendance_export')")
	public void excelGenerateIncapAttendance(HttpServletResponse response, String date,String employeeId,String employeeName,String designation,String department,String flag) {
		response.setContentType("application/octet-stream");
		DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
		String currentDateTime = dateFormat.format(new Date());
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Monthly_Attendance_" + currentDateTime + "."+flag;
		response.setHeader(headerKey, headerValue);
		
		try {
			
			MonthlyReportDto<MonthlyAttendanceDto> monthlyDataList = monthlyReportService.calculateMonthlyReport(date,employeeId,employeeName,department,designation);
			monthlyReportService.excelGenerator(response, monthlyDataList);
			
		} catch (Exception  e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/api/search/monthly-report", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('monthlyattendance_view')")
	public @ResponseBody PaginationDto<MonthlyReportDto<MonthlyAttendanceDto>> searchVehicleLog(String date, String employeeId, String employeeName,String department, String designation,
			int pageno, String sortField, String sortDir) {
		
		PaginationDto<MonthlyReportDto<MonthlyAttendanceDto>> dtoList = monthlyReportService.searchByField( date, employeeId, employeeName, department, designation, pageno, sortField, sortDir);
		
		return dtoList;
	}

}
