package com.example.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.example.Util;
import com.example.data.DB;
import com.example.data.LineItem;
import com.example.data.LineItemCSV;
import com.example.data.ReadData;
import com.example.data.UIData;
import com.example.data.WriteData;
import com.opencsv.CSVReader;

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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
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
        @FXML
        private TableView<LineItem> tableDiscretionary;

        @FXML
        private TableColumn<LineItem, Double> tableDiscretionary_Actual;

        @FXML
        private TableColumn<LineItem, Double> tableDiscretionary_Budget;

        @FXML
        private TableColumn<LineItem, String> tableDiscretionary_Category;

        @FXML
        private TableColumn<LineItem, Double> tableDiscretionary_Diff;

        @FXML
        private TableColumn<LineItem, Double> tableDiscretionary_Balance;

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

        /************************************************************************/

        @FXML
        private TableView<LineItem> tableIncome;

        @FXML
        private TableColumn<LineItem, Double> tableIncome_Actual;

        @FXML
        private TableColumn<LineItem, Double> tableIncome_Budget;

        @FXML
        private TableColumn<LineItem, String> tableIncome_Category;

        @FXML
        private TableColumn<LineItem, Double> tableIncome_Diff;

        @FXML
        private TableColumn<LineItem, Double> tableIncome_Balance;

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

        /*************************************************************************/

        @FXML
        private TableView<LineItem> tableMandatory;

        @FXML
        private TableColumn<LineItem, Double> tableMandatory_Actual;

        @FXML
        private TableColumn<LineItem, Double> tableMandatory_Budget;

        @FXML
        private TableColumn<LineItem, String> tableMandatory_Category;

        @FXML
        private TableColumn<LineItem, Double> tableMandatory_Diff;

        @FXML
        private TableColumn<LineItem, Double> tableMandatory_Balance;

        /*************************************************************************/

        @FXML
        private TableView<LineItem> tableManditoryTotal;

        @FXML
        private TableColumn<LineItem, Double> tableManditoryTotal_Actual;

        @FXML
        private TableColumn<LineItem, Double> tableManditoryTotal_Budget;

        @FXML
        private TableColumn<LineItem, String> tableManditoryTotal_Category;

        @FXML
        private TableColumn<LineItem, Double> tableManditoryTotal_Diff;

        @FXML
        private TableColumn<LineItem, Double> tableManditoryTotal_Balance;

        /*************************************************************************/

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
        private ComboBox<Integer> yearBox;

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
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/secondary.fxml"));
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

                        LocalDate inDate = LocalDate.of(yearBox.getValue(),
                                        monthBox.getSelectionModel().getSelectedIndex() + 1, 1);

                        tableIncomeTotal.setItems(
                                        FXCollections.observableArrayList(ReadData.getTotals(DB.INCOME, inDate)));
                        tableManditoryTotal.setItems(
                                        FXCollections.observableArrayList(ReadData.getTotals(DB.MANDITORY, inDate)));
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
                LocalDate inDate = LocalDate.of(yearBox.getValue(), monthBox.getSelectionModel().getSelectedIndex() + 1,
                                1);

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
                LineItem firstItem = tableMandatory.getItems().get(0);
                LocalDate inDate = firstItem.getDate();

                Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                                WriteData.getLastBudget(inDate);
                                return null;
                        }

                        @Override
                        protected void succeeded() {
                                super.succeeded();
                                // Refresh the table view to reflect the changes
                                tableIncomeTotal.setItems(FXCollections
                                                .observableArrayList(ReadData.getTotals(DB.INCOME, inDate)));
                                tableManditoryTotal.setItems(FXCollections
                                                .observableArrayList(ReadData.getTotals(DB.MANDITORY, inDate)));
                                tableDiscretionaryTotal.setItems(FXCollections
                                                .observableArrayList(ReadData.getTotals(DB.DISCRETIONARY, inDate)));
                                getTableRows(inDate);
                                UIData.updateTableTotal(tables);
                                tableIncome.refresh();
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
                LineItem firstItem = tableMandatory.getItems().get(0);
                LocalDate inDate = firstItem.getDate();

                Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                                WriteData.updateBalance(inDate);
                                return null;
                        }

                        @Override
                        protected void succeeded() {
                                super.succeeded();
                                // Refresh the table view to reflect the changes
                                tableIncomeTotal.setItems(FXCollections
                                                .observableArrayList(ReadData.getTotals(DB.INCOME, inDate)));
                                tableManditoryTotal.setItems(FXCollections
                                                .observableArrayList(ReadData.getTotals(DB.MANDITORY, inDate)));
                                tableDiscretionaryTotal.setItems(FXCollections
                                                .observableArrayList(ReadData.getTotals(DB.DISCRETIONARY, inDate)));
                                getTableRows(inDate);
                                UIData.updateTableTotal(tables);
                                tableIncome.refresh();
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
                tableManditoryTotal.getStyleClass().add("table-view-total");
                tableDiscretionaryTotal.getStyleClass().add("table-view-total");

                // set btn_Update to be disabled and uncheck chkBox
                chkBox.setSelected(false);
                btn_Update.setDisable(true);

                // ***************************************/
                // Set up table refrence to update totals table
                // ***************************************/
                tables.add(tableIncomeTotal);
                tables.add(tableManditoryTotal);
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
                Task<ArrayList<Integer>> task = new Task<ArrayList<Integer>>() {
                        @Override
                        protected ArrayList<Integer> call() throws Exception {
                                return ReadData.getYears();
                        }
                };
                new Thread(task).start();

                task.setOnSucceeded(e -> {
                        ArrayList<Integer> years = task.getValue();
                        ObservableList<Integer> yearChoices = FXCollections.observableArrayList(years);
                        yearBox.setItems(yearChoices);
                        yearBox.setEditable(true);
                        yearBox.getSelectionModel().selectFirst();
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

                tableManditoryTotal.skinProperty().addListener((a, b, newSkin) -> {
                        Pane header = (Pane) tableManditoryTotal.lookup("TableHeaderRow");
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

                // add row to tableManditoryTotal
                LineItem manditoryTotal = new LineItem();
                manditoryTotal.setCategory("Total");
                manditoryTotal.setActual(0.0);
                manditoryTotal.setBudget(0.0);
                tableManditoryTotal.getItems().add(manditoryTotal);

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
                // Set up table columns
                // ***************************************/
                tableIncome_Category.setCellValueFactory(new PropertyValueFactory<LineItem, String>("Category"));
                tableIncome_Category.setCellFactory(TextFieldTableCell.forTableColumn());

                tableIncome_Actual.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("actual"));
                tableIncome_Actual.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                tableIncome_Budget.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("budget"));
                tableIncome_Budget.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));
                tableIncome_Budget.setOnEditCommit(e -> incomeTableBudget_OnEditCommit(e));

                tableIncome_Diff.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("diff"));
                tableIncome_Diff.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                /***********************************************************/
                tableIncomeTotal_Category.setCellValueFactory(new PropertyValueFactory<LineItem, String>("Category"));
                tableIncomeTotal_Category.setCellFactory(TextFieldTableCell.forTableColumn());

                tableIncomeTotal_Actual.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("actual"));
                tableIncomeTotal_Actual.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                tableIncomeTotal_Budget.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("budget"));
                tableIncomeTotal_Budget.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                tableIncomeTotal_Diff.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("diff"));
                tableIncomeTotal_Diff.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                /***********************************************************/
                tableMandatory_Category.setCellValueFactory(new PropertyValueFactory<LineItem, String>("Category"));
                tableMandatory_Category.setCellFactory(TextFieldTableCell.forTableColumn());

                tableMandatory_Actual.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("actual"));
                tableMandatory_Actual.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                tableMandatory_Budget.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("budget"));
                tableMandatory_Budget.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));
                tableMandatory_Budget.setOnEditCommit(e -> mandatoryTableBudget_OnEditCommit(e));

                tableMandatory_Diff.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("diff"));
                tableMandatory_Diff.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                tableMandatory_Balance.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("balance"));
                tableMandatory_Balance.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                tableMandatory.setRowFactory(tv -> {
                        TableRow<LineItem> row = new TableRow<>();
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
                tableManditoryTotal_Category
                                .setCellValueFactory(new PropertyValueFactory<LineItem, String>("Category"));
                tableManditoryTotal_Category.setCellFactory(TextFieldTableCell.forTableColumn());

                tableManditoryTotal_Actual.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("actual"));
                tableIncomeTotal_Actual.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                tableManditoryTotal_Budget.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("budget"));
                tableManditoryTotal_Budget.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                tableManditoryTotal_Diff.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("diff"));
                tableManditoryTotal_Diff.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                tableManditoryTotal_Balance.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("balance"));
                tableManditoryTotal_Balance
                                .setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                /***********************************************************/

                tableDiscretionary_Category.setCellValueFactory(new PropertyValueFactory<LineItem, String>("Category"));
                tableDiscretionary_Category.setCellFactory(TextFieldTableCell.forTableColumn());

                tableDiscretionary_Actual.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("actual"));
                tableDiscretionary_Actual.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                tableDiscretionary_Budget.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("budget"));
                tableDiscretionary_Budget.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));
                tableDiscretionary_Budget.setOnEditCommit(e -> discretionaryTableBudget_OnEditCommit(e));

                tableDiscretionary_Diff.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("diff"));
                tableDiscretionary_Diff.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                tableDiscretionary_Balance.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("balance"));
                tableDiscretionary_Balance.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                tableDiscretionary.setRowFactory(tv -> {
                        TableRow<LineItem> row = new TableRow<>();
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
                                getTableRows(inDate);
                                UIData.updateTableTotal(tables);
                                return null;
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
                                getTableRows(inDate);
                                UIData.updateTableTotal(tables);
                                return null;
                        }
                };
                new Thread(task).start();
        }

        public void getTableRows(LocalDate inDate) {
                // getActuals(inDate);
                tableIncome.getItems().clear();
                tableIncome.setItems(FXCollections.observableArrayList(ReadData.getTableAmounts(DB.INCOME, inDate)));
                tableIncomeTotal.setItems(FXCollections.observableArrayList(ReadData.getTotals(DB.INCOME, inDate)));

                // get mandatory data
                tableMandatory.getItems().clear();
                tableMandatory.setItems(
                                FXCollections.observableArrayList(ReadData.getTableAmounts(DB.MANDITORY, inDate)));
                tableManditoryTotal
                                .setItems(FXCollections.observableArrayList(ReadData.getTotals(DB.MANDITORY, inDate)));

                // get discretionary data
                tableDiscretionary.getItems().clear();
                tableDiscretionary.setItems(
                                FXCollections.observableArrayList(ReadData.getTableAmounts(DB.DISCRETIONARY, inDate)));
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
                                        type = DB.MANDITORY;
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
                                        newLineItem.setIsMainCat(true);
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
                                        newLineItem.setIsMainCat(false);

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

        public void incomeTableBudget_OnEditCommit(TableColumn.CellEditEvent<LineItem, Double> e) {
                LineItem item = e.getRowValue();
                item.setBudget(e.getNewValue());

                LineItem selectedItem = tableIncome.getSelectionModel().getSelectedItem();
                selectedItem.setBudget(e.getNewValue());

                Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                                WriteData.actualUpdate(item);
                                tableIncomeTotal.setItems(FXCollections
                                                .observableArrayList(ReadData.getTotals(DB.INCOME, item.getDate())));
                                UIData.updateTableTotal(tables);

                                return null;
                        }

                        @Override
                        protected void succeeded() {
                                super.succeeded();
                                // Refresh the table view to reflect the changes
                                tableIncome.refresh();
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

        public void mandatoryTableBudget_OnEditCommit(TableColumn.CellEditEvent<LineItem, Double> e) {
                LineItem item = e.getRowValue();
                item.setBudget(e.getNewValue());

                LineItem selectedItem = tableMandatory.getSelectionModel().getSelectedItem();
                selectedItem.setBudget(e.getNewValue());

                Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                                WriteData.actualUpdate(item);
                                tableManditoryTotal.setItems(FXCollections
                                                .observableArrayList(ReadData.getTotals(DB.MANDITORY, item.getDate())));
                                UIData.updateTableTotal(tables);

                                return null;
                        }

                        @Override
                        protected void succeeded() {
                                super.succeeded();
                                // Refresh the table view to reflect the changes
                                tableMandatory.refresh();
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

        public void discretionaryTableBudget_OnEditCommit(TableColumn.CellEditEvent<LineItem, Double> e) {
                LineItem item = e.getRowValue();
                item.setBudget(e.getNewValue());

                LineItem selectedItem = tableDiscretionary.getSelectionModel().getSelectedItem();
                selectedItem.setBudget(e.getNewValue());
                Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                                WriteData.actualUpdate(item);
                                tableDiscretionaryTotal.setItems(FXCollections.observableArrayList(
                                                ReadData.getTotals(DB.DISCRETIONARY, item.getDate())));
                                UIData.updateTableTotal(tables);

                                return null;
                        }

                        @Override
                        protected void succeeded() {
                                super.succeeded();
                                // Refresh the table view to reflect the changes
                                tableDiscretionary.refresh();
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
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/editItem.fxml"));
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
                return LocalDate.of(yearBox.getValue(), monthBox.getSelectionModel().getSelectedIndex() + 1, 1);
        }

        public void updateTablesTask() {
                LocalDate inDate = getWorkingDate();
                tableIncomeTotal.setItems(FXCollections.observableArrayList(ReadData.getTotals(DB.INCOME, inDate)));
                tableManditoryTotal
                                .setItems(FXCollections.observableArrayList(ReadData.getTotals(DB.MANDITORY, inDate)));
                tableDiscretionaryTotal.setItems(
                                FXCollections.observableArrayList(ReadData.getTotals(DB.DISCRETIONARY, inDate)));
                getTableRows(inDate);
                UIData.updateTableTotal(tables);
        }

}
