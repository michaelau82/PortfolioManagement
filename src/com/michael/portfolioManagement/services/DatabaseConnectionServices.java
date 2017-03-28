package com.michael.portfolioManagement.services;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.michael.portfolioManagement.domain.AssetAllocation;
import com.michael.portfolioManagement.domain.Fund;
import com.michael.portfolioManagement.domain.Moneyness;
import com.michael.portfolioManagement.domain.Option;
import com.michael.portfolioManagement.domain.OptionStyle;
import com.michael.portfolioManagement.domain.OptionType;
import com.michael.portfolioManagement.domain.PositionType;
import com.michael.portfolioManagement.domain.Securities;

public class DatabaseConnectionServices {
	private final static Logger logger = LoggerFactory.getLogger(DatabaseConnectionServices.class);
	
	private Connection conn;
	private Statement stmt;
	private IDatabaseConnection connection;
	private IDataSet ids;
	private JdbcTemplate jdbcTemplate;
	
	private Map<String, String> sqlMap;
	
	private final static String CREATE_TABLE_SECURITIES = 
		"CREATE TABLE SECURITIES ( "
		+ "SECURITIES_ID VARCHAR(100) NOT NULL, "
		+ "EXPECTED_RETURN DOUBLE DEFAULT 0.0 NOT NULL, "
		+ "ANNUAL_STANDARD_DEVIVATION DOUBLE DEFAULT 0.0 NOT NULL, "
		+ "STOCK_PRICE DOUBLE DEFAULT 0.0 NOT NULL, "
		+ "SECURITIES_TYPE VARCHAR(20) NOT NULL, "
		+ "POSITION_TYPE VARCHAR(1) NOT NULL, "
		+ "CONSTRAINT PK_SECURITIES PRIMARY KEY(SECURITIES_ID) ) ";
	
	private final static String CREATE_TABLE_OPTION = 
		"CREATE TABLE OPTION ( "
		+ "SECURITIES_ID VARCHAR(100) NOT NULL, "
		+ "OPTION_PRICE DOUBLE DEFAULT 0.0 NOT NULL, "
		+ "STRIKE_PRICE DOUBLE DEFAULT 0.0 NOT NULL, "
		+ "EXPIRATION_DATE DATE NOT NULL, "
		+ "MONEYNESS VARCHAR(1) NOT NULL, "
		+ "OPTION_TYPE VARCHAR(1) NOT NULL, "
		+ "OPTION_STYLE VARCHAR(1) NOT NULL, "
		+ "CONSTRAINT PK_OPTION PRIMARY KEY(SECURITIES_ID), "
		+ "FOREIGN KEY(SECURITIES_ID) REFERENCES SECURITIES(SECURITIES_ID) ) ";
	
	private final static String CREATE_TABLE_ASSET_ALLOCATION = 
		"CREATE TABLE ASSET_ALLOCATION ( "
		+ "FUND_ACCOUNT_ID VARCHAR(100) NOT NULL, "
		+ "ASSET_WEIGHT DECIMAL(5,2) DEFAULT 0.0 NOT NULL, "
		+ "SECURITIES_ID VARCHAR(100) NOT NULL, "
		+ "QUANTITIES BIGINT NOT NULL, "
		+ "FOREIGN KEY(SECURITIES_ID) REFERENCES SECURITIES(SECURITIES_ID), "
		+ "FOREIGN KEY(FUND_ACCOUNT_ID) REFERENCES FUND(FUND_ACCOUNT_ID) ) ";
	
	private final static String CREATE_TABLE_FUND = 
		"CREATE TABLE FUND ( "
		+ "FUND_ACCOUNT_ID VARCHAR(100) NOT NULL, "
		+ "OS_SHARES BIGINT NOT NULL, "
		+ "CONSTRAINT PK_FUND PRIMARY KEY(FUND_ACCOUNT_ID) ) ";
	
