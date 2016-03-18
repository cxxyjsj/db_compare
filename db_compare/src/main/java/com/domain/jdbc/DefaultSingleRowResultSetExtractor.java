package com.domain.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 * 获取第一行值
 * @author mengbin
 * @date 2015年1月22日 下午3:35:38
 */
public class DefaultSingleRowResultSetExtractor implements
		ResultSetExtractor<Map<String, Object>>
{
	@Override
	public Map<String, Object> extractData(ResultSet rs) throws SQLException,
			DataAccessException
	{
		if(rs.next())
		{
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			Map<String,Object> mapOfColValues = new LinkedCaseInsensitiveMap<Object>(columnCount);
			for (int i = 1; i <= columnCount; i++)
			{
				String key = JdbcUtils.lookupColumnName(rsmd,i);
				Object obj =  JdbcUtils.getResultSetValue(rs, i);
				mapOfColValues.put(key.toUpperCase(), obj);
			}
			return mapOfColValues;
		}
		return null;
	}
}
