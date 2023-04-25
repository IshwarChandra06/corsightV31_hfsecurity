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

import com.eikona.mata.entity.Employee;
import com.eikona.mata.springbatch.mapper.EmployeeDBRowMapper;
import com.eikona.mata.springbatch.processor.EmployeeDBProcessor;
import com.eikona.mata.springbatch.writer.AreaEmployeeAssociationWriter;
import com.eikona.mata.springbatch.writer.EmployeeAddAndUpdateToDeviceWriter;
import com.eikona.mata.springbatch.writer.EmployeeDeleteFromDeviceWriter;
 
@Configuration
public class EmployeeSyncToDevice {
	
    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private EmployeeDBProcessor employeeProcessor;
    private DataSource dataSource;
    private EmployeeAddAndUpdateToDeviceWriter employeeDBWriter;
    private EmployeeDeleteFromDeviceWriter employeedeleteWriter;
    private AreaEmployeeAssociationWriter areaEmployeeAssociationWriter;

    @Autowired
    public EmployeeSyncToDevice(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, EmployeeDBProcessor employeeProcessor, DataSource dataSource, EmployeeAddAndUpdateToDeviceWriter employeeDBWriter, EmployeeDeleteFromDeviceWriter employeedeleteWriter,AreaEmployeeAssociationWriter areaEmployeeAssociationWriter){
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.employeeProcessor = employeeProcessor;
        this.dataSource = dataSource;
        this.employeeDBWriter = employeeDBWriter;
        this.employeedeleteWriter=employeedeleteWriter;
        this.areaEmployeeAssociationWriter=areaEmployeeAssociationWriter;
    }

    @Qualifier(value = "employeeSyncToDevice")
    @Primary
    @Bean
    public Job employeeSyncToDeviceJob() throws Exception {
        return this.jobBuilderFactory.get("employeeSyncToDevice")
                .start(step1EmployeeSyncToDevice())
               // .next(step2EmployeeDeleteFromDevice())
               // .next(step3AreaEmployeeAssociation())//required for corsight only
                .build();
    }
    
    @Bean
    public Step step1EmployeeSyncToDevice() throws Exception {
        return this.stepBuilderFactory.get("step1EmployeeSyncToDevice")
    		.<Employee, Employee>chunk(100)
            .reader(employeeDBReader())
            .processor(employeeProcessor)
            .writer(employeeDBWriter)
            .build();
    }

    @Bean
    public ItemStreamReader<Employee> employeeDBReader() {
        JdbcCursorItemReader<Employee> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("select * from employee where is_sync=false and is_deleted=false");
        reader.setRowMapper(new EmployeeDBRowMapper());
        return reader;
    }
    
    @Bean
    public Step step2EmployeeDeleteFromDevice() throws Exception {
        return this.stepBuilderFactory.get("step2EmployeeDeleteFromDevice")
    		.<Employee, Employee>chunk(100)
            .reader(deletedEmployeeReader())
            .processor(employeeProcessor)
            .writer(employeedeleteWriter)
            .build();
    }  
    
    @Bean
    public ItemStreamReader<Employee> deletedEmployeeReader() {
        JdbcCursorItemReader<Employee> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("select * from employee where is_sync=true and is_deleted=true");
        reader.setRowMapper(new EmployeeDBRowMapper());
        return reader;
    }
    
    @Bean
    public Step step3AreaEmployeeAssociation() throws Exception {
        return this.stepBuilderFactory.get("step3AreaEmployeeAssociation")
    		.<Employee, Employee>chunk(100)
            .reader(areaEmployeeAssociationReader())
            .processor(employeeProcessor)
            .writer(areaEmployeeAssociationWriter)
            .build();
    }  
     
    @Bean
    public ItemStreamReader<Employee> areaEmployeeAssociationReader() {
        JdbcCursorItemReader<Employee> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("select * from employee where is_sync=true and is_deleted=false");
        reader.setRowMapper(new EmployeeDBRowMapper());
        return reader;
    }
    
}

