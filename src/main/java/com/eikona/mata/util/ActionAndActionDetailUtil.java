package com.eikona.mata.util;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.entity.Action;
import com.eikona.mata.entity.ActionDetails;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.User;
import com.eikona.mata.repository.ActionDetailsRepository;
import com.eikona.mata.repository.ActionRepository;
import com.eikona.mata.repository.UserRepository;

@Component
public class ActionAndActionDetailUtil {
	
	@Autowired
	private UserRepository  userRepository;
	
	@Autowired
	private ActionRepository actionRepository;
	
	@Autowired
	private ActionDetailsRepository actionDetailsRepository;
	
	public Action saveAction(Employee employee, Principal principal, String status, Long id) {
		
		
		Action action = new Action();
		if(NumberConstants.LONG_ZERO != id) {
			action = actionRepository.findById(id).get();
		}
		action.setType(ApplicationConstants.SYNC);
		action.setEmployee(employee);
		action.setDeleted(false);
		action.setSource(ApplicationConstants.ACCESS_TYPE_APP);
		action.setDevice(ApplicationConstants.DELIMITER_EMPTY);
		action.setStatus(status);
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		action.setModifiedUser(user.getUserName());
		
		return actionRepository.save(action);
	}
	
	public void saveActionDetail(Action action, Device device, String status, String msg) {
		ActionDetails actionDetails = new ActionDetails();
		actionDetails.setAction(action);
		actionDetails.setDevice(device);
		actionDetails.setDeleted(false);
		actionDetails.setStatus(status);
		actionDetails.setMessage(msg);
		actionDetailsRepository.save(actionDetails);
		
	}
}
