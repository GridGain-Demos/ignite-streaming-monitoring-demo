package org.gridgain.demo.model;

import java.io.Serializable;

import org.apache.ignite.cache.affinity.AffinityKeyMapped;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

public class Holding implements Serializable {

	private static final long serialVersionUID = 8945488372170433220L;
	
	@QuerySqlField(index = true)
	private String id;
	@QuerySqlField
	@AffinityKeyMapped
	private String accountId;
	@QuerySqlField
	private String symbol;
	@QuerySqlField
	private long lastTradeDate;
	@QuerySqlField
	private long shareCount;

	public Holding(String id, String accountId, String symbol, long lastTradeDate,
			Long shareCount) {
		this.id = id;
		this.accountId = accountId;
		this.symbol = symbol;
		this.lastTradeDate = lastTradeDate;
		this.shareCount = shareCount;
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

	public long getLastTradeDate() {
		return lastTradeDate;
	}

	public void setLastTradeDate(long lastTradeDate) {
		this.lastTradeDate = lastTradeDate;
	}

	public long getShareCount() {
		return shareCount;
	}

	public void setShareCount(long shareCount) {
		this.shareCount = shareCount;
	}

	@Override
	public String toString() {
		return "Holdings [id=" + id + ", accountId=" + accountId + ", symbol=" + symbol + ", lastTradeDate="
				+ lastTradeDate + ", shareCount=" + shareCount + "]";
	}

}
