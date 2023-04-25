package com.eikona.mata.repository;

import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import com.eikona.mata.entity.Door;
@Repository
public interface DoorRepository extends DataTablesRepository<Door, Long> {
	List<Door> findAllByIsDeletedFalse();
}
