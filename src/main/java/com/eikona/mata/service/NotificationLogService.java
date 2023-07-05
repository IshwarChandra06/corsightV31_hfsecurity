package com.eikona.mata.service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.NotificationLog;

@Service
public interface NotificationLogService {
	List<NotificationLog> getAll();
	 
	PaginationDto<NotificationLog> searchByField(Long id, String notificationType, String sender, String receipent,
			String subject, String reportType, int pageno, String sortField, String sortDir, String orgName);
}
