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
	 * 设置数据库连接对象
	 * @param conn
	 * @throws Exception
	 */
	public void initConnection(Connection conn)throws Exception;
	
	/**
	 * 查询所有表信息
	 * @param namePattern 表明匹配模式
	 * @return
	 * @throws Exception
	 */
	public List<TableInfo> getTables(String namePattern)throws Exception;
}
