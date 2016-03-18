package com.domain.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * 获取结果集中第一列,第二列.
 * @author mengbin
 * @date 2015年1月22日 下午3:43:06
 */
public class DefaultMapResultSetExtractor implements ResultSetExtractor<Map<String, Object>>
{
	@Override
	public Map<String, Object> extractData(ResultSet rs) throws SQLException,
			DataAccessException
	{
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		while(rs.next())
		{
			map.put(rs.getString(1), rs.getObject(2));
		}
		return map;
	}
}
