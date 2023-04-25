package com.eikona.mata.repository;


import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eikona.mata.entity.Area;
import com.eikona.mata.entity.Branch;
import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.Organization;


@Repository
public interface EmployeeRepository extends DataTablesRepository<Employee, Long> {

	@Query("from com.eikona.mata.entity.Employee as emp where emp.isDeleted=false and empId= 1")
	Employee getEmp();
	
	List<Employee> findAllByIsDeletedFalse();

	List<Employee> findByArea(Area area);
	
	@Query("select emp.id from com.eikona.mata.entity.Employee as emp where emp.isDeleted=false and empId=:empId")
	Long findByEmpIdAndIsDeletedFalseCustom(String empId);
	
	Employee findByEmpIdAndIsDeletedFalse(String empId);
	
	@Query("select emp.empId from com.eikona.mata.entity.Employee as emp where emp.isDeleted=false")
	List<String> getEmpIdAndIsDeletedFalseCustom();
	
	@Query("select count(e.id) from com.eikona.mata.entity.Employee e JOIN e.area as a where e.isDeleted = false and e.branch =:branch "
			+ "and a.id = :id ")
	long countEmployeeAndIsDeletedFalseCustom(Branch branch, Long id);
	
	@Query("select e from com.eikona.mata.entity.Employee e JOIN e.area as a where e.isDeleted = false and e.branch =:branch "
			+ "and a.id = :id ")
	List<Employee> findByAreaIdAndBranchAndIsDeletedFalseCustom(Branch branch, Long id, Pageable paging);

	Employee findByPoiAndIsDeletedFalse(String string);

	List<Employee> findAllByIsDeletedFalseAndIsSyncFalse();

	List<Employee> findAllByIsDeletedFalseAndIsSyncTrue();

	List<Employee> findByOrganizationAndIsDeletedFalse(Organization org);

	List<Employee> findAllByIsDeletedTrueAndIsSyncTrue();

	Employee findByNameAndIsDeletedFalse(String name);
	
	@Query("Select count(w.id) from com.eikona.mata.entity.Employee as w where w.isDeleted = false and w.department.name =:deptName GROUP BY w.department.name")
	Long countEmployeeDeptWiseCustom(String deptName);

	Employee findByEmpId(String string);
	
	@Query("Select e.id from com.eikona.mata.entity.Employee e where e.isDeleted=false")
	List<Long> employeeIdListCustom();
	
	@Query("Select e from com.eikona.mata.entity.Employee e where e.isDeleted=false and e.organization.name=:org")
	List<Employee> findAllByIsDeletedFalseAndOrganization(String org);
	
	@Query("Select e from com.eikona.mata.entity.Employee e where e.isDeleted=false and e.createdDate like '2022-12-06%'")
	List<Employee> findByCreatedDateCustom();
	
	List<Employee> findAllByIsDeletedFalseAndIsSyncTrueAndIsFaceSyncFalse();
	
	List<Employee> findAllByIsDeletedFalseAndIsSyncFromDeviceTrue();




}