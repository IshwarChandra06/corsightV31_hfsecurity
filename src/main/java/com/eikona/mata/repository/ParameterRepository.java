package com.eikona.mata.repository;


import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import com.eikona.mata.entity.Parameter;

@Repository
public interface ParameterRepository extends DataTablesRepository<Parameter, Long> {
	List<Parameter> findAllByIsDeletedFalse();

	Parameter findByName(String name);

}
