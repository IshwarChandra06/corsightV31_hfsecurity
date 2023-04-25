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
import com.eikona.mata.constants.HeaderConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.entity.ActionDetails;
import com.eikona.mata.repository.ActionDetailsRepository;

@Component
public class ExportActionDetailsData {

	
	@Autowired
	private ActionDetailsRepository actionDetailsRepository;
	
	@Autowired
	private GeneralSpecificationUtil<ActionDetails> generalSpecification;
	
	@Autowired
	private CalendarUtil calendarUtil;
 	
	public void fileExportBySearchValue(HttpServletResponse response, String sDate, String eDate, String employeeId, String name, String device, String flag) throws ParseException, IOException {

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
		
		List<ActionDetails> listData = getListOfActionDetails(startDate, endDate, employeeId, name, device);
		
		excelGenerator(response, listData);
	}
	public List<ActionDetails> getListOfActionDetails(Date startDate, Date endDate, String employeeId, String name, String device) {
		Specification<ActionDetails> isDeletedFalse = generalSpecification.isDeletedSpecification(false);
		Specification<ActionDetails> dateSpc = generalSpecification.dateSpecification(startDate, endDate, ApplicationConstants.LAST_MODIFIED_DATE);
		Specification<ActionDetails> empIdSpc = generalSpecification.foreignKeyDoubleObjectStringSpecification(employeeId, "action","employee", "empId");
		Specification<ActionDetails> employeeNameSpc = generalSpecification.foreignKeyDoubleObjectStringSpecification(name, "action", "employee", "name");
		Specification<ActionDetails> pendingSpc = generalSpecification.stringSpecification("Pending", "status");
		Specification<ActionDetails> errorSpc = generalSpecification.stringSpecification("Error", "status");
		Specification<ActionDetails> deviceSpc = generalSpecification.foreignKeyStringSpecification(device, "device", "name");
		
		List<ActionDetails> employeeList =actionDetailsRepository.findAll(isDeletedFalse.and(dateSpc).and(employeeNameSpc).and(empIdSpc).and(deviceSpc).and(pendingSpc.or(errorSpc)));
		return employeeList;
	}
	
	public void excelGenerator(HttpServletResponse response, List<ActionDetails> actionDetailsList)
			throws ParseException, IOException {

		DateFormat dateFormat = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_INDIA_SPLIT_BY_SPACE);
		String currentDateTime = dateFormat.format(new Date());
		String filename = "Exception_Report" + currentDateTime + ApplicationConstants.EXTENSION_EXCEL;
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
		setExcelDataCellWise(actionDetailsList, sheet, rowCount, cellStyle);

		FileOutputStream fileOut = new FileOutputStream(filename);
		workBook.write(fileOut);
		ServletOutputStream outputStream = response.getOutputStream();
		workBook.write(outputStream);
		fileOut.close();
		workBook.close();

	}
	
	public CellStyle setBorderStyle(Workbook workBook, BorderStyle borderStyle, Font font) {
		CellStyle cellStyle = workBook.createCellStyle();
		cellStyle.setBorderTop(borderStyle);
		cellStyle.setBorderBottom(borderStyle);
		cellStyle.setBorderLeft(borderStyle);
		cellStyle.setBorderRight(borderStyle);
		cellStyle.setFont(font);
		return cellStyle;
	}
	private void setExcelDataCellWise(List<ActionDetails> actionDetailsList, Sheet sheet, int rowCount,
			CellStyle cellStyle) {
		
		for (ActionDetails actionDetails : actionDetailsList) {
			if(rowCount==90000)
				break;
			Row row = sheet.createRow(rowCount++);

			int columnCount = NumberConstants.ZERO;

			Cell cell = row.createCell(columnCount++);
			cell.setCellValue(actionDetails.getAction().getEmployee().getEmpId());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(actionDetails.getAction().getEmployee().getName());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(setDateFormat(actionDetails.getLastModifiedDate()));
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(actionDetails.getDevice().getName());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(actionDetails.getDevice().getIpAddress());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(actionDetails.getDevice().getSerialNo());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(actionDetails.getStatus());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(actionDetails.getMessage());
			cell.setCellStyle(cellStyle);
			
		}
	}
	
	private String setDateFormat(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
		String dareStr="";
		if(null!=date) {
			dareStr=dateFormat.format(date);
		}
		return dareStr;
		
	}
	
    private void setHeaderForExcel(Row row, CellStyle cellStyle) {
		Cell cell = row.createCell(NumberConstants.ZERO);
		cell.setCellValue(HeaderConstants.EMPLOYEE_ID);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(NumberConstants.ONE);
		cell.setCellValue(HeaderConstants.EMPLOYEE_NAME);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(NumberConstants.TWO);
		cell.setCellValue("Modified Date");
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(NumberConstants.THREE);
		cell.setCellValue("Device Name");
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(NumberConstants.FOUR);
		cell.setCellValue("IP Address");
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(NumberConstants.FIVE);
		cell.setCellValue("Serial No");
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(NumberConstants.SIX);
		cell.setCellValue("Status");
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(NumberConstants.SEVEN);
		cell.setCellValue("Message");
		cell.setCellStyle(cellStyle);
	}
}
