package com.michael.portfolioManagement.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.analysis.function.Divide;
import org.apache.commons.math3.analysis.function.Sqrt;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.michael.portfolioManagement.dao.impl.AssetAllocationDaoImpl;
import com.michael.portfolioManagement.domain.AssetAllocation;
import com.michael.portfolioManagement.domain.CommonStock;
import com.michael.portfolioManagement.domain.EuropeanCallOption;
import com.michael.portfolioManagement.domain.EuropeanPutOption;
import com.michael.portfolioManagement.domain.Securities;

public class PricingServices {
	private final static Logger logger = LoggerFactory.getLogger(PricingServices.class);
	
	private AssetAllocationDaoImpl assetAllocationDaoImpl = new AssetAllocationDaoImpl();
	
	/**
	 * Generate Stochastic Stock Price by random walk
	 */
	public Securities generateStochasticStockPrice(final Securities securities, final long timeStep) {
		logger.debug("Securities.generateStochasticStockPrice starts");
		NormalDistribution normalDistribution = new NormalDistribution();
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
		}
		String type = calculateSecurities.getSecuritiesType();
		if (type.equalsIgnoreCase("STOCK")) {
			marketValues = calculateSecurities.getStockPrice() * assetAllocation.getQuantities();
		}
		else if (type.equalsIgnoreCase("CALL_OPTION")) {
			marketValues = calculateSecurities.getStockPrice() * assetAllocation.getQuantities();
		}
		else if (type.equalsIgnoreCase("PUT_OPTION")) {
			marketValues = calculateSecurities.getStockPrice() * assetAllocation.getQuantities();
		}
		return marketValues;
	}
	
	public Double calculateNAV(final String fundAccountNum, Securities newSecurities) {
		// Step 1. Get all portfolio securities
		List<AssetAllocation> assetAllocationList = assetAllocationDaoImpl.getPortfolioAllocation(fundAccountNum);

		// Step 2. Display each securities Market Values
		Map<String, Double> marketValuesMap = new HashMap<String, Double>();
		for (AssetAllocation assetAllocation : assetAllocationList) {
			Double individualMV = calculateMarketValue(assetAllocation, newSecurities);
			logger.debug("Market Value of {}: {}", assetAllocation.getSecurities().getSecuritiesID(), individualMV);
			marketValuesMap.put(assetAllocation.getSecurities().getSecuritiesID(), individualMV);
		}

		// Step 3. Display total portfolio values
		long OutstandingShares = assetAllocationDaoImpl.getFundAccountOSShares(fundAccountNum);
		Double nav = -1D;
		for (String key : marketValuesMap.keySet()) {
			nav += marketValuesMap.get(key);
		}
		Divide divisor = new Divide();
		nav = divisor.value(nav, OutstandingShares);
		
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

	public List<AssetAllocation> getPortfolioAllocation(final String fundAccountNum) {
		return assetAllocationDaoImpl.getPortfolioAllocation(fundAccountNum);
	}
}
