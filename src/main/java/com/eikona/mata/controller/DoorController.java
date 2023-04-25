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
import com.eikona.mata.entity.Door;
import com.eikona.mata.service.DeviceService;
import com.eikona.mata.service.DoorService;
import com.eikona.mata.service.OrganizationService;

@Controller
public class DoorController {

	@Autowired
	private DoorService doorService;

	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private DeviceService deviceService;

	@GetMapping("/door")
	@PreAuthorize("hasAuthority('door_view')")
	public String doorList() {
		return "door/door_list";
	}

	@GetMapping("/door/new")
	@PreAuthorize("hasAuthority('door_create')")
	public String newDoor(Model model) {
		model.addAttribute("listOrganization", organizationService.getAll());
		model.addAttribute("deviceList", deviceService.getAll());
		Door door = new Door();
		model.addAttribute("door", door);
		model.addAttribute("title", "New Door");
		return "door/door_new";
	}

	@PostMapping("/door/add")
	@PreAuthorize("hasAnyAuthority('door_create','door_update')")
	public String saveDoor(@ModelAttribute("door") Door door, @Valid Door desig,
			Errors errors,String title, Model model) {
		if (errors.hasErrors()) {
			model.addAttribute("listOrganization", organizationService.getAll());
			model.addAttribute("deviceList", deviceService.getAll());
			model.addAttribute("title", title);
			return "door/door_new";
		} else {
			if(null==door.getId())
				doorService.save(door);
 			else {
 				Door doorObj = doorService.getById(door.getId());
 				door.setCreatedBy(doorObj.getCreatedBy());
 				door.setCreatedDate(doorObj.getCreatedDate());
 				doorService.save(door);
 			}
			return "redirect:/door";

		}

	}

	@GetMapping("/door/edit/{id}")
	@PreAuthorize("hasAuthority('door_update')")
	public String updateDoor(@PathVariable(value = "id") long id, Model model) {
		model.addAttribute("listOrganization", organizationService.getAll());
		model.addAttribute("deviceList", deviceService.getAll());
		Door door = doorService.getById(id);
		model.addAttribute("door", door);
		model.addAttribute("title", "Update Door");
		return "door/door_new";
	}

	@GetMapping("/door/delete/{id}")
	@PreAuthorize("hasAuthority('door_delete')")
	public String deleteDoor(@PathVariable(value = "id") long id) {

		this.doorService.deleteById(id);
		return "redirect:/door";
	}
	
	@RequestMapping(value = "/api/search/door", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('door_view')")
	public @ResponseBody PaginationDto<Door> searchDoor(Long id, String name,String ipAddress, int pageno, String sortField, String sortDir) {
		
		PaginationDto<Door> dtoList = doorService.searchByField(id, name,ipAddress,  pageno, sortField, sortDir);
		return dtoList;
	}
}
