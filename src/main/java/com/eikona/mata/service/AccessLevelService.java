package com.eikona.mata.service;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.AccessLevel;

public interface AccessLevelService {

	PaginationDto<AccessLevel> searchByField(Long id, String name, int pageno, String sortField, String sortDir);

}
