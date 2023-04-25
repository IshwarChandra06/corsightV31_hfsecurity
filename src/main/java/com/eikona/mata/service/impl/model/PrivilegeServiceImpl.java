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
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.constants.PrivilegeConstants;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Privilege;
import com.eikona.mata.repository.PrivilegeRepository;
import com.eikona.mata.service.PrivilegeService;
import com.eikona.mata.util.GeneralSpecificationUtil;


@Service
public class PrivilegeServiceImpl implements PrivilegeService {
	
	@Autowired
    private PrivilegeRepository privilegeRepository;
	
	@Autowired
    private GeneralSpecificationUtil<Privilege> generalSpecificationPrivilege;

	@Override
	public List<Privilege> getAll() {
		 return privilegeRepository.findAllByIsDeletedFalse();
		
	}
	
	@Override
	public void save(Privilege privilege) {
		privilege.setDeleted(false);
        this.privilegeRepository.save(privilege);
    	
    }
	
	@Override
	public  Privilege getById(long id) {
		Optional<Privilege> optional = privilegeRepository.findById(id);
		Privilege privilege = null;
        if (optional.isPresent()) {
        	privilege = optional.get();
        } else {
            throw new RuntimeException(PrivilegeConstants.PRIVILEGE_NOT_FOUND + id);
        }
        return privilege;
    	
    }
	
	@Override
	public void deleteById(long id) {
		Optional<Privilege> optional = privilegeRepository.findById(id);
		Privilege privilege = null;
        if (optional.isPresent()) {
        	privilege = optional.get();
        	privilege.setDeleted(true);
        } else {
            throw new RuntimeException(PrivilegeConstants.PRIVILEGE_NOT_FOUND + id);
        }
        this.privilegeRepository.save(privilege);
	}

	

	@Override
	public PaginationDto<Privilege> searchByField(Long id, String name, int pageno, String sortField, String sortDir) {
		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}
		Page<Privilege> page = getPrivilegePage(id, name, pageno, sortField, sortDir);
        List<Privilege> privilegeList =  page.getContent();
		
		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir))?ApplicationConstants.DESC:ApplicationConstants.ASC;
		PaginationDto<Privilege> dtoList = new PaginationDto<Privilege>(privilegeList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<Privilege> getPrivilegePage(Long id, String name, int pageno, String sortField, String sortDir) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);
		
		Specification<Privilege> idSpc = generalSpecificationPrivilege.longSpecification(id, ApplicationConstants.ID);
		Specification<Privilege> nameSpc = generalSpecificationPrivilege.stringSpecification(name, ApplicationConstants.NAME);
		Specification<Privilege> isDeletedFalse = generalSpecificationPrivilege.isDeletedSpecification();
		
    	Page<Privilege> page = privilegeRepository.findAll(idSpc.and(nameSpc).and(isDeletedFalse), pageable);
		return page;
	}
}
