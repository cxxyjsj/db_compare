package com.core;

import java.util.List;

/**
 * 表信息
 * @author MX
 *
 */
public class TableInfo {
	
	private String name;
	
	private List<ColumnInfo> columns;
	

	public TableInfo() {

	}

	public TableInfo(String name, List<ColumnInfo> columns) {
		super();
		this.name = name;
		this.columns = columns;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ColumnInfo> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnInfo> columns) {
		this.columns = columns;
	}

	@Override
	public String toString() {
		return "TableInfo [name=" + name + ", columns=" + columns + "]";
	}
}
 