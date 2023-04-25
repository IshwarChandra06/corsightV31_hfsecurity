package com.eikona.mata.service;

import java.security.Principal;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Action;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;

public interface ActionService {

	Object getAll();

	void save(Action action);

	Action getById(long id);

	void deleteById(long id);

	void schedulingActionStatusNew();

	void employeeAction(Employee employee, String type, String source, String deviceName, Principal principal);

	String employeeDeviceAction(Device device,Employee employee, String type, String source, String deviceName, Principal principal);

	PaginationDto<Action> searchByField(String sDate, String eDate, String employeeId, String name, String status,
			int pageno, String sortField, String sortDir);

}
