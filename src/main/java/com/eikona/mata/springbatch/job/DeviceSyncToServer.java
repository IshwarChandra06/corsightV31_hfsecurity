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

import com.eikona.mata.entity.Device;
import com.eikona.mata.springbatch.mapper.DeviceDBRowMapper;
import com.eikona.mata.springbatch.processor.DeviceDBProcessor;
import com.eikona.mata.springbatch.writer.DeviceAddAndUpdateWriter;
import com.eikona.mata.springbatch.writer.DeviceDeleteFromCorsighteWriter;

@Configuration
public class DeviceSyncToServer {

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private DeviceDBProcessor deviceProcessor;
    private DataSource dataSource;
    private DeviceAddAndUpdateWriter deviceDBWriter;
    private DeviceDeleteFromCorsighteWriter deviceDeleteWriter;

    @Autowired
    public DeviceSyncToServer(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, DeviceDBProcessor deviceProcessor, DataSource dataSource, DeviceAddAndUpdateWriter deviceDBWriter,DeviceDeleteFromCorsighteWriter deviceDeleteWriter){
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.deviceProcessor = deviceProcessor;
        this.dataSource = dataSource;
        this.deviceDBWriter = deviceDBWriter;
        this.deviceDeleteWriter=deviceDeleteWriter;
    }

    @Qualifier(value = "deviceSyncToCorsightServer")
    @Bean
    public Job deviceSyncToCorsightServerJob() throws Exception {
        return this.jobBuilderFactory.get("deviceSyncToCorsightServer")
                .start(step1DeviceSyncToCorsightServer())
                .next(step2DeviceDeleteFromCorsightServer())
                .build();
    }
    
    @Bean
    public Step step1DeviceSyncToCorsightServer() throws Exception {
        return this.stepBuilderFactory.get("step1DeviceSyncToCorsightServer")
    		.<Device, Device>chunk(5)
            .reader(deviceDBReader())
            .processor(deviceProcessor)
            .writer(deviceDBWriter)
            .build();
    }

    @Bean
    public ItemStreamReader<Device> deviceDBReader() {
        JdbcCursorItemReader<Device> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("select * from device where is_sync=false and is_deleted=false");
        reader.setRowMapper(new DeviceDBRowMapper());
        return reader;
    }
    
    @Bean
    public Step step2DeviceDeleteFromCorsightServer() throws Exception {
        return this.stepBuilderFactory.get("step2DeviceDeleteFromCorsightServer")
    		.<Device, Device>chunk(5)
            .reader(deletedDeviceReader())
            .processor(deviceProcessor)
            .writer(deviceDeleteWriter)
            .build();
    }  
    
    @Bean
    public ItemStreamReader<Device> deletedDeviceReader() {
        JdbcCursorItemReader<Device> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("select * from device where is_sync=true and is_deleted=true and model='Corsight'");
        reader.setRowMapper(new DeviceDBRowMapper());
        return reader;
    }
    
}

