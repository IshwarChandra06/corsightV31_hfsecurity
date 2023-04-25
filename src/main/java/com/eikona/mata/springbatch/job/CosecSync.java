package com.eikona.mata.springbatch.job;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.eikona.mata.entity.Transaction;
import com.eikona.mata.springbatch.mapper.CosecSyncMapper;
import com.eikona.mata.springbatch.processor.CosecSyncProcessor;
import com.eikona.mata.springbatch.writer.CosecSyncWriter;

@Configuration
public class CosecSync {


    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private CosecSyncProcessor cosecSyncProcessor;
    private DataSource dataSource;
    private CosecSyncWriter cosecSyncWriter;

    @Autowired
    public CosecSync(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, CosecSyncProcessor cosecSyncProcessor, DataSource dataSource, CosecSyncWriter cosecSyncWriter){
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.cosecSyncProcessor = cosecSyncProcessor;
        this.dataSource = dataSource;
        this.cosecSyncWriter = cosecSyncWriter;
    }

    @Qualifier(value = "cosecSync")
    @Bean
    public Job cosecSyncJob() throws Exception {
        return this.jobBuilderFactory.get("cosecSync")
                .start(step1CosecSync())
                .build();
    }
    
    @Bean
    public Step step1CosecSync() throws Exception {
        return this.stepBuilderFactory.get("step1CosecSync")
    		.<Transaction, Transaction>chunk(10)
            .reader(cosecSyncDBReader())
            .processor(cosecSyncProcessor)
            .writer(cosecSyncWriter)
            .build();
    }

    @Bean
    public ItemStreamReader<Transaction> cosecSyncDBReader() {
        JdbcCursorItemReader<Transaction> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("select * from transaction where isSync=false and accessType is not null and empId is not null");
        reader.setRowMapper(new CosecSyncMapper());
        return reader;
    }
    

    


}
