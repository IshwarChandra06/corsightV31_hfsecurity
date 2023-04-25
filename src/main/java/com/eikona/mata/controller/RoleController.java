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
import com.eikona.mata.entity.Role;
import com.eikona.mata.service.PrivilegeService;
import com.eikona.mata.service.RoleService;

@Controller
public class RoleController {
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private PrivilegeService privilegeService;
	
	
	
	@GetMapping("/role")
	@PreAuthorize("hasAuthority('role_view')")
	public String list() {
		return "role/role_list";
	}
	
	@RequestMapping(value = "/api/search/role", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('role_view')")
	public @ResponseBody PaginationDto<Role> searchRole(Long id, String name, int pageno, String sortField, String sortDir) {
		
		PaginationDto<Role> dtoList = roleService.searchByField(id, name,  pageno, sortField, sortDir);
		return dtoList;
	}

	@GetMapping("/role/new") 
	@PreAuthorize("hasAuthority('role_create')")
	public String newRole(Model model) {
		Role role = new Role(); 
		model.addAttribute("listPrivilege", privilegeService.getAll());
		model.addAttribute("role", role); 
		model.addAttribute("title","New Role");
		return "role/role_new"; 
	}

	@PostMapping("/role/add")
	@PreAuthorize("hasAnyAuthority('role_create','role_update')")
	public String saveRole(@ModelAttribute("role") Role role,@Valid Role orgz, Errors errors,String title,
			Model model) {

		if (errors.hasErrors()) {
			model.addAttribute("title",title);
			model.addAttribute("listPrivilege", privilegeService.getAll());
    		return "/role/role_new";
    	}else {
 			if(null==role.getId())
 			  roleService.save(role);
 			else {
 				Role roleObj = roleService.getById(role.getId());
 				role.setCreatedBy(roleObj.getCreatedBy());
 				role.setCreatedDate(roleObj.getCreatedDate());
 	 			roleService.save(role);
 			}
 		 	return "redirect:/role";
    	 }	
    }

	@GetMapping("/role/edit/{id}")
	@PreAuthorize("hasAuthority('role_update')")
	public String editRole(@PathVariable(value = "id") long id, Model model) {
		Role role = roleService.getById(id);
		model.addAttribute("listPrivilege", privilegeService.getAll());
		model.addAttribute("role", role);
		model.addAttribute("title","Update Role");
		return "role/role_new";
	}

	@GetMapping("/role/delete/{id}")
	@PreAuthorize("hasAuthority('role_delete')")
	public String deleteRole(@PathVariable(value = "id") long id) {
		this.roleService.deleteById(id);
		return "redirect:/role";
	}
}
