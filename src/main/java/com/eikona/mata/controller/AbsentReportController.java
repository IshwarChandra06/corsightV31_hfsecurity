package com.eikona.mata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.DailyAttendance;
import com.eikona.mata.repository.DepartmentRepository;
import com.eikona.mata.repository.DesignationRepository;
import com.eikona.mata.service.AbsentReportService;

@Controller
public class AbsentReportController {

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private DesignationRepository designationRepository;

	@Autowired
	private AbsentReportService absentReportService;

	@GetMapping("/absent-report")
	@PreAuthorize("hasAuthority('absent_report_view')")
	public String absentReportListPage(Model model) {
		model.addAttribute("listDesignation", designationRepository.findAll());
		model.addAttribute("listDepartment", departmentRepository.findAll());
		return "reports/absent_report";
	}

	
	@RequestMapping(value = "/api/search/absent-report", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('absent_report_view')")
	public @ResponseBody PaginationDto<DailyAttendance> search(Long id, String sDate,String eDate, String employeeId, String employeeName, String office, String department, String designation,
			int pageno, String sortField, String sortDir) {
		
		PaginationDto<DailyAttendance> dtoList = absentReportService.search(id, sDate, eDate, employeeId, employeeName, office, department, designation, pageno, sortField, sortDir);
		
		return dtoList;
	}
}
