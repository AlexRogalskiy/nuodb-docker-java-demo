package com.nuodb.samples;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.PersistenceException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.nuodb.samples.jpa.Account;
import com.nuodb.samples.jpa.AccountService;

/**
 * DEMO #5: An example program for accessing a NuoDB database using Spring Boot,
 * JPA and Hibernate.
 * <p>
 * For efficiency pooled connections should be always be used - Spring Boot uses
 * Hikari by default.
 * <p>
 * Restricting component scanning to ONLY pick up classes used by this demo.
 * 
 * @author Paul Chapman
 */
@EntityScan("com.nuodb.samples.jpa")
@EnableTransactionManagement(proxyTargetClass = true)
@SpringBootApplication(scanBasePackages = { "com.nuodb.samples.jpa", "com.nuodb.samples.web" })
public class SpringBootJpaDemo {

	protected static final Logger logger = LoggerFactory.getLogger(SpringBootJpaDemo.class);

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public Properties datasourceProperties() {
		return new Properties();
	}

	/**
	 * Uncomment this bean definition to use NuoDB's DataSource instead of Hikari's.
	 * Both support connection pooling.
	 * 
	 * @param datasourceProperties Spring Boot data source properties - those of the
	 *                             form {@code spring.datasource.xxx}.
	 * @return A NuoDB connection pooling data source
	 */
	//@Bean
	public DataSource nuoDbDataSource(Properties datasourceProperties) {
		// Use Spring Boot's default data source properties to configure
		// the NuoDB data source. You might prefer to specify custom
		// properties such as "nuodb.datasource.username" ...
		//
		// For details of all the properties this DataSource recognizes see
		// http://doc.nuodb.com/Latest/Content/JDBC-DataSource-versus-Driver-Connections.htm
		com.nuodb.jdbc.DataSource dataSource = new com.nuodb.jdbc.DataSource();

		dataSource.setUrl(datasourceProperties.getProperty("url"));
		dataSource.setUser(datasourceProperties.getProperty("username"));
		dataSource.setPassword(datasourceProperties.getProperty("password"));
		dataSource.setSchema(datasourceProperties.getProperty("hikari.schema"));

		return dataSource;

	}

	/**
	 * Spring Boot recommended way to run code at startup.
	 */
	@Component
	public class DemoRunner implements CommandLineRunner {

		// Only needed for logging
		@Autowired
		private Environment env;

		private Demo demoApp;

		public DemoRunner(SpringBootJpaApp app) {
			this.demoApp = app;
		}

		@Override
		public void run(String... args) throws Exception {
			logger.info("Running SpringBootJpaDemo");

			logger.info("Connecting to {}:{}@{}", //
					env.getProperty("spring.datasource.username"), //
					env.getProperty("spring.datasource.password"), //
					env.getProperty("spring.datasource.url"));

			demoApp.runDemo();
		}

	}

	/**
	 * The demo code.
	 */
	@Component
	public class SpringBootJpaApp implements Demo {

		//private int accountsFound = 0;

		@Autowired
		AccountService accountService;

		/**
		 * This constructor is only needed to log the data source type. Spring will
		 * automatically call it, as if annotated with @Autowired, because it is the
		 * only constructor on this spring bean.
		 * 
		 * @param dataSource
		 */
		public SpringBootJpaApp(DataSource dataSource) {
			logger.info("DataSource is an instance of {}", dataSource.getClass());
		}

		/**
		 * Creates a simple three-column table: id-name-balance.
		 * 
		 * @throws DataAccessException
		 */
		@Override
		public void createAccountTable() {
			// Nothing to do - we will let Spring Boot do it for us
		}

		/**
		 * Populate Accounts table with some sample data. Note this method runs a
		 * transaction internally, otherwise there is no entity-manager to use.
		 * <p>
		 * <b>Note:</b> This method is called from another method in the <i>same
		 * class</i> ({@link SpringBootJpaApp#runDemo()}, which means that using
		 * {@code @Transactional} won't work. Why? When a method in a Spring proxied
		 * object calls another method in the same object, <i>the second method is not
		 * proxied</i> - meaning in this case that {@code populateDemo()} would not be
		 * transactional.
		 * 
		 * @throws DataAccessException Spring catches any checked JPA
		 *                             {@link PersistenceException} and rethrows it as
		 *                             an unchecked {@link DataAccessException}s.
		 */
		@Override
		public void populateDemo() throws DataAccessException {

			List<Account> accounts = new ArrayList<Account>(ACCOUNT_DATA.length);

			for (AccountData accountData : ACCOUNT_DATA) {
				Account account = new Account(accountData.name, accountData.balance);
				accounts.add(account);
			}

			accountService.save(accounts);
			return;
		}

		/**
		 * List the accounts created by {@link {@link #populateDemo()}.
		 * 
		 * @throws DataAccessException Spring catches any checked JPA
		 *                             {@link PersistenceException}s and rethrows them
		 *                             as unchecked {@link DataAccessException}s.
		 */
		@Override
		public void displayAccounts() {

			int accountsFound = (int)accountService.totalAccounts();
			logger.info("Database contains {} accounts", accountsFound);

//			for (Account account : accountService.findAll()) {
//				accountsFound++;
//				logger.info(String.format(ACCOUNT_INFO, account.getName(), account.getId(), account.getBalance()));
//			}

			if (accountsFound != ACCOUNTS_EXPECTED) {
				throw new RuntimeException(String.format(ACCOUNTS_ERROR, ACCOUNTS_EXPECTED, accountsFound));
			}
		}
	}
}