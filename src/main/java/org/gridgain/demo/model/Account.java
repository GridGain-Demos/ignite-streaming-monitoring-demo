package org.gridgain.demo.model;

import java.io.Serializable;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

public class Account implements Serializable {

	private static final long serialVersionUID = 440361101442522582L;

	@QuerySqlField(index = true)
	private String id;
	@QuerySqlField
	private String name;

	public Account(String id, String name) {
		this.id = id;
		this.setName(name);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Account [id=" + id + ", name=" + name + "]";
	}

}
