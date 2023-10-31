package org.gridgain.demo.model;

import java.io.Serializable;

import org.apache.ignite.cache.affinity.AffinityKeyMapped;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

public class Trade implements Serializable {

	private static final long serialVersionUID = 9106561487677495739L;

	@QuerySqlField(index = true)
	private String id;
	@QuerySqlField(index = true)
	@AffinityKeyMapped
	private String accountId;
	@QuerySqlField
	private String symbol;
	@QuerySqlField
	private int order_quantity;
	@QuerySqlField
	private double bid_price;
	@QuerySqlField
	private String trade_type;
	@QuerySqlField
	private long order_date;

	public Trade(String id, String accountId, String symbol, int order_quantity, double bid_price, String trade_type, long order_date) {
		this.id = id;
		this.accountId = accountId;
		this.symbol = symbol;
		this.order_quantity = order_quantity;
		this.bid_price = bid_price;
		this.trade_type = trade_type;
		this.order_date = order_date;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
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

	public long getOrder_date() {
		return order_date;
	}

	public void setOrder_date(long order_date) {
		this.order_date = order_date;
	}

	@Override
	public String toString() {
		return "Trade [id=" + id + ", accountId=" + accountId + ", symbol=" + symbol + ", order_quantity="
				+ order_quantity + ", bid_price=" + bid_price + ", trade_type=" + trade_type + ", order_date="
				+ order_date + "]";
	}

}
