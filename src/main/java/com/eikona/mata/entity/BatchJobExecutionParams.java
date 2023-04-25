//package com.eikona.mata.entity;
//
//import java.util.Date;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.transaction.Transactional;
//
//@Entity(name = "batch_job_execution_params")
////@Transactional(readOnly = true)
//public class BatchJobExecutionParams {
//	
////	@ManyToOne
////	@JoinColumn(name = "JOB_EXECUTION_ID")
//	@Id
//	private BatchJobExecution jobExecution; 
//	
//	@Column(name = "TYPE_CD")
//	private String  typeCd; 
//	
//	@Column(name = "KEY_NAME")
//	private String keyName;
//	
//	@Column(name = "STRING_VAL")
//	private String stringVal; 
//	
//	@Column(name = "DATE_VAL")
//	private  Date dateVal;
//	
//	@Column(name = "LONG_VAL")
//	private long longVal; 
//	
//	@Column(name = "DOUBLE_VAL")
//	private  Date doubleVal; 
//	
//	@Column(name = "IDENTIFYING")
//	private  String identifying;
//
//	public String getTypeCd() {
//		return typeCd;
//	}
//
//	public String getKeyName() {
//		return keyName;
//	}
//
//	public String getStringVal() {
//		return stringVal;
//	}
//
//	public Date getDateVal() {
//		return dateVal;
//	}
//
//	public long getLongVal() {
//		return longVal;
//	}
//
//	public Date getDoubleVal() {
//		return doubleVal;
//	}
//
//	public String getIdentifying() {
//		return identifying;
//	}
//
//
//	public BatchJobExecution getJobExecution() {
//		return jobExecution;
//	}
//
//	public void setJobExecution(BatchJobExecution jobExecution) {
//		this.jobExecution = jobExecution;
//	}
//
//	public void setTypeCd(String typeCd) {
//		this.typeCd = typeCd;
//	}
//
//	public void setKeyName(String keyName) {
//		this.keyName = keyName;
//	}
//
//	public void setStringVal(String stringVal) {
//		this.stringVal = stringVal;
//	}
//
//	public void setDateVal(Date dateVal) {
//		this.dateVal = dateVal;
//	}
//
//	public void setLongVal(long longVal) {
//		this.longVal = longVal;
//	}
//
//	public void setDoubleVal(Date doubleVal) {
//		this.doubleVal = doubleVal;
//	}
//
//	public void setIdentifying(String identifying) {
//		this.identifying = identifying;
//	}
//
//}
