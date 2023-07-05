package com.eikona.mata.controller;

import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eikona.mata.dto.CountDto;
import com.eikona.mata.dto.DepartmentDto;
import com.eikona.mata.dto.OrganizationDto;
import com.eikona.mata.entity.Department;
import com.eikona.mata.entity.Transaction;
import com.eikona.mata.entity.User;
import com.eikona.mata.repository.DepartmentRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.repository.TransactionRepository;
import com.eikona.mata.repository.UserRepository;
import com.eikona.mata.util.ImageProcessingUtil;

@Controller
public class HomeController {
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private DepartmentRepository departmentRepository;
	
	@Autowired
	private ImageProcessingUtil imageProcessingUtil;
	
	@Autowired
	private UserRepository userRepository;
	
	@Value("${device.model}")
	private String deviceModel;
	
	@GetMapping(value={"/index"})
	public String index(Model model) {
		return "index";
	}
	
	@GetMapping(value={"/layout"})
	public String getLayout(Model model) {
		return "matalayout";
	}
	
	@GetMapping(value={"/home","/"})
	@PreAuthorize("hasAuthority('dashboard_view')")
	public String list(Model model,Principal principal) {
		User user=userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		CountDto countDto = new CountDto();
		long empTotalCount= employeeRepository.findAllByOrganizationAndIsDeletedFalse(user.getOrganization()).size();//count();
		long presentEmpTotalCount = transactionRepository.totalpresentCustom(dateFormat.format(date),user.getOrganization().getName());
		long noOfAppearencesTotalCount = transactionRepository.countAppearencesCustom(dateFormat.format(date),user.getOrganization().getName());
		long noMaskTotalCount = transactionRepository.totalUnMaskCustom(dateFormat.format(date),user.getOrganization().getName());
		
		countDto.setTransactions(noOfAppearencesTotalCount);
		countDto.setNoMask(noMaskTotalCount);
		countDto.setPresentEmployee(presentEmpTotalCount);
		countDto.setTotalEmployee(empTotalCount);
		
		model.addAttribute("countDto", countDto);
		
		return "dashboard";
	}
	
	@GetMapping(value = "/attendanceCount")
	@PreAuthorize("hasAuthority('dashboard_view')")
	public @ResponseBody CountDto getAttendanceCount(Principal principal) {
		User user=userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		CountDto countDto = new CountDto();
		long empTotalCount= employeeRepository.findAllByOrganizationAndIsDeletedFalse(user.getOrganization()).size();//count();
		long presentEmpTotalCount = transactionRepository.totalpresentCustom(dateFormat.format(date),user.getOrganization().getName());
		long noOfAppearencesTotalCount = transactionRepository.countAppearencesCustom(dateFormat.format(date),user.getOrganization().getName());
		long noMaskTotalCount = transactionRepository.totalUnMaskCustom(dateFormat.format(date),user.getOrganization().getName());
		
		countDto.setTransactions(noOfAppearencesTotalCount);
		countDto.setNoMask(noMaskTotalCount);
		countDto.setPresentEmployee(presentEmpTotalCount);
		countDto.setTotalEmployee(empTotalCount);
		
		return countDto;
	}
	
	@SuppressWarnings("unchecked")
	public @ResponseBody JSONArray departmenetWise(User user) {
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = dateFormat.format(new Date());
		
		JSONArray returnArray = new JSONArray();
		
		List<Department> departmentList = (List<Department>) departmentRepository.findAllByOrganizationAndIsDeletedFalse(user.getOrganization());
		List<DepartmentDto> presentDepartmentList = transactionRepository.countTotalEmployeeInDeptCustom(date,user.getOrganization().getName());
		List<DepartmentDto> totalDepartmentList = new ArrayList<>();
		
		for(Department department:departmentList) {
			
			DepartmentDto departmentDto= new DepartmentDto();
			Long totalDept = employeeRepository.countEmployeeDeptWiseCustom(department.getName(),user.getOrganization().getName());
			if(null == totalDept) {
				totalDept = 0l;
			}
			long present = 0l;
				
			for(DepartmentDto presentDepartment : presentDepartmentList) {
				if(department.getName().equalsIgnoreCase(presentDepartment.getDepartmentName()))
					present = presentDepartment.getPresentEmployee();
			}
				
			departmentDto.setDepartmentName(department.getName());
			departmentDto.setPresentEmployee(present);
			departmentDto.setTotalEmployee(totalDept);
			returnArray.add(departmentDto);
			totalDepartmentList.add(departmentDto);
		}
		
		return returnArray;
	}
	
