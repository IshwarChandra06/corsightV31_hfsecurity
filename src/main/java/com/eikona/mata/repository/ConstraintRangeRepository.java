package com.eikona.mata.repository;



import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import com.eikona.mata.entity.ConstraintRange;



public interface ConstraintRangeRepository extends DataTablesRepository<ConstraintRange, Long>{
	 List<ConstraintRange> findAllByIsDeletedFalse();

	ConstraintRange findByType(String type);
}
