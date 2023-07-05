package com.eikona.mata.controller;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

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

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.EmployeeDevice;
import com.eikona.mata.entity.Organization;
import com.eikona.mata.entity.User;
import com.eikona.mata.repository.BranchRepository;
import com.eikona.mata.repository.OrganizationRepository;
import com.eikona.mata.repository.UserRepository;
import com.eikona.mata.service.DeviceService;
import com.eikona.mata.service.DeviceSyncAbstractService;
import com.eikona.mata.sync.DeviceSync;

@Controller
public class DeviceController {
	
	@Autowired
	private DeviceService deviceService;

	@Autowired
	private BranchRepository branchRepository;

	@Autowired
	private OrganizationRepository organizationRepository;
	
	 @Autowired
	private UserRepository userRepository;
	
	@Autowired
	private DeviceSync cameraSync;

	@Value("${corsight.enabled}")
	private boolean corsightEnabled;
	
	@Autowired
	@Qualifier("unvDeviceService") 
	private DeviceSyncAbstractService<Long> unvSyncService;
	
	@Autowired
	@Qualifier("bewardDeviceService") 
	private DeviceSyncAbstractService<String> bewardSyncService;

	@GetMapping("/device")
	@PreAuthorize("hasAuthority('device_view')")
	public String deviceList(Model model){
		model.addAttribute("corsightEnabled",corsightEnabled);
		return "device/device_list";
	}

