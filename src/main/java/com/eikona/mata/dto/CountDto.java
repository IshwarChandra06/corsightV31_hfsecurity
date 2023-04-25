package com.eikona.mata.dto;

public class CountDto {
	
	private long totalEmployee;
	private long presentEmployee;
	private long transactions;
	private long noMask;
	public long getTotalEmployee() {
		return totalEmployee;
	}
	public void setTotalEmployee(long totalEmployee) {
		this.totalEmployee = totalEmployee;
	}
	public long getPresentEmployee() {
		return presentEmployee;
	}
	public void setPresentEmployee(long presentEmployee) {
		this.presentEmployee = presentEmployee;
	}
	public long getTransactions() {
		return transactions;
	}
	public void setTransactions(long transactions) {
		this.transactions = transactions;
	}
	public long getNoMask() {
		return noMask;
	}
	public void setNoMask(long noMask) {
		this.noMask = noMask;
	}
	
}
