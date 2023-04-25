package com.eikona.mata.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import com.eikona.mata.entity.Device;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.EmployeeDevice;

@Repository
public interface EmployeeDeviceRepository extends DataTablesRepository<EmployeeDevice, Long>{


	EmployeeDevice findByEmployeeAndDevice(Employee employee, Device device);
}
