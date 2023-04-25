package com.eikona.mata.entity;

import java.io.Serializable;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity(name="leave_type")
public class Leave implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "gender")
	private String gender;
	
	@Column(name = "maximum_allowed_days")
	private String maximumAllowedDays;
	
	@Column(name = "enable_pro_rata")
	private boolean enableProRata;
	
	@Column(name = "enable_leave_rounding")
	private boolean enableLeaveRounding;
	
	@Column(name = "credit_leave_in_multiples_of")
	private Double creditLeaveInMultiplesOf;
	
	@Column(name = "rounding_limit")
	private String roundingLimit;

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

	public String getMaximumAllowedDays() {
		return maximumAllowedDays;
	}

	public void setMaximumAllowedDays(String maximumAllowedDays) {
		this.maximumAllowedDays = maximumAllowedDays;
	}
	

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public boolean isEnableProRata() {
		return enableProRata;
	}

	public void setEnableProRata(boolean enableProRata) {
		this.enableProRata = enableProRata;
	}

	public boolean isEnableLeaveRounding() {
		return enableLeaveRounding;
	}

	public void setEnableLeaveRounding(boolean enableLeaveRounding) {
		this.enableLeaveRounding = enableLeaveRounding;
	}

	public Double getCreditLeaveInMultiplesOf() {
		return creditLeaveInMultiplesOf;
	}

	public void setCreditLeaveInMultiplesOf(Double creditLeaveInMultiplesOf) {
		this.creditLeaveInMultiplesOf = creditLeaveInMultiplesOf;
	}

	public String getRoundingLimit() {
		return roundingLimit;
	}

	public void setRoundingLimit(String roundingLimit) {
		this.roundingLimit = roundingLimit;
	}
	

}
