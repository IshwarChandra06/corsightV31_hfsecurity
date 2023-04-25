package com.eikona.mata.service;


import java.util.List;

import javax.validation.Valid;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import com.eikona.mata.entity.ConstraintRange;

public interface ConstraintRangeService {
	ConstraintRange find(final Long id);

	    List<ConstraintRange> findAll();

	    ConstraintRange save(final ConstraintRange entity);

	    ConstraintRange update(final ConstraintRange entity);

	    void delete(final Long entityId);
	    
	    DataTablesOutput<ConstraintRange> getAll(@Valid DataTablesInput input);
}
