package org.gridgain.demo.model;

import java.io.Serializable;

import org.apache.ignite.cache.affinity.AffinityKeyMapped;

public class HoldingKey implements Serializable {

	private static final long serialVersionUID = 5531747967825602187L;
	
	@AffinityKeyMapped
	public String accountId;
	public String symbol;

	public HoldingKey(String accountId, String symbol) {
		this.accountId = accountId;
		this.symbol = symbol;
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

}
