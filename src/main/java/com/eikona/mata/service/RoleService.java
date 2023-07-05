package com.eikona.mata.service;


import java.util.List;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Role;



public interface RoleService {
	/**
	 * Returns all role List, which are isDeleted false.
	 * @param
	 */
	List<Role> getAll();
	/**
	 * This function saves the role in database according to the respective object.  
	 * @param 
	 */
    void save(Role role);
    /**
	 * This function retrieves the role from database according to the respective id.  
	 * @param
	 */
    Role getById(long id);
    /**
	 * This function deletes the role from database according to the respective id.  
	 * @param
	 */
    void deleteById(long id);
    
    
	PaginationDto<Role> searchByField(Long id, String name, int pageno, String sortField, String sortDir, String orgName);
    
   
    }

