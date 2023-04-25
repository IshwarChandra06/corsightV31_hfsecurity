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

import com.eikona.mata.entity.Shift;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
public class ShiftRepositoryTest {
	@Autowired
    private ShiftRepository shiftRepository;
	
	    @Test
	    @Order(1)
	    public void testCreateShift() {
		Shift shift= new Shift("Day",false);
		Shift saved=shiftRepository.save(shift);
		assertNotNull(saved);
	    }
	    
	    @Test
	    @Order(2)
	    public void testfindShiftByName() {
	    	String name="Day";
	    	Shift shift=shiftRepository.findByNameAndIsDeletedFalse(name);
	    	assertThat(shift.getName().equals(name));
	    	
	    }
	    
	    @Test
	    @Order(3)
	    public void testGetListOfShift() {
	    	List<Shift> listShift=(List<Shift>) shiftRepository.findAllByIsDeletedFalse();
	    	assertThat(listShift.size()).isGreaterThan(0);
	    }
	    
	    @Test
	    @Order(4)
	    public void testUpdateShift() {
	    	String name="Night";
	    	Shift shift= shiftRepository.findByNameAndIsDeletedFalse("Day");
	 		shift.setName(name);
	    	Shift updated=shiftRepository.save(shift);
	    	assertThat(updated.getName()).isEqualTo(name);
	    	
	    }
	    
	    
	    @Test
	    @Order(5)
	    public void testDeleteShift() {
	    	Shift shift = shiftRepository.findByNameAndIsDeletedFalse("Night");
	    	shiftRepository.delete(shift);
	        Shift deletedShift = shiftRepository.findByNameAndIsDeletedFalse("Night");
	         
	        assertThat(deletedShift).isNull();
	    }
}
