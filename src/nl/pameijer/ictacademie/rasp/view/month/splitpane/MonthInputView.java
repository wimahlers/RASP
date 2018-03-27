package nl.pameijer.ictacademie.rasp.view.month.splitpane;

import java.time.LocalDate;
import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import nl.pameijer.ictacademie.rasp.model.Model;
import nl.pameijer.ictacademie.rasp.model.Student;

public class MonthInputView {

	private final VBox vBoxStudents;
	private ScrollPane sStudents;
	private DateModel dateModel;
	private ColumnHeader columnHeader;
	private ScrollPane sDaysTextFields;
	private VBox vBoxDays;
	private VBox root;
	private SplitPane splitPane;
	private GridPane gridStudents;
	private GridPane gridDays;
	private VBox vBoxCombo;
	private SplitPane splitPaneDays;
	private ScrollPane sPaneHeader;
	private ScrollPane sPaneDaysTextFields;

	public MonthInputView(DateModel dateModel) {
		this.dateModel = dateModel;
		// scrollpane
		sStudents = new ScrollPane();
		sDaysTextFields = new ScrollPane();
		sPaneHeader = new ScrollPane();
		sPaneDaysTextFields = new ScrollPane();
		
		// splitpanes
		splitPane = new SplitPane();
		splitPane.setDividerPositions(0.15f);
		//splitpane for horizontal divider
		//TODO implement splitpane in splitpane
		splitPaneDays = new SplitPane();
		splitPaneDays.setOrientation(Orientation.HORIZONTAL);
		// Box to hold views
		root = new VBox();
		vBoxStudents = new VBox();
		vBoxDays = new VBox();
		sStudents.setContent(vBoxStudents);
		sStudents.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		sStudents.setMinWidth(290);
		sDaysTextFields.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		sDaysTextFields.setContent(vBoxDays);
		sStudents.vvalueProperty().bindBidirectional(sDaysTextFields.vvalueProperty());
		columnHeader = new ColumnHeader(dateModel);
		setcomboBoxes();
		setGridPanes();

		root.getChildren().addAll(vBoxCombo, splitPane);
	}

	public void setcomboBoxes() {
		// comboboxes
		vBoxCombo = new VBox();
		vBoxCombo.getChildren().addAll(getMonthComboBox(), getYearComboBox());
	}
	public void setHeaders(){
		
	}

	public void setGridPanes() {
		// grid for students
		gridStudents = new GridPane();
		columnHeader.createStudentColumnHeader(gridStudents);
		vBoxStudents.getChildren().add(gridStudents);
		// grid for days
		gridDays = new GridPane();

		columnHeader.createColumnHeaderDays(gridDays);
		vBoxDays.getChildren().add(gridDays);
		splitPane.getItems().clear();
		splitPane.setDividerPositions(0.15f);
		// splitPane.getItems().addAll(gridStudents , gridDays);
		splitPane.getItems().addAll(sStudents, sDaysTextFields);
	}

	public void clearRefreshHeader() {

		gridStudents.getChildren().clear();
		gridDays.getChildren().clear();
		setGridPanes();

	}

	/**
	 *
	 * @return ComboBox months
	 */
	public ComboBox<String> getMonthComboBox() {
		ObservableList<String> months = FXCollections.observableArrayList("januari", "februari", "maart", "april",
				"mei", "juni", "juli", "augustus", "september", "oktober", "november", "december");

		ComboBox<String> monthBox = new ComboBox<String>(months);
		monthBox.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);
		monthBox.setOnAction(value -> {
			int month = monthBox.getSelectionModel().getSelectedIndex();
			dateModel.setMonth(month + 1);
			System.out.println("monthbox changed: " + (month + 1));
		});

		return monthBox;

	}

	/**
	 *
	 * @return ComboBox years
	 */
	public ComboBox<Integer> getYearComboBox() {
		ObservableList<Integer> years = FXCollections.observableArrayList();
		ComboBox<Integer> yearBox = new ComboBox<Integer>();
		for (int i = 0; i < 25; i++) {
			years.add(2017 + i);
		}
		yearBox.setItems(years);
		yearBox.setValue(dateModel.getYear());
		yearBox.setOnAction(value -> {
			int year = yearBox.getSelectionModel().getSelectedItem();
			dateModel.setYear(year);
			System.out.println("yearbox changed: " + year);
		});

		return yearBox;

	}

	/**
	 * Fills the grid with id, first name and last name labels
	 *
	 * @param students
	 * @param grid
	 */
	public void fillStudents(ObservableList<Student> students, GridPane grid) {
		for (int i = 0; i < students.size(); i++) {
			Label idLabel = new Label();
			idLabel.setPrefHeight(30);
			idLabel.getStyleClass().add("userlabel");
			idLabel.setText("" + (i + 1));
			grid.add(idLabel, 0, i + 1);
			columnHeader.createLabels(idLabel, grid);
			// first name
			Label firstName = new Label();
			firstName.setText(students.get(i).getFName());
			firstName.getStyleClass().add("userlabel");
			grid.add(firstName, 1, i + 1);
			columnHeader.createLabels(firstName, grid);
			// last name
			Label lastName = new Label();
			lastName.setText(students.get(i).getLName());
			lastName.getStyleClass().add("userlabel");
			grid.add(lastName, 2, i + 1);
			columnHeader.createLabels(lastName, grid);

		}
	}// end method fillStudents

	/**
	 *
	 * @param students
	 * @param grid
	 */
	public void fillDayParts(ObservableList<Student> students, GridPane grid) {
		String[] dayNames = dateModel.dayNameList();
		for (int row = 0; row < students.size(); row++) {
			for (int col = 0; col < dateModel.getLengthOfMonth() * 2; col++) {
				// weekend
				if (dayNames[col/ 2].equals("za") || dayNames[col / 2].equals("zo")) {
					Pane pane = new Pane();
					pane.setPrefWidth(10);
					pane.getStyleClass().add("weekend");
					grid.add(pane, col, row + 1, 1, students.size());
					// grid.add(pane, i + 3, row);
				} else {
					TextField txt = new TextField();
					txt.setId("" + (row + 1));

					txt.setPrefSize(30, 30);
					txt.getStyleClass().add("textfield");
					txt.setStyle("-fx-border-color: black black black black");


					txt.textProperty().addListener(new ChangeListener<String>() {

						@Override
						public void changed(ObservableValue<? extends String> observable, String oldValue,
								String newValue) {
							if (!(newValue.equals("x") || newValue.equals("z") || newValue.equals("v")
									|| newValue.equals("a"))) {
								txt.setText("");
							}

						}
					});

					grid.add(txt, col, row + 1);
				}

			}
		}
	}// end method fillDayParts

	public GridPane getGridStudents() {
		return gridStudents;
	}

	public GridPane getGridDays() {
		return gridDays;
	}

	public Parent asParent() {

		return root;
	}

}