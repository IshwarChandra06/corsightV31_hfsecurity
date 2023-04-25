package com.eikona.mata.service.impl.model;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;

import com.eikona.mata.entity.BatchJobExecution;
import com.eikona.mata.entity.BatchJobExecutionContext;
import com.eikona.mata.entity.BatchJobExecutionSeq;
import com.eikona.mata.entity.BatchJobInstance;
import com.eikona.mata.entity.BatchJobSeq;
import com.eikona.mata.entity.BatchStepExecution;
import com.eikona.mata.entity.BatchStepExecutionContext;
import com.eikona.mata.repository.BatchJobExecutionContextRepository;
import com.eikona.mata.repository.BatchJobExecutionRepository;
import com.eikona.mata.repository.BatchJobExecutionSeqRepository;
import com.eikona.mata.repository.BatchJobInstanceRepository;
import com.eikona.mata.repository.BatchJobSeqRepository;
import com.eikona.mata.repository.BatchStepExecutionContextRepository;
import com.eikona.mata.repository.BatchStepExecutionRepository;
import com.eikona.mata.repository.BatchStepExecutionSeqRepository;

@Service
public class BatchJobAndStepServiceImpl {

	@Autowired
	private BatchJobSeqRepository batchJobSeqRepository;

	@Autowired
	private BatchJobExecutionRepository batchJobExecutionRepository;

	@Autowired
	private BatchJobExecutionContextRepository batchJobExecutionContextRepository;

	@Autowired
	private BatchJobExecutionSeqRepository batchJobExecutionSeqRepository;

	@Autowired
	private BatchStepExecutionRepository batchStepExecutionRepository;

	@Autowired
	private BatchStepExecutionContextRepository batchStepExecutionContextRepository;

	@Autowired
	private BatchStepExecutionSeqRepository batchStepExecutionSeqRepository;

	@Autowired
	private BatchJobInstanceRepository batchJobInstanceRepository;

	public DataTablesOutput<BatchJobSeq> getAllBatchJobSeq(@Valid DataTablesInput input) {

		return batchJobSeqRepository.findAll(input);
	}

	public DataTablesOutput<BatchJobExecution> getAllBatchJobExecution(@Valid DataTablesInput input) {
		return batchJobExecutionRepository.findAll(input);
	}

	public DataTablesOutput<BatchJobExecutionContext> getAllBatchJobExecutionContext(@Valid DataTablesInput input) {
		return batchJobExecutionContextRepository.findAll(input);
	}

	public DataTablesOutput<BatchJobExecutionSeq> getAllBatchJobExecutionSeq(@Valid DataTablesInput input) {
		return batchJobExecutionSeqRepository.findAll(input);
	}

	public DataTablesOutput<BatchJobInstance> getAllBatchJobInstance(@Valid DataTablesInput input) {
		return batchJobInstanceRepository.findAll(input);
	}

	public DataTablesOutput<BatchStepExecution> getAllBatchStepExecution(@Valid DataTablesInput input) {
		return batchStepExecutionRepository.findAll(input);
	}

	public DataTablesOutput<BatchStepExecutionContext> getAllBatchStepExecutionContext(@Valid DataTablesInput input) {
		return batchStepExecutionContextRepository.findAll(input);
	}

	public DataTablesOutput<com.eikona.mata.entity.BatchStepExecutionSeq> getAllBatchStepExecutionSeq(
			@Valid DataTablesInput input) {
		return batchStepExecutionSeqRepository.findAll(input);
	}
}
