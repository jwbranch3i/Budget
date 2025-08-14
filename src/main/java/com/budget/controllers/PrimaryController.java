package com.budget.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.budget.GlobalVariables;
import com.budget.Util;
import com.budget.dataModal.DB;
import com.budget.dataModal.DataSource;
import com.budget.dataModal.DatabaseDataResult;
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
        private TableView<LineItem> tableGrandTotal;
        @FXML
        private TableColumn<LineItem, String> tableGrandTotal_Category;
        @FXML
        private TableColumn<LineItem, Double> tableGrandTotal_Actual;
        @FXML
        private TableColumn<LineItem, Double> tableGrandTotal_Budget;
        @FXML
        private TableColumn<LineItem, Double> tableGrandTotal_Diff;

        // UI Components
        @FXML
        private VBox categoryBox;
        @FXML
        private AnchorPane myAnchorPane;
        // @FXML
        // private ProgressIndicator progressIndicator;
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
        private final ArrayList<TableView<LineItem>> totalTablesList = new ArrayList<>();

        // ========================= INITIALIZATION =========================

        /**
         * Initializes the controller after FXML loading.
         */
        @FXML
        public void initialize() {
                try {
                        // Initialize global logging settings first
                        // GlobalVariables.enableDebugLogging();
                        LOGGER.info("Initializing PrimaryController - " + GlobalVariables.getDebugStatus());

                        // Initialize controller extension
                        @SuppressWarnings("unused")
                        PrimaryControllerExtend controllerExtend = new PrimaryControllerExtend(tableGrandTotal,
                                        tableGrandTotal_Category, tableGrandTotal_Actual, tableGrandTotal_Budget,
                                        tableGrandTotal_Diff);

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
                      //  setupTotalTableRow();

                        // Setup table columns
                        setupTableColumns();

                        // Setup context menus
                        setupContextMenus();

                        // Setup selection listeners
                        setupSelectionListeners();

                        // Initialize data
                        initializeData();

                        // Setup shutdown hook for cleanup
                        setupShutdownHook();

                        // Setup keyboard shortcuts for debugging
                        setupKeyboardShortcuts();

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
                        if (GlobalVariables.isDebugMode()) {
                                LOGGER.fine("Edit Category button clicked");
                        }
                        openEditCategoryWindow(event);
                        refreshDataAfterEdit();
                        if (GlobalVariables.isDebugMode()) {
                                LOGGER.fine("Edit Category window closed, data refreshed");
                        }
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
                        updateTables();
                }
                else {
                        handleCsvFileImport(workingDate);
                }

                updateRunningTotalsFromMonth(workingDate);
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
                        // updateRunningTotalsAndShowWarnings(); // Add this
                        // line
                }, "Error updating balance");
        }

        /**
         * Switches to secondary view (placeholder implementation).
         */
        @FXML
        private void switchToSecondary() throws IOException {
                // TODO: Implement view switching logic
        }

        // ========================= SETUP AND SHUTDOWN METHODS
        // =========================

        private void setupShutdownHook() {
                // Add shutdown hook to current stage
                Platform.runLater(() -> {
                        Stage stage = (Stage) btn_Update.getScene().getWindow();
                        if (stage != null) {
                                stage.setOnCloseRequest(event -> {
                                        LOGGER.info("Application closing - cleaning up resources");
                                        cleanup();
                                });
                        }
                });

                // Also add JVM shutdown hook as backup
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        LOGGER.info("JVM shutdown detected - cleaning up PrimaryController");
                        cleanup();
                }));
        }

        /**
         * Setup keyboard shortcuts for debugging and logging control
         */
        private void setupKeyboardShortcuts() {
                Platform.runLater(() -> {
                        Stage stage = (Stage) btn_Update.getScene().getWindow();
                        if (stage != null && stage.getScene() != null) {
                                stage.getScene().setOnKeyPressed(event -> {
                                        // Ctrl+Shift+D to toggle debug logging
                                        if (event.isControlDown() && event.isShiftDown()) {
                                                switch (event.getCode()) {
                                                case D:
                                                        toggleDebugLogging();
                                                        event.consume();
                                                        break;
                                                case T:
                                                        // Ctrl+Shift+T to
                                                        // enable trace logging
                                                        GlobalVariables.enableTraceLogging();
                                                        showLoggingStatusMessage("Trace logging enabled");
                                                        event.consume();
                                                        break;
                                                case I:
                                                        // Ctrl+Shift+I to set
                                                        // info logging
                                                        GlobalVariables.setInfoLogging();
                                                        showLoggingStatusMessage("Info logging enabled");
                                                        event.consume();
                                                        break;
                                                case S:
                                                        // Ctrl+Shift+S to show
                                                        // current settings
                                                        GlobalVariables.printCurrentSettings();
                                                        event.consume();
                                                        break;
                                                default:
                                                        break;
                                                }
                                        }
                                });
                        }
                });
        }

        /**
         * Toggle debug logging on/off
         */
        private void toggleDebugLogging() {
                if (GlobalVariables.isDebugMode()) {
                        GlobalVariables.setInfoLogging();
                        showLoggingStatusMessage("Debug logging disabled - Info level active");
                        LOGGER.info("Debug logging disabled via keyboard shortcut");
                }
                else {
                        GlobalVariables.enableDebugLogging();
                        showLoggingStatusMessage("Debug logging enabled");
                        LOGGER.fine("Debug logging enabled via keyboard shortcut");
                }
        }

        /**
         * Show a brief status message about logging changes
         */
        private void showLoggingStatusMessage(String message) {
                System.out.println("LOGGING: " + message + " - Current level: " + GlobalVariables.getLoggingLevel());

                // You could also show this in a status bar or temporary tooltip
                // if you have one
                // For now, we'll just print to console and log it
                LOGGER.info("Logging status: " + message);
        }

        private void applyStyles() {
                tableIncomeTotal.getStyleClass().add("table-view-total");
                tableMandatoryTotal.getStyleClass().add("table-view-total");
                tableDiscretionaryTotal.getStyleClass().add("table-view-total");
                myAnchorPane.getStyleClass().add("catBox");
        }

        private void setupInitialState() {
                chkBox.setSelected(false);
                btn_Update.setDisable(true);
        }

        private void setupTableReferences() {
                totalTablesList.add(tableIncomeTotal);
                totalTablesList.add(tableMandatoryTotal);
                totalTablesList.add(tableDiscretionaryTotal);
                totalTablesList.add(tableGrandTotal);
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

                // Hide root nodes in tree tables
                hideRootNodes();
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

        private void hideRootNodes() {
                tableIncome.setShowRoot(false);
                tableMandatory.setShowRoot(false);
                tableDiscretionary.setShowRoot(false);
        }

        // private void setupTotalTableRow() {
        //         addTotalRowToTable(tableIncomeTotal, 1.0, 1.0);
        //         addTotalRowToTable(tableMandatoryTotal, 0.0, 0.0);
        //         addTotalRowToTable(tableDiscretionaryTotal, 0.0, 0.0);
        // }

        // private void addTotalRowToTable(TableView<LineItem> table, double actual, double budget) {
        //         LineItem totalItem = new LineItem();
        //         totalItem.setCategory(TOTAL_LABEL);
        //         totalItem.setActual(actual);
        //         totalItem.setBudget(budget);
        //         table.getItems().add(totalItem);
        // }

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
                tableGrandTotal_Category.setCellValueFactory(new PropertyValueFactory<>("category"));
                tableGrandTotal_Category.setCellFactory(TextFieldTableCell.forTableColumn());

                tableGrandTotal_Actual.setCellValueFactory(new PropertyValueFactory<>("actual"));
                tableGrandTotal_Actual.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                tableGrandTotal_Budget.setCellValueFactory(new PropertyValueFactory<>("budget"));
                tableGrandTotal_Budget.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));

                tableGrandTotal_Diff.setCellValueFactory(new PropertyValueFactory<>("diff"));
                tableGrandTotal_Diff.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));
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
                                        updateTables();
                                        updateMainDateLabel(currentDate);
                                        updateRunningTotalsFromMonth(currentDate);
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
                // Show loading indicator
                setLoadingState(true);

                CompletableFuture.supplyAsync(() -> loadDatabaseData(date), executorService).thenAcceptAsync(data -> {
                        Platform.runLater(() -> {
                                setTreeTableRoot(tableIncome, data.getIncomeRoot());
                                setTreeTableRoot(tableMandatory, data.getMandatoryRoot());
                                setTreeTableRoot(tableDiscretionary, data.getDiscretionaryRoot());

                                tableIncomeTotal.getItems().clear();
                                tableMandatoryTotal.getItems().clear();
                                tableDiscretionaryTotal.getItems().clear();

                                
                                tableIncomeTotal.getItems().add(data.getIncomeTotals());
                                tableMandatoryTotal.getItems().add(data.getMandatoryTotals());
                                tableDiscretionaryTotal.getItems().add(data.getDiscretionaryTotals());

                                setLoadingState(false);
                        });
                }).exceptionally(throwable -> {
                        LOGGER.log(Level.SEVERE, "Error reading data from database", throwable);
                        Platform.runLater(() -> {
                                setLoadingState(false);
                                showErrorAlert("Database Error",
                                                "Failed to load data from database: " + throwable.getMessage());
                        });
                        return null;
                });
        }

        /**
         * Loads all required data from database (background thread safe).
         */
        private DatabaseDataResult loadDatabaseData(LocalDate date) {
                try {
                        // Load all data in background thread
                        TreeItem<LineItem> incomeRoot = ReadData.getTableAmountsTree(DB.INCOME, date);
                        TreeItem<LineItem> mandatoryRoot = ReadData.getTableAmountsTree(DB.MANDATORY, date);
                        TreeItem<LineItem> discretionaryRoot = ReadData.getTableAmountsTree(DB.DISCRETIONARY, date);

                        // LineItem incomeTotals = ReadData.getTotals(DB.INCOME, date);
                        // LineItem mandatoryTotals = ReadData.getTotals(DB.MANDATORY, date);
                        // LineItem discretionaryTotals = ReadData.getTotals(DB.DISCRETIONARY, date);

                        // Calculate tree totals in background
                        if (incomeRoot != null) {
                                Util.calculateTreeTotals(incomeRoot);
                        }
                        if (mandatoryRoot != null) {
                                Util.calculateTreeTotals(mandatoryRoot);
                        }
                        if (discretionaryRoot != null) {
                                Util.calculateTreeTotals(discretionaryRoot);
                        }

                        // return new DatabaseDataResult(incomeRoot,
                        // mandatoryRoot, discretionaryRoot, incomeTotals,
                        // mandatoryTotals, discretionaryTotals);

                        return new DatabaseDataResult(incomeRoot, mandatoryRoot, discretionaryRoot);

                }
                catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error loading database data for date: " + date, e);
                        throw new RuntimeException("Failed to load database data", e);
                }
        }

        private void setLoadingState(boolean loading) {
                // progressIndicator.setVisible(loading);
                btn_Update.setDisable(loading);
                // Disable other relevant controls during loading
        }

        // private void updateUIWithData(DatabaseDataResult data) {
        //         // Update all UI components
        //         updateTreeTables(data);
        // }

        // private void updateTreeTables(DatabaseDataResult data) {
        //         setTreeTableRoot(tableIncome, data.getIncomeRoot());
        //         setTreeTableRoot(tableMandatory, data.getMandatoryRoot());
        //         setTreeTableRoot(tableDiscretionary, data.getDiscretionaryRoot());
        // }

        private void setTreeTableRoot(TreeTableView<LineItem> table, TreeItem<LineItem> root) {
                table.setRoot(root);
                if (root != null) {
                        root.setExpanded(true);
                }
        }

        // ========================= CSV IMPORT METHODS
        // =========================

        private void handleCsvFileImport(LocalDate workingDate) {
                try {
                        File selectedFile = showFileChooser();
                        if (selectedFile != null) {
                                saveFilePathToStorage(selectedFile);

                                // Run readCSVFile in a background thread, then
                                // call readFromDatabase after completion
                                Task<Void> importTask = new Task<Void>() {
                                        @Override
                                        protected Void call() throws Exception {
                                                readCSVFile(selectedFile, workingDate);
                                                return null;
                                        }

                                        @Override
                                        protected void succeeded() {
                                                Platform.runLater(() -> {
                                                        // readFromDatabase(workingDate);
                                                        updateTables();
                                                        resetImportState();
                                                        // WriteData.updateAllRunningTotals(workingDate);
                                                });
                                        }

                                        @Override
                                        protected void failed() {
                                                LOGGER.log(Level.SEVERE, "Error reading CSV file in background",
                                                                getException());
                                                Platform.runLater(() -> showErrorAlert("Import Error",
                                                                "Failed to import CSV file."));
                                        }
                                };
                                executorService.submit(importTask);

                        }
                        else {
                                LOGGER.log(Level.WARNING, "No file selected for import");
                                showErrorAlert("File Selection Error", "No file was selected for import.");
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
        public static void readCSVFile(File file, LocalDate date) {
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
                        UIData.updateTableGrandTotal(totalTablesList);
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

                // getTableRowsFromDatabase(workingDate);
                readFromDatabase(workingDate);
                UIData.updateTableGrandTotal(totalTablesList);
        }

        private void refreshDataAfterEdit() {
                LocalDate workingDate = getWorkingDate();

                tableIncomeTotal.setItems(
                                FXCollections.observableArrayList(ReadData.getTotals(DB.INCOME, workingDate)));
                tableMandatoryTotal.setItems(
                                FXCollections.observableArrayList(ReadData.getTotals(DB.MANDATORY, workingDate)));
                tableDiscretionaryTotal.setItems(
                                FXCollections.observableArrayList(ReadData.getTotals(DB.DISCRETIONARY, workingDate)));

                // getTableRowsFromDatabase(workingDate);
                readFromDatabase(workingDate);
                UIData.updateTableGrandTotal(totalTablesList);

                // updateRunningTotalsAndShowWarnings();
        }

        private void refreshDataForDate(LocalDate date) {
                tableIncomeTotal.setItems(FXCollections.observableArrayList(ReadData.getTotals(DB.INCOME, date)));
                tableMandatoryTotal.setItems(FXCollections.observableArrayList(ReadData.getTotals(DB.MANDATORY, date)));
                tableDiscretionaryTotal.setItems(
                                FXCollections.observableArrayList(ReadData.getTotals(DB.DISCRETIONARY, date)));

                // getTableRowsFromDatabase(date);
                readFromDatabase(date);
                UIData.updateTableGrandTotal(totalTablesList);
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
                // TODO: possible removal - showConfirmationAlert()
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

        public ArrayList<TableView<LineItem>> getTotalTablesList() {
                return totalTablesList;
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
         * Updates running totals for the current month and all following
         * months. This cascades changes through all future months since each
         * month's running total depends on the previous month's ending balance.
         * 
         * @param startDate The month to start updating from (current month)
         * @return true if all updates were successful, false otherwise
         */
        public static boolean updateRunningTotalsFromMonth(LocalDate startDate) {
                if (startDate == null) {
                        throw new IllegalArgumentException("Start date cannot be null");
                }

                if (!DataSource.getInstance().ensureConnection()) {
                        LOGGER.severe("Database connection not available for cascading running totals update");
                        return false;
                }

                // Get all months that need updating (current month and all
                // future months)
                List<LocalDate> monthsToUpdate = getMonthsToUpdate(startDate);

                if (monthsToUpdate.isEmpty()) {
                        LOGGER.info("No months found to update from " + Util.formatDateForDatabase(startDate));
                        return true;
                }

                LOGGER.info("Starting cascading running totals update from " + Util.formatDateForDatabase(startDate)
                                + " for " + monthsToUpdate.size() + " months");

                int totalUpdatedCategories = 0;
                int totalWarnings = 0;

                // Process each month in chronological order
                for (LocalDate monthDate : monthsToUpdate) {
                        LOGGER.info("Updating running totals for month: " + Util.formatDateForDatabase(monthDate));

                        MonthUpdateResult result = updateRunningTotalsForSingleMonth(monthDate);

                        if (!result.isSuccess()) {
                                LOGGER.severe("Failed to update running totals for month: "
                                                + Util.formatDateForDatabase(monthDate));
                                return false;
                        }

                        totalUpdatedCategories += result.getUpdatedCount();
                        totalWarnings += result.getWarningCount();

                        LOGGER.fine("Month " + Util.formatDateForDatabase(monthDate) + " - Updated: "
                                        + result.getUpdatedCount() + " categories, Warnings: "
                                        + result.getWarningCount());
                }

                LOGGER.info("Cascading running totals update completed. Total categories updated: "
                                + totalUpdatedCategories + ", Total warnings: " + totalWarnings);

                return true;
        }

        /**
         * Gets all months that have actual data from the start date onwards.
         */
        private static List<LocalDate> getMonthsToUpdate(LocalDate startDate) {
                List<LocalDate> months = new ArrayList<>();
                String startDateStr = Util.formatDateForDatabase(startDate);

                String query = "SELECT DISTINCT STRFTIME('%Y-%m', date) as month_date " + "FROM actual "
                                + "WHERE STRFTIME('%Y-%m', date) >= ? " + "ORDER BY month_date";

                try (PreparedStatement stmt = DataSource.getConn().prepareStatement(query)) {
                        stmt.setString(1, startDateStr);

                        try (ResultSet rs = stmt.executeQuery()) {
                                while (rs.next()) {
                                        String monthStr = rs.getString("month_date");
                                        LocalDate monthDate = LocalDate.parse(monthStr + "-01");
                                        months.add(monthDate);
                                }
                        }
                }
                catch (SQLException e) {
                        LOGGER.log(Level.SEVERE, "Error getting months to update from " + startDateStr, e);
                }

                return months;
        }

        /**
         * Updates running totals for a single month and returns detailed
         * results.
         */
        private static MonthUpdateResult updateRunningTotalsForSingleMonth(LocalDate monthDate) {
                String dateString = Util.formatDateForDatabase(monthDate);
                int updatedCount = 0;
                int warningCount = 0;
                List<String> warnings = new ArrayList<>();

                try (PreparedStatement stmt = DataSource.getConn()
                                .prepareStatement(DB.ACTUAL_GET_LINE_ITEMS_FOR_MONTH)) {
                        stmt.setString(1, dateString);

                        try (ResultSet rs = stmt.executeQuery()) {
                                while (rs.next()) {
                                        int categoryId = rs.getInt("id");
                                        double budget = rs.getDouble("budget");
                                        double actual = rs.getDouble("actual");
                                        Double maxAmount = rs.getObject("default_maximum_amount", Double.class);
                                        String categoryName = rs.getString("category");

                                        RunningTotal result = WriteData.updateRunningTotal(categoryId, monthDate,
                                                        budget, actual, maxAmount);

                                        if (result != null) {
                                                updatedCount++;

                                                // Check for warnings
                                                if (result.needsWarning()) {
                                                        warningCount++;
                                                        String warningMsg = "NEGATIVE RUNNING TOTAL: Category '"
                                                                        + categoryName
                                                                        + "' has a negative running total of "
                                                                        + result.getRunningTotal() + " in month "
                                                                        + dateString;
                                                        warnings.add(warningMsg);
                                                        LOGGER.warning(warningMsg);
                                                }

                                                if (result.isOverMaximum()) {
                                                        warningCount++;
                                                        String warningMsg = "OVER MAXIMUM: Category '" + categoryName
                                                                        + "' running total (" + result.getRunningTotal()
                                                                        + ") exceeds maximum ("
                                                                        + result.getMaximumAmount() + ") in month "
                                                                        + dateString;
                                                        warnings.add(warningMsg);
                                                        LOGGER.warning(warningMsg);
                                                }
                                        }
                                }
                        }

                        return new MonthUpdateResult(true, updatedCount, warningCount, warnings);

                }
                catch (SQLException e) {
                        LOGGER.log(Level.SEVERE, "Error updating running totals for month " + dateString, e);
                        return new MonthUpdateResult(false, updatedCount, warningCount, warnings);
                }
        }

        /**
         * Updates running totals from current month forward when actual amounts
         * change. This is the main entry point that should be called after any
         * actual amount updates.
         */
        public static boolean cascadeRunningTotalsUpdate(LocalDate changedMonth) {
                if (changedMonth == null) {
                        throw new IllegalArgumentException("Changed month cannot be null");
                }

                LOGGER.info("Cascading running totals update triggered by changes in: "
                                + Util.formatDateForDatabase(changedMonth));

                return WriteData.performTransactionSafeBatch(() -> {
                        if (!updateRunningTotalsFromMonth(changedMonth)) {
                                throw new RuntimeException("Failed to update cascading running totals");
                        }
                });
        }

        /**
         * Updates running totals for all categories in the current month only.
         * This is the existing method renamed for clarity.
         */
        public static boolean updateCurrentMonthRunningTotals(LocalDate monthDate) {
                // TODO : maybe remove this method,
                // 'updateCurrentMonthRunningTotals()' as it is not used in the
                // current codebase
                // return updateAllRunningTotals(monthDate);
                return true; // Placeholder for future implementation
        }

        /**
         * Result object for month update operations.
         */
        public static class MonthUpdateResult {
                private final boolean success;
                private final int updatedCount;
                private final int warningCount;
                private final List<String> warnings;

                public MonthUpdateResult(boolean success, int updatedCount, int warningCount, List<String> warnings) {
                        this.success = success;
                        this.updatedCount = updatedCount;
                        this.warningCount = warningCount;
                        this.warnings = new ArrayList<>(warnings);
                }

                public boolean isSuccess() {
                        return success;
                }

                public int getUpdatedCount() {
                        return updatedCount;
                }

                public int getWarningCount() {
                        return warningCount;
                }

                public List<String> getWarnings() {
                        return warnings;
                }
        }

        /**
         * Convenience method to update running totals from current system month
         * forward.
         */
        public static boolean updateRunningTotalsFromNow() {
                LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
                return updateRunningTotalsFromMonth(currentMonth);
        }

        /**
         * Updates running totals for a specific date range.
         */
        public static boolean updateRunningTotalsForDateRange(LocalDate startMonth, LocalDate endMonth) {
                if (startMonth == null || endMonth == null) {
                        throw new IllegalArgumentException("Start and end months cannot be null");
                }

                if (startMonth.isAfter(endMonth)) {
                        throw new IllegalArgumentException("Start month cannot be after end month");
                }

                LOGGER.info("Updating running totals for date range: " + Util.formatDateForDatabase(startMonth) + " to "
                                + Util.formatDateForDatabase(endMonth));

                // Get months in the specified range
                List<LocalDate> monthsInRange = new ArrayList<>();
                LocalDate current = startMonth.withDayOfMonth(1);
                LocalDate end = endMonth.withDayOfMonth(1);

                while (!current.isAfter(end)) {
                        monthsInRange.add(current);
                        current = current.plusMonths(1);
                }

                // Filter to only include months that have actual data
                List<LocalDate> monthsToUpdate = getMonthsToUpdate(startMonth).stream()
                                .filter(month -> !month.isAfter(endMonth))
                                .collect(java.util.stream.Collectors.toList());

                if (monthsToUpdate.isEmpty()) {
                        LOGGER.info("No months with data found in specified range");
                        return true;
                }

                return WriteData.performTransactionSafeBatch(() -> {
                        for (LocalDate monthDate : monthsToUpdate) {
                                MonthUpdateResult result = updateRunningTotalsForSingleMonth(monthDate);
                                if (!result.isSuccess()) {
                                        throw new RuntimeException("Failed to update running totals for month: "
                                                        + Util.formatDateForDatabase(monthDate));
                                }
                        }
                });
        }

        // ========================= NEW METHOD =========================

        // In PrimaryController, call this after any actual amount updates
        private void handleActualAmountUpdate(LocalDate monthChanged) {
                // TODO: possible removal - handleActualAmountUpdate()
                executeAsyncTask(() -> cascadeRunningTotalsUpdate(monthChanged), () -> {
                        // Refresh UI after running totals update
                        readFromDatabase(getWorkingDate());
                        // updateRunningTotalWarnings();
                }, "Error updating running totals");
        }

        /**
         * Allows user to manually modify a running total.
         */
        public void modifyRunningTotal(int categoryId, LocalDate monthDate, double newTotal) {
                executeAsyncTask(() -> WriteData.setModifiedRunningTotal(categoryId, monthDate, newTotal), () -> {
                        // Refresh UI after modification
                        // updateRunningTotalWarnings();
                        readFromDatabase(getWorkingDate());
                }, "Error modifying running total");
        }

        /**
         * Clears manual modification for a running total.
         */
        public void clearRunningTotalModification(int categoryId, LocalDate monthDate) {
                executeAsyncTask(() -> WriteData.setModifiedRunningTotal(categoryId, monthDate, null), () -> {
                        // Refresh UI after clearing modification
                        // updateRunningTotalWarnings();
                        readFromDatabase(getWorkingDate());
                }, "Error clearing running total modification");
        }
}