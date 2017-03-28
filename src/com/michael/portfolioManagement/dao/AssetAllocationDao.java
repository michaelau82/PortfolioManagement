/**
 * 
 */
package com.michael.portfolioManagement.dao;

import java.util.List;

import com.michael.portfolioManagement.domain.AssetAllocation;

/**
 * @author Michael Au
 *
 */
public interface AssetAllocationDao {
	/**
	 * To retrieve the original portfolio allocation for specific fund account
	 * 
	 * @param fundAccountNum
	 * @return
	 */
	public List<AssetAllocation> getPortfolioAllocation(final String fundAccountNum);
	
	/**
	 * To retrieve the total Outstanding shares of particular fund account
	 * 
	 * @param fundAccountNum
	 * @return
	 */
	public long getFundAccountOSShares(final String fundAccountNum);
}
