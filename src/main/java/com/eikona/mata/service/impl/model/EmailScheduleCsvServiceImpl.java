package com.eikona.mata.service.impl.model;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.supercsv.cellprocessor.FmtDate;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.EmailScheduleConstants;
import com.eikona.mata.constants.HeaderConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.entity.DailyAttendance;
import com.eikona.mata.repository.DailyAttendanceRepository;
import com.eikona.mata.util.CalendarUtil;

@Service
public class EmailScheduleCsvServiceImpl {
	
	@Autowired
	private DailyAttendanceRepository dailyReportRepository;
	
	@Autowired
	private CalendarUtil calendarUtil;

	public String csvGenerator() throws ParseException, IOException {
		
		CellProcessor[] processors = new CellProcessor[] {
	           
				new FmtDate(ApplicationConstants.MONTH_DAY_YEAR),
	            new NotNull(), 
	            new Optional(),
	            new Optional(),
	            new Optional(),
	            new Optional(), 
	            new Optional(),
	            new Optional(),
	            new Optional(),
	            new Optional(), 
	            new Optional(),
	            new Optional(),
	            new Optional(),
	            new Optional(), 
	            new Optional(),
	            new Optional(),
	            new Optional(),
	            new Optional(), 
	            new Optional(),
	            new Optional(),
	            new Optional(),
	            new Optional(), 
	            new Optional(),
	            new Optional(),
	            new Optional(),
	            new Optional(), 
	            new Optional(),
	            new Optional(),
	            new Optional(),
		};
		
		
		Date endDate = calendarUtil.getConvertedDate(new Date(), NumberConstants.ZERO, NumberConstants.ZERO, NumberConstants.ZERO);
		
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(endDate);
		startDate.set(Calendar.DATE, -NumberConstants.ONE);
		
		List<DailyAttendance> dailyReportList= dailyReportRepository.getAllReportByDateRangeCustom(startDate.getTime(), endDate);
		
		DateFormat dateFormat = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_INDIA_SPLIT_BY_UNDERSCORE);
		String currentDateTime = dateFormat.format(new Date());
		File theDir = new File(EmailScheduleConstants.CSV_ROOTPATH);
		if (!theDir.exists()){
		    theDir.mkdirs();
		}
		String filename = theDir+EmailScheduleConstants.CSV_FILE_NAME+currentDateTime+ ApplicationConstants.EXTENSION_CSV ;
		
		 ICsvBeanWriter beanWriter = null;
		 try {
	        beanWriter = new CsvBeanWriter(new FileWriter(filename), CsvPreference.STANDARD_PREFERENCE);
	        
	        String[] header = {
	        		HeaderConstants.DATE, HeaderConstants.EMPLOYEE_ID, HeaderConstants.EMPLOYEE_NAME,
	        		HeaderConstants.ORGANIZATION, HeaderConstants.DEPARTMENT, HeaderConstants.DESIGNATION,
	        		HeaderConstants.BRANCH, HeaderConstants.CITY, HeaderConstants.SHIFT,
	        		HeaderConstants.SHIFT_IN_TIME, HeaderConstants.SHIFT_OUT_TIME, HeaderConstants.EMP_IN_TIME,
	        		HeaderConstants.EMP_OUT_TIME, HeaderConstants.WORK_TIME, HeaderConstants.MISSED_OUT_PUNCH,
	        		HeaderConstants.EARLY_COMING, HeaderConstants.LATE_GOING, HeaderConstants.LATE_COMING,
	        		HeaderConstants.EARLY_GOING, HeaderConstants.OVER_TIME, HeaderConstants.EMP_IN_MASK,
	        		HeaderConstants.EMP_OUT_MASK, HeaderConstants.EMP_IN_TEMP, HeaderConstants.EMP_OUT_TEMP,
	        		HeaderConstants.EMP_IN_LOCATION, HeaderConstants.EMP_OUT_LOCATION, HeaderConstants.EMP_IN_ACCESS_TYPE,
	        		HeaderConstants.EMP_OUT_ACCESS_TYPE, HeaderConstants.ATTENDANCE_STATUS
	        	};
	        
	        String[] field = {
	        		EmailScheduleConstants.DATE, EmailScheduleConstants.EMPID, EmailScheduleConstants.EMPNAME,
	        		EmailScheduleConstants.ORGANIZATION, EmailScheduleConstants.DEPARTMENT, EmailScheduleConstants.DESIGNATION,
	        		EmailScheduleConstants.BRANCH, EmailScheduleConstants.CITY, EmailScheduleConstants.SHIFT,
	        		EmailScheduleConstants.SHIFT_IN_TIME, EmailScheduleConstants.SHIFT_OUT_TIME, EmailScheduleConstants.EMP_IN_TIME,
	        		EmailScheduleConstants.EMP_OUT_TIME, EmailScheduleConstants.WORK_TIME, EmailScheduleConstants.MISSED_OUT_PUNCH,
	        		EmailScheduleConstants.EARLY_COMING, EmailScheduleConstants.LATE_GOING, EmailScheduleConstants.LATE_COMING,
	        		EmailScheduleConstants.EARLY_GOING, EmailScheduleConstants.OVER_TIME, EmailScheduleConstants.EMP_IN_MASK,
	        		EmailScheduleConstants.EMP_OUT_MASK, EmailScheduleConstants.EMP_IN_TEMP, EmailScheduleConstants.EMP_OUT_TEMP,
	        		EmailScheduleConstants.EMP_IN_LOCATION, EmailScheduleConstants.EMP_OUT_LOCATION, EmailScheduleConstants.EMP_IN_ACCESS_TYPE,
	        		EmailScheduleConstants.EMP_OUT_ACCESS_TYPE, EmailScheduleConstants.ATTENDANCE_STATUS
	        	};
	        
	        beanWriter.writeHeader(header);
	 
	        for (DailyAttendance dailyReport : dailyReportList) {
	            beanWriter.write(dailyReport, field, processors);
	        }
	 
	    } catch (IOException e) {
	    	e.printStackTrace();
	    } finally {
	        if (beanWriter != null) {
	            try {
	                beanWriter.close();
	            } catch (IOException e) {
	            	e.printStackTrace();
	            }
	        }
	    }
		return filename;
	}
}
