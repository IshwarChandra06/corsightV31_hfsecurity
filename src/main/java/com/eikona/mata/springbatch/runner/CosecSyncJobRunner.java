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
public class CosecSyncJobRunner {

    private JobLauncher simpleJobLauncher;
    
    @Qualifier("cosecSync")
   // @Autowired
    private Job cosecSync;

    @Autowired
    public CosecSyncJobRunner(Job cosecSync, JobLauncher jobLauncher) {
    	//super();
        this.simpleJobLauncher = jobLauncher;
        this.cosecSync = cosecSync;
    }
    
	
//	@Scheduled(fixedDelay =30000)
	public void cosecSync() {
		runBatchJob();
	}

    @Async
    public void runBatchJob() {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addDate("date", new Date(), true);
        runJob(cosecSync, jobParametersBuilder.toJobParameters());
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
