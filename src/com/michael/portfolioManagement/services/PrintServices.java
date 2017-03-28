package com.michael.portfolioManagement.services;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.michael.portfolioManagement.dao.impl.AssetAllocationDaoImpl;
import com.michael.portfolioManagement.domain.AssetAllocation;
import com.michael.portfolioManagement.producer.MarketDataProducer;

public class PrintServices {
	private final static Logger logger = LoggerFactory.getLogger(PrintServices.class);
	
	private AssetAllocationDaoImpl assetAllocationDaoImpl = new AssetAllocationDaoImpl();
	private PricingServices pricingServcies = new PricingServices();
	public void printPortfolio(){
		final String fundAccountNum = "FundAccountID123";
		
//		final Double nav = pricingServcies.calculateNAV(fundAccountNum);
	}
}
