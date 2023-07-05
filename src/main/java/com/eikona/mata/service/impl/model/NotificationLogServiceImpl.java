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
import com.eikona.mata.constants.NotificationLogConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.NotificationLog;
import com.eikona.mata.repository.NotificationLogRepository;
import com.eikona.mata.service.NotificationLogService;
import com.eikona.mata.util.GeneralSpecificationUtil;

@Service
public class NotificationLogServiceImpl implements NotificationLogService {

	@Autowired
    private  NotificationLogRepository  notificationLogRepository;
	
	@Autowired
    private GeneralSpecificationUtil<NotificationLog> generalSpecification;
	
	@Override
	public List<NotificationLog> getAll() {
		return (List<NotificationLog>) notificationLogRepository.findAll();
	}

	@Override
	public PaginationDto<NotificationLog> searchByField(Long id, String notificationType, String sender,
			String receipent, String subject, String reportType, int pageno, String sortField, String sortDir,String organization) {
		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}
		Page<NotificationLog> page = getNotificationLogPage(id, notificationType, sender, receipent, subject,
				reportType, pageno, sortField, sortDir,organization);
        List<NotificationLog> employeeShiftList =  page.getContent();
		
		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir))?ApplicationConstants.DESC:ApplicationConstants.ASC;
		PaginationDto<NotificationLog> dtoList = new PaginationDto<NotificationLog>(employeeShiftList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<NotificationLog> getNotificationLogPage(Long id, String notificationType, String sender,
			String receipent, String subject, String reportType, int pageno, String sortField, String sortDir, String organization) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);
		
		Specification<NotificationLog> idSpec = generalSpecification.longSpecification(id, ApplicationConstants.ID);
    	Specification<NotificationLog> notificationTypeSpec = generalSpecification.stringSpecification(notificationType,NotificationLogConstants.NOTIFICATION_TYPE);
    	Specification<NotificationLog> senderSpec = generalSpecification.stringSpecification(sender,NotificationLogConstants.SENDER);
    	Specification<NotificationLog> receipentSpec = generalSpecification.stringSpecification(receipent,NotificationLogConstants.RECEIPENTS); 
    	Specification<NotificationLog> subjectSpec = generalSpecification.stringSpecification(subject,NotificationLogConstants.SUBJECT); 
    	Specification<NotificationLog>  reportTypeSpec = generalSpecification.stringSpecification(reportType,NotificationLogConstants.REPORT_TYPE);
    	Specification<NotificationLog>  orgSpec = generalSpecification.foreignKeyStringSpecification(organization,"organization","name");
		
    	Page<NotificationLog> page = notificationLogRepository.findAll(idSpec.and(notificationTypeSpec).and(senderSpec).and(receipentSpec).and(subjectSpec).and(reportTypeSpec).and(orgSpec), pageable);
		return page;
	}
	
	

	
	
	
	
}
