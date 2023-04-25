package com.eikona.mata.service.impl.model;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import com.eikona.mata.constants.ActionDetailsConstants;
import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.BranchConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Action;
import com.eikona.mata.entity.ActionDetails;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.User;
import com.eikona.mata.repository.ActionDetailsRepository;
import com.eikona.mata.repository.ActionRepository;
import com.eikona.mata.repository.UserRepository;
import com.eikona.mata.service.ActionDetailsService;
import com.eikona.mata.service.ActionService;
import com.eikona.mata.util.CalendarUtil;
import com.eikona.mata.util.GeneralSpecificationUtil;

@Service
@EnableScheduling
public class ActionServiceImpl implements ActionService {

	@Autowired
	private ActionRepository actionRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ActionDetailsRepository actionDetailsRepository;

	@Autowired
	private ActionDetailsService actionDetailsService;

	@Autowired
	private ActionDetailsServiceImpl actionDetailsServiceImpl;
	
	@Autowired
	private CalendarUtil  calendarUtil;
	
	@Autowired
	private GeneralSpecificationUtil<Action> generalSpecificationAction;

	@Autowired
	private GeneralSpecificationUtil<ActionDetails> generalSpecificationActionDetails;

	@Override
	public Object getAll() {
		return actionRepository.findAll();
	}

	@Override
	public void save(Action action) {
		action.setDeleted(false);
		action = actionRepository.save(action);
		actionDetailsService.saveAsAction(action);
	}

	@Override
	public Action getById(long id) {
		return actionRepository.findById(id).get();
	}

	@Override
	public void deleteById(long id) {
		Optional<Action> actionOption = actionRepository.findById(id);
		Action action = null;

		if (actionOption.isPresent()) {
			action = actionOption.get();
			action.setDeleted(true);
		} else {
			throw new RuntimeException(BranchConstants.NOT_FOUND_FOR_ID + id);
		}
		this.actionRepository.save(action);
	}

	@Override
	public void employeeAction(Employee employee, String type, String source, String deviceName,Principal principal) {
		Action action = new Action();
		action.setType(type);
		action.setEmployee(employee);
		action.setDeleted(false);
		action.setStatus(ApplicationConstants.NEW);
		action.setSource(source);
		action.setDevice(deviceName);
		User user=userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		action.setModifiedUser(user.getUserName());
		action = actionRepository.save(action);
		String status = actionDetailsService.saveAsAction(action);
		if (!(ApplicationConstants.NEW.equalsIgnoreCase(status))) {
			action.setStatus(status);
			actionRepository.save(action);
		}
	}
	
	@Override
	public String employeeDeviceAction(Device device,Employee employee, String type, String source, String deviceName,Principal principal) {
		Action action = new Action();
		action.setType(type);
		action.setEmployee(employee);
		action.setDeleted(false);
		action.setStatus(ApplicationConstants.NEW);
		action.setSource(source);
		action.setDevice(deviceName);
		User user=userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		if(ApplicationConstants.SYSTEM.equalsIgnoreCase(principal.getName())) 
			action.setModifiedUser(principal.getName());
		else
			action.setModifiedUser(user.getUserName());
		action = actionRepository.save(action);
		String status = actionDetailsService.saveAsDeviceAction(action,device);
		if (!(ApplicationConstants.NEW.equalsIgnoreCase(status))) {
			action.setStatus(status);
			actionRepository.save(action);
		}
		
		return status;
	}

	@Override
	// @Scheduled(cron ="0 0 * ? * *")
	//@Scheduled(fixedDelay = 3000)
	public void schedulingActionStatusNew() {

		Specification<Action> actionisDeletedFalse = generalSpecificationAction.isDeletedSpecification();
		
		Specification<Action> actionStatusPending = generalSpecificationAction.stringSpecification(ApplicationConstants.PENDING, ActionDetailsConstants.STATUS);

		List<Action> actionNewList = actionRepository.findAll(actionStatusPending.and(actionisDeletedFalse));

		for (Action action : actionNewList) {
			String status = ApplicationConstants.COMPLETED;
			Specification<ActionDetails> isDeletedFalse = generalSpecificationActionDetails.isDeletedSpecification();
			
			Specification<ActionDetails> ispending = generalSpecificationActionDetails.stringSpecification(ApplicationConstants.PENDING, ActionDetailsConstants.STATUS);
					
			Specification<ActionDetails> accordingToActionObj = generalSpecificationActionDetails.objectSpecification(action, ActionDetailsConstants.ACTION);
			
			List<ActionDetails> actionDetailsSpecList = actionDetailsRepository.findAll(isDeletedFalse.and(ispending).and(accordingToActionObj));

//			get status from action details 
			status = getStatusFromActionDetails(action, status, actionDetailsSpecList);
			
			action.setStatus(status);
			actionRepository.save(action);
		}
	}

	private String getStatusFromActionDetails(Action action, String status, List<ActionDetails> actionDetailsSpecList) {
		for (ActionDetails actionDetails : actionDetailsSpecList) {
			
			String actionDetailsStatus = actionDetailsServiceImpl.saveAsDeviceAction(action, actionDetails.getDevice()); 
			
			if(ApplicationConstants.ERROR.equalsIgnoreCase(actionDetailsStatus)) {
				status = ApplicationConstants.ERROR;
			}else if(ApplicationConstants.PENDING.equalsIgnoreCase(actionDetailsStatus)) {
				status = ApplicationConstants.PENDING;
			}
		}
		return status;
	}

	@Override
	public PaginationDto<Action> searchByField(String sDate, String eDate, String employeeId, String name,
			String status, int pageno, String sortField, String sortDir) {
	
		Date startDate = null;
		Date endDate = null;
		if (!sDate.isEmpty() && !eDate.isEmpty()) {
			SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
			try {
				startDate = format.parse(sDate);
				endDate = format.parse(eDate);
				
				endDate = calendarUtil.getConvertedDate(endDate, NumberConstants.TWENTY_THREE, NumberConstants.FIFTY_NINE, NumberConstants.FIFTY_NINE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}
		Page<Action> page = getPageData(startDate,endDate, employeeId, name, status, pageno, sortField, sortDir);
        List<Action> employeeList =  page.getContent();
        
		
		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir))?ApplicationConstants.DESC:ApplicationConstants.ASC;
		PaginationDto<Action> dtoList = new PaginationDto<Action>(employeeList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	
	}

	private Page<Action> getPageData(Date startDate, Date endDate, String employeeId, String name, String status,
			int pageno, String sortField, String sortDir) {

		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);
		Specification<Action> isDeletedFalse = generalSpecificationAction.isDeletedSpecification(false);
		
		Specification<Action> dateSpec = generalSpecificationAction.dateSpecification(startDate, endDate, ApplicationConstants.LAST_MODIFIED_DATE);
		Specification<Action> empIdSpc = generalSpecificationAction.foreignKeyStringSpecification(employeeId, "employee", "empId");
		Specification<Action> employeeNameSpc = generalSpecificationAction.foreignKeyStringSpecification(name,"employee", "name");
		Specification<Action> statusSpc = generalSpecificationAction.stringSpecification(status, "status");
		
    	Page<Action> page = actionRepository.findAll(isDeletedFalse.and(dateSpec).and(empIdSpc).and(employeeNameSpc).and(statusSpc), pageable);
		return page;
	
	}
}
