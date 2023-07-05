package com.eikona.mata.controller;


import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.NotificationLog;
import com.eikona.mata.entity.User;
import com.eikona.mata.repository.UserRepository;
import com.eikona.mata.service.NotificationLogService;


@Controller
public class NotificationLogController {

	@Autowired
	private NotificationLogService notificationLogService;
	
	@Autowired
	private UserRepository userRepository;
	
	
	@GetMapping("/notification/log")
	@PreAuthorize("hasAuthority('notification_log_view')")
	public String viewHomePage(Model model) {
		model.addAttribute("notificationLog", notificationLogService. getAll());
		return "notificationLog/notificationLogIndex";
	}
	
	@GetMapping(value = "/api/search/notification-log")
	@PreAuthorize("hasAuthority('notification_log_view')")
	public @ResponseBody PaginationDto<NotificationLog> searchVehicleLog(Long id, String notificationType,String sender, String receipent, String subject, String reportType, 
			int pageno, String sortField, String sortDir, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String orgName = (null == user.getOrganization()? null: user.getOrganization().getName());
		
		PaginationDto<NotificationLog> dtoList = notificationLogService.searchByField(id,notificationType , sender, receipent, subject, reportType, pageno, sortField, sortDir,orgName);
		
		return dtoList;
	}
}
