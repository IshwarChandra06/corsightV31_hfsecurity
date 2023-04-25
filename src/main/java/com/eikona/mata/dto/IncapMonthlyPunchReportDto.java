package com.eikona.mata.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IncapMonthlyPunchReportDto {

	private String empId;
	private String empName;
	private String department;
	private String designation;
	private List<String> shiftList;
	private List<String> shiftListValue;
	private List<String> dateList;
	private List<String> dataList;
	
	private Map<String, List<String>> dataMapList;

	public String getEmpId() {
		return empId;
	}

	public String getEmpName() {
		return empName;
	}

	public String getDepartment() {
		return department;
	}

	public String getDesignation() {
		return designation;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public List<String> getShiftList() {

		shiftList = new ArrayList<String>();
		shiftList.add("PR");
		shiftList.add("WO");
		shiftList.add("PH");
		shiftList.add("PL");
		shiftList.add("TR");
		shiftList.add("AB");
		shiftList.add("UL");
		shiftList.add("FB");
		shiftList.add("RD");
		shiftList.add("LI");
		shiftList.add("EO");
		shiftList.add("OT");
		shiftList.add("WrkHrs");
		return shiftList;
	}

	public void setShiftList(List<String> shiftList) {
		this.shiftList = shiftList;
	}
	
	public List<String> getShiftListValue() {
		return shiftListValue;
	}

	public void setShiftListValue(List<String> shiftListValue) {
		this.shiftListValue = shiftListValue;
	}

	

	public List<String> getDateList() {
		return dateList;
	}

	public void setDateList(List<String> dateList) {
		this.dateList = dateList;
	}

	public List<String> getDataList() {
		return dataList;
	}

	public void setDataList(List<String> dataList) {
		this.dataList = dataList;
	}

	public Map<String, List<String>> getDataMapList() {
		return dataMapList;
	}

	public void setDataMapList(Map<String, List<String>> dataMapList) {
		this.dataMapList = dataMapList;
	}
	
}
