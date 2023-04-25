package com.eikona.mata.service;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Action;
import com.eikona.mata.entity.ActionDetails;
import com.eikona.mata.entity.Device;

public interface ActionDetailsService {

	Object getAll();

	void save(ActionDetails actionDetails);

	ActionDetails getById(long id);

	void deleteById(long id);

	String saveAsAction(Action action);

	String saveAsDeviceAction(Action action,Device device);

	PaginationDto<ActionDetails> searchByField(String sDate, String eDate, String employeeId, String name, String device,
			int pageno, String sortField, String sortDir);

}
