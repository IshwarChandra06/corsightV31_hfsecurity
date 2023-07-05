package com.eikona.mata.controller;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eikona.mata.constants.DefaultConstants;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Area;
import com.eikona.mata.entity.Branch;
import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.Organization;
import com.eikona.mata.entity.User;
import com.eikona.mata.repository.AreaRepository;
import com.eikona.mata.repository.BranchRepository;
import com.eikona.mata.repository.DoorRepository;
import com.eikona.mata.repository.OrganizationRepository;
import com.eikona.mata.repository.UserRepository;
import com.eikona.mata.service.AreaService;
import com.eikona.mata.sync.WatchListSync;

@Controller
public class AreaController {

	@Autowired
	private AreaService areaService;

	@Autowired
	private AreaRepository areaDatatableRepository;

	@Autowired
	private DoorRepository doorRepository;

	@Autowired
	private BranchRepository branchRepository;

	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Value("${corsight.enabled}")
	private boolean corsightEnabled;
	
	@Autowired
	private WatchListSync watchListSync;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/area")
	@PreAuthorize("hasAuthority('area_view')")
	public String areaListPage(Model model) {
		model.addAttribute("corsightEnabled", corsightEnabled);
		return "area/area_list";
	}

	@GetMapping("/areabybranch")
	public @ResponseBody List<Area> getAreaByBranch(@RequestParam String branch, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		Branch branchObj = new Branch();
		branchObj.setName(branch);
		List<Area> areaList = areaDatatableRepository.findByBranchAndIsDeletedFalseCustom(branch,user.getOrganization().getName());
		return areaList;
	}

	@GetMapping("/areabybranchid")
	@PreAuthorize("hasAuthority('device_create')")
	public @ResponseBody List<Area> getAreaByBranchId(@RequestParam String branchId, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		Branch branchObj = branchRepository.findById(Long.valueOf(branchId)).get();
		List<Area> areaList = areaDatatableRepository.findByBranchAndOrganizationAndIsDeletedFalse(branchObj,user.getOrganization());
		return areaList;
	}

	@GetMapping("/area/new")
	@PreAuthorize("hasAuthority('area_create')")
	public String newArea(Model model, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());

		List<Organization> organizationList = null;
		if(null == user.getOrganization()) {
			organizationList = (List<Organization>) organizationRepository.findAll();
		}else {
			organizationList = organizationRepository.findByIdAndIsDeletedFalse(user.getOrganization().getId());
		}
		
