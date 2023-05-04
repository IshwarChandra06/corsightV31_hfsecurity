package com.eikona.mata.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eikona.mata.dto.ExceptionSummaryDto;
import com.eikona.mata.entity.DailyAttendance;

@Repository
public interface DailyAttendanceRepository extends DataTablesRepository<DailyAttendance, Long> {
	
	DailyAttendance findByEmpIdAndDate(String workerCode, Date time);

	@Query("select er from com.eikona.mata.entity.DailyAttendance er where er.date between :start and :end ")
	List<DailyAttendance> findEventByDateCustom(Date start, Date end);
	
	@Query("SELECT  dr FROM com.eikona.mata.entity.DailyAttendance as dr where dr.empId = :empId and dr.date between :sDate and :eDate "
			+"order by dr.date asc")
	List<DailyAttendance> findDetailsByDateCustom(String empId, Date sDate, Date eDate);
	
	@Query("SELECT COUNT(dr.id) as present "
   		 + "FROM com.eikona.mata.entity.DailyAttendance as dr where dr.attendanceStatus = 'Present' and dr.empId = :empId and dr.date between :sDate and :eDate "
   		 + "order by dr.date asc")
	Long getCountOfPresentEmployeeCustom(String empId, Date sDate, Date eDate);
	
	@Query("SELECT COUNT(dr.id) as present "
	   		 + "FROM com.eikona.mata.entity.DailyAttendance as dr where dr.attendanceStatus = 'Absent' and dr.empId = :empId and dr.date between :sDate and :eDate "
	   		 + "order by dr.date asc")
	Long getCountOfAbsentEmployeeCustom(String empId, Date sDate, Date eDate);
	
	@Query("SELECT COUNT(dr.id) as present "
	   		 + "FROM com.eikona.mata.entity.DailyAttendance as dr where dr.overTime is not null and dr.empId = :empId and dr.date between :sDate and :eDate "
	   		 + "order by dr.date asc")
	Long getCountOfOverTimeCustom(String empId, Date sDate, Date eDate);
	
	@Query("SELECT COUNT(dr.id) as present "
	   		 + "FROM com.eikona.mata.entity.DailyAttendance as dr where dr.lateComing is not null and dr.empId = :empId and dr.date between :sDate and :eDate "
	   		 + "order by dr.date asc")
	Long getCountOfLateComingCustom(String empId, Date sDate, Date eDate);
	
	@Query("SELECT COUNT(dr.id) as present "
	   		 + "FROM com.eikona.mata.entity.DailyAttendance as dr where dr.earlyGoing is not null and dr.empId = :empId and dr.date between :sDate and :eDate "
	   		 + "order by dr.date asc")
	Long getCountOfEarlyGoingCustom(String empId, Date sDate, Date eDate);
	
	@Query("SELECT COUNT(dr.id) as present "
	   		 + "FROM com.eikona.mata.entity.DailyAttendance as dr where dr.asPerRoster = 'No' and dr.empId = :empId and dr.date between :sDate and :eDate "
	   		 + "order by dr.date asc")
	Long getCountOfWeekOffPresentEmployeeCustom(String empId, Date sDate, Date eDate);
	
	@Query("SELECT COUNT(dr.id) as present "
	   		 + "FROM com.eikona.mata.entity.DailyAttendance as dr where dr.asPerRoster = 'Week Off' and dr.empId = :empId and dr.date between :sDate and :eDate "
	   		 + "order by dr.date asc")
	Long getCountOfWeekOffEmployeeCustom(String empId, Date sDate, Date eDate);
	
	@Query("SELECT  dr FROM com.eikona.mata.entity.DailyAttendance as dr where dr.date >= :startDate and dr.date <= :endDate "
			+"order by dr.empId, dr.date asc")
	List<DailyAttendance> getAllReportByDateRangeCustom(Date startDate, Date endDate);
	
