package com.eikona.mata.service.impl.incap;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.HeaderConstants;
import com.eikona.mata.constants.IncapConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.dto.IncapMonthlyPunchReportDto;
import com.eikona.mata.entity.DailyAttendance;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.repository.DailyAttendanceRepository;
import com.eikona.mata.repository.EmployeeRepository;
import com.eikona.mata.util.CalendarUtil;

@Service
public class IncapMonthlyPunchReportServiceImpl {

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private DailyAttendanceRepository dailyAttendanceRepository;
	
	@Autowired
	private CalendarUtil calendarUtil;

	public List<IncapMonthlyPunchReportDto> calculateMonthlyReport(String startDateStr) {
		
		List<IncapMonthlyPunchReportDto> IncapMonthlyDetailDtoList = new ArrayList<>();
		try {
			String[] arry=startDateStr.split(ApplicationConstants.DELIMITER_FORWARD_SLASH);
			String str1=arry[NumberConstants.ONE];
			String str2=arry[NumberConstants.TWO];
			
			startDateStr=str1+ApplicationConstants.DELIMITER_FORWARD_SLASH+str2;
			Date date1 = new SimpleDateFormat(ApplicationConstants.MONTH_YEAR_SPLIT_BY_SLASH).parse(startDateStr);
			String dateformat = new SimpleDateFormat(ApplicationConstants.YEAR_MONTH_SPLIT_BY_HYPHEN).format(date1);
			Date date = new SimpleDateFormat(ApplicationConstants.YEAR_MONTH_SPLIT_BY_HYPHEN).parse(dateformat);
			Calendar dateCalendar = Calendar.getInstance(); 
			dateCalendar.setTime(date);
			
			int first = dateCalendar.getActualMinimum(Calendar.DATE);
			int last = dateCalendar.getActualMaximum(Calendar.DATE);
			
			Date startCalendar = calendarUtil.getConvertedDate(dateCalendar.getTime(), first, NumberConstants.ZERO, NumberConstants.ZERO, NumberConstants.ZERO);
			
			Date endCalendar = calendarUtil.getConvertedDate(dateCalendar.getTime(), last, NumberConstants.TWENTY_THREE, NumberConstants.FIFTY_NINE, NumberConstants.FIFTY_NINE);
			
			List<Employee> workerList = (List<Employee>) employeeRepository.findAllByIsDeletedFalse();
			for(Employee employee: workerList) {
					IncapMonthlyPunchReportDto monthlyDetailDto = calculateDaywiseMonthlySummaryReport(startCalendar, endCalendar, last, employee);
					IncapMonthlyDetailDtoList.add(monthlyDetailDto);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return IncapMonthlyDetailDtoList;
	}

	
	private IncapMonthlyPunchReportDto calculateDaywiseMonthlySummaryReport(Date startDate, Date endDate, int last, Employee employee) {
		
		int day= NumberConstants.ONE;
		List<String> dateList = new ArrayList<String>();
		while(day <= last) {
			
			dateList.add(String.valueOf(day));
			day++;
		}
		
		List<String> shiftListValue = new ArrayList<>();
		double totalPresentCount = NumberConstants.ZERO;
		double totalAbsentCount = NumberConstants.ZERO;
		Long totalWorkHourCount = NumberConstants.LONG_ZERO;
		
		List<DailyAttendance> dailyReportList = dailyAttendanceRepository.findDetailsByDateCustom(employee.getEmpId(), startDate, endDate);
		
		Iterator<DailyAttendance> dailyReportListItr = dailyReportList.iterator();
		DailyAttendance dailyAttendance = null;
		if (dailyReportListItr.hasNext()) {
			dailyAttendance = dailyReportListItr.next();
		}
		
		IncapMonthlyPunchReportDto IncapMonthlyPunchReportDto = new IncapMonthlyPunchReportDto();
		IncapMonthlyPunchReportDto.setDateList(dateList);
		IncapMonthlyPunchReportDto.setEmpId(employee.getEmpId());
		IncapMonthlyPunchReportDto.setEmpName(employee.getName());
		if(null != employee.getDepartment())
			IncapMonthlyPunchReportDto.setDepartment(employee.getDepartment().getName());
		else
			IncapMonthlyPunchReportDto.setDepartment(ApplicationConstants.DELIMITER_EMPTY);
		if(null != employee.getDesignation())
			IncapMonthlyPunchReportDto.setDesignation(employee.getDesignation().getName());
		else
			IncapMonthlyPunchReportDto.setDesignation(ApplicationConstants.DELIMITER_EMPTY);
		
		Map<String, List<String>> dataList = new LinkedHashMap<String, List<String>>();
		List<String> shiftList = new ArrayList<String>();
		List<String> inList = new ArrayList<String>();
		List<String> outList = new ArrayList<String>();
		List<String> brkStartList = new ArrayList<String>();
		List<String> brkEndList = new ArrayList<String>();
		List<String> lateInList = new ArrayList<String>();
		List<String> earlyOutList = new ArrayList<String>();
		List<String> wrkHrsList = new ArrayList<String>();
		List<String> overTimeList = new ArrayList<String>();
		List<String> stat1List = new ArrayList<String>();
		List<String> stat2List = new ArrayList<String>();
		
		
		day=NumberConstants.ONE;
		while(day <= last) {
			if(null != dailyAttendance) {
				
				Date date = dailyAttendance.getDate();
				Calendar dateCalendar = Calendar.getInstance(); 
				dateCalendar.setTime(date);
				int currentDay = dateCalendar.get(Calendar.DATE);
				
				if(day == currentDay) {
					shiftList.add((null !=dailyAttendance.getShift())?dailyAttendance.getShift():ApplicationConstants.DELIMITER_HYPHEN);
					inList.add((null !=dailyAttendance.getEmpInTime())?dailyAttendance.getEmpInTime():ApplicationConstants.DELIMITER_HYPHEN);
					outList.add((null !=dailyAttendance.getEmpOutTime())?dailyAttendance.getEmpOutTime():ApplicationConstants.DELIMITER_HYPHEN);
					brkStartList.add(ApplicationConstants.DELIMITER_HYPHEN);
					brkEndList.add(ApplicationConstants.DELIMITER_HYPHEN);
					lateInList.add((null != dailyAttendance.getLateComing())?String.valueOf(dailyAttendance.getLateComing()):ApplicationConstants.DELIMITER_HYPHEN);
					earlyOutList.add((null != dailyAttendance.getEarlyGoing())?String.valueOf(dailyAttendance.getEarlyGoing()):ApplicationConstants.DELIMITER_HYPHEN);
					wrkHrsList.add((null !=dailyAttendance.getWorkTime())?dailyAttendance.getWorkTime():ApplicationConstants.DELIMITER_HYPHEN);
					overTimeList.add((null != dailyAttendance.getOverTime())?String.valueOf(dailyAttendance.getOverTime()):ApplicationConstants.DELIMITER_HYPHEN);
					stat1List.add(ApplicationConstants.DELIMITER_HYPHEN);
					stat2List.add(ApplicationConstants.DELIMITER_HYPHEN);
					if (dailyReportListItr.hasNext()) {
						dailyAttendance = dailyReportListItr.next();
					}
					
					if(IncapConstants.PR.equalsIgnoreCase(dailyAttendance.getFirstHalf())) 
						totalPresentCount +=NumberConstants.FLOAT_POINT_FIVE;
					else if(IncapConstants.AB.equalsIgnoreCase(dailyAttendance.getFirstHalf()))
						totalAbsentCount +=NumberConstants.FLOAT_POINT_FIVE;
					
					if(IncapConstants.AB.equalsIgnoreCase(dailyAttendance.getSecondHalf())) 
						totalAbsentCount+=NumberConstants.FLOAT_POINT_FIVE;
					else if(IncapConstants.PR.equalsIgnoreCase(dailyAttendance.getSecondHalf()))
						totalPresentCount +=NumberConstants.FLOAT_POINT_FIVE;
				}else {
					shiftList.add(ApplicationConstants.DELIMITER_HYPHEN);
					inList.add(ApplicationConstants.DELIMITER_HYPHEN);
					outList.add(ApplicationConstants.DELIMITER_HYPHEN);
					brkStartList.add(ApplicationConstants.DELIMITER_HYPHEN);
					brkEndList.add(ApplicationConstants.DELIMITER_HYPHEN);
					lateInList.add(ApplicationConstants.DELIMITER_HYPHEN);
					earlyOutList.add(ApplicationConstants.DELIMITER_HYPHEN);
					wrkHrsList.add(ApplicationConstants.DELIMITER_HYPHEN);
					overTimeList.add(ApplicationConstants.DELIMITER_HYPHEN);
					stat1List.add(ApplicationConstants.DELIMITER_HYPHEN);
					stat2List.add(ApplicationConstants.DELIMITER_HYPHEN);
				}
			}
			day++;
		}
		
		dataList.put(IncapConstants.SHIFT, shiftList);
		dataList.put(IncapConstants.IN, inList);
		dataList.put(IncapConstants.OUT, outList);
		dataList.put(IncapConstants.BRK_START, brkStartList);
		dataList.put(IncapConstants.BRK_END, brkEndList);
		dataList.put(IncapConstants.LATE_IN, lateInList);
		dataList.put(IncapConstants.EARLY_OUT, earlyOutList);
		dataList.put(IncapConstants.WRK_HRS, wrkHrsList);
		dataList.put(IncapConstants.OVERTIME, overTimeList);
		dataList.put(IncapConstants.STAT_ONE, stat1List);
		dataList.put(IncapConstants.STAT_TWO, stat2List);
		
		shiftListValue.add(String.valueOf(totalPresentCount));
		shiftListValue.add(String.valueOf(NumberConstants.ZERO));
		shiftListValue.add(String.valueOf(NumberConstants.ZERO));
		shiftListValue.add(String.valueOf(NumberConstants.ZERO));
		shiftListValue.add(String.valueOf(NumberConstants.ZERO));
		shiftListValue.add(String.valueOf(totalAbsentCount));
		shiftListValue.add(String.valueOf(NumberConstants.ZERO));
		shiftListValue.add(String.valueOf(NumberConstants.ZERO));
		shiftListValue.add(String.valueOf(NumberConstants.ZERO));
		shiftListValue.add(String.valueOf(NumberConstants.ZERO));
		shiftListValue.add(String.valueOf(NumberConstants.ZERO));
		shiftListValue.add(String.valueOf(NumberConstants.ZERO));
		shiftListValue.add(String.valueOf(totalWorkHourCount));
		
		
		IncapMonthlyPunchReportDto.setDataMapList(dataList);
		IncapMonthlyPunchReportDto.setShiftListValue(shiftListValue);
		
		return IncapMonthlyPunchReportDto;
	}

	public void excelGenerator(HttpServletResponse response, List<IncapMonthlyPunchReportDto> incapMonthlySummaryReportList, String sDate)
			throws ParseException, IOException {
		
		String monthNo = sDate.split(ApplicationConstants.DELIMITER_FORWARD_SLASH)[NumberConstants.ONE];
		String year = sDate.split(ApplicationConstants.DELIMITER_FORWARD_SLASH)[2];
		String month = Month.of(Integer.valueOf(monthNo)).name();
		
		DateFormat dateFormat = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_INDIA_SPLIT_BY_UNDERSCORE);
		String currentDateTime = dateFormat.format(new Date());
		String filename = IncapConstants.INCAP_MONTHLY_SUMMARY_REPORT + currentDateTime + ApplicationConstants.EXTENSION_EXCEL;
		Workbook workBook = new XSSFWorkbook();
		Sheet sheet = workBook.createSheet();

		sheet.addMergedRegion(new CellRangeAddress(NumberConstants.ONE, NumberConstants.ONE, NumberConstants.SIX, NumberConstants.ELEVEN));
		
		sheet.setDisplayGridlines(false);
		Font font = workBook.createFont();
		font.setBold(true);

		CellStyle cellStyleBold = workBook.createCellStyle();
		cellStyleBold.setAlignment(HorizontalAlignment.CENTER);
		cellStyleBold.setFont(font);
		
		CellStyle cellStyleThin = workBook.createCellStyle();
		cellStyleThin.setAlignment(HorizontalAlignment.CENTER);
		
		int rowCount = NumberConstants.ZERO;
		Row row = sheet.createRow(rowCount++);
		
		Cell cell = row.createCell(NumberConstants.EIGHT);
		cell.setCellValue(HeaderConstants.ORGANIZATION+ApplicationConstants.DELIMITER_HYPHEN+NumberConstants.ONE);
		cell.setCellStyle(cellStyleBold);
		
		row = sheet.createRow(rowCount++);
		cell = row.createCell(NumberConstants.SIX);
		cell.setCellValue(IncapConstants.ORGANIZATION_WISE_ATTENDANCE_REGISTER+month+ApplicationConstants.DELIMITER_HYPHEN+year);
		cell.setCellStyle(cellStyleBold);
		
		int sn=NumberConstants.ONE;
		for(IncapMonthlyPunchReportDto IncapMonthlyPunchReportDto:incapMonthlySummaryReportList) {
			int cellIndex=NumberConstants.ZERO;
			
			//set head for incap monthly report
			rowCount = setHeadForMonthlyReport(sheet, cellStyleBold, rowCount, IncapMonthlyPunchReportDto, cellIndex);
			
			row = sheet.createRow(rowCount++);
			cellIndex=NumberConstants.ZERO;
			cell = row.createCell(cellIndex++);
			cell.setCellValue(sn);
			cell.setCellStyle(cellStyleThin);
			
			cell = row.createCell(cellIndex++);
			cell.setCellValue(IncapMonthlyPunchReportDto.getEmpId());
			cell.setCellStyle(cellStyleThin);
			
			cell = row.createCell(cellIndex++);
			cell.setCellValue(IncapMonthlyPunchReportDto.getEmpName());
			cell.setCellStyle(cellStyleThin);
			
			cell = row.createCell(cellIndex++);
			cell.setCellValue(IncapMonthlyPunchReportDto.getDepartment());
			cell.setCellStyle(cellStyleThin);
			
			cell = row.createCell(cellIndex++);
			cell.setCellValue(IncapMonthlyPunchReportDto.getDesignation());
			cell.setCellStyle(cellStyleThin);
			
			for(String shiftValue: IncapMonthlyPunchReportDto.getShiftListValue()) {
				cell = row.createCell(5+cellIndex++);
				cell.setCellValue(shiftValue);
				cell.setCellStyle(cellStyleThin);
			}
			
			row = sheet.createRow(rowCount++);
			row = sheet.createRow(rowCount++);
			
			cellIndex =NumberConstants.ZERO;
			for(String date :IncapMonthlyPunchReportDto.getDateList()) {
				cell = row.createCell(NumberConstants.ONE+cellIndex++);
				cell.setCellValue(date);
				cell.setCellStyle(cellStyleBold);
			}
			
			
			Map<String, List<String>> dataMapList = IncapMonthlyPunchReportDto.getDataMapList();
			
			for(String key:dataMapList.keySet()) {
				List<String> dataList = dataMapList.get(key);
				row = sheet.createRow(rowCount++);
				cellIndex = NumberConstants.ZERO;
				cell = row.createCell(cellIndex++);
				cell.setCellValue(key);
				cell.setCellStyle(cellStyleThin);
				for(String data: dataList) {
					cell = row.createCell(cellIndex++);
					cell.setCellValue(data);
					cell.setCellStyle(cellStyleThin);
				}
			}
			
			row = sheet.createRow(rowCount++);
			row = sheet.createRow(rowCount++);
			sn++;
		}
		
		FileOutputStream fileOut = new FileOutputStream(filename);
		workBook.write(fileOut);
		ServletOutputStream outputStream = response.getOutputStream();
		workBook.write(outputStream);
		fileOut.close();
		workBook.close();
	}


	private int setHeadForMonthlyReport(Sheet sheet, CellStyle cellStyleBold, int rowCount,
			IncapMonthlyPunchReportDto IncapMonthlyPunchReportDto, int cellIndex) {
		
		
		Row row = sheet.createRow(rowCount++);
		Cell cell = row.createCell(cellIndex++);
		cell.setCellValue(HeaderConstants.SL_NO);
		cell.setCellStyle(cellStyleBold);
		
		cell = row.createCell(cellIndex++);
		cell.setCellValue(HeaderConstants.EMPLOYEE_ID);
		cell.setCellStyle(cellStyleBold);
		
		cell = row.createCell(cellIndex++);
		cell.setCellValue(HeaderConstants.EMPLOYEE_NAME);
		cell.setCellStyle(cellStyleBold);
		
		cell = row.createCell(cellIndex++);
		cell.setCellValue(HeaderConstants.DEPARTMENT);
		cell.setCellStyle(cellStyleBold);
		
		cell = row.createCell(cellIndex++);
		cell.setCellValue(HeaderConstants.DESIGNATION);
		cell.setCellStyle(cellStyleBold);
		
		for(String shift : IncapMonthlyPunchReportDto.getShiftList()) {
			cell = row.createCell(5+cellIndex++);
			cell.setCellValue(shift);
			cell.setCellStyle(cellStyleBold);
		};
		return rowCount;
	}
}
