package com.eikona.mata.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;


@Entity(name = "area")
public class Area extends Auditable<String> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private Long id;

	@NotBlank(message = "Please provide a valid name")
	@Column(name = "area_name")
	private String name;

	@Column(name = "description")
	private String description;
	
	@Column(name = "watchlist")
	private String watchlist;
	
	@Column(name = "watchlist_id")
	private String watchlistId;

	@ManyToOne
	@JoinColumn(name = "organization_id")
	private Organization organization;
	
	@ManyToOne
	@JoinColumn(name = "branch_id")
	@NotNull(message = "Please select a branch")
	private Branch branch;
	
	@ManyToOne
	@JoinColumn(name = "door_id")
	private Door door;

	@Column(name = "is_deleted")
	private boolean isDeleted;
	
	@Column(name = "is_sync")
	private boolean isSync;

	@OneToMany
	@JoinTable(name = "area_devices", joinColumns = {
			@JoinColumn(name = "area_id", referencedColumnName = "ID") }, inverseJoinColumns = {
					@JoinColumn(name = "device_id", referencedColumnName = "ID") })
	private List<Device> device;

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

	public List<Device> getDevice() {
		return device;
	}

	public void setDevice(List<Device> device) {
		this.device = device;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public String getWatchlist() {
		return watchlist;
	}

	public void setWatchlist(String watchlist) {
		this.watchlist = watchlist;
	}

	public String getWatchlistId() {
		return watchlistId;
	}

	public void setWatchlistId(String watchlistId) {
		this.watchlistId = watchlistId;
	}

	public boolean isSync() {
		return isSync;
	}

	public void setSync(boolean isSync) {
		this.isSync = isSync;
	}
	
	public Door getDoor() {
		return door;
	}

	public void setDoor(Door door) {
		this.door = door;
	}

	public Area(@NotBlank(message = "Please provide a valid name") String name, Organization organization,
			Branch branch,boolean isDeleted) {
		super();
		this.name = name;
		this.organization = organization;
		this.branch=branch;
		this.isDeleted = isDeleted;
	}
	
	public Area() {
		super();
	}

	public Area(@NotBlank(message = "Please provide a valid name") String name,Branch branch, boolean isDeleted) {
		super();
		this.name = name;
		this.branch=branch;
		this.isDeleted = isDeleted;
	}
}