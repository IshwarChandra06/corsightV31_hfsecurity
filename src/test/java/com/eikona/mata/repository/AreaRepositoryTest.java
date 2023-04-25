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

import com.eikona.mata.entity.Area;
import com.eikona.mata.entity.Branch;
import com.eikona.mata.entity.Organization;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
public class AreaRepositoryTest {
	
	@Autowired
    private AreaRepository areaRepository;
	
	@Autowired
    private OrganizationRepository organizationRepository;
	
	@Autowired
    private  BranchRepository branchRepository;
	
	    @Test
	    @Order(1)
	    public void testCreateArea() {
		 Organization org= organizationRepository.findByNameAndIsDeletedFalse("Tata");
		 Branch branch=branchRepository.findByNameAndIsDeletedFalse("Bangalore");
		 Area area= new Area("Entrance",org,branch,false);
		 Area saved=areaRepository.save(area);
		 assertNotNull(saved);
	    }
	    
	    @Test
	    @Order(2)
	    public void testfindAreaByName() {
	    	String name="Entrance";
	    	Area area=areaRepository.findByNameAndIsDeletedFalse(name);
	    	assertThat(area.getName().equals(name));
	    	
	    }
	    
	    @Test
	    @Order(3)
	    public void testGetListOfArea() {
	    	List<Area> listArea=(List<Area>) areaRepository.findAllByIsDeletedFalse();
	    	assertThat(listArea.size()).isGreaterThan(0);
	    }
	    
	    @Test
	    @Order(4)
	    public void testUpdateArea() {
	    	 String name="Exit";
	 		Area area= areaRepository.findByNameAndIsDeletedFalse("Entrance");
	 		area.setName(name);
	    	Area updated=areaRepository.save(area);
	    	assertThat(updated.getName()).isEqualTo(name);
	    	
	    }
	    
	    
	    @Test
	    @Order(5)
	    public void testDeleteArea() {
	    	Area area = areaRepository.findByNameAndIsDeletedFalse("Exit");
	    	areaRepository.delete(area);
	        Area deletedArea = areaRepository.findByNameAndIsDeletedFalse("Exit");
	         
	        assertThat(deletedArea).isNull();
	    }

}
