package org.gridgain.demo.model;

import java.sql.Timestamp;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

public class Holdings {

	@QuerySqlField(index = true)
	Long id;
	@QuerySqlField
	Long accountId;
	@QuerySqlField
	Long productId;
	@QuerySqlField
	Timestamp lastTradeDate;
	@QuerySqlField
	Long shareCount;

	public Holdings(Long id, Long accountId, Long productId, Timestamp lastTradeDate,
			Long shareCount) {
		this.id = id;
		this.accountId = accountId;
		this.productId = productId;
		this.lastTradeDate = lastTradeDate;
		this.shareCount = shareCount;
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

	public Timestamp getLastTradeDate() {
		return lastTradeDate;
	}

	public void setLastTradeDate(Timestamp lastTradeDate) {
		this.lastTradeDate = lastTradeDate;
	}

	public Long getShareCount() {
		return shareCount;
	}

	public void setShareCount(Long shareCount) {
		this.shareCount = shareCount;
	}

}
