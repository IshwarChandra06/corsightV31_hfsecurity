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

import com.eikona.mata.entity.Parameter;
import com.eikona.mata.service.ParameterService;

@Controller
public class ParameterController {

	@Autowired
	private ParameterService parameterService;

	@GetMapping("/parameter")
	@PreAuthorize("hasAuthority('parameter_view')")
	public String list(Model model) {
		model.addAttribute("parameter", parameterService.getAll());
		return "parameter/parameter_list";
	}

	@GetMapping("/parameter/new")
	@PreAuthorize("hasAuthority('parameter_create')")
	public String newparameter(Model model) {
		Parameter parameter = new Parameter();
		model.addAttribute("parameter", parameter);
		model.addAttribute("title", "New Parameter");
		model.addAttribute("button", "Save");
		return "parameter/parameter_new";
	}

	@PostMapping("/parameter/add")
	@PreAuthorize("hasAnyAuthority('parameter_create','parameter_update')")
	public String save(@ModelAttribute("parameter") Parameter parameter, @Valid Parameter entity, Errors errors,
			Model model) {
		if (errors.hasErrors()) {
			model.addAttribute("title", "New Parameter");
			model.addAttribute("button", "Save");
			return "parameter/parameter_new";
		} else {
			model.addAttribute("message", "Add Successfully");
			parameterService.save(parameter);
			return "redirect:/parameter";

		}

	}

	@GetMapping("/parameter/edit/{id}")
	@PreAuthorize("hasAuthority('parameter_update')")
	public String edit(@PathVariable(value = "id") long id, Model model) {
		Parameter parameter = parameterService.getById(id);
		model.addAttribute("parameter", parameter);
		model.addAttribute("title", "Update Parameter");
		model.addAttribute("button", "Update");
		return "parameter/parameter_new";
	}

	@GetMapping("/parameter/delete/{id}")
	@PreAuthorize("hasAuthority('parameter_delete')")
	public String delete(@PathVariable(value = "id") long id) {

		this.parameterService.deletedById(id);
		return "redirect:/parameter";
	}

	@GetMapping(value = "/api/parameter")
	@PreAuthorize("hasAuthority('parameter_view')")
	public @ResponseBody DataTablesOutput<Parameter> ConstraintRange(@Valid DataTablesInput input) {
		// return contractorRepository.findAll(input);

		return (DataTablesOutput<Parameter>) parameterService.getAllFrom(input);
	}

}
