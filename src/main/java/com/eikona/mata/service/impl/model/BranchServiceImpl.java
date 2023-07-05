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
import com.eikona.mata.constants.BranchConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Branch;
import com.eikona.mata.repository.BranchRepository;
import com.eikona.mata.service.BranchService;
import com.eikona.mata.util.GeneralSpecificationUtil;


@Service
public class BranchServiceImpl implements BranchService {

	@Autowired
	private BranchRepository branchRepository;
	
	@Autowired
	private GeneralSpecificationUtil<Branch> generalSpecification;

	@Override
	public List<Branch> getAll() {
		return branchRepository.findAllByIsDeletedFalse();
	}

	@Override
	public void save(Branch branch) {
		branch.setDeleted(false);
		this.branchRepository.save(branch);
	}

	@Override
	public Branch getById(long id) {
		Optional<Branch> optional = branchRepository.findById(id);
		Branch branch = null;
		if (optional.isPresent()) {
			branch = optional.get();
		} else {
			throw new RuntimeException(BranchConstants.NOT_FOUND_FOR_ID + id);
		}
		return branch;
	}

	@Override
	public void deletedById(long id) {
		Optional<Branch> optional = branchRepository.findById(id);
		Branch branch = null;
		if (optional.isPresent()) {
			branch = optional.get();
			branch.setDeleted(true);
		} else {
			throw new RuntimeException(BranchConstants.NOT_FOUND_FOR_ID + id);
		}
		this.branchRepository.save(branch);
	}


	@Override
	public PaginationDto<Branch> searchByField(Long id, String name, String address, String city, String state,
			String pin, int pageno, String sortField, String sortDir,String org) {
		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Page<Branch> page = getBranchPage(id, name, address, city, state, pin, pageno, sort,org);
        List<Branch> branchList =  page.getContent();
		
		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir))?ApplicationConstants.DESC:ApplicationConstants.ASC;
		PaginationDto<Branch> dtoList = new PaginationDto<Branch>(branchList, page.getTotalPages(),
				page.getNumber() + 1, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<Branch> getBranchPage(Long id, String name, String address, String city, String state, String pin,
			int pageno, Sort sort, String organization) {
		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);
		
		Specification<Branch> idSpc = generalSpecification.longSpecification(id, ApplicationConstants.ID);
		Specification<Branch> nameSpc = generalSpecification.stringSpecification(name, ApplicationConstants.NAME);
		Specification<Branch> addressSpc = generalSpecification.stringSpecification(address, BranchConstants.ADDRESS);
		Specification<Branch> citySpc = generalSpecification.stringSpecification(city, BranchConstants.CITY);
		Specification<Branch> stateSpec = generalSpecification.stringSpecification(state, BranchConstants.STATE);
		Specification<Branch> pinSpc = generalSpecification.stringSpecification(pin, BranchConstants.PINCODE);
		Specification<Branch> isDeletedFalse = (Specification<Branch>) (root, query, builder) -> {
			return builder.equal(root.get(ApplicationConstants.IS_DELETED), false);
		};
		Specification<Branch> orgSpc = generalSpecification.foreignKeyStringSpecification(organization, "organization", ApplicationConstants.NAME);
		
    	Page<Branch> page = branchRepository.findAll(idSpc.and(nameSpc).and(addressSpc).and(isDeletedFalse).and(citySpc).and(stateSpec).and(pinSpc).and(orgSpc),pageable);
		return page;
	}

}
