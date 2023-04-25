package com.eikona.mata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.Transaction;
import com.eikona.mata.service.TransactionService;

@Controller
public class UfoController {
	
	@Autowired
	private TransactionService transactionService;
	
	@GetMapping("/ufo-event-summary")
	@PreAuthorize("hasAuthority('transaction_view')")
	public String transactionList() {
		return "ufo/ufo_summary";
	}
	
	
	@RequestMapping(value = "/api/search/ufo-event-summary", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('transaction_view')")
	public @ResponseBody PaginationDto<Transaction> search(String date, String sTime, String eTime, int pageno, String sortField, String sortDir) {
		
		PaginationDto<Transaction> dtoList = transactionService.searchEventSummary(date, sTime, eTime, pageno, sortField, sortDir);
		
		return dtoList;
	}
	
	
}
