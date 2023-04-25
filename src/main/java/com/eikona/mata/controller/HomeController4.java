//package com.eikona.mata.controller;
//
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.Set;
//
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.eikona.mata.dto.CountDto;
//import com.eikona.mata.dto.DepartmentDto;
//import com.eikona.mata.dto.OrganizationDto;
//import com.eikona.mata.entity.Department;
//import com.eikona.mata.entity.Transaction;
//import com.eikona.mata.repository.DepartmentRepository;
//import com.eikona.mata.repository.EmployeeRepository;
//import com.eikona.mata.repository.TransactionRepository;
//import com.eikona.mata.util.ImageProcessingUtil;
//
//@Controller
//public class HomeController4 {
//	
//	@Autowired
//	private TransactionRepository transactionRepository;
//	
//	@Autowired
//	private EmployeeRepository employeeRepository;
//	
////	@Autowired
////	private OrganizationRepository organizationRepository;
//	
//	@Autowired
//	private DepartmentRepository departmentRepository;
//	
//	@Autowired
//	private ImageProcessingUtil imageProcessingUtil;
//	
//	@Value("${device.model}")
//	private String deviceModel;
//	
//	@GetMapping(value={"/index"})
//	public String index(Model model) {
//		return "index";
//	}
//	
//	@GetMapping(value={"/layout"})
//	public String getLayout(Model model) {
//		return "matalayout";
//	}
//	
//	@GetMapping(value={"/home","/"})
//	@PreAuthorize("hasAuthority('dashboard_view')")
//	public String list(Model model) {
//		
//		Date date = new Date();
//		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		
//		CountDto countDto = new CountDto();
//		long empTotalCount= employeeRepository.findAllByIsDeletedFalse().size();//count();
////		long highTempTotalCount = 0;
//		long presentEmpTotalCount = transactionRepository.totalpresentCustom(dateFormat.format(date));
//		long noOfAppearencesTotalCount = transactionRepository.countAppearencesCustom(dateFormat.format(date));
//		long noMaskTotalCount = transactionRepository.totalUnMaskCustom(dateFormat.format(date));
//		
//		countDto.setTransactions(noOfAppearencesTotalCount);
//		countDto.setNoMask(noMaskTotalCount);
//		countDto.setPresentEmployee(presentEmpTotalCount);
//		countDto.setTotalEmployee(empTotalCount);
//		
//		model.addAttribute("countDto", countDto);
//		
//		return "dashboard";
//	}
//	
//	@GetMapping(value = "/attendanceCount")
//	@PreAuthorize("hasAuthority('dashboard_view')")
//	public @ResponseBody CountDto getAttendanceCount(){
//		
//		Date date = new Date();
//		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		
//		CountDto countDto = new CountDto();
//		long empTotalCount= employeeRepository.findAllByIsDeletedFalse().size();
////		long highTempTotalCount = 0;
//		long presentEmpTotalCount = transactionRepository.totalpresentCustom(dateFormat.format(date));
//		long noOfAppearencesTotalCount = transactionRepository.countAppearencesCustom(dateFormat.format(date));
//		long noMaskTotalCount = transactionRepository.totalUnMaskCustom(dateFormat.format(date));
//		
//		countDto.setTransactions(noOfAppearencesTotalCount);
//		countDto.setNoMask(noMaskTotalCount);
//		countDto.setPresentEmployee(presentEmpTotalCount);
//		countDto.setTotalEmployee(empTotalCount);
//		
//		return countDto;
//	}
//	
//	@SuppressWarnings("unchecked")
//	public @ResponseBody JSONArray departmenetWise() {
//		
//		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		String date = dateFormat.format(new Date());
//		
//		JSONArray returnArray = new JSONArray();
//		
//		List<Department> departmentList = (List<Department>) departmentRepository.findAllByIsDeletedFalse();
//		List<DepartmentDto> presentDepartmentList = transactionRepository.countTotalEmployeeInDeptCustom(date);
//		List<DepartmentDto> totalDepartmentList = new ArrayList<>();
//		
//		for(Department department:departmentList) {
//			
//			DepartmentDto departmentDto= new DepartmentDto();
//			Long totalDept = employeeRepository.countEmployeeDeptWiseCustom(department.getName());
//			if(null == totalDept) {
//				totalDept = 0l;
//			}
//			long present = 0l;
//				
//			for(DepartmentDto presentDepartment : presentDepartmentList) {
//				if(department.getName().equalsIgnoreCase(presentDepartment.getDepartmentName()))
//					present = presentDepartment.getPresentEmployee();
//			}
//				
//			departmentDto.setDepartmentName(department.getName());
//			departmentDto.setPresentEmployee(present);
//			departmentDto.setTotalEmployee(totalDept);
//			returnArray.add(departmentDto);
//			totalDepartmentList.add(departmentDto);
//		}
//		
//		return returnArray;
//	}
//	
//	@SuppressWarnings("unchecked")
//	public @ResponseBody  JSONObject barChart() {
//		
////		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
////		String endDateStr = dateFormat.format(new Date());
//		Calendar endDateStr = Calendar.getInstance();
//		endDateStr.setTime(new Date());
//		endDateStr.add(Calendar.HOUR, 23);
//		endDateStr.add(Calendar.MINUTE, 59);
//		endDateStr.add(Calendar.SECOND, 59);
//		
//		Calendar startDate = Calendar.getInstance(); 
//		startDate.setTime(new Date());
//		startDate.add(Calendar.DATE, -6);
//		startDate.add(Calendar.HOUR, 00);
//		startDate.add(Calendar.MINUTE, 00);
//		startDate.add(Calendar.SECOND, 00);
////		String startDateStr = dateFormat.format(startDate.getTime());
////		
////		JSONArray testArray = new JSONArray();
////		List<Organization> contractorList = (List<Organization>) organizationRepository.findAll();
//		
//		List<OrganizationDto> objList = transactionRepository.findAllOrganizationCustom(startDate.getTime(), endDateStr.getTime());
//	   
//	    JSONObject returnObject = new JSONObject();
//	    JSONObject jsonObject = new JSONObject();
//	    JSONArray headerArray = new JSONArray();
//	    JSONArray dateArray = new JSONArray();
//	    
//	    JSONArray dataArray = new JSONArray();
//	    
//	    for (OrganizationDto companyDto : objList) {
//	    	
//	    	if(!headerArray.toString().contains(companyDto.getCompany())) {
//	    		headerArray.add(companyDto.getCompany());
//	    	}
//	    	
//	    	if(!dateArray.toString().contains(companyDto.getAtt_date())) {
//	    		dateArray.add(companyDto.getAtt_date());
//	    	}
//	    	
//	    	JSONObject dateObject = new JSONObject();
//	    	if(jsonObject.containsKey(companyDto.getAtt_date())){
//	    		dateObject = (JSONObject)jsonObject.get(companyDto.getAtt_date());
//	    	}
//	    	
//	    	dateObject.put(companyDto.getCompany(), companyDto.getTotal());
//	    	
//	    	jsonObject.put(companyDto.getAtt_date(), dateObject);
//		}
//	     
//	    for (Object key : dateArray) {
//	    	JSONObject currObject =  (JSONObject)jsonObject.get(key);
//	    	JSONArray currArray = new JSONArray();
//	    	currArray.add(key);
//	    	for (Object object : headerArray) {
//	    		if(currObject.containsKey(object)) {
//	    			currArray.add(currObject.get(object));
//	    		}else {
//	    			currArray.add(0);
//	    		}
//			}
//	    	
//	    	dataArray.add(currArray);
//		}
//	    
//	    returnObject.put("header",headerArray);
//	    returnObject.put("data",dataArray);
//	    
//	    return returnObject;
//	}
//	
//	@SuppressWarnings("unchecked")
//	public @ResponseBody  JSONObject lineChart(){
//		
//		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		String dateStr = dateFormat.format(new Date());
//		List<OrganizationDto> exceptionDataList = transactionRepository.countEmpPresentCustom("2022-07-21");
//		
//		JSONObject returnObject = new JSONObject();
//        JSONObject jsonObject = new JSONObject();
//        JSONArray headerArray = new JSONArray();
//        
//        JSONArray dataArray = new JSONArray();
//        
//        String previousHur = null;
//        
//        Long firstHalfTotal = 0l;
//        Long secondHalfTotal = 0l;
//        int firstHalfFlag = 0;
//        int secondHalfFlag = 0;
//        JSONObject firstHalfDateObject = new JSONObject();
//        JSONObject secondHalfDateObject = new JSONObject();
//        int c=0;
//        for (OrganizationDto companyDto : exceptionDataList) {
//        	c++;
//        	if(!headerArray.toString().contains(companyDto.getCompany())) {
//        		headerArray.add(companyDto.getCompany());
//        	}
//        	
//        	String[] timeArray = companyDto.getDateTime().split(":");
//        	
//        	String currentHur = timeArray[0];
//        	int currentMin = Integer.valueOf(timeArray[1]);
//        	if((null == previousHur) || (previousHur.equalsIgnoreCase(currentHur))) {
//        		if(currentMin<30) {
//        			if(jsonObject.containsKey(companyDto.getDateTime())){
//                		firstHalfDateObject = (JSONObject)jsonObject.get(previousHur+":29:00");
//                	}
//        			firstHalfTotal += companyDto.getTotal();
//        			firstHalfFlag =99;
//        		}else {
//        			if(jsonObject.containsKey(companyDto.getDateTime())){
//        				secondHalfDateObject = (JSONObject)jsonObject.get(previousHur+":60:00");
//                	}
//        			secondHalfTotal += companyDto.getTotal();
//        			secondHalfFlag =99;
//        		}
//        			
//            	previousHur = currentHur;
//            	continue;
//        	}
//        	
//        	if(firstHalfFlag !=0) {
//        		firstHalfDateObject.put(companyDto.getCompany(), firstHalfTotal);
//        		jsonObject.put(previousHur+":29:00", firstHalfDateObject);
//        	}
//        	
//        	if(secondHalfFlag !=0) {
//        		secondHalfDateObject.put(companyDto.getCompany(), secondHalfTotal);
//        		jsonObject.put(previousHur+":60:00", secondHalfDateObject);
//        	}
//        	
//        	firstHalfFlag = 0;
//            secondHalfFlag = 0;
//            
//        	if(currentMin<30) {
//        		firstHalfTotal = companyDto.getTotal();
//        		firstHalfFlag = 99;
//        	}	
//        	else {
//        		secondHalfTotal = companyDto.getTotal();
//        		secondHalfFlag = 99;
//        	}
//        		
//        	previousHur = currentHur;
//        	firstHalfDateObject = new JSONObject();
//        	secondHalfDateObject = new JSONObject();
//        	
//        	
//		}
//        
//        if(exceptionDataList.size()==1) {
//        	OrganizationDto companyDtoObj = exceptionDataList.get(1);
//        	
//        	if(firstHalfFlag !=0) {
//        		firstHalfDateObject.put(companyDtoObj.getCompany(), firstHalfTotal);
//        		jsonObject.put(previousHur+":29:00", firstHalfDateObject);
//        	}
//        	
//        	if(secondHalfFlag !=0) {
//        		secondHalfDateObject.put(companyDtoObj.getCompany(), firstHalfTotal);
//        		jsonObject.put(previousHur+":60:00", secondHalfDateObject);
//        	}
//        }else if(exceptionDataList.size()>2) {
//        	OrganizationDto companyDtoObj = exceptionDataList.get(exceptionDataList.size()-2);
//            String currentHur = companyDtoObj.getDateTime().split(":")[0];
//            if(previousHur.equalsIgnoreCase(currentHur)) {
//            	if(firstHalfFlag !=0) {
//            		firstHalfDateObject.put(companyDtoObj.getCompany(), firstHalfTotal);
//            		jsonObject.put(previousHur+":29:00", firstHalfDateObject);
//            	}
//            	
//            	if(secondHalfFlag !=0) {
//            		secondHalfDateObject.put(companyDtoObj.getCompany(), firstHalfTotal);
//            		jsonObject.put(previousHur+":60:00", secondHalfDateObject);
//            	}
//            }
//        }
//      
//        Set<String> keySet = jsonObject.keySet();
//        
//        for (String key : keySet) {
//        	JSONObject currObject =  (JSONObject)jsonObject.get(key);
//        	JSONArray currArray = new JSONArray();
//        	currArray.add(key);
//        	for (Object object : headerArray) {
//        		if(currObject.containsKey(object)) {
//        			currArray.add(currObject.get(object));
//        		}else {
//        			currArray.add(0);
//        		}
//			}
//        	
//        	dataArray.add(currArray);
//		}
//        
//	        
//        returnObject.put("header",headerArray);
//        returnObject.put("data",dataArray);
//		return returnObject;
//	}
//	
//	@GetMapping(value="/api/pandemic/transaction")
//	@PreAuthorize("hasAuthority('pandemic_dashboard_view')")
//	public @ResponseBody List<Transaction> eventdata() {
//		
//		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		String dateStr = dateFormat.format(new Date());
//		
//		Pageable paging = PageRequest.of(0, 5, Sort.by("punchDate").descending());
//		List<Transaction> transactionList = transactionRepository.getRealTimeDataCustom(dateStr, paging);
//		
//		List<Transaction> transactions = new ArrayList<Transaction>();
//		for (Transaction trans : transactionList) {
//			byte[] image = imageProcessingUtil.searchTransactionImage(trans);
//			trans.setCropImageByte(image);
//			transactions.add(trans);
//		}
//		return transactions;
//	}
//	
//	@SuppressWarnings("unchecked")
//	@GetMapping(value="/api/common-chart")
//	@PreAuthorize("hasAuthority('dashboard_view')")
//	public @ResponseBody JSONObject commonChart() {
//		
//		JSONObject returnObject = new JSONObject();
//		
//		JSONObject barObject = barChart();
//		JSONArray departmentArray = departmenetWise();
//		JSONObject lineObject = lineChart();
//		
//		returnObject.put("barData", barObject);
//		returnObject.put("department", departmentArray);
//		returnObject.put("lineData", lineObject);
//		return returnObject;
//	}
//}
