package com.eikona.mata.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.eikona.mata.dto.ShiftSettingDto;
import com.eikona.mata.entity.Organization;
import com.eikona.mata.entity.Parameter;
import com.eikona.mata.entity.Shift;
import com.eikona.mata.repository.OrganizationRepository;
import com.eikona.mata.repository.ShiftRepository;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
public class ShiftServiceTest {

	@Autowired
	private ShiftService shiftService;
	
	@Autowired
	private ShiftRepository shiftRepository;
	
	@Autowired
	private OrganizationRepository organizationRepository;

	@Test
	@Order(1)
	public void saveShift() {
		Shift shift = new Shift("Day", false);
		Shift saved = shiftService.save(shift);
		assertNotNull(saved);
	}
	
	@Test
	@Order(2)
	public void getById() {
		Shift shiftObj=shiftRepository.findByNameAndIsDeletedFalse("Day");
		Shift shift=shiftService.getById(shiftObj.getId());
		if(null == shift)
			assertNull(shift);
		else
			assertNotNull(shift);
	}

	@Test
	@Order(3)
	public void getShiftList() {
		List<Shift> shiftList = shiftService.getAll();
		assertThat(shiftList.size()).isGreaterThan(0);
	}
	
	@Test
	@Order(4)
	public void deleteById() {
		Shift shiftObj=shiftRepository.findByNameAndIsDeletedFalse("Day");
		 shiftService.deleteById(shiftObj.getId());
		 Shift deletedShift=shiftRepository.findByNameAndIsDeletedFalse("Day");
		 assertThat(deletedShift).isNull();
	}

	@Test
	@Order(5)
	public void saveShiftSetting() {

		Organization org = organizationRepository.findByNameAndIsDeletedFalse("Tata");
		ShiftSettingDto shiftSettingDto = new ShiftSettingDto();
		shiftSettingDto.setName("Day");
		shiftSettingDto.setOrganization(org);
		List<Parameter> parameterList = shiftService.saveShiftSetting(shiftSettingDto);
		assertThat(parameterList.size()).isGreaterThan(0);
	}

}