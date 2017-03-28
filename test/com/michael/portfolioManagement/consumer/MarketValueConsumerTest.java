package com.michael.portfolioManagement.consumer;

import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarketValueConsumerTest<T> {
	private final Logger logger = LoggerFactory.getLogger(MarketValueConsumerTest.class);
	
	private BlockingQueue<T> queue;
	
	public MarketValueConsumerTest(BlockingQueue<T> queue){
		this.queue = queue;
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
			consumer.accept(queue.take());
			
			// Simulate a long running process
			Thread.sleep(1250);
		}
		catch(InterruptedException ie) {
			logger.error("MarketValueConsumer consume InterruptedException ie: {}", ie.getMessage());
			throw new RuntimeException(ie);
		}
		logger.debug("MarketValueConsumer consume ends.");
	}

}
