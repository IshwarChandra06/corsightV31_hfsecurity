package com.eikona.mata.springbatch.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.eikona.mata.entity.Device;
@Component
public class DeviceDBRowMapper implements RowMapper<Device> {

	 @Override
	    public Device mapRow(ResultSet resultSet, int i) throws SQLException {
	    	
		 Device device = new Device();
		 device.setId(resultSet.getLong("id"));
	        return device;
	    }

}
