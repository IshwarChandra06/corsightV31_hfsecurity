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
import com.eikona.mata.entity.Department;
import com.eikona.mata.service.DepartmentService;
import com.eikona.mata.service.OrganizationService;

@Controller
public class DepartmentController {

	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private OrganizationService organizationService;

	@GetMapping("/department")
	@PreAuthorize("hasAuthority('department_view')")
	public String listDepartment(Model model) {
		model.addAttribute("listDepartment", departmentService.getAll());
		return "department/department_list";
	}

	@GetMapping("/department/new")
	@PreAuthorize("hasAuthority('department_create')")
	public String newDepartment(Model model) {

		model.addAttribute("listOrganization", organizationService.getAll());
		Department department = new Department();
		model.addAttribute("department", department);
		model.addAttribute("title", "New Department");
		return "department/department_new";
	}

	@PostMapping("/department/add")
	@PreAuthorize("hasAnyAuthority('department_create','department_update')")
	public String saveDepartment(@ModelAttribute("department") Department department, @Valid Department dept,
			Errors errors, String title, Model model) {

		if (errors.hasErrors()) {
			model.addAttribute("title", title);
			return "department/department_new";
		} else {

			if (null == department.getId())
				departmentService.save(department);
			else {
				Department departmentObj = departmentService.getById(department.getId());
				department.setCreatedBy(departmentObj.getCreatedBy());
				department.setCreatedDate(departmentObj.getCreatedDate());
				departmentService.save(department);
			}
			return "redirect:/department";
		}
	}

	@GetMapping("/department/edit/{id}")
	@PreAuthorize("hasAuthority('department_update')")
	public String editDepartment(@PathVariable(value = "id") long id, Model model) {
		model.addAttribute("listOrganization", organizationService.getAll());
		Department department = departmentService.getById(id);
		model.addAttribute("department", department);
		model.addAttribute("title", "Update Department");
		return "department/department_new";
	}

	@GetMapping("/department/delete/{id}")
	@PreAuthorize("hasAuthority('department_delete')")
	public String deleteDepartment(@PathVariable(value = "id") long id) {
		this.departmentService.deleteById(id);
		return "redirect:/department";
	}

	@RequestMapping(value = "/api/search/department", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('department_view')")
	public @ResponseBody PaginationDto<Department> searchDepartment(Long id, String name, int pageno, String sortField,
			String sortDir) {

		PaginationDto<Department> dtoList = departmentService.searchByField(id, name, pageno, sortField, sortDir);
		return dtoList;
	}
}
