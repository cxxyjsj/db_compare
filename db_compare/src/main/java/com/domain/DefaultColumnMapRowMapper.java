package com.domain;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Component;

/**
 * 默认的查询结果集映射器,返回一个MAP对象
 * 键名为大写的字段名
 * @author mengbin
 * @date 2014年2月21日 上午10:02:15
 */
public class DefaultColumnMapRowMapper extends ColumnMapRowMapper 
{
	
	public Map<String, Object> mapRow(ResultSet rs, int rowNum)
			throws SQLException
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		Map<String,Object> mapOfColValues = createColumnMap(columnCount);
		for (int i = 1; i <= columnCount; i++)
		{
			String key = getColumnKey(JdbcUtils.lookupColumnName(rsmd,
					i));
			Object obj = getColumnValue(rs, i);
			mapOfColValues.put(key.toUpperCase(), obj);
		}
		return mapOfColValues;
	}
	
}
