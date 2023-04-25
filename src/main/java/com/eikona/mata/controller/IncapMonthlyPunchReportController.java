package com.eikona.mata.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eikona.mata.dto.IncapMonthlyPunchReportDto;
import com.eikona.mata.service.impl.incap.IncapMonthlyPunchReportServiceImpl;

@Controller
public class IncapMonthlyPunchReportController {
	@Autowired
	private IncapMonthlyPunchReportServiceImpl incapMonthlyPunchReportServiceImpl;
	
	
	@GetMapping("/monthly/punch-record")
	@PreAuthorize("hasAuthority('monthlypunchrecord_view')")
	public String getMonthlyReportPage(Model model) {
		return "IncapReport/monthlyPunchReport";
	}
	
	@GetMapping("/api/monthly/punch-record/export-to-file")
	@PreAuthorize("hasAuthority('monthlypunchrecord_export')")
	public void generateMonthlyReport(HttpServletResponse response, String sDate) {
		response.setContentType("application/octet-stream");
		DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
		String currentDateTime = dateFormat.format(new Date());
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Monthly_Punch_Record" + currentDateTime + ".xls";
		response.setHeader(headerKey, headerValue);
		
		List<IncapMonthlyPunchReportDto> monthlyPunchRecordList=incapMonthlyPunchReportServiceImpl.calculateMonthlyReport(sDate);
		try {
			incapMonthlyPunchReportServiceImpl.excelGenerator(response, monthlyPunchRecordList, sDate);
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
