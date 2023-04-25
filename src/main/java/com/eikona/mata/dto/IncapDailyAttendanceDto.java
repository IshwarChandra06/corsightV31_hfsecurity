package com.eikona.mata.dto;

public class IncapDailyAttendanceDto {
	
	private String empId;
	private String empName;
	private String shift;
	private String firstInTime;
	private String firstOutTime;
	private String secondInTime;
	private String secondOutTime;
	
	private String firstHalf;
	private String secondHalf;
	
	private String workTime;
	private Long lateComing;
	private Long earlyGoing;
	private Long overTime;
	
	private String hourlyPaidLeave;
	private String hourlyUnPaidLeave;
	
	private String manEntry;
	private String reason;
	
	public String getEmpId() {
		return empId;
	}
	public String getEmpName() {
		return empName;
	}
	public String getShift() {
		return shift;
	}
	public String getFirstInTime() {
		return firstInTime;
	}
	public String getFirstOutTime() {
		return firstOutTime;
	}
	public String getSecondInTime() {
		return secondInTime;
	}
	public String getSecondOutTime() {
		return secondOutTime;
	}
	public String getFirstHalf() {
		return firstHalf;
	}
	public String getSecondHalf() {
		return secondHalf;
	}
	public String getWorkTime() {
		return workTime;
	}
	public Long getLateComing() {
		return lateComing;
	}
	public Long getEarlyGoing() {
		return earlyGoing;
	}
	public Long getOverTime() {
		return overTime;
	}
	public String getHourlyPaidLeave() {
		return hourlyPaidLeave;
	}
	public String getHourlyUnPaidLeave() {
		return hourlyUnPaidLeave;
	}
	public String getManEntry() {
		return manEntry;
	}
	public String getReason() {
		return reason;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	public void setShift(String shift) {
		this.shift = shift;
	}
	public void setFirstInTime(String firstInTime) {
		this.firstInTime = firstInTime;
	}
	public void setFirstOutTime(String firstOutTime) {
		this.firstOutTime = firstOutTime;
	}
	public void setSecondInTime(String secondInTime) {
		this.secondInTime = secondInTime;
	}
	public void setSecondOutTime(String secondOutTime) {
		this.secondOutTime = secondOutTime;
	}
	public void setFirstHalf(String firstHalf) {
		this.firstHalf = firstHalf;
	}
	public void setSecondHalf(String secondHalf) {
		this.secondHalf = secondHalf;
	}
	public void setWorkTime(String workTime) {
		this.workTime = workTime;
	}
	public void setLateComing(Long lateComing) {
		this.lateComing = lateComing;
	}
	public void setEarlyGoing(Long earlyGoing) {
		this.earlyGoing = earlyGoing;
	}
	public void setOverTime(Long overTime) {
		this.overTime = overTime;
	}
	public void setHourlyPaidLeave(String hourlyPaidLeave) {
		this.hourlyPaidLeave = hourlyPaidLeave;
	}
	public void setHourlyUnPaidLeave(String hourlyUnPaidLeave) {
		this.hourlyUnPaidLeave = hourlyUnPaidLeave;
	}
	public void setManEntry(String manEntry) {
		this.manEntry = manEntry;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
}
