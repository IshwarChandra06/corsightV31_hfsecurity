package com.eikona.mata.service;


import java.util.List;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Organization;


public interface OrganizationService {
	/**
	 * Returns all organization List, which are isDeleted false.
	 * @param
	 */
	List<Organization> getAll();
	/**
	 * This function saves the organization in database according to the respective object.  
	 * @param 
	 */
	Organization save(Organization organization);
    /**
	 * This function retrieves the organization from database according to the respective id.  
	 * @param
	 */
    Organization getById(long id);
    /**
	 * This function deletes the organization from database according to the respective id.  
	 * @param
	 */
    void deleteById(long id);
    
    
	PaginationDto<Organization> searchByField(Long id, String name, String address, String city, String pin,
			int pageno, String sortField, String sortDir);
    
   
    }

