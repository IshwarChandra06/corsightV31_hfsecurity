package com.eikona.mata.entity;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

@Entity(name="et_parameter")
public class Parameter extends Auditable<String> implements Serializable {
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
	private String name;
	
	@Column(name = "value")
	private String value;
	
	@ManyToOne
	@JoinColumn(name="organization_id", nullable = true)
	private Organization organization;
	
	@Column(name = "is_deleted")
    private boolean isDeleted;
	

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Parameter() {
		super();
	}

	public Parameter(String name, String value, boolean isDeleted) {
		super();
		this.name = name;
		this.value = value;
		this.isDeleted = isDeleted;
	}
	
	

}
