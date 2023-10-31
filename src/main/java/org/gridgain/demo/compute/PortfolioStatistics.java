package org.gridgain.demo.compute;

import java.io.Serializable;

public class PortfolioStatistics implements Serializable {
	private static final long serialVersionUID = -5651213653977992505L;
	private double bestCase;
	private double worstCase;
	private double median;
	private String accountName;
	private double currentValue;

	PortfolioStatistics(String accountName, double currentValue, double bestCase, double worstCase, double median) {
		this.accountName = accountName;
		this.currentValue = currentValue;
		this.bestCase = bestCase;
		this.worstCase = worstCase;
		this.median = median;
	}

	public double getBestCase() {
		return bestCase;
	}

	public double getWorstCase() {
		return worstCase;
	}

	public double getMedian() {
		return median;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public double getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(double currentValue) {
		this.currentValue = currentValue;
	}

	@Override
	public String toString() {
		return "PortfolioStatistics [bestCase=" + bestCase + ", worstCase=" + worstCase + ", median=" + median
				+ ", accountName=" + accountName + ", currentValue=" + currentValue + "]";
	}


}