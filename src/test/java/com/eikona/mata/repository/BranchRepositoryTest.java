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

import com.eikona.mata.entity.Branch;
import com.eikona.mata.entity.Organization;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
public class BranchRepositoryTest {
	
	@Autowired
    private BranchRepository branchRepository;
	
	@Autowired
    private OrganizationRepository organizationRepository;
	
	    @Test
	    @Order(1)
	    public void testCreateBranch() {
		 Organization org= organizationRepository.findByNameAndIsDeletedFalse("Tata");
		 Branch branch= new Branch("Bangalore",org,false);
		 Branch saved=branchRepository.save(branch);
		 assertNotNull(saved);
	    }
	    
	    @Test
	    @Order(2)
	    public void testfindBranchByName() {
	    	String name="Bangalore";
	    	Branch branch=branchRepository.findByNameAndIsDeletedFalse(name);
	    	assertThat(branch.getName().equals(name));
	    	
	    }
	    
	    @Test
	    @Order(3)
	    public void testGetListOfBranch() {
	    	List<Branch> listBranch=(List<Branch>) branchRepository.findAllByIsDeletedFalse();
	    	assertThat(listBranch.size()).isGreaterThan(0);
	    }
	    
	    @Test
	    @Order(4)
	    public void testUpdateBranch() {
	    	 String name="Chennai";
	 		Branch branch= branchRepository.findByNameAndIsDeletedFalse("Bangalore");
	 		branch.setName(name);
	    	Branch updated=branchRepository.save(branch);
	    	assertThat(updated.getName()).isEqualTo(name);
	    	
	    }
	    
	    
	    @Test
	    @Order(5)
	    public void testDeleteBranch() {
	    	Branch branch = branchRepository.findByNameAndIsDeletedFalse("Chennai");
	    	branchRepository.delete(branch);
	        Branch deletedBranch = branchRepository.findByNameAndIsDeletedFalse("Chennai");
	         
	        assertThat(deletedBranch).isNull();
	    }

}
