package com.eikona.mata.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class FileSystemContent extends Auditable<String> implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	private Date created = new Date();
	private String summary;

	@ContentId private String contentId;
	@ContentLength private long contentLength;
	private String contentMimeType = "text/plain";
	private String contentPath;
	
	public FileSystemContent(FileSystemContent f) {
		this.name = f.getName();
		this.summary = f.getSummary();
		this.contentId = f.getContentId();
		this.contentLength = f.getContentLength();
		this.contentMimeType = f.getContentMimeType();
		this.contentPath = f.getContentPath();
	}
}