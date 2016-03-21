package com.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 列信息
 * @author MX
 *
 */
public class ColumnInfo {
	/**
	 * 列名
	 */
	private String name;
	
	/**
	 * 列类型,对应JAVA类型名称
	 */
	private String type;
	
	/**
	 * 列大小
	 */
	private int size;
	
	private String db;
	
	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public ColumnInfo() {

	}

	public ColumnInfo(String name, String type, int size) {
		super();
		this.name = name;
		this.type = type;
		this.size = size;
	}

	@Override
	public String toString() {
		return "ColumnInfo [name=" + name + ", type=" + type + ", size=" + size + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof ColumnInfo)){
			return false;
		}
		ColumnInfo ci = (ColumnInfo)obj;
		return this.name.equals(ci.getName()) && this.type.equals(ci.getType()) && this.size == ci.getSize();
	}
	
	public static void main(String[] args) {
		ColumnInfo c1 = new ColumnInfo();
		c1.setName("1");
		c1.setType("2");
		ColumnInfo c2 = new ColumnInfo();
		c2.setName("1");
		c2.setType("2");
		List<ColumnInfo> list = new ArrayList<>();
		list.add(c1);
		if(!list.contains(c2)){
			list.add(c2);
		}
		System.out.println(list);
	}
}
