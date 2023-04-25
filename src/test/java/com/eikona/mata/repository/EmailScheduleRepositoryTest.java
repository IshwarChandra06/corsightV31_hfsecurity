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

import com.eikona.mata.entity.EmailSchedule;
import com.eikona.mata.entity.Organization;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
public class EmailScheduleRepositoryTest {

	@Autowired
	private EmailScheduleRepository emailScheduleRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Test
	@Order(1)
	public void testCreateEmailSchedule() {
		Organization org = organizationRepository.findByNameAndIsDeletedFalse("Tata");
		EmailSchedule emailSchedule = new EmailSchedule(org, "Daily Report", "csv", false);
		EmailSchedule saved = emailScheduleRepository.save(emailSchedule);
		assertNotNull(saved);
	}

	@Test
	@Order(2)
	public void testfindEmailScheduleById() {
		String reportType = "Daily Report";
		EmailSchedule emailSchedule = emailScheduleRepository.findByReportType("Daily Report");
		assertThat(emailSchedule.getReportType().equals(reportType));

	}

	@Test
	@Order(3)
	public void testGetListOfEmailSchedule() {
		List<EmailSchedule> listEmailSchedule = (List<EmailSchedule>) emailScheduleRepository.findAllByIsDeletedFalse();
		assertThat(listEmailSchedule.size()).isGreaterThan(0);
	}

	@Test
	@Order(4)
	public void testUpdateEmailSchedule() {
		String reportType = "Visitor Report";
		EmailSchedule emailSchedule = emailScheduleRepository.findByReportType("Daily Report");
		emailSchedule.setReportType(reportType);
		EmailSchedule updated = emailScheduleRepository.save(emailSchedule);
		assertThat(updated.getReportType()).isEqualTo(reportType);

	}

	@Test
	@Order(5)
	public void testDeleteEmailSchedule() {
		EmailSchedule emailSchedule = emailScheduleRepository.findByReportType("Visitor Report");
		emailScheduleRepository.delete(emailSchedule);
		EmailSchedule deletedEmailSchedule = emailScheduleRepository.findByReportType("Visitor Report");

		assertThat(deletedEmailSchedule).isNull();
	}

}
