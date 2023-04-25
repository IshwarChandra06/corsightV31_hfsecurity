package com.eikona.mata.service;


import java.util.List;

import javax.validation.Valid;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import com.eikona.mata.entity.Parameter;


public interface ParameterService {

	List<Parameter> getAll();

	void save(Parameter parameter);

	Parameter getById(long id);

	void deletedById(long id);

	DataTablesOutput<Parameter> getAllFrom(@Valid DataTablesInput input);
}