		model.addAttribute("listOrganization", organizationList);
		model.addAttribute("listBranch", branchRepository.findByOrganizationAndIsDeletedFalse(user.getOrganization()));
		model.addAttribute("listDoor", doorRepository.findByOrganizationAndIsDeletedFalse(user.getOrganization()));
		Area area = new Area();
		model.addAttribute("area", area);
		model.addAttribute("title", "New Area");
		return "area/area_new";
	}
	@PostMapping("/rest/area/test")
	public String watchlistjsonTest(@RequestBody String request) throws ParseException{
		JSONObject jsonResponse = new JSONObject(request);
		Area area = new Area();
		area.setName((String) jsonResponse.get("display_name"));
		Branch branch = new Branch();
		branch.setId(Long.valueOf(DefaultConstants.DEFAULT_BRANCH_ID));
		area.setBranch(branch);
		areaService.save(area);
		return "area/area_list";
	}
	@GetMapping("/watch-list-sync")
	@PreAuthorize("hasAuthority('watchlist_sync')")
	public String areaSync(Model model, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String message = null;
		try {
			message = watchListSync.syncWatchlist(user.getOrganization());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		model.addAttribute("message", message);
		model.addAttribute("corsightEnabled",corsightEnabled);
		return "area/area_list";
	}
	


	@PostMapping("/area/add")
	@PreAuthorize("hasAnyAuthority('area_create','area_update')")
	public String saveArea(@ModelAttribute("area") Area area, @Valid Area ar, Errors errors, String title,
			Model model, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		if (errors.hasErrors()) {
			List<Organization> organizationList = null;
			if(null == user.getOrganization()) {
				organizationList = (List<Organization>) organizationRepository.findAll();
			}else {
				organizationList = organizationRepository.findByIdAndIsDeletedFalse(user.getOrganization().getId());
			}
			
			model.addAttribute("listOrganization", organizationList);
			model.addAttribute("listBranch", branchRepository.findByOrganizationAndIsDeletedFalse(user.getOrganization()));
			model.addAttribute("listDoor", doorRepository.findByOrganizationAndIsDeletedFalse(user.getOrganization()));
			model.addAttribute("title", title);
			return "area/area_new";
		} else {
			if (null == area.getId())
				areaService.save(area);
			else {
				Area areaObj = areaService.getById(area.getId());
				area.setWatchlistId(areaObj.getWatchlistId());
				area.setCreatedBy(areaObj.getCreatedBy());
				area.setCreatedDate(areaObj.getCreatedDate());
				areaService.save(area);
			}
			return "redirect:/area";

		}

	}

	@GetMapping("/area/edit/{id}")
	@PreAuthorize("hasAuthority('area_update')")
	public String updateArea(@PathVariable(value = "id") long id, Model model, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		Area area = areaService.getById(id);
		List<Organization> organizationList = null;
		if(null == user.getOrganization()) {
			organizationList = (List<Organization>) organizationRepository.findAll();
		}else {
			organizationList = organizationRepository.findByIdAndIsDeletedFalse(user.getOrganization().getId());
		}
		
		model.addAttribute("listOrganization", organizationList);
		model.addAttribute("listBranch", branchRepository.findByOrganizationAndIsDeletedFalse(user.getOrganization()));
		model.addAttribute("listDoor", doorRepository.findByOrganizationAndIsDeletedFalse(user.getOrganization()));
		model.addAttribute("area", area);
		model.addAttribute("title", "Update Area");
		model.addAttribute("corsightEnabled", corsightEnabled);
		return "area/area_new";
	}

	@GetMapping("/area/delete/{id}")
	@PreAuthorize("hasAuthority('area_delete')")
	public String deleteArea(@PathVariable(value = "id") long id) {

		this.areaService.deletedById(id);
		return "redirect:/area";
	}

	@RequestMapping(value = "/api/search/area", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('area_view')")
	public @ResponseBody PaginationDto<Area> search(Long id, String name, String office, int pageno, String sortField,
			String sortDir, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String orgName = (null == user.getOrganization()? null: user.getOrganization().getName());

		PaginationDto<Area> dtoList = areaService.searchByField(id, name, office, pageno, sortField, sortDir,orgName);
		return dtoList;
	}

	@GetMapping("/area-to-employee/association/{id}")
	@PreAuthorize("hasAuthority('area_employee_association')")
	public String areaEmployeeAssociation(@PathVariable(value = "id") long id, Model model) {
		Area areaObj=areaService.getById(id);
		model.addAttribute("area", areaObj.getName());
		if(null!=areaObj.getBranch())
			model.addAttribute("Office", areaObj.getBranch().getName());
		else
			model.addAttribute("Office", "");
		model.addAttribute("id", id);
		return "area/area_employee";
	}

	@RequestMapping(value = "/api/search/area-to-employee/association", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('area_employee_association')")
	public @ResponseBody PaginationDto<Employee> search(String id, String name, String office, String area, int pageno, String sortField, String sortDir, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String orgName = (null == user.getOrganization()? null: user.getOrganization().getName());
		
		PaginationDto<Employee> dtoList = areaService.searchAreaToEmployee(id, name, office, area, pageno, sortField, sortDir,orgName);
		return dtoList;
	}

	@GetMapping("/area-to-employee/association/save")
	@PreAuthorize("hasAuthority('area_employee_association')")
	public @ResponseBody ResponseEntity<Object> saveAreaEmployeeAssociation(@RequestParam Long employeeId,
			@RequestParam Long areaId, Principal principal) {

		String message = areaService.saveAreaEmployeeAssociation(employeeId, areaId, principal);
		return ResponseEntity.ok(message);
	}

	@GetMapping("/area-to-device/association/{id}/{branchId}")
	@PreAuthorize("hasAuthority('area_device_association')")
	public String areaDeviceAssociation(@PathVariable(value = "id") long id,
			@PathVariable(value = "branchId") long branchId, Model model) {
		Area areaObj = areaService.getById(id);

		model.addAttribute("device", new Device());

		model.addAttribute("area", areaObj.getName());
		if (null != areaObj.getBranch())
			model.addAttribute("Office", areaObj.getBranch().getName());
		else
			model.addAttribute("Office", "");
		model.addAttribute("id", id);
		model.addAttribute("branchId", branchId);
		return "area/area_device";
	}

	@RequestMapping(value = "/api/search/area-to-device/association", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('area_device_association')")
	public @ResponseBody PaginationDto<Device> searchAreaToDevice(String name, String office,
			String area, int pageno, String sortField, String sortDir, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String orgName = (null == user.getOrganization()? null: user.getOrganization().getName());

		PaginationDto<Device> dtoList = areaService.searchAreaToDevice(name, office, area, pageno, sortField,
				sortDir,orgName);
		return dtoList;
	}


	@GetMapping("/area-to-device/association/save")
	@PreAuthorize("hasAuthority('area_device_association')")
	public @ResponseBody ResponseEntity<Object> saveAreaDeviceAssociation(@RequestParam Long deviceId,
			@RequestParam Long areaId, Principal principal) {

		String message = areaService.saveAreaDeviceAssociation(deviceId, areaId, principal);
		return ResponseEntity.ok(message);
	}
}
