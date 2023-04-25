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

import com.eikona.mata.entity.ConstraintRange;
import com.eikona.mata.service.ConstraintRangeService;

@Controller
public class ConstraintRangeController {

	@Autowired
	private ConstraintRangeService constraintRangeService;

	@GetMapping("/constraint/range")
	@PreAuthorize("hasAuthority('constraint_range_view')")
	public String listConstraintRange(Model model) {
		model.addAttribute("ConstraintRange", constraintRangeService.findAll());
		return "constraintrange/constraintrange_list";
	}

	@GetMapping("/constraint/range/new")
	@PreAuthorize("hasAuthority('constraint_range_create')")
	public String newConstraintRange(Model model) {
		ConstraintRange constraintrange = new ConstraintRange();
		model.addAttribute("constraintrange", constraintrange);
		model.addAttribute("title", "New Constraint Range");
		return "constraintrange/constraintrange_new";
	}

	@PostMapping("/constraint/range/add")
	@PreAuthorize("hasAnyAuthority('constraint_range_create','constraint_range_update')")
	public String saveConstraintRange(@ModelAttribute("constraintrange") ConstraintRange constraintrange,
			@Valid ConstraintRange entity, Errors errors,String title, Model model) {
		if (errors.hasErrors()) {
			model.addAttribute("title", title);
			return "constraintrange/constraintrange_new";
		} else {
			model.addAttribute("message", "Add Successfully");
			constraintRangeService.save(constraintrange);
			return "redirect:/constraint/range";

		}

	}

	@GetMapping("/constraint/range/edit/{id}")
	@PreAuthorize("hasAuthority('constraint_range_update')")
	public String editConstraintRange(@PathVariable(value = "id") long id, Model model) {
		ConstraintRange constraintrange = constraintRangeService.find(id);
		model.addAttribute("constraintrange", constraintrange);
		model.addAttribute("title", "Update Constraint Range");
		return "constraintrange/constraintrange_new";
	}

	@GetMapping("/constraint/range/delete/{id}")
	@PreAuthorize("hasAuthority('constraint_range_delete')")
	public String deleteConstraintRange(@PathVariable(value = "id") long id) {

		this.constraintRangeService.delete(id);
		return "redirect:/constraint/range";
	}

	@GetMapping(value = "/api/constraint/range")
	@PreAuthorize("hasAuthority('constraint_range_view')")
	public @ResponseBody DataTablesOutput<ConstraintRange> constraintRangeDatatable(@Valid DataTablesInput input) {
		return (DataTablesOutput<ConstraintRange>) constraintRangeService.getAll(input);
	}

}
