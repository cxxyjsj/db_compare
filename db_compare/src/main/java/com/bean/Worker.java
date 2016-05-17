package com.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.domain.ColumnInfo;
import com.util.DbUtil;
import com.util.StringUtil;

/**
 * @author cxxyjsj
 * @date 2016年5月1日 下午5:48:51
 */
public class Worker extends Thread {
	
	private List<Object> tableNames;
	
	private String srcId;
	
	private String tarId;
	
	private List<String> diffTables = new ArrayList<String>();
	
	public List<String> getDiffTables() {
		return diffTables;
	}

	public Worker(String srcId, String tarId, List<Object> tableNames) {
		this.srcId = srcId;
		this.tarId = tarId;
		this.tableNames = tableNames;
	}

	@Override
	public void run() {
		try {
			String sql = "SELECT TABLE_NAME,COLUMN_NAME,COLUMN_TYPE,COLUMN_SIZE FROM "
					+ "DB_DETAIL WHERE VERSION_ID = ? AND TABLE_NAME IN("
					+ StringUtil.joinSql(tableNames) + ")";
			List<ColumnInfo> srcList = DbUtil.queryColumns(sql ,  srcId);
			List<ColumnInfo> tarList = DbUtil.queryColumns(sql ,  tarId);
			List<ColumnInfo> srcList2 = new ArrayList<ColumnInfo>(Arrays.asList(new ColumnInfo[srcList.size()]));
			Collections.copy(srcList2, srcList);
			srcList.removeAll(tarList);
			for(ColumnInfo col : srcList){
				String tableName = col.getTableName();
				if(!diffTables.contains(tableName)){
					diffTables.add(tableName);
				}
			}
			tarList.removeAll(srcList2);
			for(ColumnInfo col : tarList){
				String tableName = col.getTableName();
				if(!diffTables.contains(tableName)){
					diffTables.add(tableName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
