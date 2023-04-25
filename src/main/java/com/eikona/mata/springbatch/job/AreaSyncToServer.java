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
import org.springframework.context.annotation.Primary;

import com.eikona.mata.entity.Area;
import com.eikona.mata.springbatch.mapper.AreaDBRowMapper;
import com.eikona.mata.springbatch.processor.AreaDBProcessor;
import com.eikona.mata.springbatch.writer.AreaAddAndUpdateToCorsightWriter;
import com.eikona.mata.springbatch.writer.AreaDeleteFromCorsightWriter;
import com.eikona.mata.springbatch.writer.AreaDeviceAssociationWriter;

@Configuration
public class AreaSyncToServer {

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private AreaDBProcessor areaProcessor;
    private DataSource dataSource;
    private AreaAddAndUpdateToCorsightWriter areaDBWriter;
    private AreaDeleteFromCorsightWriter areaDeleteWriter;
    private AreaDeviceAssociationWriter areaDeviceAssociationWriter;

    @Autowired
    public AreaSyncToServer(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, AreaDBProcessor areaProcessor, DataSource dataSource, AreaAddAndUpdateToCorsightWriter areaDBWriter ,AreaDeleteFromCorsightWriter areaDeleteWriter,AreaDeviceAssociationWriter areaDeviceAssociationWriter){
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.areaProcessor = areaProcessor;
        this.dataSource = dataSource;
        this.areaDBWriter = areaDBWriter;
        this.areaDeleteWriter=areaDeleteWriter;
        this.areaDeviceAssociationWriter=areaDeviceAssociationWriter;
    }

    @Qualifier(value = "areaSyncToCorsightServer")
    @Bean
    public Job areaSyncToCorsightServerJob() throws Exception {
        return this.jobBuilderFactory.get("areaSyncToCorsightServer")
                .start(step1AreaSyncToCorsightServer())
                .next(step2AreaDeleteFromCorsightServer())
                .next(step3AreaDeviceAssociation())
                .build();
    }
    
    @Bean
    public Step step1AreaSyncToCorsightServer() throws Exception {
        return this.stepBuilderFactory.get("step1AreaSyncToCorsightServer")
    		.<Area, Area>chunk(5)
            .reader(areaDBReader())
            .processor(areaProcessor)
            .writer(areaDBWriter)
            .build();
    }

    @Bean
    public ItemStreamReader<Area> areaDBReader() {
        JdbcCursorItemReader<Area> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("select * from area where is_sync=false and is_deleted=false and watchlist is not null");
        reader.setRowMapper(new AreaDBRowMapper());
        return reader;
    }
    
    @Bean
    public Step step2AreaDeleteFromCorsightServer() throws Exception {
        return this.stepBuilderFactory.get("step2AreaDeleteFromCorsightServer")
    		.<Area, Area>chunk(5)
            .reader(deletedAreaReader())
            .processor(areaProcessor)
            .writer(areaDeleteWriter)
            .build();
    }  
    
    @Bean
    public ItemStreamReader<Area> deletedAreaReader() {
        JdbcCursorItemReader<Area> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("select * from area where is_sync=true and is_deleted=true and watchlist is not null'");
        reader.setRowMapper(new AreaDBRowMapper());
        return reader;
    }
    
    @Bean
    public Step step3AreaDeviceAssociation() throws Exception {
        return this.stepBuilderFactory.get("step3AreaDeviceAssociation")
    		.<Area, Area>chunk(5)
            .reader(areaDBReader())
            .processor(areaProcessor)
            .writer(areaDeviceAssociationWriter)
            .build();
    }

    @Bean
    public ItemStreamReader<Area> areaDeviceAssociationDBReader() {
        JdbcCursorItemReader<Area> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("select * from area where is_sync=true and is_deleted=false and watchlist_id is not null");
        reader.setRowMapper(new AreaDBRowMapper());
        return reader;
    }
    
}
