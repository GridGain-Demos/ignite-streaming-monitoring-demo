package org.gridgain.demo.model;

import java.sql.Timestamp;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

public class Trade {

	public enum TRADE_TYPE {
		BUY, SELL
	};

	@QuerySqlField(index = true)
	private Long id;
	@QuerySqlField(index = true)
	private Long accountId;
	@QuerySqlField
	private Long productId;
	@QuerySqlField
	private int order_quantity;
	@QuerySqlField
	private double bid_price;
	@QuerySqlField
	private String trade_type;
	@QuerySqlField
	private Timestamp order_date;

	public Trade(Long id, Long accountId, Long productId, int order_quantity, double bid_price, String trade_type, Timestamp order_date) {
		this.id = id;
		this.accountId = accountId;
		this.productId = productId;
		this.order_quantity = order_quantity;
		this.bid_price = bid_price;
		this.trade_type = trade_type;
		this.order_date = order_date;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public int getOrder_quantity() {
		return order_quantity;
	}

	public void setOrder_quantity(int order_quantity) {
		this.order_quantity = order_quantity;
	}

	public double getBid_price() {
		return bid_price;
	}

	public void setBid_price(double bid_price) {
		this.bid_price = bid_price;
	}

	public String getTrade_type() {
		return trade_type;
	}

	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}

	public Timestamp getOrder_date() {
		return order_date;
	}

	public void setOrder_date(Timestamp order_date) {
		this.order_date = order_date;
	}

	@Override
	public String toString() {
		return "Trade [id=" + id + ", accountId=" + accountId + ", productId=" + productId + ", order_quantity="
				+ order_quantity + ", bid_price=" + bid_price + ", trade_type=" + trade_type + ", order_date="
				+ order_date + "]";
	}

}
