package com.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.domain.ColumnInfo;
import com.domain.TableInfo;
import com.util.DbUtil;

/**
 * MySQL实现方式
 * @author mengbin
 * @date 2016年4月28日 下午5:01:14
 */
@Service("comparator.mysql")
public class MySqlDbCompartor implements IDbCompartor {
	
	private static transient Log log = LogFactory.getLog(MySqlDbCompartor.class);
	
	@Override
	public List<TableInfo> getTables(Connection conn, String namePattern) throws Exception {
		String schema = conn.getCatalog();
		StringBuilder buf = new StringBuilder();
		List<Object> params = new ArrayList<>();
		buf.append("SELECT TABLE_NAME,COLUMN_NAME,DATA_TYPE AS COLUMN_TYPE, ")
			 .append("CHARACTER_MAXIMUM_LENGTH AS COLUMN_SIZE ")
			 .append("FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ? ");
		params.add(schema);
		if(!StringUtils.isEmpty(namePattern)){
			buf.append(" AND TABLE_NAME LIKE ? ");
			params.add(namePattern);
		}
		List<TableInfo> results = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(buf.toString());
			for(int i=0;i<params.size();i++){
				pstmt.setObject(i+1, params.get(i));
			}
			rs = pstmt.executeQuery();
			TableInfo table = null;
			while(rs.next()){
				String tableName = rs.getString("TABLE_NAME");
				String columnName = rs.getString("COLUMN_NAME");
				String columnType = rs.getString("COLUMN_TYPE");
				int columnSize = rs.getInt("COLUMN_SIZE");
				if(table == null || !tableName.equals(table.getName())){
					table = new TableInfo();
					table.setName(tableName);
					results.add(table);
				}
				ColumnInfo col = new ColumnInfo();
				col.setName(columnName);
				col.setType(columnType);
				col.setSize(columnSize);
				List<ColumnInfo> cols = table.getColumns();
				if(cols == null){
					cols = new ArrayList<>();
					table.setColumns(cols);
				}
				cols.add(col);
			}
		} catch (Exception e) {
			log.error(this,e);
		}finally{
			DbUtil.closeJdbc(null, new Statement[]{pstmt}, new ResultSet[]{rs});
		}
		return results;
	}
}
