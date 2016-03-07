package com.main;

import java.util.List;

import com.core.TableInfo;
import com.core.impl.OracleDbCompartor;
import com.util.DbUtil;

public class TestMain {
	public static void main(String[] args)throws Exception {
		OracleDbCompartor odc = new OracleDbCompartor();
		odc.initConnection(DbUtil.getConn());
		List<TableInfo> tables = odc.getTables(null);
		System.out.println(tables);
	}
}
