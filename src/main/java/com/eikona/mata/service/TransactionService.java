package com.eikona.mata.service;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Transaction;

public interface TransactionService {

	

	PaginationDto<Transaction> searchByField(Long id, String sDate, String eDate, String employeeId,
			String employeeName, String office, String device, String department, String designation, int pageno,
			String sortField, String sortDir, String orgName);

	PaginationDto<Transaction> searchEventSummary(String date, String sTime, String eTime, int pageno, String sortField,
			String sortDir);

}