	private final static String INSERT_SECURITIES = 
		"INSERT INTO SECURITIES (SECURITIES_ID, EXPECTED_RETURN, ANNUAL_STANDARD_DEVIVATION, STOCK_PRICE, SECURITIES_TYPE, POSITION_TYPE) "
		+ "VALUES(?, ?, ?, ?, ?, ?)";
	
	private final static String INSERT_OPTION = 
		"INSERT INTO OPTION (SECURITIES_ID, OPTION_PRICE, STRIKE_PRICE, EXPIRATION_DATE, MONEYNESS, OPTION_TYPE, OPTION_STYLE) "
		+ "VALUES(?, ?, ?, ?, ?, ?, ?)";
	
	private final static String INSERT_ASSET_ALLOCATION = 
		"INSERT INTO ASSET_ALLOCATION (FUND_ACCOUNT_ID, ASSET_WEIGHT, SECURITIES_ID, QUANTITIES) "
		+ "VALUES(?, ?, ?, ?)";
	
	private final static String INSERT_FUND = 
		"INSERT INTO FUND (FUND_ACCOUNT_ID, OS_SHARES) "
		+ "VALUES(?, ?)";
	
	
	public void connectH2DB() throws SQLException, DatabaseUnitException {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUrl("jdbc:h2:./db/test");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		try {
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			connection = new DatabaseConnection(conn);
			ids = connection.createDataSet();
		} catch (SQLException sqle) {
			logger.error("SQLException sqle: {}", sqle.getMessage());
			throw sqle;
		} catch (DatabaseUnitException dbue) {
			logger.error("DatabaseUnitException sqle: {}", dbue.getMessage());
			throw dbue;
		}
	}
	
	public void dataSetup() throws SQLException, DataAccessException {
		sqlMap = getCreateTableSQLs();
		createApplicationTables(sqlMap);
		insertStaticData();
	}
	
	private Map<String, String> getCreateTableSQLs(){
		Map<String, String> sqlMap = new HashMap<String, String>();
		
		// SECURITIES
		String tableName = "SECURITIES";
		String sql = CREATE_TABLE_SECURITIES;
		sqlMap.put(tableName, sql);
		
		// OPTION
		tableName = "OPTION";
		sql = CREATE_TABLE_OPTION;
		sqlMap.put(tableName, sql);
		
		// ASSET ALLOCATION / PORTFOLIO
		tableName = "ASSET_ALLOCATION";
		sql = CREATE_TABLE_ASSET_ALLOCATION;
		sqlMap.put(tableName, sql);
		
		// FUND
		tableName = "FUND";
		sql = CREATE_TABLE_FUND;
		sqlMap.put(tableName, sql);
				
		return sqlMap;
	}
	
	private void createApplicationTables(final Map<String, String> sqlMap) throws SQLException {
		for(final String tableName: sqlMap.keySet()) {
			StringBuilder createTable = new StringBuilder();
			String sql = sqlMap.get(tableName);
			createTable.append(sql);
			logger.debug("create table tableName: {}", tableName);
			logger.debug("create table sql: {}", sql);
			
			ITable iTable = null;
			try {
				iTable = ids.getTable(tableName);
			} catch(DataSetException dse) {
				logger.debug("get table dse: {}", dse.getMessage());
			}
			try {
				if (iTable != null) {
					logger.debug("{} is existing", tableName);
					try {
						stmt.executeUpdate("DROP TABLE " + tableName);
						logger.debug("{} is dropped.", tableName);
					} catch (SQLException sqle){
						logger.error("Drop table {} failed. SQLException: {}", tableName, sqle.getMessage());
					}
				}
				stmt.executeUpdate(createTable.toString());
				logger.debug("{} is created", tableName);
			} catch (SQLException sqle){
				logger.debug("createApplicationTables SQLException sqle: {}", sqle.getMessage());
				throw sqle;
			}
		}
	}
	
