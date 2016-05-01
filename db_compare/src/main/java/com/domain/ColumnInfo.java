package com.domain;

/**
 * 列信息
 * @author MX
 *
 */
public class ColumnInfo {
	/**
	 * 表名
	 */
	private String tableName;
	
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

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public ColumnInfo() {

	}

	public ColumnInfo(String tableName,String name, String type, int size) {
		super();
		this.tableName = tableName;
		this.name = name;
		this.type = type;
		this.size = size;
	}

	@Override
	public String toString() {
		return "ColumnInfo [tableName=" + tableName + ",name=" + name + ", type=" + type + ", size=" + size + "]";
	}
	
	public String desc() {
		return "[" + name + " " + type + "(" + size + ")]";
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof ColumnInfo)){
			return false;
		}
		ColumnInfo ci = (ColumnInfo)obj;
		return this.tableName.equals(ci.getTableName()) && this.name.equals(ci.getName()) && this.type.equals(ci.getType()) && this.size == ci.getSize();
	}
}
