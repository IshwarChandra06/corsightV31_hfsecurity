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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import com.eikona.mata.entity.ConstraintSingle;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
public class ConstraintSingleRepositoryTest {
	@Autowired
    private ConstraintSingleRepository constraintSingleRepository;
	
	    @Test
	    @Order(1)
	    public void testCreateConstraintSingle() {
		ConstraintSingle constraintSingle= new ConstraintSingle("1","Quality",false);
		ConstraintSingle saved=constraintSingleRepository.save(constraintSingle);
		assertNotNull(saved);
	    }
	    
	    @Test
	    @Order(2)
	    public void testfindConstraintSingleByValue() {
	    	String value="1";
	    	ConstraintSingle constraintSingle=constraintSingleRepository.findByValue(value);
	    	assertThat(constraintSingle.getValue().equals(value));
	    	
	    }
	    
	    @Test
	    @Order(3)
	    public void testGetListOfConstraintSingle() {
	    	List<ConstraintSingle> listConstraintSingle=(List<ConstraintSingle>) constraintSingleRepository.findAllByIsDeletedFalse();
	    	assertThat(listConstraintSingle.size()).isGreaterThan(0);
	    }
	    
	    @Test
	    @Rollback(false)
	    @Order(4)
	    public void testUpdateConstraintSingle() {
	    	String value="2";
	    	ConstraintSingle constraintSingle= constraintSingleRepository.findByValue("1");
	 		constraintSingle.setValue(value);
	    	ConstraintSingle updated=constraintSingleRepository.save(constraintSingle);
	    	assertThat(updated.getValue()).isEqualTo(value);
	    	
	    }
	    
	    
	    @Test
	    @Rollback(false)
	    @Order(5)
	    public void testDeleteConstraintSingle() {
	    	ConstraintSingle constraintSingle = constraintSingleRepository.findByValue("2");
	    	constraintSingleRepository.delete(constraintSingle);
	        ConstraintSingle deletedConstraintSingle = constraintSingleRepository.findByValue("2");
	         
	        assertThat(deletedConstraintSingle).isNull();
	    }
}
