package com.eikona.mata.service;

import java.util.List;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Door;

public interface DoorService {
	List<Door> getAll();

	Door save(Door Door);

	Door getById(long id);

	void deleteById(long id);
	
	PaginationDto<Door> searchByField(Long id, String name,  String ipAddress, int pageno, String sortField, String sortDir, String orgName);
}
