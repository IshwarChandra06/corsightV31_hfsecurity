package com.eikona.mata.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.GenericGenerator;

@Entity(name="designation")
public class Designation extends Auditable<String> implements Serializable{

	private static final long serialVersionUID = 1L;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private Long id;
	
	@Column(name = "name")
	@NotBlank(message = "Please provide a valid name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@ManyToOne
	@JoinColumn(name="organization_id")
	private Organization organization;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Designation() {
		super();
	}

	public Designation(@NotBlank(message = "Please provide a valid name") String name, String description,
			Organization organization, boolean isDeleted) {
		//super();
		this.name = name;
		this.description = description;
		this.organization = organization;
		this.isDeleted = isDeleted;
	}

	public Designation(@NotBlank(message = "Please provide a valid name") String name, boolean isDeleted) {
		super();
		this.name = name;
		this.isDeleted = isDeleted;
	}
	
	
}