	@Query("SELECT new com.eikona.mata.dto.ExceptionSummaryDto( "
	  		+ "dr.organization, dr.date, "
	  		+ "SUM( CASE WHEN (dr.attendanceStatus = 'Present') THEN 1 ELSE 0 END) as present, "
	  		//+"SUM( CASE WHEN (dr.attendanceStatus = 'Present') THEN 1 ELSE 0 END) as present,"
	  		+ "SUM( CASE WHEN (dr.empInMask = 'true') THEN 1 ELSE 0 END) as empInMask, "
	  		+ "SUM( CASE WHEN (dr.empOutMask = 'true' and (dr.empOutTime != '' or dr.empOutTime is not null )) THEN 1 ELSE 0 END) as empOutMask, "
	  		+ "SUM( CASE WHEN (dr.missedOutPunch = 1) THEN 1 ELSE 0 END) as missedOutPunch, "
	  		+ "SUM( CASE WHEN (dr.overTime > 0) THEN 1 ELSE 0 END) as overTime, "
	  		+ "SUM( CASE WHEN (dr.workTime != null AND dr.workTime != '8:30' and dr.overTime = null) THEN 1 ELSE 0 END) as lessTime, "
	  		+ "SUM( CASE WHEN (dr.lateComing  > 0) THEN 1 ELSE 0 END) as lateComing, "
	  		+ "SUM( CASE WHEN (dr.earlyGoing  > 0) THEN 1 ELSE 0 END) as earlyGoing, "
	  		//+ "SUM( CASE WHEN (dr.empInTemp IN ('98.7','98.8','98.9') OR dr.empInTemp LIKE '99%' ) THEN 1 ELSE 0 END) as empInTemp, "
	  		+ "SUM( CASE WHEN (dr.empInTemp+0 > 98.6) THEN 1 ELSE 0 END) as empInTemp, "//count(distinct dr.id) as
	  		+ "SUM( CASE WHEN (dr.empOutTemp+0 > 98.6) THEN 1 ELSE 0 END) as empOutTemp "
	  		+ ") "
			  + "FROM com.eikona.mata.entity.DailyAttendance AS dr where dr.date >= :startDate and dr.date <= :endDate and dr.organization = :company "
  		      + " GROUP BY dr.date,dr.organization ORDER BY dr.date ASC") //and dr.attendanceStatus = 'Present'
    //List<ExceptionSummaryDto> getAllExceptions(Date startDate, Date endDate);
    List<ExceptionSummaryDto> getAllExceptions(Date startDate, Date endDate, String company);
	
	
	@Query("SELECT  dr FROM com.eikona.mata.entity.DailyAttendance as dr where dr.attendanceStatus = 'Present' and "
			+ "  dr.date >= :sDate and dr.date <= :eDate and dr.organization = :company ") 
	List<DailyAttendance> getDailyAttendancePresent(Date sDate,Date eDate, String company); //and dr.branch = :branch 
	
	@Query("SELECT  dr FROM com.eikona.mata.entity.DailyAttendance as dr where dr.empInMask = 'false' and "
			+ " dr.date >= :sDate and dr.date <= :eDate and dr.organization = :company")
	List<DailyAttendance> getDailyAttendanceInNoMask(Date sDate,Date eDate, String company);
	
	@Query("SELECT  dr FROM com.eikona.mata.entity.DailyAttendance as dr where dr.empOutMask = 'false' and (dr.empOutTime != '' or dr.empOutTime is not null ) "
			+ "and dr.date >= :sDate and dr.date <= :eDate and dr.organization = :company")
	List<DailyAttendance> getDailyAttendanceOutNoMask(Date sDate,Date eDate, String company);
	
	@Query("SELECT  dr FROM com.eikona.mata.entity.DailyAttendance as dr where dr.missedOutPunch = 'true' and "
			+ "dr.date >= :sDate and dr.date <= :eDate and dr.organization = :company")
	List<DailyAttendance> getDailyAttendanceMissOutPunch(Date sDate,Date eDate, String company);

