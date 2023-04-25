package com.eikona.mata.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "BATCH_JOB_EXECUTION")
public class BatchJobExecution {

	@Id
	@Column(name = "JOB_EXECUTION_ID")
	private long jobExecutionId;
	
	@Column(name = "VERSION")
	private long version;
	
	@ManyToOne
	@JoinColumn(name = "JOB_INSTANCE_ID")
	private BatchJobInstance jobInstance;
	
	@Column(name = "CREATE_TIME")
	private Date createTime;
	
	@Column(name = "START_TIME")
	private Date startTime;
	
	@Column(name = "END_TIME")
	private Date endTime; 
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "EXIT_CODE")
	private String exitCode; 
	
	@Column(name = "EXIT_MESSAGE")
	private String exitMessage;
	
	@Column(name = "LAST_UPDATED")
	private Date lastUpdate; 
	
	@Column(name = "JOB_CONFIGURATION_LOCATION")
	private String jobConfigurationLocation;

	public long getJobExecutionId() {
		return jobExecutionId;
	}

	public long getVersion() {
		return version;
	}

	public BatchJobInstance getJobInstanceId() {
		return jobInstance;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public String getStatus() {
		return status;
	}

	public String getExitCode() {
		return exitCode;
	}

	public String getExitMessage() {
		return exitMessage;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public String getJobConfigurationLocation() {
		return jobConfigurationLocation;
	}

	public void setJobExecutionId(long jobExecutionId) {
		this.jobExecutionId = jobExecutionId;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public void setJobInstanceId(BatchJobInstance jobInstanceId) {
		this.jobInstance = jobInstanceId;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setExitCode(String exitCode) {
		this.exitCode = exitCode;
	}

	public void setExitMessage(String exitMessage) {
		this.exitMessage = exitMessage;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public void setJobConfigurationLocation(String jobConfigurationLocation) {
		this.jobConfigurationLocation = jobConfigurationLocation;
	}
	
}
