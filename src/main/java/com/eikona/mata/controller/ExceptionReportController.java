package com.eikona.mata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eikona.mata.dto.ExceptionSummaryDto;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.DailyAttendance;
import com.eikona.mata.service.DailyAttendanceService;
import com.eikona.mata.service.OrganizationService;

@Controller
public class ExceptionReportController {

	@Autowired
	private DailyAttendanceService dailyAttendanceService;

	@Autowired
	private OrganizationService organizationService;

	@GetMapping(value = "/dailysummary")
	@PreAuthorize("hasAuthority('exception_summary_view')")
	public String exceptionDailyReportSummaryPage(Model model) {
		model.addAttribute("listOrganization", organizationService.getAll());
		return "reports/exceptionSummary";
	}

	
	@RequestMapping(value = "/api/search/exceptionsummary", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('exception_summary_view')")
	public @ResponseBody PaginationDto<ExceptionSummaryDto> exceptionDailyAttendanceSummaryData(String sDate, String eDate, String company, int pageno, String sortField, String sortDir) {
		
		PaginationDto<ExceptionSummaryDto> dtoList = dailyAttendanceService.search( sDate, eDate, company, pageno, sortField, sortDir);
		
		return dtoList;
	}

	@RequestMapping(value = "/api/list/present/{date}/{company}", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('exception_summary_view')")
	public String listDailyAttendancePresent(@PathVariable(value = "date") String date,
			@PathVariable(value = "company") String company, Model model) {
		try {
			model.addAttribute("title", "Present Employee Report");
			model.addAttribute("date", date);
			model.addAttribute("flag", "present");
			model.addAttribute("organization", company);

			return "reports/listOfDailyAttendanceEmp";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@RequestMapping(value = "/api/search/present/daily-attendance", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('exception_summary_view')")
	public @ResponseBody PaginationDto<DailyAttendance> searchPresent(Long id, String date, String organization, String employeeId, String employeeName, String office, String department, String designation,
			int pageno, String sortField, String sortDir) {
		
		PaginationDto<DailyAttendance> dtoList = dailyAttendanceService.searchPresent(id, date, organization, employeeId, employeeName, office, department, designation, pageno, sortField, sortDir);
		
		return dtoList;
	}

	@RequestMapping(value = "/api/list/inNoMask/{date}/{company}", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('exception_summary_view')")
	public String listDailyAttendanceInMask(@PathVariable(value = "date") String date,
			@PathVariable(value = "company") String company, Model model) {
		try {
			model.addAttribute("date", date);
			model.addAttribute("flag", "inNoMask");
			model.addAttribute("organization", company);
			model.addAttribute("title", "In Not Wearing Mask ");
			
			return "reports/listOfDailyAttendanceEmp";

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping(value = "/api/search/inNoMask/daily-attendance", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('exception_summary_view')")
	public @ResponseBody PaginationDto<DailyAttendance> searchInNoMask(Long id, String date, String organization, String employeeId, String employeeName, String office, String department, String designation,
			int pageno, String sortField, String sortDir) {
		
		PaginationDto<DailyAttendance> dtoList = dailyAttendanceService.searchInNoMask(id, date, organization, employeeId, employeeName, office, department, designation, pageno, sortField, sortDir);
		
		return dtoList;
	}

	@RequestMapping(value = "/api/list/outNoMask/{date}/{company}", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('exception_summary_view')")
	public String listDailyAttendanceOutMask(@PathVariable(value = "date") String date,
			@PathVariable(value = "company") String company, Model model) {
		try {
			model.addAttribute("date", date);
			model.addAttribute("flag", "outNoMask");
			model.addAttribute("organization", company);
			model.addAttribute("title", "Out Not Wearing Mask");
			return "reports/listOfDailyAttendanceEmp";

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@RequestMapping(value = "/api/search/outNoMask/daily-attendance", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('exception_summary_view')")
	public @ResponseBody PaginationDto<DailyAttendance> searchOutNoMask(Long id, String date, String organization, String employeeId, String employeeName, String office, String department, String designation,
			int pageno, String sortField, String sortDir) {
		
		PaginationDto<DailyAttendance> dtoList = dailyAttendanceService.searchOutNoMask(id, date, organization, employeeId, employeeName, office, department, designation, pageno, sortField, sortDir);
		
		return dtoList;
	}

	@RequestMapping(value = "/api/list/missedOutPunch/{date}/{company}", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('exception_summary_view')")
	public String listDailyAttendanceMissOutPunch(@PathVariable(value = "date") String date,
			@PathVariable(value = "company") String company, Model model) {
		try {
			model.addAttribute("date", date);
			model.addAttribute("flag", "missedOutPunch");
			model.addAttribute("organization", company);
			model.addAttribute("title", "Missed Out Punch Report");
			return "reports/listOfDailyAttendanceEmp";

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@RequestMapping(value = "/api/search/missedOutPunch/daily-attendance", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('exception_summary_view')")
	public @ResponseBody PaginationDto<DailyAttendance> searchMissedOutPunch(Long id, String date, String organization, String employeeId, String employeeName, String office, String department, String designation,
			int pageno, String sortField, String sortDir) {
		
		PaginationDto<DailyAttendance> dtoList = dailyAttendanceService.searchMissedOutPunch(id, date, organization, employeeId, employeeName, office, department, designation, pageno, sortField, sortDir);
		
		return dtoList;
	}
	@RequestMapping(value = "/api/list/overTime/{date}/{company}", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('exception_summary_view')")
	public String listDailyAttendanceOverTime(@PathVariable(value = "date") String date,
			@PathVariable(value = "company") String company, Model model) {
		try {
			model.addAttribute("date", date);
			model.addAttribute("flag", "overTime");
			model.addAttribute("organization", company);
			model.addAttribute("title", "Over Time Report");
			return "reports/listOfDailyAttendanceEmp";

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@RequestMapping(value = "/api/search/overTime/daily-attendance", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('exception_summary_view')")
	public @ResponseBody PaginationDto<DailyAttendance> searchOverTime(Long id, String date, String organization, String employeeId, String employeeName, String office, String department, String designation,
			int pageno, String sortField, String sortDir) {
		
		PaginationDto<DailyAttendance> dtoList = dailyAttendanceService.searchOverTime(id, date, organization, employeeId, employeeName, office, department, designation, pageno, sortField, sortDir);
		
		return dtoList;
	}

	@RequestMapping(value = "/api/list/inAbnormalTemp/{date}/{company}", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('exception_summary_view')")
	public String listInAbnormalTemp(@PathVariable(value = "date") String date,
			@PathVariable(value = "company") String company, Model model) {
		try {
			model.addAttribute("date", date);
			model.addAttribute("flag", "inAbnormalTemp");
			model.addAttribute("organization", company);
			model.addAttribute("title", "In Abnormal Temperature Report");
			return "reports/listOfDailyAttendanceEmp";

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@RequestMapping(value = "/api/search/inAbnormalTemp/daily-attendance", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('exception_summary_view')")
	public @ResponseBody PaginationDto<DailyAttendance> searchInAbnormalTemp(Long id, String date, String organization, String employeeId, String employeeName, String office, String department, String designation,
			int pageno, String sortField, String sortDir) {
		
		PaginationDto<DailyAttendance> dtoList = dailyAttendanceService.searchInAbnormalTemp(id, date, organization, employeeId, employeeName, office, department, designation, pageno, sortField, sortDir);
		
		return dtoList;
	}

	@RequestMapping(value = "/api/list/outAbnormalTemp/{date}/{company}", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('exception_summary_view')")
	public String listoutAbnormalTemp(@PathVariable(value = "date") String date,
			@PathVariable(value = "company") String company, Model model) {
		try {

			model.addAttribute("date", date);
			model.addAttribute("flag", "outAbnormalTemp");
			model.addAttribute("organization", company);
			model.addAttribute("title", "Out Abnormal Temperature Report");
			return "reports/listOfDailyAttendanceEmp";

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@RequestMapping(value = "/api/search/outAbnormalTemp/daily-attendance", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('exception_summary_view')")
	public @ResponseBody PaginationDto<DailyAttendance> searchOutAbnormalTemp(Long id, String date, String organization, String employeeId, String employeeName, String office, String department, String designation,
			int pageno, String sortField, String sortDir) {
		
		PaginationDto<DailyAttendance> dtoList = dailyAttendanceService.searchOutAbnormalTemp(id, date, organization, employeeId, employeeName, office, department, designation, pageno, sortField, sortDir);
		
		return dtoList;
	}

	@RequestMapping(value = "/api/list/lateComing/{date}/{company}", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('exception_summary_view')")
	public String listlatecoming(@PathVariable(value = "date") String date,
			@PathVariable(value = "company") String company, Model model) {
		try {
			model.addAttribute("date", date);
			model.addAttribute("flag", "lateComing");
			model.addAttribute("organization", company);
			model.addAttribute("title", "Late Coming Report");
			return "reports/listOfDailyAttendanceEmp";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@RequestMapping(value = "/api/search/lateComing/daily-attendance", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('exception_summary_view')")
	public @ResponseBody PaginationDto<DailyAttendance> searchLateComing(Long id, String date, String organization, String employeeId, String employeeName, String office, String department, String designation,
			int pageno, String sortField, String sortDir) {
		
		PaginationDto<DailyAttendance> dtoList = dailyAttendanceService.searchLateComing(id, date, organization, employeeId, employeeName, office, department, designation, pageno, sortField, sortDir);
		
		return dtoList;
	}

	@RequestMapping(value = "/api/list/earlyGoing/{date}/{company}", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('exception_summary_view')")
	public String listearlyGoing(@PathVariable(value = "date") String date,
			@PathVariable(value = "company") String company, Model model) {
		try {
			model.addAttribute("date", date);
			model.addAttribute("flag", "earlyGoing");
			model.addAttribute("organization", company);
			model.addAttribute("title", "Early Going Report");
			return "reports/listOfDailyAttendanceEmp";

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@RequestMapping(value = "/api/search/earlyGoing/daily-attendance", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('exception_summary_view')")
	public @ResponseBody PaginationDto<DailyAttendance> searchEarlyGoing(Long id, String date, String organization, String employeeId, String employeeName, String office, String department, String designation,
			int pageno, String sortField, String sortDir) {
		
		PaginationDto<DailyAttendance> dtoList = dailyAttendanceService.searchEarlyGoing(id, date, organization, employeeId, employeeName, office, department, designation, pageno, sortField, sortDir);
		
		return dtoList;
	}

	@RequestMapping(value = "/api/list/lessTime/{date}/{company}", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('exception_summary_view')")
	public String listDailyAttendanceLessTime(@PathVariable(value = "date") String date,
			@PathVariable(value = "company") String company, Model model) {
		try {

			model.addAttribute("date", date);
			model.addAttribute("flag", "lessTime");
			model.addAttribute("organization", company);
			model.addAttribute("title", "Less Time Report");
			return "reports/listOfDailyAttendanceEmp";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@RequestMapping(value = "/api/search/lessTime/daily-attendance", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('exception_summary_view')")
	public @ResponseBody PaginationDto<DailyAttendance> searchLessTime(Long id, String date, String organization, String employeeId, String employeeName, String office, String department, String designation,
			int pageno, String sortField, String sortDir) {
		
		PaginationDto<DailyAttendance> dtoList = dailyAttendanceService.searchLessTime(id, date, organization, employeeId, employeeName, office, department, designation, pageno, sortField, sortDir);
		
		return dtoList;
	}
}
