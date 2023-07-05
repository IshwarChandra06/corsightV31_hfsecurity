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
import com.eikona.mata.constants.RoleConstants;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Role;
import com.eikona.mata.repository.RoleRepository;
import com.eikona.mata.service.RoleService;
import com.eikona.mata.util.GeneralSpecificationUtil;

@Service
public class RoleServiceImpl implements RoleService {
	
	@Autowired
    private RoleRepository roleRepository;
	
	@Autowired
    private GeneralSpecificationUtil<Role> generalSpecificationRole;

	@Override
	public List<Role> getAll() {
		 return roleRepository.findAllByIsDeletedFalse();
		
	}
	
	@Override
	public void save(Role role) {
		role.setDeleted(false);
        this.roleRepository.save(role);
    	
    }
	
	@Override
	public  Role getById(long id) {
		Optional<Role> optional = roleRepository.findById(id);
		Role role = null;
        if (optional.isPresent()) {
        	role = optional.get();
        } else {
            throw new RuntimeException(RoleConstants.ROLE_NOT_FOUND + id);
        }
        return role;
    }
	
	@Override
	public void deleteById(long id) {
		Optional<Role> optional = roleRepository.findById(id);
		Role role = null;
        if (optional.isPresent()) {
        	role = optional.get();
        	role.setDeleted(true);
        } else {
            throw new RuntimeException(RoleConstants.ROLE_NOT_FOUND + id);
        }
        this.roleRepository.save(role);
	}

	@Override
	public PaginationDto<Role> searchByField(Long id, String name, int pageno, String sortField, String sortDir,String org) {
		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}
		Page<Role> page = getSpecificationOfRole(id, name, pageno, sortField, sortDir,org);
        List<Role> roleList =  page.getContent();
		
		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir))?ApplicationConstants.DESC:ApplicationConstants.ASC;
		PaginationDto<Role> dtoList = new PaginationDto<Role>(roleList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<Role> getSpecificationOfRole(Long id, String name, int pageno, String sortField, String sortDir, String organization) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);
		
		Specification<Role> idSpc = generalSpecificationRole.longSpecification(id, ApplicationConstants.ID);
		Specification<Role> nameSpc = generalSpecificationRole.stringSpecification(name, ApplicationConstants.NAME);
		Specification<Role> isDeletedFalse = generalSpecificationRole.isDeletedSpecification();
		Specification<Role> orgSpc = generalSpecificationRole.foreignKeyStringSpecification(organization, "organization", ApplicationConstants.NAME);
		
    	Page<Role> page = roleRepository.findAll(idSpc.and(nameSpc).and(isDeletedFalse).and(orgSpc),pageable);
		return page;
	}
}
