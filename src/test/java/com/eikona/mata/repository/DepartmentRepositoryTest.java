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

import com.eikona.mata.entity.Department;
import com.eikona.mata.entity.Organization;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
public class DepartmentRepositoryTest {
	
	@Autowired
    private DepartmentRepository departmentRepository;
	
	@Autowired
    private OrganizationRepository organizationRepository;
	
	    @Test
	    @Order(1)
	    public void testCreateDepartment() {
		 Organization org= organizationRepository.findByNameAndIsDeletedFalse("Tata");
		 Department department= new Department("IT",org,false);
		 Department saved=departmentRepository.save(department);
		 assertNotNull(saved);
	    }
	    
	    @Test
	    @Order(2)
	    public void testfindDepartmentByName() {
	    	String name="IT";
	    	Department department=departmentRepository.findByNameAndIsDeletedFalse(name);
	    	assertThat(department.getName().equals(name));
	    	
	    }
	    
	    @Test
	    @Order(3)
	    public void testGetListOfDepartment() {
	    	List<Department> listDepartment=(List<Department>) departmentRepository.findAllByIsDeletedFalse();
	    	assertThat(listDepartment.size()).isGreaterThan(0);
	    }
	    
	    @Test
	    @Order(4)
	    public void testUpdateDepartment() {
	    	 String name="Mechanical";
	 		Department department= departmentRepository.findByNameAndIsDeletedFalse("IT");
	 		department.setName(name);
	    	Department updated=departmentRepository.save(department);
	    	assertThat(updated.getName()).isEqualTo(name);
	    	
	    }
	    
	    
	    @Test
	    @Order(5)
	    public void testDeleteDepartment() {
	    	Department department = departmentRepository.findByNameAndIsDeletedFalse("Mechanical");
	    	departmentRepository.delete(department);
	        Department deletedDepartment = departmentRepository.findByNameAndIsDeletedFalse("Mechanical");
	         
	        assertThat(deletedDepartment).isNull();
	    }

}
