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
import com.eikona.mata.constants.DepartmentConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Department;
import com.eikona.mata.repository.DepartmentRepository;
import com.eikona.mata.service.DepartmentService;
import com.eikona.mata.util.GeneralSpecificationUtil;


@Service
public class DepartmentServiceImpl implements DepartmentService{
	
	@Autowired
    private DepartmentRepository departmentRepository;
	
	@Autowired
    private GeneralSpecificationUtil<Department> generalSpecification;

    @Override
    public List <Department> getAll() {
        return departmentRepository.findAllByIsDeletedFalse();
    }

    @Override
    public void save(Department department) {
    	department.setDeleted(false);
        this.departmentRepository.save(department);
    }

    @Override
    public Department getById(long id) {
        Optional<Department> optional = departmentRepository.findById(id);
        Department department = null;
        if (optional.isPresent()) {
        	department = optional.get();
        } else {
            throw new RuntimeException(DepartmentConstants.DEPARTMENT_NOT_FOUND + id);
        }
        return department;
    }
    
    @Override
	public void deleteById(long id) {
    	Optional<Department> optional = departmentRepository.findById(id);
    	Department department = null;
        if (optional.isPresent()) {
        	department = optional.get();
        	department.setDeleted(true);
        } else {
            throw new RuntimeException(DepartmentConstants.DEPARTMENT_NOT_FOUND + id);
        }
        this.departmentRepository.save(department);
	}

	@Override
	public PaginationDto<Department> searchByField(Long id, String name, int pageno, String sortField, String sortDir) {
		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}
		Page<Department> page = getDepartmentPage(id, name, pageno, sortField, sortDir);
        List<Department> departmentList =  page.getContent();
		
		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir))?ApplicationConstants.DESC:ApplicationConstants.ASC;
		PaginationDto<Department> dtoList = new PaginationDto<Department>(departmentList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<Department> getDepartmentPage(Long id, String name, int pageno, String sortField, String sortDir) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);
		
		Specification<Department> idSpc = generalSpecification.longSpecification(id, ApplicationConstants.ID);
		Specification<Department> nameSpc = generalSpecification.stringSpecification(name, ApplicationConstants.NAME);
		Specification<Department> isDeletedFalse = generalSpecification.isDeletedSpecification();
		
    	Page<Department> page = departmentRepository.findAll(idSpc.and(nameSpc).and(isDeletedFalse),pageable);
		return page;
	}
	
    
}