	@Query("SELECT  dr FROM com.eikona.mata.entity.DailyAttendance as dr where dr.overTime > 0 and "
			+ "dr.date >= :sDate and dr.date <= :eDate and dr.organization = :company")
	List<DailyAttendance> getDailyAttendanceOverTime(Date sDate,Date eDate, String company);

	@Query("SELECT  dr FROM com.eikona.mata.entity.DailyAttendance as dr where dr.workTime != null AND "
			+ "dr.workTime != '8:30' and dr.overTime = null and "
			+ " dr.date >= :sDate and dr.date <= :eDate and dr.organization = :company")
	List<DailyAttendance> getDailyAttendanceLessTime(Date sDate,Date eDate, String company);
	
	@Query("SELECT  dr FROM com.eikona.mata.entity.DailyAttendance as dr where dr.empInTemp+0 > 99.14 and "
			+ "dr.date >= :sDate and dr.date <= :eDate and dr.organization = :company")
	List<DailyAttendance> getInAbnormalTempEmpList(Date sDate,Date eDate, String company);
	
	@Query("SELECT  dr FROM com.eikona.mata.entity.DailyAttendance as dr where dr.empOutTemp+0 > 99.14 and "
			+ "dr.date >= :sDate and dr.date <= :eDate and dr.organization = :company")
	List<DailyAttendance> getOutAbnormalTempEmpList(Date sDate,Date eDate, String company);

	
	@Query("SELECT  dr FROM com.eikona.mata.entity.DailyAttendance as dr where dr.lateComing > 0 and "
			+ "dr.date >= :sDate and dr.date <= :eDate and dr.organization = :company")
	List<DailyAttendance> getLateComingEmpList(Date sDate,Date eDate, String company);
	
	@Query("SELECT  dr FROM com.eikona.mata.entity.DailyAttendance as dr where dr.earlyGoing > 0 and "
			+ "dr.date >= :sDate and dr.date <= :eDate and dr.organization = :company")
	List<DailyAttendance> getEarlyGoingEmpList(Date sDate,Date eDate, String company);

	@Query("SELECT dr FROM com.eikona.mata.entity.DailyAttendance AS dr "
			+ "where dr.attendanceStatus= 'Absent' order by dr.empId,dr.dateStr")
	List<DailyAttendance> findByAttendanceStatusAbsentCustom();
	
	@Query("SELECT dr FROM com.eikona.mata.entity.DailyAttendance as dr where  dr.empId = :employeeId and dr.date between :sDate and :eDate "
	   		 + "order by dr.date asc")
	List<DailyAttendance> findByDateRangeAndEmpIdCustom(Date sDate, Date eDate, String employeeId);
	
	@Query("SELECT  dr FROM com.eikona.mata.entity.DailyAttendance as dr where dr.date >= :startDate and dr.date <= :endDate "
			+"order by dr.dateStr, dr.empId asc")
	List<DailyAttendance> findDailyAttendanceByDateCustom(Date startDate, Date endDate);

