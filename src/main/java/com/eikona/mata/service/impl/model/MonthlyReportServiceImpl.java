package com.eikona.mata.service.impl.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.EmployeeConstants;
import com.eikona.mata.constants.HeaderConstants;
import com.eikona.mata.constants.MonthlyAttendanceConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.dto.MonthlyAttendanceDto;
import com.eikona.mata.dto.MonthlyReportDto;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.DailyAttendance;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.repository.DailyAttendanceRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.service.MonthlyReportService;
import com.eikona.mata.util.CalendarUtil;
import com.eikona.mata.util.GeneralSpecificationUtil;

@Service
public class MonthlyReportServiceImpl implements MonthlyReportService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private DailyAttendanceRepository dailyAttendanceRepository;

	@Autowired
	private GeneralSpecificationUtil<Employee> generalSpecification;

	@Autowired
	private CalendarUtil calendarUtil;

	

	@Override
	public PaginationDto<MonthlyReportDto<MonthlyAttendanceDto>> searchByField(String dateStr,
			String employeeId, String employeeName, String department, String designation, int pageno, String sortField,
			String sortDir) {

		PaginationDto<MonthlyReportDto<MonthlyAttendanceDto>> dtoList = new PaginationDto<>();
		MonthlyReportDto<MonthlyAttendanceDto> monthlyDetailsReport = new MonthlyReportDto<>();
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
		if(dateStr.isEmpty()) {
			dateStr = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US).format(new Date());
		}
		else {
			dateStr=dateStr+"-01";
		}
			try {

				date = format.parse(dateStr);
				Calendar dateCalendar = Calendar.getInstance(); 
				dateCalendar.setTime(date);
				String month = dateCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH );
				
				int first = dateCalendar.getActualMinimum(Calendar.DATE), day = dateCalendar.getActualMinimum(Calendar.DATE);
				int last = dateCalendar.getActualMaximum(Calendar.DATE);
				
				List<String> headList = getHeadList(month, day, last);

				Date startCalendar = calendarUtil.getConvertedDate(date, first, NumberConstants.ZERO, NumberConstants.ZERO, NumberConstants.ZERO);
				
				Date endCalendar = calendarUtil.getConvertedDate(date,last, NumberConstants.TWENTY_THREE, NumberConstants.FIFTY_NINE, NumberConstants.FIFTY_NINE);
				
				if (null == sortDir || sortDir.isEmpty()) {
					sortDir = ApplicationConstants.ASC;
				}
				if (null == sortField || sortField.isEmpty()) {
					sortField = ApplicationConstants.ID;
				}
				Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
						: Sort.by(sortField).descending();

				Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);
				
		    	
				Specification<Employee> isDeletedSpec = generalSpecification.isDeletedSpecification();
				Specification<Employee> empIdSpec = generalSpecification.stringSpecification(employeeId,
						EmployeeConstants.EMPID);
				Specification<Employee> empNameSpec = generalSpecification.stringSpecification(employeeName,
						ApplicationConstants.NAME);
				Specification<Employee> deptSpec = generalSpecification.foreignKeyStringSpecification(department,
						EmployeeConstants.DEPARTMENT, ApplicationConstants.NAME);
				Specification<Employee> degSpec = generalSpecification.foreignKeyStringSpecification(designation,
						EmployeeConstants.DESIGNATION, ApplicationConstants.NAME);

				Page<Employee> page = employeeRepository.findAll(isDeletedSpec.and(empIdSpec).and(empNameSpec).and(deptSpec).and(degSpec), pageable);
				setMonthlyAttendanceDtoList(monthlyDetailsReport, headList, startCalendar, endCalendar, page);

				sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
				List<MonthlyReportDto<MonthlyAttendanceDto>> monthlyDetailsReportList = new ArrayList<MonthlyReportDto<MonthlyAttendanceDto>>();
				monthlyDetailsReportList.add(monthlyDetailsReport);
				dtoList = new PaginationDto<>(monthlyDetailsReportList, page.getTotalPages(), page.getNumber() + NumberConstants.ONE,
						page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);

			} catch (ParseException e) {
				e.printStackTrace();
			}
		return dtoList;
	}
	private List<String> getHeadList(String month, int day, int last) {
		List<String> headList = new ArrayList<String>();
		headList.add(HeaderConstants.EMPLOYEE_ID);
		headList.add(HeaderConstants.NAME);
		headList.add(HeaderConstants.DEPARTMENT);
		headList.add(HeaderConstants.DESIGNATION);
		headList.add(HeaderConstants.MOBILE);
		while(day <= last) {
			headList.add(day+ApplicationConstants.DELIMITER_SPACE+month.substring(NumberConstants.ZERO, NumberConstants.THREE));
			day++;
		}
		headList.add(ApplicationConstants.TOTAL_PRESENT);
		headList.add(ApplicationConstants.TOTAL_OVERTIME);
		return headList;
	}

	private void setMonthlyAttendanceDtoList(MonthlyReportDto<MonthlyAttendanceDto> monthlyDetailsReport,
			List<String> headList, Date startCalendar, Date endCalendar, Page<Employee> page) {
		List<Employee> workerList = page.getContent();
		List<MonthlyAttendanceDto> monthlyReportList = new ArrayList<>();
		for (Employee employee : workerList) {
			MonthlyAttendanceDto monthlyDetailDto = calculateDaywiseMonthlyReport(startCalendar, endCalendar, employee);
			monthlyReportList.add(monthlyDetailDto);
		}

		monthlyDetailsReport.setHeadList(headList);
		monthlyDetailsReport.setDataList(monthlyReportList);
	}
	public MonthlyAttendanceDto calculateDaywiseMonthlyReport(Date startDate, Date endDate, Employee employee){
		 
		Calendar calender = Calendar.getInstance();
		calender.setTime(endDate);
		int lastDayOfMonth = calender.get(Calendar.DATE);
		
		List<DailyAttendance> dailyReportList = dailyAttendanceRepository.findDetailsByDateCustom(employee.getEmpId(), startDate, endDate);
		
		MonthlyAttendanceDto monthlyDailyReportDto = new MonthlyAttendanceDto();
		
		//set employee details in monthly report
		setEmployeeDetailsInMonthlyReport(employee, monthlyDailyReportDto);
			
		Iterator<DailyAttendance> dailyReportListItr = dailyReportList.iterator();
		DailyAttendance dailyAttendance = null;
		if (dailyReportListItr.hasNext()) {
			dailyAttendance = dailyReportListItr.next();
		}
		
		Calendar startDayCalendar = Calendar.getInstance(); 
		startDayCalendar.setTime(startDate);
		int first = startDayCalendar.getActualMinimum(Calendar.DATE);
		
		Calendar endDayCalendar = Calendar.getInstance(); 
		endDayCalendar.setTime(endDate);
		int last = endDayCalendar.getActualMaximum(Calendar.DATE);
		
		//set monthly report data
		setMonthlyReportDto(lastDayOfMonth, monthlyDailyReportDto, dailyReportListItr, dailyAttendance, first,
				last);
		
		return monthlyDailyReportDto;
		 
	 }


	private void setMonthlyReportDto(int lastDayOfMonth, MonthlyAttendanceDto monthlyDailyReportDto,
			Iterator<DailyAttendance> dailyReportListItr, DailyAttendance dailyAttendance, int first, int last) {
		Integer totalPresentCount = NumberConstants.ZERO;
		
		Integer totalAbsentCount = NumberConstants.ZERO;
		
		long totalOverTime = NumberConstants.LONG_ZERO;
		
		List<String> dataList = new ArrayList<String>();
		while(first <= last) {
			if(null != dailyAttendance) {
				String[] dateArray=dailyAttendance.getDateStr().split("-");
				String date=dateArray[2];
				if(Integer.valueOf(date)==first) {
					if(MonthlyAttendanceConstants.PRESENT.equalsIgnoreCase(dailyAttendance.getAttendanceStatus())) 
						totalPresentCount += NumberConstants.ONE;
					else if(MonthlyAttendanceConstants.ABSENT.equalsIgnoreCase(dailyAttendance.getAttendanceStatus()))
						totalAbsentCount += NumberConstants.ONE;
					
					if(null!=dailyAttendance.getOverTime())
						totalOverTime+=dailyAttendance.getOverTime();

					if(MonthlyAttendanceConstants.ABSENT.equalsIgnoreCase(dailyAttendance.getAttendanceStatus()))
						dataList.add("A");
					else if(MonthlyAttendanceConstants.PRESENT.equalsIgnoreCase(dailyAttendance.getAttendanceStatus()))
						dataList.add("P");
					else if(null == dailyAttendance.getAttendanceStatus() )
							dataList.add(ApplicationConstants.DELIMITER_HYPHEN);
					
					if (dailyReportListItr.hasNext()) {
						dailyAttendance = dailyReportListItr.next();
					}
				}
					else 
						dataList.add(ApplicationConstants.DELIMITER_HYPHEN);
				}
			 else{
				dataList.add(ApplicationConstants.DELIMITER_HYPHEN);
			}
			
			first++;
		}
		
		monthlyDailyReportDto.setTotalPresentCount(String.valueOf(totalPresentCount));
		monthlyDailyReportDto.setTotalAbsentCount(String.valueOf(lastDayOfMonth-totalPresentCount));
		monthlyDailyReportDto.setTotalOverTime(String.valueOf(totalOverTime/60));
		
		monthlyDailyReportDto.setTotalDays(String.valueOf(lastDayOfMonth));
		
		monthlyDailyReportDto.setDateList(dataList);
	}


	private void setEmployeeDetailsInMonthlyReport(Employee employee,MonthlyAttendanceDto monthlyDailyReportDto) {
		if(null != employee) {
			
			if(null != employee.getEmpId())
				monthlyDailyReportDto.setEmpId(employee.getEmpId());
			else
				monthlyDailyReportDto.setEmpId(ApplicationConstants.DELIMITER_EMPTY);
			monthlyDailyReportDto.setEmpName(employee.getName());
			if(null != employee.getDepartment())
				monthlyDailyReportDto.setDepartment(employee.getDepartment().getName());
			else
				monthlyDailyReportDto.setDepartment(ApplicationConstants.DELIMITER_EMPTY);
			if(null != employee.getDesignation())
				monthlyDailyReportDto.setDesignation(employee.getDesignation().getName());
			else
				monthlyDailyReportDto.setDesignation(ApplicationConstants.DELIMITER_EMPTY);
		   monthlyDailyReportDto.setMobile(employee.getMobile());
			
		}else {
			monthlyDailyReportDto.setEmpId(ApplicationConstants.DELIMITER_EMPTY);
			monthlyDailyReportDto.setEmpName(ApplicationConstants.DELIMITER_EMPTY);
			monthlyDailyReportDto.setMobile(ApplicationConstants.DELIMITER_EMPTY);
			monthlyDailyReportDto.setDepartment(ApplicationConstants.DELIMITER_EMPTY);
			monthlyDailyReportDto.setDesignation(ApplicationConstants.DELIMITER_EMPTY);
		}
	}
	 
	 public void excelGenerator(HttpServletResponse response, MonthlyReportDto<MonthlyAttendanceDto> monthlyDetailsList) throws IOException {

		DateFormat dateFormat = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_INDIA_SPLIT_BY_UNDERSCORE);
		String currentDateTime = dateFormat.format(new Date());
		String filename = MonthlyAttendanceConstants.MONTHLY_ATTENDANCE + currentDateTime + ApplicationConstants.EXTENSION_EXCEL;
		Workbook workBook = new XSSFWorkbook();
		Sheet sheet = workBook.createSheet();

		int rowCount = NumberConstants.ZERO;
		Row row = sheet.createRow(rowCount++);

		Font font = workBook.createFont();
		font.setBold(true);

		//set border style for header data
		CellStyle cellStyle = setExcelBorderStyle(workBook, BorderStyle.THICK, font);

		int index=NumberConstants.ZERO;
		Cell cell = row.createCell(NumberConstants.ZERO);
		List<String> headList = monthlyDetailsList.getHeadList();
		for(String head : headList) {
			cell = row.createCell(index++);
			cell.setCellValue(head);
			cell.setCellStyle(cellStyle);
		}
		
		
		font = workBook.createFont();
		font.setBold(false);

		//set border style for body data
		cellStyle = setExcelBorderStyle(workBook, BorderStyle.THIN, font);

		List<MonthlyAttendanceDto> incapMonthlyDetailDtoList = monthlyDetailsList.getDataList();
		//set excel data for incap monthly report
		setExcelDataForMonthlyReport(sheet, rowCount, cellStyle, incapMonthlyDetailDtoList);

		FileOutputStream fileOut = new FileOutputStream(filename);
		workBook.write(fileOut);
		ServletOutputStream outputStream = response.getOutputStream();
		workBook.write(outputStream);
		fileOut.close();
		workBook.close();

	}


	private void setExcelDataForMonthlyReport(Sheet sheet, int rowCount, CellStyle cellStyle,
			List<MonthlyAttendanceDto> monthlyDetailDtoList) {
		
		for (MonthlyAttendanceDto monthlyDetail : monthlyDetailDtoList) {
			if(rowCount==90000)
				break;
			Row row = sheet.createRow(rowCount++);

			int columnCount = NumberConstants.ZERO;

			Cell cell = row.createCell(columnCount++);
			cell.setCellValue(monthlyDetail.getEmpId());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(monthlyDetail.getEmpName());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(monthlyDetail.getDepartment());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(monthlyDetail.getDesignation());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(monthlyDetail.getMobile());
			cell.setCellStyle(cellStyle);
			
			//month
			for(String data: monthlyDetail.getDateList()) {
				cell = row.createCell(columnCount++);
				cell.setCellValue(data);
				cell.setCellStyle(cellStyle);
			}

			cell = row.createCell(columnCount++);
			cell.setCellValue(monthlyDetail.getTotalPresentCount());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(monthlyDetail.getTotalOverTime());
			cell.setCellStyle(cellStyle);
			
		
		}
	}


	private CellStyle setExcelBorderStyle(Workbook workBook, BorderStyle borderStyle, Font font) {
		CellStyle cellStyle = workBook.createCellStyle();
		cellStyle.setBorderTop(borderStyle);
		cellStyle.setBorderBottom(borderStyle);
		cellStyle.setBorderLeft(borderStyle);
		cellStyle.setBorderRight(borderStyle);
		cellStyle.setFont(font);
		return cellStyle;
	}
	@Override
	public MonthlyReportDto<MonthlyAttendanceDto> calculateMonthlyReport(String startDateStr, String employeeId,
			String employeeName, String department, String designation) {

		
		MonthlyReportDto<MonthlyAttendanceDto> monthlyDetailsReport = new MonthlyReportDto<>();
		
		if(startDateStr.isEmpty()) {
			startDateStr = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US).format(new Date());
		}
		else {
			startDateStr=startDateStr+"-01";
		}
		try {

			Date date = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US).parse(startDateStr);
			Calendar dateCalendar = Calendar.getInstance(); 
			dateCalendar.setTime(date);
			String month = dateCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH );
			
			int first = dateCalendar.getActualMinimum(Calendar.DATE), day = dateCalendar.getActualMinimum(Calendar.DATE);
			int last = dateCalendar.getActualMaximum(Calendar.DATE);
			List<String> headList = getHeadList(month, day, last);
			
			Date startCalendar = calendarUtil.getConvertedDate(date, first, NumberConstants.ZERO, NumberConstants.ZERO, NumberConstants.ZERO);
			
			Date endCalendar = calendarUtil.getConvertedDate(date,last, NumberConstants.TWENTY_THREE, NumberConstants.FIFTY_NINE, NumberConstants.FIFTY_NINE);
			
			Specification<Employee> isDeletedSpec = generalSpecification.isDeletedSpecification();
			Specification<Employee> empIdSpec = generalSpecification.stringSpecification(employeeId,
					EmployeeConstants.EMPID);
			Specification<Employee> empNameSpec = generalSpecification.stringSpecification(employeeName,
					ApplicationConstants.NAME);
			Specification<Employee> deptSpec = generalSpecification.foreignKeyStringSpecification(department,
					EmployeeConstants.DEPARTMENT, ApplicationConstants.NAME);
			Specification<Employee> degSpec = generalSpecification.foreignKeyStringSpecification(designation,
					EmployeeConstants.DESIGNATION, ApplicationConstants.NAME);

			List<Employee> workerList = employeeRepository.findAll(isDeletedSpec.and(empIdSpec).and(empNameSpec).and(deptSpec).and(degSpec));
			
			List<MonthlyAttendanceDto> monthlyReportList = new ArrayList<>();
			for(Employee employee: workerList) {
				MonthlyAttendanceDto monthlyDetailDto = calculateDaywiseMonthlyReport(startCalendar, endCalendar, employee);
				monthlyReportList.add(monthlyDetailDto);
				
			}
			
			monthlyDetailsReport.setHeadList(headList);
			monthlyDetailsReport.setDataList(monthlyReportList);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return monthlyDetailsReport;
	
	}

	
}
