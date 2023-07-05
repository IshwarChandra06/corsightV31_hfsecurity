package com.eikona.mata.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.DailyAttendance;
import com.eikona.mata.entity.User;
import com.eikona.mata.repository.UserRepository;
import com.eikona.mata.service.AbsentReportService;

@Controller
public class AbsentReportController {

	@Autowired
	private AbsentReportService absentReportService;
	
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/absent-report")
	@PreAuthorize("hasAuthority('absent_report_view')")
	public String absentReportListPage() {
		return "reports/absent_report";
	}

	
	@RequestMapping(value = "/api/search/absent-report", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('absent_report_view')")
	public @ResponseBody PaginationDto<DailyAttendance> search(Long id, String sDate,String eDate, String employeeId, String employeeName, String office, String department, String designation,
			int pageno, String sortField, String sortDir, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String orgName = (null == user.getOrganization()? null: user.getOrganization().getName());
		
		PaginationDto<DailyAttendance> dtoList = absentReportService.search(id, sDate, eDate, employeeId, employeeName, office, department, designation, pageno, sortField, sortDir,orgName);
		
		return dtoList;
	}
}
