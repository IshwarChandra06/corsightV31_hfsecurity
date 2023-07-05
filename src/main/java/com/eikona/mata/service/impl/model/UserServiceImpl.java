package com.eikona.mata.service.impl.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.constants.UserConstants;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.User;
import com.eikona.mata.repository.UserRepository;
import com.eikona.mata.service.UserService;
import com.eikona.mata.util.GeneralSpecificationUtil;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private GeneralSpecificationUtil<User> generalSpecification;

	@Override
	public List<User> getAll() {
		return userRepository.findAllByIsDeletedFalse();

	}

	@Override
	public void save(User user) {
		user.setDeleted(false);
		user.setActive(true);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		this.userRepository.save(user);

	}

	@Override
	public User getById(long id) {
		Optional<User> optional = userRepository.findById(id);
		User user = null;
		if (optional.isPresent()) {
			user = optional.get();
		} else {
			throw new RuntimeException(UserConstants.USER_NOT_FOUND + id);
		}
		return user;

	}

	@Override
	public void deleteById(long id) {
		Optional<User> optional = userRepository.findById(id);
		User user = null;
		if (optional.isPresent()) {
			user = optional.get();
			user.setDeleted(true);
		} else {
			throw new RuntimeException(UserConstants.USER_NOT_FOUND + id);
		}
		this.userRepository.save(user);
	}

	@Override
	public PaginationDto<User> searchByField(Long id, String name, String phone, String role, int pageno,
			String sortField, String sortDir,String org) {

		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}

		Page<User> page = getSpecificationOfUser(id, name, phone, role, pageno, sortField, sortDir,org);
		
		List<User> userList = page.getContent();

		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
		PaginationDto<User> dtoList = new PaginationDto<User>(userList, page.getTotalPages(), page.getNumber() + NumberConstants.ONE,
				page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<User> getSpecificationOfUser(Long id, String name, String phone, String role, int pageno, String sortField, String sortDir, String organization) {

		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();
		
		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);
		
		Specification<User> idSpec = generalSpecification.longSpecification(id, ApplicationConstants.ID);
		Specification<User> nameSpec = generalSpecification.stringSpecification(name, UserConstants.USER_NAME);
		Specification<User> phoneSpec = generalSpecification.stringSpecification(phone, UserConstants.PHONE);
		Specification<User> roleSpec = generalSpecification.foreignKeyStringSpecification(role, UserConstants.ROLE, ApplicationConstants.NAME);
		Specification<User> isDeletedFalse = generalSpecification.isDeletedSpecification();
		Specification<User> orgSpc = generalSpecification.foreignKeyStringSpecification(organization, "organization", ApplicationConstants.NAME);

		Page<User> page = userRepository.findAll(idSpec.and(nameSpec).and(isDeletedFalse).and(phoneSpec).and(roleSpec).and(orgSpc),
				pageable);
		return page;
	}
}
