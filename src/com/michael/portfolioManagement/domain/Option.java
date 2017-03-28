package com.michael.portfolioManagement.domain;

import java.sql.Date;

public class Option extends Securities {
	private Double price;
	private Double StrikePrice;
	private Date expirationDate;
	private Moneyness moneyness;
	private OptionType optionType;
	private OptionStyle optionStyle;
	
	/**
	 * @return the price
	 */
	public Double getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(Double price) {
		this.price = price;
	}
	/**
	 * @return the strikePrice
	 */
	public Double getStrikePrice() {
		return StrikePrice;
	}
	/**
	 * @param strikePrice the strikePrice to set
	 */
	public void setStrikePrice(Double strikePrice) {
		StrikePrice = strikePrice;
	}
	/**
	 * @return the expirationDate
	 */
	public Date getExpirationDate() {
		return expirationDate;
	}
	/**
	 * @param expirationDate the expirationDate to set
	 */
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	/**
	 * @return the moneyness
	 */
	public Moneyness getMoneyness() {
		return moneyness;
	}
	/**
	 * @param moneyness the moneyness to set
	 */
	public void setMoneyness(Moneyness moneyness) {
		this.moneyness = moneyness;
	}
	
	/**
	 * @return the optionType
	 */
	public OptionType getOptionType() {
		return optionType;
	}
	/**
	 * @param optionType the optionType to set
	 */
	public void setOptionType(OptionType optionType) {
		this.optionType = optionType;
	}
	/**
	 * @return the optionStyle
	 */
	public OptionStyle getOptionStyle() {
		return optionStyle;
	}
	/**
	 * @param optionStyle the optionStyle to set
	 */
	public void setOptionStyle(OptionStyle optionStyle) {
		this.optionStyle = optionStyle;
	}
}
