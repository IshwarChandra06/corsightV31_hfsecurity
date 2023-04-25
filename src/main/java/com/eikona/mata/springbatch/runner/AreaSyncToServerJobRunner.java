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
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class AreaSyncToServerJobRunner {
private JobLauncher simpleJobLauncher;
    
    @Qualifier("areaSyncToCorsightServer")
    private Job areaSyncToCorsightServer;

    @Autowired
    public AreaSyncToServerJobRunner(Job areaSyncToCorsightServer, JobLauncher jobLauncher) {
    	super();
        this.simpleJobLauncher = jobLauncher;
        this.areaSyncToCorsightServer = areaSyncToCorsightServer;
    }
    
	
	//@Scheduled(fixedDelay =300000)
	public void pushToCorsight() {
		runBatchJob();
	}

    @Async
    public void runBatchJob() {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addDate("date", new Date(), true);
        runJob(areaSyncToCorsightServer, jobParametersBuilder.toJobParameters());
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
