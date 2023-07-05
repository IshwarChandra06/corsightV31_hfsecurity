package com.eikona.mata.service.impl.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.AreaConstants;
import com.eikona.mata.constants.DailyAttendanceConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Area;
import com.eikona.mata.entity.DailyAttendance;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.repository.AreaRepository;
import com.eikona.mata.repository.DailyAttendanceRepository;
import com.eikona.mata.service.AbsentReportService;
import com.eikona.mata.util.CalendarUtil;
import com.eikona.mata.util.CorsightDeviceUtil;
import com.eikona.mata.util.EmployeeObjectMap;
import com.eikona.mata.util.GeneralSpecificationUtil;

@Service
public class AbsentReportServiceImpl implements AbsentReportService {

	@Autowired
	private DailyAttendanceRepository dailyAttendanceRepository;

	@Autowired
	private EmployeeObjectMap employeeObjectMap;

	@Autowired
	private CorsightDeviceUtil corsightDeviceUtil;

	@Autowired
	private AreaRepository areaRepository;

	@Autowired
	private GeneralSpecificationUtil<DailyAttendance> generalSpecification;
	
	@Autowired
	private CalendarUtil calendarUtil; 

	@Override
	public PaginationDto<DailyAttendance> search(Long id, String sDate, String eDate, String employeeId,
			String employeeName, String office, String department, String designation, int pageno, String sortField,
			String sortDir,String organization) {

		Date startDate = null;
		Date endDate = null;
		if (!sDate.isEmpty() && !eDate.isEmpty()) {
			SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
			try {
				startDate = format.parse(sDate);
				endDate = format.parse(eDate);
				endDate = calendarUtil.getConvertedDate(endDate, NumberConstants.TWENTY_THREE, NumberConstants.FIFTY_NINE, NumberConstants.FIFTY_NINE);
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Page<DailyAttendance> page = getPaginatedDailyAttendance(id, employeeId, employeeName, office, department,
				designation, pageno, startDate, endDate, sort,organization);
		List<DailyAttendance> employeeShiftList = page.getContent();
		
		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
		PaginationDto<DailyAttendance> dtoList = new PaginationDto<DailyAttendance>(employeeShiftList,
				page.getTotalPages(), page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(),
				page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		
		return dtoList;
	}

	private Page<DailyAttendance> getPaginatedDailyAttendance(Long id, String employeeId, String employeeName,
			String office, String department, String designation, int pageno, Date startDate, Date endDate, Sort sort, String organization) {
		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);

		Specification<DailyAttendance> idSpec = generalSpecification.longSpecification(id, ApplicationConstants.ID);
		Specification<DailyAttendance> dateSpec = generalSpecification.dateSpecification(startDate, endDate, ApplicationConstants.DATE);
		Specification<DailyAttendance> empIdSpec = generalSpecification.stringSpecification(employeeId, DailyAttendanceConstants.EMPLOYEE_ID);
		Specification<DailyAttendance> empNameSpec = generalSpecification.stringSpecification(employeeName,DailyAttendanceConstants.EMPLOYEE_NAME);
		Specification<DailyAttendance> offSpec = generalSpecification.stringSpecification(office, DailyAttendanceConstants.BRANCH);
		Specification<DailyAttendance> deptSpec = generalSpecification.stringSpecification(department, DailyAttendanceConstants.DEPARTMENT);
		Specification<DailyAttendance> desiSpec = generalSpecification.stringSpecification(designation, DailyAttendanceConstants.DESIGNATION);
		Specification<DailyAttendance> orgSpec = generalSpecification.stringSpecification(organization, "organization");
		Specification<DailyAttendance> absentSpec = generalSpecification.stringSpecification(DailyAttendanceConstants.ABSENT, DailyAttendanceConstants.ATTENDANCE_STATUS);
		Page<DailyAttendance> page = dailyAttendanceRepository.findAll(
				idSpec.and(dateSpec).and(empIdSpec).and(empNameSpec).and(offSpec).and(deptSpec).and(desiSpec).and(absentSpec).and(orgSpec),
				pageable);
		return page;
	}

	public void addEmployeeStatusToInactive() {
		Map<Long, Employee> employeeMap = employeeObjectMap.getEmployeeByIsDeletedFalseAndIsSyncTrue();

		List<DailyAttendance> dailyAttendanceList = dailyAttendanceRepository.findByAttendanceStatusAbsentCustom();

		Date previousDate = null;
		String previousEmpid = null;
		int count = NumberConstants.ZERO;
		for (DailyAttendance dailyAttendance : dailyAttendanceList) {
			Date currentDate = dailyAttendance.getDate();
			String currentEmpid = dailyAttendance.getEmpId();

			if (null != previousEmpid && previousEmpid.equalsIgnoreCase(currentEmpid)) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(previousDate);
				calendar.add(Calendar.DATE, NumberConstants.ONE);

				previousDate = calendar.getTime();
			} else {
				previousEmpid = null;
				count = NumberConstants.ZERO;
			}

			if (previousEmpid == null) {
				previousDate = currentDate;
				previousEmpid = currentEmpid;
			}

			if ((currentDate.compareTo(previousDate) == NumberConstants.ZERO) && (currentEmpid.equalsIgnoreCase(previousEmpid))) {
				previousDate = currentDate;
				previousEmpid = currentEmpid;
				count++;
			} else {
				count = NumberConstants.ONE;
				previousDate = currentDate;
				previousEmpid = currentEmpid;
			}
			if (count == NumberConstants.THREE) {
				addEmployeeToBlacklist(employeeMap, dailyAttendance);
			}

		}
	}

	private void addEmployeeToBlacklist(Map<Long, Employee> employeeMap, DailyAttendance dailyAttendance) {
		Employee employee = employeeMap.get(Long.valueOf(dailyAttendance.getEmpId()));
		List<Area> areaList = employee.getArea();
		for (Area area : areaList) {
			corsightDeviceUtil.removePoiFromWatchList(area, employee);
		}
		Area blacklistArea = areaRepository.findByWatchlistAndIsDeletedFalse(AreaConstants.BLACKLIST);
		corsightDeviceUtil.addPoiToWatchList(blacklistArea, employee);
	}

	public void addEmployeeStatusToActive() {
		Map<Long, Employee> employeeMap = employeeObjectMap.getEmployeeByIsDeletedFalseAndIsSyncTrue();
		List<DailyAttendance> dailyReportList = (List<DailyAttendance>) dailyAttendanceRepository.findAll();
		for (DailyAttendance dailyReport : dailyReportList) {
			Employee employee = employeeMap.get(Long.valueOf(dailyReport.getEmpId()));
			List<Area> areaList = employee.getArea();
			for (Area area : areaList) {
				corsightDeviceUtil.addPoiToWatchList(area, employee);
			}
		}
	}
}
