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
import com.eikona.mata.constants.EmployeeConstants;
import com.eikona.mata.constants.EmployeeShiftDailyAssociationConstants;
import com.eikona.mata.constants.HeaderConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.entity.EmployeeShiftDailyAssociation;
import com.eikona.mata.repository.EmployeeShiftDailyAssociationRepository;

@Component
public class ExportEmployeeShiftDailyAssociation {


	@Autowired
	private EmployeeShiftDailyAssociationRepository employeeShiftDailyAssociationRepository;
	
	@Autowired
	private CalendarUtil calendarUtil;
	
	@Autowired
	private GeneralSpecificationUtil<EmployeeShiftDailyAssociation> generalSpecification;

	public void fileExportBySearchValue(HttpServletResponse response, Long id, String sDate, String eDate,
			String employeeId, String employeeName, String department, String shift, String flag)
			throws ParseException, IOException {
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

		List<EmployeeShiftDailyAssociation> employeeShiftList = getEmployeeRosterList(id, employeeId, employeeName,
				department, shift, startDate, endDate);

		excelGenerator(response, employeeShiftList);
	}

	private List<EmployeeShiftDailyAssociation> getEmployeeRosterList(Long id, String employeeId, String employeeName,
			String department, String shift, Date startDate, Date endDate) {
		Specification<EmployeeShiftDailyAssociation> idSpc = generalSpecification.longSpecification(id, ApplicationConstants.ID);
		Specification<EmployeeShiftDailyAssociation> dateSpc = generalSpecification.dateSpecification(startDate, endDate,ApplicationConstants.DATE);
		Specification<EmployeeShiftDailyAssociation> employeeIdSpc = generalSpecification.foreignKeyStringSpecification(employeeId, EmployeeShiftDailyAssociationConstants.EMPLOYEE, EmployeeConstants.EMPID);
		Specification<EmployeeShiftDailyAssociation> employeeNameSpc = generalSpecification.foreignKeyStringSpecification(employeeName, EmployeeShiftDailyAssociationConstants.EMPLOYEE, EmployeeConstants.NAME);
		Specification<EmployeeShiftDailyAssociation> departmentSpc = generalSpecification.foreignKeyDoubleObjectStringSpecification(department, EmployeeShiftDailyAssociationConstants.EMPLOYEE, EmployeeConstants.DEPARTMENT, EmployeeConstants.NAME);
		Specification<EmployeeShiftDailyAssociation> shiftSpc = generalSpecification.foreignKeyStringSpecification(shift, EmployeeShiftDailyAssociationConstants.SHIFT, EmployeeConstants.NAME);

		List<EmployeeShiftDailyAssociation> employeeShiftList = employeeShiftDailyAssociationRepository
				.findAll(idSpc.and(dateSpc).and(employeeIdSpc).and(employeeNameSpc).and(departmentSpc).and(shiftSpc));
		return employeeShiftList;
	}

	public void excelGenerator(HttpServletResponse response, List<EmployeeShiftDailyAssociation> employeeShiftList)
			throws ParseException, IOException {

		DateFormat dateFormat = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_INDIA_SPLIT_BY_SPACE);
		String currentDateTime = dateFormat.format(new Date());
		String filename = EmployeeShiftDailyAssociationConstants.EMPLOYEE_SHIFT + currentDateTime + ApplicationConstants.EXTENSION_EXCEL;
		Workbook workBook = new XSSFWorkbook();
		Sheet sheet = workBook.createSheet();

		int rowCount = NumberConstants.ZERO;
		Row row = sheet.createRow(rowCount++);

		Font font = workBook.createFont();
		font.setBold(true);

		CellStyle cellStyle = setBorderStyle(workBook, BorderStyle.THICK, font);

		setShiftAssigedExcelHeader(row, cellStyle);

		font = workBook.createFont();
		font.setBold(false);
		cellStyle = setBorderStyle(workBook, BorderStyle.THIN, font);

		setShiftAssignedExcelData(employeeShiftList, sheet, rowCount, cellStyle);

