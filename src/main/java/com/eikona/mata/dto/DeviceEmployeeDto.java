package com.eikona.mata.dto;

import java.util.Date;
import java.util.List;

import com.eikona.mata.entity.Area;

public class DeviceEmployeeDto {
	
	private Long empId;
	
	private String employeeId;
	
	private String designation;
	
	private String empName;
	
	private String branchName;
	
	private List<Area> area;
	
	private Date syncDate;

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public Long getEmpId() {
		return empId;
	}

	public void setEmpId(Long empId) {
		this.empId = empId;
	}

	public Date getSyncDate() {
		return syncDate;
	}

	public void setSyncDate(Date syncDate) {
		this.syncDate = syncDate;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public List<Area> getArea() {
		return area;
	}

	public void setArea(List<Area> area) {
		this.area = area;
	}
	

}
