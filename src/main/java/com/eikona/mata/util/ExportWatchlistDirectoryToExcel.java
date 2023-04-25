package com.eikona.mata.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

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
import org.springframework.stereotype.Component;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.dto.ImageExportDto;

@Component
public class ExportWatchlistDirectoryToExcel {
	
	public void exportWatchlistImageToExcel(HttpServletResponse response,String rootPath) {
		try {
			File file = new File(rootPath);
			 File watchlistFolder[] = file.listFiles();
			
			List<ImageExportDto> imageList = new ArrayList<>();
			 for(File watchlist : watchlistFolder) {
				 File imageNameList[] =watchlist.listFiles();
				 
				 for(File image:imageNameList) {
					 ImageExportDto imageDto = new ImageExportDto();
					 imageDto.setWatchlist(watchlist.getName());
					 String[] splitBydot = image.getName().split(Pattern.quote(ApplicationConstants.DELIMITER_DOT));
					 String empId = splitBydot[NumberConstants.ZERO];
					 imageDto.setEmpId(empId);
					 imageList.add(imageDto);
				 }
			 }
			 excelGenerator(response, imageList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void excelGenerator(HttpServletResponse response, List<ImageExportDto> imageList) throws IOException {
		DateFormat dateFormat = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_INDIA_SPLIT_BY_SPACE);
		String currentDateTime = dateFormat.format(new Date());
		String filename = "Watchlist_image_" + currentDateTime + ApplicationConstants.EXTENSION_EXCEL;
		Workbook workBook = new XSSFWorkbook();
		Sheet sheet = workBook.createSheet();

		int rowCount = NumberConstants.ZERO;
		Row row = sheet.createRow(rowCount++);

		Font font = workBook.createFont();
		font.setBold(true);

		CellStyle cellStyle = setBorderStyle(workBook, BorderStyle.THICK, font);

		setExcelHeader(row, cellStyle);

		font = workBook.createFont();
		font.setBold(false);
		cellStyle = setBorderStyle(workBook, BorderStyle.THIN, font);

		setExcelData(imageList, sheet, rowCount, cellStyle);

		FileOutputStream fileOut = new FileOutputStream(filename);
		workBook.write(fileOut);
		ServletOutputStream outputStream = response.getOutputStream();
		workBook.write(outputStream);
		fileOut.close();
		workBook.close();
		
	}
	
	private void setExcelData(List<ImageExportDto> imageList, Sheet sheet, int rowCount, CellStyle cellStyle) {
		for(ImageExportDto imageDto:imageList) {
			Row row = sheet.createRow(rowCount++);

			int columnCount = NumberConstants.ZERO;

			Cell cell = row.createCell(columnCount++);
			cell.setCellValue(imageDto.getWatchlist());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(imageDto.getEmpId());
			cell.setCellStyle(cellStyle);
		}
		
	}

	

	private void setExcelHeader(Row row, CellStyle cellStyle) {
		
		Cell cell = row.createCell(NumberConstants.ZERO);
		cell.setCellValue("Watchlist Name");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(NumberConstants.ONE);
		cell.setCellValue("Employee Id");
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
