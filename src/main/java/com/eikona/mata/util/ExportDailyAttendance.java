package com.eikona.mata.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.DailyAttendanceConstants;
import com.eikona.mata.constants.HeaderConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.entity.DailyAttendance;
import com.eikona.mata.repository.DailyAttendanceRepository;

@Component
public class ExportDailyAttendance {

	
	@Autowired
	private DailyAttendanceRepository dailyAttendanceRepository;
	
	@Autowired
	private GeneralSpecificationUtil<DailyAttendance> generalSpecification;
	
	@Autowired
	private CalendarUtil calendarUtil;
 
	public void fileExportBySearchValue(HttpServletResponse response, String sDate, String eDate, String employeeName,
			String employeeId, String designation, String office, String department,String status, String flag) throws ParseException, IOException {

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
		
		List<DailyAttendance> dailyAttendanceList = getListOfDailyAttendance(employeeName, employeeId, designation,
				office, department, status, startDate, endDate);
		
		excelGenerator(response, dailyAttendanceList);
	}
	private List<DailyAttendance> getListOfDailyAttendance(String employeeName, String employeeId, String designation,
			String office, String department, String status, Date startDate, Date endDate) {
		Specification<DailyAttendance> containsDate = generalSpecification.dateSpecification(startDate, endDate,ApplicationConstants.DATE);
		Specification<DailyAttendance> containsEmployeeName = generalSpecification.stringSpecification(employeeName,DailyAttendanceConstants.EMPLOYEE_NAME);
		Specification<DailyAttendance> containsEmployeeId = generalSpecification.stringSpecification(employeeId,DailyAttendanceConstants.EMPLOYEE_ID);
    	Specification<DailyAttendance> departmentSpec = generalSpecification.stringSpecification(department,DailyAttendanceConstants.DEPARTMENT); 
    	Specification<DailyAttendance>  designationSpec = generalSpecification.stringSpecification(designation,DailyAttendanceConstants.DESIGNATION);
    	Specification<DailyAttendance> branchSpec = generalSpecification.stringSpecification(office,DailyAttendanceConstants.BRANCH); 
    	Specification<DailyAttendance> statusSpec = generalSpecification.stringSpecification(status,DailyAttendanceConstants.ATTENDANCE_STATUS);
		
		List<DailyAttendance> dailyAttendanceList =dailyAttendanceRepository.findAll(containsDate.and(containsEmployeeName)
				.and(containsEmployeeId).and(branchSpec).and(departmentSpec).and(designationSpec).and(statusSpec));
		return dailyAttendanceList;
	}
	public void excelGenerator(HttpServletResponse response, List<DailyAttendance> dailyAttendanceList)
			throws ParseException, IOException {

		DateFormat dateFormat = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_INDIA_SPLIT_BY_SPACE);
		String currentDateTime = dateFormat.format(new Date());
		String filename = DailyAttendanceConstants.DAILY_ATTENDANCE_REPORT + currentDateTime + ApplicationConstants.EXTENSION_EXCEL;
		Workbook workBook = new XSSFWorkbook();
		Sheet sheet = workBook.createSheet();

		int rowCount = NumberConstants.ZERO;
		Row row = sheet.createRow(rowCount++);

		Font font = workBook.createFont();
		font.setBold(true);

		CellStyle cellStyle = setBorderStyle(workBook, BorderStyle.THICK, font);

		setHeaderForExcel(row, cellStyle);

		font = workBook.createFont();
		font.setBold(false);
		cellStyle = setBorderStyle(workBook, BorderStyle.THIN, font);
		
		//set data for excel
		setExcelDataCellWise(dailyAttendanceList, sheet, rowCount, cellStyle);

