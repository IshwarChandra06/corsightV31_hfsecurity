package com.eikona.mata.service.impl.model;



import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.ParameterConstants;
import com.eikona.mata.entity.Parameter;
import com.eikona.mata.repository.ParameterRepository;
import com.eikona.mata.service.ParameterService;

@Service
public class ParameterServiceImpl implements ParameterService{
	
	@Autowired
	private ParameterRepository parameterRepository;

	@Override
	public List<Parameter> getAll() {
		return parameterRepository.findAllByIsDeletedFalse();
	}

	@Override
	public void save(Parameter parameter) {
		parameter.setDeleted(false);
		this.parameterRepository.save(parameter);
	}

	@Override
	public Parameter getById(long id) {
		Optional<Parameter> optional = parameterRepository.findById(id);
		Parameter parameter = null;
		if (optional.isPresent()) {
			parameter = optional.get();
		} else {
			throw new RuntimeException(ParameterConstants.PARAMETER_NOT_FOUND + id);
		}
		return parameter;
	}

	@Override
	public void deletedById(long id) {
		Optional<Parameter> optional = parameterRepository.findById(id);
		Parameter parameter = null;
		if (optional.isPresent()) {
			parameter = optional.get();
			parameter.setDeleted(true);
		} else {
			throw new RuntimeException(ParameterConstants.PARAMETER_NOT_FOUND + id);
		}
		this.parameterRepository.save(parameter);
	}
	
	 @Override
	    public DataTablesOutput<Parameter> getAllFrom(@Valid DataTablesInput input){
		 Specification<Parameter> isDeletedFalse = (Specification<Parameter>)(root, query, builder)->{
	    		return builder.equal(root.get(ApplicationConstants.IS_DELETED), false);
	    	};
	    	return (DataTablesOutput<Parameter>) parameterRepository.findAll(input,isDeletedFalse);
	    }
	

}
