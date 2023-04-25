package com.eikona.mata.repository;


import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import com.eikona.mata.entity.EmailSetup;
@Repository
public interface EmailSetUpRepository extends DataTablesRepository<EmailSetup, Long> {
	 List<EmailSetup> findAllByIsDeletedFalse();

	EmailSetup findByUserName(String string);

}
