package com.eikona.mata.repository;

import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import com.eikona.mata.entity.Branch;
import com.eikona.mata.entity.Organization;



@Repository
public interface BranchRepository extends DataTablesRepository<Branch, Long> {
	 List<Branch> findAllByIsDeletedFalse();

	Branch findByNameAndIsDeletedFalse(String str);

	 List<Branch> findByOrganizationAndIsDeletedFalse(Organization organization);



}