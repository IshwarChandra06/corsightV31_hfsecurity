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

import com.eikona.mata.entity.Parameter;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
public class ParameterRepositoryTest {
	@Autowired
    private ParameterRepository parameterRepository;
	
	    @Test
	    @Order(1)
	    public void testCreateParameter() {
		Parameter parameter= new Parameter("Lunch Break","60 min",false);
		Parameter saved=parameterRepository.save(parameter);
		assertNotNull(saved);
	    }
	    
	    @Test
	    @Order(2)
	    public void testfindParameterByName() {
	    	String name="Lunch Break";
	    	Parameter parameter=parameterRepository.findByName(name);
	    	assertThat(parameter.getName().equals(name));
	    	
	    }
	    
	    @Test
	    @Order(3)
	    public void testGetListOfParameter() {
	    	List<Parameter> listParameter=(List<Parameter>) parameterRepository.findAllByIsDeletedFalse();
	    	assertThat(listParameter.size()).isGreaterThan(0);
	    }
	    
	    @Test
	    @Order(4)
	    public void testUpdateParameter() {
	    	String name="Tea Break";
	    	Parameter parameter= parameterRepository.findByName("Lunch Break");
	 		parameter.setName(name);
	    	Parameter updated=parameterRepository.save(parameter);
	    	assertThat(updated.getName()).isEqualTo(name);
	    	
	    }
	    
	    
	    @Test
	    @Order(5)
	    public void testDeleteParameter() {
	    	Parameter parameter = parameterRepository.findByName("Tea Break");
	    	parameterRepository.delete(parameter);
	        Parameter deletedParameter = parameterRepository.findByName("Tea Break");
	         
	        assertThat(deletedParameter).isNull();
	    }
}
