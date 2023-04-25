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
import com.eikona.mata.entity.Device;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
public class DeviceRepositoryTest {
	
	@Autowired
    private DeviceRepository deviceRepository;
	
	@Autowired
    private  BranchRepository branchRepository;
	
	    @Test
	    @Order(1)
	    public void testCreateDevice() {
		 Branch branch=branchRepository.findByNameAndIsDeletedFalse("Bangalore");
		 Device device= new Device("E1","E1-065",branch,"UNV",false);
		 Device saved=deviceRepository.save(device);
		 assertNotNull(saved);
	    }
	    
	    @Test
	    @Order(2)
	    public void testfindDeviceByName() {
	    	String name="E1";
	    	Device device=deviceRepository.findByNameAndIsDeletedFalse(name);
	    	assertThat(device.getName().equals(name));
	    	
	    }
	    
	    @Test
	    @Order(3)
	    public void testGetListOfDevice() {
	    	List<Device> listDevice=(List<Device>) deviceRepository.findAllByIsDeletedFalse();
	    	assertThat(listDevice.size()).isGreaterThan(0);
	    }
	    
	    @Test
	    @Order(4)
	    public void testUpdateDevice() {
	    	 String name="D1";
	 		Device device= deviceRepository.findByNameAndIsDeletedFalse("E1");
	 		device.setName(name);
	    	Device updated=deviceRepository.save(device);
	    	assertThat(updated.getName()).isEqualTo(name);
	    	
	    }
	    
	    
	    @Test
	    @Order(5)
	    public void testDeleteDevice() {
	    	Device device = deviceRepository.findByNameAndIsDeletedFalse("D1");
	    	deviceRepository.delete(device);
	        Device deletedDevice = deviceRepository.findByNameAndIsDeletedFalse("D1");
	         
	        assertThat(deletedDevice).isNull();
	    }

}
