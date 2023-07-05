package com.eikona.mata.service;

import java.util.List;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Branch;


public interface BranchService {
	/**
	 * Returns all branch List, which are isDeleted false.
	 * @param
	 */
	List<Branch> getAll();

	/**
	 * This function saves the branch in database according to the respective object.  
	 * @param 
	 */
	void save(Branch branch);
	/**
	 * This function retrieves the branch from database according to the respective id.  
	 * @param
	 */
	Branch getById(long id);
	/**
	 * This function deletes the branch from database according to the respective id.  
	 * @param
	 */

	void deletedById(long id);

	PaginationDto<Branch> searchByField(Long id, String name, String address, String city, String state, String pin, int pageno, String sortField, String sortDir, String orgName);

}
