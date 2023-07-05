package com.eikona.mata.controller;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
import com.eikona.mata.entity.Role;
import com.eikona.mata.entity.User;
import com.eikona.mata.repository.OrganizationRepository;
import com.eikona.mata.repository.PrivilegeRepository;
import com.eikona.mata.repository.RoleRepository;
import com.eikona.mata.repository.UserRepository;
import com.eikona.mata.service.UserService;


@Controller
public class UserController {
	@Autowired
	private UserService userService;

	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PrivilegeRepository privilegeRepository;

	@GetMapping("/user")
	@PreAuthorize("hasAuthority('user_view')")
	public String list() {
		return "user/user_list";
	}

	@GetMapping("/user/new")
	@PreAuthorize("hasAuthority('user_create')")
	public String newUser(Model model, Principal principal) {
		User userObj = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		List<Organization> organizationList = null;
		if(null == userObj.getOrganization()) {
			organizationList = (List<Organization>) organizationRepository.findAll();
		}else {
			organizationList = organizationRepository.findByIdAndIsDeletedFalse(userObj.getOrganization().getId());
		}
		model.addAttribute("listRole", roleRepository.findByOrganizationAndIsDeletedFalse(userObj.getOrganization()));
		model.addAttribute("listOrganization", organizationList);
		User user = new User();
		model.addAttribute("user", user);
		model.addAttribute("title", "New User");
		return "user/user_new";
	}
	
	@GetMapping("/signup")
	public String signUp(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		return "signup";
	}
	
	@PostMapping("/signup/details")
	public String saveSignupDetails(@ModelAttribute("user") User user,String orgName,Model model) {
		Organization org= organizationRepository.findByNameAndIsDeletedFalse(orgName);
		
		if(null==org) {
			org=new Organization();
			org.setName(orgName);
			org=organizationRepository.save(org);
		}
		Role role=roleRepository.findByNameAndOrganizationAndIsDeletedFalse("Admin",org);
		if(role==null) {
			role=new Role();
			role.setName("Admin");
			role.setOrganization(org);
			role.setPrivileges(privilegeRepository.findAllByIsDeletedFalse());
			role=roleRepository.save(role);
		}
		user.setRole(role);
		user.setOrganization(org);
		
		userService.save(user);
		return "redirect:/login";

	}

	@PostMapping("/user/add")
	@PreAuthorize("hasAnyAuthority('user_create','user_update')")
	public String saveUser(@ModelAttribute("user") User user, @Valid User users, Errors errors, BindingResult bindingResult,
			Model model, String title, Principal principal) {
		User userObj = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		if (errors.hasErrors())

		{
			List<Organization> organizationList = null;
			if(null == userObj.getOrganization()) {
				organizationList = (List<Organization>) organizationRepository.findAll();
			}else {
				organizationList = organizationRepository.findByIdAndIsDeletedFalse(userObj.getOrganization().getId());
			}
			model.addAttribute("listRole", roleRepository.findByOrganizationAndIsDeletedFalse(userObj.getOrganization()));
			model.addAttribute("listOrganization", organizationList);
			model.addAttribute("title", title);
			return "user/user_new";
		}
		model.addAttribute("message", "Add Successfully");
		userService.save(user);
		return "redirect:/user";

	}

	@GetMapping("/user/edit/{id}")
	@PreAuthorize("hasAuthority('user_update')")
	public String editUser(@PathVariable(value = "id") Integer id, Model model, Principal principal) {
		
		User userObj = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		List<Organization> organizationList = null;
		if(null == userObj.getOrganization()) {
			organizationList = (List<Organization>) organizationRepository.findAll();
		}else {
			organizationList = organizationRepository.findByIdAndIsDeletedFalse(userObj.getOrganization().getId());
		}
		model.addAttribute("listRole", roleRepository.findByOrganizationAndIsDeletedFalse(userObj.getOrganization()));
		model.addAttribute("listOrganization", organizationList);
		
		User user = userService.getById(id);
		model.addAttribute("user", user);
		model.addAttribute("title", "Update User");
		return "user/user_new";
	}

	@GetMapping("user/delete/{id}")
	@PreAuthorize("hasAuthority('user_delete')")
	public String deleteUser(@PathVariable(value = "id") Integer id) {

		this.userService.deleteById(id);
		return "redirect:/user";
	}

	@RequestMapping(value = "/api/search/user", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('user_view')")
	public @ResponseBody PaginationDto<User> searchDoor(Long id, String name,String phone,String role, int pageno, String sortField, String sortDir, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String orgName = (null == user.getOrganization()? null: user.getOrganization().getName());
		
		PaginationDto<User> dtoList = userService.searchByField(id, name,phone,role,  pageno, sortField, sortDir,orgName);
		return dtoList;
	}
}
