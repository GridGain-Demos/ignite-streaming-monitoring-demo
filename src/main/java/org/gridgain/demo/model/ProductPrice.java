package org.gridgain.demo.model;

import java.sql.Timestamp;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

public class ProductPrice {

	@QuerySqlField(index = true)
	private Long id;
	@QuerySqlField
	private Timestamp time;
	@QuerySqlField
	private Long productId;
	@QuerySqlField
	private Double price;

	public ProductPrice(Long id, Timestamp time, Long productId, Double price) {
		this.id = id;
		this.time = time;
		this.productId = productId;
		this.price = price;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

}
