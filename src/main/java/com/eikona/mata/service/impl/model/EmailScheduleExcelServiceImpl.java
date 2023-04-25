package com.eikona.mata.service.impl.model;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.EmailScheduleConstants;
import com.eikona.mata.constants.HeaderConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.entity.DailyAttendance;
import com.eikona.mata.repository.DailyAttendanceRepository;
import com.eikona.mata.util.CalendarUtil;


@Service
public class EmailScheduleExcelServiceImpl {
	
	@Autowired
	private DailyAttendanceRepository dailyReportRepository;
	
	@Autowired
	private CalendarUtil calendarUtil;
	
	public String emailSchedular() throws ParseException, IOException {
		
		Date endDate = calendarUtil.getConvertedDate(new Date(), NumberConstants.ZERO, NumberConstants.ZERO, NumberConstants.ZERO);
		
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(endDate);
		startDate.set(Calendar.DATE, -NumberConstants.ONE);
		
		
		List<DailyAttendance> dailyReportList= dailyReportRepository.getAllReportByDateRangeCustom(startDate.getTime(), endDate);
		DateFormat dateFormat = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_INDIA_SPLIT_BY_UNDERSCORE);
		String currentDateTime = dateFormat.format(new Date());
		File theDir = new File(EmailScheduleConstants.EXCEL_ROOTPATH);
		if (!theDir.exists()){
		    theDir.mkdirs();
		}
		
		String filename = theDir + EmailScheduleConstants.EXCEL_FILE_NAME + currentDateTime + ApplicationConstants.EXTENSION_EXCEL ;
		Workbook workBook = new XSSFWorkbook();
		Sheet sheet = workBook.createSheet();

		int rowCount =  NumberConstants.ZERO;
		Row row = sheet.createRow(rowCount++);

		Font font = workBook.createFont();
		font.setBold(true);

		CellStyle cellStyle = setBorderStyle(workBook, BorderStyle.THICK, font);
	
		//set head for excel
		setHeadForExcel(row, cellStyle);
		
		font = workBook.createFont();
		font.setBold(false);
		cellStyle = setBorderStyle(workBook, BorderStyle.THIN, font);
		
		//set data for excel
		setDataForExcel(dailyReportList, sheet, rowCount, cellStyle);

		FileOutputStream fileOut = new FileOutputStream(filename);
		workBook.write(fileOut);
		fileOut.close();
		workBook.close();

		return filename;
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

