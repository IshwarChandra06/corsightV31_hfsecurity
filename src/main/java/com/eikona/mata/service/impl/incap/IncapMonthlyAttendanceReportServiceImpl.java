package com.eikona.mata.service.impl.incap;

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
import org.springframework.stereotype.Service;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.HeaderConstants;
import com.eikona.mata.constants.IncapConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.dto.IncapMonthlyAttendanceDto;
import com.eikona.mata.dto.MonthlyReportDto;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.DailyAttendance;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.repository.DailyAttendanceRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.util.CalendarUtil;

@Service
//@Qualifier("incapMonthlyReport")
public class IncapMonthlyAttendanceReportServiceImpl{
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private DailyAttendanceRepository dailyAttendanceRepository;
	
	@Autowired
	private CalendarUtil calendarUtil;
	
//	@Override
	public MonthlyReportDto<IncapMonthlyAttendanceDto> calculateMonthlyReport(String startDateStr) {
		
		MonthlyReportDto<IncapMonthlyAttendanceDto> monthlyDetailsReport = new MonthlyReportDto<>();
		
		if(startDateStr.isEmpty()) {
			startDateStr = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US).format(new Date());
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
			
			//endCalendar.add(Calendar.DATE, NumberConstants.ONE);
			List<Employee> workerList = (List<Employee>) employeeRepository.findAllByIsDeletedFalse();
			List<IncapMonthlyAttendanceDto> monthlyReportList = new ArrayList<>();
			for(Employee employee: workerList) {
				IncapMonthlyAttendanceDto monthlyDetailDto = calculateDaywiseMonthlyReport(startCalendar, endCalendar, employee);
				monthlyReportList.add(monthlyDetailDto);
				
			}
			
			monthlyDetailsReport.setHeadList(headList);
			monthlyDetailsReport.setDataList(monthlyReportList);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return monthlyDetailsReport;
	}


	private List<String> getHeadList(String month, int day, int last) {
		List<String> headList = new ArrayList<String>();
		headList.add(IncapConstants.USER_ID);
		headList.add(IncapConstants.NAME);
		headList.add(HeaderConstants.DEPARTMENT);
		headList.add(HeaderConstants.DESIGNATION);
		headList.add(HeaderConstants.ORGANIZATION);
		while(day <= last) {
			headList.add(day+ApplicationConstants.DELIMITER_EMPTY+month.substring(NumberConstants.ZERO, NumberConstants.THREE));
			day++;
		}
		
		headList.add(ApplicationConstants.PRESENT);
		headList.add(ApplicationConstants.ABSENT);
		headList.add(IncapConstants.WEEK_OFF);
		headList.add(IncapConstants.TOTAL_OVERTIME);
		headList.add(IncapConstants.TOTAL_LATE_IN);
		headList.add(IncapConstants.TOTAL_EARLY_OUT);
		headList.add(IncapConstants.PAYABLE_DAYS);
		headList.add(IncapConstants.TOTAL_DAYS);
		return headList;
	}
	
	 
	public IncapMonthlyAttendanceDto calculateDaywiseMonthlyReport(Date startDate, Date endDate, Employee employee){
		 
		Calendar calender = Calendar.getInstance();
		calender.setTime(endDate);
		int lastDayOfMonth = calender.get(Calendar.DATE);
		
		List<DailyAttendance> dailyReportList = dailyAttendanceRepository.findDetailsByDateCustom(employee.getEmpId(), startDate, endDate);
		
		IncapMonthlyAttendanceDto monthlyDailyReportDto = new IncapMonthlyAttendanceDto();
		
		//set employee details in monthly report
		setEmployeeDetailsInIncapMonthlyReport(employee, monthlyDailyReportDto);
			
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
		setIncapMonthlyReportDto(lastDayOfMonth, monthlyDailyReportDto, dailyReportListItr, dailyAttendance, first,
				last);
		
		return monthlyDailyReportDto;
		 
	 }


