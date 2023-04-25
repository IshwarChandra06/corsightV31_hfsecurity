package com.eikona.mata.service.impl.model;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.DailyAttendanceConstants;
import com.eikona.mata.constants.EmailScheduleConstants;
import com.eikona.mata.constants.HeaderConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.entity.DailyAttendance;
import com.eikona.mata.repository.DailyAttendanceRepository;
import com.eikona.mata.util.CalendarUtil;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class EmailSechedulePdfServiceImpl {

	@Autowired
	private DailyAttendanceRepository dailyReportRepository;

	@Autowired
	private CalendarUtil calendarUtil;

	private void writeTableHeader(PdfPTable table) {
		PdfPCell cell = new PdfPCell();
		cell.setBackgroundColor(Color.BLUE);
		cell.setPadding(NumberConstants.FIVE);

		Font font = FontFactory.getFont(FontFactory.HELVETICA);
		font.setColor(Color.WHITE);

		cell.setPhrase(new Phrase(HeaderConstants.DATE, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.EMPLOYEE_ID, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.EMPLOYEE_NAME, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.DEPARTMENT, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.DESIGNATION, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.BRANCH, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.ORGANIZATION, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.CITY, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.SHIFT, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.SHIFT_IN_TIME, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.SHIFT_OUT_TIME, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.EMP_IN_TIME, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.EMP_OUT_TIME, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.WORK_TIME, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.ATTENDANCE_STATUS, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.MISSED_OUT_PUNCH, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.EARLY_COMING, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.LATE_GOING, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.LATE_COMING, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.EARLY_GOING, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.OVER_TIME, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.EMP_IN_MASK, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.EMP_OUT_MASK, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.EMP_IN_TEMP, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.EMP_OUT_TEMP, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.EMP_IN_LOCATION, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.EMP_OUT_LOCATION, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.EMP_IN_ACCESS_TYPE, font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(HeaderConstants.EMP_OUT_ACCESS_TYPE, font));
		table.addCell(cell);

	}

	private void writeTableData(PdfPTable table, DailyAttendance dailyReport) {
		table.addCell(String.valueOf(dailyReport.getDate()));
		table.addCell(dailyReport.getEmpId());
		table.addCell(dailyReport.getEmployeeName());
		table.addCell(dailyReport.getDepartment());
		table.addCell(dailyReport.getDesignation());
		table.addCell(dailyReport.getBranch());
		table.addCell(dailyReport.getOrganization());
		table.addCell(dailyReport.getCity());
		table.addCell(dailyReport.getShift());
		table.addCell(dailyReport.getShiftInTime());
		table.addCell(dailyReport.getShiftOutTime());
		table.addCell(dailyReport.getEmpInTime());
		table.addCell(dailyReport.getEmpOutTime());
		table.addCell(dailyReport.getWorkTime());
		table.addCell(dailyReport.getAttendanceStatus());
		table.addCell(dailyReport.getMissedOutPunch());
		table.addCell(String.valueOf(dailyReport.getEarlyComing()));
		table.addCell(String.valueOf(dailyReport.getLateGoing()));
		table.addCell(String.valueOf(dailyReport.getLateComing()));
		table.addCell(String.valueOf(dailyReport.getEarlyGoing()));
		table.addCell(String.valueOf(dailyReport.getOverTime()));
		table.addCell(String.valueOf(dailyReport.getEmpInMask()));
		table.addCell(String.valueOf(dailyReport.getEmpOutMask()));
		table.addCell(dailyReport.getEmpInTemp());
		table.addCell(dailyReport.getEmpOutTemp());
		table.addCell(dailyReport.getEmpInLocation());
		table.addCell(dailyReport.getEmpOutLocation());
		table.addCell(dailyReport.getEmpInAccessType());
		table.addCell(dailyReport.getEmpOutAccessType());
	}

	public String export() throws DocumentException, IOException, ParseException {

		Date endDate = calendarUtil.getConvertedDate(new Date(), NumberConstants.ZERO, NumberConstants.ZERO,
				NumberConstants.ZERO);

		Calendar startDate = Calendar.getInstance();
		startDate.setTime(endDate);
		startDate.set(Calendar.DATE, -NumberConstants.ONE);

		List<DailyAttendance> dailyReportList = dailyReportRepository.getAllReportByDateRangeCustom(startDate.getTime(),
				endDate);

		DateFormat dateFormat = new SimpleDateFormat(
				ApplicationConstants.DATE_TIME_FORMAT_OF_INDIA_SPLIT_BY_UNDERSCORE);
		String currentDateTime = dateFormat.format(new Date());
		File theDir = new File(EmailScheduleConstants.PDF_ROOTPATH);
		if (!theDir.exists()) {
			theDir.mkdirs();
		}
		String filename = theDir + EmailScheduleConstants.PDF_FILE_NAME + currentDateTime
				+ ApplicationConstants.EXTENSION_PDF;
		Document document = new Document(PageSize.A1);
		PdfWriter.getInstance(document, new FileOutputStream(filename));

		document.open();
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		font.setSize(NumberConstants.EIGHTEEN);
		font.setColor(Color.BLUE);

		Paragraph p = new Paragraph(DailyAttendanceConstants.DAILY_ATTENDANCE_REPORT, font);
		p.setAlignment(Paragraph.ALIGN_CENTER);

		document.add(p);

		PdfPTable table = new PdfPTable(NumberConstants.TWENTY_NINE);
		table.setWidthPercentage(NumberConstants.FLOAT_HUNDRED);
		table.setSpacingBefore(NumberConstants.TEN);

		writeTableHeader(table);
		for (DailyAttendance dailyreport : dailyReportList) {
			writeTableData(table, dailyreport);
		}

		document.add(table);
		document.close();
		return filename;
	}
}
