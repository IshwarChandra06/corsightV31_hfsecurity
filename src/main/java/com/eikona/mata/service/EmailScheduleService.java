package com.eikona.mata.service;


import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.EmailSchedule;

public interface EmailScheduleService {

	List<EmailSchedule> getAll();

	void save(EmailSchedule emailSetUp);

	EmailSchedule getById(long id);

	void deletedById(long id);


	void sendFileFromMail(EmailSchedule emailSchedule, HttpServletResponse response);

	void removeTaskFromScheduler();

	PaginationDto<EmailSchedule> searchByField(Long id, String reportType, String fileType, String toMail,
			String subject, int pageno, String sortField, String sortDir);
}
