package com.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.domain.ColumnInfo;
import com.util.DbUtil;

/**
 * Oracle数据库比较器实现
 * @author MX
 *
 */
@Service("comparator.oracle")
public class OracleDbCompartor extends AbstractDbCompartor {
	
	@Override
	public List<ColumnInfo> getColumns(Connection conn, String namePattern) throws Exception {
		StringBuilder buf = new StringBuilder();
		buf.append("SELECT B.TABLE_NAME,B.COLUMN_NAME,B.DATA_TYPE AS COLUMN_TYPE,NVL(B.DATA_PRECISION,B.DATA_LENGTH) ")
			 .append(" AS COLUMN_SIZE FROM USER_TABLES A LEFT JOIN USER_TAB_COLUMNS B ON A.TABLE_NAME ")
			 .append(" = B.TABLE_NAME WHERE 1=1 ");
		List<Object> params = new ArrayList<Object>();
		if(!StringUtils.isEmpty(namePattern)){
			buf.append(" AND A.TABLE_NAME LIKE ? ");
			params.add(namePattern);
		}
		buf.append(" ORDER BY B.TABLE_NAME,B.COLUMN_ID");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(buf.toString());
			for(int i=0;i<params.size();i++){
				pstmt.setObject(i+1, params.get(i));
			}
			rs = pstmt.executeQuery();
			return extractColumns(rs);
		} catch (Exception e) {
			log.error(this,e);
		}finally {
			DbUtil.closeJdbc(null, new Statement[]{pstmt}, new ResultSet[]{rs});
		}
		return null;
	}
	
	@Override
	public List<ColumnInfo> getViews(Connection conn) throws Exception {
		StringBuilder buf = new StringBuilder();
		buf.append("SELECT B.TABLE_NAME,B.COLUMN_NAME,B.DATA_TYPE AS COLUMN_TYPE,B.DATA_LENGTH ")
			 .append(" AS COLUMN_SIZE FROM USER_VIEWS A LEFT JOIN USER_TAB_COLUMNS B ON A.VIEW_NAME ")
			 .append(" = B.TABLE_NAME ORDER BY B.TABLE_NAME,B.COLUMN_ID");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(buf.toString());
			rs = pstmt.executeQuery();
			return extractColumns(rs);
		} catch (Exception e) {
			log.error(this,e);
		}finally {
			DbUtil.closeJdbc(null, new Statement[]{pstmt}, new ResultSet[]{rs});
		}
		return null;
	}

	@Override
	public String getAddSql(ColumnInfo col) {
		String sql = "ALTER TABLE " + col.getTableName() + " ADD (" 
				+ col.getName() + " " + col.getType();
		if(col.getSize() > 0){
			if("DATE".equals(col.getType()) || "CLOB".equals(col.getType()) || "BLOB".equals(col.getType())){
				
			}else{
				sql += "(" + col.getSize() + ")";
			}
		}
		sql += ");";
		return sql;
	}

	@Override
	public String getModifySql(ColumnInfo srcCol, ColumnInfo tarCol) {
		if(!tarCol.getType().equals(srcCol.getType())){
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			pw.append("ALTER TABLE ").append(srcCol.getTableName()).append(" ADD ").append(srcCol.getName())
			     .append("_TMP ").append(getColumnTypeStr(srcCol)).append(";").println();
			pw.append("UPDATE ").append(srcCol.getTableName()).append(" SET ").append(srcCol.getName())
				 .append("_TMP = ").append(srcCol.getName()).append(",").append(srcCol.getName()).append("=NULL;").println();;
			pw.append("ALTER TABLE ").append(srcCol.getTableName()).append(" MODIFY (").append(srcCol.getName()).append(" ")
			     .append(getColumnTypeStr(srcCol)).append(");").println();;
			pw.append("UPDATE ").append(srcCol.getTableName()).append(" SET ").append(srcCol.getName())
			     .append(" = ").append(srcCol.getName()).append("_TMP;").println();;
			pw.append("ALTER TABLE ").append(srcCol.getTableName()).append(" DROP COLUMN ")
			      .append(srcCol.getName()).append("_TMP;").println();;
			return sw.toString();
		}else{
			String sql = "ALTER TABLE " + srcCol.getTableName() + " MODIFY (" 
					+ srcCol.getName() + " " + srcCol.getType();
			if(srcCol.getSize() > 0){
				if("DATE".equals(srcCol.getType()) || "CLOB".equals(srcCol.getType()) || "BLOB".equals(srcCol.getType())){
					
				}else{
					sql += "(" + srcCol.getSize() + ")";
				}
			}
			sql += ");";
			return sql;
		}
	}
	
	private String getColumnTypeStr(ColumnInfo col) {
		String res = col.getType();
		if(col.getSize() > 0){
			if("DATE".equals(col.getType()) || "CLOB".equals(col.getType()) || "BLOB".equals(col.getType())){
				
			}else{
				res += "(" + col.getSize() + ")";
			}
		}
		return res;
	}
}
