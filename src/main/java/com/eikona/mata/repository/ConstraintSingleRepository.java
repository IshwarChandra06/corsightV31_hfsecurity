package com.eikona.mata.repository;



import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import com.eikona.mata.entity.ConstraintSingle;



public interface ConstraintSingleRepository extends DataTablesRepository<ConstraintSingle, Long>{
	 List<ConstraintSingle> findAllByIsDeletedFalse();

	ConstraintSingle findByValue(String string);
}
