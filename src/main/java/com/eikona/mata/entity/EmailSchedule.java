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

@Entity(name="email_schedule")
public class EmailSchedule extends Auditable<String> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="organization_id", nullable = false)
	private Organization organization;
	
	@Column(name = "report_type")
	private String reportType;
	
	@Column(name = "file_name")
	private String fileName;
	
	@Column(name = "file_type")
	private String fileType;
	
	@Column(name = "send_timing")
	private Date sendTiming;
	
	@Column(name = "to_email_address")
	private String toEmailAddress;
	
	@Column(name = "email_subject")
	private String emailSubject;
	
	@Column(name = "email_body")
	private String emailBody;
	
	@Column(name = "is_deleted")
    private boolean isDeleted;

	
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}


	public Date getSendTiming() {
		return sendTiming;
	}

	public void setSendTiming(Date sendTiming) {
		this.sendTiming = sendTiming;
	}

	public String getToEmailAddress() {
		return toEmailAddress;
	}

	public void setToEmailAddress(String toEmailAddress) {
		this.toEmailAddress = toEmailAddress;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getEmailBody() {
		return emailBody;
	}

	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public EmailSchedule(Organization organization, String reportType, String fileType, boolean isDeleted) {
		super();
		this.organization = organization;
		this.reportType = reportType;
		this.fileType = fileType;
		this.isDeleted = isDeleted;
	}

	public EmailSchedule() {
		super();
	}
	
	
	
}
