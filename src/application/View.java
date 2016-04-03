/**
 * This JavaFX skeleton is provided for the Software Laboratory 5 course. Its structure
 * should provide a general guideline for the students.
 * As suggested by the JavaFX model, we'll have a GUI (view),
 * a controller class (this one) and a model.
 */

package application;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.MapValueFactory;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

// Controller class
public class View {

	public TextField person_id;
	public TextField name;
	public TextField address;
	public TextField phone;
	public TextField income;
	public TextField favouriteMovie;
	public Label editStatusLabel;
	public ComboBox hobby;
	public TextField placeId;
	private Controller controller;



	@FXML
	private ComboBox<String> comboSample;

	//Layouts
	@FXML
	private VBox rootLayout;
	@FXML
	private HBox connectionLayout;

	//Texts
	@FXML
	private TextField usernameField;
	@FXML
	private TextField passwordField;
	@FXML
	private TextField searchTextField;
	@FXML
	private TextField sampleID;
	@FXML
	private TextArea logTextArea;

	//Buttons
	@FXML
	private Button connectButton;
	@FXML
	private Button commitButton;
	@FXML
	private Button editButton;
	@FXML
	private Button statisticsButton;
	@FXML
	private Button searchButton;


	// Labels
	@FXML
	private Label connectionStateLabel;

	// Tabs
	@FXML
	private Tab editTab;
	@FXML
	private Tab statisticsTab;
	@FXML
	private Tab logTab;
	@FXML
	private Tab searchTab;


	// Tables
	@FXML
	private TableView searchTable;
	@FXML
	private TableView statisticsTable;


	// Titles and map keys of table columns search
	String searchColumnTitles[] = new String[] { "Name", "Address", "Phone" };
	String searchColumnKeys[] = new String[] { "name", "address", "phone", };

	// Titles and map keys of table columns statistics
	String statisticsColumnTitles[] = new String[] { "Person ID", "Person Name", "Place Name", "Place Type"};
	String statisticsColumnKeys[] = new String[] { "person_id", "person_name", "place_name", "place_type" };

	/**
	 * View constructor
	 */
	public View() {
		controller = new Controller();
	}

	/**
	 * View initialization, it will be called after view was prepared
	 */
	@FXML
	public void initialize() {

		// Clear username and password textfields and display status
		// 'disconnected'
		usernameField.setText("a7twos");
		passwordField.setText("dba");
		connectionStateLabel.setText("Connection: disconnected");
		connectionStateLabel.setTextFill(Color.web("#ee0000"));

		// Create table (search table) columns
		for (int i = 0; i < searchColumnTitles.length; i++) {
			// Create table column
			TableColumn<Map, String> column = new TableColumn<>(searchColumnTitles[i]);
			// Set map factory
			column.setCellValueFactory(new MapValueFactory(searchColumnKeys[i]));
			// Set width of table column
			column.prefWidthProperty().bind(searchTable.widthProperty().divide(searchColumnTitles.length));
			// Add column to the table
			searchTable.getColumns().add(column);
		}

		// Create table (statistics table) columns
		for (int i = 0; i < statisticsColumnTitles.length; i++) {
			// Create table column
			TableColumn<Map, String> column = new TableColumn<>(statisticsColumnTitles[i]);
			// Set map factory
			column.setCellValueFactory(new MapValueFactory(statisticsColumnKeys[i]));
			// Set width of table column
			column.prefWidthProperty().bind(statisticsTable.widthProperty().divide(statisticsColumnTitles.length));
			// Add column to the table
			statisticsTable.getColumns().add(column);
		}

	}

