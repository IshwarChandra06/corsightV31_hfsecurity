package com.eikona.mata.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import com.eikona.mata.entity.BatchJobInstance;

public interface BatchJobInstanceRepository extends DataTablesRepository<BatchJobInstance, Long>{

}
