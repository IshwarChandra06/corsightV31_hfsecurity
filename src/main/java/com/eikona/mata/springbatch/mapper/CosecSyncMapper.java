package com.eikona.mata.springbatch.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.eikona.mata.entity.Transaction;

public class CosecSyncMapper implements RowMapper<Transaction> {

    @Override
    public Transaction mapRow(ResultSet resultSet, int i) throws SQLException {
    	
    	Transaction transaction = new Transaction();
    	transaction.setId(resultSet.getLong("id"));
//    	transaction.setPunchDate(resultSet.getDate("punch_date"));
//    	transaction.setPoiId(resultSet.getString("poi_id"));
//    	transaction.setAccessType(resultSet.getString("access_type"));
//    	transaction.setEmpId(resultSet.getString("emp_id"));
        return transaction;
    }

}
