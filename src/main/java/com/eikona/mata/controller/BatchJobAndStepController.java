package com.eikona.mata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eikona.mata.entity.BatchJobExecution;
import com.eikona.mata.entity.BatchJobExecutionContext;
import com.eikona.mata.entity.BatchJobExecutionSeq;
import com.eikona.mata.entity.BatchJobInstance;
import com.eikona.mata.entity.BatchJobSeq;
import com.eikona.mata.entity.BatchStepExecution;
import com.eikona.mata.entity.BatchStepExecutionContext;
import com.eikona.mata.entity.BatchStepExecutionSeq;
import com.eikona.mata.service.impl.model.BatchJobAndStepServiceImpl;

@Controller
public class BatchJobAndStepController {

	@Autowired
	private BatchJobAndStepServiceImpl batchJobAndStepServiceImpl;

	@GetMapping("/batchjobseq")
	public String batchJobSeq() {

		return "batch/batch_job_seq";
	}

	@GetMapping("/api/batchjobseq")
	public @ResponseBody DataTablesOutput<BatchJobSeq> batchJobSeqDatatable(@Valid DataTablesInput input) {
		 return (DataTablesOutput<BatchJobSeq>) batchJobAndStepServiceImpl.getAllBatchJobSeq(input);
	}

	@GetMapping("/batchjobexecution")
	public String batchJobExecution() {

		return "batch/batch_job_execution";
	}

	@GetMapping("/api/batchjobexecution")
	public @ResponseBody DataTablesOutput<BatchJobExecution> batchjobexecutionDatatable(@Valid DataTablesInput input) {
		 return (DataTablesOutput<BatchJobExecution>) batchJobAndStepServiceImpl.getAllBatchJobExecution(input);
	}

	@GetMapping("/batchjobexecutioncontext")
	public String batchJobExecutionContext() {

		return "batch/batch_job_execution_context";
	}

	@GetMapping("/api/batchjobexecutioncontext")
	public @ResponseBody DataTablesOutput<BatchJobExecutionContext> batchJobExecutionContextDatatable(@Valid DataTablesInput input) {
		 return (DataTablesOutput<BatchJobExecutionContext>) batchJobAndStepServiceImpl.getAllBatchJobExecutionContext(input);
	}

	@GetMapping("/batchjobexecutionseq")
	public String batchJobExecutionSeq() {

		return "batch/batch_job_execution_seq";
	}

	@GetMapping("/api/batchjobexecutionseq")
	public @ResponseBody DataTablesOutput<BatchJobExecutionSeq> batchJobExecutionSeqDatatable(@Valid DataTablesInput input) {
		 return (DataTablesOutput<BatchJobExecutionSeq>) batchJobAndStepServiceImpl.getAllBatchJobExecutionSeq(input);
	}

	@GetMapping("/batchjobinstance")
	public String batchJobInstance() {

		return "batch/batch_job_instance";
	}

	@GetMapping("/api/batchjobinstance")
	public @ResponseBody DataTablesOutput<BatchJobInstance> batchJobInstanceDatatable(@Valid DataTablesInput input) {
		 return (DataTablesOutput<BatchJobInstance>) batchJobAndStepServiceImpl.getAllBatchJobInstance(input);
	}

	@GetMapping("/batchstepexecution")
	public String batchStepExecution() {

		return "batch/batch_step_execution";
	}

	@GetMapping("/api/batchstepexecution")
	public @ResponseBody DataTablesOutput<BatchStepExecution> batchstepexecutionDatatable(@Valid DataTablesInput input) {
		 return (DataTablesOutput<BatchStepExecution>) batchJobAndStepServiceImpl.getAllBatchStepExecution(input);
	}

	@GetMapping("/batchstepexecutioncontext")
	public String batchstepexecutioncontext() {
		return "batch/batch_step_execution_context";
	}

	@GetMapping("/api/batchstepexecutioncontext")
	public @ResponseBody DataTablesOutput<BatchStepExecutionContext> batchStepExecutionContextDatatable(@Valid DataTablesInput input) {
		 return (DataTablesOutput<BatchStepExecutionContext>) batchJobAndStepServiceImpl.getAllBatchStepExecutionContext(input);
	}

	@GetMapping("/batchstepexecutionseq")
	public String batchstepexecutionseq() {
		return "batch/batch_step_execution_seq";
	}

	@GetMapping("/api/batchstepexecutionseq")
	public @ResponseBody DataTablesOutput<BatchStepExecutionSeq> batchstepexecutionseqDatatable(@Valid DataTablesInput input) {
		 return (DataTablesOutput<BatchStepExecutionSeq>) batchJobAndStepServiceImpl.getAllBatchStepExecutionSeq(input);
	}
}
