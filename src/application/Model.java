/**
 * This JavaFX skeleton is provided for the Software Laboratory 5 course. Its structure
 * should provide a general guideline for the students.
 * As suggested by the JavaFX model, we'll have a GUI (view),
 * a controller class and a model (this one).
 */

package application;

import com.sun.javafx.binding.IntegerConstant;
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
		String query = "SELECT COUNT(*) FROM PERSONS WHERE PERSONS.PERSON_ID = ?";
		ModifyResult type = ModifyResult.Error;

		try {
			connection.setAutoCommit(AutoCommit);
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, Integer.parseInt((String)data.get("person_id")));
			ResultSet check = ps.executeQuery();
			check.next();

			if (Integer.parseInt(check.getString(1)) > 0) {
				// Update
				type = ModifyResult.UpdateOccured;
				query = "UPDATE PERSONS SET " +
						"Persons.name = ?, " +
						"Persons.address = ?, " +
						"Persons.phone = ?, " +
						"Persons.income = ?, " +
						"Persons.hobby = ?, " +
						"Persons.favourite_movie = ? " +
						"WHERE Persons.person_id = ?";
				ps = connection.prepareStatement(query);
				ps.setString(1, (String)data.get("name"));
				ps.setString(2, (String)data.get("address"));
				ps.setString(3, (String)data.get("phone"));
				if ( !((String)data.get("income")).isEmpty() ) ps.setInt(4, Integer.parseInt((String)data.get("income")));
				else ps.setNull(4, java.sql.Types.INTEGER);
				ps.setString(5, (String)data.get("hobby"));
				ps.setString(6, (String)data.get("favourite_movie"));
				ps.setInt(7, Integer.parseInt((String)data.get("person_id")));
			} else {
				// INSERT
				type = ModifyResult.InsertOccured;
				query = "INSERT INTO PERSONS VALUES (?, ?, ?, ?, ?, ?, ?)";
				ps = connection.prepareStatement(query);
				ps.setInt(1, Integer.parseInt((String)data.get("person_id")));
				ps.setString(2, (String)data.get("name"));
				ps.setString(3, (String)data.get("address"));
				ps.setString(4, (String)data.get("phone"));
				if ( !((String)data.get("income")).isEmpty() ) ps.setInt(5, Integer.parseInt((String)data.get("income")));
				else ps.setNull(5, java.sql.Types.INTEGER);
				ps.setString(6, (String)data.get("hobby"));
				ps.setString(7, (String)data.get("favourite_movie"));
			}

			ps.executeUpdate();

			// Visit a place if listed
			if (!((String)data.get("place_id")).isEmpty()) {
				query = "INSERT INTO VISITS (PLACE_ID, PERSON_ID)VALUES (?, ?)";
				ps = connection.prepareStatement(query);
				ps.setInt(1, Integer.parseInt((String)data.get("place_id")));
				ps.setInt(2, Integer.parseInt((String)data.get("person_id")));
				ps.executeUpdate();
			}
		} catch (SQLException e) {
			lastError = "error ".concat(e.toString());
			return ModifyResult.Error;
		}
		return type;
	}


	/**
	 * Method for Exercise #4
	 *
	 * @return True on success, false on fail
	 */
	public boolean commit() {
		try {
			connection.commit();
		} catch (SQLException e) {
			lastError = "error ".concat(e.toString());
			return false;
		}
		return false;
	}

	/**
	 * Method for Exercise #4
	 */
	public void rollback(){
		try {
			connection.rollback();
		} catch (SQLException e) {
			lastError = "error ".concat(e.toString());
		}
	}

	/**
	 * Method for Exercise #5
	 *
	 * @return Result of the query
	 */
	public ResultSet getStatistics() {
		String query = "SELECT DISTINCT PERSONS.PERSON_ID, PERSONS.NAME, PLACES.NAME, PLACES.PLACE_TYPE " +
				"FROM PERSONS, PLACES, VISITS " +
				"WHERE " +
				"PERSONS.PERSON_ID = VISITS.PERSON_ID AND " +
				"PLACES.PLACE_ID = VISITS.PLACE_ID AND " +
				"LOWER(PLACES.PLACE_TYPE) LIKE '%diszkó%' AND " +
				"VISITS.LAST_VISIT - VISITS.FIRST_VISIT > (4*365 + 365/2 + 1) " +
				"ORDER BY PERSONS.NAME";
		try {
			Statement s = connection.createStatement();
			return s.executeQuery(query);
		} catch (SQLException e) {
			lastError = "error ".concat(e.toString());
			return null;
		}
	}

}
