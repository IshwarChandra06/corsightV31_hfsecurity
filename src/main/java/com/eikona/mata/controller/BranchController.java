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
import com.eikona.mata.entity.Branch;
import com.eikona.mata.service.BranchService;
import com.eikona.mata.service.OrganizationService;


@Controller
public class BranchController {
	
	 @Autowired 
	 private BranchService branchService;
	 
	 @Autowired
	private OrganizationService organizationService;
	 
	 @GetMapping("/branch") 
	 @PreAuthorize("hasAuthority('branch_view')")
	 public String branchlist(Model model) {
		 model.addAttribute("Branch", branchService.getAll()); 
	 return "branch/branch_list"; 
	 }
		 
	@GetMapping("/branch/new")
	@PreAuthorize("hasAuthority('branch_create')")
	public String newBranch(Model model) {
		
		model.addAttribute("listOrganization", organizationService.getAll());
		Branch branch = new Branch();
		model.addAttribute("branch", branch);
		model.addAttribute("title","New Office");
		return "branch/branch_new";
	}

	@PostMapping("/branch/add")
	@PreAuthorize("hasAnyAuthority('branch_create','branch_update')")
	public String saveBranch(@ModelAttribute("branch") Branch branch, @Valid Branch br, Errors errors,String title,
			Model model) {
		if (errors.hasErrors()) {
			model.addAttribute("title",title);
			return "branch/branch_new";
		} else {
			if(null==branch.getId())
			   branchService.save(branch);
			else {
				Branch branchObj = branchService.getById(branch.getId());
				branch.setCreatedBy(branchObj.getCreatedBy());
				branch.setCreatedDate(branchObj.getCreatedDate());
				branchService.save(branch);
			}
				
			return "redirect:/branch";

		}

	}

	@GetMapping("/branch/edit/{id}")
	@PreAuthorize("hasAuthority('branch_update')")
	public String updateBranch(@PathVariable(value = "id") long id, Model model) {
		model.addAttribute("listOrganization", organizationService.getAll());
		Branch branch = branchService.getById(id);
		model.addAttribute("branch", branch);
		model.addAttribute("title","Update Office");
		return "branch/branch_new";
	}

	@GetMapping("/branch/delete/{id}")
	@PreAuthorize("hasAuthority('branch_delete')")
	public String deleteBranch(@PathVariable(value = "id") long id) {

		this.branchService.deletedById(id);
		return "redirect:/branch";
	}

	@RequestMapping(value = "/api/search/branch", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('branch_view')")
	public @ResponseBody PaginationDto<Branch> searchBranch(Long id, String name,String address,String city,String state,String pin, int pageno, String sortField, String sortDir) {
		
		PaginationDto<Branch> dtoList = branchService.searchByField(id, name,address,city,state,pin,pageno, sortField, sortDir);
		return dtoList;
	}
}

