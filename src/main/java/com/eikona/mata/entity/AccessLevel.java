package com.eikona.mata.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;


@Entity(name = "access_level")
public class AccessLevel extends Auditable<String> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private Long id;
	
	@Column(name = "access_id")
	private String accessId;
	
	@Column(name = "name")
	private String name;

	public Long getId() {
		return id;
	}

	public String getAccessId() {
		return accessId;
	}

	public String getName() {
		return name;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
