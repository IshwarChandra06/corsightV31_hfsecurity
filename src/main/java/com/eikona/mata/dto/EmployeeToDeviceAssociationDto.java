package com.eikona.mata.dto;

import java.util.Date;

public class EmployeeToDeviceAssociationDto {
	
	private Long employeeId;
	private Long deviceId;
	private String branchName;
	private String deviceName;
	private String areaName;
	private String empName;
	
	private Date syncDate;
	
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public Long getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	
	public String getEmpName() {
		return empName;
	}
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	public Date getSyncDate() {
		return syncDate;
	}
	public void setSyncDate(Date syncDate) {
		this.syncDate = syncDate;
	}
	
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public EmployeeToDeviceAssociationDto() {
		super();
	}
	public EmployeeToDeviceAssociationDto(Long employeeId, Long deviceId, String deviceName, String areaName) {
		super();
		this.employeeId = employeeId;
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.areaName = areaName;
	}
}
