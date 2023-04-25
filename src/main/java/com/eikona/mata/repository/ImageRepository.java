package com.eikona.mata.repository;

import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import com.eikona.mata.entity.Employee;
import com.eikona.mata.entity.Image;

@Repository
public interface ImageRepository  extends DataTablesRepository<Image, Long> {

	Image findByOriginalPath(String string);

	List<Image> findByEmployee(Employee emp);

}
