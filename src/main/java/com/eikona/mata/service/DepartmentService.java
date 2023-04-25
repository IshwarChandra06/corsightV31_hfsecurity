package com.eikona.mata.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Department;

public interface DepartmentService {
	/**
	 * Returns all department List, which are isDeleted false.
	 * @param
	 */
	List<Department> getAll();
	/**
	 * This function saves the department in database according to the respective object.  
	 * @param 
	 */
	void save(Department department);
	/**
	 * This function retrieves the department from database according to the respective id.  
	 * @param
	 */
	Department getById(long id);
	/**
	 * This function deletes the department from database according to the respective id.  
	 * @param
	 */
	void deleteById(long id);

	PaginationDto<Department> searchByField(Long id, String name, int pageno, String sortField, String sortDir);
}
