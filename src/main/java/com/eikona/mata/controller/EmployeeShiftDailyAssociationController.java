package com.eikona.mata.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.springframework.web.multipart.MultipartFile;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.dto.ShiftRosterValidationDto;
import com.eikona.mata.entity.EmployeeShiftDailyAssociation;
import com.eikona.mata.entity.User;
import com.eikona.mata.repository.UserRepository;
import com.eikona.mata.service.EmployeeShiftDailyAssociationService;
import com.eikona.mata.util.ExportEmployeeShiftDailyAssociation;

@Controller
public class EmployeeShiftDailyAssociationController {
	@Autowired
	private EmployeeShiftDailyAssociationService employeeShiftDailyAssociationService;
	
	@Autowired
	private ExportEmployeeShiftDailyAssociation exportEmployeeShiftDailyAssociation;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/employee-shift-daily-association")
	@PreAuthorize("hasAuthority('employee_roster_view')")
	public String employeeShiftDailyAssociation(Model model) {
		return "employeeshift/employee_shift_list";
	}
	
	@RequestMapping(value = "/api/search/employee-shift/daily-association", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('employee_roster_view')")
	public @ResponseBody PaginationDto<EmployeeShiftDailyAssociation> searchVehicleLog(Long id, String sDate,String eDate, String employeeId, String employeeName,String department, String shift,
			int pageno, String sortField, String sortDir, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String orgName = (null == user.getOrganization()? null: user.getOrganization().getName());
		
		PaginationDto<EmployeeShiftDailyAssociation> dtoList = employeeShiftDailyAssociationService.searchByField(id, sDate, eDate, employeeId, employeeName,department, shift,  pageno, sortField, sortDir,orgName);
		return dtoList;
	}
	
	@GetMapping("/import/employee-shift-excel")
	@PreAuthorize("hasAuthority('employee_roster_import')")
	public String importEmployeeShiftExcel() {
		return "multipartfile/employee_shift_upload";
	}

	@PostMapping("/upload/employee-shift/excel")
	@PreAuthorize("hasAuthority('employee_roster_import')")
	public String uploadEmployeeShiftExcel(@RequestParam("uploadfile") MultipartFile file, Model model) {
		ShiftRosterValidationDto validationMsg=new ShiftRosterValidationDto();
		try {
			validationMsg=employeeShiftDailyAssociationService.storeEmployeeShiftData(file);
			model.addAttribute("message", "File uploaded successfully!");
			model.addAttribute("validationMsg", validationMsg);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", "Fail! -> uploaded filename: " + file.getOriginalFilename()+":"+e.getMessage());
			model.addAttribute("validationMsg", validationMsg);
		}
		return "multipartfile/employee_shift_upload";
	} 
	
	@RequestMapping(value="/api/employee-shift/export-to-file",method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('employee_roster_export')")
	public void exportToFile(HttpServletResponse response,Long id, String sDate,String eDate, String employeeId, String employeeName,String department, String shift,
			String flag) {
		 response.setContentType("application/octet-stream");
			DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
			String currentDateTime = dateFormat.format(new Date());
			String headerKey = "Content-Disposition";
			String headerValue = "attachment; filename=Employee_Shift" + currentDateTime + "."+flag;
			response.setHeader(headerKey, headerValue);
		try {
			exportEmployeeShiftDailyAssociation.fileExportBySearchValue(response,id,sDate,eDate,employeeId, employeeName, department,shift,
					flag );
		} catch (Exception  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@GetMapping("/shift-roster-template-download")
	@PreAuthorize("hasAuthority('employee_roster_export')")
	public void downloadEmployeeListExcelTemplate(HttpServletResponse response) throws IOException {
        String filename = "/excel/Shift_Roster_import_template.xlsx";
        try {
        	
        	String headerKey = "Content-Disposition";
			String headerValue = "attachment; filename=Shift_Roster_import_template.xlsx";
			response.setHeader(headerKey, headerValue);
			FileInputStream inputStream = new FileInputStream(new File(filename));
			Workbook workBook = new XSSFWorkbook(inputStream);
			FileOutputStream fileOut = new FileOutputStream(filename);
			workBook.write(fileOut);
			ServletOutputStream outputStream = response.getOutputStream();
			workBook.write(outputStream);
			workBook.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
