package com.eikona.mata.springbatch.runner;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@EnableScheduling
public class EmployeeSyncToHFSecurityJobRunner {

    private JobLauncher simpleJobLauncher;
    
    @Qualifier("employeeSyncToHFSecurity")
    @Autowired
    private Job employeeSyncToHFSecurity;

    @Autowired
    public EmployeeSyncToHFSecurityJobRunner(Job employeeSyncToHFSecurity, JobLauncher jobLauncher) {
    	super();
        this.simpleJobLauncher = jobLauncher;
        this.employeeSyncToHFSecurity = employeeSyncToHFSecurity;
    }
    
	
//	@Scheduled(fixedDelay =300000)
	public void pushToDevice() {
		runBatchJob();
	}

    @Async
    public void runBatchJob() {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addDate("date", new Date(), true);
        runJob(employeeSyncToHFSecurity, jobParametersBuilder.toJobParameters());
    }

    public void runJob(Job job, JobParameters parameters) {
        try {
            JobExecution jobExecution = simpleJobLauncher.run(job, parameters);
        }
        catch (Exception e) {
			e.printStackTrace();
		}
    }
}



