package com.michael.portfolioManagement.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.michael.portfolioManagement.dao.AssetAllocationDao;
import com.michael.portfolioManagement.domain.AssetAllocation;
import com.michael.portfolioManagement.domain.PositionType;
import com.michael.portfolioManagement.domain.Securities;

public class AssetAllocationDaoImpl extends JdbcDaoSupport implements AssetAllocationDao {
	private final static Logger logger = LoggerFactory.getLogger(AssetAllocationDaoImpl.class);
	private final static String QUERY_PORTFOLIO_ALLOCATION = 
			"SELECT FUND_ACCOUNT_ID, ASSET_WEIGHT, SECURITIES_ID, QUANTITIES FROM ASSET_ALLOCATION WHERE FUND_ACCOUNT_ID = ?";
	private final static String QUERY_SECURITIES = 
			"SELECT SECURITIES_ID, EXPECTED_RETURN, ANNUAL_STANDARD_DEVIVATION, STOCK_PRICE, SECURITIES_TYPE, POSITION_TYPE FROM SECURITIES WHERE SECURITIES_ID = ?";
	private final static String QUERY_FUND_ACCT_OS_SHARES = "SELECT OS_SHARES FROM FUND WHERE FUND_ACCOUNT_ID = ?";
	
	/* (non-Javadoc)
	 * @see com.michael.portfolioManagement.dao.AssetAllocationDao#getPortfolioAllocation(java.lang.String)
	 */
	public List<AssetAllocation> getPortfolioAllocation(final String fundAccountNum) {
		logger.debug("AssetAllocationDaoImpl.getPortfolioAllocation starts");
		final String sql = QUERY_PORTFOLIO_ALLOCATION;
		final Object[] params = {fundAccountNum};
		
		logger.debug("sql: {}", sql);
		logger.debug("paramMap: {}", ToStringBuilder.reflectionToString(params));
		
		List<Map<String, Object>> resultList = null;
		try{
			resultList = this.getJdbcTemplate().queryForList(sql, params);
			logger.debug("resultList.size: {}", resultList.size());
		}
		catch(DataAccessException dae){
			logger.debug("DataAccessException dae: {}", dae.getMessage());
			throw dae;
		}
		
		List<AssetAllocation> portfolio = new ArrayList<AssetAllocation>();
		for(Map<String, Object> result: resultList){
			final String fundAccountID = (String)result.get("FUND_ACCOUNT_ID");
			final BigDecimal assetWeight = (BigDecimal)result.get("ASSET_WEIGHT");
			final String securitiesID = (String)result.get("SECURITIES_ID");
			final long quantities = (long)result.get("QUANTITIES");
			AssetAllocation assetAllocation = new AssetAllocation();
			assetAllocation.setFundAccountID(fundAccountID);
			assetAllocation.setAssetWeight(assetWeight);
			Securities securities = getSecurities(securitiesID);
			assetAllocation.setSecurities(securities);
			assetAllocation.setQuantities(quantities);
			portfolio.add(assetAllocation);
		}
		
		logger.debug("AssetAllocationDaoImpl.getPortfolioAllocation ends");
		return portfolio;
	}
	
	public Securities getSecurities(final String securitiesID) {
		logger.debug("AssetAllocationDaoImpl.getSecurities starts");
		final String sql = QUERY_SECURITIES;
		final Object[] params = {securitiesID};
		
		logger.debug("sql: {}", sql);
		logger.debug("paramMap: {}", ToStringBuilder.reflectionToString(params));
		
		List<Map<String, Object>> resultList = null;
		try{
			resultList = this.getJdbcTemplate().queryForList(sql, params);
			logger.debug("resultList.size: {}", resultList.size());
		}
		catch(DataAccessException dae){
			logger.debug("DataAccessException dae: {}", dae.getMessage());
			throw dae;
		}
		
		Securities securities = new Securities();
		for(Map<String, Object> result: resultList){
			final String retSecuritiesID = (String)result.get("SECURITIES_ID");
			final Double expectedReturn = (Double)result.get("EXPECTED_RETURN");
			final Double annualizedStandardDeviation = (Double)result.get("ANNUAL_STANDARD_DEVIVATION");
			final Double stockPrice = (Double)result.get("STOCK_PRICE");
			final String securitiesType = (String)result.get("SECURITIES_TYPE");
			final String positionType = (String)result.get("POSITION_TYPE");
			securities.setSecuritiesID(retSecuritiesID);
			securities.setExpectedReturn(expectedReturn);
			securities.setAnnualizedStandardDeviation(annualizedStandardDeviation);
			securities.setStockPrice(stockPrice);
			securities.setSecuritiesType(securitiesType);
			securities.setPositionType(PositionType.fromString(positionType));
		}
		
		logger.debug("AssetAllocationDaoImpl.getSecurities ends");
		return securities;
	}
	
	public long getFundAccountOSShares(final String fundAccountNum) {
		logger.debug("AssetAllocationDaoImpl.getFundAccountOSShares starts");
		final String sql = QUERY_FUND_ACCT_OS_SHARES;
		final Object[] params = {fundAccountNum};
		
		logger.debug("sql: {}", sql);
		logger.debug("params: {}", ToStringBuilder.reflectionToString(params));
		
		long fundAccountOSShares = -1L;
		
		try{
			fundAccountOSShares = this.getJdbcTemplate().queryForObject(sql, Long.class, params);
			logger.debug("fundAccountOSShares: {}", fundAccountOSShares);
		}
		catch(DataAccessException dae){
			logger.debug("DataAccessException dae: {}", dae.getMessage());
			throw dae;
		}
		
		logger.debug("AssetAllocationDaoImpl.getFundAccountOSShares ends");
		return fundAccountOSShares;
	}
}
