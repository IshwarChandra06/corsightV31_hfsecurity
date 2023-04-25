package com.eikona.mata.dto;

import java.util.Date;

public class OrganizationDto {

	private Date attendance_date;

	private String company;

	private String dateTime;

	private String timeStr;

	private String dateStr;

	private Long total;

	private String name;

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public Date getAttendance_date() {
		return attendance_date;
	}

	public void setAttendance_date(Date attendance_date) {
		this.attendance_date = attendance_date;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDateStr() {
		return dateStr;
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}

	public String getTimeStr() {
		return timeStr;
	}

	public void setTimeStr(String timeStr) {
		this.timeStr = timeStr;
	}

	public OrganizationDto() {
		super();
	}

	public OrganizationDto(Date attendance_date, String att_date, String company, Long total) {
		super();
		this.attendance_date = attendance_date;
		this.timeStr = att_date;
		this.company = company;
		this.total = total;
	}

	public OrganizationDto(Date attendance_date, String company, Long total) {
		super();
		this.attendance_date = attendance_date;
		this.company = company;
		this.total = total;
	}

	public OrganizationDto(String dateStr, String company, Long total) {
		this.timeStr = dateStr;
		this.company = company;
		this.total = total;
	}

	public OrganizationDto(Date attendance_date, Long total) {
		super();
		this.attendance_date = attendance_date;
		this.total = total;
	}

	public OrganizationDto(String dateStr, Long total) {
		super();
		this.dateStr = dateStr;
		this.total = total;
	}

}
