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

import com.eikona.mata.entity.Designation;
import com.eikona.mata.entity.Organization;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
public class DesignationRepositoryTest {
	@Autowired
    private DesignationRepository designationRepository;
	
	@Autowired
    private OrganizationRepository organizationRepository;
	
	    @Test
	    @Order(1)
	    public void testCreateDesignation() {
		 Organization org= organizationRepository.findByNameAndIsDeletedFalse("Tata");
		 Designation designation= new Designation("Developer","description",org,false);
		 Designation saved=designationRepository.save(designation);
		 assertNotNull(saved);
	    }
	    
	    @Test
	    @Order(2)
	    public void testfindDesignationByName() {
	    	String name="Developer";
	    	Designation designation=designationRepository.findByNameAndIsDeletedFalse(name);
	    	assertThat(designation.getName().equals(name));
	    	
	    }
	    
	    @Test
	    @Order(3)
	    public void testGetListOfDesignation() {
	    	List<Designation> listDesignation=(List<Designation>) designationRepository.findAllByIsDeletedFalse();
	    	assertThat(listDesignation.size()).isGreaterThan(0);
	    }
	    
	    @Test
	    @Order(4)
	    public void testUpdateDesignation() {
	    	 String name="Tester";
	 		Designation designation= designationRepository.findByNameAndIsDeletedFalse("Developer");
	 		designation.setName(name);
	    	Designation updated=designationRepository.save(designation);
	    	assertThat(updated.getName()).isEqualTo(name);
	    	
	    }
	    
	    
	    @Test
	    @Order(5)
	    public void testDeleteDesignation() {
	    	Designation designation = designationRepository.findByNameAndIsDeletedFalse("Tester");
	    	designationRepository.delete(designation);
	        Designation deletedDesignation = designationRepository.findByNameAndIsDeletedFalse("Tester");
	         
	        assertThat(deletedDesignation).isNull();
	    }
}
