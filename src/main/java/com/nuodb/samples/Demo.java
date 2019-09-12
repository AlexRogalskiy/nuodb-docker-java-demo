package com.nuodb.samples;

/**
 * Defines the three steps in each demo. Also provides useful constant data,
 * such as the SQL to run & the account data used to populate the database.
 * 
 * @author Paul Chapman
 */
public interface Demo {

	/**
	 * SQL to drop the Accounts table.
	 */
	public static final String DROP_TABLE_ACCOUNTS = "DROP TABLE Accounts IF EXISTS";

	/**
	 * SQL to create the Accounts table using a NuoDB generated key.
	 */
	public static final String CREATE_TABLE_ACCOUNTS = //
			"CREATE TABLE Accounts (id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, name STRING)";

	/**
	 * Just to show it can be done, add the balance column afterwards.
	 */
	public static final String ALTER_TABLE_ACCOUNTS = "ALTER TABLE Accounts ADD COLUMN balance INT";

	/**
	 * SQL to insert a row into the Accounts table.
	 */
	public static final String INSERT_INTO_ACCOUNTS = "INSERT INTO Accounts (name, balance) VALUES (?, ?)";

	/**
	 * SQL to fetch the first 30 rows in the Accounts table. In our demos the table
	 * is smaller than this, but as a general rule, avoid selecting all the rows
	 * from a table unless you can guarantee it will never be that large. You do not
	 * want to fetch half a million rows by mistake.
	 */
	public static final String SELECT_ALL_ACCOUNTS = "SELECT id, name, balance FROM accounts LIMIT 30";

	/**
	 * Error message if accounts found does not match the accounts created.
	 */
	public static final String ACCOUNTS_ERROR = "Expected %d accounts, but received %d";

	/**
	 * String format for displaying the details of an account.
	 */
	public static final String ACCOUNT_INFO = "%-9s (ID = %2s) - balance %7s";

	/**
	 * Holds an account name and balance - see {@link Demo#ACCOUNT_DATA}.
	 */
	public static class AccountData {
		public final String name;
		public final int balance;

		public AccountData(String name, int balance) {
			this.name = name;
			this.balance = balance;
		}
	}

	/**
	 * Account data to save to the Accounts table - instances of
	 * {@link AccountData}.
	 */
	public static final AccountData[] ACCOUNT_DATA = { //
			new AccountData("Ayesha", 15000), //
			new AccountData("Ada", 47000), //
			new AccountData("Andrei", 52900), //
			new AccountData("Bobo", 4750), //
			new AccountData("Chung", 4000), //
			new AccountData("Cruz", 55000), //
			new AccountData("Fang", 90000), //
			new AccountData("Kungawo", 69500), //
			new AccountData("Leslie", 72000), //
			new AccountData("Matt", 100000), //
			new AccountData("Max", 47000), //
			new AccountData("Maya", 8000), //
			new AccountData("Mia", 40800), //
			new AccountData("Morgan", 10000), //
			new AccountData("Silas", 11000), //
			new AccountData("Stefan", 2000), //
			new AccountData("Taj", 63000), //
			new AccountData("Tyler", 20000), //
			new AccountData("Uma", 47000), //
			new AccountData("Val", 2500), //
			new AccountData("Zoe", 6700) //
	};

	/**
	 * Number of accounts expected in the Accounts table after populating.
	 */
	public static final int ACCOUNTS_EXPECTED = ACCOUNT_DATA.length;

	/**
	 * Drop the Accounts table (if necessary) then create an empty Accounts table
	 * with three columns: id, name, balance.
	 */
	public void createAccountTable();

	/**
	 * Populate Accounts table using the sample data in {@link #ACCOUNT_DATA}.
	 */
	public void populateDemo();

	/**
	 * Display contents of the Accounts table assuming it populated correctly.
	 */
	public void displayAccounts();

	/**
	 * Run the demo. Executes:
	 * <ul>
	 * <li>{@link #createAccountTable()}
	 * <li>{@link #populateDemo()}
	 * <li>{@link #displayAccounts()}
	 * </ul>
	 * Each demo implements these three methods in its own way.
	 */
	public default void runDemo() {
		createAccountTable();
		populateDemo();
		displayAccounts();
	}

}