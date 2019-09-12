package com.nuodb.samples;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base functionality and constants for the demos. Each demo implements its own
 * version of:
 * <ul>
 * <li>{@link #createAccountTable()}
 * <li>{@link #populateDemo()}
 * <li>{@link #displayAccounts()}
 * </ul>
 * 
 * For convenience, this class also performs command-line argument parsing.
 * <p>
 * 
 * @author Paul Chapman
 */
public abstract class DemoBaseClass implements Demo {

	/**
	 * Default database name - change as required or pass alternative via
	 * command-line.
	 */
	public static final String DEFAULT_DATABASE_NAME = "testdb"; // "test"

	/**
	 * Default user name - change as required or pass alternative via command-line.
	 */
	public static final String DEFAULT_NUODB_USER = "dba";

	/**
	 * Default password - change as required or pass alternative via command-line.
	 */
	public static final String DEFAULT_NUODB_PASSWORD = "dba"; // "goalie"

	/**
	 * The driver class provided by NuoDB: {@value}.
	 */
	public static final String NUODB_DRIVER_CLASS = "com.nuodb.jdbc.Driver";

	/**
	 * Default user name - change as required or pass alternative via command-line.
	 */
	public static final String DEFAULT_H2_USER = "sa";

	/**
	 * Default password - change as required or pass alternative via command-line.
	 */
	public static final String DEFAULT_H2_PASSWORD = "";

	/**
	 * The driver class provided by H2: {@value}.
	 */
	public static final String H2_DRIVER_CLASS = "org.h2.Driver";

	/**
	 * The JDBC connection prefix for connecting to a NuoDB database server. There
	 * should be no need to change this.
	 */
	public static final String NUODB_JDBC = "jdbc:com.nuodb://";

	/**
	 * The base URL for connecting to a local database server - change for a remote
	 * server if required or pass alternative via command-line.
	 */
	public static final String NUODB_DATABASE_URL = NUODB_JDBC + "localhost/";

	/**
	 * The Hibernate dialect class for H2.
	 */
	public static final String H2_HIBERNATE_DIALECT = "org.hibernate.dialect.H2Dialect";

	/**
	 * Default schema: {@value}
	 */
	public static final String DEMO_SCHEMA = "demo";

	protected static final Logger logger = LoggerFactory.getLogger(DemoBaseClass.class);

	public static class ConnectionInfo {
		public final String driverClass;
		public final String user;
		public final String password;
		public final String dbUrl;
		
		public ConnectionInfo(String driverClass, String user, String password, String dbUrl) {
			this.driverClass = driverClass;
			this.user = user;
			this.password = password;
			this.dbUrl = dbUrl;
		}

		@Override
		public String toString() {
			return user + ":" + password + "@" + dbUrl;
		}
	}

	/**
	 * Holds the NuoDB connection details.
	 */
	protected final ConnectionInfo connectionInfo;

	/**
	 * Get the connection details from the command-line arguments, if specified, or
	 * use the defaults: {@link #DEFAULT_NUODB_USER},
	 * {@link #DEFAULT_NUODB_PASSWORD}, {@link #DEFAULT_DATABASE_NAME},
	 * 
	 * @param args Command line arguments. Expects:
	 *             <li>user name - the user name for the connection
	 *             <li>password - the password for the given user
	 *             <li>dbName - the name of database running on localhost or a
	 *             database location in the form 'host/db-name'.
	 * @return The connection properties found.
	 */
	private static ConnectionInfo getArgsOrDefaults(String[] args) {

		String[] connArgs = //
				new String[] { DEFAULT_NUODB_USER, DEFAULT_NUODB_PASSWORD, DEFAULT_DATABASE_NAME };
		int ix = 0;

		for (String arg : args) {
			// Spring Boot passes "--spring.output.ansi.enabled=always", ignore
			if (!arg.startsWith("--")) {
				if (ix == 3)
					logger.warn("Ignoring unexpected argument: {}", arg);
				else
					connArgs[ix++] = arg;
			}
		}

		if (ix == 0)
			return null;  // Default to setup in application.properties
		
		if (ix != 3) {
			// Use defaults
			logger.info("Three arguments expected: username, password and "
					+ "database-name.  Received only {}, using defaults.", ix);
		}

		String driverClass = NUODB_DRIVER_CLASS;
		String user = connArgs[0];
		String password = connArgs[1];
		String dbName = connArgs[2];
		String dbUrl;

		if (dbName.startsWith("h2/")) {
			String[] bits = dbName.split("/");
			String b1 = bits[1];
			dbName = Strings.isNotBlank(b1) ? b1 : DEFAULT_DATABASE_NAME;
			dbUrl = "jdbc:h2:mem:" + dbName + ";INIT=CREATE SCHEMA IF NOT EXISTS demo;";
			user = DEFAULT_H2_USER;
			password = DEFAULT_H2_PASSWORD;
			driverClass = H2_DRIVER_CLASS;
			System.setProperty("spring.datasource.platform", "h2");
			System.setProperty("spring.jpa.database-platform", H2_HIBERNATE_DIALECT);
		} else {
			dbUrl = dbName.contains(":") ? dbName : //
					dbName.contains("/") ? NUODB_JDBC + dbName : NUODB_DATABASE_URL + dbName;
		}

		logger.info(String.format("Connecting to %s:%s@%s", user, password, dbUrl));
		return new ConnectionInfo(driverClass, user, password, dbUrl);
	}

	/**
	 * Get the connection details from the command-line arguments.
	 * <p>
	 * If no arguments are specified, the values in {@code application.properties}
	 * are used.
	 * <p>
	 * If less than the expected 3 arguments are provided, the defaults are used:
	 * {@link #DEFAULT_NUODB_PASSWORD}, {@link #DEFAULT_DATABASE_NAME}.
	 * <p>
	 * The command-line values are used to override the Spring Boot properties
	 * {@code spring.datasource.user}, {@code spring.datasource.password} and
	 * {@code spring.datasource.url}.
	 * <p>
	 * <b>NOTE:</b> Using {@code application.properties} is convenient but not
	 * flexible. You can specify any Spring Boot property on the command-line, but
	 * syntax is verbose:
	 * {@code java -jar myapp.jar --spring.datasource.username=dba}.
	 * <p>
	 * This method allows you to simply pass values in a known order, then sets the
	 * properties for you.
	 * 
	 * @param args Command line arguments. Expects:
	 *             <li>user name - the user name for the connection
	 *             <li>password - the password for the given user
	 *             <li>dbName - the name of database running on localhost or a
	 *             database location in the form 'host/db-name'.
	 * @return The connection properties found.
	 */
	public static void setBootPropertiesFromCommandLine(String[] args) {
		ConnectionInfo connectionInfo = getArgsOrDefaults(args);
		
		if (connectionInfo == null)
			return; // Nothing to do, use properties in application.properties

		System.setProperty("spring.datasource.driverClassName", connectionInfo.driverClass);
		System.setProperty("spring.datasource.username", connectionInfo.user);
		System.setProperty("spring.datasource.password", connectionInfo.password);
		System.setProperty("spring.datasource.url", connectionInfo.dbUrl);
	}

	/**
	 * Creates an instance of DemoBaseClass and saves away the specified connection
	 * properties to be used by any implementing subclass.
	 * <p>
	 * If insufficient arguments are provided, it uses the defaults for the missing
	 * arguments: {@link #DEFAULT_NUODB_USER}, {@link #DEFAULT_NUODB_PASSWORD} and
	 * {@link #DEFAULT_DATABASE_NAME}.
	 *
	 * @param args Command line arguments. Expects:
	 *             <li>user name - the user name for the connection
	 *             <li>password - the password for the given user
	 *             <li>dbName - the name of database running on localhost or a
	 *             database location in the form 'host/db-name'.
	 */
	private DemoBaseClass(String[] args) {
		this.connectionInfo = getArgsOrDefaults(args);
	}

}
