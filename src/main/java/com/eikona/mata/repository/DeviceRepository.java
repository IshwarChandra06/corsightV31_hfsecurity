package com.eikona.mata.repository;


import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eikona.mata.entity.Area;
import com.eikona.mata.entity.Branch;
import com.eikona.mata.entity.Device;


@Repository
public interface DeviceRepository extends DataTablesRepository<Device, Long> {

	List<Device> findAllByIsDeletedFalse();
	
	List<Device> findAllByModelAndIsDeletedFalse(String model);

	Device findByIpAddressAndIsDeletedFalse(String ipAddress);

	Device findByNameAndIsDeletedFalse(String device);

	Device findBySerialNoAndIsDeletedFalse(String serialno);

	@Query("select de from com.eikona.mata.entity.Device as de where de.area in :area and isDeleted=false")
	List<Device> findByAreaAndIsDeletedFalseCustom(List<Area> area);
	
	@Query("select de from com.eikona.mata.entity.Device as de where de.branch = :branch and isDeleted=false")
	List<Device> findByBranchAndIsDeletedFalseCustom(Branch branch);

	Device findByCameraIdAndIsDeletedFalse(String string);
	
	List<Device> findAllByIsDeletedFalseAndIsSyncFalse();
	
	@Query("select de from com.eikona.mata.entity.Device as de where isSync=true and isDeleted=true and model='Corsight'")
	List<Device> findAllByIsDeletedTrueAndIsSyncTrueCustom();

	Device findByIdAndIsDeletedFalse(long id);
}
