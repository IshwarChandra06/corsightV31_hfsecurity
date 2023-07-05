package com.eikona.mata.repository;


import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import com.eikona.mata.entity.Organization;


@Repository
public interface OrganizationRepository extends DataTablesRepository<Organization, Long> {

	List<Organization> findAllByIsDeletedFalse();
	
	Organization  findByNameAndIsDeletedFalse(String string);

	List<Organization> findByIdAndIsDeletedFalse(Long id);
	
	//List<Organization> findAllByUser();

}
