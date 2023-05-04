package com.eikona.mata.service.impl.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.DailyAttendanceConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.dto.ExceptionSummaryDto;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.DailyAttendance;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.Transaction;
import com.eikona.mata.repository.DailyAttendanceRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.repository.TransactionRepository;
import com.eikona.mata.service.DailyAttendanceService;
import com.eikona.mata.util.CalendarUtil;
import com.eikona.mata.util.GeneralSpecificationUtil;

@Service
public class DailyAttendanceServiceImpl implements DailyAttendanceService {

	@Autowired
	private DailyAttendanceRepository dailyAttendanceRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private GeneralSpecificationUtil<DailyAttendance> generalSpecificationDailyAttendance;
	
	@Autowired
	private CalendarUtil calendarUtil;

	@Override
	public List<DailyAttendance> findAll() {
		return (List<DailyAttendance>) dailyAttendanceRepository.findAll();
	}
	
   public void generateNotPunchDailyAttendance(String sDate, String eDate) {
		
		try {
			
			SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			sDate = format.format(inputFormat.parse(sDate));
			eDate = format.format(inputFormat.parse(eDate));
			
			Date startDate = calendarUtil.getConvertedDate(format.parse(sDate), 00, 00, 00);
			Date endDate = calendarUtil.getConvertedDate(format.parse(eDate), 23, 59, 59);
			
			List<String> dailyReportEmpIdList = dailyAttendanceRepository.findByDateCustom(startDate, endDate);
			
			if(dailyReportEmpIdList.isEmpty())
				dailyReportEmpIdList.add(" ");
			
			List<Employee> absentEmployeeList = employeeRepository.findByEmpIdAndIsDeletedFalseCustom(dailyReportEmpIdList);
			List<DailyAttendance> dailyReportList = new ArrayList<>();
			for(Employee employee : absentEmployeeList) {
				
				DailyAttendance dailyReport = new DailyAttendance();
				dailyReport.setEmpId(employee.getEmpId().trim());
				dailyReport.setDateStr(sDate);
				dailyReport.setDate(startDate);
				dailyReport.setEmployeeName(employee.getName());
				dailyReport.setOrganization((null == employee.getOrganization()?"":employee.getOrganization().getName()));
				dailyReport.setDepartment((null == employee.getDepartment()?"":employee.getDepartment().getName()));
				dailyReport.setDesignation((null == employee.getDesignation()?"":employee.getDesignation().getName()));
				if(null!=employee.getBranch())
					  dailyReport.setBranch(employee.getBranch().getName());
				dailyReport.setAttendanceStatus("-");
				dailyReport.setMissedOutPunch("No");
				dailyReportList.add(dailyReport);
				
			}
			
			dailyAttendanceRepository.saveAll(dailyReportList);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	public List<DailyAttendance> generateDailyAttendanceShiftWise(String sDate, String eDate) {

		List<DailyAttendance> dailyReportList = new ArrayList<>();
		try {
			
			SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			sDate = format.format(inputFormat.parse(sDate));
			eDate = format.format(inputFormat.parse(eDate));
			
			Date startDate = calendarUtil.getConvertedDate(format.parse(sDate), 00, 00, 00);
			Date endDate = calendarUtil.getConvertedDate(format.parse(eDate), 23, 59, 59);
	
			List<Transaction> transactionList = transactionRepository.getTransactionData(startDate,endDate);
			
			Map<String, DailyAttendance> reportMap = new HashMap<String, DailyAttendance>();

			for (Transaction transaction : transactionList) {
	
				try {
					
					String key = transaction.getEmpId() + "-" + transaction.getPunchDateStr();
					key = key.trim();
					DailyAttendance dailyReport = null;
					
					if(!reportMap.containsKey(key)) {
						
						dailyReport = new DailyAttendance();
						dailyReport.setEmpId(transaction.getEmpId().trim());
						dailyReport.setDateStr(transaction.getPunchDateStr());
						dailyReport.setDate(format.parse(transaction.getPunchDateStr()));
						dailyReport.setEmployeeName(transaction.getName());
						dailyReport.setOrganization(transaction.getEmployee().getOrganization().getName());
						dailyReport.setDepartment(transaction.getDepartment());
						dailyReport.setDesignation(transaction.getDesignation());
						if(null!=transaction.getEmployee().getBranch())
						  dailyReport.setBranch(transaction.getEmployee().getBranch().getName());
						dailyReport.setMissedOutPunch("Yes");
						dailyReport.setEmpInTime(transaction.getPunchTimeStr());
						dailyReport.setEmpInTemp(transaction.getTemperature());
						dailyReport.setEmpInMask(transaction.getMaskStatus());
						dailyReport.setEmpInAccessType("Face");
						dailyReport.setEmpInLocation(transaction.getDeviceName());
						dailyReport.setAttendanceStatus("Present");
						String city = "Moscow";
						System.out.println(transaction.getPunchTimeStr());
							int hour = Integer.parseInt(transaction.getPunchTimeStr().split(":")[0]);
							int minute = Integer.parseInt(transaction.getPunchTimeStr().split(":")[1]);
							if(hour > 5 && hour<19){
								
								if (hour<7) {
									dailyReport.setShift("1 смена"); 
									dailyReport.setShiftInTime("07:00:00");
									dailyReport.setShiftOutTime("16:00:00");
							}
							
							if(hour == 7) {
								if(minute <= 10) {
									dailyReport.setShift("1 смена"); 
									dailyReport.setShiftInTime("07:00:00");
									dailyReport.setShiftOutTime("16:00:00");
								}else if(minute > 10) {
									dailyReport.setShift("2 смена"); 
									dailyReport.setShiftInTime("08:00:00");
									dailyReport.setShiftOutTime("17:00:00");
								}
									
							}
							
							if(hour == 8) {
								if(minute <= 10) {
									dailyReport.setShift("2 смена"); 
									dailyReport.setShiftInTime("08:00:00");
									dailyReport.setShiftOutTime("17:00:00");
								}else if(minute > 10) {
									dailyReport.setShift("3 смена"); 
									dailyReport.setShiftInTime("09:00:00");
									dailyReport.setShiftOutTime("18:00:00");
								}
									
							}
							
							if(hour == 9) {
								if(minute <= 10) {
									dailyReport.setShift("3 смена"); 
									dailyReport.setShiftInTime("09:00:00");
									dailyReport.setShiftOutTime("18:00:00");
								}else if(minute > 10) {
									dailyReport.setShift("4 смена"); 
									dailyReport.setShiftInTime("10:00:00");
									dailyReport.setShiftOutTime("19:00:00");
								}
									
							}
							
							if(hour >=10 && hour < 19) {
								dailyReport.setShift("4 смена"); 
								dailyReport.setShiftInTime("10:00:00");
								dailyReport.setShiftOutTime("19:00:00");
						}
						
						dailyReport.setCity(city);
	
						LocalTime shiftIn = LocalTime.parse(dailyReport.getShiftInTime());
						LocalTime empIn = LocalTime.parse(dailyReport.getEmpInTime().replace("'", ""));
	
						Long lateComing = shiftIn.until(empIn, ChronoUnit.MINUTES);
						Long earlyComing = empIn.until(shiftIn, ChronoUnit.MINUTES);
	
						if (earlyComing > 0)
							dailyReport.setEarlyComing(earlyComing);
	
						if (lateComing > 0)
							dailyReport.setLateComing(lateComing);
						
						dailyReport.setCity(city);
						reportMap.put(key, dailyReport);
							}
							
					} else {
						
						dailyReport = reportMap.get(key);
						if (dailyReport.getEmpInTime().equalsIgnoreCase(transaction.getPunchTimeStr())) {
							continue;
						} else {
							
							dailyReport.setEmpOutTime(transaction.getPunchTimeStr());
							dailyReport.setEmpOutTemp(transaction.getTemperature());
							dailyReport.setEmpOutMask(transaction.getMaskStatus());
							dailyReport.setEmpOutAccessType("Face");
							dailyReport.setEmpOutLocation(transaction.getDeviceName());
	
							dailyReport.setMissedOutPunch("No");
	
							LocalTime shiftIn = LocalTime.parse(dailyReport.getShiftInTime());
							LocalTime shiftOut = LocalTime.parse(dailyReport.getShiftOutTime());
							LocalTime empIn = LocalTime.parse(dailyReport.getEmpInTime());
							LocalTime empOut = LocalTime.parse(dailyReport.getEmpOutTime());
	
                            Long shiftMinutes = shiftIn.until(shiftOut, ChronoUnit.MINUTES);
							
	
							Long workHours = empIn.until(empOut, ChronoUnit.HOURS);
							Long workMinutes = empIn.until(empOut, ChronoUnit.MINUTES);
							
							if(workHours<0) {
								workHours = 24 + workHours;
								workMinutes = 60 + workMinutes;
							}
							
	
							dailyReport.setWorkTime(String.valueOf(workHours) + ":" + String.valueOf(workMinutes % 60));
							dailyReport.setWorkMinute(workMinutes);
	
							Long overTime = workMinutes - shiftMinutes;
							Long earlyGoing =0l;
							Long lateGoing =0l;
							
							 lateGoing = shiftOut.until(empOut, ChronoUnit.MINUTES);
							 earlyGoing = empOut.until(shiftOut, ChronoUnit.MINUTES);
	
							if (lateGoing > 0)
								dailyReport.setLateGoing(lateGoing);
							else
								dailyReport.setLateGoing(null);
	
							if (earlyGoing > 0) {
								dailyReport.setEarlyGoing(earlyGoing);
							} else {
								dailyReport.setEarlyGoing(null);
							}
	
							if (overTime > 0) {
								dailyReport.setOverTime(overTime);
								dailyReport.setOverTimeStr(overTime/60+":"+(overTime % 60));
							}
								
							
							reportMap.put(key, dailyReport);
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		
			Set<String> keySet =  reportMap.keySet();
			for (String string : keySet) {
				dailyReportList.add(reportMap.get(string));
			}
			dailyAttendanceRepository.saveAll(dailyReportList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dailyReportList;
	}

	@Override
	public List<DailyAttendance> generateDailyAttendance(String sDate, String eDate) {

		SimpleDateFormat fontformat = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_INDIA_SPLIT_BY_SLASH);
		SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
		
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = fontformat.parse(sDate);

			sDate = format.format(startDate);
			startDate = format.parse(sDate);

			endDate = fontformat.parse(eDate);

			eDate = format.format(endDate);
			endDate = format.parse(eDate);

		} catch (ParseException e) {
			e.printStackTrace();
		}

		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(startDate);

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endDate);
		endCalendar.add(Calendar.DATE, NumberConstants.ONE);

		List<Employee> employeeList = (List<Employee>) employeeRepository.findAllByIsDeletedFalse();
		
		List<DailyAttendance> dailyAttendanceList = new ArrayList<>();
		while (startCalendar.before(endCalendar)) {
			for (Employee employee : employeeList) {
				
				DailyAttendance dailyAttendance = new DailyAttendance();
				List<Transaction> eventReportMinList = transactionRepository.findByEmpIdMinCustom(employee.getEmpId(),
						format.format(startCalendar.getTime()));
				
				List<Transaction> eventReportMaxList = transactionRepository.findByEmpIdMaxCustom(employee.getEmpId(),
						format.format(startCalendar.getTime()));

				Iterator<Transaction> eventReportMinItr = eventReportMinList.iterator();
				Iterator<Transaction> eventReportMaxItr = eventReportMaxList.iterator();
				Transaction eventReportMin = null;
				Transaction eventReportMax = null;
				if (eventReportMinItr.hasNext() && eventReportMaxItr.hasNext()) {
					eventReportMin = eventReportMinItr.next();
					eventReportMax = eventReportMaxItr.next();
				}

				dailyAttendance.setDate(startCalendar.getTime());
				dailyAttendance.setDateStr(format.format(startCalendar.getTime()));
				setEmployeeDetails(employee, dailyAttendance); 

				if (eventReportMinList.isEmpty() && eventReportMaxList.isEmpty()) {
					// absent
					dailyAttendance.setAttendanceStatus(DailyAttendanceConstants.ABSENT);
					DailyAttendance dailyAttendanceTest = dailyAttendanceRepository.findByEmpIdAndDate(employee.getEmpId(), startCalendar.getTime());
					if (null == dailyAttendanceTest) {
						dailyAttendanceList.add(dailyAttendance);
					}
				} else {
					// present
					dailyAttendance.setEmpInLocation(eventReportMin.getDeviceName());
					dailyAttendance.setEmpInMask((eventReportMin.getWearingMask() == true) ? ApplicationConstants.TRUE : ApplicationConstants.FALSE);
					dailyAttendance.setEmpInTime(eventReportMin.getPunchTimeStr());
					dailyAttendance.setAttendanceStatus(DailyAttendanceConstants.PRESENT);
					if (!(eventReportMin.getPunchTimeStr().equalsIgnoreCase(eventReportMax.getPunchTimeStr()))) {
						dailyAttendance.setEmpOutLocation(eventReportMax.getDeviceName());
						dailyAttendance.setEmpOutMask((eventReportMin.getWearingMask() == true) ? ApplicationConstants.TRUE : ApplicationConstants.FALSE);
						dailyAttendance.setEmpOutTime(eventReportMax.getPunchTimeStr());

						LocalTime empIn = LocalTime.parse(eventReportMin.getPunchTimeStr());
						LocalTime empOut = LocalTime.parse(eventReportMax.getPunchTimeStr());
						Long workMin = empIn.until(empOut, ChronoUnit.MINUTES);
						Long workHr = empIn.until(empOut, ChronoUnit.HOURS);

						dailyAttendance.setWorkTime(String.valueOf(workHr) + ApplicationConstants.DELIMITER_COLON + String.valueOf(workMin % NumberConstants.SIXTY));
						dailyAttendance.setMissedOutPunch(ApplicationConstants.FALSE);
					} else
						dailyAttendance.setMissedOutPunch(ApplicationConstants.TRUE);

					DailyAttendance dailyAttendanceTest = dailyAttendanceRepository.findByEmpIdAndDate(employee.getEmpId(), startCalendar.getTime());
					if (null == dailyAttendanceTest) {
						dailyAttendanceList.add(dailyAttendance);
					}

				}
			}
			startCalendar.add(Calendar.DATE, NumberConstants.ONE);
		}
		dailyAttendanceRepository.saveAll(dailyAttendanceList);
		return dailyAttendanceList;
	}

	@Override
	public List<DailyAttendance> dailyReportDataTable(String sDate, String eDate) {
		Date start = null;
		Date end = null;
		try {
			start = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US).parse(sDate);
			end = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US).parse(eDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		List<DailyAttendance> dailyReport = dailyAttendanceRepository.findEventByDateCustom(start, end);
		return dailyReport;
	}

	@Override
	public List<ExceptionSummaryDto> exceptionDailyAttendanceSummaryData(String sDate, String eDate, String company) {
		List<ExceptionSummaryDto> listExceptionReport = new ArrayList<ExceptionSummaryDto>();
		try {

			SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
			Date startDate = calendarUtil.getConvertedDate(format.parse(sDate), NumberConstants.ZERO, NumberConstants.ZERO, NumberConstants.ZERO);
			

			Date endDate = calendarUtil.getConvertedDate(format.parse(eDate), NumberConstants.TWENTY_THREE, NumberConstants.FIFTY_NINE, NumberConstants.FIFTY_NINE);

			listExceptionReport = dailyAttendanceRepository.getAllExceptions(startDate, endDate, company);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return listExceptionReport;
	}

	@Override
	public PaginationDto<DailyAttendance> searchPresent(Long id, String dateStr, String organization, String employeeId,
			String employeeName, String office, String department, String designation, int pageno, String sortField,
			String sortDir) {

		SimpleDateFormat formatStr = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_INDIA);
		SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
		Date startDate = null;
		Date endDate = null;
		if (!dateStr.isEmpty()) {
			try {
				Date date = formatStr.parse(dateStr);
				dateStr = format.format(date);
				startDate = format.parse(dateStr);
				endDate = format.parse(dateStr);

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
		Specification<DailyAttendance> flagSpec = generalSpecificationDailyAttendance.stringSpecification(DailyAttendanceConstants.PRESENT, DailyAttendanceConstants.ATTENDANCE_STATUS);
		
		Page<DailyAttendance> page = getDailyAttendancePage(id, employeeId, employeeName, organization, office, department,
				designation, pageno, sortField, sortDir, startDate, endDate, flagSpec);
		
		List<DailyAttendance> employeeShiftList = page.getContent();

		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
		PaginationDto<DailyAttendance> dtoList = new PaginationDto<DailyAttendance>(employeeShiftList,
				page.getTotalPages(), page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(),
				page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	@Override
	public PaginationDto<DailyAttendance> searchInNoMask(Long id, String dateStr, String organization,
			String employeeId, String employeeName, String office, String department, String designation, int pageno,
			String sortField, String sortDir) {
		// empInMask
		SimpleDateFormat formatStr = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_INDIA);
		SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
		Date startDate = null;
		Date endDate = null;
		if (!dateStr.isEmpty()) {
			try {
				Date date = formatStr.parse(dateStr);
				dateStr = format.format(date);
				startDate = format.parse(dateStr);
				endDate = format.parse(dateStr);

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
		
		Specification<DailyAttendance> flagSpec = generalSpecificationDailyAttendance.stringSpecification(ApplicationConstants.TRUE, DailyAttendanceConstants.EMP_IN_MASK);
		
		Page<DailyAttendance> page = getDailyAttendancePage(id, employeeId, employeeName, organization, office, department,
				designation, pageno, sortField, sortDir, startDate, endDate, flagSpec);

		List<DailyAttendance> employeeShiftList = page.getContent();

		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
		PaginationDto<DailyAttendance> dtoList = new PaginationDto<DailyAttendance>(employeeShiftList,
				page.getTotalPages(), page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(),
				page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	@Override
	public PaginationDto<DailyAttendance> searchOutNoMask(Long id, String dateStr, String organization,
			String employeeId, String employeeName, String office, String department, String designation, int pageno,
			String sortField, String sortDir) {
		SimpleDateFormat formatStr = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_INDIA);
		SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
		Date startDate = null;
		Date endDate = null;
		if (!dateStr.isEmpty()) {
			try {
				Date date = formatStr.parse(dateStr);
				dateStr = format.format(date);
				startDate = format.parse(dateStr);
				endDate = format.parse(dateStr);

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

		Specification<DailyAttendance> flagSpec = generalSpecificationDailyAttendance.stringSpecification(ApplicationConstants.TRUE, DailyAttendanceConstants.EMP_OUT_MAST);

		Page<DailyAttendance> page = getDailyAttendancePage(id, employeeId, employeeName, organization, office, department,
				designation, pageno, sortField, sortDir, startDate, endDate, flagSpec);
		
		List<DailyAttendance> employeeShiftList = page.getContent();

		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
		PaginationDto<DailyAttendance> dtoList = new PaginationDto<DailyAttendance>(employeeShiftList,
				page.getTotalPages(), page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(),
				page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	@Override
	public PaginationDto<DailyAttendance> searchMissedOutPunch(Long id, String dateStr, String organization,
			String employeeId, String employeeName, String office, String department, String designation, int pageno,
			String sortField, String sortDir) {
		SimpleDateFormat formatStr = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_INDIA);
		SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
		Date startDate = null;
		Date endDate = null;
		if (!dateStr.isEmpty()) {
			try {
				Date date = formatStr.parse(dateStr);
				dateStr = format.format(date);
				startDate = format.parse(dateStr);
				endDate = format.parse(dateStr);

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

		Specification<DailyAttendance> flagSpec = generalSpecificationDailyAttendance.stringSpecification(ApplicationConstants.YES, DailyAttendanceConstants.MISSED_OUT_PUNCH);
      
		Page<DailyAttendance> page = getDailyAttendancePage(id, employeeId, employeeName,  organization,office, department,
				designation, pageno, sortField, sortDir, startDate, endDate, flagSpec);
		List<DailyAttendance> employeeShiftList = page.getContent();

		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
		PaginationDto<DailyAttendance> dtoList = new PaginationDto<DailyAttendance>(employeeShiftList,
				page.getTotalPages(), page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(),
				page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	@Override
	public PaginationDto<DailyAttendance> searchOverTime(Long id, String dateStr, String organization,
			String employeeId, String employeeName, String office, String department, String designation, int pageno,
			String sortField, String sortDir) {
		SimpleDateFormat formatStr = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_INDIA);
		SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
		Date startDate = null;
		Date endDate = null;
		if (!dateStr.isEmpty()) {
			try {
				Date date = formatStr.parse(dateStr);
				dateStr = format.format(date);
				startDate = format.parse(dateStr);
				endDate = format.parse(dateStr);

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

		Specification<DailyAttendance> flagSpec = generalSpecificationDailyAttendance.greaterThanSpecification(NumberConstants.ZERO, DailyAttendanceConstants.OVER_TIME);

		Page<DailyAttendance> page = getDailyAttendancePage(id, employeeId, employeeName, organization, office, department,
				designation, pageno, sortField, sortDir, startDate, endDate, flagSpec);
		List<DailyAttendance> employeeShiftList = page.getContent();

		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
		PaginationDto<DailyAttendance> dtoList = new PaginationDto<DailyAttendance>(employeeShiftList,
				page.getTotalPages(), page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(),
				page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	@Override
	public PaginationDto<DailyAttendance> searchInAbnormalTemp(Long id, String dateStr, String organization,
			String employeeId, String employeeName, String office, String department, String designation, int pageno,
			String sortField, String sortDir) {

		SimpleDateFormat formatStr = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_INDIA);
		SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
		Date startDate = null;
		Date endDate = null;
		if (!dateStr.isEmpty()) {
			try {
				Date date = formatStr.parse(dateStr);
				dateStr = format.format(date);
				startDate = format.parse(dateStr);
				endDate = format.parse(dateStr);

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

		Specification<DailyAttendance> flagSpec = generalSpecificationDailyAttendance.greaterThanSpecification(DailyAttendanceConstants.NINETY_NIN_POINT_ONE_FOUR, DailyAttendanceConstants.EMP_IN_TEMP);

		Page<DailyAttendance> page = getDailyAttendancePage(id, employeeId, employeeName, organization, office, department,
				designation, pageno, sortField, sortDir, startDate, endDate, flagSpec);
		List<DailyAttendance> employeeShiftList = page.getContent();

		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
		PaginationDto<DailyAttendance> dtoList = new PaginationDto<DailyAttendance>(employeeShiftList,
				page.getTotalPages(), page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(),
				page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	@Override
	public PaginationDto<DailyAttendance> searchOutAbnormalTemp(Long id, String dateStr, String organization,
			String employeeId, String employeeName, String office, String department, String designation, int pageno,
			String sortField, String sortDir) {
		SimpleDateFormat formatStr = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_INDIA);
		SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
		Date startDate = null;
		Date endDate = null;
		if (!dateStr.isEmpty()) {
			try {
				Date date = formatStr.parse(dateStr);
				dateStr = format.format(date);
				startDate = format.parse(dateStr);
				endDate = format.parse(dateStr);

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

		Specification<DailyAttendance> flagSpec = generalSpecificationDailyAttendance.greaterThanSpecification(DailyAttendanceConstants.NINETY_NIN_POINT_ONE_FOUR, DailyAttendanceConstants.EMP_OUT_TEMP);

		Page<DailyAttendance> page = getDailyAttendancePage(id, employeeId, employeeName, organization, office, department,
				designation, pageno, sortField, sortDir, startDate, endDate, flagSpec);
		List<DailyAttendance> employeeShiftList = page.getContent();

		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
		PaginationDto<DailyAttendance> dtoList = new PaginationDto<DailyAttendance>(employeeShiftList,
				page.getTotalPages(), page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(),
				page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	@Override
	public PaginationDto<DailyAttendance> searchLateComing(Long id, String dateStr, String organization,
			String employeeId, String employeeName, String office, String department, String designation, int pageno,
			String sortField, String sortDir) {
		SimpleDateFormat formatStr = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_INDIA);
		SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
		Date startDate = null;
		Date endDate = null;
		if (!dateStr.isEmpty()) {
			try {
				Date date = formatStr.parse(dateStr);
				dateStr = format.format(date);
				startDate = format.parse(dateStr);
				endDate = format.parse(dateStr);

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

		Specification<DailyAttendance> flagSpec = generalSpecificationDailyAttendance.greaterThanSpecification(NumberConstants.ZERO, DailyAttendanceConstants.LATE_COMING);

		Page<DailyAttendance> page = getDailyAttendancePage(id, employeeId, employeeName, organization, office, department,
				designation, pageno, sortField, sortDir, startDate, endDate, flagSpec);
		List<DailyAttendance> employeeShiftList = page.getContent();

		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
		PaginationDto<DailyAttendance> dtoList = new PaginationDto<DailyAttendance>(employeeShiftList,
				page.getTotalPages(), page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(),
				page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	@Override
	public PaginationDto<DailyAttendance> searchEarlyGoing(Long id, String dateStr, String organization,
			String employeeId, String employeeName, String office, String department, String designation, int pageno,
			String sortField, String sortDir) {
		SimpleDateFormat formatStr = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_INDIA);
		SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
		Date startDate = null;
		Date endDate = null;
		if (!dateStr.isEmpty()) {
			try {
				Date date = formatStr.parse(dateStr);
				dateStr = format.format(date);
				startDate = format.parse(dateStr);
				endDate = format.parse(dateStr);

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

		Specification<DailyAttendance> flagSpec = generalSpecificationDailyAttendance.greaterThanSpecification(NumberConstants.ZERO, DailyAttendanceConstants.EARLY_GOING);
				

		Page<DailyAttendance> page = getDailyAttendancePage(id, employeeId, employeeName, organization, office, department,
				designation, pageno, sortField, sortDir, startDate, endDate, flagSpec);
		List<DailyAttendance> employeeShiftList = page.getContent();

		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
		PaginationDto<DailyAttendance> dtoList = new PaginationDto<DailyAttendance>(employeeShiftList,
				page.getTotalPages(), page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(),
				page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	@Override
	public PaginationDto<DailyAttendance> searchLessTime(Long id, String dateStr, String organization,
			String employeeId, String employeeName, String office, String department, String designation, int pageno,
			String sortField, String sortDir) {
		SimpleDateFormat formatStr = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_INDIA);
		SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
		Date startDate = null;
		Date endDate = null;
		if (!dateStr.isEmpty()) {
			try {
				Date date = formatStr.parse(dateStr);
				dateStr = format.format(date);
				startDate = format.parse(dateStr);
				endDate = format.parse(dateStr);

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

		Specification<DailyAttendance> overTimeSpec = generalSpecificationDailyAttendance.isNullSpecification(DailyAttendanceConstants.OVER_TIME);
		Specification<DailyAttendance> wortTimeInNotNullSpec =generalSpecificationDailyAttendance.isNotNullSpecification(DailyAttendanceConstants.WORK_TIME);
		Specification<DailyAttendance> workTimeSpec =generalSpecificationDailyAttendance.stringNotSpecification(DailyAttendanceConstants.EIGHT_COLON_THIRTY, DailyAttendanceConstants.WORK_TIME);
		Specification<DailyAttendance> flagSpec = overTimeSpec.and(wortTimeInNotNullSpec).and(workTimeSpec);
		
		Page<DailyAttendance> page = getDailyAttendancePage(id, employeeId, employeeName, organization, office, department,
				designation, pageno, sortField, sortDir, startDate, endDate, flagSpec);
		List<DailyAttendance> employeeShiftList = page.getContent();

		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
		PaginationDto<DailyAttendance> dtoList = new PaginationDto<DailyAttendance>(employeeShiftList,
				page.getTotalPages(), page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(),
				page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	@Override
	public PaginationDto<DailyAttendance> searchByField(Long id, String sDate, String eDate, String employeeId,
			String employeeName, String office, String department, String designation, String status, int pageno,
			String sortField, String sortDir) {

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
		
		Specification<DailyAttendance> flagSpec = generalSpecificationDailyAttendance.stringSpecification(status, DailyAttendanceConstants.ATTENDANCE_STATUS);
		
		Page<DailyAttendance> page = getDailyAttendancePage(id, employeeId, employeeName, "", office, department,
				designation, pageno, sortField, sortDir, startDate, endDate, flagSpec);
		List<DailyAttendance> employeeShiftList = page.getContent();

		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
		PaginationDto<DailyAttendance> dtoList = new PaginationDto<DailyAttendance>(employeeShiftList,
				page.getTotalPages(), page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(),
				page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}
	
	private Page<DailyAttendance> getDailyAttendancePage(Long id, String employeeId, String employeeName, String organization, String office,
			String department, String designation, int pageno, String sortField, String sortDir, Date startDate,
			Date endDate, Specification<DailyAttendance> flagSpec) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);

		Specification<DailyAttendance> idSpec = generalSpecificationDailyAttendance.longSpecification(id, ApplicationConstants.ID);
		Specification<DailyAttendance> dateSpec = generalSpecificationDailyAttendance.dateSpecification(startDate, endDate, ApplicationConstants.DATE);
		Specification<DailyAttendance> empIdSpec = generalSpecificationDailyAttendance.stringSpecification(employeeId, DailyAttendanceConstants.EMPLOYEE_ID);
		Specification<DailyAttendance> empNameSpec = generalSpecificationDailyAttendance.stringSpecification(employeeName, DailyAttendanceConstants.EMPLOYEE_NAME);
		Specification<DailyAttendance> orgSpec = generalSpecificationDailyAttendance.stringSpecification(organization, "organization");
		Specification<DailyAttendance> offSpec = generalSpecificationDailyAttendance.stringSpecification(office, DailyAttendanceConstants.BRANCH);
		Specification<DailyAttendance> deptSpec = generalSpecificationDailyAttendance.stringSpecification(department, DailyAttendanceConstants.DEPARTMENT);
		Specification<DailyAttendance> desiSpec = generalSpecificationDailyAttendance.stringSpecification(designation, DailyAttendanceConstants.DESIGNATION);
		 List<DailyAttendance> l1= dailyAttendanceRepository.findAll(flagSpec.and(dateSpec).and(offSpec));
		Page<DailyAttendance> page = dailyAttendanceRepository.findAll(flagSpec.and(idSpec).and(dateSpec).and(empIdSpec)
				.and(empNameSpec).and(offSpec).and(deptSpec).and(desiSpec).and(orgSpec), pageable);
		return page;
	}
	

	@Override
	public PaginationDto<ExceptionSummaryDto> search( String sDate, String eDate, String organization,
			int pageno, String sortField, String sortDir) {

		if(organization.isEmpty())
			organization = null;
			
		Date startDate = null;
		Date endDate = null;
		if (!sDate.isEmpty() && !eDate.isEmpty()) {
			try {
				SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
				startDate = calendarUtil.getConvertedDate(format.parse(sDate), NumberConstants.ZERO, NumberConstants.ZERO, NumberConstants.ZERO);
				
				endDate = calendarUtil.getConvertedDate(format.parse(eDate), NumberConstants.TWENTY_THREE, NumberConstants.FIFTY_NINE, NumberConstants.FIFTY_NINE);

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

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);

		Page<ExceptionSummaryDto> page = null;
		if(null == startDate && null == endDate && null == organization ) {
			page = dailyAttendanceRepository.findByDateAndOrganizationCustom(0l, pageable);
		}else {
			page = dailyAttendanceRepository.findByDateAndOrganizationCustom(startDate, endDate,
					organization, pageable);
		}
//		Page<ExceptionSummaryDto> page = dailyAttendanceRepository.findByDateAndOrganizationCustom(startDate, endDate,
//				organization, pageable);
		List<ExceptionSummaryDto> employeeShiftList = page.getContent();

		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
		PaginationDto<ExceptionSummaryDto> dtoList = new PaginationDto<ExceptionSummaryDto>(employeeShiftList,
				page.getTotalPages(), page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(),
				page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;

	}
	
	// @Scheduled(cron = "0 0 0 * * ?")
	public void generateDailyAttendanceThroughScheduler() {
		try {
			
			SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
			
			Date date = calendarUtil.getConvertedDate(new Date(), -NumberConstants.ONE, NumberConstants.ZERO, NumberConstants.ZERO, NumberConstants.ZERO);

			List<Employee> employeeList = (List<Employee>) employeeRepository.findAllByIsDeletedFalse();
			List<DailyAttendance> dailyAttendanceList = new ArrayList<>();
			for (Employee employee : employeeList) {
				
				DailyAttendance dailyAttendance = new DailyAttendance();
				List<Transaction> eventReportMinList = transactionRepository.findByEmpIdMinCustom(employee.getEmpId(),
						format.format(date));
				List<Transaction> eventReportMaxList = transactionRepository.findByEmpIdMaxCustom(employee.getEmpId(),
						format.format(date));

				Iterator<Transaction> eventReportMinItr = eventReportMinList.iterator();
				Iterator<Transaction> eventReportMaxItr = eventReportMaxList.iterator();
				Transaction eventReportMin = null;
				Transaction eventReportMax = null;
				
				if (eventReportMinItr.hasNext() && eventReportMaxItr.hasNext()) {
					eventReportMin = eventReportMinItr.next();
				}
				
				//set employee details to dailyAttendance
				setEmployeeDetails(employee, dailyAttendance);

				//set absent, present to dailyAttendance and add into dailyAttendanceList 
				setDailyAttendanceDetails(date, dailyAttendanceList, employee, dailyAttendance, eventReportMinList,
						eventReportMaxList, eventReportMin, eventReportMax);
			}
			dailyAttendanceRepository.saveAll(dailyAttendanceList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setDailyAttendanceDetails(Date date, List<DailyAttendance> dailyAttendanceList, Employee employee,
			DailyAttendance dailyAttendance, List<Transaction> eventReportMinList, List<Transaction> eventReportMaxList,
			Transaction eventReportMin, Transaction eventReportMax) {
		
		SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
		dailyAttendance.setDate(date);
		dailyAttendance.setDateStr(format.format(date));
		
		if (eventReportMinList.isEmpty() && eventReportMaxList.isEmpty()) {
			// absent
			dailyAttendance.setAttendanceStatus(DailyAttendanceConstants.ABSENT);
			DailyAttendance dailyAttendanceTest = dailyAttendanceRepository.findByEmpIdAndDate(employee.getEmpId(), date);
			if (null == dailyAttendanceTest) {
				dailyAttendanceList.add(dailyAttendance);
			}
		} else {
			// present
			dailyAttendance.setEmpInLocation(eventReportMin.getDeviceName());
			dailyAttendance.setEmpInMask((eventReportMin.getWearingMask() == true) ? ApplicationConstants.TRUE : ApplicationConstants.FALSE);
			dailyAttendance.setEmpInTime(eventReportMin.getPunchTimeStr());
			dailyAttendance.setAttendanceStatus(DailyAttendanceConstants.PRESENT);
			if (!(eventReportMin.getPunchTimeStr().equalsIgnoreCase(eventReportMax.getPunchTimeStr()))) {
				dailyAttendance.setEmpOutLocation(eventReportMax.getDeviceName());
				dailyAttendance.setEmpOutMask((eventReportMin.getWearingMask() == true) ? ApplicationConstants.TRUE : ApplicationConstants.FALSE);
				dailyAttendance.setEmpOutTime(eventReportMax.getPunchTimeStr());

				LocalTime empIn = LocalTime.parse(eventReportMin.getPunchTimeStr());
				LocalTime empOut = LocalTime.parse(eventReportMax.getPunchTimeStr());
				Long workMin = empIn.until(empOut, ChronoUnit.MINUTES);
				Long workHr = empIn.until(empOut, ChronoUnit.HOURS);

				dailyAttendance.setWorkTime(String.valueOf(workHr) + ApplicationConstants.DELIMITER_COLON + String.valueOf(workMin % NumberConstants.SIXTY));
				dailyAttendance.setMissedOutPunch(ApplicationConstants.FALSE);
			} else
				dailyAttendance.setMissedOutPunch(ApplicationConstants.TRUE);

			DailyAttendance dailyAttendanceTest = dailyAttendanceRepository.findByEmpIdAndDate(employee.getEmpId(), date);
			if (null == dailyAttendanceTest) {
				dailyAttendanceList.add(dailyAttendance);
			}
		}
	}

	private void setEmployeeDetails(Employee employee, DailyAttendance dailyAttendance) {
		if (null != employee.getDepartment())
			dailyAttendance.setDepartment(employee.getDepartment().getName());
		if (null != employee.getOrganization())
			dailyAttendance.setOrganization(employee.getOrganization().getName());
		if (null != employee.getBranch())
			dailyAttendance.setBranch(employee.getBranch().getName());
		if (null != employee.getDesignation())
			dailyAttendance.setDesignation(employee.getDesignation().getName());
	
		dailyAttendance.setEmpId(employee.getEmpId());
		dailyAttendance.setEmployeeName(employee.getName());
	}
}