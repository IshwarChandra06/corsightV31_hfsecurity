package com.eikona.mata.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "daily_report")
public class DailyAttendance implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private Long id;

	@Column(name = "emp_id")
	private String empId;

	@Column(name = "date")
	private Date date;

	@Column(name = "date_str")
	private String dateStr;

	@Column(name = "employee_name")
	private String employeeName;

	@Column(name = "department")
	private String department;

	@Column(name = "organization")
	private String organization;

	@Column(name = "designation")
	private String designation;
	
	@Column(name = "area")
	private String area;
	
	@Column(name = "emp_in_time")
	private String empInTime;

	@Column(name = "emp_out_time")
	private String empOutTime;

	@Column(name = "emp_in_temp")
	private String empInTemp;

	@Column(name = "emp_out_temp") 
	private String empOutTemp;

	@Column(name = "emp_in_mask")
	private String empInMask;

	@Column(name = "emp_out_mask")
	private String empOutMask;

	@Column(name = "emp_in_location")
	private String empInLocation;

	@Column(name = "emp_out_location")
	private String empOutLocation;

	@Column(name = "emp_in_access_type")
	private String empInAccessType;

	@Column(name = "emp_out_access_type")
	private String empOutAccessType;

	@Column(name = "missed_out_punch")
	private String missedOutPunch;
	
	@Column(name = "attendance_status")
	private String attendanceStatus;
	
	@Column(name = "city")
	private String city;

	@Column(name = "branch")
	private String branch;
	
	@Column(name = "shift")
	private String shift;

	@Column(name = "shift_in_time")
	private String shiftInTime;

	@Column(name = "shift_out_time")
	private String shiftOutTime;
	
	@Column(name = "over_time")
	private Long overTime;

	@Column(name = "early_coming")
	private Long earlyComing;

	@Column(name = "late_coming")
	private Long lateComing;

	@Column(name = "early_going")
	private Long earlyGoing;

	@Column(name = "late_going")
	private Long lateGoing;

	@Column(name = "work_time")
	private String workTime;
	
	@Column(name ="as_per_roster")
	private String asPerRoster;
	
	@Column(name = "first_half")
	private String firstHalf;
	
	@Column(name ="second_half")
	private String secondHalf;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDateStr() {
		return dateStr;
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getEmpInTime() {
		return empInTime;
	}

	public void setEmpInTime(String empInTime) {
		this.empInTime = empInTime;
	}

	public String getEmpOutTime() {
		return empOutTime;
	}

	public void setEmpOutTime(String empOutTime) {
		this.empOutTime = empOutTime;
	}

	public String getEmpInTemp() {
		return empInTemp;
	}

	public void setEmpInTemp(String empInTemp) {
		this.empInTemp = empInTemp;
	}

	public String getEmpOutTemp() {
		return empOutTemp;
	}

	public void setEmpOutTemp(String empOutTemp) {
		this.empOutTemp = empOutTemp;
	}


	public String getEmpInMask() {
		return empInMask;
	}

	public void setEmpInMask(String empInMask) {
		this.empInMask = empInMask;
	}

	public String getEmpOutMask() {
		return empOutMask;
	}

	public void setEmpOutMask(String empOutMask) {
		this.empOutMask = empOutMask;
	}

	public String getEmpInLocation() {
		return empInLocation;
	}

	public void setEmpInLocation(String empInLocation) {
		this.empInLocation = empInLocation;
	}

	public String getEmpOutLocation() {
		return empOutLocation;
	}

	public void setEmpOutLocation(String empOutLocation) {
		this.empOutLocation = empOutLocation;
	}

	public String getEmpInAccessType() {
		return empInAccessType;
	}

	public void setEmpInAccessType(String empInAccessType) {
		this.empInAccessType = empInAccessType;
	}

	public String getEmpOutAccessType() {
		return empOutAccessType;
	}

	public void setEmpOutAccessType(String empOutAccessType) {
		this.empOutAccessType = empOutAccessType;
	}

	public String getMissedOutPunch() {
		return missedOutPunch;
	}

	public void setMissedOutPunch(String missedOutPunch) {
		this.missedOutPunch = missedOutPunch;
	}

	public String getAttendanceStatus() {
		return attendanceStatus;
	}

	public void setAttendanceStatus(String attendanceStatus) {
		this.attendanceStatus = attendanceStatus;
	}

	public String getWorkTime() {
		return workTime;
	}

	public void setWorkTime(String workTime) {
		this.workTime = workTime;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getShift() {
		return shift;
	}

	public void setShift(String shift) {
		this.shift = shift;
	}

	public String getShiftInTime() {
		return shiftInTime;
	}

	public void setShiftInTime(String shiftInTime) {
		this.shiftInTime = shiftInTime;
	}

	public String getShiftOutTime() {
		return shiftOutTime;
	}

	public void setShiftOutTime(String shiftOutTime) {
		this.shiftOutTime = shiftOutTime;
	}

	public Long getOverTime() {
		return overTime;
	}

	public void setOverTime(Long overTime) {
		this.overTime = overTime;
	}

	public Long getEarlyComing() {
		return earlyComing;
	}

	public void setEarlyComing(Long earlyComing) {
		this.earlyComing = earlyComing;
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

	public Long getLateGoing() {
		return lateGoing;
	}

	public void setLateGoing(Long lateGoing) {
		this.lateGoing = lateGoing;
	}

	public String getAsPerRoster() {
		return asPerRoster;
	}

	public void setAsPerRoster(String asPerRoster) {
		this.asPerRoster = asPerRoster;
	}

	public String getFirstHalf() {
		return firstHalf;
	}

	public void setFirstHalf(String firstHalf) {
		this.firstHalf = firstHalf;
	}

	public String getSecondHalf() {
		return secondHalf;
	}

	public void setSecondHalf(String secondHalf) {
		this.secondHalf = secondHalf;
	}
	
	
}

