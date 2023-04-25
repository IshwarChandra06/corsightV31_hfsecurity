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
@Entity(name="et_email_setup")
public class EmailSetup extends Auditable<String> implements Serializable{

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
	@JoinColumn(name="organization_id", nullable = false)
	private Organization organization;
	
	@Column(name = "smpp_server")
	private String smppServer;
	
	@Column(name = "user_name")
	private String userName;
	
	@Column(name = "password")
	private String Password;
	
	@Column(name = "port_no")
    private String portNo;
    
	@Column(name = "sender_name")
    private String senderName;
    
	@Column(name = "ssl_bit")
    private boolean ssl;
    
	@Column(name = "tls_bit")
    private boolean tls;
    
    private boolean isDeleted;

    private String sslStr;
    private String tlsStr;
    


	

	public String getSslStr() {
		return sslStr;
	}

	public void setSslStr(String sslStr) {
		this.sslStr = sslStr;
	}

	public String getTlsStr() {
		return tlsStr;
	}

	public void setTlsStr(String tlsStr) {
		this.tlsStr = tlsStr;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public String getSmppServer() {
		return smppServer;
	}

	public void setSmppServer(String smppServer) {
		this.smppServer = smppServer;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public String getPortNo() {
		return portNo;
	}

	public void setPortNo(String portNo) {
		this.portNo = portNo;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}


	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		if(this.getSslStr()=="Yes") {
			this.ssl = true;
		}
		else
		{
			this.ssl = false;
		}
		
	}

	public boolean isTls() {
		return tls;
	}

	public void setTls(boolean tls) {
		if(this.getTlsStr()=="Yes") {
			this.tls = true;
		}
		else
		{
			this.tls = false;
		}
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public EmailSetup() {
		super();
	}

	public EmailSetup(Organization organization,String userName, String password, String portNo, boolean isDeleted) {
		super();
		this.organization=organization;
		this.userName = userName;
		this.Password = password;
		this.portNo = portNo;
		this.isDeleted = isDeleted;
	}


    
    
    
    

}
