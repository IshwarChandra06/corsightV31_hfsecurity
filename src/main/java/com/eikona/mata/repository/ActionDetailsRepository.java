package com.eikona.mata.repository;

import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eikona.mata.entity.ActionDetails;

@Repository
public interface ActionDetailsRepository extends DataTablesRepository<ActionDetails, Long>{
	
	List<ActionDetails> findAllByIsDeletedFalse();
	
	@Query("select ad from com.eikona.mata.entity.ActionDetails as ad where ad.isDeleted=false and ad.action.id=:id")
	List<ActionDetails> findByActionIdCustom(Long id);

	List<ActionDetails> findByStatus(String string);
	

}
