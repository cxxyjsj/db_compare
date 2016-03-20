package com.domain.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class DefaultSingleValuesResultSetExtractor implements ResultSetExtractor<List<Object>> {

	@Override
	public List<Object> extractData(ResultSet rs) throws SQLException, DataAccessException {
		List<Object> list = new ArrayList<>();
		while(rs.next()){
			list.add(rs.getObject(1));
		}
		return list;
	}
	
}
