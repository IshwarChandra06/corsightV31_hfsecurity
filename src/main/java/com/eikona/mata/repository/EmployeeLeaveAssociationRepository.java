package com.eikona.mata.repository;

import java.util.Date;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eikona.mata.entity.EmployeeLeaveAssociation;

@Repository
public interface EmployeeLeaveAssociationRepository extends DataTablesRepository<EmployeeLeaveAssociation, Long>{
	@Query("select el from com.eikona.mata.entity.EmployeeLeaveAssociation el where el.date=:currentDate and el.employee.empId=:empId and el.approveOrReject='approved'")
	EmployeeLeaveAssociation findLeaveStatusByDateAndEmpIdCustom(String empId, Date currentDate);

}
