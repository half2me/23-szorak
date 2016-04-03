package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.sql.*;

public class Controller {
	//model instance, controller communicate just with the model
	//Don't use javaFX imports classes, etc.
	private Model model;

	public Controller(){
		model = new Model();
	}

	/**
	 * Connect to DB with model
	 * @param userName Your DB username
	 * @param password Your DB password
	 * @param log Log container
	 * @return true if connect success else false
	 */
	public boolean connect(String userName, String password, List<String> log){
		if (model.connect(userName, password)) {
			// Test the connection
			String results = model.testConnection();
			if (results != null) {
				log.add("Connection seems to be working.");
				log.add("Connected to: '" + model.getDatabaseUrl() + "'");
				log.add(String.format("DBMS: %s, version: %s", model.getDatabaseProductName(),
						model.getDatabaseProductVersion()));
				log.add(results);
				return true;
			}
		}
		//always log
		log.add(model.getLastError());
		return false;
	}

	/**
	 * Task 1: Search with keyword
	 * USE: model.search
	 * Don't forget close the statement!
	 * @param keyword the search keyword
	 * @param log Log container
	 * @return every row in a String[],and the whole table in List<String[]>
	 */
	public List<String[]> search(String keyword, List<String> log){
		List<String[]> result = new ArrayList<>();
		//TODO Task 1
		ResultSet r = model.search(keyword);
		log.add(model.getLastError());

		if (r != null) {
			try {
				while (r.next()) {
					String [] row = {r.getString(1), r.getString(2), r.getString(3)};
                    result.add(row);
                }
			} catch (SQLException e) {
				log.add(e.getMessage());
			}
		} else {
			log.add(model.getLastError());
		}
		return result;
	}

	/**
	 * Task 2 and 3: Modify data (task 2) and (before) verify(task 3) it, and disable autocommit (task 4.1)
	 * USE: model.modifyData and Model.ModifyResult
	 * @param data Modify data
	 * @param AutoCommit autocommit parameter
	 * @param log Log container
	 * @return true if verify ok else false
	 */
	public boolean modifyData(Map data,boolean AutoCommit, List<String> log){
		// Validation


		Model.ModifyResult result = model.modifyData(data, AutoCommit);

		if (result == Model.ModifyResult.Error) {
			log.add(model.getLastError());
			return false;
		}

		if (result == Model.ModifyResult.InsertOccured) {
			log.add("insert occured");
		} else {
			log.add("update occured");
		}

		return true;

	}

	/**
	 * Task 5: get statistics
	 * USE: model.getStatistics
	 * Don't forget close the statement!
	 * @param log Log container
	 * @return every row in a String[],and the whole table in List<String[]>
	 */
	public List<String[]> getStatistics(List<String> log){
		List<String[]> result = new ArrayList<>();
		//TODO task 5
		return result;
	}

	/**
	 * Commit all uncommitted changes
	 * USE: model.commit
	 * @param log Log container
	 * @return true if model.commit true else false
	 */
	public boolean commit(List<String> log){
		if (model.commit()) return true;
		log.add(model.lastError);
		return false;
	}

	/**
	 * Verify all fields value
	 * USE it to modifyData function
	 * USE regular expressions, try..catch
	 * @param data Modify data
	 * @param log Log container
	 * @return true if all fields in Map is correct else false
	 */
	private boolean verifyData(Map data, List<String> log) {
		//TODO task 3
		return true;
	}

}
