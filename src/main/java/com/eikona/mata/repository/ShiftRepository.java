package com.eikona.mata.repository;


import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import com.eikona.mata.entity.Shift;

@Repository
public interface ShiftRepository extends DataTablesRepository<Shift, Long> {
	public List<Shift> findAllByIsDeletedFalse();

	Shift findByNameAndIsDeletedFalse(String string);

	public List<Shift> findAllByOrganizationName(String organization);

    Shift findByNameAndIsDeletedTrue(String string);

}
