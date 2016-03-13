package com.core.db;

import java.sql.Connection;
import java.util.List;

import com.core.TableInfo;

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
	public List<TableInfo> getTables(Connection conn,String namePattern)throws Exception;
}
