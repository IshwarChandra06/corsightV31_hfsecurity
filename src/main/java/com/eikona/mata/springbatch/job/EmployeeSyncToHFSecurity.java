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

import com.eikona.mata.entity.Employee;
import com.eikona.mata.springbatch.mapper.EmployeeDBRowMapper;
import com.eikona.mata.springbatch.processor.EmployeeDBProcessor;
import com.eikona.mata.springbatch.writer.EmployeeSyncToHFSecurityWriter;
import com.eikona.mata.springbatch.writer.FaceSyncToHFSecurityWriter;

@Configuration
public class EmployeeSyncToHFSecurity {
	
	    private JobBuilderFactory jobBuilderFactory;
	    private StepBuilderFactory stepBuilderFactory;
	    private EmployeeDBProcessor employeeProcessor;
	    private DataSource dataSource;
	    private EmployeeSyncToHFSecurityWriter employeeSyncToHFSecurityWriter;
	    private FaceSyncToHFSecurityWriter faceSyncToHFSecurityWriter;
	    
	    @Autowired
	    public EmployeeSyncToHFSecurity(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, EmployeeDBProcessor employeeProcessor, DataSource dataSource,EmployeeSyncToHFSecurityWriter employeeSyncToHFSecurityWriter,FaceSyncToHFSecurityWriter faceSyncToHFSecurityWriter ){
	        this.jobBuilderFactory = jobBuilderFactory;
	        this.stepBuilderFactory = stepBuilderFactory;
	        this.employeeProcessor = employeeProcessor;
	        this.dataSource = dataSource;
	        this.employeeSyncToHFSecurityWriter=employeeSyncToHFSecurityWriter;
	        this.faceSyncToHFSecurityWriter=faceSyncToHFSecurityWriter;
	    }
	    @Qualifier(value = "employeeSyncToHFSecurity")
	    @Bean
	    public Job employeeSyncToHFSecurityJob() throws Exception {
	        return this.jobBuilderFactory.get("employeeSyncToHFSecurity")
	                .start(step1EmployeeSyncToHFSecurity())
	                //.next(step2FaceSyncToHFSecurity())
	                .build();
	    }
	    
		@Bean
	    public Step step1EmployeeSyncToHFSecurity() throws Exception {
	        return this.stepBuilderFactory.get("step1EmployeeSyncToHFSecurity")
	    		.<Employee, Employee>chunk(100)
	            .reader(employeeDBReader())
	            .processor(employeeProcessor)
	            .writer(employeeSyncToHFSecurityWriter)
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
	    public Step step2FaceSyncToHFSecurity() throws Exception {
	        return this.stepBuilderFactory.get("step2FaceSyncToHFSecurity")
	    		.<Employee, Employee>chunk(100)
	            .reader(faceDBReader())
	            .processor(employeeProcessor)
	            .writer(faceSyncToHFSecurityWriter)
	            .build();
	    }

	    @Bean
	    public ItemStreamReader<Employee> faceDBReader() {
	        JdbcCursorItemReader<Employee> reader = new JdbcCursorItemReader<>();
	        reader.setDataSource(dataSource);
	        reader.setSql("select * from employee where is_sync=true and is_deleted=false and is_face_sync=false");
	        reader.setRowMapper(new EmployeeDBRowMapper());
	        return reader;
	    } 
	    
}
