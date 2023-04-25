package com.eikona.mata.springbatch.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.eikona.mata.entity.Area;

@Component
public class AreaDBRowMapper implements RowMapper<Area> {

	 @Override
	    public Area mapRow(ResultSet resultSet, int i) throws SQLException {
	    	
		 Area area = new Area();
		 area.setId(resultSet.getLong("id"));
	        return area;
	    }
}
