package com.eikona.mata.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "BATCH_STEP_EXECUTION")
public class BatchStepExecution {
	
	
	@Id
	@Column(name = "STEP_EXECUTION_ID")
	private long stepExecutionId;
	
	@Column(name = "VERSION")
	private long version; 
	
	@ManyToOne
	@JoinColumn(name = "JOB_EXECUTION_ID")
	private BatchJobExecution jobExecution;
	
	@Column(name = "STEP_NAME")
	private String  stepName;
	
	@Column(name = "START_TIME")
	private  Date startTime; 
	
	@Column(name = "END_TIME")
	private Date endTime; 
	
	@Column(name = "STATUS")
	private String  status;
	
	@Column(name = "COMMIT_COUNT")
	private long commitCount;
	
	@Column(name = "READ_COUNT")
	private long  readCount; 
	
	@Column(name = "FILTER_COUNT")
	private long filterCount; 
	
	@Column(name = "WRITE_COUNT")
	private long writeCount; 
	
	@Column(name = "READ_SKIP_COUNT")
	private long readSkipCount; 
	
	@Column(name = "WRITE_SKIP_COUNT")
	private long writeSkipCount; 
	
	@Column(name = "PROCESS_SKIP_COUNT")
	private long processSkipCount; 
	
	@Column(name = "ROLLBACK_COUNT")
	private long rollbackCount; 
	
	@Column(name = "EXIT_CODE")
	private String exitCode;
	
	@Column(name = "EXIT_MESSAGE")
	private String exitMessage;
	
	@Column(name = "LAST_UPDATED")
	private Date lastUpdate;

	public long getStepExecutionId() {
		return stepExecutionId;
	}

	public long getVersion() {
		return version;
	}

	
	public String getStepName() {
		return stepName;
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

	public long getCommitCount() {
		return commitCount;
	}

	public long getReadCount() {
		return readCount;
	}

	public long getFilterCount() {
		return filterCount;
	}

	public long getWriteCount() {
		return writeCount;
	}

	public long getReadSkipCount() {
		return readSkipCount;
	}

	public long getWriteSkipCount() {
		return writeSkipCount;
	}

	public long getProcessSkipCount() {
		return processSkipCount;
	}

	public long getRollbackCount() {
		return rollbackCount;
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

	public void setStepExecutionId(long stepExecutionId) {
		this.stepExecutionId = stepExecutionId;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
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

	public void setCommitCount(long commitCount) {
		this.commitCount = commitCount;
	}

	public void setReadCount(long readCount) {
		this.readCount = readCount;
	}

	public void setFilterCount(long filterCount) {
		this.filterCount = filterCount;
	}

	public void setWriteCount(long writeCount) {
		this.writeCount = writeCount;
	}

	public void setReadSkipCount(long readSkipCount) {
		this.readSkipCount = readSkipCount;
	}

	public void setWriteSkipCount(long writeSkipCount) {
		this.writeSkipCount = writeSkipCount;
	}

	public void setProcessSkipCount(long processSkipCount) {
		this.processSkipCount = processSkipCount;
	}

	public void setRollbackCount(long rollbackCount) {
		this.rollbackCount = rollbackCount;
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

	public BatchJobExecution getJobExecution() {
		return jobExecution;
	}

	public void setJobExecution(BatchJobExecution jobExecution) {
		this.jobExecution = jobExecution;
	}
	
	
}
