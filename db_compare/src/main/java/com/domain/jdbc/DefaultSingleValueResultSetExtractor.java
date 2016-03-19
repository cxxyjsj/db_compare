package com.domain.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * 获取记录第一行第一列
 * @author mengbin
 * @date 2014年8月25日 下午5:04:07
 */
public class DefaultSingleValueResultSetExtractor implements ResultSetExtractor<Object>
{
	public Object extractData(ResultSet rs) throws SQLException,
			DataAccessException
	{
		if(rs.next())
		{
			return rs.getObject(1);
		}
		return null;
	}
}
