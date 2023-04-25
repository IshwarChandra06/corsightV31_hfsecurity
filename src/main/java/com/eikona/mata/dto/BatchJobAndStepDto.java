package com.eikona.mata.dto;

import java.util.Date;

public class BatchJobAndStepDto {

	private Date startTime;
	private Date endTime;
	private String status;
	private String exitCode;
	private String exitMessage;
	private String jobName;

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

	public String getJobName() {
		return jobName;
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

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public BatchJobAndStepDto(Date startTime, Date endTime, String status, String exitCode, String exitMessage,
			String jobName) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status;
		this.exitCode = exitCode;
		this.exitMessage = exitMessage;
		this.jobName = jobName;
	}
	
	
}