	/**
	 * Initialize controller with data from AppMain (now only sets stage)
	 *
	 * @param stage
	 *            The top level JavaFX container
	 */
	public void initData(Stage stage) {

		// Set 'onClose' event handler (of the container)
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent winEvent) {
				//TODO 4.2
			}
		});
	}

	/**
	 * This is called whenever the connect button is pressed
	 *
	 * @param event
	 *            Contains details about the JavaFX event
	 */
	@FXML
	private void connectEventHandler(ActionEvent event) {
		//Log container
		List<String> log = new ArrayList<>();

		// Controller connect method will do everything for us, just call it
		if (controller.connect(usernameField.getText(), passwordField.getText(), log))
		{
			connectionStateLabel.setText("Connection created");
			connectionStateLabel.setTextFill(Color.web("#009900"));
		}

		//Write log to gui
		for (String string : log) logMsg(string);
	}

	/**
	 * This is called whenever the search button is pressed
	 * Task 1
	 * USE controller search method
	 * @param event
	 *            Contains details about the JavaFX event
	 */
	@FXML
	private void searchEventHandler(ActionEvent event) {
		//always use log
		List<String> log = new ArrayList<>();

		// Get a reference to the row list of search table
		ObservableList<Map> allRows = searchTable.getItems();

		// Delete all the rows
		allRows.clear();

		// Search or list data:
		List<String[]> result = controller.search(searchTextField.getText(), log);

		// Loop through results
		for (String[] row : result) {
			// Create a map object from string array
			Map<String, String> dataRow = new HashMap<>();
			for (int i = 0; i < searchTable.getColumns().size(); i++) {
				dataRow.put(searchColumnKeys[i], row[i]);
			}
			// Add the row to the table
			allRows.add(dataRow);
		}

		//and write it to gui
		for (String string : log) logMsg(string);
	}


	/**
	 * This is called whenever the edit button is pressed
	 * Task 2,3,4
	 * USE controller modify method (verify data in controller !!!)
	 * @param event
	 *            Contains details about the JavaFX event
	 */
	@FXML
	private void editEventHandler(ActionEvent event) {
		List<String> log = new ArrayList<>();

		Map<String, String> data = new HashMap<>();
		data.put("person_id",		person_id.getText());
		data.put("name", 			name.getText());
		data.put("address", 		address.getText());
		data.put("phone", 			phone.getText());
		data.put("income", 			income.getText());
		data.put("hobby", 			hobby.getSelectionModel().getSelectedItem().toString());
		data.put("favourite_movie", favouriteMovie.getText());
		data.put("place_id", 		placeId.getText());

		boolean result = controller.modifyData(data, placeId.getText().isEmpty(), log);

		if (result) {
			editStatusLabel.setText("Success!");
			editStatusLabel.setTextFill(Color.web("#009900"));
		} else {
			editStatusLabel.setText("Failed!");
			editStatusLabel.setTextFill(Color.web("#990000"));
		}
		for (String string : log) logMsg(string);
	}


	/**
	 * This is called whenever the commit button is pressed
	 * Task 4
	 * USE controller commit method
	 * Don't forget SET the commit button disable state
	 * LOG:
	 * 	commit ok: if commit return true
	 *  commit failed: if commit return false
	 * @param event
	 *            Contains details about the JavaFX event
	 */
	@FXML
	private void commitEventHandler(ActionEvent event) {
		List<String> log = new ArrayList<>();
		if (controller.commit(log)) {
			editStatusLabel.setText("Committed!");
			editStatusLabel.setTextFill(Color.web("#009900"));
		} else {
			editStatusLabel.setText("Rolled back!");
			editStatusLabel.setTextFill(Color.web("#990000"));
		}
		for (String string : log) logMsg(string);
	}



	/**
	 * This is called whenever the statistics button is pressed
	 * Task 5
	 * USE controller getStatistics method
	 * @param event
	 *            Contains details about the JavaFX event
	 */
	@FXML
	private void statisticsEventHandler(ActionEvent event) {
		List<String> log = new ArrayList<>();

		// Get a reference to the row list of search table
		ObservableList<Map> allRows = statisticsTable.getItems();

		// Delete all the rows
		allRows.clear();

		// Search or list data:
		List<String[]> result = controller.getStatistics(log);

		// Loop through results
		for (String[] row : result) {
			// Create a map object from string array
			Map<String, String> dataRow = new HashMap<>();
			for (int i = 0; i < statisticsTable.getColumns().size(); i++) {
				dataRow.put(statisticsColumnKeys[i], row[i]);
			}
			// Add the row to the table
			allRows.add(dataRow);
		}

		//and write it to gui
		for (String string : log) logMsg(string);
	}

	/**
	 * Appends the message (with a line break added) to the log
	 *
	 * @param message
	 *            The message to be logged
	 */
	protected void logMsg(String message) {

		logTextArea.appendText(message + "\n");

	}

}
