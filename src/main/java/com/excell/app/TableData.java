package com.excell.app;

public class TableData {
	private String name;
	private String type;
	private String dataType;
	private boolean notNull;
	private boolean unique;
	public TableData(String name, String type, String dataType, boolean notNull, boolean unique) {
		this.name = name;
		this.type = type;
		this.dataType = dataType;
		this.notNull = notNull;
		this.unique = unique;
	}
	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}
	public String getDataType() {
		return dataType;
	}
	public boolean isNotNull() {
		return notNull;
	}
	public boolean isUnique() {
		return unique;
	}
}
