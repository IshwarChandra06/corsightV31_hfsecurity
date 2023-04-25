package com.eikona.mata.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.eikona.mata.entity.Organization;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@EnableAutoConfiguration
@ActiveProfiles("test")
public class OrganizationRepositoryTest {
	
	@Autowired
    private OrganizationRepository organizationRepository;
	
	    @Test
	    @Order(1)
	    public void testCreateOrganization() {
		 Organization organization= new Organization("Incap",false);
		 Organization saved=organizationRepository.save(organization);
		 assertNotNull(saved);
	    }
	    
	    @Test
	    @Order(2)
	    public void testfindOrganizationByName() {
	    	String name="Incap";
	    	Organization organization=organizationRepository.findByNameAndIsDeletedFalse(name);
	    	assertThat(organization.getName().equals(name));
	    	
	    }
	    
	    @Test
	    @Order(3)
	    public void testGetListOfOrganization() {
	    	List<Organization> listOrganization=(List<Organization>) organizationRepository.findAllByIsDeletedFalse();
	    	assertThat(listOrganization.size()).isGreaterThan(0);
	    }
	    
	    @Test
	    @Order(4)
	    public void testUpdateOrganization() {
	    	 String name="MyHome";
	 		Organization organization= organizationRepository.findByNameAndIsDeletedFalse("Incap");
	 		organization.setName(name);
	    	Organization updated=organizationRepository.save(organization);
	    	assertThat(updated.getName()).isEqualTo(name);
	    	
	    }
	    
	    
	    @Test
	    @Order(5)
	    public void testDeleteOrganization() {
	    	Organization organization = organizationRepository.findByNameAndIsDeletedFalse("MyHome");
	    	organizationRepository.delete(organization);
	        Organization deletedOrganization = organizationRepository.findByNameAndIsDeletedFalse("MyHome");
	         
	        assertThat(deletedOrganization).isNull();
	    }
}
