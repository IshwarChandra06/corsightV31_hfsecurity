package com.eikona.mata.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "BATCH_JOB_EXECUTION_CONTEXT")
public class BatchJobExecutionContext {
	
	@Id
	@Column(name = "JOB_EXECUTION_ID")
	private long  jobExecutionId;
	
	@Column(name = "SHORT_CONTEXT")
	private String shortContext; 
	
	@Column(name = "SERIALIZED_CONTEXT")
	private  String serializedContext;

	public long getJobExecutionId() {
		return jobExecutionId;
	}

	public String getShortContext() {
		return shortContext;
	}

	public String getSerializedContext() {
		return serializedContext;
	}

	public void setJobExecutionId(long jobExecutionId) {
		this.jobExecutionId = jobExecutionId;
	}

	public void setShortContext(String shortContext) {
		this.shortContext = shortContext;
	}

	public void setSerializedContext(String serializedContext) {
		this.serializedContext = serializedContext;
	}

}