	@RequestMapping(value = "/api/search/device", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('device_view')")
	public @ResponseBody PaginationDto<Device> search(Long id, String deviceType, String name, String office, String area, int pageno, String sortField, String sortDir, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String orgName = (null == user.getOrganization()? null: user.getOrganization().getName());
		
		PaginationDto<Device> dtoList = deviceService.searchByField(id, deviceType, name, area, office, pageno, sortField, sortDir,orgName);
		return dtoList;
	}

	@GetMapping("/device/new")
	@PreAuthorize("hasAuthority('device_create')")
	public String newDevice(Model model, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		List<Organization> organizationList = null;
		if(null == user.getOrganization()) {
			organizationList = (List<Organization>) organizationRepository.findAll();
		}else {
			organizationList = organizationRepository.findByIdAndIsDeletedFalse(user.getOrganization().getId());
		}
		
		model.addAttribute("listOrganization", organizationList);

		model.addAttribute("listBranch", branchRepository.findByOrganizationAndIsDeletedFalse(user.getOrganization()));
		Device device = new Device();
		model.addAttribute("device", device);
		model.addAttribute("title", "New Device");
		model.addAttribute("corsightEnabled",corsightEnabled);
		return "device/device_new";
	}

	@PostMapping("/device/add")
	@PreAuthorize("hasAnyAuthority('device_create','device_update')")
	public String saveDevice(@ModelAttribute("device") Device device, @Valid Device dev, Errors errors, String title,
			Model model, Principal principal) {
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		
		if (errors.hasErrors()){
			List<Organization> organizationList = null;
			if(null == user.getOrganization()) {
				organizationList = (List<Organization>) organizationRepository.findAll();
			}else {
				organizationList = organizationRepository.findByIdAndIsDeletedFalse(user.getOrganization().getId());
			}
			
			model.addAttribute("listOrganization", organizationList);
			model.addAttribute("listBranch", branchRepository.findByOrganizationAndIsDeletedFalse(user.getOrganization()));
			model.addAttribute("title", title);
			return "device/device_new";
		} else {
			if (null == device.getId())
				deviceService.save(device, principal);
			else {
				Device deviceObj = deviceService.getById(device.getId());
				device.setCreatedBy(deviceObj.getCreatedBy());
				device.setCreatedDate(deviceObj.getCreatedDate());
				deviceService.save(device, principal);
			}
			return "redirect:/device";
		}
	}

	@GetMapping("/device/edit/{id}")
	@PreAuthorize("hasAuthority('device_update')")
	public String editDevice(@PathVariable(value = "id") long id, Model model, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		List<Organization> organizationList = null;
		if(null == user.getOrganization()) {
			organizationList = (List<Organization>) organizationRepository.findAll();
		}else {
			organizationList = organizationRepository.findByIdAndIsDeletedFalse(user.getOrganization().getId());
		}
		Device device = deviceService.getById(id);
		
		model.addAttribute("listOrganization", organizationList);
		model.addAttribute("listBranch", branchRepository.findByOrganizationAndIsDeletedFalse(user.getOrganization()));
		model.addAttribute("device", device);
		model.addAttribute("title", "Update Device");
		model.addAttribute("corsightEnabled",corsightEnabled);
		return "device/device_new";
	}

	@GetMapping("/device/delete/{id}")
	@PreAuthorize("hasAuthority('device_delete')")
	public String deleteDevice(@PathVariable(value = "id") long id) {
		this.deviceService.deleteById(id);
		return "redirect:/device";
	}
	@GetMapping("/sync-camera")
	@PreAuthorize("hasAuthority('device_sync')")
	public String syncDevice(Model model,Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
	
		String message = null;
		try {
			message = cameraSync.syncDevice(user.getOrganization());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("message", message);
		model.addAttribute("corsightEnabled",corsightEnabled);
		return "device/device_list";
	}
	
	@GetMapping("/device-to-employee/association/{id}")
	@PreAuthorize("hasAuthority('device_employee_association')")
	public String deviceEmployeeAssociation(@PathVariable(value = "id") long id, Model model) {

		Device deviceObj = deviceService.getById(id);
		model.addAttribute("id", id);
		if (null != deviceObj.getArea()) {
			model.addAttribute("area", deviceObj.getArea().getName());
			model.addAttribute("office", deviceObj.getArea().getBranch().getName());
		}
		model.addAttribute("device", deviceObj.getName());
		model.addAttribute("title", "New Employee Association");

		return "device/device_employees";
	}

	@GetMapping("/api/search/device-to-employee/association")
	@PreAuthorize("hasAuthority('device_employee_association')")
	public @ResponseBody PaginationDto<Employee> deviceEmployee(Long id, String empId, String empName, String designation, String office, String area, int pageno, String sortField, String sortDir, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String orgName = (null == user.getOrganization()? null: user.getOrganization().getName());

		PaginationDto<Employee> deviceToEmpList = deviceService.searchDeviceToEmployee(id, empId, empName, designation, office, area, pageno, sortField, sortDir,orgName);
		
		return deviceToEmpList;

	}

	@GetMapping("/device-to-employee/association/save")
	@PreAuthorize("hasAuthority('device_employee_association')")
	public @ResponseBody ResponseEntity<Object> saveDeviceEmployeeAssociation(
			@ModelAttribute("EmployeeDevice") EmployeeDevice employeeDevice, @RequestParam Long empId,
			@RequestParam Long devId, Principal principal) {

		try {
			String status=deviceService.saveDeviceEmployeeAssociation(employeeDevice,empId,devId,principal);
			return ResponseEntity.ok(("Completed".equalsIgnoreCase(status))?"Sync Successfully!!":"Sync Failed!!");
		} catch (Exception e) {
			return ResponseEntity.ok("Sync Failed!!");
		}
	}

	
	@GetMapping("/device-to-mata-upload/{id}")
	@PreAuthorize("hasAuthority('device_to_mata_sync')")
	public String uploadEmployeeFromDeviceToMata(@PathVariable(value = "id") long id, Model model) {
		Device device = deviceService.getById(id);
		if ("BFRC".equalsIgnoreCase(device.getModel())) {
			try {
				bewardSyncService.pullEmployeeFromDeviceToMata(device);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("FRWT".equalsIgnoreCase(device.getModel())) {
			try {
				unvSyncService.pullEmployeeFromDeviceToMata(device);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return "redirect:/device";
	}

	@GetMapping("/mata-to-device-sync/{id}")
	@PreAuthorize("hasAuthority('mata_to_device_sync')")
	public String employeeSyncFromMataToDevice(@PathVariable(value = "id") long id, Principal principal) {
		deviceService.employeeSyncFromMataToDevice(id,principal);
		return "redirect:/device";
	}
	
	@RequestMapping(value = "/get-transaction-by-date", method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('transaction_by_date')")
	public @ResponseBody String getTransacionByDateRange(long id, String sDate, String eDate) {
		String message = deviceService.generateTransactionByDate(id, sDate, eDate);
		
		return message;
	}
	
	@GetMapping("/get-transaction-by-date/{id}")
	@PreAuthorize("hasAuthority('transaction_by_date')")
	public String getTransactionRecordByDateRange(@PathVariable(value = "id") long id, Model model) {

		model.addAttribute("id", id);
		return "transaction/syncTransaction";
		
	}
}
