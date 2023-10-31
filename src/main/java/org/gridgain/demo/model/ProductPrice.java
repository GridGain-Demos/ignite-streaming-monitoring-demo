package org.gridgain.demo.model;

import java.io.Serializable;

import org.apache.ignite.cache.affinity.AffinityKeyMapped;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

public class ProductPrice implements Serializable {

	private static final long serialVersionUID = -8705621765983545827L;
	
	@QuerySqlField(index = true)
	private String id;
	@QuerySqlField
	private long time;
	@QuerySqlField(index = true)
	@AffinityKeyMapped
	private String symbol;
	@QuerySqlField
	private double price;

	public ProductPrice(String id, long time, String symbol, double price) {
		this.id = id;
		this.time = time;
		this.symbol = symbol;
		this.price = price;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getTime() {
		return time;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "ProductPrice [id=" + id + ", time=" + time + ", symbol=" + symbol + ", price=" + price + "]";
	}

}
