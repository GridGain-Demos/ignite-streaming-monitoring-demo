package org.gridgain.demo.model;

import org.apache.ignite.cache.affinity.AffinityKeyMapped;

public class HoldingsKey {

	@AffinityKeyMapped
	public Long accountId;
	public Long productId;

	public HoldingsKey(Long accountId, Long productId) {
		this.accountId = accountId;
		this.productId = productId;
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

}
