package com.eikona.mata.springbatch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eikona.mata.entity.Employee;
import com.eikona.mata.repository.DeviceRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.util.EmployeeObjectMap;
@Component
public class FaceSyncToHFSecurityWriter implements ItemWriter<Employee> {
	
	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private EmployeeObjectMap employeeObjectMap;
	
	@Override
	public void write(List<? extends Employee> employeeList) throws Exception {
		
	}

}
