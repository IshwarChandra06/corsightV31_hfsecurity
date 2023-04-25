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
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

@Entity(name="shift")
public class Shift extends Auditable<String> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private Long id;
	
	@Column(name = "name")
	@NotBlank(message = "Please provide a valid name")
	private String name;
	
	@Column(name = "start_time")
	@DateTimeFormat(pattern="HH:mm:ss")
	private Date startTime;
	
	@DateTimeFormat(pattern="HH:mm:ss")
	@Column(name = "end_time")	
	private Date endTime;
	
	@Column(name = "last_sync_date")	
	private Date lastSyncDate;
	
	@ManyToOne
	@JoinColumn(name="organization_id",nullable = true)
	private Organization organization;
	
	@Column(name = "shift_end_next_day")
	private boolean shiftEndNextDay;
	
	@Column(name = "is_deleted")
    private boolean isDeleted;
	
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	
	
	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	public boolean isShiftEndNextDay() {
		return shiftEndNextDay;
	}

	public void setShiftEndNextDay(boolean shiftEndNextDay) {
		this.shiftEndNextDay = shiftEndNextDay;
	}
	
	

	public Date getLastSyncDate() {
		return lastSyncDate;
	}

	public void setLastSyncDate(Date lastSyncDate) {
		this.lastSyncDate = lastSyncDate;
	}

	public Shift() {
		super();
	}

	public Shift(@NotBlank(message = "Please provide a valid name") String name, boolean isDeleted) {
		super();
		this.name = name;
		this.isDeleted = isDeleted;
	}

	public Shift(@NotBlank(message = "Please provide a valid name") String name, Date startTime, Date endTime,
			Organization organization) {
		super();
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.organization = organization;
	}


	
	
}
