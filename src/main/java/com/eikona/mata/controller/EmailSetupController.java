package com.eikona.mata.controller;

import java.security.Principal;
import java.util.List;

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
import com.eikona.mata.entity.EmailSetup;
import com.eikona.mata.entity.Organization;
import com.eikona.mata.entity.User;
import com.eikona.mata.repository.OrganizationRepository;
import com.eikona.mata.repository.UserRepository;
import com.eikona.mata.service.EmailSetUpService;

@Controller
public class EmailSetupController {
	@Autowired
	private EmailSetUpService emailSetUpService;

	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/email/setup")
	@PreAuthorize("hasAuthority('email_setup_view')")
	public String listEmailSetup() {
		return "emailsetup/emailsetup_list";
	}

	@GetMapping("/email/setup/new")
	@PreAuthorize("hasAuthority('email_setup_create')")
	public String newEmailSetup(Model model, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		List<Organization> organizationList = null;
		if(null == user.getOrganization()) {
			organizationList = (List<Organization>) organizationRepository.findAll();
		}else {
			organizationList = organizationRepository.findByIdAndIsDeletedFalse(user.getOrganization().getId());
		}
		
		model.addAttribute("listOrganization", organizationList);
		EmailSetup emailSetup = new EmailSetup();
		model.addAttribute("emailSetup", emailSetup);
		model.addAttribute("title", "New Email Setup");
		return "emailsetup/emailsetup_new";
	}

	@PostMapping("/email/setup/add")
	@PreAuthorize("hasAnyAuthority('email_setup_create','email_setup_update')")
	public String saveEmailSetup(@ModelAttribute("emailSetup") EmailSetup emailSetup, @Valid EmailSetup entity, Errors errors,
			Model model, String title, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		
		if (errors.hasErrors()) {
			List<Organization> organizationList = null;
			if(null == user.getOrganization()) {
				organizationList = (List<Organization>) organizationRepository.findAll();
			}else {
				organizationList = organizationRepository.findByIdAndIsDeletedFalse(user.getOrganization().getId());
			}
			
			model.addAttribute("listOrganization", organizationList);
			model.addAttribute("title", title);
			return "emailsetup/emailsetup_new";
		} else {
			if(null==emailSetup.getId()) {
				emailSetUpService.save(emailSetup);
			}
			else {
				EmailSetup  emailSetupObj = emailSetUpService.getById(emailSetup.getId());
				emailSetup.setCreatedBy(emailSetupObj.getCreatedBy());
	            emailSetup.setCreatedDate(emailSetupObj.getCreatedDate());
				emailSetUpService.save(emailSetup);
			}
			return "redirect:/email/setup";

		}

	}

	@GetMapping("/email/setup/edit/{id}")
	@PreAuthorize("hasAuthority('email_setup_update')")
	public String editEmailSetup(@PathVariable(value = "id") long id, Model model, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		List<Organization> organizationList = null;
		if(null == user.getOrganization()) {
			organizationList = (List<Organization>) organizationRepository.findAll();
		}else {
			organizationList = organizationRepository.findByIdAndIsDeletedFalse(user.getOrganization().getId());
		}
		
		model.addAttribute("listOrganization", organizationList);
		EmailSetup emailSetup = emailSetUpService.getById(id);
		model.addAttribute("emailSetup", emailSetup);
		model.addAttribute("title", "Update Email Setup");
		return "emailsetup/emailsetup_new";
	}

	@GetMapping("/email/setup/delete/{id}")
	@PreAuthorize("hasAuthority('email_setup_delete')")
	public String deleteEmailSetup(@PathVariable(value = "id") long id) {

		this.emailSetUpService.deletedById(id);
		return "redirect:/email/setup";
	}

	@RequestMapping(value = "/api/search/email-setup", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('email_setup_view')")
	public @ResponseBody PaginationDto<EmailSetup> searchEmailSetup(Long id, String smppServer,String port,String userName,String senderName, int pageno, String sortField, String sortDir, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String orgName = (null == user.getOrganization()? null: user.getOrganization().getName());
		
		PaginationDto<EmailSetup> dtoList = emailSetUpService.searchByField(id, smppServer,port,userName,senderName,  pageno, sortField, sortDir,orgName);
		return dtoList;
	}

}
