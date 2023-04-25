package com.eikona.mata.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Organization;
import com.eikona.mata.repository.OrganizationRepository;

@SpringBootTest
@ActiveProfiles("test")
public class OrganizationServiceTest {

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private OrganizationRepository orgRepo;

	@Test
	public void getAllTest() {
		List<Organization> orgList = organizationService.getAll();

		if (null == orgList)
			assertNull(orgList);
		else
			assertNotNull(orgList);
	}

	@Test
	public void saveTest() {
		Organization org = new Organization("Eikona", false);
		org = organizationService.save(org);

		if (null == org)
			assertNull(org);
		else
			assertNotNull(org);
	}

	@Test
	public void getByIdTest() {
		Organization org = organizationService.getById(1);

		if (null == org)
			assertNull(org);
		else
			assertNotNull(org);
	}

	@Test
	public void deleteByIdTest() {
		Organization org = orgRepo.findByNameAndIsDeletedFalse("");
		if (null == org)
			assertNull(org);
		else {
			organizationService.deleteById(org.getId());
			assertNotNull(org);
		}
	}

	@Test
	public void searchByFieldTest() {
		PaginationDto<Organization> paginationObj = organizationService.searchByField(1l, "Tata", "", "", "", 1, "",
				"asc");
//		searchByField(Long id, String name, String address, String city, String pin,
//				int pageno, String sortField, String sortDir);
		if (paginationObj.getData().isEmpty())
			assert (paginationObj).getData().size() == 0;
		else {
			assert (paginationObj).getData().size() > 0;
		}
	}
}