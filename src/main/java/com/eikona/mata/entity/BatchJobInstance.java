package com.eikona.mata.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "BATCH_JOB_INSTANCE")
public class BatchJobInstance {
	
	@Id
	@Column(name = "JOB_INSTANCE_ID")
	private long  jobInstanceId;
	
	@Column(name = "VERSION")
	private long  version; 
	
	@Column(name = "JOB_NAME")
	private String jobName;
	
	@Column(name = "JOB_KEY")
	private String  jobKey;

	public long getJobInstanceId() {
		return jobInstanceId;
	}

	public long getVersion() {
		return version;
	}

	public String getJobName() {
		return jobName;
	}

	public String getJobKey() {
		return jobKey;
	}

	public void setJobInstanceId(long jobInstanceId) {
		this.jobInstanceId = jobInstanceId;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public void setJobKey(String jobKey) {
		this.jobKey = jobKey;
	}
}
