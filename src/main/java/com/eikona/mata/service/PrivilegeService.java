package com.eikona.mata.service;


import java.util.List;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Privilege;



public interface PrivilegeService {
	/**
	 * Returns all privilege List, which are isDeleted false.
	 * @param
	 */
	List<Privilege> getAll();
	/**
	 * This function saves the privilege in database according to the respective object.  
	 * @param 
	 */
    void save(Privilege privilege);
    /**
	 * This function retrieves the privilege from database according to the respective id.  
	 * @param
	 */
    Privilege getById(long id);
    /**
	 * This function deletes the privilege from database according to the respective id.  
	 * @param
	 */
    void deleteById(long id);
    
	PaginationDto<Privilege> searchByField(Long id, String name, int pageno, String sortField, String sortDir);
    
   
    }

