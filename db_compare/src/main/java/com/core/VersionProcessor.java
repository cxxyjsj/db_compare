package com.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.stereotype.Component;

import com.domain.ColumnInfo;
import com.domain.TableInfo;

/**
 * 版本处理器
 * @author MX
 * @date 2016年3月19日 下午7:36:13
 */
@Component
public class VersionProcessor {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public void process(String versionId,Connection conn, IDbCompartor compartor)throws Exception {
		final List<TableInfo> tables = compartor.getTables(conn, null);
		if(tables == null || tables.size() < 1){
			return;
		}
		jdbcTemplate.batchUpdate("INSERT INTO DB_DETAIL(VERSION_ID,TABLE_NAME,COLUMN_NAME,COLUMN_TYPE,COLUMN_SIZE) VALUES (?,?,?,?,?)", 
				tables,100,new ParameterizedPreparedStatementSetter<TableInfo>() {
				@Override
				public void setValues(PreparedStatement pstmt, TableInfo table) throws SQLException {
					List<ColumnInfo> cols = table.getColumns();
					for(int j=0;j<cols.size();j++){
						ColumnInfo col = cols.get(j);
						pstmt.setObject(1, versionId);
						pstmt.setObject(2, table.getName());
						pstmt.setObject(3, col.getName());
						pstmt.setObject(4, col.getType());
						pstmt.setObject(5, col.getSize());
					}
				}
		});
	}
}
