package com.eikona.mata.service;

import static org.junit.Assert.assertNotNull;

import java.security.Principal;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Area;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.repository.AreaRepository;
import com.eikona.mata.repository.DeviceRepository;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
public class AreaServiceTest {
	
	@Autowired
	private AreaService areaService;
	
	@Autowired
	private AreaRepository areaRepository;
	
	@Autowired
	private DeviceRepository deviceRepository;
	
	@Test
	@Order(5)
	public void saveAreaEmployeeAssociationTest() {
		Principal principal = new Principal() {
			
			@Override
			public String getName() {
				
				return "System";
			}
		}; 
		
		areaService.saveAreaEmployeeAssociation(1l, 1l, principal);
		assertNotNull(principal);
	}
	
//	saveAreaDeviceAssociation(Long deviceId, Long areaId, Principal principal);
	@Test
	@Order(3)
	public void saveAreaDeviceAssociationTest() {
		Principal principal = new Principal() {
			@Override
			public String getName() {
				return "System";
			}
		}; 
		Device device = deviceRepository.findByNameAndIsDeletedFalse("");
		Long devId = (null != device)?device.getId():null;
		Area area = areaRepository.findByNameAndIsDeletedFalse("");
		Long areaId = (null != area)?area.getId():null;
		String message = areaService.saveAreaDeviceAssociation(devId, areaId, principal);
		assertNotNull(message);
	}
	
//	PaginationDto<Area> searchByField(Long id, String name, String office, int pageno, String sortField,
//			String sortDir);
	@Test
	@Order(4)
	public void searchByFieldTest() {
		System.out.println("4");
		PaginationDto<Area> pagination = areaService.searchByField(null, "", "", 1, "id", "asc");
		assertNotNull(pagination.getData());
	}

//	PaginationDto<Employee> searchAreaToEmployee(String id, String name, String office, String area, int pageno,
//			String sortField, String sortDir);
	@Test
	@Order(2)
	public void searchAreaToEmployeeTest() {
		System.out.println("2");
		PaginationDto<Employee> pagination = areaService.searchAreaToEmployee(null, "", "", "", 1, "id", "asc");
		assertNotNull(pagination.getData());
	}

//	PaginationDto<Device> searchAreaToDevice(String name, String office, String area, int pageno,
//			String sortField, String sortDir);
	@Test
	@Order(1)
	public void searchAreaToDeviceTest() {
		System.out.println("1");
		PaginationDto<Device> pagination = areaService.searchAreaToDevice("", "", "", 1, "id", "asc");
		assertNotNull(pagination.getData());
	}
}
