package com.eikona.mata.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExceptionSummaryDto {
	
	private Date attendance_date;
	
	private String att_date;
	
	private String company;

	private String department;

	private Long empPresent;
	
	private Long inNotWaringMask;
	
	private Long outNotWaringMask;
	
	private Long missedOutPunch;
	
	private Long overTime;
	
	private Long lessTime;
	
	private Long lateComing;
	
	private Long earlyGoing;
	
	private Long inAbnormalTemp;
	
	private Long outAbnormalTemp;
	
	public ExceptionSummaryDto(String company, Date attendance_date, Long empPresent,
			Long inNotWaringMask, Long outNotWaringMask, Long missedOutPunch, Long overTime, Long lessTime,
			Long lateComing, Long earlyGoing, Long inAbnormalTemp, Long outAbnormalTemp) {
		super();
		this.attendance_date = attendance_date;
		this.company = company;
		this.empPresent = empPresent;
		this.inNotWaringMask = inNotWaringMask;
		this.outNotWaringMask = outNotWaringMask;
		this.missedOutPunch = missedOutPunch;
		this.overTime = overTime;
		this.lessTime = lessTime;
		this.lateComing = lateComing;
		this.earlyGoing = earlyGoing;
		this.inAbnormalTemp = inAbnormalTemp;
		this.outAbnormalTemp = outAbnormalTemp;
	}

	public Date getAttendance_date() {
		return attendance_date;
	}

	public void setAttendance_date(Date attendance_date) {
		this.attendance_date = attendance_date;
	}

	public String getAtt_date() {
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		if(null!=attendance_date) {
			this.att_date = df.format(attendance_date);
		}
		return att_date;
	}

	public void setAtt_date(String att_date) {
		this.att_date = att_date;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Long getEmpPresent() {
		return empPresent;
	}

	public void setEmpPresent(Long empPresent) {
		this.empPresent = empPresent;
	}

	public Long getInNotWaringMask() {
		return inNotWaringMask;
	}

	public void setInNotWaringMask(Long inNotWaringMask) {
		this.inNotWaringMask = inNotWaringMask;
	}

	public Long getOutNotWaringMask() {
		return outNotWaringMask;
	}

	public void setOutNotWaringMask(Long outNotWaringMask) {
		this.outNotWaringMask = outNotWaringMask;
	}

	public Long getMissedOutPunch() {
		return missedOutPunch;
	}

	public void setMissedOutPunch(Long missedOutPunch) {
		this.missedOutPunch = missedOutPunch;
	}

	public Long getOverTime() {
		return overTime;
	}

	public void setOverTime(Long overTime) {
		this.overTime = overTime;
	}

	public Long getLessTime() {
		return lessTime;
	}

	public void setLessTime(Long lessTime) {
		this.lessTime = lessTime;
	}

	public Long getLateComing() {
		return lateComing;
	}

	public void setLateComing(Long lateComing) {
		this.lateComing = lateComing;
	}

	public Long getEarlyGoing() {
		return earlyGoing;
	}

	public void setEarlyGoing(Long earlyGoing) {
		this.earlyGoing = earlyGoing;
	}

	public Long getInAbnormalTemp() {
		return inAbnormalTemp;
	}

	public void setInAbnormalTemp(Long inAbnormalTemp) {
		this.inAbnormalTemp = inAbnormalTemp;
	}

	public Long getOutAbnormalTemp() {
		return outAbnormalTemp;
	}

	public void setOutAbnormalTemp(Long outAbnormalTemp) {
		this.outAbnormalTemp = outAbnormalTemp;
	}

}

