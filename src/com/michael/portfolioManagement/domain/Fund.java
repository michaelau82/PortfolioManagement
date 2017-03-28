package com.michael.portfolioManagement.domain;

public class Fund {
	private String fundAccountID;
	private long OSShare;

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
	 * @return the oSShare
	 */
	public long getOSShare() {
		return OSShare;
	}

	/**
	 * @param oSShare
	 *            the oSShare to set
	 */
	public void setOSShare(long oSShare) {
		OSShare = oSShare;
	}
}
