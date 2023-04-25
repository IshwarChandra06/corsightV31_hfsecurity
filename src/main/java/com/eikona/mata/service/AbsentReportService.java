package com.eikona.mata.service;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.DailyAttendance;

public interface AbsentReportService {

	PaginationDto<DailyAttendance> search(Long id, String sDate, String eDate, String employeeId, String employeeName,
			String office, String department, String designation, int pageno, String sortField, String sortDir);
}
