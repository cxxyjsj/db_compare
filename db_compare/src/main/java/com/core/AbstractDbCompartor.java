package com.core;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.domain.ColumnInfo;

public abstract class AbstractDbCompartor implements IDbCompartor {
	
	protected String querySql;
	
	protected List<Object> params;
	
	protected static transient Log log = LogFactory.getLog(AbstractDbCompartor.class);

	protected List<ColumnInfo> extractColumns(ResultSet rs) throws Exception {
		List<ColumnInfo> results = new ArrayList<>();
		try {
			while(rs.next()){
				String tableName = rs.getString("TABLE_NAME");
				String columnName = rs.getString("COLUMN_NAME");
				String columnType = rs.getString("COLUMN_TYPE");
				int columnSize = rs.getInt("COLUMN_SIZE");
				ColumnInfo col = new ColumnInfo();
				col.setTableName(tableName);
				col.setName(columnName);
				col.setType(columnType);
				col.setSize(columnSize);
				results.add(col);
			}
		} catch (Exception e) {
			log.error(this,e);
		}
		return results;
	}
}
