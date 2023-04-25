package com.eikona.mata.service.impl.model;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.entity.ConstraintSingle;
import com.eikona.mata.repository.ConstraintSingleRepository;
import com.eikona.mata.service.ConstraintSingleService;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class ConstraintSingleServiceImpl implements ConstraintSingleService {

	@Autowired
	protected ConstraintSingleRepository constraintSingleRepository;

	// @Override
	public ConstraintSingle find(Long id) {
		ConstraintSingle result = null;

		Optional<ConstraintSingle> catalog = constraintSingleRepository.findById(id);

		if (catalog.isPresent()) {
			result = catalog.get();
		}

		return result;
	}

	// @Override
	public List<ConstraintSingle> findAll() {
		return constraintSingleRepository.findAllByIsDeletedFalse();
	}

	// @Override
	public ConstraintSingle save(ConstraintSingle entity) {
		entity.setDeleted(false);
		return constraintSingleRepository.save(entity);
	}

	// @Override
	public void delete(Long id) {

		ConstraintSingle result = null;

		Optional<ConstraintSingle> catalog = constraintSingleRepository.findById(id);

		if (catalog.isPresent()) {
			result = catalog.get();
			result.setDeleted(true);
		}
		this.constraintSingleRepository.save(result);
	}

	// @Override
	public ConstraintSingle update(ConstraintSingle entity) {
		return constraintSingleRepository.save(entity);
	}

	public DataTablesOutput<ConstraintSingle> getAll(@Valid DataTablesInput input) {
		Specification<ConstraintSingle> isDeletedFalse = (Specification<ConstraintSingle>) (root, query, builder) -> {
			return builder.equal(root.get(ApplicationConstants.IS_DELETED), false);
		};
		return (DataTablesOutput<ConstraintSingle>) constraintSingleRepository.findAll(input, isDeletedFalse);
	}

}
