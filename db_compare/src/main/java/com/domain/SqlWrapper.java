package com.domain;

import java.util.List;

/**
 * SQL封装
 * @author mengbin
 * @date 2014年5月7日 上午8:38:26
 */
public class SqlWrapper
{
	private String sql;
	
	private List<Object> params;

	public SqlWrapper(String sql,List<Object> params)
	{
		this.sql = sql;
		this.params = params;
	}
	
	public String getSql()
	{
		return sql;
	}

	public void setSql(String sql)
	{
		this.sql = sql;
	}

	public List<Object> getParams()
	{
		return params;
	}

	public void setParams(List<Object> params)
	{
		this.params = params;
	}
}