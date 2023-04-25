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
import com.eikona.mata.dto.ShiftSettingDto;
import com.eikona.mata.entity.Shift;
import com.eikona.mata.service.OrganizationService;
import com.eikona.mata.service.ShiftService;



@Controller
public class ShiftController {

	@Autowired
	private ShiftService shiftService;
	@Autowired
	private OrganizationService organizationService;
	
	@GetMapping("/shift")
	@PreAuthorize("hasAuthority('shift_view')")
	public String shiftListPage() {
		return "shift/shift_list";
	}

	@GetMapping("/shift/new")
	@PreAuthorize("hasAuthority('shift_create')")
	public String newShift(Model model) {
		model.addAttribute("listOrganization", organizationService.getAll());
		Shift shift = new Shift();
		model.addAttribute("shift", shift);
		model.addAttribute("title", "New Shift");
		return "shift/shift_new";
	}

	@PostMapping("/shift/add")
	@PreAuthorize("hasAnyAuthority('shift_create','shift_update')")
	 public String saveShift(@ModelAttribute("shift") Shift shift, @Valid Shift shf, Errors errors,
				Model model,String title) {
	    	if (errors.hasErrors()) 
	    	{   model.addAttribute("title", title);
	    		return "shift/shift_new";
	    	}
	    	 else {
	 			if(null==shift.getId())
					shiftService.save(shift);
	 			else {
	 				Shift shiftObj = shiftService.getById(shift.getId());
	 				shift.setCreatedBy(shiftObj.getCreatedBy());
	 				shift.setCreatedDate(shiftObj.getCreatedDate());
	 				shiftService.save(shift);
	 			}
				return "redirect:/shift";

			}	
	        
	    }

	@GetMapping("/shift/edit/{id}")
	@PreAuthorize("hasAuthority('shift_update')")
	public String updateEntity(@PathVariable(value = "id") long id, Model model) {
		model.addAttribute("listOrganization", organizationService.getAll());
		Shift shift = shiftService.getById(id);
		model.addAttribute("shift", shift);
		model.addAttribute("title", "Edit Shift");
		return "shift/shift_new";
	}

	@GetMapping("/shift/delete/{id}")
	@PreAuthorize("hasAuthority('shift_delete')")
	public String deleteEntity(@PathVariable(value = "id") long id) {

		this.shiftService.deleteById(id);
		return "redirect:/shift";
	}
	
	@RequestMapping(value = "/api/search/shift", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('shift_view')")
	public @ResponseBody PaginationDto<Shift> search(Long id, String name, int pageno, String sortField, String sortDir) {
		
		PaginationDto<Shift> dtoList = shiftService.searchByField(id, name, pageno, sortField, sortDir);
		return dtoList;
	}
	
	@GetMapping("/shift/setting")
	@PreAuthorize("hasAuthority('shift_setting')")
	public String shiftSetting(Model model) {
		model.addAttribute("listOrganization", organizationService.getAll());
		model.addAttribute("shiftSetting", new ShiftSettingDto());
		return "shift/shift_setting";
	}
	
	
	
	@PostMapping("/shift/setting/add")
	@PreAuthorize("hasAuthority('shift_setting')")
	public String seveShiftSetting(@ModelAttribute("shiftSetting") ShiftSettingDto shiftSettingDto) {
		shiftService.saveShiftSetting(shiftSettingDto);
		return "parameter/parameter_list";
	}
}
