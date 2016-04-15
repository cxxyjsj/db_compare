package com.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public Map<String, ColumnInfo> getColumnsMap() {
		Map<String, ColumnInfo> map = new HashMap<>();
		for(ColumnInfo ci : columns){
			map.put(ci.getName(), ci);
		}
		return map;
	}

	public void setColumns(List<ColumnInfo> columns) {
		this.columns = columns;
	}

	@Override
	public String toString() {
		return "TableInfo [name=" + name + ", columns=" + columns + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof TableInfo)){
			return false;
		}
		TableInfo target = (TableInfo)obj;
		if(!getName().equals(target.getName())){
			return false;
		}
		if(getColumns().size() != target.getColumns().size()){
			return false;
		}
		Map<String, ColumnInfo> targetCols = target.getColumnsMap();
		for(ColumnInfo ci : columns){
			String name = ci.getName();
			if(!targetCols.containsKey(name)){
				return false;
			}
			if(!ci.equals(targetCols.get(name))){
				return false;
			}
			targetCols.remove(name); // 移除目标
		}
		if(targetCols.size() > 0){
			return false;
		}
		return true;
	}
}
 