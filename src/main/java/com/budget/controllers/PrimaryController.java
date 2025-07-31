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
import com.budget.dataModal.RunningTotal;
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
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
        private static final String EDIT_CATEGORY_TITLE = "Edit Category";
        private static final String EDIT_ITEM_TITLE = "Edit Item";
        private static final String TOTAL_LABEL = "Total";
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

        // ========================= INITIALIZATION =========================

        /**
         * Initializes the controller after FXML loading.
         */
        @FXML
        public void initialize() {
                try {
                        LOGGER.info("Initializing PrimaryController");

                        // Initialize controller extension
                        @SuppressWarnings("unused")
                        PrimaryControllerExtend controllerExtend = new PrimaryControllerExtend(tableTotal,
                                        tableTotal_Category, tableTotal_Actual, tableTotal_Budget, tableTotal_Diff);

                        // Apply styles
                        applyStyles();

                        // Setup initial state
                        setupInitialState();

                        // Setup tables reference for totals update
                        setupTableReferences();

                        // Setup choice boxes
                        setupChoiceBoxes();

                        // Setup table headers and rows
                        setupTableHeaders();
                        setupTableRows();

                        // Setup table columns
                        setupTableColumns();

                        // Setup context menus
                        setupContextMenus();

                        // Setup selection listeners
                        setupSelectionListeners();

                        // Initialize data
                        initializeData();

                        LOGGER.info("PrimaryController initialization completed successfully");

                }
                catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error during PrimaryController initialization", e);
                        showErrorAlert("Initialization Error", "Failed to initialize the application properly.");
                }
        }

        // ========================= EVENT HANDLERS =========================

        /**
         * Handles the Edit Category button click.
         */
        @FXML
        void button_EditCat(ActionEvent event) {
                try {
                        openEditCategoryWindow(event);
                        refreshDataAfterEdit();
                }
                catch (IOException e) {
                        LOGGER.log(Level.SEVERE, "Error opening edit category window", e);
                        showErrorAlert("Window Error", "Failed to open the edit category window.");
                }
        }

        /**
         * Handles the Update Category button click.
         */
        @FXML
        void btn_Update(ActionEvent event) {
                LocalDate workingDate = getWorkingDate();

                if (!chkBox.isSelected()) {
                        readFromDatabase(workingDate);
                }
                else {
                        handleCsvFileImport(workingDate);
                }

                updateMainDateLabel(workingDate);
        }

        /**
         * Handles the Update Budget button click.
         */
        @FXML
        void button_UpdateBudget(ActionEvent event) {
                executeAsyncTask(() -> WriteData.copyLastMonthBudget(getWorkingDate()), this::updateTables,
                                "Error updating budget from last month");
        }

        /**
         * Handles the Update Balance button click.
         */

        @FXML
        void button_UpdateBalance(ActionEvent event) {
                LocalDate workingDate = getWorkingDate();

                executeAsyncTask(() -> WriteData.updateBalance(workingDate), () -> {
                        refreshDataForDate(workingDate);
                        updateRunningTotalsAndShowWarnings(); // Add this line
                }, "Error updating balance");
        }

        /**
         * Handles reading headings (placeholder implementation).
         */
        @FXML
        void readHeadingsButton(ActionEvent event) {
                progressIndicator.setVisible(true);
                progressIndicator.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

                // TODO: Implement actual heading reading logic

                progressIndicator.setVisible(false);
        }

        /**
         * Switches to secondary view (placeholder implementation).
         */
        @FXML
        private void switchToSecondary() throws IOException {
                // TODO: Implement view switching logic
        }

        // ========================= SETUP METHODS =========================

        private void applyStyles() {
                tableIncomeTotal.getStyleClass().add("table-view-total");
                tableMandatoryTotal.getStyleClass().add("table-view-total");
                tableDiscretionaryTotal.getStyleClass().add("table-view-total");
                myAnchorPane.getStyleClass().add("catBox");
                tableIncomeTotal.getStyleClass().add("total-table");
        }

        private void setupInitialState() {
                chkBox.setSelected(false);
                btn_Update.setDisable(true);
        }

        private void setupTableReferences() {
                tables.add(tableIncomeTotal);
                tables.add(tableMandatoryTotal);
                tables.add(tableDiscretionaryTotal);
                tables.add(tableTotal);
        }

        private void setupChoiceBoxes() {
                LocalDate currentDate = LocalDate.now();

                // Setup month box
                ObservableList<String> monthChoices = FXCollections.observableArrayList("January", "February", "March",
                                "April", "May", "June", "July", "August", "September", "October", "November",
                                "December");
                monthBox.setItems(monthChoices);
                monthBox.getSelectionModel().select(currentDate.getMonthValue() - 1);
                monthBox.setOnAction(e -> btn_Update.setDisable(false));

                // Setup year box asynchronously
                setupYearBoxAsync(currentDate);
                yearBox.setOnAction(e -> btn_Update.setDisable(false));
        }

        private void setupYearBoxAsync(LocalDate currentDate) {
                Task<List<String>> yearTask = new Task<List<String>>() {
                        @Override
                        protected List<String> call() throws Exception {
                                return ReadData.getYears();
                        }
                };

                yearTask.setOnSucceeded(e -> {
                        List<String> years = yearTask.getValue();
                        populateYearBox(years, currentDate);
                });

                yearTask.setOnFailed(e -> {
                        LOGGER.log(Level.WARNING, "Failed to load years from database", yearTask.getException());
                        // Fallback to current year
                        ObservableList<String> fallbackYears = FXCollections
                                        .observableArrayList(String.valueOf(currentDate.getYear()));
                        yearBox.setItems(fallbackYears);
                        yearBox.getSelectionModel().select(0);
                });

                executorService.submit(yearTask);
        }

        private void populateYearBox(List<String> years, LocalDate currentDate) {
                String currentYear = String.valueOf(currentDate.getYear());
                String nextYear = String.valueOf(currentDate.getYear() + 1);
                String previousYear = String.valueOf(currentDate.getYear() - 1);

                // Add missing years
                if (!years.contains(currentYear))
                        years.add(currentYear);
                if (!years.contains(nextYear))
                        years.add(nextYear);
                if (!years.contains(previousYear))
                        years.add(previousYear);

                years.sort(String::compareTo);

                ObservableList<String> yearChoices = FXCollections.observableArrayList(years);
                yearBox.setItems(yearChoices);
                yearBox.setEditable(true);
                yearBox.getSelectionModel().select(currentYear);
        }

        private void setupTableHeaders() {
                hideTableHeaders(tableIncomeTotal);
                hideTableHeaders(tableMandatoryTotal);
                hideTableHeaders(tableDiscretionaryTotal);
        }

        private void hideTableHeaders(TableView<LineItem> table) {
                table.skinProperty().addListener((obs, oldSkin, newSkin) -> {
                        if (newSkin != null) {
                                Pane header = (Pane) table.lookup("TableHeaderRow");
                                if (header != null) {
                                        header.setMinHeight(0);
                                        header.setPrefHeight(0);
                                        header.setMaxHeight(0);
                                        header.setVisible(false);
                                }
                        }
                });
        }

        private void setupTableRows() {
                addTotalRowToTable(tableIncomeTotal, 1.0, 1.0);
                addTotalRowToTable(tableMandatoryTotal, 0.0, 0.0);
                addTotalRowToTable(tableDiscretionaryTotal, 0.0, 0.0);
        }

        private void addTotalRowToTable(TableView<LineItem> table, double actual, double budget) {
                LineItem totalItem = new LineItem();
                totalItem.setCategory(TOTAL_LABEL);
                totalItem.setActual(actual);
                totalItem.setBudget(budget);
                table.getItems().add(totalItem);
        }

        private void setupSelectionListeners() {
                setupClearSelectionListener(tableIncome, tableMandatory, tableDiscretionary);
                setupClearSelectionListener(tableMandatory, tableIncome, tableDiscretionary);
                setupClearSelectionListener(tableDiscretionary, tableIncome, tableMandatory);
        }

        @SafeVarargs
        private final void setupClearSelectionListener(TreeTableView<LineItem> sourceTable,
                        TreeTableView<LineItem>... otherTables) {
                sourceTable.getSelectionModel().selectedItemProperty()
                                .addListener((obs, oldSelection, newSelection) -> {
                                        if (newSelection != null) {
                                                for (TreeTableView<LineItem> table : otherTables) {
                                                        table.getSelectionModel().clearSelection();
                                                }
                                        }
                                });
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
                        // Custom cell factory for budget columns that checks
                        // for children
                        ((TreeTableColumn<LineItem, Double>) column).setCellFactory(tv -> {
                                return new javafx.scene.control.cell.TextFieldTreeTableCell<LineItem, Double>(
                                                Util.getCurrencyConverter()) {

                                        @Override
                                        public void startEdit() {
                                                // Check if this tree item has
                                                // children
                                                TreeTableRow<LineItem> row = getTableRow();
                                                TreeItem<LineItem> treeItem = (row != null) ? row.getTreeItem() : null;
                                                if (treeItem != null && !treeItem.getChildren().isEmpty()) {
                                                        // Don't allow editing
                                                        // if item has children
                                                        return;
                                                }
                                                super.startEdit();
                                        }

                                        @Override
                                        public void updateItem(Double item, boolean empty) {
                                                super.updateItem(item, empty);

                                                // Visual indication that parent
                                                // items are not editable
                                                TreeTableRow<LineItem> row = getTableRow();
                                                TreeItem<LineItem> treeItem = (row != null) ? row.getTreeItem() : null;
                                                if (!empty && treeItem != null && !treeItem.getChildren().isEmpty()) {
                                                        setStyle("-fx-text-fill: #888888;"); // Grey
                                                                                             // out
                                                                                             // parent
                                                                                             // items
                                                        setTooltip(new javafx.scene.control.Tooltip(
                                                                        "Parent categories cannot be edited"));
                                                }
                                                else {
                                                        setStyle("");
                                                        setTooltip(null);
                                                }
                                        }
                                };
                        });
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

        // ========================= CONTEXT MENU SETUP
        // =========================
        private void setupContextMenus() {
                setupTreeTableRowFactory(tableIncome, false); // No context menu
                setupTreeTableRowFactory(tableMandatory, true); // With context
                                                                // menu
                setupTreeTableRowFactory(tableDiscretionary, true); // With
                                                                    // context
                                                                    // menu
        }

        private void setupTreeTableRowFactory(TreeTableView<LineItem> table, boolean hasRightClickAction) {
                table.setRowFactory(tv -> {
                        TreeTableRow<LineItem> row = new TreeTableRow<LineItem>() {
                                @Override
                                protected void updateItem(LineItem item, boolean empty) {
                                        super.updateItem(item, empty);

                                        if (empty || item == null) {
                                                setStyle("");
                                        }
                                        else {
                                                TreeItem<LineItem> treeItem = getTreeItem();
                                                if (treeItem != null && !treeItem.getChildren().isEmpty()) {
                                                        // Parent category -
                                                        // light blue background
                                                        setStyle("-fx-background-color: lightblue;");
                                                }
                                                else {
                                                        // Leaf item - default
                                                        // background
                                                        setStyle("");
                                                }
                                        }
                                }
                        };

                        // Add right-click action to open secondary window
                        if (hasRightClickAction) {
                                row.setOnMouseClicked(event -> {
                                        if (event.getButton() == MouseButton.SECONDARY && !row.isEmpty()) {
                                                try {
                                                        editLineItemDetail(event);
                                                }
                                                catch (IOException e) {
                                                        LOGGER.log(Level.SEVERE, "Error opening secondary window", e);
                                                        showErrorAlert("Window Error",
                                                                        "Failed to open the category edit window.");
                                                }
                                        }
                                });
                        }

                        return row;
                });
        }
        // private void setupTreeTableRowFactory(TreeTableView<LineItem> table,
        // boolean hasContextMenu) {
        // table.setRowFactory(tv -> {
        // TreeTableRow<LineItem> row = new TreeTableRow<LineItem>() {
        // @Override
        // protected void updateItem(LineItem item, boolean empty) {
        // super.updateItem(item, empty);

        // if (empty || item == null) {
        // setStyle("");
        // } else {
        // TreeItem<LineItem> treeItem = getTreeItem();
        // if (treeItem != null && !treeItem.getChildren().isEmpty()) {
        // // Parent category - light blue background
        // setStyle("-fx-background-color: lightblue;");
        // } else {
        // // Leaf item - default background
        // setStyle("");
        // }
        // }
        // }
        // };

        // // Add context menu only if requested
        // if (hasContextMenu) {
        // ContextMenu contextMenu = new ContextMenu();
        // MenuItem editItem = new MenuItem("Edit");
        // contextMenu.getItems().add(editItem);

        // row.setOnMouseClicked(event -> {
        // if (event.getButton() == MouseButton.SECONDARY && !row.isEmpty()) {
        // contextMenu.show(row, event.getScreenX(), event.getScreenY());
        // }
        // });

        // editItem.setOnAction(event -> {
        // LineItem item = row.getItem();
        // if (item != null) {
        // editLineItem(item);
        // }
        // });
        // }

        // return row;
        // });
        // }

        private void initializeData() {
                LocalDate currentDate = LocalDate.now();

                Task<Void> initTask = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                                return null; // Data processing would go here
                        }

                        @Override
                        protected void succeeded() {
                                Platform.runLater(() -> {
                                        getTableRows(currentDate);
                                        calculateAllTreeTotals();
                                        UIData.updateTableTotal(tables);
                                        updateMainDateLabel(currentDate);
                                });
                        }

                        @Override
                        protected void failed() {
                                LOGGER.log(Level.SEVERE, "Failed to initialize data", getException());
                                Platform.runLater(() -> showErrorAlert("Data Error", "Failed to load initial data."));
                        }
                };

                executorService.submit(initTask);
        }

        /**
         * Reads data from database for the specified date.
         */
        public void readFromDatabase(LocalDate date) {
                executeAsyncTask(() -> {
                        // No background processing needed for this operation
                        // All database operations will happen on the UI thread
                }, () -> {
                        getTableRows(date);
                        UIData.updateTableTotal(tables);
                }, "Error reading data from database");
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
                                WriteData.updateAllRunningTotals(workingDate);
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

        private String loadFilePathFromStorage() {
                try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_STORAGE))) {
                        return reader.readLine();
                }
                catch (IOException e) {
                        LOGGER.log(Level.WARNING, "Error retrieving file path from storage", e);
                        return DEFAULT_DIRECTORY;
                }
        }

        private void saveFilePathToStorage(File selectedFile) {
                try (FileWriter writer = new FileWriter(FILE_PATH_STORAGE)) {
                        writer.write(selectedFile.getParent());
                }
                catch (IOException e) {
                        LOGGER.log(Level.WARNING, "Error saving file path to storage", e);
                }
        }

        private void resetImportState() {
                chkBox.setSelected(false);
                btn_Update.setDisable(true);
        }

        /**
         * Reads actual data from CSV file.
         */
        public static void readActual(File file, LocalDate date) {
                try (FileReader fileReader = new FileReader(file); CSVReader csvReader = new CSVReader(fileReader)) {

                        LineItemCSV newLineItem;

                        String[] nextRecord;
                        String category;
                        String parent = "";
                        String workingType = "";
                        double amount;
                        int type = 0;

                        while ((nextRecord = csvReader.readNext()) != null) {
                                if (nextRecord.length <= 1)
                                        continue;

                                int leadingSpaces = nextRecord[1].length() - nextRecord[1].trim().length();
                                String trimmedValue = nextRecord[1].trim();

                                if ("INFLOWS".equals(trimmedValue)) {
                                        type = DB.INCOME;
                                        continue;
                                }
                                else if ("OUTFLOWS".equals(trimmedValue)) {
                                        type = DB.MANDATORY;
                                        continue;
                                }

                                category = trimmedValue;
                                if (category.contains("TOTAL") || nextRecord.length < 3) {
                                        continue;
                                }

                                try {
                                        amount = Double.parseDouble(nextRecord[2].replaceAll(",", ""));
                                }
                                catch (NumberFormatException e) {
                                        amount = 0.0;
                                }

                                switch (leadingSpaces) {
                                case 4: // Category level
                                        parent = category;
                                        workingType = category;
                                        newLineItem = new LineItemCSV(type, date, category, category, amount);
                                        newLineItem.isMainCategory(true);
                                        processLineItem(newLineItem);
                                        break;

                                case 8: // Sub-category level
                                        parent = workingType;
                                        newLineItem = new LineItemCSV(type, date, parent, category, amount);
                                        newLineItem.isMainCategory(false);
                                        processLineItem(newLineItem);
                                        break;

                                default:
                                        // Handle other indentation levels if
                                        // needed
                                        break;
                                }
                        }

                        ReadData.findMissingCategories(date);

                }
                catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error reading CSV file: " + file.getName(), e);
                }
        }

        private static void processLineItem(LineItemCSV newLineItem) {
                try {
                        // Find or insert category
                        LineItemCSV existingCategory = ReadData.categoryFindRecord(newLineItem);
                        if (existingCategory.getId() == -1) {
                                existingCategory = WriteData.categoryInsertRecord(newLineItem);
                        }

                        // Find or insert actual record
                        LineItemCSV existingActual = ReadData.actualFindCategory(existingCategory);
                        if (existingActual.getId() == -1) {
                                WriteData.actualInsertRecord(existingCategory);
                        }
                        else {
                                WriteData.actualUpdateAmount(existingActual);
                        }
                }
                catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error processing line item: " + newLineItem.getCategory(), e);
                }
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

        // ========================= UTILITY METHODS =========================

        /**
         * Gets the currently selected working date from the UI controls.
         */
        public LocalDate getWorkingDate() {
                try {
                        int year = Integer.parseInt(yearBox.getValue());
                        int month = monthBox.getSelectionModel().getSelectedIndex() + 1;
                        return LocalDate.of(year, month, 1);
                }
                catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Error parsing working date, using current date", e);
                        return LocalDate.now();
                }
        }

        /**
         * Updates all tables with latest data.
         */
        public void updateTables() {
                executeAsyncTask(null, this::updateTablesTask, "Error updating tables");
        }

        /**
         * Updates table data on UI thread.
         */
        public void updateTablesTask() {
                LocalDate workingDate = getWorkingDate();

                tableIncomeTotal.setItems(
                                FXCollections.observableArrayList(ReadData.getTotals(DB.INCOME, workingDate)));
                tableMandatoryTotal.setItems(
                                FXCollections.observableArrayList(ReadData.getTotals(DB.MANDATORY, workingDate)));
                tableDiscretionaryTotal.setItems(
                                FXCollections.observableArrayList(ReadData.getTotals(DB.DISCRETIONARY, workingDate)));

                getTableRows(workingDate);
                UIData.updateTableTotal(tables);
        }

        private void refreshDataAfterEdit() {
                LocalDate workingDate = getWorkingDate();

                tableIncomeTotal.setItems(
                                FXCollections.observableArrayList(ReadData.getTotals(DB.INCOME, workingDate)));
                tableMandatoryTotal.setItems(
                                FXCollections.observableArrayList(ReadData.getTotals(DB.MANDATORY, workingDate)));
                tableDiscretionaryTotal.setItems(
                                FXCollections.observableArrayList(ReadData.getTotals(DB.DISCRETIONARY, workingDate)));

                getTableRows(workingDate);
                UIData.updateTableTotal(tables);

                updateRunningTotalsAndShowWarnings();
        }

        private void refreshDataForDate(LocalDate date) {
                tableIncomeTotal.setItems(FXCollections.observableArrayList(ReadData.getTotals(DB.INCOME, date)));
                tableMandatoryTotal.setItems(FXCollections.observableArrayList(ReadData.getTotals(DB.MANDATORY, date)));
                tableDiscretionaryTotal.setItems(
                                FXCollections.observableArrayList(ReadData.getTotals(DB.DISCRETIONARY, date)));

                getTableRows(date);
                UIData.updateTableTotal(tables);
                tableIncome.refresh();
        }

        private void calculateAllTreeTotals() {
                if (tableIncome.getRoot() != null)
                        Util.calculateTreeTotals(tableIncome.getRoot());
                if (tableMandatory.getRoot() != null)
                        Util.calculateTreeTotals(tableMandatory.getRoot());
                if (tableDiscretionary.getRoot() != null)
                        Util.calculateTreeTotals(tableDiscretionary.getRoot());
        }

        private void updateMainDateLabel(LocalDate date) {
                mainDateLabel.setText(date.format(MONTH_YEAR_FORMATTER));
        }

        private void openEditCategoryWindow(ActionEvent event) throws IOException {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/budget/secondary.fxml"));
                Parent root = fxmlLoader.load();

                Stage stage = new Stage();
                stage.setTitle(EDIT_CATEGORY_TITLE);
                stage.setScene(new Scene(root));
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(((Node) event.getSource()).getScene().getWindow());
                stage.showAndWait();
        }

        /**
         * Opens the edit item detail dialog for the specified line item.
         */
        private void editLineItemDetail(MouseEvent event) throws IOException {
                // Get the selected LineItem from the row that was right-clicked
                @SuppressWarnings("unchecked")
                TreeTableRow<LineItem> row = (TreeTableRow<LineItem>) event.getSource();
                LineItem selectedItem = row.getItem();

                if (selectedItem == null) {
                        LOGGER.warning("No item selected for editing");
                        return;
                }

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/budget/editItem.fxml"));
                Parent root = fxmlLoader.load();

                // Get the controller and pass the selected item
                EditItemController editItemController = fxmlLoader.getController();
                editItemController.setLineItem(selectedItem);

                Stage stage = new Stage();
                stage.setTitle(EDIT_ITEM_TITLE);
                stage.setScene(new Scene(root));
                stage.initModality(Modality.WINDOW_MODAL);

                // Get the window from the event source
                Node source = (Node) event.getSource();
                stage.initOwner(source.getScene().getWindow());

                stage.showAndWait();

                // Check if changes were made and refresh if needed
                if (editItemController.wasItemModified()) {
                        refreshDataAfterEdit();
                }
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

        // ========================= GETTERS =========================

        public ArrayList<TableView<LineItem>> getTables() {
                return tables;
        }

        // ========================= CLEANUP =========================

        /**
         * Cleanup method to shutdown executor service. Should be called when
         * the controller is no longer needed.
         */
        public void cleanup() {
                if (executorService != null && !executorService.isShutdown()) {
                        executorService.shutdown();
                        LOGGER.info("PrimaryController cleanup completed");
                }
        }

        // Add to your PrimaryController class

        /**
         * Updates running totals after data changes and shows warnings if
         * needed.
         */
        private void updateRunningTotalsAndShowWarnings() {
                LocalDate currentDate = getWorkingDate();

                Task<List<RunningTotal>> task = new Task<List<RunningTotal>>() {
                        @Override
                        protected List<RunningTotal> call() throws Exception {
                                // Update all running totals for current month
                                boolean success = WriteData.updateAllRunningTotals(currentDate);
                                if (!success) {
                                        throw new RuntimeException("Failed to update running totals");
                                }
                                // Get negative totals for warnings
                                return ReadData.getNegativeRunningTotals(currentDate);
                        }

                        @Override
                        protected void succeeded() {
                                List<RunningTotal> negativeTotals = getValue();
                                if (negativeTotals != null && !negativeTotals.isEmpty()) {
                                        showRunningTotalWarnings(negativeTotals);
                                }
                        }

                        @Override
                        protected void failed() {
                                LOGGER.log(Level.SEVERE, "Error updating running totals", getException());
                                Platform.runLater(() -> showErrorAlert("Operation Error", "Error updating running totals"));
                        }
                };

                executorService.submit(task);
        }

        /**
         * Shows warnings for negative running totals.
         */
        private void showRunningTotalWarnings(List<RunningTotal> negativeTotals) {
                StringBuilder message = new StringBuilder();
                message.append("The following categories have negative running totals:\n\n");

                for (RunningTotal total : negativeTotals) {
                        message.append(String.format("• %s: $%.2f\n", total.getCategoryName(),
                                        total.getRunningTotal()));

                        // Mark warning as issued
                        WriteData.markWarningIssued(total.getCategoryId(), total.getMonthDate());
                }

                message.append("\nConsider adjusting your budget or spending for these categories.");

                Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Budget Warning");
                        alert.setHeaderText("Negative Running Totals Detected");
                        alert.setContentText(message.toString());
                        alert.setResizable(true);
                        alert.getDialogPane().setPrefSize(400, 300);
                        alert.showAndWait();
                });
        }

}