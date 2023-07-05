package com.eikona.mata.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.MessageConstants;
import com.eikona.mata.dto.EmployeeToDeviceAssociationDto;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Area;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.Organization;
import com.eikona.mata.entity.User;
import com.eikona.mata.repository.AreaRepository;
import com.eikona.mata.repository.BranchRepository;
import com.eikona.mata.repository.DepartmentRepository;
import com.eikona.mata.repository.DesignationRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.repository.OrganizationRepository;
import com.eikona.mata.repository.UserRepository;
import com.eikona.mata.service.AreaService;
import com.eikona.mata.service.DeviceService;
import com.eikona.mata.service.DeviceSyncAbstractService;
import com.eikona.mata.service.EmployeeService;
import com.eikona.mata.sync.EmployeeSync;
import com.eikona.mata.util.ImageProcessingUtil;
import com.eikona.mata.util.RequestExecutionUtil;

@Controller
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private BranchRepository branchRepository;
	
	@Autowired
	private AreaRepository areaRepository;
	
	@Autowired
	private AreaService areaService;

	@Autowired
	private DesignationRepository designationRepository;

	@Autowired
	private DeviceService deviceService;

	@Autowired
	private ImageProcessingUtil imageProcessingUtil;
	
	@Autowired
	private EmployeeSync employeeSync;
	
	@Value("${cosec.employee.import}")
	private boolean cosecEnabled;
	
	@Autowired
	@Qualifier("unvDeviceService")
	private DeviceSyncAbstractService<Long> unvBewardDeviceService;
	
	@Autowired
	@Qualifier("corsightDeviceService")
	private DeviceSyncAbstractService<String> corsightDeviceService;
	
	@Value("${corsight.enabled}")
	private boolean corsightEnabled;
	
	
	@Value("${corsight.host.url}")
    private String corsightHost;
	
	@Value("${corsight.poi.port}")
    private String poiPort;
	
	
	@GetMapping("/employee")
	@PreAuthorize("hasAuthority('employee_view')")
	public String employeeList(Model model) {
		model.addAttribute("corsightEnabled",corsightEnabled);
		model.addAttribute("cosecEnabled",cosecEnabled);
		return "employee/employee_list";
	}
	
	@GetMapping("/update-area")
	public String updateArea(Model model) {
		List<Employee>empList=employeeRepository.findAllByIsDeletedFalse();
//		Area area1=areaService.getById(1l);
		Area area2=areaService.getById(2l);
		Area area3=areaService.getById(3l);
		Area area4=areaService.getById(4l);
//		Area area5=areaService.getById(5l);
//		Area area6=areaService.getById(6l);
//		Area area7=areaService.getById(7l);
//		Area area8=areaService.getById(8l);
//		Area area9=areaService.getById(9l);
//		Area area10=areaService.getById(10l);
		List<Area> areaList=new ArrayList<Area>();
//		areaList.add(area1);
		areaList.add(area2);
		areaList.add(area3);
		areaList.add(area4);
//		areaList.add(area5);
//		areaList.add(area6);
//		areaList.add(area7);
//		areaList.add(area8);
//		areaList.add(area9);
//		areaList.add(area10);
		for(Employee emp:empList) {
			emp.setArea(areaList);
			employeeRepository.save(emp);
		}
		
		return "employee/employee_list";
	}
	
	// Import Employee
	@GetMapping("/import/cosec/employee-list")
	@PreAuthorize("hasAuthority('employee_import')")
	public String importCosecEmployeeList() {
		return "multipartfile/uploadCosecEmployeeList";
	}

	@PostMapping("/upload/cosec/employee-list/excel")
	@PreAuthorize("hasAuthority('employee_import')")
	public String uploadCosecEmployeeList(@RequestParam("uploadfile") MultipartFile file, Model model, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String message = employeeService.storeCosecEmployeeList(file,user);
		model.addAttribute("message", message);
		return "multipartfile/uploadCosecEmployeeList";
	}

	// Import Employee
	@GetMapping("/import/employee-list")
	@PreAuthorize("hasAuthority('employee_import')")
	public String importEmployeeList() {
		return "multipartfile/uploadEmployeeList";
	}

	@PostMapping("/upload/employee-list/excel")
	@PreAuthorize("hasAuthority('employee_import')")
	public String uploadEmployeeList(@RequestParam("uploadfile") MultipartFile file, Model model, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String message = employeeService.storeEmployeeList(file,user);
		model.addAttribute("message", message);
		return "multipartfile/uploadEmployeeList";
	}

	@GetMapping("/employee/new")
	@PreAuthorize("hasAuthority('employee_create')")
	public String newEmployee(Model model, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		List<Organization> organizationList = null;
		if(null == user.getOrganization()) {
			organizationList = (List<Organization>) organizationRepository.findAll();
		}else {
			organizationList = organizationRepository.findByIdAndIsDeletedFalse(user.getOrganization().getId());
		}
		
		model.addAttribute("listOrganization", organizationList);
		model.addAttribute("listDepartment", departmentRepository.findByOrganizationAndIsDeletedFalse(user.getOrganization()));
		model.addAttribute("listDesignation", designationRepository.findByOrganizationAndIsDeletedFalse(user.getOrganization()));
		model.addAttribute("listBranch", branchRepository.findByOrganizationAndIsDeletedFalse(user.getOrganization()));
		model.addAttribute("listArea", areaRepository.findByOrganizationAndIsDeletedFalse(user.getOrganization()));
		Employee employee = new Employee();
		model.addAttribute("employee", employee);
		model.addAttribute("title", "New Employee");
		return "employee/employee_new";
	}

