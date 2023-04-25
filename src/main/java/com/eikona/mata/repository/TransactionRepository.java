package com.eikona.mata.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;

import com.eikona.mata.dto.DepartmentDto;
import com.eikona.mata.dto.OrganizationDto;
import com.eikona.mata.entity.Transaction;

public interface TransactionRepository extends DataTablesRepository<Transaction, Long>{

	@Query("select t from com.eikona.mata.entity.Transaction t where t.empId =:id  and t.punchDate = (select MIN(tr.punchDate) "
			+ "from com.eikona.mata.entity.Transaction tr where tr.empId=:id and tr.punchDateStr=:date ) ")
	List<Transaction> findByEmpIdMinCustom(String id, String date);

	
	@Query("select t from com.eikona.mata.entity.Transaction t where t.empId =:id  and t.punchDate = (select MAX(tr.punchDate) "
			+ "from com.eikona.mata.entity.Transaction tr where tr.empId=:id and tr.punchDateStr=:date ) ")
	List<Transaction> findByEmpIdMaxCustom(String id, String date);


	Transaction findByEmpIdAndPunchDate(String empId, Date punchDate);


	Transaction findByAppearanceId(String appearance_id);
	

	@Query("SELECT new com.eikona.mata.entity.Transaction("
			+ "tr.empId,tr.employeeCode,tr.name,tr.department,tr.designation, tr.employeeType"
			+ ", tr.organization,tr.branch, tr.organization,tr.deviceId,tr.logId, tr.deviceName, tr.serialNo,"
			+ "tr.wearingMask,tr.temperature, tr.accessType, tr.punchDate, tr.punchTime,tr.punchDateStr, tr.punchTimeStr,tr.deviceType "
			+ ") "
			+ "FROM com.eikona.mata.entity.Transaction AS tr where tr.accessType='Check In' and tr.empId is not null and "
			+ "tr.punchDate in (select MIN(tr.punchDate) FROM com.eikona.mata.entity.Transaction AS tr where tr.empId is not null and "
			+ "tr.punchDate >= :startTime and tr.punchDate <= :endTime and tr.accessType='Check In' GROUP BY tr.empId ORDER BY tr.empId ASC) "
			+ "and tr.punchDate >= :startTime and tr.punchDate <= :endTime GROUP BY tr.empId ORDER BY tr.empId asc")
	List<Transaction> getMinTransactionByTimeIntervalCustom(Date startTime, Date endTime);


	@Query(" SELECT new com.eikona.mata.entity.Transaction("
			+ "tr.empId,tr.employeeCode,tr.name,tr.department,tr.designation,tr.employeeType "
			+ ", tr.organization, tr.organization,tr.branch,tr.deviceId,tr.logId, tr.deviceName, tr.serialNo, "
			+ "tr.wearingMask,tr.temperature,tr.accessType,tr.punchDate,tr.punchTime,tr.punchDateStr, MAX(tr.punchTimeStr),tr.deviceType "
			+ ") "
			+ "FROM com.eikona.mata.entity.Transaction AS tr where tr.accessType='Check Out' and tr.empId is not null and "
			+ "tr.punchDate in (select MAX(tr.punchDate) FROM com.eikona.mata.entity.Transaction AS tr where tr.empId is not null and "
			+ "tr.punchDate >= :startTime and tr.punchDate <= :endTime and tr.accessType='Check Out' GROUP BY tr.empId ORDER BY tr.empId ASC) "
			+ "and tr.punchDate >= :startTime and tr.punchDate <= :endTime GROUP BY tr.empId ORDER BY tr.empId asc")
	List<Transaction> getMaxTransactionByTimeIntervalCustom(Date startTime, Date endTime);

	@Query("Select count(distinct ev.poiId) from com.eikona.mata.entity.Transaction as ev where ev.employee is not null and "
			+ "ev.punchDateStr = :dateStr")// 
	long totalpresentCustom(String dateStr);
	
	@Query("Select count(distinct ev.poiId) from com.eikona.mata.entity.Transaction as ev where ev.wearingMask = true and ev.employee is not null "
			+ "and ev.punchDateStr = :dateStr")
	long totalUnMaskCustom(String dateStr);
	
	@Query("Select count(ev.id) from com.eikona.mata.entity.Transaction as ev where ev.appearanceId is not null "
			+ "and ev.punchDateStr = :dateStr")
	long countAppearencesCustom(String dateStr);
	
	@Query("Select new com.eikona.mata.dto.DepartmentDto(ev.employee.department.name, count(distinct ev.poiId) as presentEmployee) "
			+ "from com.eikona.mata.entity.Transaction as ev where ev.employee.department is not null "
			+ "and ev.punchDateStr =:date GROUP BY ev.employee.department.name")
	List<DepartmentDto> countTotalEmployeeInDeptCustom(String date);

	@Query("Select new com.eikona.mata.dto.OrganizationDto(ev.punchTimeStr, ev.employee.organization.name, count(distinct ev.poiId) as total) "
			+ "from com.eikona.mata.entity.Transaction as ev where ev.employee is not null and ev.employee.organization is not null "
			+ "and ev.punchDateStr = :dateStr GROUP BY ev.punchDate order by  ev.punchTimeStr asc")
	List<OrganizationDto> countEmpPresentCustom(String dateStr);
	
//	GROUP BY ev.punchDate, ev.employee.organization.name
	@Query("Select tr from com.eikona.mata.entity.Transaction as tr where tr.punchDateStr =:dateStr and tr.employee IS NOT NULL ORDER BY tr.id DESC ")
	List<Transaction> getRealTimeDataCustom(String dateStr, Pageable paging);
	
//	@Query("Select new com.eikona.mata.dto.OrganizationDto(ev.punchDate, ev.employee.organization.name, count(distinct ev.poiId) as total) "
//			+ "from com.eikona.mata.entity.Transaction as ev where ev.employee.organization is not null "
//			+ "and ev.punchDate >= :startDate and ev.punchDate <=:endDate GROUP BY ev.punchDateStr, ev.employee.organization.name ORDER BY ev.punchDateStr ASC" )//  GROUP BY ev.punchDateStr, ev.employee.organization.name
	@Query("Select new com.eikona.mata.dto.OrganizationDto(ev.punchDateStr, count(distinct ev.poiId) as total) "
			+ "from com.eikona.mata.entity.Transaction as ev where ev.employee.organization is not null "
			+ "and ev.punchDate >= :startDate and ev.punchDate <=:endDate GROUP BY ev.punchDateStr ORDER BY ev.punchDateStr ASC" )
	List<OrganizationDto> findAllOrganizationCustom(Date startDate, Date endDate);


	@Query("Select ev.totalCount from com.eikona.mata.entity.Transaction as ev  WHERE ev.id=(SELECT max(ev.id) FROM com.eikona.mata.entity.Transaction as ev)")
	Long findLastRecordCustom();
}
