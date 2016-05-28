package com.core;

import java.io.PrintWriter;
import java.io.StringWriter;
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
		List<ColumnInfo> results = new ArrayList<ColumnInfo>();
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
	
	@Override
	public String getCreateSql(String tableName, List<ColumnInfo> cols) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.append("CREATE TABLE ").append(tableName).println("(");
		for(int i=0;i<cols.size();i++){
			ColumnInfo col = cols.get(i);
			pw.append("\t").append(col.getName()).append(" ").append(col.getType());
			String type = col.getType();
			if(!"COLB".equals(type) && !"BLOB".equals(type) && col.getSize() > 0){
				pw.append("(").append(col.getSize() + "").append(")");
			}
			if(i < cols.size() - 1){
				pw.append(",");
			}
			pw.println();
		}
		pw.println(");");
		return sw.toString();
	}
}
