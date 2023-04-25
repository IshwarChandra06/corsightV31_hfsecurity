package com.eikona.mata.dto;

public class ShiftRosterValidationDto {
	private String emptyEmployeeId;
	private String emptyEmployeeName;
	private String mismatchEmployee;
	private String mismatchShift;
	public String getEmptyEmployeeId() {
		return emptyEmployeeId;
	}
	public void setEmptyEmployeeId(String emptyEmployeeId) {
		this.emptyEmployeeId = emptyEmployeeId;
	}
	public String getEmptyEmployeeName() {
		return emptyEmployeeName;
	}
	public void setEmptyEmployeeName(String emptyEmployeeName) {
		this.emptyEmployeeName = emptyEmployeeName;
	}
	public String getMismatchEmployee() {
		return mismatchEmployee;
	}
	public void setMismatchEmployee(String mismatchEmployee) {
		this.mismatchEmployee = mismatchEmployee;
	}
	public String getMismatchShift() {
		return mismatchShift;
	}
	public void setMismatchShift(String mismatchShift) {
		this.mismatchShift = mismatchShift;
	}
	
	
}
