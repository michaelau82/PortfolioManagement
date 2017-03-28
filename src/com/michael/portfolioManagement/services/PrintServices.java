package com.michael.portfolioManagement.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.michael.portfolioManagement.domain.AssetAllocation;

public class PrintServices {
	private final static Logger logger = LoggerFactory.getLogger(PrintServices.class);
	
	private PricingServices pricingServices = new PricingServices();
	private final static String FUND_ACCOUNT_ID = "FUND_ACCOUNT_1";
	
	public void printPortfolio(){
		Map<AssetAllocation, Double> marketValueMap = pricingServices.getMarketValueMap();
		Map<String, Double> navMap = pricingServices.getNavMap();
		
		final String folderStr = "report";
		final String reportFilename = "report" + File.separator + "Fund_Account_" + FUND_ACCOUNT_ID + ".txt";
		logger.debug("reportFilename: {}", reportFilename);
		try {
			File folder = new File(folderStr);
			if(!folder.exists())
				folder.mkdirs();
			File reportFile = new File(reportFilename);
			if(!reportFile.exists())
				reportFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(reportFile, false); 
			
			PrintStream out = new PrintStream(fos);
			out.println("Fund account: " + FUND_ACCOUNT_ID);
			if (marketValueMap != null) {
				for (AssetAllocation assetAllocation : marketValueMap.keySet()) {
					String securitiesID = assetAllocation.getSecurities().getSecuritiesID();
					Double marketValue = marketValueMap.get(assetAllocation);
					out.println("Securities ID: " + securitiesID + ", Market Value: " + marketValue);
				}
				out.println("NAV: " + navMap.get(FUND_ACCOUNT_ID));
			}
			
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * @return the pricingServices
	 */
	public PricingServices getPricingServices() {
		return pricingServices;
	}

	/**
	 * @param pricingServices the pricingServices to set
	 */
	public void setPricingServices(PricingServices pricingServices) {
		this.pricingServices = pricingServices;
	}
}
