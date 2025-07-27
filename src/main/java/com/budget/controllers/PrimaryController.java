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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Primary controller for the Budget application main window. Manages the budget
 * tables, data loading, and user interactions.
 */
public class PrimaryController {

        private static final Logger LOGGER = Logger.getLogger(PrimaryController.class.getName());

        // ========================= CONSTANTS =========================

         private static final String FILE_PATH_STORAGE = "filePath.txt";
         private static final String DEFAULT_DIRECTORY = "C:\\";
        // private static final String EDIT_CATEGORY_TITLE = "Edit Category";
        // private static final String EDIT_ITEM_TITLE = "Edit Item";
        // private static final String TOTAL_LABEL = "Total";
        private static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");

        // ========================= THREAD POOL =========================

        private final ExecutorService executorService = Executors.newCachedThreadPool(r -> {
                Thread t = new Thread(r);
                t.setDaemon(true);
                t.setName("PrimaryController-Worker");
                return t;
        });

        // ========================= FXML COMPONENTS =========================

        // Income Table Components
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

        // Mandatory Table Components
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

        // Discretionary Table Components
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

        // Totals Table Components
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

        // UI Components
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

        // ========================= DATA STRUCTURES =========================

        /** Array of tables for UIData total table update */
        private final ArrayList<TableView<LineItem>> tables = new ArrayList<>();

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
        void btn_editCategories(ActionEvent event) {
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
        void btn_getLastMonthsBudget(ActionEvent event) {
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
                LineItem firstItem = null;
                if (tableMandatory.getRoot() != null && tableMandatory.getRoot().getChildren().size() > 0) {
                        firstItem = tableMandatory.getRoot().getChildren().get(0).getValue();
                }
                LocalDate inDate = firstItem != null ? firstItem.getDate() : getWorkingDate();

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
                                tableIncomeTotal.setItems(FXCollections
                                                .observableArrayList(ReadData.getTotals(DB.INCOME, inDate)));
                                tableMandatoryTotal.setItems(FXCollections
                                                .observableArrayList(ReadData.getTotals(DB.MANDATORY, inDate)));
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

        public ArrayList<TableView<LineItem>> getTables() {
                return tables;
        }

        public void initialize() {
                try {
                        LOGGER.info("Initializing PrimaryController");

                        // Initialize controller extension
                        @SuppressWarnings("unused")
                        PrimaryControllerExtend controllerExtend = new PrimaryControllerExtend(tableTotal,
                                        tableTotal_Category, tableTotal_Actual, tableTotal_Budget, tableTotal_Diff);

                        // Setup table columns
                        setupTableColumns();
                        LOGGER.info("PrimaryController initialization completed successfully");

                }
                catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error during PrimaryController initialization", e);
                        showErrorAlert("Initialization Error", "Failed to initialize the application properly.");
                }

         
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
                Task<List<String>> task = new Task<List<String>>() {
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
                LineItem mandatoryTotal = new LineItem();
                mandatoryTotal.setCategory("Total");
                mandatoryTotal.setActual(0.0);
                mandatoryTotal.setBudget(0.0);
                tableMandatoryTotal.getItems().add(mandatoryTotal);

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


               // ========================= EVENT HANDLERS =========================
        /**
         * Handles the Update Category button click.
         */
        @FXML
        void button_UpdateCat(ActionEvent event) {
                LocalDate workingDate = getWorkingDate();

                if (!chkBox.isSelected()) {
                        readFromDatabase(workingDate);
                }
                else {
                        handleCsvFileImport(workingDate);
                }

                updateMainDateLabel(workingDate);
        }


        // ========================= TABLE COLUMN SETUP
        // =========================

        private void setupTableColumns() {
                setupIncomeTableColumns();
                setupMandatoryTableColumns();
                setupDiscretionaryTableColumns();
                setupTotalTableColumns();
        }

        private void setupIncomeTableColumns() {
                setupTreeTableColumn(tableIncome_Category, LineItem::getCategory, true);
                setupTreeTableColumn(tableIncome_Actual, LineItem::getActual, false);
                setupTreeTableColumn(tableIncome_Budget, LineItem::getBudget, false);
                setupTreeTableColumn(tableIncome_Diff, LineItem::getDiff, false);

                tableIncome_Budget.setOnEditCommit(this::incomeTableBudget_OnEditCommit);

                // Setup total table columns
                setupTotalTableColumn(tableIncomeTotal_Category, "category", false);
                setupTotalTableColumn(tableIncomeTotal_Actual, "actual", true);
                setupTotalTableColumn(tableIncomeTotal_Budget, "budget", true);
                setupTotalTableColumn(tableIncomeTotal_Diff, "diff", true);
        }

        private void setupMandatoryTableColumns() {
                setupTreeTableColumn(tableMandatory_Category, LineItem::getCategory, true);
                setupTreeTableColumn(tableMandatory_Actual, LineItem::getActual, false);
                setupTreeTableColumn(tableMandatory_Budget, LineItem::getBudget, false);
                setupTreeTableColumn(tableMandatory_Diff, LineItem::getDiff, false);
                setupTreeTableColumn(tableMandatory_Balance, LineItem::getBalance, false);

                tableMandatory_Budget.setOnEditCommit(this::mandatoryTableBudget_OnEditCommit);

                // Setup total table columns
                setupTotalTableColumn(tableMandatoryTotal_Category, "category", false);
                setupTotalTableColumn(tableMandatoryTotal_Actual, "actual", true);
                setupTotalTableColumn(tableMandatoryTotal_Budget, "budget", true);
                setupTotalTableColumn(tableMandatoryTotal_Diff, "diff", true);
                setupTotalTableColumn(tableMandatoryTotal_Balance, "balance", true);
        }

        private void setupDiscretionaryTableColumns() {
                setupTreeTableColumn(tableDiscretionary_Category, LineItem::getCategory, true);
                setupTreeTableColumn(tableDiscretionary_Actual, LineItem::getActual, false);
                setupTreeTableColumn(tableDiscretionary_Budget, LineItem::getBudget, false);
                setupTreeTableColumn(tableDiscretionary_Diff, LineItem::getDiff, false);

                tableDiscretionary_Budget.setOnEditCommit(this::discretionaryTableBudget_OnEditCommit);

                // Setup total table columns
                setupTotalTableColumn(tableDiscretionaryTotal_Category, "category", false);
                setupTotalTableColumn(tableDiscretionaryTotal_Actual, "actual", true);
                setupTotalTableColumn(tableDiscretionaryTotal_Budget, "budget", true);
                setupTotalTableColumn(tableDiscretionaryTotal_Diff, "diff", true);
        }

        private void setupTotalTableColumns() {
                tableTotal_Category.setCellValueFactory(new PropertyValueFactory<>("category"));
                tableTotal_Category.setCellFactory(TextFieldTableCell.forTableColumn());

                tableTotal_Actual.setCellValueFactory(new PropertyValueFactory<>("actual"));
                tableTotal_Actual.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                tableTotal_Budget.setCellValueFactory(new PropertyValueFactory<>("budget"));
                tableTotal_Budget.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                tableTotal_Diff.setCellValueFactory(new PropertyValueFactory<>("diff"));
                tableTotal_Diff.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));
        }

        @SuppressWarnings("unchecked")
        private <T> void setupTreeTableColumn(TreeTableColumn<LineItem, T> column,
                        java.util.function.Function<LineItem, T> valueExtractor, boolean isStringColumn) {
                column.setCellValueFactory(cellData -> {
                        LineItem item = cellData.getValue().getValue();
                        if (item != null) {
                                T value = valueExtractor.apply(item);
                                if (isStringColumn) {
                                        return (javafx.beans.value.ObservableValue<T>) new javafx.beans.property.SimpleStringProperty(
                                                        (String) value);
                                }
                                else {
                                        return new javafx.beans.property.SimpleObjectProperty<>(value);
                                }
                        }
                        else {
                                return isStringColumn
                                                ? (javafx.beans.value.ObservableValue<T>) new javafx.beans.property.SimpleStringProperty(
                                                                "")
                                                : new javafx.beans.property.SimpleObjectProperty<>(
                                                                (T) Double.valueOf(0.0));
                        }
                });

                if (isStringColumn) {
                        ((TreeTableColumn<LineItem, String>) column).setCellFactory(
                                        javafx.scene.control.cell.TextFieldTreeTableCell.forTreeTableColumn());
                }
                else {
                        ((TreeTableColumn<LineItem, Double>) column)
                                        .setCellFactory(javafx.scene.control.cell.TextFieldTreeTableCell
                                                        .forTreeTableColumn(Util.getCurrencyConverter()));
                }
        }

        @SuppressWarnings("unchecked")
        private <T> void setupTotalTableColumn(TableColumn<LineItem, T> column, String propertyName,
                        boolean isCurrency) {
                column.setCellValueFactory(new PropertyValueFactory<>(propertyName));

                if (isCurrency) {
                        ((TableColumn<LineItem, Double>) column)
                                        .setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));
                }
                else {
                        ((TableColumn<LineItem, String>) column).setCellFactory(TextFieldTableCell.forTableColumn());
                }
        }

        // ========================= ERROR HANDLING =========================
        private void showErrorAlert(String title, String message) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
        }

        private void showConfirmationAlert(String title, String message, Runnable onConfirm) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);

                alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK && onConfirm != null) {
                                onConfirm.run();
                        }
                });
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

        /**
         * Loads table data for the specified date.
         */
        public void getTableRows(LocalDate date) {
                try {
                        // Load income data
                        TreeItem<LineItem> incomeRoot = ReadData.getTableAmountsTree(DB.INCOME, date);
                        tableIncome.setRoot(incomeRoot);
                        if (incomeRoot != null) {
                                incomeRoot.setExpanded(true);
                                Util.calculateTreeTotals(incomeRoot);
                        }
                        tableIncomeTotal.setItems(
                                        FXCollections.observableArrayList(ReadData.getTotals(DB.INCOME, date)));

                        // Load mandatory data
                        TreeItem<LineItem> mandatoryRoot = ReadData.getTableAmountsTree(DB.MANDATORY, date);
                        tableMandatory.setRoot(mandatoryRoot);
                        if (mandatoryRoot != null) {
                                mandatoryRoot.setExpanded(true);
                                Util.calculateTreeTotals(mandatoryRoot);
                        }
                        tableMandatoryTotal.setItems(
                                        FXCollections.observableArrayList(ReadData.getTotals(DB.MANDATORY, date)));

                        // Load discretionary data
                        TreeItem<LineItem> discretionaryRoot = ReadData.getTableAmountsTree(DB.DISCRETIONARY, date);
                        tableDiscretionary.setRoot(discretionaryRoot);
                        if (discretionaryRoot != null) {
                                discretionaryRoot.setExpanded(true);
                                Util.calculateTreeTotals(discretionaryRoot);
                        }
                        tableDiscretionaryTotal.setItems(
                                        FXCollections.observableArrayList(ReadData.getTotals(DB.DISCRETIONARY, date)));

                }
                catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error loading table data for date: " + date, e);
                        showErrorAlert("Data Loading Error", "Failed to load table data for the selected date.");
                }
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
                                                WriteData.actualUpdateAmount(existingActual);
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
                                                WriteData.actualUpdateAmount(existingActual);
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

        // ========================= EDIT COMMIT HANDLERS
        // =========================

        /**
         * Handles budget edit commit for income table.
         */
        public void incomeTableBudget_OnEditCommit(TreeTableColumn.CellEditEvent<LineItem, Double> event) {
                handleBudgetEditCommit(event, tableIncome, tableIncomeTotal, DB.INCOME);
        }

        /**
         * Handles budget edit commit for mandatory table.
         */
        public void mandatoryTableBudget_OnEditCommit(TreeTableColumn.CellEditEvent<LineItem, Double> event) {
                handleBudgetEditCommit(event, tableMandatory, tableMandatoryTotal, DB.MANDATORY);
        }

        /**
         * Handles budget edit commit for discretionary table.
         */
        public void discretionaryTableBudget_OnEditCommit(TreeTableColumn.CellEditEvent<LineItem, Double> event) {
                handleBudgetEditCommit(event, tableDiscretionary, tableDiscretionaryTotal, DB.DISCRETIONARY);
        }

        private void handleBudgetEditCommit(TreeTableColumn.CellEditEvent<LineItem, Double> event,
                        TreeTableView<LineItem> treeTable, TableView<LineItem> totalTable, int dbType) {
                LineItem item = event.getTreeTablePosition().getTreeItem().getValue();
                if (item == null || item.isCategory()) {
                        return;
                }

                item.setBudget(event.getNewValue());

                TreeItem<LineItem> selectedTreeItem = treeTable.getSelectionModel().getSelectedItem();
                if (selectedTreeItem != null) {
                        LineItem selectedItem = selectedTreeItem.getValue();
                        selectedItem.setBudget(event.getNewValue());
                }

                executeAsyncTask(() -> WriteData.actualUpdate(item), () -> {
                        Util.calculateTreeTotals(treeTable.getRoot());
                        totalTable.setItems(
                                        FXCollections.observableArrayList(ReadData.getTotals(dbType, item.getDate())));
                        UIData.updateTableTotal(tables);
                        treeTable.refresh();
                        treeTable.requestFocus();
                }, "Error updating budget for item: " + item.getCategory());
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
                                updateTablesTask();
                                return null;
                        }

                        @Override
                        protected void succeeded() {
                                super.succeeded();
                                // Refresh the table views to reflect the
                                // changes
                                tableIncome.refresh();
                                tableMandatory.refresh();
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

        // ========================= CSV IMPORT METHODS
        // =========================

        private void handleCsvFileImport(LocalDate workingDate) {
                try {
                        File selectedFile = showFileChooser();
                        if (selectedFile != null) {
                                saveFilePathToStorage(selectedFile);
                                readActual(selectedFile, workingDate);
                                readFromDatabase(workingDate);
                                resetImportState();
                        }
                }
                catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error handling CSV file import", e);
                        showErrorAlert("Import Error", "Failed to import CSV file.");
                }
        }


        private File showFileChooser() {
                String savedFilePath = loadFilePathFromStorage();
                File initialDirectory = new File(savedFilePath);

                if (!initialDirectory.exists() || !initialDirectory.canRead()) {
                        initialDirectory = new File(DEFAULT_DIRECTORY);
                }

                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialDirectory(initialDirectory);
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

                return fileChooser.showOpenDialog(null);
        }

                private void saveFilePathToStorage(File selectedFile) {
                try (FileWriter writer = new FileWriter(FILE_PATH_STORAGE)) {
                        writer.write(selectedFile.getParent());
                }
                catch (IOException e) {
                        LOGGER.log(Level.WARNING, "Error saving file path to storage", e);
                }
        }


        private String loadFilePathFromStorage() {
                try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_STORAGE))) {
                        return reader.readLine();
                }
                catch (IOException e) {
                        LOGGER.log(Level.WARNING, "Error retrieving file path from storage", e);
                        return DEFAULT_DIRECTORY;
                }
        }

        private void updateMainDateLabel(LocalDate date) {
                mainDateLabel.setText(date.format(MONTH_YEAR_FORMATTER));
        }

        private void resetImportState() {
                chkBox.setSelected(false);
                btn_Update.setDisable(true);
        }


        // ========================= ASYNC TASK UTILITIES
        // =========================

        private void executeAsyncTask(Runnable backgroundTask, Runnable uiTask, String errorMessage) {
                Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                                if (backgroundTask != null) {
                                        backgroundTask.run();
                                }
                                return null;
                        }

                        @Override
                        protected void succeeded() {
                                if (uiTask != null) {
                                        Platform.runLater(uiTask);
                                }
                        }

                        @Override
                        protected void failed() {
                                LOGGER.log(Level.SEVERE, errorMessage, getException());
                                Platform.runLater(() -> showErrorAlert("Operation Error", errorMessage));
                        }
                };

                executorService.submit(task);
        }

}
