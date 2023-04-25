package com.eikona.mata.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.DailyAttendance;
import com.eikona.mata.service.BranchService;
import com.eikona.mata.service.DailyAttendanceService;
import com.eikona.mata.service.DepartmentService;
import com.eikona.mata.service.DesignationService;
import com.eikona.mata.service.impl.model.ShiftWiseDailyReportServiceImpl;
import com.eikona.mata.util.ExportDailyAttendance;

@Controller
public class DailyAttendanceController {

	@Autowired
	private DailyAttendanceService dailyAttendanceService;

	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private DesignationService designationService;
	
	@Autowired
	private BranchService branchService;
	
	@Autowired
	private ShiftWiseDailyReportServiceImpl shiftWiseDailyReportServiceImpl;
	
	@Autowired
	private ExportDailyAttendance exportDailyAttendance;

	@GetMapping("/daily-reports")
	@PreAuthorize("hasAuthority('dailyreport_view')")
	public String dailyReportList(Model model) {
		model.addAttribute("listDesignation", designationService.getAll());
		model.addAttribute("listOffice", branchService.getAll());
		model.addAttribute("listDepartment", departmentService.getAll());
		return "reports/dailyAttendance";
	}

	@GetMapping("/generate/daily-attendance")
	@PreAuthorize("hasAuthority('dailyreport_generate')")
	public String generateDailyReportPage() {
		return "reports/generateDailyAttendance";
	}

	@GetMapping("/get/daily-attendance")
	@PreAuthorize("hasAuthority('dailyreport_generate')")
	public String generateDailyReportReportAction(String sDate,String eDate) {
		dailyAttendanceService.generateDailyAttendance(sDate, eDate);
		return "redirect:/daily-reports";
	}
	
	@GetMapping(value = "/generate/daily-attendance/shift")
	@PreAuthorize("hasAuthority('dailyreport_generate_shiftwise')")
	public String generateDailyReportByShiftPage(Model model) {
		return "reports/generateDailyAttendanceShiftWise";
	}
	
	@PostMapping(value = "/get/daily-attendance/shift")
	@PreAuthorize("hasAuthority('dailyreport_generate_shiftwise')")
	public  String getShiftWiseDailyReport(@RequestParam String sDate,@RequestParam String eDate)  {
		shiftWiseDailyReportServiceImpl.generateDailyAttendanceShiftWise(sDate, eDate);
		return "redirect:/daily-reports";
		
	}

	@GetMapping(value = "/api/dailyreport")
	@PreAuthorize("hasAuthority('dailyreport_view')")
	public @ResponseBody List<DailyAttendance> dailyReportDataTable(String sDate, String eDate) {
		List<DailyAttendance> dailyReport = dailyAttendanceService.dailyReportDataTable(sDate, eDate);
		return dailyReport;

	}

	
	@RequestMapping(value = "/api/search/daily-attendance", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('dailyreport_view')")
	public @ResponseBody PaginationDto<DailyAttendance> searchVehicleLog(Long id, String sDate,String eDate, String employeeId, String employeeName, String office, String department, String designation,
			String status,int pageno, String sortField, String sortDir) {
		
		PaginationDto<DailyAttendance> dtoList = dailyAttendanceService.searchByField(id, sDate, eDate, employeeId, employeeName, office, department, designation,status, pageno, sortField, sortDir);
		
		return dtoList;
	}
	
	@RequestMapping(value="/api/daily-attendance/export-to-file",method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('dailyreport_export')")
	public void exportToFile(HttpServletResponse response,String sDate, String eDate, String employeeName,
			String employeeId, String designation, String office, String department,String status,
			String flag) {
		 response.setContentType("application/octet-stream");
			DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
			String currentDateTime = dateFormat.format(new Date());
			String headerKey = "Content-Disposition";
			String headerValue = "attachment; filename=Daily_Attendance" + currentDateTime + "."+flag;
			response.setHeader(headerKey, headerValue);
		try {
			exportDailyAttendance.fileExportBySearchValue(response,sDate, eDate, employeeName,employeeId, designation, office, department,status,flag );
		} catch (Exception  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
