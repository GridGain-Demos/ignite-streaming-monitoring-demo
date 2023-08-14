package org.gridgain.demo.model;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

public class Product {
	@QuerySqlField(index = true)
	Long id;
	@QuerySqlField
	String symbol;
	@QuerySqlField
	String companyName;

	public Product(Long id, String symbol, String companyName) {
		this.id = id;
		this.symbol = symbol;
		this.companyName = companyName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

}
