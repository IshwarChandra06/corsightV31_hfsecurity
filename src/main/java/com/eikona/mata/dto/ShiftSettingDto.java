package com.eikona.mata.dto;


import com.eikona.mata.entity.Organization;

public class ShiftSettingDto {
	private String name;
	private String timeTableType;
	private Organization organization;
	private String checkInTime;
	private String checkOutTime;
	private String beforeGoingToWork;
	private String beforeGoingOffDuty;
	private String afterWork;
	private String afterDuty;
	private String allowLate;
	private String allowEarlyLeave;
	private String mustCheckIn;
	private String mustCheckOut;
	private String workDay;
	private String workTime;
	private String autoDeductBreakTime;
	private String onDuty;
	private String offDuty;
	private String enableFlexibleWork;
	public String getName() {
		return name;
	}
	
	
	public Organization getOrganization() {
		return organization;
	}


	public void setOrganization(Organization organization) {
		this.organization = organization;
	}


	public String getCheckOutTime() {
		return checkOutTime;
	}

	public void setCheckOutTime(String checkOutTime) {
		this.checkOutTime = checkOutTime;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getTimeTableType() {
		return timeTableType;
	}
	public void setTimeTableType(String timeTableType) {
		this.timeTableType = timeTableType;
	}
	public String getCheckInTime() {
		return checkInTime;
	}
	public void setCheckInTime(String checkInTime) {
		this.checkInTime = checkInTime;
	}
	public String getBeforeGoingToWork() {
		return beforeGoingToWork;
	}
	public void setBeforeGoingToWork(String beforeGoingToWork) {
		this.beforeGoingToWork = beforeGoingToWork;
	}
	public String getBeforeGoingOffDuty() {
		return beforeGoingOffDuty;
	}
	public void setBeforeGoingOffDuty(String beforeGoingOffDuty) {
		this.beforeGoingOffDuty = beforeGoingOffDuty;
	}
	public String getAfterWork() {
		return afterWork;
	}
	public void setAfterWork(String afterWork) {
		this.afterWork = afterWork;
	}
	public String getAfterDuty() {
		return afterDuty;
	}
	public void setAfterDuty(String afterDuty) {
		this.afterDuty = afterDuty;
	}
	public String getAllowLate() {
		return allowLate;
	}
	public void setAllowLate(String allowLate) {
		this.allowLate = allowLate;
	}
	public String getAllowEarlyLeave() {
		return allowEarlyLeave;
	}
	public void setAllowEarlyLeave(String allowEarlyLeave) {
		this.allowEarlyLeave = allowEarlyLeave;
	}
	public String getMustCheckIn() {
		return mustCheckIn;
	}
	public void setMustCheckIn(String mustCheckIn) {
		this.mustCheckIn = mustCheckIn;
	}
	public String getMustCheckOut() {
		return mustCheckOut;
	}
	public void setMustCheckOut(String mustCheckOut) {
		this.mustCheckOut = mustCheckOut;
	}
	public String getWorkDay() {
		return workDay;
	}
	public void setWorkDay(String workDay) {
		this.workDay = workDay;
	}
	public String getWorkTime() {
		return workTime;
	}
	public void setWorkTime(String workTime) {
		this.workTime = workTime;
	}
	public String getAutoDeductBreakTime() {
		return autoDeductBreakTime;
	}
	public void setAutoDeductBreakTime(String autoDeductBreakTime) {
		this.autoDeductBreakTime = autoDeductBreakTime;
	}
	public String getOnDuty() {
		return onDuty;
	}
	public void setOnDuty(String onDuty) {
		this.onDuty = onDuty;
	}
	public String getOffDuty() {
		return offDuty;
	}
	public void setOffDuty(String offDuty) {
		this.offDuty = offDuty;
	}
	public String getEnableFlexibleWork() {
		return enableFlexibleWork;
	}
	public void setEnableFlexibleWork(String enableFlexibleWork) {
		this.enableFlexibleWork = enableFlexibleWork;
	}
	
	
	


}