	private void insertStaticData() throws DataAccessException {
		String fundAccountID = "FUND_ACCOUNT_1";
		Fund fund = new Fund();
		fund.setFundAccountID(fundAccountID);
		fund.setOSShare(10000);
		
		List<Securities> securitiesList = new ArrayList<Securities>();
		Securities securities1 = new Securities();
		securities1.setSecuritiesID("COMMON_STOCK_1");
		securities1.setExpectedReturn(0.05);
		securities1.setAnnualizedStandardDeviation(0.02);
		securities1.setStockPrice(5.1);
		securities1.setSecuritiesType("STOCK");
		securities1.setPositionType(PositionType.LONG);
		securitiesList.add(securities1);
		
		Securities securities2 = new Securities();
		securities2.setSecuritiesID("EUROPEAN_CALL_OPTION_1");
		securities2.setExpectedReturn(0.13);
		securities2.setAnnualizedStandardDeviation(0.2);
		securities2.setStockPrice(1.2);
		securities2.setSecuritiesType("CALL_OPTION");
		securities2.setPositionType(PositionType.LONG);
		securitiesList.add(securities2);
		
		Securities securities3 = new Securities();
		securities3.setSecuritiesID("EUROPEAN_PUT_OPTION_1");
		securities3.setExpectedReturn(0.11);
		securities3.setAnnualizedStandardDeviation(0.4);
		securities3.setStockPrice(1.5);
		securities3.setSecuritiesType("PUT_OPTION");
		securities3.setPositionType(PositionType.SHORT);
		securitiesList.add(securities3);
		
		List<Option> optionList = new ArrayList<Option>();
		Option callOption = new Option();
		callOption.setSecuritiesID("EUROPEAN_CALL_OPTION_1");
		callOption.setPrice(1.2);
		callOption.setStrikePrice(1.3);
		Date expirationDate1 = Date.valueOf("2017-07-01");
		callOption.setExpirationDate(expirationDate1);
		callOption.setMoneyness(Moneyness.AT_THE_MONEY);
		callOption.setOptionStyle(OptionStyle.EUROPEAN_STYLE);
		callOption.setOptionType(OptionType.CALL);
		optionList.add(callOption);
		
		Option putOption = new Option();
		putOption.setSecuritiesID("EUROPEAN_PUT_OPTION_1");
		putOption.setPrice(1.2);
		putOption.setStrikePrice(1.1);
		Date expirationDate2 = Date.valueOf("2017-07-02");
		putOption.setExpirationDate(expirationDate2);
		putOption.setMoneyness(Moneyness.AT_THE_MONEY);
		putOption.setOptionStyle(OptionStyle.EUROPEAN_STYLE);
		putOption.setOptionType(OptionType.PUT);
		optionList.add(putOption);
		
		List<AssetAllocation> assetAllocationList = new ArrayList<AssetAllocation>();
		AssetAllocation assetAllocation1 = new AssetAllocation();
		assetAllocation1.setFundAccountID(fundAccountID);
		assetAllocation1.setAssetWeight(new BigDecimal(0.5));
		assetAllocation1.setSecurities(securities1);
		assetAllocation1.setQuantities(4000);
		assetAllocationList.add(assetAllocation1);
		
		AssetAllocation assetAllocation2 = new AssetAllocation();
		assetAllocation2.setFundAccountID(fundAccountID);
		assetAllocation2.setAssetWeight(new BigDecimal(0.2));
		assetAllocation2.setSecurities(securities2);
		assetAllocation2.setQuantities(500);
		assetAllocationList.add(assetAllocation2);
		
		AssetAllocation assetAllocation3 = new AssetAllocation();
		assetAllocation3.setFundAccountID(fundAccountID);
		assetAllocation3.setAssetWeight(new BigDecimal(0.3));
		assetAllocation3.setSecurities(securities3);
		assetAllocation3.setQuantities(750);
		assetAllocationList.add(assetAllocation3);
		
		
		// Batch insert FUND
		BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, fund.getFundAccountID());
				ps.setLong(2, fund.getOSShare());
			}

			@Override
			public int getBatchSize() {
				return 1;
			}
		};

		try {
			final int[] rowsInserted = jdbcTemplate.batchUpdate(INSERT_FUND, setter);
			logger.debug("{} rows inserted in FUND.", rowsInserted);
		} catch (DataAccessException dae) {
			logger.error("Insert Fund DataAccessException dae: {}", dae.getMessage());
			throw dae;
		}
		
		// Batch insert SECURITIES
		setter = new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Securities securities = securitiesList.get(i);
				ps.setString(1, securities.getSecuritiesID());
				ps.setDouble(2, securities.getExpectedReturn());
				ps.setDouble(3, securities.getAnnualizedStandardDeviation());
				ps.setDouble(4, securities.getStockPrice());
				ps.setString(5, securities.getSecuritiesType());
				ps.setString(6, securities.getPositionType().getType());
			}
			
			@Override
			public int getBatchSize() {
				return securitiesList.size();
			}
		};
		
		try {
			final int[] rowsInserted = jdbcTemplate.batchUpdate(INSERT_SECURITIES, setter);
			logger.debug("{} rows inserted in SECURITIES.", rowsInserted);
		}catch (DataAccessException dae){
			logger.error("Insert securities DataAccessException dae: {}", dae.getMessage());
			throw dae;
		}
		
		// Batch insert OPTION
		setter = new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Option option = optionList.get(i);
				ps.setString(1, option.getSecuritiesID());
				ps.setDouble(2, option.getPrice());
				ps.setDouble(3, option.getStrikePrice());
				ps.setDate(4, option.getExpirationDate());
				ps.setString(5, option.getMoneyness().getMoneyness());
				ps.setString(6, option.getOptionType().getType());
				ps.setString(7, option.getOptionStyle().getStyle());
			}

			@Override
			public int getBatchSize() {
				return optionList.size();
			}
		};

		try {
			final int[] rowsInserted = jdbcTemplate.batchUpdate(INSERT_OPTION, setter);
			logger.debug("{} rows inserted in OPTION.", rowsInserted);
		} catch (DataAccessException dae) {
			logger.error("Insert option DataAccessException dae: {}", dae.getMessage());
			throw dae;
		}
		
		// Batch insert ASSET¡@ALLOCATION
		setter = new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				AssetAllocation assetAllocation = assetAllocationList.get(i);
				ps.setString(1, assetAllocation.getFundAccountID());
				ps.setBigDecimal(2, assetAllocation.getAssetWeight());
				ps.setString(3, assetAllocation.getSecurities().getSecuritiesID());
				ps.setLong(4, assetAllocation.getQuantities());
			}

			@Override
			public int getBatchSize() {
				return assetAllocationList.size();
			}
		};

		try {
			final int[] rowsInserted = jdbcTemplate.batchUpdate(INSERT_ASSET_ALLOCATION, setter);
			logger.debug("{} rows inserted in ASSET_ALLOCATION.", rowsInserted);
		} catch (DataAccessException dae) {
			logger.error("Insert asset allocation DataAccessException dae: {}", dae.getMessage());
			throw dae;
		}
	}
	
	public void closeConnection() throws SQLException {
		dropTables();
		if(stmt != null) {
			stmt.close();
		}
		if(conn != null){
			conn.close();
		}
	}
	
	private void dropTables() {
		for(final String tableName: sqlMap.keySet()){
			try {
				stmt.executeUpdate("DROP TABLE " + tableName);
				logger.debug("{} is dropped.", tableName);
			} catch (SQLException sqle){
				logger.error("Drop table {} failed. SQLException: {}", tableName, sqle.getMessage());
			}
		}
	}
	
	/**
	 * @return the jdbcTemplate
	 */
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	/**
	 * @param jdbcTemplate the jdbcTemplate to set
	 */
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
}
