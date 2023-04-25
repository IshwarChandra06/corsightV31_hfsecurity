package com.eikona.mata.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "BATCH_STEP_EXECUTION_SEQ")
public class BatchStepExecutionSeq {
	
	@Column(name = "ID")
	private long Id;
	
	@Id
	@Column(name = "UNIQUE_KEY")
	private String unique_key;

	public long getId() {
		return Id;
	}

	public String getUnique_key() {
		return unique_key;
	}

	public void setId(long id) {
		Id = id;
	}

	public void setUnique_key(String unique_key) {
		this.unique_key = unique_key;
	}
	
}
