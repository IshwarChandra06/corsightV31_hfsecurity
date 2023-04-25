package com.eikona.mata.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eikona.mata.entity.EmployeeShiftDailyAssociation;
@Repository
public interface EmployeeShiftDailyAssociationRepository  extends DataTablesRepository<EmployeeShiftDailyAssociation, Long> {

	@Query("select es from com.eikona.mata.entity.EmployeeShiftDailyAssociation es where es.dateStr = :dateStr and es.employee.empId = :empId")
	EmployeeShiftDailyAssociation findByDateAndEmpIdCustom(String dateStr, String empId);
	
	@Query("select es.employee.id from com.eikona.mata.entity.EmployeeShiftDailyAssociation es where es.dateStr = :dateStr and es.shift is null")
	List<Long> getemployeeIdListCustom(String dateStr);
	@Query("select es from com.eikona.mata.entity.EmployeeShiftDailyAssociation es where es.date >= :startDate and es.date <= :endDate and es.shift=null")
	List<EmployeeShiftDailyAssociation> findUnassignedDataByDateCustom(Date startDate, Date endDate);

	@Query("select es.employee.empId from com.eikona.mata.entity.EmployeeShiftDailyAssociation es where es.date >= :startDate and es.date < :endDate "
			+ "group by es.dateStr, es.employee.empId")
	List<String> findByDateRangeCustom(Date startDate, Date endDate);

	@Query("select es from com.eikona.mata.entity.EmployeeShiftDailyAssociation es where es.date = :date "
			+ "and es.shift.name = :shift order by es.employee.empId asc")
	List<EmployeeShiftDailyAssociation> findEmployeeByDateAndShiftCustom(String shift, Date date);



}