	private void setIncapMonthlyReportDto(int lastDayOfMonth, IncapMonthlyAttendanceDto monthlyDailyReportDto,
			Iterator<DailyAttendance> dailyReportListItr, DailyAttendance dailyAttendance, int first, int last) {
		float totalPresentCount = NumberConstants.FLOAT_ZERO;
		
		float totalAbsentCount = NumberConstants.FLOAT_ZERO;
		
		long totalOverTime = NumberConstants.LONG_ZERO;
		
		long totalLateComing = NumberConstants.LONG_ZERO;
		
		long totalEarlyGoing = NumberConstants.LONG_ZERO;
		
		long totalWeekOffCount = NumberConstants.LONG_ZERO;
		
		List<String> dataList = new ArrayList<String>();
		while(first <= last) {
			
			if(null != dailyAttendance) {
				if(IncapConstants.PR.equalsIgnoreCase(dailyAttendance.getFirstHalf())) 
					totalPresentCount += NumberConstants.FLOAT_POINT_FIVE;
				else if(IncapConstants.AB.equalsIgnoreCase(dailyAttendance.getFirstHalf()))
					totalAbsentCount +=NumberConstants.FLOAT_POINT_FIVE;
				
				if(IncapConstants.PR.equalsIgnoreCase(dailyAttendance.getSecondHalf())) 
					totalPresentCount += NumberConstants.FLOAT_POINT_FIVE;
				else if(IncapConstants.AB.equalsIgnoreCase(dailyAttendance.getSecondHalf()))
					totalAbsentCount += NumberConstants.FLOAT_POINT_FIVE;
				
				if(null!=dailyAttendance.getOverTime())
					totalOverTime+=dailyAttendance.getOverTime();
				
				if(null!=dailyAttendance.getEarlyGoing())
					totalEarlyGoing+=dailyAttendance.getEarlyGoing();
				
				if(null!=dailyAttendance.getLateComing())
					totalLateComing+=dailyAttendance.getLateComing();
				
				if(IncapConstants.WEEK_OFF.equalsIgnoreCase(dailyAttendance.getAsPerRoster()))
					totalWeekOffCount+= NumberConstants.ONE;
				
//				Date date = dailyAttendance.getDate();
//				Calendar dateCalendar = Calendar.getInstance(); 
//				dateCalendar.setTime(date);
//				int day = dateCalendar.get(Calendar.DATE);
				
				if(IncapConstants.AB.equalsIgnoreCase(dailyAttendance.getFirstHalf()) && IncapConstants.AB.equalsIgnoreCase(dailyAttendance.getSecondHalf()))
					dataList.add(IncapConstants.AB);
				else if(IncapConstants.PR.equalsIgnoreCase(dailyAttendance.getFirstHalf()) && IncapConstants.PR.equalsIgnoreCase(dailyAttendance.getSecondHalf()))
					dataList.add(IncapConstants.PR);
				else {
					if(null == dailyAttendance.getFirstHalf() && null == dailyAttendance.getSecondHalf())
						dataList.add(ApplicationConstants.DELIMITER_HYPHEN);
					else if(null == dailyAttendance.getFirstHalf())
						dataList.add(ApplicationConstants.DELIMITER_HYPHEN+ApplicationConstants.DELIMITER_FORMAT_SPACE+dailyAttendance.getSecondHalf());
					else if(null == dailyAttendance.getSecondHalf())
						dataList.add(dailyAttendance.getFirstHalf()+ApplicationConstants.DELIMITER_FORMAT_SPACE+ApplicationConstants.DELIMITER_HYPHEN);
					else
						dataList.add(dailyAttendance.getFirstHalf()+ApplicationConstants.DELIMITER_FORWARD_SLASH+dailyAttendance.getSecondHalf());
				}
					
				dailyAttendance=null;
			} else{
				dataList.add(ApplicationConstants.DELIMITER_HYPHEN);
			}
			
			if (dailyReportListItr.hasNext()) {
				dailyAttendance = dailyReportListItr.next();
			}
			first++;
		}
		
//		float s = totalPresentCount;
		
		monthlyDailyReportDto.setTotalPresentCount(String.valueOf(totalPresentCount));
		monthlyDailyReportDto.setTotalAbsentCount(String.valueOf(totalAbsentCount));
		monthlyDailyReportDto.setTotalWeekOff(String.valueOf(totalWeekOffCount));
		monthlyDailyReportDto.setTotalOverTime(String.valueOf(totalOverTime));
		monthlyDailyReportDto.setTotalLateIn(String.valueOf(totalLateComing));
		monthlyDailyReportDto.setTotalEarlyOut(String.valueOf(totalEarlyGoing));
		
		monthlyDailyReportDto.setTotalDays(String.valueOf(lastDayOfMonth));
		monthlyDailyReportDto.setPayableDays(String.valueOf(totalWeekOffCount+totalPresentCount));
		
		monthlyDailyReportDto.setDateList(dataList);
	}


