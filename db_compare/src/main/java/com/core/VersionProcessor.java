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

/**
 * 版本处理器
 * @author MX
 * @date 2016年3月19日 下午7:36:13
 */
@Component
public class VersionProcessor {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public void process(final String versionId,Connection conn, IDbCompartor compartor)throws Exception {
		final List<ColumnInfo> columns = compartor.getColumns(conn, null);
		if(columns == null || columns.size() < 1){
			return;
		}
		jdbcTemplate.batchUpdate("INSERT INTO DB_DETAIL(VERSION_ID,TABLE_NAME,COLUMN_NAME,COLUMN_TYPE,COLUMN_SIZE) VALUES (?,?,?,?,?)", 
				columns,100,new ParameterizedPreparedStatementSetter<ColumnInfo>() {
				@Override
				public void setValues(PreparedStatement pstmt, ColumnInfo column) throws SQLException {
					pstmt.setObject(1, versionId);
					pstmt.setObject(2, column.getTableName());
					pstmt.setObject(3, column.getName());
					pstmt.setObject(4, column.getType());
					pstmt.setObject(5, column.getSize());
				}
		});
	}
}
