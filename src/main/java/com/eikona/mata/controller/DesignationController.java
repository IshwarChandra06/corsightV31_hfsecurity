package com.eikona.mata.controller;

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
import com.eikona.mata.entity.Designation;
import com.eikona.mata.service.DesignationService;
import com.eikona.mata.service.OrganizationService;

@Controller
public class DesignationController {
	@Autowired
	private DesignationService designationService;

	@Autowired
	private OrganizationService organizationService;

	@GetMapping("/designation")
	@PreAuthorize("hasAuthority('designation_view')")
	public String designationList(Model model) {
		model.addAttribute("Designation", designationService.getAll());
		return "designation/designation_list";
	}

	@GetMapping("/designation/new")
	@PreAuthorize("hasAuthority('designation_create')")
	public String newDesignation(Model model) {
		model.addAttribute("listOrganization", organizationService.getAll());
		Designation designation = new Designation();
		model.addAttribute("designation", designation);
		model.addAttribute("title", "New Designation");
		return "designation/designation_new";
	}

	@PostMapping("/designation/add")
	@PreAuthorize("hasAnyAuthority('designation_create','designation_update')")
	public String saveDesignation(@ModelAttribute("designation") Designation designation, @Valid Designation desig,
			Errors errors,String title, Model model) {
		if (errors.hasErrors()) {
			model.addAttribute("title", title);
			return "designation/designation_new";
		} else {
			if(null==designation.getId())
				designationService.save(designation);
 			else {
 				Designation designationObj = designationService.getById(designation.getId());
 				designation.setCreatedBy(designationObj.getCreatedBy());
 				designation.setCreatedDate(designationObj.getCreatedDate());
 				designationService.save(designation);
 			}
			return "redirect:/designation";

		}

	}

	@GetMapping("/designation/edit/{id}")
	@PreAuthorize("hasAuthority('designation_update')")
	public String updateDesignation(@PathVariable(value = "id") long id, Model model) {
		model.addAttribute("listOrganization", organizationService.getAll());
		Designation designation = designationService.getById(id);
		model.addAttribute("designation", designation);
		model.addAttribute("title", "Update Designation");
		return "designation/designation_new";
	}

	@GetMapping("/designation/delete/{id}")
	@PreAuthorize("hasAuthority('designation_delete')")
	public String deleteDesignation(@PathVariable(value = "id") long id) {

		this.designationService.deleteById(id);
		return "redirect:/designation";
	}

	@RequestMapping(value = "/api/search/designation", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('designation_view')")
	public @ResponseBody PaginationDto<Designation> searchAccessLevel(Long id, String name, int pageno, String sortField, String sortDir) {
		
		PaginationDto<Designation> dtoList = designationService.searchByField(id, name,  pageno, sortField, sortDir);
		return dtoList;
	}

}
