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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.eikona.mata.service.BranchService;
import com.eikona.mata.service.DepartmentService;
import com.eikona.mata.service.DesignationService;
import com.eikona.mata.service.impl.incap.IncapShiftWiseDailyReportServiceImpl;
import com.eikona.mata.util.IncapExportDailyAttendance;

@Controller
public class IncapDailyAttendanceController {
	
	@Autowired
	private IncapShiftWiseDailyReportServiceImpl incapDailyReportServiceImpl;
	
	
	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private DesignationService designationService;

	@Autowired
	private BranchService branchService;
	
	@Autowired
	private IncapExportDailyAttendance exportDailyAttendance;
	
	@GetMapping("/incap/daily-reports")
	@PreAuthorize("hasAuthority('dailyreport_view')")
	public String dailyReportList(Model model) {
		model.addAttribute("listDesignation", designationService.getAll());
		model.addAttribute("listOffice", branchService.getAll());
		model.addAttribute("listDepartment", departmentService.getAll());
		return "IncapReport/dailyAttendance";
	}
	
	@GetMapping(value = "/incap/generate/daily-attendance/shift")
	@PreAuthorize("hasAuthority('dailyreport_generate_shiftwise')")
	public String generateDailyReportByShiftPage(Model model) {
		return "IncapReport/generateDailyAttendanceShiftWise";
	}
	
	@PostMapping(value = "/incap/get/daily-attendance/shift")
	@PreAuthorize("hasAuthority('dailyreport_generate_shiftwise')")
	public  String getShiftWiseDailyReport(@RequestParam String sDate,@RequestParam String eDate)  {
		incapDailyReportServiceImpl.generateDailyAttendanceShiftWise(sDate, eDate);
		return "redirect:/incap/daily-reports";
		
	}
	@RequestMapping(value="/api/incap/daily-attendance/export-to-file",method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('dailyreport_export')")
	public void exportToFile(HttpServletResponse response,String sDate, String eDate, String employeeName,
			String employeeId, String designation, String office, String department,
			String flag) {
		 response.setContentType("application/octet-stream");
			DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
			String currentDateTime = dateFormat.format(new Date());
			String headerKey = "Content-Disposition";
			String headerValue = "attachment; filename=Daily_Attendance" + currentDateTime + "."+flag;
			response.setHeader(headerKey, headerValue);
		try {
			exportDailyAttendance.fileExportBySearchValue(response,sDate, eDate, employeeName,employeeId, designation, office, department,flag );
		} catch (Exception  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
