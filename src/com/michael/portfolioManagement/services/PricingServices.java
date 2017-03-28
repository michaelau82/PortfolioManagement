package com.michael.portfolioManagement.services;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.analysis.function.Divide;
import org.apache.commons.math3.analysis.function.Exp;
import org.apache.commons.math3.analysis.function.Sqrt;
import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.michael.portfolioManagement.dao.impl.AssetAllocationDaoImpl;
import com.michael.portfolioManagement.dao.impl.OptionDaoImpl;
import com.michael.portfolioManagement.domain.AssetAllocation;
import com.michael.portfolioManagement.domain.Option;
import com.michael.portfolioManagement.domain.PositionType;
import com.michael.portfolioManagement.domain.Securities;

public class PricingServices {
	private final static Logger logger = LoggerFactory.getLogger(PricingServices.class);
	
	private AssetAllocationDaoImpl assetAllocationDaoImpl = new AssetAllocationDaoImpl();
	private NormalDistribution normalDistribution = new NormalDistribution();
	private LogNormalDistribution logNormalDistribution = new LogNormalDistribution();
	private Exp exp = new Exp();
	private OptionDaoImpl optionDaoImpl = new OptionDaoImpl();
	private double riskFreeRate = 0.02;
	private List<AssetAllocation> assetAllocationList = null;
	private Map<AssetAllocation, Double> marketValueMap = null;
	private Map<String, Double> navMap = new HashMap<String, Double>();;
	
	

	/**
	 * Generate Stochastic Stock Price by random walk
	 */
	public Securities generateStochasticStockPrice(final Securities securities, final long timeStep) {
		logger.debug("Securities.generateStochasticStockPrice starts");
		
		double epsilon = normalDistribution.probability(0, 1);
		Sqrt sqrt = new Sqrt();
		
		Double stockPrice = securities.getStockPrice();
		Double mean = securities.getExpectedReturn();
		Double sigma = securities.getAnnualizedStandardDeviation();
		
		Double changeInPrice = stockPrice * ((mean * timeStep/7257600)+ sigma*epsilon*sqrt.value(timeStep/7257600));
		logger.debug("stockPrice: {}, changeInPrice: {}", stockPrice, changeInPrice);
		stockPrice = stockPrice + changeInPrice;
		logger.debug("new stockPrice: {}", stockPrice);
		securities.setStockPrice(stockPrice);
		logger.debug("Securities.generateStochasticStockPrice ends");
		return securities;
	}
	
	public Double calculateMarketValue(AssetAllocation assetAllocation, Securities newSecurities){
		Double marketValues = -1D;
		Securities calculateSecurities = assetAllocation.getSecurities();
		if(calculateSecurities.getSecuritiesID().equalsIgnoreCase(newSecurities.getSecuritiesID())){
			calculateSecurities = newSecurities;
			assetAllocation.setSecurities(newSecurities);
		}
		String securitiesType = calculateSecurities.getSecuritiesType();
		logger.debug("securitiesType: {}", securitiesType);
		
		Double stockPrice = calculateSecurities.getStockPrice();
		long quantities = assetAllocation.getQuantities();
		logger.debug("stockPrice: {}", stockPrice);
		logger.debug("quantities: {}", quantities);
		logger.debug("calculateSecurities.getPositionType: {}", calculateSecurities.getPositionType().getType());
		
		if (securitiesType.equalsIgnoreCase("STOCK")) {
			if (PositionType.LONG.equals(calculateSecurities.getPositionType())) {
				marketValues = stockPrice * quantities;
			}
			else if (PositionType.SHORT.equals(calculateSecurities.getPositionType())) {
				marketValues = -1 * stockPrice * quantities;
			}
		}
		else if (securitiesType.equalsIgnoreCase("CALL_OPTION")) {
			Double callOptionPrice = getCallOptionPrice(calculateSecurities);
			if (PositionType.LONG.equals(calculateSecurities.getPositionType())) {
				marketValues = callOptionPrice * quantities;
			}
			else if (PositionType.SHORT.equals(calculateSecurities.getPositionType())) {
				marketValues = -1 * callOptionPrice * quantities;
			}
		}
		else if (securitiesType.equalsIgnoreCase("PUT_OPTION")) {
			Double putOptionPrice = getPutOptionPrice(calculateSecurities);
			if (PositionType.LONG.equals(calculateSecurities.getPositionType())) {
				marketValues = putOptionPrice * quantities;
			}
			else if (PositionType.SHORT.equals(calculateSecurities.getPositionType())) {
				marketValues = -1 * putOptionPrice * quantities;
			}
		}
		logger.debug("marketValues: {}", marketValues);
		return marketValues;
	}
	
