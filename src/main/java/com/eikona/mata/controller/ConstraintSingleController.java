package com.eikona.mata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eikona.mata.entity.ConstraintSingle;
import com.eikona.mata.service.ConstraintSingleService;

@Controller
public class ConstraintSingleController {

	@Autowired
	private ConstraintSingleService constraintSingleService;

	@GetMapping("/constraint/single")
	@PreAuthorize("hasAuthority('constraint_single_view')")
	public String list(Model model) {
		model.addAttribute("ConstraintSingle", constraintSingleService.findAll());
		return "constraintsingle/constraintsingle_list";
	}

	@GetMapping("/constraint/single/new")
	@PreAuthorize("hasAuthority('constraint_single_create')")
	public String newConstraintSingle(Model model) {
		ConstraintSingle constraintsingle = new ConstraintSingle();
		model.addAttribute("constraintsingle", constraintsingle);
		model.addAttribute("title", "New Constraint Single");
		model.addAttribute("button", "Save");
		return "constraintsingle/constraintsingle_new";
	}

	@PostMapping("/constraint/single/add")
	@PreAuthorize("hasAnyAuthority('constraint_single_create','constraint_single_update')")
	public String save(@ModelAttribute("constraintsingle") ConstraintSingle constraintsingle,
			@Valid ConstraintSingle entity, Errors errors,String title, Model model) {
		if (errors.hasErrors()) {
			model.addAttribute("title", title);
			model.addAttribute("button", "Save");
			return "constraintsingle/constraintsingle_new";
		} else {
			model.addAttribute("message", "Add Successfully");
			constraintSingleService.save(constraintsingle);
			return "redirect:/constraint/single";

		}

	}

	@GetMapping("/constraint/single/edit/{id}")
	@PreAuthorize("hasAuthority('constraint_single_update')")
	public String edit(@PathVariable(value = "id") long id, Model model) {
		ConstraintSingle constraintsingle = constraintSingleService.find(id);
		model.addAttribute("constraintsingle", constraintsingle);
		model.addAttribute("title", "Update Constraint Single");
		return "constraintsingle/constraintsingle_new";
	}

	@GetMapping("/constraint/single/delete/{id}")
	@PreAuthorize("hasAuthority('constraint_single_delete')")
	public String delete(@PathVariable(value = "id") long id) {
		this.constraintSingleService.delete(id);
		return "redirect:/constraint/single";
	}

	@GetMapping(value = "/api/constraint/single")
	@PreAuthorize("hasAuthority('constraint_single_view')")
	public @ResponseBody DataTablesOutput<ConstraintSingle> ConstraintRange(@Valid DataTablesInput input) {
		return (DataTablesOutput<ConstraintSingle>) constraintSingleService.getAll(input);
	}

}
