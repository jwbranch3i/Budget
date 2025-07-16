package com.budget.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.budget.Util;
import com.budget.dataModal.DB;
import com.budget.dataModal.LineItem;
import com.budget.dataModal.LineItemCSV;
import com.budget.dataModal.ReadData;
import com.budget.dataModal.UIData;
import com.budget.dataModal.WriteData;
import com.opencsv.CSVReader;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PrimaryController {
        /********************* */
        /* Income Table */
        /********************* */
        @FXML
        private TreeTableView<LineItem> tableIncome;

        @FXML
        private TreeTableColumn<LineItem, Double> tableIncome_Actual;

        @FXML
        private TreeTableColumn<LineItem, Double> tableIncome_Budget;

        @FXML
        private TreeTableColumn<LineItem, String> tableIncome_Category;

        @FXML
        private TreeTableColumn<LineItem, Double> tableIncome_Diff;

        @FXML
        private TreeTableColumn<LineItem, Double> tableIncome_Balance;

        /*************************************************************************/

        @FXML
        private TableView<LineItem> tableIncomeTotal;

        @FXML
        private TableColumn<LineItem, Double> tableIncomeTotal_Actual;

        @FXML
        private TableColumn<LineItem, Double> tableIncomeTotal_Budget;

        @FXML
        private TableColumn<LineItem, String> tableIncomeTotal_Category;

        @FXML
        private TableColumn<LineItem, Double> tableIncomeTotal_Diff;

        /********************* */
        /* Mandatory Table */
        /********************* */
        @FXML
        private TreeTableView<LineItem> tableMandatory;

        @FXML
        private TreeTableColumn<LineItem, Double> tableMandatory_Actual;

        @FXML
        private TreeTableColumn<LineItem, Double> tableMandatory_Budget;

        @FXML
        private TreeTableColumn<LineItem, String> tableMandatory_Category;

        @FXML
        private TreeTableColumn<LineItem, Double> tableMandatory_Diff;

        @FXML
        private TreeTableColumn<LineItem, Double> tableMandatory_Balance;

        /*************************************************************************/

        @FXML
        private TableView<LineItem> tableMandatoryTotal;

        @FXML
        private TableColumn<LineItem, Double> tableMandatoryTotal_Actual;

        @FXML
        private TableColumn<LineItem, Double> tableMandatoryTotal_Budget;

        @FXML
        private TableColumn<LineItem, String> tableMandatoryTotal_Category;

        @FXML
        private TableColumn<LineItem, Double> tableMandatoryTotal_Diff;

        @FXML
        private TableColumn<LineItem, Double> tableMandatoryTotal_Balance;

        /********************* */
        /* Discretionary Table */
        /********************* */
        @FXML
        private TreeTableView<LineItem> tableDiscretionary;

        @FXML
        private TreeTableColumn<LineItem, Double> tableDiscretionary_Actual;

        @FXML
        private TreeTableColumn<LineItem, Double> tableDiscretionary_Budget;

        @FXML
        private TreeTableColumn<LineItem, String> tableDiscretionary_Category;

        @FXML
        private TreeTableColumn<LineItem, Double> tableDiscretionary_Diff;

        @FXML
        private TreeTableColumn<LineItem, Double> tableDiscretionary_Balance;

        /************************************************************************/

        @FXML
        private TableView<LineItem> tableDiscretionaryTotal;

        @FXML
        private TableColumn<LineItem, Double> tableDiscretionaryTotal_Actual;

        @FXML
        private TableColumn<LineItem, Double> tableDiscretionaryTotal_Budget;

        @FXML
        private TableColumn<LineItem, String> tableDiscretionaryTotal_Category;

        @FXML
        private TableColumn<LineItem, Double> tableDiscretionaryTotal_Diff;

        @FXML
        private TableColumn<LineItem, Double> tableDiscretionaryTotal_Balance;

        /*************************************************************************/

        /********************* */
        /* Totals Table */
        /********************* */
        @FXML
        private TableView<LineItem> tableTotal;

        @FXML
        private TableColumn<LineItem, String> tableTotal_Category;

        @FXML
        private TableColumn<LineItem, Double> tableTotal_Actual;

        @FXML
        private TableColumn<LineItem, Double> tableTotal_Budget;

        @FXML
        private TableColumn<LineItem, Double> tableTotal_Diff;

        /*************************************************************************/

        @FXML
        private VBox categoryBox;
        @FXML
        private AnchorPane myAnchorPane;
        @FXML
        private ProgressIndicator progressIndicator;
        @FXML
        private ComboBox<String> yearBox;
        @FXML
        private ComboBox<String> monthBox;
        @FXML
        private CheckBox chkBox;
        @FXML
        private Button btn_Update;
        @FXML
        private Button btn_EditCat;
        @FXML
        private Button btn_UpdateBudget;
        @FXML
        private Button btn_UpdateBalance;
        @FXML
        private Label mainDateLabel;

        @FXML
        void button_EditCat(ActionEvent event) {
                try {
                        // Load the FXML file for the new window
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/budget/secondary.fxml"));
                        Parent root = fxmlLoader.load();

                        // Create a new Stage
                        Stage stage = new Stage();
                        stage.setTitle("Edit Category");

                        // Set the scene with the loaded FXML
                        stage.setScene(new Scene(root));

                        // Set the stage as a modal window
                        stage.initModality(Modality.WINDOW_MODAL);
                        stage.initOwner(((Node) event.getSource()).getScene().getWindow());

                        // Show the modal window
                        stage.showAndWait();

                        LocalDate inDate = LocalDate.of(Integer.parseInt(yearBox.getValue()),
                                        monthBox.getSelectionModel().getSelectedIndex() + 1, 1);

                        tableIncomeTotal.setItems(
                                        FXCollections.observableArrayList(ReadData.getTotals(DB.INCOME, inDate)));
                        tableMandatoryTotal.setItems(
                                        FXCollections.observableArrayList(ReadData.getTotals(DB.MANDATORY, inDate)));
                        tableDiscretionaryTotal.setItems(FXCollections
                                        .observableArrayList(ReadData.getTotals(DB.DISCRETIONARY, inDate)));
                        getTableRows(inDate);
                        UIData.updateTableTotal(tables);
                }
                catch (IOException e) {
                        e.printStackTrace();
                }
        }

        @FXML
        void button_UpdateCat(ActionEvent event) {
                LocalDate inDate = LocalDate.of(Integer.parseInt(yearBox.getValue()),
                                monthBox.getSelectionModel().getSelectedIndex() + 1, 1);

                if (!chkBox.isSelected()) {
                        readFromDatabase(inDate);
                }
                else {
                        // routine to open dialog box to select file
                        String savedFilePath = "C:\\";
                        try {
                                // Retrieve the file path from disk
                                FileReader fileReader = new FileReader("filePath.txt");
                                BufferedReader bufferedReader = new BufferedReader(fileReader);
                                savedFilePath = bufferedReader.readLine();
                                bufferedReader.close();
                        }
                        catch (IOException e) {
                                System.out.println("Error retrieving file path: " + e.getMessage());
                        }

                        File csvFilePath = new File(savedFilePath);
                        if (!csvFilePath.exists() || !csvFilePath.canRead()) {
                                csvFilePath = new File("C:\\");
                        }

                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setInitialDirectory(new File(csvFilePath.getPath()));
                        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

                        File selectedFile = fileChooser.showOpenDialog(null);
                        if (selectedFile != null) {
                                // Save the directory from selectedFile to
                                // filePath.txt
                                String filePath = selectedFile.getParent();

                                try {
                                        FileWriter fileWriter = new FileWriter("filePath.txt");
                                        fileWriter.write(filePath);
                                        fileWriter.close();
                                }
                                catch (IOException e) {
                                        System.out.println("Error saving file path: " + e.getMessage());
                                }

                                readActual(selectedFile, inDate);

                        }
                        readFromDatabase(inDate);
                        chkBox.setSelected(false);
                        btn_Update.setDisable(true);

                }
                // Display indate as text
                mainDateLabel.setText(inDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")));

        }

        @FXML
        void button_UpdateBudget(ActionEvent event) {
                Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                                WriteData.getLastBudget(getWorkingDate());
                                return null;
                        }

                        @Override
                        protected void succeeded() {
                                super.succeeded();
                                // Refresh the table view to reflect the changes
                                updateTables();
                        }

                        @Override
                        protected void failed() {
                                super.failed();
                                // Handle any errors that occurred during the
                                // task
                                Throwable exception = getException();
                                exception.printStackTrace();
                        }
                };
                new Thread(task).start();
        }

        @FXML
        void button_UpdateBalance(ActionEvent event) {
                LineItem firstItem = tableMandatory.getRoot().getChildren().get(0).getValue();
                // LineItem firstItem = tableMandatory.getItems().get(0);
                LocalDate inDate = firstItem.getDate();

                Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                                WriteData.updateBalance(getWorkingDate());
                                return null;
                        }

                        @Override
                        protected void succeeded() {
                                super.succeeded();
                                // Refresh the table view to reflect the changes
                                // - wrap in Platform.runLater
                                Platform.runLater(() -> {
                                        tableIncomeTotal.setItems(FXCollections
                                                        .observableArrayList(ReadData.getTotals(DB.INCOME, inDate)));
                                        tableMandatoryTotal.setItems(FXCollections
                                                        .observableArrayList(ReadData.getTotals(DB.MANDATORY, inDate)));
                                        tableDiscretionaryTotal.setItems(FXCollections.observableArrayList(
                                                        ReadData.getTotals(DB.DISCRETIONARY, inDate)));
                                        getTableRows(inDate);
                                        UIData.updateTableTotal(tables);
                                        tableIncome.refresh();
                                });
                        }

                        @Override
                        protected void failed() {
                                super.failed();
                                // Handle any errors that occurred during the
                                // task
                                Throwable exception = getException();
                                exception.printStackTrace();
                        }
                };
                new Thread(task).start();
        }

        /**
         * Reads the headings when the "readHeadingsButton" is clicked. This
         * method shows a progress indicator and sets its progress to
         * indeterminate. It then hides the progress indicator after the
         * headings are read.
         *
         * @param event the action event triggered by clicking the
         *              "readHeadingsButton"
         */
        @FXML
        void readHeadingsButton(ActionEvent event) {
                progressIndicator.setVisible(true);
                progressIndicator.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

                // rejectedRecords.setText(String.valueOf(temp));
                // addedRecords.setText(String.valueOf(WriteData.getAddedRecordCount()));

                progressIndicator.setVisible(false);

        }

        @FXML
        private void switchToSecondary() throws IOException {
                // App.setRoot("secondary");
        }

        // Array of tables for UIDatat total table update
        ArrayList<TableView<LineItem>> tables = new ArrayList<TableView<LineItem>>();

        public ArrayList<TableView<LineItem>> getTables() {
                return tables;
        }

        public void initialize() {

                @SuppressWarnings("unused")
                PrimaryControllerExtend controllerExtend = new PrimaryControllerExtend(tableTotal, tableTotal_Category,
                                tableTotal_Actual, tableTotal_Budget, tableTotal_Diff);

                // Apply the style class to the table
                tableIncomeTotal.getStyleClass().add("table-view-total");
                tableMandatoryTotal.getStyleClass().add("table-view-total");
                tableDiscretionaryTotal.getStyleClass().add("table-view-total");

                // set btn_Update to be disabled and uncheck chkBox
                chkBox.setSelected(false);
                btn_Update.setDisable(true);

                // ***************************************/
                // Set up table refrence to update totals table
                // ***************************************/
                tables.add(tableIncomeTotal);
                tables.add(tableMandatoryTotal);
                tables.add(tableDiscretionaryTotal);
                tables.add(tableTotal);

                // TODO: Update button is not disabled on startup

                myAnchorPane.getStyleClass().add("catBox");
                tableIncomeTotal.getStyleClass().add("total-table");

                // * ***************************************/
                // Set up choice boxes
                // ***************************************/
                LocalDate inDate = LocalDate.now();
                ObservableList<String> monthChoices = FXCollections.observableArrayList("January", "February", "March",
                                "April", "May", "June", "July", "August", "September", "October", "November",
                                "December");
                monthBox.setItems(monthChoices);
                monthBox.getSelectionModel().select(inDate.getMonthValue() - 1);
                monthBox.setOnAction(e -> {
                        btn_Update.setDisable(false);
                });

                // Create a task to run getYears in another thread
                Task<java.util.List<String>> task = new Task<java.util.List<String>>() {
                        @Override
                        protected List<String> call() throws Exception {
                                return ReadData.getYears();
                        }
                };
                new Thread(task).start();

                task.setOnSucceeded(e -> {
                        List<String> years = task.getValue();

                        /* add current year to array if not in array */
                        String currentYear = String.valueOf(LocalDate.now().getYear());
                        String currentYearPlusOne = String.valueOf(LocalDate.now().getYear() + 1);
                        String currentYearMinusOne = String.valueOf(LocalDate.now().getYear() - 1);
                        if (!years.contains(currentYear)) {
                                years.add(currentYear);
                        }
                        if (!years.contains(currentYearPlusOne)) {
                                years.add(currentYearPlusOne);
                        }
                        if (!years.contains(currentYearMinusOne)) {
                                years.add(currentYearMinusOne);
                        }
                        years.sort(String::compareTo);

                        ObservableList<String> yearChoices = FXCollections.observableArrayList(years);
                        yearBox.setItems(yearChoices);
                        yearBox.setEditable(true);
                        yearBox.getSelectionModel().select(currentYear);
                });
                yearBox.setOnAction(e -> {
                        btn_Update.setDisable(false);
                });

                // ***************************************/
                // Remove headers from totals tables
                // ***************************************/
                tableIncomeTotal.skinProperty().addListener((a, b, newSkin) -> {
                        Pane header = (Pane) tableIncomeTotal.lookup("TableHeaderRow");
                        header.setMinHeight(0);
                        header.setPrefHeight(0);
                        header.setMaxHeight(0);
                        header.setVisible(false);
                });

                tableMandatoryTotal.skinProperty().addListener((a, b, newSkin) -> {
                        Pane header = (Pane) tableMandatoryTotal.lookup("TableHeaderRow");
                        header.setMinHeight(0);
                        header.setPrefHeight(0);
                        header.setMaxHeight(0);
                        header.setVisible(false);
                });

                tableDiscretionaryTotal.skinProperty().addListener((a, b, newSkin) -> {
                        Pane header = (Pane) tableDiscretionaryTotal.lookup("TableHeaderRow");
                        header.setMinHeight(0);
                        header.setPrefHeight(0);
                        header.setMaxHeight(0);
                        header.setVisible(false);
                });

                // ***************************************/
                // Add row to totals tables
                // ***************************************/

                // add row to tableIncomeTotal
                LineItem incomeTotal = new LineItem();
                incomeTotal.setCategory("Total");
                incomeTotal.setActual(1.0);
                incomeTotal.setBudget(1.0);
                tableIncomeTotal.getItems().add(incomeTotal);

                // add row to tableMandatoryTotal
                LineItem manditoryTotal = new LineItem();
                manditoryTotal.setCategory("Total");
                manditoryTotal.setActual(0.0);
                manditoryTotal.setBudget(0.0);
                tableMandatoryTotal.getItems().add(manditoryTotal);

                // add row to tableDiscretionaryTotal
                LineItem discretionaryTotal = new LineItem();
                discretionaryTotal.setCategory("Total");
                discretionaryTotal.setActual(0.0);
                discretionaryTotal.setBudget(0.0);
                tableDiscretionaryTotal.getItems().add(discretionaryTotal);

                // ****************************************************/
                // Add listener to tables to clear other tableview selection */
                // when tableview is selected */
                // ****************************************************/
                tableIncome.getSelectionModel().selectedItemProperty()
                                .addListener((obs, oldSelection, newSelection) -> {
                                        if (newSelection != null) {
                                                tableMandatory.getSelectionModel().clearSelection();
                                                tableDiscretionary.getSelectionModel().clearSelection();
                                        }
                                });

                tableMandatory.getSelectionModel().selectedItemProperty()
                                .addListener((obs, oldSelection, newSelection) -> {
                                        if (newSelection != null) {
                                                tableIncome.getSelectionModel().clearSelection();
                                                tableDiscretionary.getSelectionModel().clearSelection();
                                        }
                                });

                tableDiscretionary.getSelectionModel().selectedItemProperty()
                                .addListener((obs, oldSelection, newSelection) -> {
                                        if (newSelection != null) {
                                                tableIncome.getSelectionModel().clearSelection();
                                                tableMandatory.getSelectionModel().clearSelection();
                                        }
                                });

                // ***************************************/
                // Set up table columns */
                // tableIncome columns */
                // ***************************************/

                tableIncome_Category.setCellValueFactory(cellData -> {
                        LineItem item = cellData.getValue().getValue();
                        if (item != null) {
                                return new javafx.beans.property.SimpleStringProperty(item.getCategory());
                        }
                        else {
                                return new javafx.beans.property.SimpleStringProperty("");
                        }
                });
                tableIncome_Category
                                .setCellFactory(javafx.scene.control.cell.TextFieldTreeTableCell.forTreeTableColumn());

                // --
                tableIncome_Actual.setCellValueFactory(cellData -> {
                        LineItem item = cellData.getValue().getValue();
                        if (item != null) {
                                return new javafx.beans.property.SimpleObjectProperty<>(item.getActual());
                        }
                        else {
                                return new javafx.beans.property.SimpleObjectProperty<>(0.0);
                        }
                });
                tableIncome_Actual.setCellFactory(javafx.scene.control.cell.TextFieldTreeTableCell
                                .forTreeTableColumn(Util.getCurrencyConverter()));

                // --

                tableIncome_Budget.setCellValueFactory(cellData -> {
                        LineItem item = cellData.getValue().getValue();
                        if (item != null) {
                                return new javafx.beans.property.SimpleObjectProperty<>(item.getBudget());
                        }
                        else {
                                return new javafx.beans.property.SimpleObjectProperty<>(0.0);
                        }
                });
                tableIncome_Budget.setCellFactory(javafx.scene.control.cell.TextFieldTreeTableCell
                                .forTreeTableColumn(Util.getCurrencyConverter()));
                tableIncome_Budget.setOnEditCommit(e -> mandatoryTableBudget_OnEditCommit(e));

                // --
                tableIncome_Diff.setCellValueFactory(cellData -> {
                        LineItem item = cellData.getValue().getValue();
                        if (item != null) {
                                return new javafx.beans.property.SimpleObjectProperty<>(item.getBudget());
                        }
                        else {
                                return new javafx.beans.property.SimpleObjectProperty<>(0.0);
                        }
                });
                tableIncome_Diff.setCellFactory(javafx.scene.control.cell.TextFieldTreeTableCell
                                .forTreeTableColumn(Util.getCurrencyConverter()));

                /***********************************************************/
                tableIncomeTotal_Category.setCellValueFactory(new PropertyValueFactory<LineItem, String>("Category"));
                tableIncomeTotal_Category.setCellFactory(TextFieldTableCell.forTableColumn());

                tableIncomeTotal_Actual.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("actual"));
                tableIncomeTotal_Actual.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                tableIncomeTotal_Budget.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("budget"));
                tableIncomeTotal_Budget.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                tableIncomeTotal_Diff.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("diff"));
                tableIncomeTotal_Diff.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                /****************************************/
                // tableMandatory columns */
                // **************************************/
                tableMandatory_Category.setCellValueFactory(cellData -> {
                        LineItem item = cellData.getValue().getValue();
                        if (item != null) {
                                return new javafx.beans.property.SimpleStringProperty(item.getCategory());
                        }
                        else {
                                return new javafx.beans.property.SimpleStringProperty("");
                        }
                });
                tableMandatory_Category
                                .setCellFactory(javafx.scene.control.cell.TextFieldTreeTableCell.forTreeTableColumn());

                // --
                tableMandatory_Actual.setCellValueFactory(cellData -> {
                        LineItem item = cellData.getValue().getValue();
                        if (item != null) {
                                return new javafx.beans.property.SimpleObjectProperty<>(item.getActual());
                        }
                        else {
                                return new javafx.beans.property.SimpleObjectProperty<>(0.0);
                        }
                });
                tableMandatory_Actual.setCellFactory(javafx.scene.control.cell.TextFieldTreeTableCell
                                .forTreeTableColumn(Util.getCurrencyConverter()));

                // --

                tableMandatory_Budget.setCellValueFactory(cellData -> {
                        LineItem item = cellData.getValue().getValue();
                        if (item != null) {
                                return new javafx.beans.property.SimpleObjectProperty<>(item.getBudget());
                        }
                        else {
                                return new javafx.beans.property.SimpleObjectProperty<>(0.0);
                        }
                });
                tableMandatory_Budget.setCellFactory(javafx.scene.control.cell.TextFieldTreeTableCell
                                .forTreeTableColumn(Util.getCurrencyConverter()));
                tableMandatory_Budget.setOnEditCommit(e -> mandatoryTableBudget_OnEditCommit(e));

                // --
                tableMandatory_Diff.setCellValueFactory(cellData -> {
                        LineItem item = cellData.getValue().getValue();
                        if (item != null) {
                                return new javafx.beans.property.SimpleObjectProperty<>(item.getBudget());
                        }
                        else {
                                return new javafx.beans.property.SimpleObjectProperty<>(0.0);
                        }
                });
                tableMandatory_Diff.setCellFactory(javafx.scene.control.cell.TextFieldTreeTableCell
                                .forTreeTableColumn(Util.getCurrencyConverter()));

                // --
                tableMandatory_Balance.setCellValueFactory(cellData -> {
                        LineItem item = cellData.getValue().getValue();
                        if (item != null) {
                                return new javafx.beans.property.SimpleObjectProperty<>(item.getBudget());
                        }
                        else {
                                return new javafx.beans.property.SimpleObjectProperty<>(0.0);
                        }
                });
                tableMandatory_Balance.setCellFactory(javafx.scene.control.cell.TextFieldTreeTableCell
                                .forTreeTableColumn(Util.getCurrencyConverter()));

                // **************************************

                tableMandatory.setRowFactory(tv -> {
                        TreeTableRow<LineItem> row = new TreeTableRow<>();
                        ContextMenu contextMenu = new ContextMenu();
                        MenuItem editItem = new MenuItem("Edit");
                        contextMenu.getItems().add(editItem);

                        row.setOnMouseClicked(event -> {
                                if (event.getButton() == MouseButton.SECONDARY && !row.isEmpty()) {
                                        contextMenu.show(row, event.getScreenX(), event.getScreenY());
                                }
                        });

                        editItem.setOnAction(event -> {
                                LineItem item = row.getItem();
                                editLineItem(item); // Call your routine here
                        });

                        return row;
                });

                /***********************************************************/
                tableMandatoryTotal_Category
                                .setCellValueFactory(new PropertyValueFactory<LineItem, String>("Category"));
                tableMandatoryTotal_Category.setCellFactory(TextFieldTableCell.forTableColumn());

                tableMandatoryTotal_Actual.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("actual"));
                tableIncomeTotal_Actual.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                tableMandatoryTotal_Budget.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("budget"));
                tableMandatoryTotal_Budget.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                tableMandatoryTotal_Diff.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("diff"));
                tableMandatoryTotal_Diff.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                tableMandatoryTotal_Balance.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("balance"));
                tableMandatoryTotal_Balance
                                .setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                /***********************************************************/
                tableDiscretionary_Category.setCellValueFactory(cellData -> {
                        LineItem item = cellData.getValue().getValue();
                        if (item != null) {
                                return new javafx.beans.property.SimpleStringProperty(item.getCategory());
                        }
                        else {
                                return new javafx.beans.property.SimpleStringProperty("");
                        }
                });
                tableDiscretionary_Category
                                .setCellFactory(javafx.scene.control.cell.TextFieldTreeTableCell.forTreeTableColumn());

                // --
                tableDiscretionary_Actual.setCellValueFactory(cellData -> {
                        LineItem item = cellData.getValue().getValue();
                        if (item != null) {
                                return new javafx.beans.property.SimpleObjectProperty<>(item.getActual());
                        }
                        else {
                                return new javafx.beans.property.SimpleObjectProperty<>(0.0);
                        }
                });
                tableDiscretionary_Actual.setCellFactory(javafx.scene.control.cell.TextFieldTreeTableCell
                                .forTreeTableColumn(Util.getCurrencyConverter()));

                // --

                tableDiscretionary_Budget.setCellValueFactory(cellData -> {
                        LineItem item = cellData.getValue().getValue();
                        if (item != null) {
                                return new javafx.beans.property.SimpleObjectProperty<>(item.getBudget());
                        }
                        else {
                                return new javafx.beans.property.SimpleObjectProperty<>(0.0);
                        }
                });
                tableDiscretionary_Budget.setCellFactory(javafx.scene.control.cell.TextFieldTreeTableCell
                                .forTreeTableColumn(Util.getCurrencyConverter()));
                tableDiscretionary_Budget.setOnEditCommit(e -> mandatoryTableBudget_OnEditCommit(e));

                // --
                tableDiscretionary_Diff.setCellValueFactory(cellData -> {
                        LineItem item = cellData.getValue().getValue();
                        if (item != null) {
                                return new javafx.beans.property.SimpleObjectProperty<>(item.getBudget());
                        }
                        else {
                                return new javafx.beans.property.SimpleObjectProperty<>(0.0);
                        }
                });
                tableDiscretionary_Diff.setCellFactory(javafx.scene.control.cell.TextFieldTreeTableCell
                                .forTreeTableColumn(Util.getCurrencyConverter()));

                tableDiscretionary.setRowFactory(tv -> {
                        TreeTableRow<LineItem> row = new TreeTableRow<>();
                        ContextMenu contextMenu = new ContextMenu();
                        MenuItem editItem = new MenuItem("Edit");
                        contextMenu.getItems().add(editItem);

                        row.setOnMouseClicked(event -> {
                                if (event.getButton() == MouseButton.SECONDARY && !row.isEmpty()) {
                                        contextMenu.show(row, event.getScreenX(), event.getScreenY());
                                }
                        });

                        editItem.setOnAction(event -> {
                                LineItem item = row.getItem();
                                editLineItem(item); // Call your routine here
                        });

                        return row;
                });

                /***********************************************************/
                tableDiscretionaryTotal_Category
                                .setCellValueFactory(new PropertyValueFactory<LineItem, String>("Category"));
                tableDiscretionaryTotal_Category.setCellFactory(TextFieldTableCell.forTableColumn());

                tableDiscretionaryTotal_Actual
                                .setCellValueFactory(new PropertyValueFactory<LineItem, Double>("actual"));
                tableDiscretionaryTotal_Actual
                                .setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                tableDiscretionaryTotal_Budget
                                .setCellValueFactory(new PropertyValueFactory<LineItem, Double>("budget"));
                tableDiscretionaryTotal_Budget
                                .setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                tableDiscretionaryTotal_Diff.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("diff"));
                tableDiscretionaryTotal_Diff
                                .setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                // Create a task to run getTableRows in another thread
                Task<Void> task2 = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                                // Do data processing on background thread, not
                                // UI updates
                                return null;
                        }

                        @Override
                        protected void succeeded() {
                                super.succeeded();
                                // Do UI updates on FX Application Thread
                                Platform.runLater(() -> {
                                        getTableRows(inDate);
                                        Util.calculateTreeTotals(tableIncome.getRoot());
                                        Util.calculateTreeTotals(tableMandatory.getRoot());
                                        Util.calculateTreeTotals(tableDiscretionary.getRoot());
                                        UIData.updateTableTotal(tables);
                                });
                        }
                };
                new Thread(task2).start();

                // set btn_Update to be disabled and uncheck chkBox
                chkBox.setSelected(false);
                btn_Update.setDisable(true);
                // Display indate as text
                mainDateLabel.setText(inDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")));

        }

        public void readFromDatabase(LocalDate inDate) {
                Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                                // Do data processing on background thread, not
                                // UI updates
                                return null;
                        }

                        @Override
                        protected void succeeded() {
                                super.succeeded();
                                // Do UI updates on FX Application Thread
                                Platform.runLater(() -> {
                                        getTableRows(inDate);
                                        UIData.updateTableTotal(tables);
                                });
                        }
                };
                new Thread(task).start();
        }

        public void getTableRows(LocalDate inDate) {
                // getActuals(inDate);
                tableIncome.setRoot(null);
                tableIncome.setRoot(ReadData.getTableAmountsTree(DB.INCOME, inDate));
                tableIncome.getRoot().setExpanded(true);
                // tableIncome.setShowRoot(false);
                Util.calculateTreeTotals(tableIncome.getRoot());
                tableIncomeTotal.setItems(FXCollections.observableArrayList(ReadData.getTotals(DB.INCOME, inDate)));

                // get mandatory data
                tableMandatory.setRoot(null);
                tableMandatory.setRoot(ReadData.getTableAmountsTree(DB.MANDATORY, inDate));
                tableMandatory.getRoot().setExpanded(true);
                // tableMandatory.setShowRoot(false);
                Util.calculateTreeTotals(tableMandatory.getRoot());
                tableMandatoryTotal
                                .setItems(FXCollections.observableArrayList(ReadData.getTotals(DB.MANDATORY, inDate)));

                // get discretionary data
                tableDiscretionary.setRoot(null);
                tableDiscretionary.setRoot(ReadData.getTableAmountsTree(DB.DISCRETIONARY, inDate));
                tableDiscretionary.getRoot().setExpanded(true);
                // tableDiscretionary.setShowRoot(false);
                Util.calculateTreeTotals(tableDiscretionary.getRoot());
                tableDiscretionaryTotal.setItems(
                                FXCollections.observableArrayList(ReadData.getTotals(DB.DISCRETIONARY, inDate)));

        }

        public static void readActual(File file, LocalDate inDate) {
                LineItemCSV newLineItem = new LineItemCSV();
                LineItemCSV existingCategory = new LineItemCSV();
                LineItemCSV existingActual = new LineItemCSV();

                String[] nextRecord;
                String category = "";
                String parent = "";
                String workingType = "";

                Double amount = 0.0;

                int leadingSpaces = 0;
                int type = 0;
                int newRecordType = 0;

                try {
                        FileReader filereader = new FileReader(file);
                        CSVReader csvReader = new CSVReader(filereader);

                        // we are going to read data line by line
                        while ((nextRecord = csvReader.readNext()) != null) {

                                // if the line is empty, skip it
                                if (nextRecord.length <= 1) {
                                        continue;
                                }

                                leadingSpaces = nextRecord[1].length() - nextRecord[1].trim().length();
                                if (nextRecord[1].trim().equals("INFLOWS")) {
                                        type = DB.INCOME;
                                        continue;
                                }
                                else if (nextRecord[1].trim().equals("OUTFLOWS")) {
                                        type = DB.MANDATORY;
                                        continue;
                                }

                                category = nextRecord[1].trim();
                                // if category contains the string 'TOTAL' then
                                // skip it
                                if (category.contains("TOTAL")) {
                                        continue;
                                }

                                if (nextRecord.length < 3) {
                                        continue;
                                }

                                try {
                                        amount = Double.parseDouble(nextRecord[2].replaceAll(",", ""));
                                }
                                catch (NumberFormatException e) {
                                        amount = 0.0;
                                }

                                switch (leadingSpaces) {
                                case 4: // if the line is a category

                                        newRecordType = type;
                                        // parent = "";
                                        parent = category;
                                        workingType = category;
                                        newLineItem = new LineItemCSV(newRecordType, inDate, category, category,
                                                        amount);
                                        newLineItem.isMainCategory(true);
                                        // if the category is not in the
                                        // category database, insert
                                        // it
                                        existingCategory = ReadData.categoryFindRecord(newLineItem);
                                        if ((existingCategory.getId() == -1)) {
                                                existingCategory = WriteData.categoryInsertRecord(newLineItem);
                                        }

                                        // if the category is not in the actual
                                        // database, insert it
                                        existingActual = ReadData.actualFindCategory(existingCategory);
                                        if (existingActual.getId() == -1) {
                                                WriteData.actualInsertRecord(existingCategory);
                                        }
                                        else {
                                                WriteData.autualUpdateAmount(existingActual);
                                        }

                                        break;

                                case 8:
                                        parent = workingType;
                                        newLineItem = new LineItemCSV(type, inDate, parent, category, amount);
                                        newLineItem.isMainCategory(false);

                                        // if the category is not in the
                                        // category database, insert
                                        // it
                                        existingCategory = ReadData.categoryFindRecord(newLineItem);
                                        if ((existingCategory.getId() == -1)) {
                                                existingCategory = WriteData.categoryInsertRecord(newLineItem);
                                        }

                                        // if the category is not in the actual
                                        // database, insert it
                                        existingActual = ReadData.actualFindCategory(existingCategory);
                                        if (existingActual.getId() == -1) {
                                                WriteData.actualInsertRecord(existingCategory);
                                        }
                                        else {
                                                WriteData.autualUpdateAmount(existingActual);
                                        }

                                        break;

                                default:
                                        // Handle any other number of leading
                                        // spaces
                                        break;
                                }

                        }
                        csvReader.close();

                }
                catch (Exception e) {
                        e.printStackTrace();
                }

                ReadData.findMissingCategories(inDate);
        }

        public void incomeTableBudget_OnEditCommit(TreeTableColumn.CellEditEvent<LineItem, Double> e) {
                LineItem item = e.getTreeTablePosition().getTreeItem().getValue();
                if (item.isCategory()) {
                        return;
                }
                item.setBudget(e.getNewValue());

                javafx.scene.control.TreeItem<LineItem> selectedTreeItem = tableIncome.getSelectionModel()
                                .getSelectedItem();
                if (selectedTreeItem != null) {
                        LineItem selectedItem = selectedTreeItem.getValue();
                        selectedItem.setBudget(e.getNewValue());
                }

                Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                                WriteData.actualUpdate(item);
                                return null;
                        }

                        @Override
                        protected void succeeded() {
                                super.succeeded();
                                // Refresh the table view to reflect the changes
                                // - wrap in Platform.runLater
                                Platform.runLater(() -> {
                                        Util.calculateTreeTotals(tableIncome.getRoot());
                                        tableIncomeTotal.setItems(FXCollections.observableArrayList(
                                                        ReadData.getTotals(DB.INCOME, item.getDate())));
                                        UIData.updateTableTotal(tables);
                                        tableIncome.refresh();
                                });
                        }

                        @Override
                        protected void failed() {
                                super.failed();
                                // Handle any errors that occurred during the
                                // task
                                Throwable exception = getException();
                                exception.printStackTrace();
                        }
                };
                new Thread(task).start();

                // keep focus on the selected row
                tableIncome.requestFocus();
        }

        public void mandatoryTableBudget_OnEditCommit(TreeTableColumn.CellEditEvent<LineItem, Double> e) {
                LineItem item = e.getTreeTablePosition().getTreeItem().getValue();
                if (item.isCategory()) {
                        return;
                }
                item.setBudget(e.getNewValue());

                javafx.scene.control.TreeItem<LineItem> selectedTreeItem = tableMandatory.getSelectionModel()
                                .getSelectedItem();
                if (selectedTreeItem != null) {
                        LineItem selectedItem = selectedTreeItem.getValue();
                        selectedItem.setBudget(e.getNewValue());
                }

                Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                                WriteData.actualUpdate(item);
                                return null;
                        }

                        @Override
                        protected void succeeded() {
                                super.succeeded();
                                // Refresh the table view to reflect the changes
                                // - wrap in Platform.runLater
                                Platform.runLater(() -> {
                                        // TreeItem<LineItem> root =
                                        // tableMandatory.getRoot();
                                        Util.calculateTreeTotals(tableMandatory.getRoot());
                                        tableMandatoryTotal.setItems(FXCollections.observableArrayList(
                                                        ReadData.getTotals(DB.MANDATORY, item.getDate())));
                                        UIData.updateTableTotal(tables);
                                        tableMandatory.refresh();
                                });
                        }

                        @Override
                        protected void failed() {
                                super.failed();
                                // Handle any errors that occurred during the
                                // task
                                Throwable exception = getException();
                                exception.printStackTrace();
                        }
                };
                new Thread(task).start();

                // keep focus on the selected row
                tableMandatory.requestFocus();
        }

        public void discretionaryTableBudget_OnEditCommit(TreeTableColumn.CellEditEvent<LineItem, Double> e) {

                LineItem item = e.getTreeTablePosition().getTreeItem().getValue();
                if (item.isCategory()) {
                        return;
                }
                item.setBudget(e.getNewValue());

                javafx.scene.control.TreeItem<LineItem> selectedTreeItem = tableDiscretionary.getSelectionModel()
                                .getSelectedItem();
                if (selectedTreeItem != null) {
                        LineItem selectedItem = selectedTreeItem.getValue();
                        selectedItem.setBudget(e.getNewValue());
                }

                Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                                WriteData.actualUpdate(item);
                                return null;
                        }

                        @Override
                        protected void succeeded() {
                                super.succeeded();
                                // Refresh the table view to reflect the changes
                                // - wrap in Platform.runLater
                                Platform.runLater(() -> {
                                        Util.calculateTreeTotals(tableDiscretionary.getRoot());
                                        tableDiscretionaryTotal.setItems(FXCollections.observableArrayList(
                                                        ReadData.getTotals(DB.DISCRETIONARY, item.getDate())));
                                        UIData.updateTableTotal(tables);
                                        tableDiscretionary.refresh();
                                });
                        }

                        @Override
                        protected void failed() {
                                super.failed();
                                // Handle any errors that occurred during the
                                // task
                                Throwable exception = getException();
                                exception.printStackTrace();
                        }
                };
                new Thread(task).start();

                // keep focus on the selected row
                tableDiscretionary.requestFocus();
        }

        public void editLineItem(LineItem item) {
                try {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/budget/editItem.fxml"));
                        Parent root = fxmlLoader.load();

                        EditItemController editItemController = fxmlLoader.getController();
                        editItemController.setItem(item);

                        Stage stage = new Stage();
                        stage.setTitle("Edit Item");
                        stage.setScene(new Scene(root));
                        stage.initModality(Modality.WINDOW_MODAL);
                        stage.initOwner(myAnchorPane.getScene().getWindow());
                        stage.showAndWait();

                }
                catch (IOException e) {
                        e.printStackTrace();
                }
        }

        public LocalDate getWorkingDate() {
                return LocalDate.of(Integer.parseInt(yearBox.getValue()),
                                monthBox.getSelectionModel().getSelectedIndex() + 1, 1);
        }

        public void updateTables() {
                Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                                // Do the data processing on background thread,
                                // but not UI updates
                                return null;
                        }

                        @Override
                        protected void succeeded() {
                                super.succeeded();
                                // Do all UI updates on the FX Application
                                // Thread
                                Platform.runLater(() -> {
                                        updateTablesTask();
                                        // Refresh the table views to reflect
                                        // the changes
                                        tableIncome.refresh();
                                        tableMandatory.refresh();
                                        tableDiscretionary.refresh();
                                });
                        }

                        @Override
                        protected void failed() {
                                super.failed();
                                // Handle any errors that occurred during the
                                // task
                                Throwable exception = getException();
                                exception.printStackTrace();
                        }
                };
                new Thread(task).start();
        }

        public void updateTablesTask() {
                LocalDate inDate = getWorkingDate();
                tableIncomeTotal.setItems(FXCollections.observableArrayList(ReadData.getTotals(DB.INCOME, inDate)));
                tableMandatoryTotal
                                .setItems(FXCollections.observableArrayList(ReadData.getTotals(DB.MANDATORY, inDate)));
                tableDiscretionaryTotal.setItems(
                                FXCollections.observableArrayList(ReadData.getTotals(DB.DISCRETIONARY, inDate)));
                getTableRows(inDate);
                UIData.updateTableTotal(tables);
        }

}