	private void setDataForExcel(List<DailyAttendance> dailyReportList, Sheet sheet, int rowCount,
			CellStyle cellStyle) {
		
		for (DailyAttendance dailyReport : dailyReportList) {
			Row row = sheet.createRow(rowCount++);
	
			int columnCount = NumberConstants.ZERO;
	
			Cell cell = row.createCell(columnCount++);
			cell.setCellValue(dailyReport.getEmpId());
			cell.setCellStyle(cellStyle);
	
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyReport.getDate());
			cell.setCellStyle(cellStyle);
	
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyReport.getEmployeeName());
			cell.setCellStyle(cellStyle);
	
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyReport.getDepartment());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyReport.getOrganization());
			cell.setCellStyle(cellStyle);
	
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyReport.getCity());
			cell.setCellStyle(cellStyle);
	
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyReport.getBranch());
			cell.setCellStyle(cellStyle);
	
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyReport.getDesignation());
			cell.setCellStyle(cellStyle);
	
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyReport.getShift());
			cell.setCellStyle(cellStyle);
	
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyReport.getShiftInTime());
			cell.setCellStyle(cellStyle);
	
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyReport.getShiftOutTime());
			cell.setCellStyle(cellStyle);
	
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyReport.getEmpInTime());
			cell.setCellStyle(cellStyle);
	
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyReport.getEmpOutTime());
			cell.setCellStyle(cellStyle);
	
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyReport.getEmpInTemp());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyReport.getEmpOutTemp());
			cell.setCellStyle(cellStyle);
	
			cell = row.createCell(columnCount++);
			if(null !=dailyReport.getEmpInMask())
				cell.setCellValue(dailyReport.getEmpInMask());
			cell.setCellStyle(cellStyle);
	
			cell = row.createCell(columnCount++);
			if(null !=dailyReport.getEmpOutMask())
				cell.setCellValue(dailyReport.getEmpOutMask());
			cell.setCellStyle(cellStyle);
	
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyReport.getEmpInLocation());
			cell.setCellStyle(cellStyle);
	
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyReport.getEmpOutLocation());
			cell.setCellStyle(cellStyle);
	
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyReport.getEmpInAccessType());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyReport.getEmpOutAccessType());
			cell.setCellStyle(cellStyle);
	
			cell = row.createCell(columnCount++);
			if(null !=dailyReport.getMissedOutPunch())
				cell.setCellValue(dailyReport.getMissedOutPunch());
			cell.setCellStyle(cellStyle);
	
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyReport.getWorkTime());
			cell.setCellStyle(cellStyle);
	
			cell = row.createCell(columnCount++);
			if(null !=dailyReport.getOverTime())
				cell.setCellValue(dailyReport.getOverTime());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			if(null !=dailyReport.getLateGoing())
				cell.setCellValue(dailyReport.getLateGoing());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			if(null !=dailyReport.getEarlyComing())
				cell.setCellValue(dailyReport.getEarlyComing());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			if(null !=dailyReport.getLateComing())
				cell.setCellValue(dailyReport.getLateComing());
			cell.setCellStyle(cellStyle);
	
			cell = row.createCell(columnCount++);
			if(null !=dailyReport.getEarlyGoing())
				cell.setCellValue(dailyReport.getEarlyGoing());
			cell.setCellStyle(cellStyle);
	
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyReport.getAttendanceStatus());
			cell.setCellStyle(cellStyle);
	
		}
	}

	private void setHeadForExcel(Row row, CellStyle cellStyle) {
		
		Cell cell = row.createCell( NumberConstants.ZERO);
		cell.setCellValue(HeaderConstants.EMPLOYEE_ID);
		cell.setCellStyle(cellStyle);
	
		cell = row.createCell( NumberConstants.ONE);
		cell.setCellValue(HeaderConstants.DATE);
		cell.setCellStyle(cellStyle);
	
		cell = row.createCell( NumberConstants.TWO);
		cell.setCellValue(HeaderConstants.EMPLOYEE_NAME);
		cell.setCellStyle(cellStyle);
	
		cell = row.createCell( NumberConstants.THREE);
		cell.setCellValue(HeaderConstants.DEPARTMENT);
		cell.setCellStyle(cellStyle);
	
		cell = row.createCell( NumberConstants.FOUR);
		cell.setCellValue(HeaderConstants.ORGANIZATION);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(NumberConstants.FIVE);
		cell.setCellValue(HeaderConstants.CITY);
		cell.setCellStyle(cellStyle);
	
		cell = row.createCell(NumberConstants.SIX);
		cell.setCellValue(HeaderConstants.BRANCH);
		cell.setCellStyle(cellStyle);
	
		cell = row.createCell(NumberConstants.SEVEN);
		cell.setCellValue(HeaderConstants.DESIGNATION);
		cell.setCellStyle(cellStyle);
	
		cell = row.createCell(NumberConstants.EIGHT);
		cell.setCellValue(HeaderConstants.SHIFT);
		cell.setCellStyle(cellStyle);
	
		cell = row.createCell(NumberConstants.NINE);
		cell.setCellValue(HeaderConstants.SHIFT_IN_TIME);
		cell.setCellStyle(cellStyle);
	
		cell = row.createCell(NumberConstants.TEN);
		cell.setCellValue(HeaderConstants.SHIFT_OUT_TIME);
		cell.setCellStyle(cellStyle);
	
		cell = row.createCell(NumberConstants.ELEVEN);
		cell.setCellValue(HeaderConstants.EMP_IN_TIME);
		cell.setCellStyle(cellStyle);
	
		cell = row.createCell(NumberConstants.TWELVE);
		cell.setCellValue(HeaderConstants.EMP_OUT_TIME);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(NumberConstants.THIRTEEN);
		cell.setCellValue(HeaderConstants.EMP_IN_TEMP);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(NumberConstants.FOURTEEN);
		cell.setCellValue(HeaderConstants.EMP_OUT_TEMP);
		cell.setCellStyle(cellStyle);
	
		cell = row.createCell(NumberConstants.FIFTEEN);
		cell.setCellValue(HeaderConstants.EMP_IN_MASK);
		cell.setCellStyle(cellStyle);
	
		cell = row.createCell(NumberConstants.SIXTEEN);
		cell.setCellValue(HeaderConstants.EMP_OUT_MASK);
		cell.setCellStyle(cellStyle);
	
		cell = row.createCell(NumberConstants.SEVENTEEN);
		cell.setCellValue(HeaderConstants.EMP_IN_LOCATION);
		cell.setCellStyle(cellStyle);
	
		cell = row.createCell(NumberConstants.EIGHTEEN);
		cell.setCellValue(HeaderConstants.EMP_OUT_LOCATION);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(NumberConstants.NINETEEN);
		cell.setCellValue(HeaderConstants.EMP_IN_ACCESS_TYPE);
		cell.setCellStyle(cellStyle);
	
		cell = row.createCell(NumberConstants.TWENTY);
		cell.setCellValue(HeaderConstants.EMP_OUT_ACCESS_TYPE);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(NumberConstants.TWENTY_ONE);
		cell.setCellValue(EmailScheduleConstants.MISSED_OUT_PUNCH);
		cell.setCellStyle(cellStyle);
	
		cell = row.createCell(NumberConstants.TWENTY_TWO);
		cell.setCellValue(HeaderConstants.WORK_TIME);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(NumberConstants.TWENTY_THREE);
		cell.setCellValue(HeaderConstants.OVER_TIME);
		cell.setCellStyle(cellStyle);
	
		cell = row.createCell(NumberConstants.TWENTY_FOUR);
		cell.setCellValue(HeaderConstants.LATE_GOING);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(NumberConstants.TWENTY_FIVE);
		cell.setCellValue(HeaderConstants.EARLY_COMING);
		cell.setCellStyle(cellStyle);
	
		cell = row.createCell(NumberConstants.TWENTY_SIX);
		cell.setCellValue(HeaderConstants.LATE_COMING);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(NumberConstants.TWENTY_SEVEN);
		cell.setCellValue(HeaderConstants.EARLY_GOING);
		cell.setCellStyle(cellStyle);
	
		cell = row.createCell(NumberConstants.TWENTY_EIGHT);
		cell.setCellValue(HeaderConstants.ATTENDANCE_STATUS);
		cell.setCellStyle(cellStyle);
	}
}
