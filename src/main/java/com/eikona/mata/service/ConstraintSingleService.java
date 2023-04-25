package com.eikona.mata.service;



import java.util.List;

import javax.validation.Valid;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import com.eikona.mata.entity.ConstraintSingle;


public interface ConstraintSingleService  {
	ConstraintSingle find(final Long id);

    List<ConstraintSingle> findAll();

    ConstraintSingle save(final ConstraintSingle entity);

    ConstraintSingle update(final ConstraintSingle entity);

    void delete(final Long entityId);
    
    DataTablesOutput<ConstraintSingle> getAll(@Valid DataTablesInput input);
}
