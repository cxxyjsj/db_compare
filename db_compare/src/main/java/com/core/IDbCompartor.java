package com.core;

import java.sql.Connection;
import java.util.List;

import com.domain.ColumnInfo;

/**
 * 数据库对比接口
 * @author MX
 *
 */
public interface IDbCompartor {
	
	/**
	 * 查询所有表信息
	 * @param namePattern 表明匹配模式
	 * @return
	 * @throws Exception
	 */
	public List<ColumnInfo> getColumns(Connection conn,String namePattern)throws Exception;
	
	/**
	 * 获取新增列SQL
	 * @author cxxyjsj
	 * @date 2016年5月1日 下午8:51:15
	 * @param col
	 * @return
	 */
	public String getAddSql(ColumnInfo col);
	
	/**
	 * 获取更新列SQL
	 * @author cxxyjsj
	 * @date 2016年5月1日 下午8:51:30
	 * @param col
	 * @return
	 */
	public String getModifySql(ColumnInfo col);
}
