package org.gridgain.demo.model;

import java.io.Serializable;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

public class Product implements Serializable {
	private static final long serialVersionUID = 9049637334145614908L;
	
	@QuerySqlField(index = true)
	String symbol;
	@QuerySqlField
	String name;

	public Product(String symbol, String name) {
		this.symbol = symbol;
		this.name = name;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Product [symbol=" + symbol + ", companyName=" + name + "]";
	}

}
