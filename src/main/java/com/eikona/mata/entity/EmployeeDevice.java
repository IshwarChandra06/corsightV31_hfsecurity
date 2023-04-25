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

@Entity(name="employee_device")
public class EmployeeDevice extends Auditable<String> implements Serializable{

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
	@JoinColumn(name="employee_id")
	private Employee employee;
	
	@ManyToOne
	@JoinColumn(name="device_id")
	private Device device;
	
	@Column(name="last_sync")
	private Date lastSync;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Date getLastSync() {
		return lastSync;
	}

	public void setLastSync(Date lastSync) {
		this.lastSync = lastSync;
	}
	
	
}
