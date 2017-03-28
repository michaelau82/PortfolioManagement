package com.michael.portfolioManagement.dao;

import com.michael.portfolioManagement.domain.Option;

public interface OptionDao {
	/**
	 * @param securitiesID
	 * @return
	 */
	public Option getOption(final String securitiesID);
}
