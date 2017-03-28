package com.michael.portfolioManagement.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReporterPrinter extends Thread {
	private final static Logger logger = LoggerFactory.getLogger(ReporterPrinter.class);
	
	private boolean isExit = false;
	
	private PrintServices printServices;
	
	

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		logger.debug("ReporterPrinter run.");
		
		while(!isExit) {
			try {
				printServices.printPortfolio();
				// Simulate a long running process
				Thread.sleep(10000);
			}
			catch(InterruptedException ie) {
				logger.error("ReporterPrinter InterruptedException ie: {}", ie.getMessage());
				throw new RuntimeException(ie);
			}
			
		}
		logger.debug("ReporterPrinter ends.");
	}
	
	/**
	 * @return the printServices
	 */
	public PrintServices getPrintServices() {
		return printServices;
	}

	/**
	 * @param printServices the printServices to set
	 */
	public void setPrintServices(PrintServices printServices) {
		this.printServices = printServices;
	}
}