//	@GetMapping("/employee/gallery/{id}")
//	public String gallery(@PathVariable(value = "id") long id, Model model) {
//		Employee employee = employeeService.getById(id);
//		List<Byte[]> images = employee.getImage();
//		List<String> encodedImageList = new ArrayList<>();
//		for (Byte[] imageBytes : images) {
//			byte[] bytes = ArrayUtils.toPrimitive(imageBytes);
//			String encodedImage = Base64.encodeBase64String(bytes);
//			encodedImageList.add(encodedImage);
//		}
//		model.addAttribute("listImages", encodedImageList);
//		model.addAttribute("employee", employee);
//		return "employee/employee_galley";
//	}
	
	@GetMapping("/employee-sync")
	@PreAuthorize("hasAuthority('employee_sync')")
	public String syncDevice(Model model,Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
	
		String message = null;
		try {
			message = syncEmployeeList(user.getOrganization());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("message",message);
		model.addAttribute("corsightEnabled",true);
		model.addAttribute("cosecEnabled",cosecEnabled);
		return "employee/employee_list";
	}
	
	private String syncEmployeeList(Organization organization) {
		try {
			String afterId = ApplicationConstants.DELIMITER_EMPTY;
			employeeSync.getEmployeeList(afterId,organization);
			return MessageConstants.SYNC_SUCCESSFULLY;
		} catch (Exception e) {
			e.printStackTrace();
			return MessageConstants.SYNC_FAILED;
		}
	}

	@PostMapping("/employee/add")
	@PreAuthorize("hasAnyAuthority('employee_create','employee_update')")
	public String saveEmployee(@RequestParam("files") MultipartFile file, @ModelAttribute("employee") Employee employee,
			Model model, @Valid Employee emp, Errors errors, String title, Principal principal) {
		if (errors.hasErrors()) {
			User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
			List<Organization> organizationList = null;
			if(null == user.getOrganization()) {
				organizationList = (List<Organization>) organizationRepository.findAll();
			}else {
				organizationList = organizationRepository.findByIdAndIsDeletedFalse(user.getOrganization().getId());
			}
			
			model.addAttribute("listOrganization", organizationList);
			model.addAttribute("listDepartment", departmentRepository.findByOrganizationAndIsDeletedFalse(user.getOrganization()));
			model.addAttribute("listDesignation", designationRepository.findByOrganizationAndIsDeletedFalse(user.getOrganization()));
			model.addAttribute("listBranch", branchRepository.findByOrganizationAndIsDeletedFalse(user.getOrganization()));
			model.addAttribute("title", title);
			return "employee/employee_new";
		} else {
			if (null == employee.getId()) {
				employee=employeeService.save(employee, principal);
				if (null != file && !file.getOriginalFilename().isEmpty()) {
					imageProcessingUtil.saveEmployeeImageWhileEnrolling(file,employee);
				}

			} else {
				Employee employeeObj = employeeService.getById(employee.getId());
				employee.setCreatedBy(employeeObj.getCreatedBy());
				employee.setCreatedDate(employeeObj.getCreatedDate());
				employee=employeeService.save(employee, principal);
				if (null != file && !file.getOriginalFilename().isEmpty()) {
					imageProcessingUtil.saveEmployeeImageWhileEnrolling(file,employee);
				}
				
			}

			return "redirect:/employee";
		}
	}

//	private Byte[] convertToBytes(MultipartFile file) throws IOException  {
//        Byte[] byteObjects = new Byte[file.getBytes().length];
//        int i = 0;
//        for (Byte b : file.getBytes()) {
//            byteObjects[i++] = b;
//        }
//        return byteObjects;
//    }

	@GetMapping("/employee/edit/{id}")
	@PreAuthorize("hasAuthority('employee_update')")
	public String editEmployee(@PathVariable(value = "id") long id, Model model, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		List<Organization> organizationList = null;
		if(null == user.getOrganization()) {
			organizationList = (List<Organization>) organizationRepository.findAll();
		}else {
			organizationList = organizationRepository.findByIdAndIsDeletedFalse(user.getOrganization().getId());
		}
		
		model.addAttribute("listOrganization", organizationList);
		model.addAttribute("listDepartment", departmentRepository.findByOrganizationAndIsDeletedFalse(user.getOrganization()));
		model.addAttribute("listDesignation", designationRepository.findByOrganizationAndIsDeletedFalse(user.getOrganization()));
		model.addAttribute("listBranch", branchRepository.findByOrganizationAndIsDeletedFalse(user.getOrganization()));

		Employee employee = employeeService.getById(id);
		model.addAttribute("employee", employee);
		model.addAttribute("title", "Update Employee");
		return "employee/employee_new";
	}

	@GetMapping("/employee/delete/{id}")
	@PreAuthorize("hasAuthority('employee_delete')")
	public String deleteEmployee(@PathVariable(value = "id") long id, Principal principal) {
		this.employeeService.deleteById(id, principal);
		return "redirect:/employee";
	}

//	@GetMapping(value = "/api/employee")
//	@PreAuthorize("hasAuthority('employee_view')")
//	public @ResponseBody DataTablesOutput<Employee> employeeDatatable(@Valid DataTablesInput input) {
//		DataTablesOutput<Employee> empData = employeeService.getAllFrom(input);
//		List<Employee> empList = empData.getData();
//		List<Employee> empListForDatatable = new ArrayList<Employee>();
//		for (Employee employee : empList) {
//			byte[] image = imageProcessingUtil.searchImage(employee.getId());
//			employee.setCropImage(image);
//			empListForDatatable.add(employee);
//		}
//
//		empData.setData(empListForDatatable);
//		return empData;
//	}
	
	@RequestMapping(value = "/api/search/employee", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('employee_view')")
	public @ResponseBody PaginationDto<Employee> searchEmployee(Long id, String name,String empId,String branch,String department,String designation, int pageno, String sortField, String sortDir, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String orgName = (null == user.getOrganization()? null: user.getOrganization().getName());
		
		PaginationDto<Employee> dtoList = employeeService.searchByField(id, name,empId,branch,department,designation,pageno, sortField, sortDir,orgName);
		return dtoList;
	}

	@GetMapping("/employee-to-area/association/{id}")
	@PreAuthorize("hasAuthority('employee_area_association')")
	public String employeeAreaAssociation(@PathVariable(value = "id") long id, Model model,Principal principal) {
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		List<Area> areaList = areaRepository.findByOrganizationAndIsDeletedFalse(user.getOrganization());
		model.addAttribute("listArea", areaList);
		Employee employee = employeeService.getById(id);
		model.addAttribute("employee", employee);
		model.addAttribute("id", id);
		return "employee/employee_area";
	}

	@PostMapping("/employee-to-area/association/save")
	@PreAuthorize("hasAuthority('employee_area_association')")
	public String saveEmployeeAreaAssociation(@ModelAttribute("employee") Employee employee, Long id,
			Principal principal) {
		
		employeeService.saveEmployeeAreaAssociation(employee, id, principal);
		return "redirect:/employee";

	}

	@GetMapping("/employee-to-device/association/{id}")
	@PreAuthorize("hasAuthority('employee_device_association')")
	public String employeeDeviceAssociation(@PathVariable(value = "id") long id, Model model) {

		Employee employee = employeeService.getById(id);
		model.addAttribute("employee", employee);
		model.addAttribute("id", id);
		return "employee/employee_device";
	}

	@GetMapping("/api/search/employee-to-device/association")
	@PreAuthorize("hasAuthority('employee_device_association')")
	public @ResponseBody PaginationDto<EmployeeToDeviceAssociationDto> employeeDeviceDatatable(Long id, String device, String office, String area, int pageno, String sortField, String sortDir, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String orgName = (null == user.getOrganization()? null: user.getOrganization().getName());

		PaginationDto<EmployeeToDeviceAssociationDto> empToDevAssociationList = employeeService.searchEmployeeToDevice(id, device, office, area, pageno, sortField, sortDir,orgName);
		return empToDevAssociationList;

	}

	@GetMapping("/employee-to-device/association/save")
	@PreAuthorize("hasAuthority('employee_device_association')")
	public @ResponseBody ResponseEntity<Object> saveEmployeeDeviceAssociation(@RequestParam Long deviceId, @RequestParam Long empId,
			Principal principal) {

		try {
			
			String status = employeeService.saveEmployeeDeviceAssociation(deviceId, empId, principal);
			return ResponseEntity.ok(("Completed".equalsIgnoreCase(status))?"Sync Successfully!!":"Sync Failed!!");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.ok("Sync Failed!!");
		}
	}

	@GetMapping("/employee-delete-from/device")
	@PreAuthorize("hasAuthority('employee_device_association')")
	public @ResponseBody ResponseEntity<Object> deleteEmployeeFromParticularDevice(@RequestParam Long deviceId,
			@RequestParam Long empId, Principal principal) {

		try {

			Device device = deviceService.getById(deviceId);
			String status = employeeService.deleteEmployeeFromParticularDevice(deviceId, empId, principal);
			return ResponseEntity.ok(("Completed".equalsIgnoreCase(status))?"Deleted Successfully from "+device.getName()+" !!":"Deletion Failed!!");
		
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.ok("Delete Failed!!");
		}
	}
	
	@GetMapping("/excel-template-download")
	@PreAuthorize("hasAuthority('employee_import')")
	public void downloadEmployeeListExcelTemplate(HttpServletResponse response) throws IOException {
        String filename = "/excel/Employee_import_template.xlsx";
        try {
        	
        	String headerKey = "Content-Disposition";
			String headerValue = "attachment; filename=Employee_import_template.xlsx";
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
