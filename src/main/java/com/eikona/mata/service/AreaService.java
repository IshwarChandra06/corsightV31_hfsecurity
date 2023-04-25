package com.eikona.mata.service;


import java.security.Principal;
import java.util.List;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Area;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;


public interface AreaService {
/**
 * Returns all area List, which are isDeleted false.
 * @param
 */
	List<Area> getAll();
	
	/**
	 * This function saves the area in database according to the respective object.  
	 * @param 
	 */
	void save(Area area);
	/**
	 * This function retrieves the area from database according to the respective id.  
	 * @param
	 */
	Area getById(long id);
	/**
	 * This function deletes the area from database according to the respective id.  
	 * @param
	 */

	void deletedById(long id);
	

	String saveAreaEmployeeAssociation(Long employeeId, Long areaId, Principal principal);

	String saveAreaDeviceAssociation(Long deviceId, Long areaId, Principal principal);

	PaginationDto<Area> searchByField(Long id, String name, String office, int pageno, String sortField,
			String sortDir);

	PaginationDto<Employee> searchAreaToEmployee(String id, String name, String office, String area, int pageno,
			String sortField, String sortDir);

	PaginationDto<Device> searchAreaToDevice(String name, String office, String area, int pageno,
			String sortField, String sortDir);
	
	
}
