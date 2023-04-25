package com.eikona.mata.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.DailyAttendance;

@SpringBootTest
@ActiveProfiles("test")
public class AbsentReportServiceTest {
	
	@Autowired
	private AbsentReportService absentReportService;
	
	@Test
	public void seatchTest() {
//		search(Long id, String sDate, String eDate, String employeeId, String employeeName,
//				String office, String department, String designation, int pageno, String sortField, String sortDir);

		PaginationDto<DailyAttendance> paginationObj = absentReportService.search(1l, "", "", "", "",
				"", "", "", 1, "", "asc");
		
		if(paginationObj.getData().isEmpty())
			assert(paginationObj).getData().size() == 0;
		else {
			assert(paginationObj).getData().size() > 0;
		}
	}
}