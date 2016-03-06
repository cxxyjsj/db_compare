package com.core.impl;

import java.sql.Connection;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.core.IDbCompartor;
import com.core.TableInfo;

/**
 * Oracle数据库比较器实现
 * @author MX
 *
 */
public class OracleDbCompartor implements IDbCompartor {
	
	private Connection conn;

	@Override
	public void initConnection(Connection conn) throws Exception {
		this.conn = conn;
	}

	@Override
	public List<TableInfo> getTables(String namePattern) throws Exception {
		
		CountDownLatch cdl = new CountDownLatch(10);
		cdl.await();
		return null;
	}

}
