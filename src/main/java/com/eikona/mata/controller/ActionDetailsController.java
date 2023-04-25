package com.eikona.mata.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.ActionDetails;
import com.eikona.mata.repository.ActionDetailsRepository;
import com.eikona.mata.service.ActionDetailsService;
import com.eikona.mata.util.ExportActionDetailsData;

@Controller
public class ActionDetailsController {

	@Autowired
	private ActionDetailsService actionDetailsService;
	
	@Autowired
	private ActionDetailsRepository actionDetailsRepository;
	
	@Autowired
	private ExportActionDetailsData exportActionDetailsData;
	
	

	@GetMapping("/action-details")
	public String listActionDetails(Model model) {
		model.addAttribute("listActionDetails", actionDetailsService.getAll());
		return "actionDetails/actionDetails_list";
	}

	@GetMapping("/action-details/new")
	public String newActionDetails(Model model) {

		ActionDetails actionDetails = new ActionDetails();
		model.addAttribute("actionDatils", actionDetails);
		model.addAttribute("title", "New Exception");
		return "actionDetails/actionDetails_new";
	}

	@PostMapping("/action-details/add")
	public String saveActionDetails(@ModelAttribute("actionDetails") ActionDetails actionDetails, @Valid ActionDetails enitiy, Errors errors, String title,
			Model model) {

		if (errors.hasErrors()) {
			model.addAttribute("title", title);
			return "actionDetails/actionDetails_new";
		} else {
			model.addAttribute("message", "Add Successfully");
			actionDetailsService.save(actionDetails);
			return "redirect:/action-details";
		}
	}

	@GetMapping("/action-details/edit/{id}")
	public String editActionDetails(@PathVariable(value = "id") long id, Model model) {
		ActionDetails actionDetails = actionDetailsService.getById(id);
		model.addAttribute("actionDatils", actionDetails);
		model.addAttribute("title", "Update Exception");
		return "actionDetails/actionDetails_new";
	}

	@GetMapping("/action-details/delete/{id}")
	public String deleteActionDetails(@PathVariable(value = "id") long id) {
		this.actionDetailsService.deleteById(id);
		return "redirect:/action-details";
	}

	@RequestMapping(value = "/api/search/action-details", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('action_details_view')")
	public @ResponseBody PaginationDto<ActionDetails> searchEmployee( String employeeId, String name,String device,String sDate,String eDate, int pageno, String sortField, String sortDir) {
		
		PaginationDto<ActionDetails> dtoList = actionDetailsService.searchByField(sDate,eDate,employeeId, name, device, pageno, sortField, sortDir);
		return dtoList;
	}
	
	@GetMapping("/action-datails/per-id/{id}")
	public String actionDetailsPerEachIdViewPage(@PathVariable(value = "id") long id, Model model) {
		model.addAttribute("id", id);
		return "actionDetails/actionDetails_perid";
	}
	
	@GetMapping(value = "/api/action-details/per-id")
	public @ResponseBody  List<ActionDetails> actionDetailsDatatable(@RequestParam Long id) {
		
		List<ActionDetails> actionDetailsList = actionDetailsRepository.findByActionIdCustom(id);
		return actionDetailsList;
	}
	
	
	@RequestMapping(value="/api/action-details/export-to-file",method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('action_details_export')")
	public void exportToFile(HttpServletResponse response,String sDate,String eDate, String employeeId, String name,String device, String flag) {
		 response.setContentType("application/octet-stream");
			DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
			String currentDateTime = dateFormat.format(new Date());
			String headerKey = "Content-Disposition";
			String headerValue = "attachment; filename=Exception_report_" + currentDateTime + "."+flag;
			response.setHeader(headerKey, headerValue);
		try {
			exportActionDetailsData.fileExportBySearchValue(response,sDate, eDate, employeeId, name, device,flag );
		} catch (Exception  e) {
			e.printStackTrace();
		}
	}
	

}