	@Query("SELECT new com.eikona.mata.dto.ExceptionSummaryDto( "
	  		+ "dr.organization, dr.date, "
	  		+ "SUM( CASE WHEN (dr.attendanceStatus = 'Present') THEN 1 ELSE 0 END) as present, "
	  		+ "SUM( CASE WHEN (dr.empInMask = 'true') THEN 1 ELSE 0 END) as empInMask, "
	  		+ "SUM( CASE WHEN (dr.empOutMask = 'true' and (dr.empOutTime != '' or dr.empOutTime is not null )) THEN 1 ELSE 0 END) as empOutMask, "
	  		+ "SUM( CASE WHEN (dr.missedOutPunch = 'Yes') THEN 1 ELSE 0 END) as missedOutPunch, "
	  		+ "SUM( CASE WHEN (dr.overTime > 0) THEN 1 ELSE 0 END) as overTime, "
	  		+ "SUM( CASE WHEN (dr.workTime != null AND dr.workTime != '8:30' and dr.overTime = null) THEN 1 ELSE 0 END) as lessTime, "
	  		+ "SUM( CASE WHEN (dr.lateComing  > 0) THEN 1 ELSE 0 END) as lateComing, "
	  		+ "SUM( CASE WHEN (dr.earlyGoing  > 0) THEN 1 ELSE 0 END) as earlyGoing, "
	  		+ "SUM( CASE WHEN (dr.empInTemp+0 > 98.6) THEN 1 ELSE 0 END) as empInTemp, "
	  		+ "SUM( CASE WHEN (dr.empOutTemp+0 > 98.6) THEN 1 ELSE 0 END) as empOutTemp "
	  		+ ") "
			  + "FROM com.eikona.mata.entity.DailyAttendance AS dr where dr.id >:id"
  		      + " GROUP BY dr.date,dr.organization ORDER BY dr.date desc")
	Page<ExceptionSummaryDto> findByDateAndOrganizationCustom(Long id, Pageable pageable);
	
	
	@Query("SELECT new com.eikona.mata.dto.ExceptionSummaryDto( "
	  		+ "dr.organization, dr.date, "
	  		+ "SUM( CASE WHEN (dr.attendanceStatus = 'Present') THEN 1 ELSE 0 END) as present, "
	  		+ "SUM( CASE WHEN (dr.empInMask = 'true') THEN 1 ELSE 0 END) as empInMask, "
	  		+ "SUM( CASE WHEN (dr.empOutMask = 'true' and (dr.empOutTime != '' or dr.empOutTime is not null )) THEN 1 ELSE 0 END) as empOutMask, "
	  		+ "SUM( CASE WHEN (dr.missedOutPunch = 'Yes') THEN 1 ELSE 0 END) as missedOutPunch, "
	  		+ "SUM( CASE WHEN (dr.overTime > 0) THEN 1 ELSE 0 END) as overTime, "
	  		+ "SUM( CASE WHEN (dr.workTime != null AND dr.workTime != '8:30' and dr.overTime = null) THEN 1 ELSE 0 END) as lessTime, "
	  		+ "SUM( CASE WHEN (dr.lateComing  > 0) THEN 1 ELSE 0 END) as lateComing, "
	  		+ "SUM( CASE WHEN (dr.earlyGoing  > 0) THEN 1 ELSE 0 END) as earlyGoing, "
	  		+ "SUM( CASE WHEN (dr.empInTemp+0 > 98.6) THEN 1 ELSE 0 END) as empInTemp, "
	  		+ "SUM( CASE WHEN (dr.empOutTemp+0 > 98.6) THEN 1 ELSE 0 END) as empOutTemp "
	  		+ ") "
			  + "FROM com.eikona.mata.entity.DailyAttendance AS dr where dr.date >= :startDate and dr.date <= :endDate and dr.organization = :company "
  		      + " GROUP BY dr.date,dr.organization ORDER BY dr.date desc")
	Page<ExceptionSummaryDto> findByDateAndOrganizationCustom(Date startDate, Date endDate, String company, Pageable pageable);

	@Query("SELECT  dr FROM com.eikona.mata.entity.DailyAttendance as dr where dr.workTime != null and dr.workTime != '8:30' and dr.overTime = null ")
	Page<DailyAttendance> findAllLessTimeCustom(Specification<DailyAttendance> and, Pageable pageable);

	@Query("SELECT  dr FROM com.eikona.mata.entity.DailyAttendance as dr where dr.date >= :sDate and dr.date <= :eDate")
	List<DailyAttendance> findByDateAndOrganization(Date sDate,Date eDate);

	@Query("SELECT  dr.empId FROM com.eikona.mata.entity.DailyAttendance as dr where dr.date >= :sDate and dr.date <= :eDate")
	List<String> findByDateCustom(Date sDate,Date eDate);



	

	



}
