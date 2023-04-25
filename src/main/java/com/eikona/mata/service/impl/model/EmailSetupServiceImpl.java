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
import com.eikona.mata.constants.EmailSetupConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.EmailSetup;
import com.eikona.mata.repository.EmailSetUpRepository;
import com.eikona.mata.service.EmailSetUpService;
import com.eikona.mata.util.GeneralSpecificationUtil;

@Service
public class EmailSetupServiceImpl implements EmailSetUpService{
	@Autowired
	private EmailSetUpRepository emailSetUpRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private GeneralSpecificationUtil<EmailSetup> generalSpecification;

	@Override
	public List<EmailSetup> getAll() {
		return emailSetUpRepository.findAllByIsDeletedFalse();
	}

	@Override
	public void save(EmailSetup emailSetup) {
		emailSetup.setDeleted(false);
		emailSetup.setPassword(passwordEncoder.encode(emailSetup.getPassword()));
		this.emailSetUpRepository.save(emailSetup);
	}

	@Override
	public EmailSetup getById(long id) {
		Optional<EmailSetup> optional = emailSetUpRepository.findById(id);
		EmailSetup emailSetup = null;
		if (optional.isPresent()) {
			emailSetup = optional.get();
		} else {
			throw new RuntimeException(EmailSetupConstants.EMAIL_SETUP_NOT_FOUND + id);
		}
		return emailSetup;
	}

	@Override
	public void deletedById(long id) {
		Optional<EmailSetup> optional = emailSetUpRepository.findById(id);
		EmailSetup emailSetup = null;
		if (optional.isPresent()) {
			emailSetup = optional.get();
			emailSetup.setDeleted(true);
		} else {
			throw new RuntimeException(EmailSetupConstants.EMAIL_SETUP_NOT_FOUND + id);
		}
		this.emailSetUpRepository.save(emailSetup);
	}
	
	 
	@Override
	public PaginationDto<EmailSetup> searchByField(Long id, String smppServer, String port, String userName,
			String senderName, int pageno, String sortField, String sortDir) {
		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}
		Page<EmailSetup> page = getEmailSetupPage(id, smppServer, port, userName, senderName, pageno, sortField,sortDir);
        List<EmailSetup> emailSetupList =  page.getContent();
		
		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir))?ApplicationConstants.DESC:ApplicationConstants.ASC;
		PaginationDto<EmailSetup> dtoList = new PaginationDto<EmailSetup>(emailSetupList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<EmailSetup> getEmailSetupPage(Long id, String smppServer, String port, String userName,
			String senderName, int pageno, String sortField, String sortDir) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);
		
		Specification<EmailSetup> idSpc = generalSpecification.longSpecification(id, ApplicationConstants.ID);
		Specification<EmailSetup> smppServerSpc = generalSpecification.stringSpecification(smppServer, EmailSetupConstants.SMPP_SERVER);
		Specification<EmailSetup> portSpc = generalSpecification.stringSpecification(port, EmailSetupConstants.PORT_NO);
		Specification<EmailSetup> userNameSpc = generalSpecification.stringSpecification(userName, EmailSetupConstants.USER_NAME);
		Specification<EmailSetup> senderNameSpc = generalSpecification.stringSpecification(senderName, EmailSetupConstants.SENDER_NAME);
		Specification<EmailSetup> isDeletedFalse = generalSpecification.isDeletedSpecification();
		
    	Page<EmailSetup> page = emailSetUpRepository.findAll(idSpc.and(smppServerSpc).and(isDeletedFalse).and(senderNameSpc).and(userNameSpc).and(portSpc),pageable);
		return page;
	}
	
	
}
