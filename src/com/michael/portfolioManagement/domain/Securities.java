package com.michael.portfolioManagement.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Securities {
	private final static Logger logger = LoggerFactory.getLogger(Securities.class);
	
	// SEDOL/CUSPIN
	private String securitiesID = "";
	private Double expectedReturn = -1D;
	private Double annualizedStandardDeviation = -1D;
	private Double stockPrice = 0D;
	private String securitiesType;
	private PositionType positionType; 
	
	/**
	 * @return the securitiesID
	 */
	public String getSecuritiesID() {
		return securitiesID;
	}

	/**
	 * @param securitiesID
	 *            the securitiesID to set
	 */
	public void setSecuritiesID(String securitiesID) {
		this.securitiesID = securitiesID;
	}

	/**
	 * @return the expectedReturn
	 */
	public Double getExpectedReturn() {
		return expectedReturn;
	}

	/**
	 * @param expectedReturn
	 *            the expectedReturn to set
	 */
	public void setExpectedReturn(Double expectedReturn) {
		this.expectedReturn = expectedReturn;
	}

	/**
	 * @return the annualizedStandardDeviation
	 */
	public Double getAnnualizedStandardDeviation() {
		return annualizedStandardDeviation;
	}

	/**
	 * @param annualizedStandardDeviation
	 *            the annualizedStandardDeviation to set
	 */
	public void setAnnualizedStandardDeviation(Double annualizedStandardDeviation) {
		this.annualizedStandardDeviation = annualizedStandardDeviation;
	}
	
	/**
	 * @return the stockPrice
	 */
	public Double getStockPrice() {
		return stockPrice;
	}

	/**
	 * @param stockPrice the stockPrice to set
	 */
	public void setStockPrice(Double stockPrice) {
		this.stockPrice = stockPrice;
	}
	
	/**
	 * @return the securitiesType
	 */
	public String getSecuritiesType() {
		return securitiesType;
	}

	/**
	 * @param securitiesType the securitiesType to set
	 */
	public void setSecuritiesType(String securitiesType) {
		this.securitiesType = securitiesType;
	}
	
	/**
	 * @return the positionType
	 */
	public PositionType getPositionType() {
		return positionType;
	}

	/**
	 * @param positionType the positionType to set
	 */
	public void setPositionType(PositionType positionType) {
		this.positionType = positionType;
	}
}
