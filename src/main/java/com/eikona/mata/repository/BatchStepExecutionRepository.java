package com.eikona.mata.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import com.eikona.mata.entity.BatchStepExecution;

@Repository
public interface BatchStepExecutionRepository extends DataTablesRepository<BatchStepExecution, Long>{

}
