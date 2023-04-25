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

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.EmployeeShiftDailyAssociation;
import com.eikona.mata.service.EmployeeShiftDailyAssociationService;
import com.eikona.mata.util.ExportEmployeeShiftDailyAssociation;
@Controller
public class EmployeeShiftUnassignedController {
	
	@Autowired
	private ExportEmployeeShiftDailyAssociation exportEmployeeShiftDailyAssociation;
	
	@Autowired
	private EmployeeShiftDailyAssociationService employeeShiftDailyAssociationService;
	
	@GetMapping("/employee-shift-daily-association/unassigned")
	@PreAuthorize("hasAuthority('employee_shift_unassigned_view')")
	public String employeeShiftDailyAssociationUnassigned(Model model) {
		return "employeeshift/employee_shift_unassigned";
	}
	
	@RequestMapping(value = "/api/search/employee-shift/daily-association/unassigned", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('employee_shift_unassigned_view')")
	public @ResponseBody PaginationDto<EmployeeShiftDailyAssociation> searchUnassigned(Long id, String sDate,String eDate, String employeeId, String employeeName,String department,
			int pageno, String sortField, String sortDir) {
		
		PaginationDto<EmployeeShiftDailyAssociation> dtoList = employeeShiftDailyAssociationService.searchByFieldUnassigned(id, sDate, eDate, employeeId, employeeName,department,   pageno, sortField, sortDir);
		return dtoList;
	}
	
	@RequestMapping(value="/api/employee-shift/unassigned/export-to-file",method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('employee_shift_unassigned_export')")
	public void exportToFile(HttpServletResponse response,Long id, String sDate,String eDate, String employeeId, String employeeName,String department, 
			String flag) {
		 response.setContentType("application/octet-stream");
			DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
			String currentDateTime = dateFormat.format(new Date());
			String headerKey = "Content-Disposition";
			String headerValue = "attachment; filename=Employee_Shift_Unassigned" + currentDateTime + "."+flag;
			response.setHeader(headerKey, headerValue);
		try {
			exportEmployeeShiftDailyAssociation.employeeShiftUnassignedExportBySearchValue(response,id,sDate,eDate,employeeId, employeeName, department,
					flag );
		} catch (Exception  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
