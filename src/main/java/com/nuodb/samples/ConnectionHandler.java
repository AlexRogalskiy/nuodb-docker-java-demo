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
public class ConnectionHandler {

	public static final String H2_IN_MEMORY_DATABASE = "H2 in-memory";

	public static final String NUODB_DATABASE = "NuoDB Database";

	/**
	 * Default database name - change as required or pass alternative via
	 * command-line.
	 */
	public static final String DEFAULT_DATABASE_NAME = "testdb"; // "test"

	/**
	 * The driver class provided by NuoDB: {@value}.
	 */
	public static final String NUODB_DRIVER_CLASS = "com.nuodb.jdbc.Driver";

	/**
	 * Default user name - change as required or pass alternative via command-line.
	 */
	public static final String DEFAULT_NUODB_USER = "dba";

	/**
	 * Default password - change as required or pass alternative via command-line.
	 */
	public static final String DEFAULT_NUODB_PASSWORD = "dba"; // "goalie"

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
	 * Default user name - change as required or pass alternative via command-line.
	 */
	public static final String NUODB_PLATFORM = "nuodb";

	/**
	 * The Hibernate dialect class for NuoDB.
	 */
	public static final String NUODB_HIBERNATE_DIALECT = "com.nuodb.hibernate.NuoDBDialect";

	/**
	 * Default schema: {@value}
	 */
	public static final String DEMO_SCHEMA = "demo";

	protected static final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);

	protected static ConnectionHandler connectionHandler;

	public final String databaseType;
	public final String driverClass;
	public final String user;
	public final String password;
	public final String dbUrl;
	public final String platform;
	public final String dialect;

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
		connectionHandler = new ConnectionHandler(args);
		connectionHandler.setBootProperties();
	}

	public static ConnectionHandler getConnectionInfo() {
		return connectionHandler;
	}

	@Override
	public String toString() {
		return databaseType.equals(H2_IN_MEMORY_DATABASE) ? //
				H2_IN_MEMORY_DATABASE : NUODB_DATABASE + ": " + user + "@" + dbUrl;
	}

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
	private ConnectionHandler(String[] args) {

		String[] connArgs = new String[] { DEFAULT_NUODB_USER, DEFAULT_NUODB_PASSWORD, DEFAULT_DATABASE_NAME };
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

		if (ix == 0) {
			this.databaseType = H2_IN_MEMORY_DATABASE;

			// Default to setup in application.properties
			this.driverClass = null;
			this.user = null;
			this.password = null;
			this.dbUrl = null;
			this.platform = null;
			this.dialect = null;
			return;
		}

		if (ix != 3) {
			// Use defaults
			logger.info("Three arguments expected: username, password and "
					+ "database-name.  Received only {}, using defaults.", ix);
		}

		this.databaseType = NUODB_DATABASE;
		this.driverClass = NUODB_DRIVER_CLASS;
		this.user = connArgs[0];
		this.password = connArgs[1];

		String dbName = connArgs[2];
		this.dbUrl = dbName.contains(":") ? dbName : //
				dbName.contains("/") ? NUODB_JDBC + dbName : NUODB_DATABASE_URL + dbName;

		this.platform = NUODB_PLATFORM;
		this.dialect = NUODB_HIBERNATE_DIALECT;

		logger.info(String.format("Connecting to %s:%s@%s", user, password, dbUrl));

		return;
	}

	private void setBootProperties() {
		setBootProperty("spring.datasource.driverClassName", driverClass);
		setBootProperty("spring.datasource.username", user);
		setBootProperty("spring.datasource.password", password);
		setBootProperty("spring.datasource.url", dbUrl);
		setBootProperty("spring.datasource.platform", platform);
		setBootProperty("spring.jpa.database-platform", dialect);
	}

	private void setBootProperty(String propName, String propValue) {
		if (Strings.isNotBlank(propValue))
			System.setProperty(propName, propValue);
	}

}
