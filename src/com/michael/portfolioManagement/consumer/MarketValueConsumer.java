package com.michael.portfolioManagement.consumer;

import java.sql.Time;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.michael.portfolioManagement.domain.Securities;
import com.michael.portfolioManagement.services.PricingServices;

public class MarketValueConsumer<T> extends Thread {
	private final static Logger logger = LoggerFactory.getLogger(MarketValueConsumer.class);
	
	private BlockingQueue<T> queue;
	
	private boolean isExit = false;
	
	private Class<T> clazz;

	private PricingServices pricingServices;
	
	private final static String FUND_ACCOUNT_ID = "FUND_ACCOUNT_1";
	
	public MarketValueConsumer(BlockingQueue<T> queue, Class<T> clazz){
		this.queue = queue;
		this.clazz = clazz;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while(!isExit) {
			Consumer<T> consumer = (s) -> logger.debug("Consumed message: {}", s);
			consume(consumer);
		}
	}
	
	/**
     * Retrieves an object from the head of the queue and passes it to the
     * consumer
     * 
     * @param consumer
     *            Contains the logic on what to do with the retrieved object
     */
	public void consume(Consumer<T> consumer){
		logger.debug("MarketValueConsumer consume starts.");
		try {
			T t = queue.take();
			consumer.accept(t);
			logger.debug("MarketValueConsumer t: {}", ToStringBuilder.reflectionToString(t));
			Double nav = pricingServices.calculateNAV(FUND_ACCOUNT_ID, (Securities)t);
			logger.debug("Fund:{}, NAV: {}", FUND_ACCOUNT_ID, nav);
			
			// Simulate a long running process
			Thread.sleep(1250);
		}
		catch(InterruptedException ie) {
			logger.error("MarketValueConsumer consume InterruptedException ie: {}", ie.getMessage());
			throw new RuntimeException(ie);
		}
		logger.debug("MarketValueConsumer consume ends.");
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
