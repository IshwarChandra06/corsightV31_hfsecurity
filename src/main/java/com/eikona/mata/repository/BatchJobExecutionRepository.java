package com.eikona.mata.repository;

import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eikona.mata.dto.BatchJobAndStepDto;
import com.eikona.mata.entity.BatchJobExecution;

@Repository
public interface BatchJobExecutionRepository extends DataTablesRepository<BatchJobExecution, Long> {
	@Query("select new com.eikona.mata.dto.BatchJobAndStepDto(be.startTime,be.endTime,be.status, be.exitCode, be.exitMessage, bi.jobName) "
			+ "from com.eikona.mata.entity.BatchJobExecution as be join com.eikona.mata.entity.BatchJobInstance as bi on "
			+ "be.jobInstance = bi.jobInstanceId where bi.jobName = :jobName")
	List<BatchJobAndStepDto> jobAndInstance(String jobName);
}
