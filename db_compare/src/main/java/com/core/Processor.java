package com.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.util.DbUtil;

/**
 * 核心处理器
 * @author MX
 *
 */
public class Processor {
	/**
	 * 开始处理
	 * @throws Exception
	 */
	public static void process(String db, String namePattern)throws Exception {
		Connection conn = DbUtil.getConnection(db);
		try{
			IDbCompartor idc = getCompartor(db);
			idc.initConnection(conn);
			List<TableInfo> tables = idc.getTables(namePattern);
			save2Db(db, tables);
		}finally {
			DbUtil.closeJdbc(new Connection[]{conn}, null, null);
		}
	}
	
	public static IDbCompartor getCompartor(String db)throws Exception {
		String className = DbUtil.PROPS.getProperty(db + ".compartor");
		return (IDbCompartor)Class.forName(className).newInstance();
	}
	
	public static void save2Db(String db, List<TableInfo> tables)throws Exception {
		if(tables == null || tables.size() < 1){
			return;
		}
		Connection conn = DbUtil.getConnection("h2");
		PreparedStatement pstmt = null;
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement("INSERT INTO " + db.toUpperCase() + "_DB(VERSION_DATE,TABLE_NAME,COLUMN_NAME"
					+ ",COLUMN_TYPE,COLUMN_SIZE) VALUES(?,?,?,?,?)");
			String versionDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
			long count = 0;
			for(int i=0;i<tables.size();i++){
				TableInfo ti = tables.get(i);
				List<ColumnInfo> cols = ti.getColumns();
				for(int j=0;j<cols.size();j++){
					ColumnInfo col = cols.get(j);
					pstmt.setString(1, versionDate);
					pstmt.setString(2, ti.getName());
					pstmt.setString(3, col.getName());
					pstmt.setString(4, col.getType());
					pstmt.setInt(5, col.getSize());
					pstmt.addBatch();
					count++;
					if(count % 100 == 0){
						pstmt.executeBatch();
						pstmt.clearBatch();
					}
				}
			}
			pstmt.executeBatch();
			conn.commit();
		}catch(Exception e){
			conn.rollback();
			throw e;
		}
		finally {
			DbUtil.closeJdbc(new Connection[]{conn}, new Statement[]{pstmt}, null);
		}
	}
}
