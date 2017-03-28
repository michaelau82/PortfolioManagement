package com.michael.portfolioManagement.domain;

import java.math.BigDecimal;

public class AssetAllocation {
	private String fundAccountID;
	private BigDecimal assetWeight;
	private Securities securities;
	private long quantities;

	/**
	 * @return the fundAccountID
	 */
	public String getFundAccountID() {
		return fundAccountID;
	}

	/**
	 * @param fundAccountID
	 *            the fundAccountID to set
	 */
	public void setFundAccountID(String fundAccountID) {
		this.fundAccountID = fundAccountID;
	}

	/**
	 * @return the assetWeight
	 */
	public BigDecimal getAssetWeight() {
		return assetWeight;
	}

	/**
	 * @param assetWeight
	 *            the assetWeight to set
	 */
	public void setAssetWeight(BigDecimal assetWeight) {
		this.assetWeight = assetWeight;
	}

	/**
	 * @return the securities
	 */
	public Securities getSecurities() {
		return securities;
	}

	/**
	 * @param securities
	 *            the securities to set
	 */
	public void setSecurities(Securities securities) {
		this.securities = securities;
	}

	/**
	 * @return the quantities
	 */
	public long getQuantities() {
		return quantities;
	}

	/**
	 * @param quantities
	 *            the quantities to set
	 */
	public void setQuantities(long quantities) {
		this.quantities = quantities;
	}
}
