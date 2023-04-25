package com.eikona.mata.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "et_image")
public class Image extends Auditable<String> implements Serializable {
	
	private static final long serialVersionUID = 1L; 
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private Long id;
	
	@ManyToMany
    private List<Employee> employee;
	
	@Column(name = "thumbnail_path")
	private String thumbnailPath; //100*100
	
	@Column(name = "resize_path")
	private String resizePath; //300*300
	
	@Column(name = "original_path")
	private String originalPath; //orginal
	
	@Column(name = "face_id")
	private String faceId;
	
	@Column(name = "is_deleted")
	private boolean isDeleted;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Employee> getEmployee() {
		return employee;
	}

	public void setEmployee(List<Employee> employee) {
		this.employee = employee;
	}

	public String getThumbnailPath() {
		return thumbnailPath;
	}

	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}

	public String getResizePath() {
		return resizePath;
	}

	public void setResizePath(String resizePath) {
		this.resizePath = resizePath;
	}

	public String getOriginalPath() {
		return originalPath;
	}

	public void setOriginalPath(String originalPath) {
		this.originalPath = originalPath;
	}

	public String getFaceId() {
		return faceId;
	}

	public void setFaceId(String faceId) {
		this.faceId = faceId;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	
}
