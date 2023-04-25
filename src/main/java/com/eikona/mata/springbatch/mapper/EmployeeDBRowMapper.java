package com.eikona.mata.springbatch.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.eikona.mata.entity.Employee;

@Component
public class EmployeeDBRowMapper implements RowMapper<Employee> {

    @Override
    public Employee mapRow(ResultSet resultSet, int i) throws SQLException {
    	
        Employee employee = new Employee();
        employee.setId(resultSet.getLong("id"));
        employee.setEmpId(resultSet.getString("empId"));
        employee.setName(resultSet.getString("name"));
        employee.setJoinDate(resultSet.getString("joinDate"));
        return employee;
    }
}
