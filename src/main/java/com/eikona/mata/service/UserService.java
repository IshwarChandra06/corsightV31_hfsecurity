package com.eikona.mata.service;


import java.util.List;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.User;



public interface UserService {
	/**
	 * Returns all user List, which are isDeleted false.
	 * @param
	 */
	List<User> getAll();
	/**
	 * This function saves the user in database according to the respective object.  
	 * @param 
	 */
    void save(User user);
    /**
	 * This function retrieves the user from database according to the respective id.  
	 * @param
	 */
    User getById(long id);
    /**
	 * This function deletes the user from database according to the respective id.  
	 * @param
	 */
    void deleteById(long id);
    
	PaginationDto<User> searchByField(Long id, String name, String phone, String role, int pageno, String sortField,
			String sortDir);
    
   
    }

