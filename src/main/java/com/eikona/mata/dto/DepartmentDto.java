package com.eikona.mata.dto;

public class DepartmentDto {
	
	private long totalEmployee;
	private long presentEmployee;
	private String departmentName;
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
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	
	public DepartmentDto() {
		super();
	}
	
	public DepartmentDto(String departmentName, long presentEmployee) {
		super();
		this.presentEmployee = presentEmployee;
		this.departmentName = departmentName;
	}
	
}
