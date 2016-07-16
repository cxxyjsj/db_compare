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
 * MySQL实现方式
 * @author mengbin
 * @date 2016年4月28日 下午5:01:14
 */
@Service("comparator.mysql")
public class MySqlDbCompartor extends AbstractDbCompartor {
	
	@Override
	public List<ColumnInfo> getColumns(Connection conn, String namePattern) throws Exception {
		StringBuilder buf = new StringBuilder();
		buf.append("SELECT TABLE_NAME,COLUMN_NAME,DATA_TYPE AS COLUMN_TYPE, ")
			 .append("CHARACTER_MAXIMUM_LENGTH AS COLUMN_SIZE ")
			 .append("FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ? ");
		List<Object> params = new ArrayList<Object>();
		params.add(conn.getCatalog());
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
	public List<ColumnInfo> getViews(Connection conn) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAddSql(ColumnInfo col) {
		String sql = "ALTER TABLE " + col.getTableName() + " ADD COLUMN " 
				+ col.getName() + " " + col.getType();
		if(col.getSize() > 0){
			sql += "(" + col.getSize() + ");";
		}
		return sql;
	}

	@Override
	public String getModifySql(ColumnInfo srcCol,ColumnInfo tarCol) {
		String sql = "ALTER TABLE " + srcCol.getTableName() + " CHANGE " 
				+ srcCol.getName() + " " + srcCol.getType();
		if(srcCol.getSize() > 0){
			sql += "(" + srcCol.getSize() + ");";
		}
		return sql;
	}
}
