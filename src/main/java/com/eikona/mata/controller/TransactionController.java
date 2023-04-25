package com.eikona.mata.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.Transaction;
import com.eikona.mata.repository.TransactionRepository;
import com.eikona.mata.service.BranchService;
import com.eikona.mata.service.DepartmentService;
import com.eikona.mata.service.DesignationService;
import com.eikona.mata.service.DeviceService;
import com.eikona.mata.service.OrganizationService;
import com.eikona.mata.service.TransactionService;
import com.eikona.mata.util.ImageProcessingUtil;

@Controller
public class TransactionController {
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private DepartmentService departmentService;
	
	@Autowired
	private DesignationService designationService;
	
	@Autowired
	private BranchService branchService;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private ImageProcessingUtil imageProcessingUtil;
	
	@GetMapping("/transaction")
	@PreAuthorize("hasAuthority('transaction_view')")
	public String transactionList(Model model) {
		model.addAttribute("listDevice", deviceService.getAll());
		model.addAttribute("listOffice", branchService.getAll());
		model.addAttribute("listDepartment", departmentService.getAll());
		model.addAttribute("listDesignation", designationService.getAll());
		return "transaction/transaction_list";
	}
	
	@GetMapping("/add/employee-from-transaction/{id}")
	@PreAuthorize("hasAuthority('transaction_view')")
	public String editEmployee(@PathVariable(value = "id") long id, Model model) {

		model.addAttribute("listOrganization", organizationService.getAll());
		model.addAttribute("listDepartment", departmentService.getAll());
		model.addAttribute("listDesignation", designationService.getAll());
		model.addAttribute("listBranch", branchService.getAll());
		
		Transaction trans=transactionRepository.findById(id).get();

		Employee employee = new Employee();
		employee.setEmpId(trans.getEmployeeCode());
		employee.setPoi(trans.getPoiId());
		
		model.addAttribute("employee", employee);
		model.addAttribute("title", "Update Employee");
		return "employee/employee_new";
	}
	
	@RequestMapping(value = "/api/search/transaction", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('transaction_view')")
	public @ResponseBody PaginationDto<Transaction> searchVehicleLog(Long id, String sDate,String eDate, String employeeId, String employeeName, String office, String device, String department, String designation,
			int pageno, String sortField, String sortDir) {
		
		PaginationDto<Transaction> dtoList = transactionService.searchByField(id, sDate, eDate, employeeId, employeeName, office, device, department, designation, pageno, sortField, sortDir);
		
		List<Transaction> eventsList = dtoList.getData();
		List<Transaction> transactionList = new ArrayList<Transaction>();
		for (Transaction trans : eventsList) {
			byte[] image = imageProcessingUtil.searchTransactionImage(trans);
			trans.setCropImageByte(image);
			transactionList.add(trans);
		}
		dtoList.setData(transactionList);
		return dtoList;
	}
	
	
}
