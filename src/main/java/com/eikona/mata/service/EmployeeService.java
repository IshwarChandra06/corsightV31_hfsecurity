package com.eikona.mata.service;


import java.security.Principal;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.eikona.mata.dto.EmployeeToDeviceAssociationDto;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Employee;


public interface EmployeeService {
	/**
	 * Returns all employee List, which are isDeleted false.
	 * @param
	 */
	List<Employee> getAll();
	/**
	 * This function saves the employee in database according to the respective object.  
	 * @param 
	 */
    Employee save(Employee employee, Principal principal);
    /**
	 * This function retrieves the employee from database according to the respective id.  
	 * @param
	 */
    Employee getById(long id);
    
	/**
	 * This function retrieves the employee data from the excel file and set into database.
	 * @param file -MultipartFile
	 */
	String storeEmployeeList(MultipartFile file);
	/**
	 * This function deletes the employee from database according to the respective id.  
	 * @param
	 */
	void deleteById(long id, Principal principal);
	
	String storeCosecEmployeeList(MultipartFile file);
	
	void saveEmployeeAreaAssociation(Employee employee, Long id, Principal principal);
	
	String saveEmployeeDeviceAssociation(Long deviceId, Long empId, Principal principal) throws Exception;
	
	String deleteEmployeeFromParticularDevice(Long deviceId, Long empId, Principal principal) throws Exception;
	
	PaginationDto<Employee> searchByField(Long id, String name, String empId, String branch, String department,
			String designation, int pageno, String sortField, String sortDir);
	
	PaginationDto<EmployeeToDeviceAssociationDto> searchEmployeeToDevice(Long id, String device, String office,
			String area, int pageno, String sortField, String sortDir);



}
