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
import com.eikona.mata.entity.Privilege;
import com.eikona.mata.repository.UserRepository;
import com.eikona.mata.service.PrivilegeService;

@Controller
public class PrivilegeController {
	
	@Autowired
	private PrivilegeService privilegeService;
	
	
	@GetMapping("/privilege")
	@PreAuthorize("hasAuthority('privilege_view')")
	public String list() {
		
		return "privilege/privilege_list";
	}
	
	@RequestMapping(value = "/api/search/privilege", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('role_view')")
	public @ResponseBody PaginationDto<Privilege> searchRole(Long id, String name, int pageno, String sortField, String sortDir) {
		
		PaginationDto<Privilege> dtoList = privilegeService.searchByField(id, name,  pageno, sortField, sortDir);
		return dtoList;
	}

	@GetMapping("/privilege/new") 
	@PreAuthorize("hasAuthority('privilege_create')")
	public String newPrivilege(Model model) {
		Privilege privilege = new Privilege(); 
		model.addAttribute("privilege", privilege); 
		model.addAttribute("title","New Privilege");
		return "privilege/privilege_new"; 
	}

	@PostMapping("/privilege/add")
	@PreAuthorize("hasAnyAuthority('privilege_create','privilege_update')")
	public String savePrivilege(@ModelAttribute("privilege") Privilege privilege,@Valid Privilege orgz, Errors errors,String title,
			Model model) {

		if (errors.hasErrors()) {
			model.addAttribute("title",title);
    		return "/privilege/privilege_new";
    	}else {
 			if(null==privilege.getId())
 			  privilegeService.save(privilege);
 			else {
 				Privilege privilegeObj = privilegeService.getById(privilege.getId());
 				privilege.setCreatedBy(privilegeObj.getCreatedBy());
 				privilege.setCreatedDate(privilegeObj.getCreatedDate());
 	 			privilegeService.save(privilege);
 			}
 		 	return "redirect:/privilege";
    	 }	
    }

	@GetMapping("/privilege/edit/{id}")
	@PreAuthorize("hasAuthority('privilege_update')")
	public String editPrivilege(@PathVariable(value = "id") long id, Model model) {
		Privilege privilege = privilegeService.getById(id);
		model.addAttribute("privilege", privilege);
		model.addAttribute("title","Update Privilege");
		return "privilege/privilege_new";
	}

	@GetMapping("/privilege/delete/{id}")
	@PreAuthorize("hasAuthority('privilege_delete')")
	public String deletePrivilege(@PathVariable(value = "id") long id) {
		this.privilegeService.deleteById(id);
		return "redirect:/privilege";
	}
}
