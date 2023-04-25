package com.eikona.mata.controller;


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
import com.eikona.mata.entity.Organization;
import com.eikona.mata.service.OrganizationService;


@Controller
public class OrganizationController {
	
	@Autowired
	private OrganizationService organizationService;

	@GetMapping("/organization")
	@PreAuthorize("hasAuthority('organization_view')")
	public String list() {
		
		return "organization/organization_list";
	}

	@GetMapping("/organization/new") 
	@PreAuthorize("hasAuthority('organization_create')")
	public String newOrganization(Model model) {
		Organization organization = new Organization(); 
		model.addAttribute("organization", organization); 
		model.addAttribute("title","New Organization");
		return "organization/organization_new"; 
	}

	@PostMapping("/organization/add")
	@PreAuthorize("hasAnyAuthority('organization_create','organization_update')")
	public String saveOrganization(@ModelAttribute("organization") Organization organization,@Valid Organization orgz, Errors errors,String title,
			Model model) {

		if (errors.hasErrors()) {
			model.addAttribute("title",title);
    		return "/organization/organization_new";
    	}else {
 			if(null==organization.getId())
 			  organizationService.save(organization);
 			else {
 				Organization organizationObj = organizationService.getById(organization.getId());
 				organization.setCreatedBy(organizationObj.getCreatedBy());
 				organization.setCreatedDate(organizationObj.getCreatedDate());
 	 			organizationService.save(organization);
 			}
 		 	return "redirect:/organization";
    	 }	
    }

	@GetMapping("/organization/edit/{id}")
	@PreAuthorize("hasAuthority('organization_update')")
	public String editOrganization(@PathVariable(value = "id") long id, Model model) {
		Organization organization = organizationService.getById(id);
		model.addAttribute("organization", organization);
		model.addAttribute("title","Update Organization");
		return "organization/organization_new";
	}

	@GetMapping("/organization/delete/{id}")
	@PreAuthorize("hasAuthority('organization_delete')")
	public String deleteOrganization(@PathVariable(value = "id") long id) {
		this.organizationService.deleteById(id);
		return "redirect:/organization";
	}
	
	@RequestMapping(value = "/api/search/organization", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('organization_view')")
	public @ResponseBody PaginationDto<Organization> searchOrganization(Long id, String name,String address,String city,String pin, int pageno, String sortField, String sortDir) {
		
		PaginationDto<Organization> dtoList = organizationService.searchByField(id, name,address,city,pin,pageno, sortField, sortDir);
		return dtoList;
	}
}
