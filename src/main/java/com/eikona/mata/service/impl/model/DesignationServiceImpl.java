package com.eikona.mata.service.impl.model;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.DesignationConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Designation;
import com.eikona.mata.repository.DesignationRepository;
import com.eikona.mata.service.DesignationService;
import com.eikona.mata.util.GeneralSpecificationUtil;


@Service
public class DesignationServiceImpl implements DesignationService {

	@Autowired
	private DesignationRepository designationRepository;
	
	@Autowired
	private GeneralSpecificationUtil<Designation> generalSpecification;

	@Override
	public List<Designation> getAll() {
		return designationRepository.findAllByIsDeletedFalse();
	}

	@Override
	public void save(Designation designation) {
		designation.setDeleted(false);
		this.designationRepository.save(designation);
	}

	@Override
	public Designation getById(long id) {
		Optional<Designation> optional = designationRepository.findById(id);
		Designation designation = null;
		if (optional.isPresent()) {
			designation = optional.get();
		} else {
			throw new RuntimeException(DesignationConstants.DESIGNATION_NOT_FOUND+ id);
		}
		return designation;
	}

	@Override
	public void deleteById(long id) {
		Optional<Designation> optional = designationRepository.findById(id);
		Designation designation = null;
		if (optional.isPresent()) {
			designation = optional.get();
			designation.setDeleted(true);
		} else {
			throw new RuntimeException(DesignationConstants.DESIGNATION_NOT_FOUND + id);
		}
		this.designationRepository.save(designation);
	}

	@Override
	public PaginationDto<Designation> searchByField(Long id, String name, int pageno, String sortField,
			String sortDir) {
		if (null == sortDir || sortDir.isEmpty()) {
			sortDir =  ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}
		Page<Designation> page = getDesignationPage(id, name, pageno, sortField, sortDir);
        List<Designation> designationList =  page.getContent();
		
		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir))?ApplicationConstants.DESC:ApplicationConstants.ASC;
		PaginationDto<Designation> dtoList = new PaginationDto<Designation>(designationList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<Designation> getDesignationPage(Long id, String name, int pageno, String sortField, String sortDir) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);
		
		Specification<Designation> idSpc = generalSpecification.longSpecification(id, ApplicationConstants.ID);
		Specification<Designation> nameSpc = generalSpecification.stringSpecification(name, ApplicationConstants.NAME);
		Specification<Designation> isDeletedFalse = generalSpecification.isDeletedSpecification();
		
		
    	Page<Designation> page = designationRepository.findAll(idSpc.and(nameSpc).and(isDeletedFalse),pageable);
		return page;
	}
	
	
	
	 

}
