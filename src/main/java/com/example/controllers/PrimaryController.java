package com.example.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PrimaryController {
        @FXML
        private TableView<LineItem> tableView_Discretionary;

        @FXML
        private TableColumn<LineItem, Double> discretionaryTable_Actual;

        @FXML
        private TableColumn<LineItem, Double> discretionaryTable_Budget;

        @FXML
        private TableColumn<LineItem, String> discretionaryTable_Category;

        @FXML
        private TableColumn<LineItem, Double> discretionaryTable_Diff;

        /************************************************************************/

        @FXML
        private TableView<LineItem> tableDiscretionaryTotal;

        @FXML
        private TableColumn<LineItem, Double> discretionaryTotalTable_Actual;

        @FXML
        private TableColumn<LineItem, Double> discretionaryTotalTable_Budget;

        @FXML
        private TableColumn<LineItem, String> discretionaryTotalTable_Category;

        @FXML
        private TableColumn<LineItem, Double> discretionaryTotalTable_Diff;

        /************************************************************************/

        @FXML
        private TableView<LineItem> tableView_Income;

        @FXML
        private TableColumn<LineItem, Double> incomeTable_Actual;

        @FXML
        private TableColumn<LineItem, Double> incomeTable_Budget;

        @FXML
        private TableColumn<LineItem, String> incomeTable_Category;

        @FXML
        private TableColumn<LineItem, Double> incomeTable_Diff;

        /*************************************************************************/

        @FXML
        private TableView<LineItem> tableIncomeTotal;

        @FXML
        private TableColumn<LineItem, Double> incomeTotalTable_Actual;

        @FXML
        private TableColumn<LineItem, Double> incomeTotalTable_Budget;

        @FXML
        private TableColumn<LineItem, String> incomeTotalTable_Category;

        @FXML
        private TableColumn<LineItem, Double> incomeTotalTable_Diff;

        /*************************************************************************/

        @FXML
        private TableView<LineItem> tableView_Mandatory;

        @FXML
        private TableColumn<LineItem, Double> mandatoryTable_Actual;

        @FXML
        private TableColumn<LineItem, Double> mandatoryTable_Budget;

        @FXML
        private TableColumn<LineItem, String> mandatoryTable_Category;

        @FXML
        private TableColumn<LineItem, Double> mandatoryTable_Diff;

        /*************************************************************************/

        @FXML
        private TableView<LineItem> tableManditoryTotal;

        @FXML
        private TableColumn<LineItem, Double> manditoryTotalTable_Actual;

        @FXML
        private TableColumn<LineItem, Double> manditoryTotalTable_Budget;

        @FXML
        private TableColumn<LineItem, String> manditoryTotalTable_Category;

        @FXML
        private TableColumn<LineItem, Double> manditoryTotalTable_Diff;

        /*************************************************************************/

        @FXML
        private TableView<LineItem> tableView_Total;

        @FXML
        private TableColumn<LineItem, String> totalTable_Category;

        @FXML
        private TableColumn<LineItem, Double> totalTable_Actual;

        @FXML
        private TableColumn<LineItem, Double> totalTable_Budget;

        @FXML
        private TableColumn<LineItem, Double> totalTable_Diff;

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

        public void initialize() {

                @SuppressWarnings("unused")
                PrimaryControllerExtend controllerExtend = new PrimaryControllerExtend(tableView_Total,
                                totalTable_Category, totalTable_Actual, totalTable_Budget, totalTable_Diff);

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
                tables.add(tableView_Total);

                // TODO: Update button is not disabled on startup

                myAnchorPane.getStyleClass().add("catBox");
                tableIncomeTotal.getStyleClass().add("total-table");

                // * ***************************************/
                // Set up choice boxes
                // ***************************************/
                LocalDate indate = LocalDate.now();
                ObservableList<String> monthChoices = FXCollections.observableArrayList("January", "February", "March",
                                "April", "May", "June", "July", "August", "September", "October", "November",
                                "December");
                monthBox.setItems(monthChoices);
                monthBox.getSelectionModel().select(indate.getMonthValue() - 1);
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

                // ***************************************/
                // Set up table columns
                // ***************************************/
                incomeTable_Category.setCellValueFactory(new PropertyValueFactory<LineItem, String>("Category"));
                incomeTable_Category.setCellFactory(TextFieldTableCell.forTableColumn());
                // incomeTable_Category.setOnEditCommit(e ->
                // incomeTableCategory_OnEditCommit(e));

                incomeTable_Actual.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("actual"));
                incomeTable_Actual.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));
                // incomeTable_Actual.setOnEditCommit(e ->
                // incomeTableActual_OnEditCommit(e));

                incomeTable_Budget.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("budget"));
                incomeTable_Budget.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));
                incomeTable_Budget.setOnEditCommit(e -> incomeTableBudget_OnEditCommit(e));

                incomeTable_Diff.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("diff"));
                incomeTable_Diff.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));
                // incomeTable_Diff.setOnEditCommit(e ->
                // incomeTableDiff_OnEditCommit(e));

                /***********************************************************/
                incomeTotalTable_Category.setCellValueFactory(new PropertyValueFactory<LineItem, String>("Category"));
                incomeTotalTable_Category.setCellFactory(TextFieldTableCell.forTableColumn());

                incomeTotalTable_Actual.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("actual"));
                incomeTotalTable_Actual.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                incomeTotalTable_Budget.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("budget"));
                incomeTotalTable_Budget.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                incomeTotalTable_Diff.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("diff"));
                incomeTotalTable_Diff.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                /***********************************************************/
                manditoryTotalTable_Category
                                .setCellValueFactory(new PropertyValueFactory<LineItem, String>("Category"));
                manditoryTotalTable_Category.setCellFactory(TextFieldTableCell.forTableColumn());

                manditoryTotalTable_Actual.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("actual"));
                incomeTotalTable_Actual.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                manditoryTotalTable_Budget.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("budget"));
                manditoryTotalTable_Budget.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                manditoryTotalTable_Diff.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("diff"));
                manditoryTotalTable_Diff.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                /***********************************************************/
                discretionaryTotalTable_Category
                                .setCellValueFactory(new PropertyValueFactory<LineItem, String>("Category"));
                discretionaryTotalTable_Category.setCellFactory(TextFieldTableCell.forTableColumn());

                discretionaryTotalTable_Actual
                                .setCellValueFactory(new PropertyValueFactory<LineItem, Double>("actual"));
                discretionaryTotalTable_Actual
                                .setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                discretionaryTotalTable_Budget
                                .setCellValueFactory(new PropertyValueFactory<LineItem, Double>("budget"));
                discretionaryTotalTable_Budget
                                .setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                discretionaryTotalTable_Diff.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("diff"));
                discretionaryTotalTable_Diff
                                .setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                /***********************************************************/
                mandatoryTable_Category.setCellValueFactory(new PropertyValueFactory<LineItem, String>("Category"));
                mandatoryTable_Category.setCellFactory(TextFieldTableCell.forTableColumn());
                mandatoryTable_Category.setOnEditCommit(e -> mandatoryTableCategory_OnEditCommit(e));

                mandatoryTable_Actual.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("actual"));
                mandatoryTable_Actual.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));
                mandatoryTable_Actual.setOnEditCommit(e -> mandatoryTableActual_OnEditCommit(e));

                mandatoryTable_Budget.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("budget"));
                mandatoryTable_Budget.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));
                mandatoryTable_Budget.setOnEditCommit(e -> mandatoryTableBudget_OnEditCommit(e));

                mandatoryTable_Diff.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("diff"));
                mandatoryTable_Diff.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));
                mandatoryTable_Diff.setOnEditCommit(e -> mandatoryTableDiff_OnEditCommit(e));

                /***********************************************************/

                discretionaryTable_Category.setCellValueFactory(new PropertyValueFactory<LineItem, String>("Category"));
                discretionaryTable_Category.setCellFactory(TextFieldTableCell.forTableColumn());
                discretionaryTable_Category.setOnEditCommit(e -> discretionaryTableCategory_OnEditCommit(e));

                discretionaryTable_Actual.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("actual"));
                discretionaryTable_Actual.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));
                discretionaryTable_Actual.setOnEditCommit(e -> discretionaryTableActual_OnEditCommit(e));

                discretionaryTable_Budget.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("budget"));
                discretionaryTable_Budget.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));
                discretionaryTable_Budget.setOnEditCommit(e -> discretionaryTableBudget_OnEditCommit(e));

                discretionaryTable_Diff.setCellValueFactory(new PropertyValueFactory<LineItem, Double>("diff"));
                discretionaryTable_Diff.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));
                discretionaryTable_Diff.setOnEditCommit(e -> discretionaryTableDiff_OnEditCommit(e));

                // Create a task to run getTableRows in another thread
                Task<Void> task2 = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                                getTableRows(indate);
                                UIData.updateTableTotal(tables);
                                return null;
                        }
                };
                new Thread(task2).start();

                // set btn_Update to be disabled and uncheck chkBox
                chkBox.setSelected(false);
                btn_Update.setDisable(true);

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
                tableView_Income.getItems().clear();
                tableView_Income.setItems(
                                FXCollections.observableArrayList(ReadData.getTableAmounts(DB.INCOME, inDate)));
                tableIncomeTotal.setItems(FXCollections.observableArrayList(ReadData.getTotals(DB.INCOME, inDate)));

                // get mandatory data
                tableView_Mandatory.getItems().clear();
                tableView_Mandatory.setItems(
                                FXCollections.observableArrayList(ReadData.getTableAmounts(DB.MANDITORY, inDate)));
                tableManditoryTotal
                                .setItems(FXCollections.observableArrayList(ReadData.getTotals(DB.MANDITORY, inDate)));

                // get discretionary data
                tableView_Discretionary.getItems().clear();
                tableView_Discretionary.setItems(
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
                                        parent = "";
                                        workingType = category;
                                        newLineItem = new LineItemCSV(newRecordType, inDate, parent, category, amount);

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
                                                WriteData.actualInsertRecord(existingActual, existingCategory);
                                        }
                                        else {
                                                WriteData.autualUpdateAmount(existingActual);
                                        }

                                        break;

                                case 8:
                                        parent = workingType;
                                        newLineItem = new LineItemCSV(type, inDate, parent, category, amount);

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
                                                WriteData.actualInsertRecord(existingActual, existingCategory);
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
        }

        public void incomeTableCategory_OnEditCommit(TableColumn.CellEditEvent<LineItem, String> e) {
                LineItem item = e.getRowValue();
                item.setCategory(e.getNewValue());
        }

        public void incomeTableActual_OnEditCommit(TableColumn.CellEditEvent<LineItem, Double> e) {
                LineItem item = e.getRowValue();
                item.setActual(e.getNewValue());
        }

        public void incomeTableBudget_OnEditCommit(TableColumn.CellEditEvent<LineItem, Double> e) {
                LineItem item = e.getRowValue();
                item.setBudget(e.getNewValue());

                LineItem selectedItem = tableView_Income.getSelectionModel().getSelectedItem();
                selectedItem.setBudget(e.getNewValue());

                Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                                WriteData.actualUpdateBudget(item);
                                tableIncomeTotal.setItems(FXCollections
                                                .observableArrayList(ReadData.getTotals(DB.INCOME, item.getDate())));
                                UIData.updateTableTotal(tables);

                                return null;
                        }
                };
                new Thread(task).start();

                // keep focus on the selected row
                tableView_Income.requestFocus();

        }

        public void incomeTableDiff_OnEditCommit(TableColumn.CellEditEvent<LineItem, Double> e) {
                LineItem item = e.getRowValue();
                item.setDiff(e.getNewValue());
        }

        public void mandatoryTableCategory_OnEditCommit(TableColumn.CellEditEvent<LineItem, String> e) {
                LineItem item = e.getRowValue();
                item.setCategory(e.getNewValue());
        }

        public void mandatoryTableActual_OnEditCommit(TableColumn.CellEditEvent<LineItem, Double> e) {
                LineItem item = e.getRowValue();
                item.setActual(e.getNewValue());
        }

        public void mandatoryTableBudget_OnEditCommit(TableColumn.CellEditEvent<LineItem, Double> e) {
                LineItem item = e.getRowValue();
                item.setBudget(e.getNewValue());

                LineItem selectedItem = tableView_Mandatory.getSelectionModel().getSelectedItem();
                selectedItem.setBudget(e.getNewValue());

                Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                                WriteData.actualUpdateBudget(item);
                                tableManditoryTotal.setItems(FXCollections
                                                .observableArrayList(ReadData.getTotals(DB.MANDITORY, item.getDate())));
                                UIData.updateTableTotal(tables);

                                return null;
                        }
                };
                new Thread(task).start();

                // keep focus on the selected row
                tableView_Mandatory.requestFocus();
        }

        public void mandatoryTableDiff_OnEditCommit(TableColumn.CellEditEvent<LineItem, Double> e) {
                LineItem item = e.getRowValue();
                item.setDiff(e.getNewValue());
        }

        public void discretionaryTableCategory_OnEditCommit(TableColumn.CellEditEvent<LineItem, String> e) {
                LineItem item = e.getRowValue();
                item.setCategory(e.getNewValue());
        }

        public void discretionaryTableActual_OnEditCommit(TableColumn.CellEditEvent<LineItem, Double> e) {
                LineItem item = e.getRowValue();
                item.setActual(e.getNewValue());
        }

        public void discretionaryTableBudget_OnEditCommit(TableColumn.CellEditEvent<LineItem, Double> e) {
                LineItem item = e.getRowValue();
                item.setBudget(e.getNewValue());

                LineItem selectedItem = tableView_Discretionary.getSelectionModel().getSelectedItem();
                selectedItem.setBudget(e.getNewValue());
                Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                                WriteData.actualUpdateBudget(item);
                                tableDiscretionaryTotal.setItems(FXCollections.observableArrayList(
                                                ReadData.getTotals(DB.DISCRETIONARY, item.getDate())));
                                UIData.updateTableTotal(tables);

                                return null;
                        }
                };
                new Thread(task).start();

                // keep focus on the selected row
                tableView_Discretionary.requestFocus();

        }

        public void discretionaryTableDiff_OnEditCommit(TableColumn.CellEditEvent<LineItem, Double> e) {
                LineItem item = e.getRowValue();
                item.setDiff(e.getNewValue());
        }

}
