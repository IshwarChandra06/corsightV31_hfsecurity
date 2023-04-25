package com.eikona.mata.dto;

import java.util.List;

public class IncapMonthlyAttendanceDto {
	
	private String empId;
	private String empName;
	private String organization;
	private String department;
	private String designation;
	private String totalPresentCount;
	private String totalAbsentCount;
	private String totalWeekOff;
	private String totalOverTime;
	private String totalLateIn;
	private String totalEarlyOut;
	
	private String payableDays;
	private String totalDays;

	private List<String> dateList;

	public String getEmpId() {
		return empId;
	}

	public String getEmpName() {
		return empName;
	}

	public String getOrganization() {
		return organization;
	}

	public String getDepartment() {
		return department;
	}

	public String getDesignation() {
		return designation;
	}

	public String getTotalPresentCount() {
		return totalPresentCount;
	}

	public String getTotalAbsentCount() {
		return totalAbsentCount;
	}

	public String getTotalWeekOff() {
		return totalWeekOff;
	}

	public String getTotalOverTime() {
		return totalOverTime;
	}

	public String getTotalLateIn() {
		return totalLateIn;
	}

	public String getTotalEarlyOut() {
		return totalEarlyOut;
	}

	public String getPayableDays() {
		return payableDays;
	}

	public String getTotalDays() {
		return totalDays;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public void setTotalPresentCount(String totalPresentCount) {
		this.totalPresentCount = totalPresentCount;
	}

	public void setTotalAbsentCount(String totalAbsentCount) {
		this.totalAbsentCount = totalAbsentCount;
	}

	public void setTotalWeekOff(String totalWeekOff) {
		this.totalWeekOff = totalWeekOff;
	}

	public void setTotalOverTime(String totalOverTime) {
		this.totalOverTime = totalOverTime;
	}

	public void setTotalLateIn(String totalLateIn) {
		this.totalLateIn = totalLateIn;
	}

	public void setTotalEarlyOut(String totalEarlyOut) {
		this.totalEarlyOut = totalEarlyOut;
	}

	public void setPayableDays(String payableDays) {
		this.payableDays = payableDays;
	}

	public void setTotalDays(String totalDays) {
		this.totalDays = totalDays;
	}

	public List<String> getDateList() {
		return dateList;
	}

	public void setDateList(List<String> dateList) {
		this.dateList = dateList;
	}
	
}
