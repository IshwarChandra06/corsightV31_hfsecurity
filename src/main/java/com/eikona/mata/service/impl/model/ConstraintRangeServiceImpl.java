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
import com.eikona.mata.entity.ConstraintRange;
import com.eikona.mata.repository.ConstraintRangeRepository;
import com.eikona.mata.service.ConstraintRangeService;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class ConstraintRangeServiceImpl implements ConstraintRangeService {

    @Autowired
    protected ConstraintRangeRepository constraintRangeRepository;

    //@Override
    public ConstraintRange find(Long id) {
        ConstraintRange result = null;

        Optional<ConstraintRange> catalog = constraintRangeRepository.findById(id);

        if (catalog.isPresent()) {
            result = catalog.get();
        }

        return result;
    }

    //@Override
    public List<ConstraintRange> findAll() {
        return (List<ConstraintRange>) constraintRangeRepository.findAllByIsDeletedFalse();
    }

    //@Override
    public ConstraintRange save(ConstraintRange entity) {
    	entity.setDeleted(false);
        return constraintRangeRepository.save(entity);
    }

    //@Override
    public void delete(Long id) {
    	ConstraintRange result = null;

        Optional<ConstraintRange> catalog = constraintRangeRepository.findById(id);

        if (catalog.isPresent()) {
            result = catalog.get();
            result.setDeleted(true);
        }
    	
    	 this.constraintRangeRepository.save(result);
    }

    //@Override
    public ConstraintRange update(ConstraintRange entity) {
        return constraintRangeRepository.save(entity);
    }
    
    public DataTablesOutput<ConstraintRange> getAll(@Valid DataTablesInput input){
   	 Specification<ConstraintRange> isDeletedFalse = (Specification<ConstraintRange>)(root, query, builder)->{
       		return builder.equal(root.get(ApplicationConstants.IS_DELETED), false);
       	};
       	return (DataTablesOutput<ConstraintRange>) constraintRangeRepository.findAll(input,isDeletedFalse);
       }
}
