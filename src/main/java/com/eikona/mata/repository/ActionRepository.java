package com.eikona.mata.repository;

import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import com.eikona.mata.entity.Action;

@Repository
public interface ActionRepository extends DataTablesRepository<Action, Long>{
	List<Action> findAllByIsDeletedFalse();
}
