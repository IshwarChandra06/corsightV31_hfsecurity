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
import com.eikona.mata.constants.DoorConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Door;
import com.eikona.mata.repository.DoorRepository;
import com.eikona.mata.service.DoorService;
import com.eikona.mata.util.GeneralSpecificationUtil;

@Service
public class DoorServiceImpl implements DoorService {

	@Autowired
	private DoorRepository doorRepository;
	
	@Autowired
	private GeneralSpecificationUtil<Door> generalSpecification;

	@Override
	public List<Door> getAll() {
		return doorRepository.findAllByIsDeletedFalse();
	}

	@Override
	public Door save(Door door) {
		door.setDeleted(false);
		return this.doorRepository.save(door);
	}

	@Override
	public Door getById(long id) {
		Optional<Door> optional = doorRepository.findById(id);
		Door Door = null;
		if (optional.isPresent()) {
			Door = optional.get();
		} else {
			throw new RuntimeException(DoorConstants.DOOR_NOT_FOUND + id);
		}
		return Door;
	}

	@Override
	public void deleteById(long id) {
		Optional<Door> optional = doorRepository.findById(id);
		Door door = null;
		if (optional.isPresent()) {
			door = optional.get();
			door.setDeleted(true);
		} else {
			throw new RuntimeException(DoorConstants.DOOR_NOT_FOUND + id);
		}
		this.doorRepository.save(door);
	}

	@Override
	public PaginationDto<Door> searchByField(Long id, String name,String ipAddress, int pageno, String sortField, String sortDir,String orgName) {
		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}
		Page<Door> page = getDoorPage(id, name, ipAddress, pageno, sortField, sortDir,orgName);
		List<Door> doorList = page.getContent();

		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
		PaginationDto<Door> dtoList = new PaginationDto<Door>(doorList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir,
				ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<Door> getDoorPage(Long id, String name, String ipAddress, int pageno, String sortField,
			String sortDir, String orgName) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);

		Specification<Door> idSpc = generalSpecification.longSpecification(id, ApplicationConstants.ID);
		Specification<Door> nameSpc = generalSpecification.stringSpecification(name, ApplicationConstants.NAME);
		Specification<Door> ipAddressSpc = generalSpecification.stringSpecification(ipAddress, DoorConstants.IP_ADDRESS);
		Specification<Door> isDeletedFalse = generalSpecification.isDeletedSpecification();
		Specification<Door> orgSpc = generalSpecification.foreignKeyStringSpecification(orgName, "organization","name");

		Page<Door> page = doorRepository.findAll(idSpc.and(nameSpc).and(ipAddressSpc).and(isDeletedFalse).and(orgSpc), pageable);
		return page;
	}

	
}
