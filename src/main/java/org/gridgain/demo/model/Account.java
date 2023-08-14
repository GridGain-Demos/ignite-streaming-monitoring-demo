package org.gridgain.demo.model;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

public class Account {

	@QuerySqlField(index = true)
	Long id;
	@QuerySqlField
	private
	String name;

	public Account(Long id, String name) {
		this.id = id;
		this.setName(name);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