	private Double getCallOptionPrice(final Securities securities){
		Option option = optionDaoImpl.getOption(securities.getSecuritiesID());
		Double stockPrice = option.getStockPrice();
		Double strikePrice = option.getStrikePrice();
		Double sigma = option.getAnnualizedStandardDeviation();
		Date day1 = option.getExpirationDate();
		long day2Long = System.currentTimeMillis();
		long diff = Math.abs(day1.getTime() - day2Long);
		long diffDays = diff / (24 * 60 * 60 * 1000);
		
		Double d1_nomin = logNormalDistribution.logDensity(stockPrice/strikePrice) + ((riskFreeRate+(sigma*sigma/2))*diffDays);
		Double d1_denomin = sigma * Math.sqrt(diffDays);
		Double d1 = d1_nomin/d1_denomin;
		
		Double d2 = d1 - d1_denomin;
		
		Double callPrice_Operand1 = stockPrice * normalDistribution.cumulativeProbability(d1);
		Double callPrice_Operand2 = strikePrice * exp.value(-1*riskFreeRate*diffDays) *normalDistribution.cumulativeProbability(d2);
		Double callPrice = callPrice_Operand1 - callPrice_Operand2;
		return callPrice;
	}
	
	private Double getPutOptionPrice(final Securities securities){
		Option option = optionDaoImpl.getOption(securities.getSecuritiesID());
		Double stockPrice = option.getStockPrice();
		Double strikePrice = option.getStrikePrice();
		Double sigma = option.getAnnualizedStandardDeviation();
		Date day1 = option.getExpirationDate();
		long day2Long = System.currentTimeMillis();
		long diff = Math.abs(day1.getTime() - day2Long);
		long diffDays = diff / (24 * 60 * 60 * 1000);
		
		Double d1_nomin = logNormalDistribution.logDensity(stockPrice/strikePrice) + ((riskFreeRate+(sigma*sigma/2))*diffDays);
		Double d1_denomin = sigma * Math.sqrt(diffDays);
		Double d1 = d1_nomin/d1_denomin;
		
		Double d2 = d1 - d1_denomin;
		
		Double putPrice_Operand1 = strikePrice * exp.value(-1*riskFreeRate*diffDays) *normalDistribution.cumulativeProbability(-1 * d2);
		Double putPrice_Operand2 = stockPrice * normalDistribution.cumulativeProbability(-1 * d1);
		Double putPrice = putPrice_Operand1 - putPrice_Operand2;
		return putPrice;
	}
	
	public Double calculateNAV(final String fundAccountNum, Securities newSecurities) {
		// Step 1. Get all portfolio securities
		if (marketValueMap == null) {
			marketValueMap = new HashMap<AssetAllocation, Double>();
		}
		if (assetAllocationList == null) {
			assetAllocationList = assetAllocationDaoImpl.getPortfolioAllocation(fundAccountNum);
		}

		// Step 2. Display each securities Market Values
		Map<String, Double> marketValuesMap = new HashMap<String, Double>();
		for (AssetAllocation assetAllocation : assetAllocationList) {
			Double individualMV = calculateMarketValue(assetAllocation, newSecurities);
			logger.debug("Market Value of {}: {}", assetAllocation.getSecurities().getSecuritiesID(), individualMV);
			marketValuesMap.put(assetAllocation.getSecurities().getSecuritiesID(), individualMV);
			marketValueMap.put(assetAllocation, individualMV);
		}

		// Step 3. Display total portfolio values
		long OutstandingShares = assetAllocationDaoImpl.getFundAccountOSShares(fundAccountNum);
		Double nav = -1D;
		for (String key : marketValuesMap.keySet()) {
			nav += marketValuesMap.get(key);
		}
		Divide divisor = new Divide();
		nav = divisor.value(nav, OutstandingShares);
		
		navMap.put(fundAccountNum, nav);
		
		return nav;
	}
	
	/**
	 * @return the assetAllocationDaoImpl
	 */
	public AssetAllocationDaoImpl getAssetAllocationDaoImpl() {
		return assetAllocationDaoImpl;
	}

	/**
	 * @param assetAllocationDaoImpl the assetAllocationDaoImpl to set
	 */
	public void setAssetAllocationDaoImpl(AssetAllocationDaoImpl assetAllocationDaoImpl) {
		this.assetAllocationDaoImpl = assetAllocationDaoImpl;
	}
	
	/**
	 * @return the optionDaoImpl
	 */
	public OptionDaoImpl getOptionDaoImpl() {
		return optionDaoImpl;
	}

	/**
	 * @param optionDaoImpl the optionDaoImpl to set
	 */
	public void setOptionDaoImpl(OptionDaoImpl optionDaoImpl) {
		this.optionDaoImpl = optionDaoImpl;
	}
	
	/**
	 * @return the marketValueMap
	 */
	public Map<AssetAllocation, Double> getMarketValueMap() {
		return marketValueMap;
	}

	/**
	 * @param marketValueMap the marketValueMap to set
	 */
	public void setMarketValueMap(Map<AssetAllocation, Double> marketValueMap) {
		this.marketValueMap = marketValueMap;
	}
	
	/**
	 * @return the navMap
	 */
	public Map<String, Double> getNavMap() {
		return navMap;
	}

	/**
	 * @param navMap the navMap to set
	 */
	public void setNavMap(Map<String, Double> navMap) {
		this.navMap = navMap;
	}

	public List<AssetAllocation> getPortfolioAllocation(final String fundAccountNum) {
		return assetAllocationDaoImpl.getPortfolioAllocation(fundAccountNum);
	}
}
