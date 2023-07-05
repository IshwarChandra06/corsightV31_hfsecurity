package com.eikona.mata.service.impl.model;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.CorsightDeviceConstants;
import com.eikona.mata.constants.DeviceConstants;
import com.eikona.mata.constants.EmployeeConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.constants.UnvDeviceConstants;
import com.eikona.mata.dto.EmployeeToDeviceAssociationDto;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Area;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.EmployeeDevice;
import com.eikona.mata.entity.User;
import com.eikona.mata.repository.DeviceRepository;
import com.eikona.mata.repository.EmployeeDeviceRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.service.ActionService;
import com.eikona.mata.service.DeviceSyncAbstractService;
import com.eikona.mata.service.EmployeeService;
import com.eikona.mata.util.ExcelEmployeeImport;
import com.eikona.mata.util.GeneralSpecificationUtil;
import com.eikona.mata.util.ImageProcessingUtil;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private ExcelEmployeeImport excelEmployeeImport;
	
	@Autowired
	private ActionService actionService;
	
	@Autowired
	private ImageProcessingUtil imageProcessingUtil;
	
	
	@Autowired
	@Qualifier(UnvDeviceConstants.SERVICE)
	private DeviceSyncAbstractService<Long> unvBewardDeviceService;
	
	@Autowired
	@Qualifier(CorsightDeviceConstants.SERVICE)
	private DeviceSyncAbstractService<String> corsightDeviceService;
	
	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private EmployeeDeviceRepository employeeDeviceRepository;
	
	@Autowired
	private GeneralSpecificationUtil<Employee> generalSpecificationEmployee;
	
	@Autowired
	private GeneralSpecificationUtil<Device> generalSpecificationDevice;

	
	@Override
	public List<Employee> getAll() {
		return employeeRepository.findAllByIsDeletedFalse();
	}

	@Override
	public Employee save(Employee employee,Principal principal) {
		employee.setDeleted(false);
		employee.setSync(false);
		if (null == employee.getId()) {
			return this.employeeRepository.save(employee);
			//actionService.employeeAction(employee, "Create", "App", "",principal);
		} else {
			Employee employeeObj = employeeRepository.findById(employee.getId()).get();
			employee.setArea(employeeObj.getArea());
			
			employee.setPoi(employeeObj.getPoi());
			return this.employeeRepository.save(employee);
			//actionService.employeeAction(employee, "Edit", "App", "",principal);

		}

	}

	@Override
	public Employee getById(long id) {
		Optional<Employee> optional = employeeRepository.findById(id);
		Employee employee = null;
		if (optional.isPresent()) {
			employee = optional.get();
		} else {
			throw new RuntimeException(EmployeeConstants.EMPLOYEE_NOT_FOUND+ id);
		}
		return employee;
	}

	@Override
	public void deleteById(long id,Principal principal) {
		Optional<Employee> optional = employeeRepository.findById(id);
		Employee employee = null;
		if (optional.isPresent()) {
			employee = optional.get();
			employee.setDeleted(true);
		} else {
			throw new RuntimeException(EmployeeConstants.EMPLOYEE_NOT_FOUND + id);
		}
		this.employeeRepository.save(employee);

		//actionService.employeeAction(employee, "Delete", "App", "",principal);
	}

	@Override
	public String storeEmployeeList(MultipartFile file,User user) {
		try {
			List<Employee> employeeList = excelEmployeeImport.parseExcelFileEmployeeList(file.getInputStream(),user.getOrganization());
			employeeRepository.saveAll(employeeList);
			return "File uploaded successfully!";
		} catch (IOException e) {
			e.printStackTrace();
			return "Fail! -> uploaded filename: " + file.getOriginalFilename();
		}
	}
	
	@Override
	public String storeCosecEmployeeList(MultipartFile file,User user) {
		try {
			List<Employee> employeeList = excelEmployeeImport.parseCosecExcelFileEmployeeList(file.getInputStream(),user.getOrganization());
			employeeRepository.saveAll(employeeList);
			return "File uploaded successfully!";
		} catch (IOException | InvalidFormatException e) {
			e.printStackTrace();
			return "Fail! -> uploaded filename: " + file.getOriginalFilename();
		}
	}

	@Override
	public void saveEmployeeAreaAssociation(Employee employee, Long id, Principal principal) {
		Employee employeeObj = getById(id);
		employeeObj.setArea(employee.getArea());
		employeeRepository.save(employeeObj);
		
//		if(corsightEnabled) {
//			employee.setId(id);
//			corsightDeviceService.saveAsArea(employee, principal);
//		}else {
//			Employee employeeObj = getById(id);
//			List<Area> areaList = new ArrayList<>();
//			areaList.addAll(employee.getArea());
//			employeeObj.setArea(areaList);
//			employee.setId(id);
//			unvBewardDeviceService.saveAsArea(employee, principal);
//		}
	}

	@Override
	public String saveEmployeeDeviceAssociation(Long deviceId, Long empId, Principal principal) throws Exception {
		
		Device device = deviceRepository.findById(deviceId).get();

		Employee employee = getById(empId);
		String status = actionService.employeeDeviceAction(device, employee, ApplicationConstants.SYNC, ApplicationConstants.ACCESS_TYPE_APP, ApplicationConstants.DELIMITER_EMPTY, principal);
		EmployeeDevice empDevice = employeeDeviceRepository.findByEmployeeAndDevice(employee, device);
		if (null != empDevice) {
			empDevice.setLastSync(new Date());
		} else {
			empDevice = new EmployeeDevice();
			empDevice.setDevice(device);
			empDevice.setEmployee(employee);
			empDevice.setLastSync(new Date());
		}
		employeeDeviceRepository.save(empDevice);
		
		return status;
	}

	@Override
	public String deleteEmployeeFromParticularDevice(Long deviceId, Long empId, Principal principal) throws Exception {
		
		Device device = deviceRepository.findById(deviceId).get();

		Employee employee = getById(empId);
		
		String status = actionService.employeeDeviceAction(device, employee, ApplicationConstants.DELETE, ApplicationConstants.ACCESS_TYPE_APP, ApplicationConstants.DELIMITER_EMPTY, principal);
		EmployeeDevice empDevice = employeeDeviceRepository.findByEmployeeAndDevice(employee, device);
		
		if(null!=empDevice)
			employeeDeviceRepository.delete(empDevice);
		
		return status;
	}
	
	@Override
	public PaginationDto<Employee> searchByField(Long id, String name, String empId, String branch, String department,
			String designation, int pageno, String sortField, String sortDir,String org) {

		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}
		Page<Employee> page = getEmployeePage(id, name, empId, branch, department, designation, pageno, sortField,
				sortDir,org);
        List<Employee> employeeList =  page.getContent();
        List<Employee> employeeWithImgList = new ArrayList<Employee>();
        for (Employee employee : employeeList) {
			byte[] image = imageProcessingUtil.searchEmployeeImage(employee.getId());
			employee.setCropImage(image);
			employeeWithImgList.add(employee);
		}
		
		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir))?ApplicationConstants.DESC:ApplicationConstants.ASC;
		PaginationDto<Employee> dtoList = new PaginationDto<Employee>(employeeWithImgList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<Employee> getEmployeePage(Long id, String name, String empId, String branch, String department,
			String designation, int pageno, String sortField, String sortDir, String organization) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);
		Specification<Employee> isDeletedFalse = generalSpecificationEmployee.isDeletedSpecification();
		Specification<Employee> idSpc = generalSpecificationEmployee.longSpecification(id, ApplicationConstants.ID);
		Specification<Employee> nameSpc = generalSpecificationEmployee.stringSpecification(name, ApplicationConstants.NAME);
		Specification<Employee> empIdSpc = generalSpecificationEmployee.stringSpecification(empId, EmployeeConstants.EMPID);
		Specification<Employee> branchSpc = generalSpecificationEmployee.foreignKeyStringSpecification(branch, EmployeeConstants.BRANCH,ApplicationConstants.NAME);
		Specification<Employee> deptSpec = generalSpecificationEmployee.foreignKeyStringSpecification(department, EmployeeConstants.DEPARTMENT,ApplicationConstants.NAME);
		Specification<Employee> designationSpc = generalSpecificationEmployee.foreignKeyStringSpecification(designation, EmployeeConstants.DESIGNATION,ApplicationConstants.NAME);
		Specification<Employee> orgSpc = generalSpecificationEmployee.foreignKeyStringSpecification(organization, "organization", ApplicationConstants.NAME);
    	Page<Employee> page = employeeRepository.findAll(idSpc.and(nameSpc).and(empIdSpc).and(branchSpc).and(deptSpec).and(designationSpc).and(orgSpc).and(isDeletedFalse),pageable);
		return page;
	}
	

	@Override
	public PaginationDto<EmployeeToDeviceAssociationDto> searchEmployeeToDevice(Long id, String device, String office,
			String area, int pageno, String sortField, String sortDir,String orgName) {
		
		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}
		Employee employee = getById(id);
		List<String> areaName = new ArrayList<String>();
		for(Area areaObj: employee.getArea()) {
			areaName.add(areaObj.getName());
		}
		Page<Device> page = getEmployeeToDevicePage(device, office, area, pageno, sortField, sortDir, areaName,orgName);
		List<Device> deviceList =  page.getContent();
		List<EmployeeToDeviceAssociationDto> empToDevAssociationList = new ArrayList<>();
		for (Device deviceOnj : deviceList) {
			EmployeeToDeviceAssociationDto empDevDto = new EmployeeToDeviceAssociationDto();

			EmployeeDevice empDevice = employeeDeviceRepository.findByEmployeeAndDevice(employee, deviceOnj);
			if (null != empDevice) {
				empDevDto.setEmployeeId(empDevice.getEmployee().getId());
				empDevDto.setSyncDate(empDevice.getLastSync());
			}
			empDevDto.setBranchName(deviceOnj.getArea().getBranch().getName());
			empDevDto.setEmpName(deviceOnj.getName());
			empDevDto.setDeviceId(deviceOnj.getId());
			empDevDto.setDeviceName(deviceOnj.getName());
			empDevDto.setAreaName(deviceOnj.getArea().getName());

			empToDevAssociationList.add(empDevDto);
		}
		
		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir))?ApplicationConstants.DESC:ApplicationConstants.ASC;
		PaginationDto<EmployeeToDeviceAssociationDto> dtoList = new PaginationDto<EmployeeToDeviceAssociationDto>(empToDevAssociationList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<Device> getEmployeeToDevicePage(String device, String office, String area, int pageno,
			String sortField, String sortDir, List<String> areaName, String orgName) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);
		Specification<Device> areaListSpc = generalSpecificationDevice.foreignKeyListStringSpecification(areaName, DeviceConstants.AREA,DeviceConstants.NAME); 
		Specification<Device> isDeletedFalse = generalSpecificationDevice.isDeletedSpecification();
		Specification<Device> nameSpc = generalSpecificationDevice.stringSpecification(device, DeviceConstants.NAME);
		Specification<Device> officeSpc = generalSpecificationDevice.foreignKeyStringSpecification(office, DeviceConstants.BRANCH,ApplicationConstants.NAME);
		Specification<Device> areaSpc = generalSpecificationDevice.foreignKeyStringSpecification(area, DeviceConstants.AREA,ApplicationConstants.NAME);
		Specification<Device> orgSpc = generalSpecificationDevice.foreignKeyStringSpecification(orgName, "organization",ApplicationConstants.NAME);
		
		Page<Device> page = deviceRepository.findAll(isDeletedFalse.and(areaListSpc).and(nameSpc).and(officeSpc).and(areaSpc).and(orgSpc), pageable);
		return page;
	}

	

}