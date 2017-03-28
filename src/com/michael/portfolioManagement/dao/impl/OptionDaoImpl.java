package com.michael.portfolioManagement.dao.impl;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.michael.portfolioManagement.dao.OptionDao;
import com.michael.portfolioManagement.domain.Moneyness;
import com.michael.portfolioManagement.domain.Option;
import com.michael.portfolioManagement.domain.OptionStyle;
import com.michael.portfolioManagement.domain.OptionType;

public class OptionDaoImpl extends JdbcDaoSupport implements OptionDao {
	private final static Logger logger = LoggerFactory.getLogger(OptionDaoImpl.class);
	
	private final static String QUERY_OPTION = 
		"SELECT securites.SECURITIES_ID, securites.EXPECTED_RETURN, securites.ANNUAL_STANDARD_DEVIVATION, securites.STOCK_PRICE, securites.SECURITIES_TYPE, "
		+ "option.OPTION_PRICE, option.STRIKE_PRICE, option.EXPIRATION_DATE, option.MONEYNESS, option.OPTION_TYPE, option.OPTION_STYLE "
		+ "FROM SECURITIES securites, OPTION option " 
		+ "WHERE securites.SECURITIES_ID = option.SECURITIES_ID "
		+ "AND option.SECURITIES_ID = ?";
	
	/* (non-Javadoc)
	 * @see com.michael.portfolioManagement.dao.OptionDao#getOption(java.lang.String)
	 */
	public Option getOption(final String securitiesID){
		logger.debug("OptionDaoImpl.getOption starts");
		final String sql = QUERY_OPTION;
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
		
		Option option = new Option();
		for(Map<String, Object> result: resultList){
			final String retSecuritiesID = (String)result.get("SECURITIES_ID");
			final Double expectedReturn = (Double)result.get("EXPECTED_RETURN");
			final Double annualizedStandardDeviation = (Double)result.get("ANNUAL_STANDARD_DEVIVATION");
			final Double stockPrice = (Double)result.get("STOCK_PRICE");
			final String securitiesType = (String)result.get("SECURITIES_TYPE");
			
			final Double optionPrice = (Double)result.get("OPTION_PRICE");
			final Double strikePrice = (Double)result.get("STRIKE_PRICE");
			final Date expirationDate = (Date)result.get("EXPIRATION_DATE");
			final String moneyness = (String)result.get("MONEYNESS");
			final String optionType = (String)result.get("OPTION_TYPE");
			final String optionStyle = (String)result.get("OPTION_STYLE");
			option.setSecuritiesID(retSecuritiesID);
			option.setExpectedReturn(expectedReturn);
			option.setAnnualizedStandardDeviation(annualizedStandardDeviation);
			option.setStockPrice(stockPrice);
			option.setSecuritiesType(securitiesType);
			option.setPrice(optionPrice);
			option.setStrikePrice(strikePrice);
			option.setExpirationDate(expirationDate);
			option.setMoneyness(Moneyness.fromString(moneyness));
			option.setOptionType(OptionType.fromString(optionType));
			option.setOptionStyle(OptionStyle.fromString(optionStyle));
		}
		
		logger.debug("OptionDaoImpl.getOption ends");
		return option;
	}
}
