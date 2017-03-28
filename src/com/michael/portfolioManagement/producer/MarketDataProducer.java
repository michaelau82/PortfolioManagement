package com.michael.portfolioManagement.producer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.michael.portfolioManagement.domain.AssetAllocation;
import com.michael.portfolioManagement.domain.Securities;
import com.michael.portfolioManagement.services.PricingServices;

public class MarketDataProducer<T> extends Thread {
	private final static Logger logger = LoggerFactory.getLogger(MarketDataProducer.class);
	
	private BlockingQueue<T> queue;
	
	private boolean isExit = false;
	
	private Class<T> clazz;
	private PricingServices pricingServices;
	
	private final static String FUND_ACCOUNT_ID = "FUND_ACCOUNT_1";
	
	public MarketDataProducer(BlockingQueue<T> queue, Class<T> clazz) {
		this.queue = queue;
		this.clazz = clazz;
	}
	
	// getSecuritisFromDB
	// publishToConsumer
	
	public T buildOne(final long timeStep) throws InstantiationException, IllegalAccessException {
		Method method = null;
		T t = clazz.newInstance();
		List<AssetAllocation>  assetAllocationList = pricingServices.getPortfolioAllocation(FUND_ACCOUNT_ID);
		for (AssetAllocation assetAllocation : assetAllocationList) {
			t = (T)pricingServices.generateStochasticStockPrice(assetAllocation.getSecurities(), timeStep);
		}
		return t;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while(!isExit) {
			long timeStep = generateRandomTime();
			
			try {
				List<AssetAllocation>  assetAllocationList = pricingServices.getPortfolioAllocation(FUND_ACCOUNT_ID);
				for (AssetAllocation assetAllocation : assetAllocationList) {
					T t = (T)pricingServices.generateStochasticStockPrice(assetAllocation.getSecurities(), timeStep);
					Supplier<T> supplier = () -> t;
					produce(supplier);
				}
				Thread.sleep(timeStep);
			} catch (InterruptedException ie) {
				logger.error("InterruptedException ie: {}", ie.getMessage());
				isExit=true;
			}
		}
	}

	/**
	 * Generate a random time step 0.5s - 2s
	 */
	private long generateRandomTime() {
		logger.debug("generateRandomTime starts");
		final Random random = new Random();
		final int randomTimeStep = 1500;
		final long timeStep = random.nextInt(randomTimeStep) + 500;
		logger.debug("timeStep: {}", timeStep);
		logger.debug("generateRandomTime ends");
		return timeStep;
	}
	
	/**
	 * Insert the supplied object in the queue
	 * 
	 * @param supplier
	 *            Is responsible for supplying the object that will be put in
	 *            the queue
	 */
	public void produce(Supplier<T> supplier) {
		logger.debug("MarketDataProducer produce starts.");
		final T msg = supplier.get();
		try {
			queue.put(msg);
			logger.debug("Added message: {}", msg);

			// Simulate a long running process
			Thread.sleep(900);

		} catch (InterruptedException ie) {
			logger.error("MarketDataProducer produce InterruptedException ie: {}", ie.getMessage());
			throw new RuntimeException(ie);
		}
		logger.debug("MarketDataProducer produce ends.");
	}
	
	/**
	 * @param isExit the isExit to set
	 */
	public void setExit(boolean isExit) {
		this.isExit = isExit;
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
