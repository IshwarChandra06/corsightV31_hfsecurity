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

import com.eikona.mata.entity.EmailSetup;
import com.eikona.mata.entity.Organization;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
public class EmailSetupRepositoryTest {
	@Autowired
    private EmailSetUpRepository emailSetupRepository;
	
	@Autowired
    private OrganizationRepository organizationRepository;
	
	    @Test
	    @Order(1)
	    public void testCreateEmailSetup() {
		    Organization org= organizationRepository.findByNameAndIsDeletedFalse("Tata");
			EmailSetup emailSetup= new EmailSetup(org,"ishwar.mahapatra@eikona.tech","eikona@1234","587",false);
			EmailSetup saved=emailSetupRepository.save(emailSetup);
			assertNotNull(saved);
	    }
	    
	    @Test
	    @Order(2)
	    public void testfindEmailSetupByUserName() {
	    	String username="ishwar.mahapatra@eikona.tech";
	    	EmailSetup emailSetup=emailSetupRepository.findByUserName(username);
	    	assertThat(emailSetup.getUserName().equals(username));
	    	
	    }
	    
	    @Test
	    @Order(3)
	    public void testGetListOfEmailSetup() {
	    	List<EmailSetup> listEmailSetup=(List<EmailSetup>) emailSetupRepository.findAllByIsDeletedFalse();
	    	assertThat(listEmailSetup.size()).isGreaterThan(0);
	    }
	    
	    @Test
	    @Order(4)
	    public void testUpdateEmailSetup() {
	    	String username="pradeep.thapa@eikona.tech";
	    	EmailSetup emailSetup= emailSetupRepository.findByUserName("ishwar.mahapatra@eikona.tech");
	 		emailSetup.setUserName(username);
	    	EmailSetup updated=emailSetupRepository.save(emailSetup);
	    	assertThat(updated.getUserName()).isEqualTo(username);
	    	
	    }
	    
	    
	    @Test
	    @Order(5)
	    public void testDeleteEmailSetup() {
	    	EmailSetup emailSetup = emailSetupRepository.findByUserName("pradeep.thapa@eikona.tech");
	    	emailSetupRepository.delete(emailSetup);
	        EmailSetup deletedEmailSetup = emailSetupRepository.findByUserName("pradeep.thapa@eikona.tech");
	         
	        assertThat(deletedEmailSetup).isNull();
	    }
}
