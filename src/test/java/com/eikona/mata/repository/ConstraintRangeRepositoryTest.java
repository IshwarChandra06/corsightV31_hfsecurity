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

import com.eikona.mata.entity.ConstraintRange;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
public class ConstraintRangeRepositoryTest {
	@Autowired
    private ConstraintRangeRepository constraintRangeRepository;
	
	    @Test
	    @Order(1)
	    public void testCreateConstraintRange() {
		ConstraintRange constraintRange= new ConstraintRange("0","18","Teen Age",false);
		ConstraintRange saved=constraintRangeRepository.save(constraintRange);
		assertNotNull(saved);
	    }
	    
	    @Test
	    @Order(2)
	    public void testfindConstraintRangeByType() {
	    	String type="Teen Age";
	    	ConstraintRange constraintRange=constraintRangeRepository.findByType(type);
	    	assertThat(constraintRange.getType().equals(type));
	    	
	    }
	    
	    @Test
	    @Order(3)
	    public void testGetListOfConstraintRange() {
	    	List<ConstraintRange> listConstraintRange=(List<ConstraintRange>) constraintRangeRepository.findAllByIsDeletedFalse();
	    	assertThat(listConstraintRange.size()).isGreaterThan(0);
	    }
	    
	    @Test
	    @Order(4)
	    public void testUpdateConstraintRange() {
	    	String type="Child";
	    	ConstraintRange constraintRange= constraintRangeRepository.findByType("Teen Age");
	 		constraintRange.setType(type);
	    	ConstraintRange updated=constraintRangeRepository.save(constraintRange);
	    	assertThat(updated.getType()).isEqualTo(type);
	    	
	    }
	    
	    
	    @Test
	    @Rollback(false)
	    @Order(5)
	    public void testDeleteConstraintRange() {
	    	ConstraintRange constraintRange = constraintRangeRepository.findByType("Child");
	    	constraintRangeRepository.delete(constraintRange);
	        ConstraintRange deletedConstraintRange = constraintRangeRepository.findByType("Child");
	         
	        assertThat(deletedConstraintRange).isNull();
	    }
}
