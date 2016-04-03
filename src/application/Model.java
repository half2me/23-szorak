/**
 * This JavaFX skeleton is provided for the Software Laboratory 5 course. Its structure
 * should provide a general guideline for the students.
 * As suggested by the JavaFX model, we'll have a GUI (view),
 * a controller class and a model (this one).
 */

package application;

import com.sun.org.apache.xpath.internal.operations.Mod;

import java.sql.*;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

// Model class
public class Model {

	// Database driver and URL
	protected static final String driverName = "oracle.jdbc.driver.OracleDriver";
	protected static final String databaseUrl = "jdbc:oracle:thin:@rapid.eik.bme.hu:1521:szglab";

	// Product name and product version of the database
	protected String databaseProductName = null;
	protected String databaseProductVersion = null;

	// Connection object
	protected Connection connection = null;

	// Enum structure for Exercise #2
	protected enum ModifyResult {
		InsertOccured, UpdateOccured, Error
	}

	// String containing last error message
	protected String lastError = "";

	/**
	 * Model constructor
	 */
	public Model() {
	}

	/**
	 * Gives product name of the database
	 *
	 * @return Product name of the database
	 */
	public String getDatabaseProductName() {

		return databaseProductName;

	}

	/**
	 * Gives product version of the database
	 *
	 * @return Product version of the database
	 */
	public String getDatabaseProductVersion() {

		return databaseProductVersion;

	}

	/**
	 * Gives database URL
	 *
	 * @return Database URL
	 */
	public String getDatabaseUrl() {

		return databaseUrl;

	}

	/**
	 * Gives the message of last error
	 *
	 * @return Message of last error
	 */
	public String getLastError() {

		return lastError;

	}

	/**
	 * Tries to connect to the database
	 *
	 * @param userName
	 *            User who has access to the database
	 * @param password
	 *            User's password
	 * @return True on success, false on fail
	 */
	public boolean connect(String userName, String password) {

		try {

			// If connection status is disconnected
			if (connection == null || !connection.isValid(30)) {

				if (connection == null) {

					// Load the specified database driver
					Class.forName(driverName);

					// Driver is for Oracle 12cR1 (certified with JDK 7 and JDK
					// 8)
					if (java.lang.System.getProperty("java.vendor").equals("Microsoft Corp.")) {
						DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
					}
				} else {

					connection.close();

				}

				// Create new connection and get metadata
				connection = DriverManager.getConnection(databaseUrl, userName, password);
				DatabaseMetaData dbmd = connection.getMetaData();

				databaseProductName = dbmd.getDatabaseProductName();
				databaseProductVersion = dbmd.getDatabaseProductVersion();

			}

			return true;

		} catch (SQLException e) {

			// !TODO: More user friendly error handling
			// use 'error' String beginning of the error string
			lastError = "error ".concat(e.toString());
			return false;

		} catch (ClassNotFoundException e) {
			// !TODO: More user friendly error handling
			// use 'error' String beginning of the error string
			lastError = "error ".concat(e.toString());
			return false;

		}

	}

	/**
	 * Tests the database connection by submitting a query
	 *
	 * @return True on success, false on fail
	 */
	public String testConnection() {

		try {

			// Create SQL query and execute it
			// If user input has to be processed, use PreparedStatement instead!
			Statement stmt = connection.createStatement();
			ResultSet rset = stmt.executeQuery("SELECT count(*) FROM oktatas.igazolvanyok");

			// Process the results
			String result = null;
			while (rset.next()) {
				result = String.format("Total number of rows in 'Igazolvanyok' table in 'Oktatas' schema: %s",
						rset.getString(1));
			}

			// Close statement
			stmt.close();

			return result;

		} catch (SQLException e) {
			// !TODO: More user friendly error handling
			// use 'error' String beginning of the error string
			lastError = "error ".concat(e.toString());
			return null;

		}
	}

	/**
	 * Method for Exercise #1
	 * @param keyword Search keyword
	 * @return Result of the query
	 */
	public ResultSet search(String keyword) {
		try {
			ResultSet places = null;
			if (keyword == null || keyword.equals("")) {
				// No search string is specified we list everything
				Statement s = connection.createStatement();
				places = s.executeQuery("SELECT NAME, ADDRESS, PHONE FROM PLACES");
			} else {
				String query = "SELECT NAME, ADDRESS, PHONE FROM PLACES WHERE LOWER(PLACES.NAME) LIKE ?";
				PreparedStatement ps = connection.prepareStatement(query);
				ps.setString(1, "%" + keyword.toLowerCase() + "%");
				places = ps.executeQuery();
			}
			return places;
		} catch (SQLException e) {
			// !TODO: More user friendly error handling
			// use 'error' String beginning of the error string
			lastError = "error ".concat(e.toString());
			return null;
		}
	}

	/**
	 * Method for Exercise #2-#3
	 *
	 * @param data
	 *            New or modified data
	 * @param AutoCommit set the connection type (use default true, and 4.1 use false
	 * @return Type of action has been performed
	 */
	public ModifyResult modifyData(Map data, boolean AutoCommit) {
		String query =
				"MERGE INTO PERSONS USING dual ON ( Persons.person_id = ? )" +
				"WHEN MATCHED THEN UPDATE SET " +
						"Persons.name = ?, " +
						"Persons.address = ?, " +
						"Persons.phone = ?, " +
						"Persons.income = ?, " +
						"Persons.hobby = ?, " +
						"Persons.favourite_movie = ? " +
				"WHEN NOT MATCHED THEN INSERT VALUES (?, ?, ?, ?, ?, ?, ?)";
		try {
			connection.setAutoCommit(AutoCommit);
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, Integer.parseInt((String)data.get("person_id")));
			ps.setString(2, (String)data.get("name"));
			ps.setString(3, (String)data.get("address"));
			ps.setString(4, (String)data.get("phone"));
			ps.setString(5, (String)data.get("income"));
			ps.setString(6, (String)data.get("hobby"));
			ps.setString(7, (String)data.get("favourite_movie"));
			ps.setInt(8, Integer.parseInt((String)data.get("person_id")));
			ps.setString(9, (String)data.get("name"));
			ps.setString(10, (String)data.get("address"));
			ps.setString(11, (String)data.get("phone"));
			ps.setString(12, (String)data.get("income"));
			ps.setString(13, (String)data.get("hobby"));
			ps.setString(14, (String)data.get("favourite_movie"));
			int result = ps.executeUpdate();
			if (result > 0) {
				return ModifyResult.UpdateOccured;
			} else {
				return ModifyResult.InsertOccured;
			}
		} catch (SQLException e) {
			lastError = "error ".concat(e.toString());
			return ModifyResult.Error;
		}
	}


	/**
	 * Method for Exercise #4
	 *
	 * @return True on success, false on fail
	 */
	public boolean commit() {
		//TODO task 4
		return false;
	}

	/**
	 * Method for Exercise #4
	 */
	public void rollback(){
		//TODO task 4
	}

	/**
	 * Method for Exercise #5
	 *
	 * @return Result of the query
	 */
	public ResultSet getStatistics() {
		//TODO task 5
		return null;

	}

}
