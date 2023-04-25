package com.eikona.mata.repository;

import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import com.eikona.mata.entity.Branch;



@Repository
public interface BranchRepository extends DataTablesRepository<Branch, Long> {
	 List<Branch> findAllByIsDeletedFalse();

	Branch findByNameAndIsDeletedFalse(String str);



}