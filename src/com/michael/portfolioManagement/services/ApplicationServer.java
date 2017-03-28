package com.michael.portfolioManagement.services;

import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.dbunit.DatabaseUnitException;
import org.h2.store.fs.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.michael.portfolioManagement.consumer.MarketValueConsumer;
import com.michael.portfolioManagement.domain.Securities;
import com.michael.portfolioManagement.producer.MarketDataProducer;

public class ApplicationServer {
	private final static Logger logger = LoggerFactory.getLogger(ApplicationServer.class);
	private static final int queueDepth = 100;
	private final BlockingQueue<Securities> queue = new ArrayBlockingQueue<Securities>(queueDepth);
	private final DatabaseConnectionServices databaseConnectionServices = new DatabaseConnectionServices();
	private PricingServices pricingServices = new PricingServices();
	private PrintServices printServices = new PrintServices();
	
	public ApplicationServer(){
		printServices.setPricingServices(pricingServices);
	}
	
	public void start(){
		try {
			databaseConnectionServices.connectH2DB();
			databaseConnectionServices.dataSetup();
			JdbcTemplate jdbcTemplate = databaseConnectionServices.getJdbcTemplate();
			pricingServices.getAssetAllocationDaoImpl().setJdbcTemplate(jdbcTemplate);
			pricingServices.getOptionDaoImpl().setJdbcTemplate(jdbcTemplate);
			startMarketValueProducer();
			startMarketValueConsumer();
			startPrinter();
		} catch (SQLException | DatabaseUnitException e) {
			logger.error("ApplicationServer connect db Exception e: {}", e.getMessage());
		} catch (DataAccessException dae) {
			logger.error("ApplicationServer data setup Exception dae: {}", dae.getMessage());
		}
	}
	
	/**
	 * Market Data Producer thread
	 */
	private void startMarketValueProducer(){
		final MarketDataProducer<Securities> producer = new MarketDataProducer<Securities>(queue, Securities.class);
		producer.setPricingServices(pricingServices);
		producer.start();
		logger.debug("Market Data Producer starts.");
	}
	
	/**
	 * Market Data Consumer thread
	 */
	private void startMarketValueConsumer(){
		final MarketValueConsumer<Securities> consumer = new MarketValueConsumer<Securities>(queue, Securities.class);
		consumer.setPricingServices(pricingServices);
		consumer.start();
		logger.debug("Market Data Consumer starts.");
	}
	
	/**
	 * Report Printer
	 */
	private void startPrinter(){
		final ReporterPrinter reporterPrinter = new ReporterPrinter();
		reporterPrinter.setPrintServices(printServices);
		reporterPrinter.start();
		logger.debug("Report Printer starts.");
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Application Server starts.");
		new ApplicationServer().start();
	}

}
