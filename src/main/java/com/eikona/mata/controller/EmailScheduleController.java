package com.eikona.mata.controller;


import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.EmailSchedule;
import com.eikona.mata.entity.Organization;
import com.eikona.mata.entity.User;
import com.eikona.mata.repository.OrganizationRepository;
import com.eikona.mata.repository.UserRepository;
import com.eikona.mata.service.ConstraintSingleService;
import com.eikona.mata.service.EmailScheduleService;

@Controller
public class EmailScheduleController {

	@Autowired
	private EmailScheduleService emailScheduleService;
	
	@Autowired
    private ConstraintSingleService constraintSingleService;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/email/schedule")
	@PreAuthorize("hasAuthority('email_schedule_view')")
	public String listEmailSchedule() {
		return "emailschedule/emailschedule_list";
	}

	@GetMapping("/email/schedule/new")
	@PreAuthorize("hasAuthority('email_schedule_create')")
	public String newEmailSchedule(Model model, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		List<Organization> organizationList = null;
		if(null == user.getOrganization()) {
			organizationList = (List<Organization>) organizationRepository.findAll();
		}else {
			organizationList = organizationRepository.findByIdAndIsDeletedFalse(user.getOrganization().getId());
		}
		
		model.addAttribute("listOrganization", organizationList);
		model.addAttribute("listConstraint", constraintSingleService.findAll());
		EmailSchedule emailSchedule = new EmailSchedule();
		model.addAttribute("emailSchedule", emailSchedule);
		model.addAttribute("title", "New Email Schedule");
		model.addAttribute("button", "Save");
		return "emailschedule/emailschedule_new";
	}

	@PostMapping("/email/schedule/add")
	@PreAuthorize("hasAnyAuthority('email_schedule_create','email_schedule_update')")
	public String saveEmailSchedule(@ModelAttribute("emailSchedule") EmailSchedule emailSchedule, @Valid EmailSchedule entity,
			Errors errors, Model model, HttpServletResponse response,String title, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		
		if (errors.hasErrors()) {
			List<Organization> organizationList = null;
			if(null == user.getOrganization()) {
				organizationList = (List<Organization>) organizationRepository.findAll();
			}else {
				organizationList = organizationRepository.findByIdAndIsDeletedFalse(user.getOrganization().getId());
			}
			
			model.addAttribute("listOrganization", organizationList);
			model.addAttribute("listConstraint", constraintSingleService.findAll());
			model.addAttribute("title", title);
			return "emailschedule/emailschedule_new";
		} else {
			//emailScheduleService.sendFileFromMail(emailSchedule, response);
			if(null==emailSchedule.getId()) {
				emailScheduleService.save(emailSchedule);
			}
			else {
				EmailSchedule  emailScheduleObj = emailScheduleService.getById(emailSchedule.getId());
				emailSchedule.setCreatedBy(emailScheduleObj.getCreatedBy());
				emailSchedule.setCreatedDate(emailScheduleObj.getCreatedDate());
				emailScheduleService.save(emailSchedule);
			}
			
			return "redirect:/email/schedule";
		}
	}

	@GetMapping("/email/schedule/edit/{id}")
	@PreAuthorize("hasAuthority('email_schedule_update')")
	public String editEmailSchedule(@PathVariable(value = "id") long id, Model model, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		List<Organization> organizationList = null;
		if(null == user.getOrganization()) {
			organizationList = (List<Organization>) organizationRepository.findAll();
		}else {
			organizationList = organizationRepository.findByIdAndIsDeletedFalse(user.getOrganization().getId());
		}
		
		model.addAttribute("listOrganization", organizationList);
		model.addAttribute("listConstraint", constraintSingleService.findAll());
		EmailSchedule emailSchedule = emailScheduleService.getById(id);
		model.addAttribute("emailSchedule", emailSchedule);
		model.addAttribute("title", "Update Email Schedule");
		return "emailschedule/emailschedule_new";
	}

	@GetMapping("/email/schedule/delete/{id}")
	@PreAuthorize("hasAuthority('email_schedule_delete')")
	public String deleteEmailSchedule(@PathVariable(value = "id") long id) {
		emailScheduleService.removeTaskFromScheduler();
		this.emailScheduleService.deletedById(id);
		return "redirect:/email/schedule";
	}
	

	@RequestMapping(value = "/api/search/email-schedule", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('email_schedule_view')")
	public @ResponseBody PaginationDto<EmailSchedule> searchEmailSetup(Long id, String reportType,String fileType,String toMail,String subject, int pageno, String sortField, String sortDir, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String orgName = (null == user.getOrganization()? null: user.getOrganization().getName());
		
		PaginationDto<EmailSchedule> dtoList = emailScheduleService.searchByField(id, reportType,fileType,toMail,subject,pageno, sortField, sortDir,orgName);
		return dtoList;
	}
}