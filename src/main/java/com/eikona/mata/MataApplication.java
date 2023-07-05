package com.eikona.mata;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableBatchProcessing
@ComponentScan(basePackages = "com.eikona.mata")
@ComponentScan(basePackages = "com.eikona.mata.controller" )
@ComponentScan(basePackages = "com.eikona.mata.service")
@EntityScan(basePackages = {"com.eikona.mata.entity"})
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages ={"com.eikona.mata.repository"},
									repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class)
public class MataApplication {

	@Value("${mata.time-zone}")
	private String zone;
	
	@PostConstruct 
	public void init(){
		TimeZone.setDefault(TimeZone.getTimeZone("IST"));
	}
	public static void main(String[] args) {
		
		SpringApplication.run(MataApplication.class, args);
	}

}