	@SuppressWarnings("unchecked")
	public @ResponseBody  JSONObject barChart(User user) {
		
		Calendar endDate = Calendar.getInstance();
		endDate.setTime(new Date());
		endDate.add(Calendar.HOUR, 23);
		endDate.add(Calendar.MINUTE, 59);
		endDate.add(Calendar.SECOND, 59);
		
		Calendar startDate = Calendar.getInstance(); 
		startDate.setTime(new Date());
		startDate.add(Calendar.DATE, -6);
		startDate.add(Calendar.HOUR, 00);
		startDate.add(Calendar.MINUTE, 00);
		startDate.add(Calendar.SECOND, 00);
		
		List<OrganizationDto> objList = transactionRepository.findAllOrganizationCustom(startDate.getTime(), endDate.getTime(),user.getOrganization().getName());
	   
	    JSONObject returnObject = new JSONObject();
	    
	    JSONArray dataArray = new JSONArray();
	    
	    for (OrganizationDto companyDto : objList) {
	    	JSONArray currObject = new JSONArray();
	    	currObject.add(companyDto.getDateStr());
	    	currObject.add(companyDto.getTotal());
	    	dataArray.add(currObject);
	    	
		}
	     
	    returnObject.put("data",dataArray);
	    return returnObject;
	}
	
	@SuppressWarnings("unchecked")
	public @ResponseBody  JSONObject lineChart(User user) {
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = dateFormat.format(new Date());
		List<OrganizationDto> exceptionDataList = transactionRepository.countEmpPresentCustom(dateStr,user.getOrganization().getName());
		
		JSONObject returnObject = new JSONObject();
        
        JSONArray dataArray = new JSONArray();
        
        Long total = 0l;
        JSONObject dateObject = new JSONObject();
        for (OrganizationDto companyDto : exceptionDataList) {
        	
        	String currentHur = companyDto.getTimeStr().split(":")[0];
        	String key = currentHur+":00:00";
        	
        	if(dateObject.containsKey(key)) {
        		total = (Long)dateObject.get(key) + companyDto.getTotal();
        		dateObject.remove(key);
        		dateObject.put(key, total);
        	}else {
        		dateObject.put(key, companyDto.getTotal());
        	}
        }
        
        Set<String> keySet = dateObject.keySet();
        for (String key : keySet) {
        	JSONArray currArray = new JSONArray();
        	currArray.add(key);
        	currArray.add(dateObject.get(key));
        	dataArray.add(currArray);
        	
        }
        returnObject.put("data",dataArray);
		return returnObject;
	}
	
	@GetMapping(value="/api/pandemic/transaction")
	@PreAuthorize("hasAuthority('pandemic_dashboard_view')")
	public @ResponseBody List<Transaction> eventdata(Principal principal) {
		User user=userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = dateFormat.format(new Date());
		
		Pageable paging = PageRequest.of(0, 5, Sort.by("punchDate").descending());
		List<Transaction> transactionList = transactionRepository.getRealTimeDataCustom(dateStr,user.getOrganization().getName(), paging);
		
		List<Transaction> transactions = new ArrayList<Transaction>();
		for (Transaction trans : transactionList) {
			byte[] image = imageProcessingUtil.searchTransactionImage(trans);
			trans.setCropImageByte(image);
			transactions.add(trans);
		}
		return transactions;
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping(value="/api/common-chart")
	@PreAuthorize("hasAuthority('dashboard_view')")
	public @ResponseBody JSONObject commonChart(Principal principal) {
		User user=userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		
		JSONObject returnObject = new JSONObject();
		
		JSONObject barObject = barChart(user);
		JSONArray departmentArray = departmenetWise(user);
		JSONObject lineObject = lineChart(user);
		
		returnObject.put("barData", barObject);
		returnObject.put("department", departmentArray);
		returnObject.put("lineData", lineObject);
		return returnObject;
	}
}
