package com.eikona.mata.springbatch.processor;


import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.eikona.mata.entity.Employee;

@Component
public class EmployeeDBProcessor implements  ItemProcessor<Employee, Employee>{
	
	@Override
    public Employee process(Employee employeeObj) throws Exception {
       Employee employee = new Employee();
       employee.setId(employeeObj.getId());
       employee.setEmpId(employeeObj.getEmpId());
       employee.setName(employeeObj.getName());
       employee.setJoinDate(employeeObj.getJoinDate());
        return employee;
    }
}