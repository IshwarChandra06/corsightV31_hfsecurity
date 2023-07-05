package com.eikona.mata.service;


import java.util.List;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.EmailSetup;

public interface EmailSetUpService {
	List<EmailSetup> getAll();

	void save(EmailSetup emailSetUp);

	EmailSetup getById(long id);

	void deletedById(long id);

	PaginationDto<EmailSetup> searchByField(Long id, String smppServer, String port, String userName, String senderName,
			int pageno, String sortField, String sortDir, String orgName);

}