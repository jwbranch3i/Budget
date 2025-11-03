package com.budget.controllers;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.budget.Util;
import com.budget.dataModel.LineItem;
import com.budget.dataModel.WriteData;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Controller for the item editing dialog. Allows editing of line item
 * properties such as starting balance and include status.
 */
public class EditItemController {

    private static final Logger LOGGER = Logger.getLogger(EditItemController.class.getName());

    // ========================= CONSTANTS =========================

    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM yyyy");
    private static final String SAVE_SUCCESS_MESSAGE = "Item updated successfully";
    private static final String VALIDATION_ERROR_TITLE = "Validation Error";
    private static final String SAVE_ERROR_TITLE = "Save Error";

    // ========================= THREAD POOL =========================

    private final ExecutorService executorService = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.setName("EditItemController-Worker");
        return t;
    });

    // ========================= FXML COMPONENTS =========================

    @FXML
    private GridPane mainGridPane;
    @FXML
    private ChoiceBox<String> CB_itemType;
    @FXML
    private Label LBL_id;
    @FXML
    private Label LBL_date;
    @FXML
    private Label LBL_category;
    @FXML
    private Label LBL_actual;
    @FXML
    private Label LBL_budget;
    @FXML
    private Label LBL_balance;
    @FXML
    private Button BTN_cancel;
    @FXML
    private Button BTN_save;
    @FXML
    private CheckBox CHKBOX_hide;
    @FXML
    private TextField TXTFIELD_startBal;

    // ========================= INSTANCE VARIABLES =========================

    private boolean hasUnsavedDataChanges = false;
    private boolean hasUnsavedTypeChange = false;
    private boolean itemModified = false;
    private LineItem currentLineItem;

    // ========================= INITIALIZATION =========================

    /**
     * Initializes the controller after FXML loading.
     */
    @FXML
    public void initialize() {
        try {
            LOGGER.info("Initializing EditItemController");

            setupEventHandlers();
            setupChoiceBox();
            setupInputValidation();
            setupKeyboardShortcuts();
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during EditItemController initialization", e);
            Util.showErrorAlert(SAVE_ERROR_TITLE, "Failed to initialize the edit dialog properly.");
        }
    }


    // setup choice box with options "Income", "Mandatory", "Discretionary"
    private void setupChoiceBox() {
        CB_itemType.getItems().addAll("Income", "Mandatory", "Discretionary");
    }

    private void setupEventHandlers() {
        TXTFIELD_startBal.textProperty().addListener((observable, oldValue, newValue) -> {
            hasUnsavedDataChanges = true;
            updateSaveButtonState();
        });

        CHKBOX_hide.selectedProperty().addListener((observable, oldValue, newValue) -> {
            hasUnsavedTypeChange = true;
            updateSaveButtonState();
        });

        CB_itemType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            hasUnsavedTypeChange = true;
            updateSaveButtonState();
        });

        // Setup window close handler
        Platform.runLater(this::setupWindowCloseHandler);
    }

    private void setupInputValidation() {
        // Allow only numeric input for start balance field
        TXTFIELD_startBal.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isValidCurrencyInput(newValue)) {
                TXTFIELD_startBal.setText(oldValue);
            }
        });
    }

    private void setupKeyboardShortcuts() {
        // Enter key saves, Escape cancels
        Platform.runLater(() -> {
            if (mainGridPane.getScene() != null) {
                mainGridPane.getScene().setOnKeyPressed(event -> {
                    switch (event.getCode()) {
                    case ENTER:
                        if (BTN_save.isDisabled() == false) {
                            btn_saveAction(new ActionEvent());
                        }
                        break;
                    case ESCAPE:
                        button_cancel(new ActionEvent());
                        break;
                    default:
                        break;
                    }
                });
            }
        });
    }

    private void setupWindowCloseHandler() {
        try {
            Stage stage = (Stage) mainGridPane.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                if (hasUnsavedDataChanges || hasUnsavedTypeChange) {
                    event.consume(); // Prevent immediate close
                    handleUnsavedChanges(() -> stage.close());
                }
            });
        }
        catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error setting up window close handler", e);
        }
    }

    // ========================= EVENT HANDLERS =========================

    /**
     * Handles the cancel button click.
     */
    @FXML
    void button_cancel(ActionEvent event) {
        if (hasUnsavedDataChanges || hasUnsavedTypeChange) {
            handleUnsavedChanges(this::closeWindow);
        }
        else {
            closeWindow();
        }
    }

    /**
     * Handles the save button click.
     */
    @FXML
    void btn_saveAction(ActionEvent event) {
        if (!validateInput()) {
            return;
        }

        try {
            updateItemFromUI();

            BTN_save.setDisable(true);

            saveItemData();
            itemModified = true;

        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error preparing item for save", e);
            Util.showErrorAlert(SAVE_ERROR_TITLE, "Error occurred while preparing to save the item.");
            BTN_save.setDisable(false);
        }

        closeWindow();
    }

    // ========================= ITEM MANAGEMENT =========================

    /**
     * Sets the item to be edited and populates the UI fields.
     * 
     * @param editItem The LineItem to edit
     * @throws IllegalArgumentException if editItem is null
     */
    public void setLineItem(LineItem editItem) {
        if (editItem == null) {
            throw new IllegalArgumentException("Edit item cannot be null");
        }

        this.currentLineItem = editItem;
        populateUIFromItem();
        hasUnsavedDataChanges = false;
        hasUnsavedTypeChange = false;
        updateSaveButtonState();
    }

    private void populateUIFromItem() {
        try {
            CB_itemType.setValue(currentLineItem.getTypeString());
            LBL_id.setText(String.valueOf(currentLineItem.getId()));
            LBL_date.setText(currentLineItem.getDate() != null ? currentLineItem.getDate().format(DATE_FORMAT) : "N/A");
            LBL_category.setText(currentLineItem.getCategory());
            LBL_budget.setText(String.valueOf(currentLineItem.getBudget()));
            LBL_actual.setText(String.valueOf(currentLineItem.getActual()));
            TXTFIELD_startBal.setText(formatCurrencyInput(currentLineItem.getStartBal()));
            CHKBOX_hide.setSelected(currentLineItem.hide());
            LBL_balance.setText(formatCurrency(currentLineItem.getComputed()));
            resetChangeFlags();
        }
        catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error populating UI from item", e);
            Util.showErrorAlert(SAVE_ERROR_TITLE, "Error loading item data.");
        }
    }

    private void updateItemFromUI() {
        String selectedType = CB_itemType.getValue();
        boolean newHideStatus = CHKBOX_hide.isSelected();
        double newStartBal = parseStartBalance();

        currentLineItem.setType(selectedType);
        currentLineItem.hide(newHideStatus);
        currentLineItem.setStartBal(newStartBal);
    }

    // ========================= VALIDATION =========================

    private boolean validateInput() {
        if (!isValidStartBalance()) {
            Util.showErrorAlert(VALIDATION_ERROR_TITLE, "Please enter a valid starting balance (numeric value).");
            TXTFIELD_startBal.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidStartBalance() {
        try {
            parseStartBalance();
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidCurrencyInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return true; // Allow empty input
        }

        // Allow negative numbers, decimals, and commas
        return input.matches("-?\\d{1,3}(,\\d{3})*(\\.\\d{0,2})?") || input.matches("-?\\d+(\\.\\d{0,2})?");
    }

    private double parseStartBalance() {
        String text = TXTFIELD_startBal.getText().trim();
        if (text.isEmpty()) {
            return 0.0;
        }

        // Remove commas and parse
        return Double.parseDouble(text.replace(",", ""));
    }

    private void saveItemData() {
        Runnable taskSaveItemData = new Runnable() {
            @Override
            public void run() {
                WriteData.updateItemAcrossTables(currentLineItem);
            }
        };

        new Thread(taskSaveItemData).start();
    }

    // ========================= ASYNC OPERATIONS =========================

    private void saveItemDataAsync() {
    }

    private void saveItemDataAsync(boolean saveType) {
        Task<Boolean> saveTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                if (saveType) {
                    boolean result = WriteData.updateCategoryType(currentLineItem)
                            && WriteData.updateHideField(currentLineItem);
                    return result;
                }
                else {
                    return WriteData.updateLineItem(currentLineItem);
                }
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    boolean success = getValue();
                    if (success) {
                        handleSaveSuccess();
                    }
                    else {
                        handleSaveFailure("Database update returned false");
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> handleSaveFailure(getException().getMessage()));
            }
        };

        executorService.submit(saveTask);
    }

    private void handleSaveSuccess() {
        hasUnsavedDataChanges = false;
        hasUnsavedTypeChange = false;

        // Show brief success message
        Util.showInfoAlert("Success", SAVE_SUCCESS_MESSAGE);

        // Close window
        closeWindow();
    }

    private void handleSaveFailure(String errorMessage) {
        BTN_save.setDisable(false);
        LOGGER.log(Level.SEVERE, "Failed to save item: " + errorMessage);
        Util.showErrorAlert(SAVE_ERROR_TITLE, "Failed to save the item. Please try again.\n\nError: " + errorMessage);
    }

    // ========================= UI UTILITY METHODS =========================

    private void updateSaveButtonState() {
        BTN_save.setDisable(!(hasUnsavedDataChanges || hasUnsavedTypeChange));
    }

    private void resetChangeFlags() {
        hasUnsavedDataChanges = false;
        hasUnsavedTypeChange = false;
        updateSaveButtonState();
    }

    private void handleUnsavedChanges(Runnable onDiscard) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Unsaved Changes");
        alert.setHeaderText("You have unsaved changes");
        alert.setContentText("Do you want to discard your changes?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                hasUnsavedDataChanges = false;
                hasUnsavedTypeChange = false;
                onDiscard.run();
            }
        });
    }

    // ========================= FORMATTING UTILITIES =========================

    private String formatCurrency(Double value) {
        if (value == null) {
            return "$0.00";
        }
        return "$" + CURRENCY_FORMAT.format(value);
    }

    private String formatCurrencyInput(Double value) {
        if (value == null) {
            return "0.00";
        }
        return CURRENCY_FORMAT.format(value);
    }

    // ========================= PUBLIC API =========================

    /**
     * Gets the current item being edited.
     * 
     * @return The current LineItem, or null if none set
     */
    public LineItem getItem() {
        return currentLineItem;
    }

    public boolean wasItemModified() {
        return itemModified;
    }

    // ========================= CLEANUP =========================

    /**
     * Cleanup method to shutdown executor service. Should be called when the
     * controller is no longer needed.
     */
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            LOGGER.info("EditItemController cleanup completed");
        }
    }

    private void closeWindow() {
        try {
            Stage stage = (Stage) mainGridPane.getScene().getWindow();
            stage.close();
        }
        catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error closing window", e);
        }
    }
}