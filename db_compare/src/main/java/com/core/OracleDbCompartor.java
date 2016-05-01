package com.core;

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
		buf.append("SELECT TABLE_NAME,COLUMN_NAME,DATA_TYPE AS COLUMN_TYPE,DATA_LENGTH ")
			 .append(" AS COLUMN_SIZE FROM USER_TAB_COLUMNS WHERE 1=1 ");
		List<Object> params = new ArrayList<>();
		if(!StringUtils.isEmpty(namePattern)){
			buf.append(" AND TABLE_NAME LIKE ? ");
			params.add(namePattern);
		}
		buf.append(" ORDER BY TABLE_NAME");
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
	public String getAddSql(ColumnInfo col) {
		String sql = "ALTER TABLE " + col.getTableName() + " ADD (" 
				+ col.getName() + " " + col.getType();
		if(col.getSize() > 0){
			sql += "(" + col.getSize() + ")";
		}
		sql += ");";
		return sql;
	}

	@Override
	public String getModifySql(ColumnInfo col) {
		String sql = "ALTER TABLE " + col.getTableName() + " MODIFY (" 
				+ col.getName() + " " + col.getType();
		if(col.getSize() > 0){
			sql += "(" + col.getSize() + ")";
		}
		sql += ");";
		return sql;
	}
}
