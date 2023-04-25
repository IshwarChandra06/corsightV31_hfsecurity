package com.eikona.mata.repository;


import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import com.eikona.mata.entity.Designation;


@Repository
public interface DesignationRepository extends DataTablesRepository<Designation, Long> {


	 List<Designation> findAllByIsDeletedFalse();

    Designation findByNameAndIsDeletedFalse(String str);
	

}
