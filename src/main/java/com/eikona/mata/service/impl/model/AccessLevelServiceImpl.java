package com.eikona.mata.service.impl.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.AccessLevel;
import com.eikona.mata.repository.AccessLevelRepository;
import com.eikona.mata.service.AccessLevelService;
import com.eikona.mata.util.GeneralSpecificationUtil;

@Service
public class AccessLevelServiceImpl implements AccessLevelService {
	
	@Autowired
	private AccessLevelRepository accessLevelRepository;
	
	@Autowired
	private GeneralSpecificationUtil<AccessLevel> generalSpecification;

	@Override
	public PaginationDto<AccessLevel> searchByField(Long id, String name, int pageno, String sortField,
			String sortDir) {
		
		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}

		Page<AccessLevel> page = getPaginatedAccessLevel(id, name, pageno, sortField, sortDir);
        List<AccessLevel> accessLevelList =  page.getContent();
        
		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir))?ApplicationConstants.DESC:ApplicationConstants.ASC;
		PaginationDto<AccessLevel> dtoList = new PaginationDto<AccessLevel>(accessLevelList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}


	private Page<AccessLevel> getPaginatedAccessLevel(Long id, String name, int pageno, String sortField,
			String sortDir) {
		
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();
		
		Pageable pageable = PageRequest.of(pageno -NumberConstants.ONE, NumberConstants.TEN, sort);
		
		Specification<AccessLevel> idSpc = generalSpecification.longSpecification(id, ApplicationConstants.ID);
		Specification<AccessLevel> nameSpc = generalSpecification.stringSpecification(name, ApplicationConstants.NAME);
		
    	Page<AccessLevel> page = accessLevelRepository.findAll(idSpc.and(nameSpc),pageable);
		return page;
	}
}
