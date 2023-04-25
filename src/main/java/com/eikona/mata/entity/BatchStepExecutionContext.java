package com.eikona.mata.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "BATCH_STEP_EXECUTION_CONTEXT")
public class BatchStepExecutionContext {
	
	@Id
	@Column(name = "STEP_EXECUTION_ID")
	private long stepExecutionId;
	
	@Column(name = "SHORT_CONTEXT")
	private String shortContext;
	
	@Column(name = "SERIALIZED_CONTEXT")
	private String serializedContext;

	public long getStepExecutionId() {
		return stepExecutionId;
	}

	public String getShortContext() {
		return shortContext;
	}

	public String getSerializedContext() {
		return serializedContext;
	}

	public void setStepExecutionId(long stepExecutionId) {
		this.stepExecutionId = stepExecutionId;
	}

	public void setShortContext(String shortContext) {
		this.shortContext = shortContext;
	}

	public void setSerializedContext(String serializedContext) {
		this.serializedContext = serializedContext;
	}
	
}