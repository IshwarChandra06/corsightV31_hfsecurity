package com.eikona.mata.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.eikona.mata.entity.Employee;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
public class EmployeeRepositoryTest {
	@Autowired
    private EmployeeRepository employeeRepository;
	
	
	    @Test
	    @Order(1)
	    public void testCreateEmployee() {
		 Employee employee= new Employee("Ishwar","Eikona02",false);
		 Employee saved=employeeRepository.save(employee);
		 assertNotNull(saved);
	    }
	    
	    @Test
	    @Order(2)
	    public void testfindEmployeeByName() {
	    	String name="Ishwar";
	    	Employee employee=employeeRepository.findByNameAndIsDeletedFalse(name);
	    	assertThat(employee.getName().equals(name));
	    	
	    }
	    
	    @Test
	    @Order(3)
	    public void testGetListOfEmployee() {
	    	List<Employee> listEmployee=(List<Employee>) employeeRepository.findAllByIsDeletedFalse();
	    	assertThat(listEmployee.size()).isGreaterThan(0);
	    }
	    
	    @Test
	    @Order(4)
	    public void testUpdateEmployee() {
	    	 String name="Pradeep";
	 		Employee employee= employeeRepository.findByNameAndIsDeletedFalse("Ishwar");
	 		employee.setName(name);
	    	Employee updated=employeeRepository.save(employee);
	    	assertThat(updated.getName()).isEqualTo(name);
	    	
	    }
	    
	    
	    @Test
	    @Order(5)
	    public void testDeleteEmployee() {
	    	Employee employee = employeeRepository.findByNameAndIsDeletedFalse("Pradeep");
	    	employeeRepository.delete(employee);
	        Employee deletedEmployee = employeeRepository.findByNameAndIsDeletedFalse("Pradeep");
	         
	        assertThat(deletedEmployee).isNull();
	    }
}
