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

@Entity(name="transaction")
public class Transaction implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private Long id;
	
	@Column
	private String empId;
	
	@ManyToOne
	@JoinColumn(name="employee_id")
	private Employee employee;
	
	@Column
	private String employeeCode;

	@Column
	private String name;
	
	@Column
	private String department;
	
	@Column
	private String designation;
	
	@Column
	private String employeeType;
	
	@Column
	private String contractor;
	
	@Column
	private String area;
	
	@Column
	private String branch;
	
	@Column
	private String organization;

	@Column
	private String deviceId;
	
	@Column
	private Integer logId;
	
	@Column
	private String deviceName;

	@Column
	private String serialNo;
	
	@Column
	private Boolean wearingMask;
	
	@Column
	private String temperature;
	
	@Column
	private String accessType;
	
	@Column
	private Date punchDate;
	
	@Column
	private Date punchTime;
	
	@Column
	private String punchDateStr;
	
	@Column
	private String punchTimeStr;
	
	@Column
	private String deviceType;
	
	@Column
	private String longitude;
	
	@Column
	private String latitude;
	
	@Column
	private String gpsLocation;
	
	@Column
	private String watchlistName;
	
	@Column
	private String watchlistId;
	
	@Column
	private String eventId;
	
	@Column
	private String appearanceId;
	
	@Column
	private Double poiConfidence;
	
	@Column
	private String poiId;
	
	@Column
	private String maskStatus;
	
	@Column
	private Double maskedScore;
	
	@Column
	private boolean isSync;
	
	@Column
	private boolean isFailed;
	
	@Column
	private String age;
	
	@Column
	private String gender;
	
	@Column
	private String searchScore;
	
	@Column
	private String livenessScore;
	
	@Column
	private String cropimagePath;
	
	@Column
	private byte[] cropImageByte;
	
	@Column
	private Long totalCount;
	
	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getPunchDateStr() {
		return punchDateStr;
	}

	public void setPunchDateStr(String punchDateStr) {
		this.punchDateStr = punchDateStr;
	}

	public String getSearchScore() {
		return searchScore;
	}

	public void setSearchScore(String searchScore) {
		this.searchScore = searchScore;
	}

	public String getLivenessScore() {
		return livenessScore;
	}

	public void setLivenessScore(String livenessScore) {
		this.livenessScore = livenessScore;
	}

	public String getPunchTimeStr() {
		
			return punchTimeStr;
	}

	public void setPunchTimeStr(String punchTimeStr) {
		this.punchTimeStr = punchTimeStr;
	}

	public String getContractor() {
		return contractor;
	}

	public void setContractor(String contractor) {
		this.contractor = contractor;
	}

	public Boolean getWearingMask() {
		return wearingMask;
	}

	public Long getId() {
		return id;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	
	public Boolean isWearingMask() {
		return wearingMask;
	}

	public void setWearingMask(Boolean wearingMask) {
		this.wearingMask = wearingMask;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public Date getPunchTime() {
		return punchTime;
	}

	public void setPunchTime(Date punchTime) {
		this.punchTime = punchTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getEmployeeType() {
		return employeeType;
	}

	public void setEmployeeType(String employeeType) {
		this.employeeType = employeeType;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public Date getPunchDate() {
		return punchDate;
	}

	public void setPunchDate(Date punchDate) {
		this.punchDate = punchDate;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	
	
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}
	
	public Integer getLogId() {
		return logId;
	}

	public void setLogId(Integer logId) {
		this.logId = logId;
	}

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getGpsLocation() {
		return gpsLocation;
	}

	public void setGpsLocation(String gpsLocation) {
		this.gpsLocation = gpsLocation;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getWatchlistName() {
		return watchlistName;
	}

	public void setWatchlistName(String watchlistName) {
		this.watchlistName = watchlistName;
	}

	public String getWatchlistId() {
		return watchlistId;
	}

	public void setWatchlistId(String watchlistId) {
		this.watchlistId = watchlistId;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getAppearanceId() {
		return appearanceId;
	}

	public void setAppearanceId(String appearanceId) {
		this.appearanceId = appearanceId;
	}


	public String getPoiId() {
		return poiId;
	}

	public void setPoiId(String poiId) {
		this.poiId = poiId;
	}

	public Double getPoiConfidence() {
		return poiConfidence;
	}

	public void setPoiConfidence(Double poiConfidence) {
		this.poiConfidence = poiConfidence;
	}

	public Double getMaskedScore() {
		return maskedScore;
	}

	public void setMaskedScore(Double maskedScore) {
		this.maskedScore = maskedScore;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getMaskStatus() {
		return maskStatus;
	}

	public void setMaskStatus(String maskStatus) {
		this.maskStatus = maskStatus;
	}

	public boolean isSync() {
		return isSync;
	}

	public void setSync(boolean isSync) {
		this.isSync = isSync;
	}

	public boolean isFailed() {
		return isFailed;
	}

	public void setFailed(boolean isFailed) {
		this.isFailed = isFailed;
	}
	
	public String getCropimagePath() {
		return cropimagePath;
	}

	public void setCropimagePath(String cropimagePath) {
		this.cropimagePath = cropimagePath;
	}

	public byte[] getCropImageByte() {
		return cropImageByte;
	}

	public void setCropImageByte(byte[] cropImageByte) {
		this.cropImageByte = cropImageByte;
	}

	public Transaction() {
		super();
	}
	public Transaction(String empId, String employeeCode, String name, String department, String designation,
			String employeeType, String contractor, String branch, String organization, String deviceId, Integer logId,
			String deviceName, String serialNo, Boolean wearingMask, String temperature, String accessType,
			Date punchDate, Date punchTime, String punchDateStr, String punchTimeStr, String deviceType) {
		super();
		this.empId = empId;
		this.employeeCode = employeeCode;
		this.name = name;
		this.department = department;
		this.designation = designation;
		this.employeeType = employeeType;
		this.contractor = contractor;
		this.branch = branch;
		this.organization = organization;
		this.deviceId = deviceId;
		this.logId = logId;
		this.deviceName = deviceName;
		this.serialNo = serialNo;
		this.wearingMask = wearingMask;
		this.temperature = temperature;
		this.accessType = accessType;
		this.punchDate = punchDate;
		this.punchTime = punchTime;
		this.punchDateStr = punchDateStr;
		this.punchTimeStr = punchTimeStr;
		this.deviceType = deviceType;
	}
	
}
