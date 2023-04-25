package com.eikona.mata.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

@Entity(name="employee_leave_association")
public class EmployeeLeaveAssociation implements Serializable{
		
	private static final long serialVersionUID = 1L;
		
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "employee_id")
	private Employee employee;
	
	@ManyToOne
	@JoinColumn(name = "leave_id")
	private Leave leave;
	
	@Column
	private Date date;
	
	@Column(name = "shift_wise_leave_selection")
	private String shiftWiseleaveSelection;
	
	@Column(name = "applied_days")
	private String appliedDays;
	
	@Column(name = "posted_days")
	private String postedDays;
	
	@Column(name = "current_balance")
	private String currentBalance;
	
	@Column(name = "reason")
	private String reason;
	
	@Column(name = "contact_no")
	private String contactNo;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "medical_certificate")
	private String medicalCertificate;
	
	@Column(name = "pending_leave")
	private boolean pendingLeave;
	
	@Column(name = "approve_or_reject")
	private String approveOrReject;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Leave getLeave() {
		return leave;
	}

	public void setLeave(Leave leave) {
		this.leave = leave;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}


	public String getShiftWiseleaveSelection() {
		return shiftWiseleaveSelection;
	}

	public void setShiftWiseleaveSelection(String shiftWiseleaveSelection) {
		this.shiftWiseleaveSelection = shiftWiseleaveSelection;
	}

	public String getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(String currentBalance) {
		this.currentBalance = currentBalance;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMedicalCertificate() {
		return medicalCertificate;
	}

	public void setMedicalCertificate(String medicalCertificate) {
		this.medicalCertificate = medicalCertificate;
	}

	public boolean isPendingLeave() {
		return pendingLeave;
	}

	public void setPendingLeave(boolean pendingLeave) {
		this.pendingLeave = pendingLeave;
	}

	public String getApproveOrReject() {
		return approveOrReject;
	}

	public void setApproveOrReject(String approveOrReject) {
		this.approveOrReject = approveOrReject;
	}

	public String getAppliedDays() {
		return appliedDays;
	}

	public void setAppliedDays(String appliedDays) {
		this.appliedDays = appliedDays;
	}

	public String getPostedDays() {
		return postedDays;
	}

	public void setPostedDays(String postedDays) {
		this.postedDays = postedDays;
	}
	
	
	
	

}
