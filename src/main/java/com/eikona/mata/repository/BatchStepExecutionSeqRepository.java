package com.eikona.mata.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import com.eikona.mata.entity.BatchStepExecutionSeq;

@Repository
public interface BatchStepExecutionSeqRepository extends DataTablesRepository<BatchStepExecutionSeq, Long>{

}