		FileOutputStream fileOut = new FileOutputStream(filename);
		workBook.write(fileOut);
		ServletOutputStream outputStream = response.getOutputStream();
		workBook.write(outputStream);
		fileOut.close();
		workBook.close();

	}
	
	private CellStyle setBorderStyle(Workbook workBook, BorderStyle borderStyle, Font font) {
		CellStyle cellStyle = workBook.createCellStyle();
		cellStyle.setBorderTop(borderStyle);
		cellStyle.setBorderBottom(borderStyle);
		cellStyle.setBorderLeft(borderStyle);
		cellStyle.setBorderRight(borderStyle);
		cellStyle.setFont(font);
		return cellStyle;
	}
	private void setExcelDataCellWise(List<DailyAttendance> dailyAttendanceList, Sheet sheet, int rowCount,
			CellStyle cellStyle) {
		for (DailyAttendance dailyAttendance : dailyAttendanceList) {
			Row row = sheet.createRow(rowCount++);

			int columnCount = NumberConstants.ZERO;

			Cell cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getId());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getDateStr());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getEmpId());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getEmployeeName());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getAttendanceStatus());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getAsPerRoster());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getDepartment());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getDesignation());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getBranch());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getCity());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getShift());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getShiftInTime());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getShiftOutTime());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getEmpInTime());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getEmpOutTime());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getWorkTime());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getOverTimeStr());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			if(null!=dailyAttendance.getEarlyComing())
				cell.setCellValue(dailyAttendance.getEarlyComing());
			else
				cell.setCellValue(ApplicationConstants.DELIMITER_EMPTY);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			if(null!=dailyAttendance.getLateGoing())
				cell.setCellValue(dailyAttendance.getLateGoing());
			else
				cell.setCellValue(ApplicationConstants.DELIMITER_EMPTY);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			if(null!=dailyAttendance.getEarlyGoing())
				cell.setCellValue(dailyAttendance.getEarlyGoing());
			else
				cell.setCellValue(ApplicationConstants.DELIMITER_EMPTY);
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			if(null!=dailyAttendance.getLateComing())
				cell.setCellValue(dailyAttendance.getLateComing());
			else
				cell.setCellValue(ApplicationConstants.DELIMITER_EMPTY);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getMissedOutPunch());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getEmpInTemp());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getEmpOutTemp());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getEmpInMask());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getEmpOutMask());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getEmpInLocation());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getEmpOutLocation());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getEmpInAccessType());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getEmpOutAccessType());
			cell.setCellStyle(cellStyle);
			

		}
	}
	private void setHeaderForExcel(Row row, CellStyle cellStyle) {
		Cell cell = row.createCell(NumberConstants.ZERO);
		cell.setCellValue(HeaderConstants.ID);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(NumberConstants.ONE);
		cell.setCellValue(HeaderConstants.DATE);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(NumberConstants.TWO);
		cell.setCellValue(HeaderConstants.EMPLOYEE_ID);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(NumberConstants.THREE);
		cell.setCellValue(HeaderConstants.EMPLOYEE_NAME);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(NumberConstants.FOUR);
		cell.setCellValue(HeaderConstants.ATTENDANCE_STATUS);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(NumberConstants.FIVE);
		cell.setCellValue(HeaderConstants.AS_PER_ROSTER);
		cell.setCellStyle(cellStyle);
		
	    cell = row.createCell(NumberConstants.SIX);
		cell.setCellValue(HeaderConstants.DEPARTMENT);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(NumberConstants.SEVEN);
		cell.setCellValue(HeaderConstants.DESIGNATION);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(NumberConstants.EIGHT);
		cell.setCellValue(HeaderConstants.OFFICE);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(NumberConstants.NINE);
		cell.setCellValue(HeaderConstants.CITY);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(NumberConstants.TEN);
		cell.setCellValue(HeaderConstants.SHIFT);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(NumberConstants.ELEVEN);
		cell.setCellValue(HeaderConstants.SHIFT_IN_TIME);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(NumberConstants.TWELVE);
		cell.setCellValue(HeaderConstants.SHIFT_OUT_TIME);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(NumberConstants.THIRTEEN);
		cell.setCellValue(HeaderConstants.EMP_IN_TIME);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(NumberConstants.FOURTEEN);
		cell.setCellValue(HeaderConstants.EMP_OUT_TIME);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(NumberConstants.FIFTEEN);
		cell.setCellValue(HeaderConstants.WORK_TIME);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(NumberConstants.SIXTEEN);
		cell.setCellValue(HeaderConstants.OVER_TIME);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(NumberConstants.SEVENTEEN);
		cell.setCellValue(HeaderConstants.EARLY_COMING);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(NumberConstants.EIGHTEEN);
		cell.setCellValue(HeaderConstants.LATE_GOING);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(NumberConstants.NINETEEN);
		cell.setCellValue(HeaderConstants.EARLY_GOING);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(NumberConstants.TWENTY);
		cell.setCellValue(HeaderConstants.LATE_COMING);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(NumberConstants.TWENTY_ONE);
		cell.setCellValue(HeaderConstants.MISSED_OUT_PUNCH);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(NumberConstants.TWENTY_TWO);
		cell.setCellValue(HeaderConstants.EMP_IN_TEMP);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(NumberConstants.TWENTY_THREE);
		cell.setCellValue(HeaderConstants.EMP_OUT_TEMP);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(NumberConstants.TWENTY_FOUR);
		cell.setCellValue(HeaderConstants.EMP_IN_MASK);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(NumberConstants.TWENTY_FIVE);
		cell.setCellValue(HeaderConstants.EMP_OUT_MASK);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(NumberConstants.TWENTY_SIX);
		cell.setCellValue(HeaderConstants.EMP_IN_LOCATION);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(NumberConstants.TWENTY_SEVEN);
		cell.setCellValue(HeaderConstants.EMP_OUT_LOCATION);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(NumberConstants.TWENTY_EIGHT);
		cell.setCellValue(HeaderConstants.EMP_IN_ACCESS_TYPE);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(NumberConstants.TWENTY_NINE);
		cell.setCellValue(HeaderConstants.EMP_OUT_ACCESS_TYPE);
		cell.setCellStyle(cellStyle);
	}
}