	private void setEmployeeDetailsInIncapMonthlyReport(Employee employee,
			IncapMonthlyAttendanceDto monthlyDailyReportDto) {
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
			if(null != employee.getOrganization())
				monthlyDailyReportDto.setOrganization(employee.getOrganization().getName());
			else
				monthlyDailyReportDto.setOrganization(ApplicationConstants.DELIMITER_EMPTY);
		}else {
			monthlyDailyReportDto.setEmpId(ApplicationConstants.DELIMITER_EMPTY);
			monthlyDailyReportDto.setEmpName(ApplicationConstants.DELIMITER_EMPTY);
			monthlyDailyReportDto.setOrganization(ApplicationConstants.DELIMITER_EMPTY);
			monthlyDailyReportDto.setDepartment(ApplicationConstants.DELIMITER_EMPTY);
			monthlyDailyReportDto.setDesignation(ApplicationConstants.DELIMITER_EMPTY);
		}
	}
	 
	 public void excelGenerator(HttpServletResponse response, MonthlyReportDto<IncapMonthlyAttendanceDto> monthlyDetailsList)
				throws ParseException, IOException {

		DateFormat dateFormat = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_INDIA_SPLIT_BY_UNDERSCORE);
		String currentDateTime = dateFormat.format(new Date());
		String filename = IncapConstants.EMPLOYEE_ATTENDANCE + currentDateTime + ApplicationConstants.EXTENSION_EXCEL;
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

		List<IncapMonthlyAttendanceDto> incapMonthlyDetailDtoList = monthlyDetailsList.getDataList();
		//set excel data for incap monthly report
		setExcelDataForIncapMonthlyReport(sheet, rowCount, cellStyle, incapMonthlyDetailDtoList);

		FileOutputStream fileOut = new FileOutputStream(filename);
		workBook.write(fileOut);
		ServletOutputStream outputStream = response.getOutputStream();
		workBook.write(outputStream);
		fileOut.close();
		workBook.close();

	}


	private void setExcelDataForIncapMonthlyReport(Sheet sheet, int rowCount, CellStyle cellStyle,
			List<IncapMonthlyAttendanceDto> incapMonthlyDetailDtoList) {
		
		for (IncapMonthlyAttendanceDto monthlyDetail : incapMonthlyDetailDtoList) {

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
			cell.setCellValue(monthlyDetail.getOrganization());
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
			cell.setCellValue(monthlyDetail.getTotalAbsentCount());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(monthlyDetail.getTotalWeekOff());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(monthlyDetail.getTotalOverTime());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(monthlyDetail.getTotalLateIn());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(monthlyDetail.getTotalEarlyOut());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(monthlyDetail.getPayableDays());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(monthlyDetail.getTotalDays());
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

	public PaginationDto<MonthlyReportDto<IncapMonthlyAttendanceDto>> search(String dateStr, int pageno, String sortField,
			String sortDir) {

		PaginationDto<MonthlyReportDto<IncapMonthlyAttendanceDto>> dtoList = new PaginationDto<>();
		MonthlyReportDto<IncapMonthlyAttendanceDto> monthlyDetailsReport = new MonthlyReportDto<>();
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
		if (!dateStr.isEmpty()) {
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
				
		    	Page<Employee> page = employeeRepository.findAll(pageable);

				setMonthlyAttendanceDtoList(monthlyDetailsReport, headList, startCalendar, endCalendar, page);

				sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
				List<MonthlyReportDto<IncapMonthlyAttendanceDto>> monthlyDetailsReportList = new ArrayList<MonthlyReportDto<IncapMonthlyAttendanceDto>>();
				monthlyDetailsReportList.add(monthlyDetailsReport);
				dtoList = new PaginationDto<>(monthlyDetailsReportList, page.getTotalPages(), page.getNumber() + NumberConstants.ONE,
						page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return dtoList;
	}


	private void setMonthlyAttendanceDtoList(MonthlyReportDto<IncapMonthlyAttendanceDto> monthlyDetailsReport,
			List<String> headList, Date startCalendar, Date endCalendar, Page<Employee> page) {
		List<Employee> workerList = page.getContent();
		List<IncapMonthlyAttendanceDto> monthlyReportList = new ArrayList<>();
		for (Employee employee : workerList) {
			IncapMonthlyAttendanceDto monthlyDetailDto = calculateDaywiseMonthlyReport(startCalendar, endCalendar, employee);
			monthlyReportList.add(monthlyDetailDto);
		}

		monthlyDetailsReport.setHeadList(headList);
		monthlyDetailsReport.setDataList(monthlyReportList);
	}
}
