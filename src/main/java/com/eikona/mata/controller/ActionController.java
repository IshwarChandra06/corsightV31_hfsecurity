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
import com.eikona.mata.entity.Action;
import com.eikona.mata.service.ActionService;
import com.eikona.mata.service.EmployeeService;

@Controller
public class ActionController {

	@Autowired
	private ActionService actionService;
	
	@Autowired
	private EmployeeService employeeService;
	

	@GetMapping("/action")
	public String actionList(Model model) {
		model.addAttribute("listAction", actionService.getAll());
		return "action/action_list";
	}
	
	

	@GetMapping("/action/new")
	public String newAction(Model model) {
		model.addAttribute("listEmployee", employeeService.getAll());
		model.addAttribute("listAction", actionService.getAll());
		Action action = new Action();
		model.addAttribute("actionObj", action);
		model.addAttribute("title", "New Audit");
		return "action/action_new";
	}

	@PostMapping("/action/add")
	public String saveAction(@ModelAttribute("actionObj") Action actionObj, @Valid Action enitiy, Errors errors, String title,
			Model model) {

		if (errors.hasErrors()) {
			model.addAttribute("title", title);
			return "action/action_new";
		} else {
			model.addAttribute("message", "Add Successfully");
			actionService.save(actionObj);
			return "redirect:/action";
		}
	}

	@GetMapping("/action/edit/{id}")
	public String editAction(@PathVariable(value = "id") long id, Model model) {
		model.addAttribute("listEmployee", employeeService.getAll());
		Action action = actionService.getById(id);
		model.addAttribute("actionObj", action);
		model.addAttribute("title", "Update Audit");
		return "action/action_new";
	}

	@GetMapping("/action/delete/{id}")
	public String deleteAction(@PathVariable(value = "id") long id) {
		this.actionService.deleteById(id);
		return "redirect:/action";
	}

	@RequestMapping(value = "/api/search/action", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('action_view')")
	public @ResponseBody PaginationDto<Action> searchEmployee(String sDate,String eDate, String employeeId, String name,String status, int pageno, String sortField, String sortDir) {
		
		PaginationDto<Action> dtoList = actionService.searchByField(sDate,eDate,employeeId, name, status,pageno, sortField, sortDir);
		return dtoList;
	}	
}
