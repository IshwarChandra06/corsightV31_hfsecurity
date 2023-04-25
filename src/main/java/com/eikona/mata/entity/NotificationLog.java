package com.eikona.mata.entity;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "notification_log")
public class NotificationLog implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Id
		@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
		@GenericGenerator(name = "native", strategy = "native")
		@Column(name = "id")
		private Long id;
		
		@Column(name = "notification_type")
		private String notificationType;
		
		@Column(name = "sender")
		private String sender;
		
		@Column(name = "subject")
		private String subject;
		
		@Column(name = "report_type")
		private String reportType;
		
		@Column(name = "receipents")
		private String receipents;
		
		@Column(name = "date_and_time")
		private Date dateAndTime;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getNotificationType() {
			return notificationType;
		}

		public void setNotificationType(String notificationType) {
			this.notificationType = notificationType;
		}

		public String getSender() {
			return sender;
		}

		public void setSender(String sender) {
			this.sender = sender;
		}

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public String getReportType() {
			return reportType;
		}

		public void setReportType(String reportType) {
			this.reportType = reportType;
		}

		public String getReceipents() {
			return receipents;
		}

		public void setReceipents(String receipents) {
			this.receipents = receipents;
		}

		public Date getDateAndTime() {
			return dateAndTime;
		}

		public void setDateAndTime(Date dateAndTime) {
			this.dateAndTime = dateAndTime;
		}
		
		

}
