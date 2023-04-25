package com.eikona.mata.service;


import java.security.Principal;
import java.util.List;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.EmployeeDevice;


public interface DeviceService {
	/**
	 * Returns all device List, which are isDeleted false.
	 * @param
	 */
	List<Device> getAll();
	/**
	 * This function saves the device in database according to the respective object.  
	 * @param 
	 */
    void save(Device device,Principal principal);
    /**
	 * This function retrieves the device from database according to the respective id.  
	 * @param
	 */
    Device getById(long id);
    /**
	 * This function deletes the device from database according to the respective id.  
	 * @param
	 */
    void deleteById(long id);
    
    String generateTransactionByDate(long id, String sDate, String eDate);
    
	void employeeSyncFromMataToDevice(long id, Principal principal);
	
	String saveDeviceEmployeeAssociation(EmployeeDevice employeeDevice, Long empId, Long devId, Principal principal) throws Exception;
	
	PaginationDto<Device> searchByField(Long id, String deviceType, String name, String area, String office, int pageno,
			String sortField, String sortDir);
	
	PaginationDto<Employee> searchDeviceToEmployee(Long id, String empId, String empName, String designation,
			String office, String area, int pageno, String sortField, String sortDir);
}