		FileOutputStream fileOut = new FileOutputStream(filename);
		workBook.write(fileOut);
		ServletOutputStream outputStream = response.getOutputStream();
		workBook.write(outputStream);
		fileOut.close();
		workBook.close();

	}

	private void setShiftAssignedExcelData(List<EmployeeShiftDailyAssociation> employeeShiftList, Sheet sheet,
			int rowCount, CellStyle cellStyle) {
		for (EmployeeShiftDailyAssociation employeeShift : employeeShiftList) {
			Row row = sheet.createRow(rowCount++);

			int columnCount = NumberConstants.ZERO;

			Cell cell = row.createCell(columnCount++);
			cell.setCellValue(employeeShift.getId());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(employeeShift.getDateStr());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			if (null != employeeShift.getEmployee())
				cell.setCellValue(employeeShift.getEmployee().getEmpId());
			else
				cell.setCellValue(ApplicationConstants.DELIMITER_SPACE);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			if (null != employeeShift.getEmployee())
				cell.setCellValue(employeeShift.getEmployee().getName());
			else
				cell.setCellValue(ApplicationConstants.DELIMITER_SPACE);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			if (null != employeeShift.getEmployee()) {
				if (null != employeeShift.getEmployee().getDepartment())
					cell.setCellValue(employeeShift.getEmployee().getDepartment().getName());
				else
					cell.setCellValue(ApplicationConstants.DELIMITER_SPACE);
			} else
				cell.setCellValue(ApplicationConstants.DELIMITER_SPACE);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			if (null != employeeShift.getShift())
				cell.setCellValue(employeeShift.getShift().getName());
			else
				cell.setCellValue(ApplicationConstants.DELIMITER_SPACE);
			cell.setCellStyle(cellStyle);

		}
	}

	private void setShiftAssigedExcelHeader(Row row, CellStyle cellStyle) {
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
		cell.setCellValue(HeaderConstants.DEPARTMENT);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(NumberConstants.FIVE);
		cell.setCellValue(HeaderConstants.SHIFT);
		cell.setCellStyle(cellStyle);
	}

	public void employeeShiftUnassignedExportBySearchValue(HttpServletResponse response, Long id, String sDate,
			String eDate, String employeeId, String employeeName, String department, String flag) throws ParseException, IOException {
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
		Specification<EmployeeShiftDailyAssociation> idSpc = generalSpecification.longSpecification(id, ApplicationConstants.ID);
		Specification<EmployeeShiftDailyAssociation> dateSpc = generalSpecification.dateSpecification(startDate, endDate,ApplicationConstants.DATE);
		Specification<EmployeeShiftDailyAssociation> employeeIdSpc = generalSpecification.foreignKeyStringSpecification(employeeId, EmployeeShiftDailyAssociationConstants.EMPLOYEE, EmployeeConstants.EMPID);
		Specification<EmployeeShiftDailyAssociation> employeeNameSpc = generalSpecification.foreignKeyStringSpecification(employeeName, EmployeeShiftDailyAssociationConstants.EMPLOYEE, EmployeeConstants.NAME);
		Specification<EmployeeShiftDailyAssociation> departmentSpc = generalSpecification.foreignKeyDoubleObjectStringSpecification(department, EmployeeShiftDailyAssociationConstants.EMPLOYEE, EmployeeConstants.DEPARTMENT, EmployeeConstants.NAME);
		Specification<EmployeeShiftDailyAssociation> shiftIsNull = generalSpecification.isNullSpecification(EmployeeShiftDailyAssociationConstants.SHIFT);
    	
    	List<EmployeeShiftDailyAssociation> employeeShiftList = employeeShiftDailyAssociationRepository
				.findAll(idSpc.and(dateSpc).and(employeeIdSpc).and(employeeNameSpc).and(departmentSpc).and(shiftIsNull));

		shiftUnAssignedExcelGenerator(response, employeeShiftList);
		
	}

	private void shiftUnAssignedExcelGenerator(HttpServletResponse response,
			List<EmployeeShiftDailyAssociation> employeeShiftList) throws ParseException, IOException {

		DateFormat dateFormat = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_INDIA_SPLIT_BY_SPACE);
		String currentDateTime = dateFormat.format(new Date());
		String filename = EmployeeShiftDailyAssociationConstants.EMPLOYEE_SHIFT_UNASSIGNED + currentDateTime + ApplicationConstants.EXTENSION_EXCEL;
		Workbook workBook = new XSSFWorkbook();
		Sheet sheet = workBook.createSheet();

		int rowCount = NumberConstants.ZERO;
		Row row = sheet.createRow(rowCount++);

		Font font = workBook.createFont();
		font.setBold(true);

		CellStyle cellStyle = setBorderStyle(workBook, BorderStyle.THICK, font);

		getShiftUnassignedExcelHeader(row, cellStyle);


		font = workBook.createFont();
		font.setBold(false);
		cellStyle = setBorderStyle(workBook, BorderStyle.THIN, font);

		setShiftUnassignedExcelData(employeeShiftList, sheet, rowCount, cellStyle);

		FileOutputStream fileOut = new FileOutputStream(filename);
		workBook.write(fileOut);
		ServletOutputStream outputStream = response.getOutputStream();
		workBook.write(outputStream);
		fileOut.close();
		workBook.close();
		
	}

	private void setShiftUnassignedExcelData(List<EmployeeShiftDailyAssociation> employeeShiftList, Sheet sheet,
			int rowCount, CellStyle cellStyle) {
		for (EmployeeShiftDailyAssociation employeeShift : employeeShiftList) {
			Row row = sheet.createRow(rowCount++);

			int columnCount = NumberConstants.ZERO;

			Cell cell = row.createCell(columnCount++);
			cell.setCellValue(employeeShift.getId());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(employeeShift.getDateStr());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			if (null != employeeShift.getEmployee())
				cell.setCellValue(employeeShift.getEmployee().getEmpId());
			else
				cell.setCellValue(ApplicationConstants.DELIMITER_SPACE);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			if (null != employeeShift.getEmployee())
				cell.setCellValue(employeeShift.getEmployee().getName());
			else
				cell.setCellValue(ApplicationConstants.DELIMITER_SPACE);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			if (null != employeeShift.getEmployee()) {
				if (null != employeeShift.getEmployee().getDepartment())
					cell.setCellValue(employeeShift.getEmployee().getDepartment().getName());
				else
					cell.setCellValue(ApplicationConstants.DELIMITER_SPACE);
			} else
				cell.setCellValue(ApplicationConstants.DELIMITER_SPACE);
			cell.setCellStyle(cellStyle);


		}
	}

	private void getShiftUnassignedExcelHeader(Row row, CellStyle cellStyle) {
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
		cell.setCellValue(HeaderConstants.DEPARTMENT);
		cell.setCellStyle(cellStyle);
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
}
