package com.budget.controllers;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.budget.dataModal.LineItem;
import com.budget.dataModal.WriteData;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Controller for the item editing dialog.
 * Allows editing of line item properties such as starting balance and include status.
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
    
    @FXML private GridPane gridPane;
    @FXML private Label id_LBL;
    @FXML private Label Category_LBL;
    @FXML private Label actual_LBL;
    @FXML private Label budget_LBL;
    @FXML private Label computed_LBL;
    @FXML private Label date_LBL;
    @FXML private Button btn_Cancel;
    @FXML private Button btn_Save;
    @FXML private CheckBox chkbox_include;
    @FXML private TextField startBal_Field;
    
    // ========================= INSTANCE VARIABLES =========================
    
    private LineItem item;
    private boolean hasUnsavedChanges = false;
    
    // ========================= INITIALIZATION =========================
    
    /**
     * Initializes the controller after FXML loading.
     */
    @FXML
    public void initialize() {
        try {
            LOGGER.info("Initializing EditItemController");
            
            setupEventHandlers();
            setupInputValidation();
            setupKeyboardShortcuts();
            
            LOGGER.fine("EditItemController initialization completed");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during EditItemController initialization", e);
            showErrorAlert(SAVE_ERROR_TITLE, "Failed to initialize the edit dialog properly.");
        }
    }
    
    private void setupEventHandlers() {
        // Track changes to enable/disable save button
        setupChangeTracking();
        
        // Setup window close handler
        Platform.runLater(this::setupWindowCloseHandler);
    }
    
    private void setupChangeTracking() {
        startBal_Field.textProperty().addListener((observable, oldValue, newValue) -> {
            hasUnsavedChanges = true;
            updateSaveButtonState();
        });
        
        chkbox_include.selectedProperty().addListener((observable, oldValue, newValue) -> {
            hasUnsavedChanges = true;
            updateSaveButtonState();
        });
    }
    
    private void setupInputValidation() {
        // Allow only numeric input for start balance field
        startBal_Field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isValidCurrencyInput(newValue)) {
                startBal_Field.setText(oldValue);
            }
        });
    }
    
    private void setupKeyboardShortcuts() {
        // Enter key saves, Escape cancels
        Platform.runLater(() -> {
            if (gridPane.getScene() != null) {
                gridPane.getScene().setOnKeyPressed(event -> {
                    switch (event.getCode()) {
                        case ENTER:
                            if (btn_Save.isDisabled() == false) {
                                button_saveStartBal(new ActionEvent());
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
            Stage stage = (Stage) gridPane.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                if (hasUnsavedChanges) {
                    event.consume(); // Prevent immediate close
                    handleUnsavedChanges(() -> stage.close());
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error setting up window close handler", e);
        }
    }
    
    // ========================= EVENT HANDLERS =========================
    
    /**
     * Handles the cancel button click.
     */
    @FXML
    void button_cancel(ActionEvent event) {
        if (hasUnsavedChanges) {
            handleUnsavedChanges(this::closeWindow);
        } else {
            closeWindow();
        }
    }
    
    /**
     * Handles the save button click.
     */
    @FXML
    void button_saveStartBal(ActionEvent event) {
        if (!validateInput()) {
            return;
        }
        
        try {
            // Update item with new values
            updateItemFromUI();
            
            // Disable save button during save operation
            btn_Save.setDisable(true);
            
            // Save asynchronously
            saveItemAsync();
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error preparing item for save", e);
            showErrorAlert(SAVE_ERROR_TITLE, "Error occurred while preparing to save the item.");
            btn_Save.setDisable(false);
        }
    }
    
    // ========================= ITEM MANAGEMENT =========================
    
    /**
     * Sets the item to be edited and populates the UI fields.
     * 
     * @param editItem The LineItem to edit
     * @throws IllegalArgumentException if editItem is null
     */
    public void setItem(LineItem editItem) {
        if (editItem == null) {
            throw new IllegalArgumentException("Edit item cannot be null");
        }
        
        this.item = editItem;
        populateUIFromItem();
        hasUnsavedChanges = false;
        updateSaveButtonState();
        
        LOGGER.info("Item set for editing: " + item.getCategory() + " (ID: " + item.getId() + ")");
    }
    
    private void populateUIFromItem() {
        try {
            id_LBL.setText(String.valueOf(item.getId()));
            Category_LBL.setText(item.getCategory());
            actual_LBL.setText(formatCurrency(item.getActual()));
            budget_LBL.setText(formatCurrency(item.getBudget()));
            computed_LBL.setText(formatCurrency(item.getComputed()));
            date_LBL.setText(item.getDate() != null ? item.getDate().format(DATE_FORMAT) : "N/A");
            startBal_Field.setText(formatCurrencyInput(item.getStartBal()));
            chkbox_include.setSelected(item.includeInTotal());
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error populating UI from item", e);
            showErrorAlert(SAVE_ERROR_TITLE, "Error loading item data.");
        }
    }
    
    private void updateItemFromUI() {
        double newStartBal = parseStartBalance();
        boolean newIncludeStatus = chkbox_include.isSelected();
        
        item.setStartBal(newStartBal);
        item.includeInTotal(newIncludeStatus);
        
        LOGGER.fine("Item updated from UI - StartBal: " + newStartBal + ", Include: " + newIncludeStatus);
    }
    
    // ========================= VALIDATION =========================
    
    private boolean validateInput() {
        if (!isValidStartBalance()) {
            showErrorAlert(VALIDATION_ERROR_TITLE, 
                         "Please enter a valid starting balance (numeric value).");
            startBal_Field.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private boolean isValidStartBalance() {
        try {
            parseStartBalance();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean isValidCurrencyInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return true; // Allow empty input
        }
        
        // Allow negative numbers, decimals, and commas
        return input.matches("-?\\d{1,3}(,\\d{3})*(\\.\\d{0,2})?") || 
               input.matches("-?\\d+(\\.\\d{0,2})?");
    }
    
    private double parseStartBalance() {
        String text = startBal_Field.getText().trim();
        if (text.isEmpty()) {
            return 0.0;
        }
        
        // Remove commas and parse
        return Double.parseDouble(text.replace(",", ""));
    }
    
    // ========================= ASYNC OPERATIONS =========================
    
    private void saveItemAsync() {
        Task<Boolean> saveTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return WriteData.actualUpdate(item);
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    boolean success = getValue();
                    if (success) {
                        handleSaveSuccess();
                    } else {
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
        hasUnsavedChanges = false;
        LOGGER.info("Item saved successfully: " + item.getCategory());
        
        // Show brief success message
        showInfoAlert("Success", SAVE_SUCCESS_MESSAGE);
        
        // Close window
        closeWindow();
    }
    
    private void handleSaveFailure(String errorMessage) {
        btn_Save.setDisable(false);
        LOGGER.log(Level.SEVERE, "Failed to save item: " + errorMessage);
        showErrorAlert(SAVE_ERROR_TITLE, 
                      "Failed to save the item. Please try again.\n\nError: " + errorMessage);
    }
    
    // ========================= UI UTILITY METHODS =========================
    
    private void updateSaveButtonState() {
        btn_Save.setDisable(!hasUnsavedChanges || !isValidStartBalance());
    }
    
    private void handleUnsavedChanges(Runnable onDiscard) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Unsaved Changes");
        alert.setHeaderText("You have unsaved changes");
        alert.setContentText("Do you want to discard your changes?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                hasUnsavedChanges = false;
                onDiscard.run();
            }
        });
    }
    
    private void closeWindow() {
        try {
            Stage stage = (Stage) btn_Cancel.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error closing window", e);
        }
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
    
    // ========================= ALERT UTILITIES =========================
    
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show(); // Non-blocking
    }
    
    // ========================= PUBLIC API =========================
    
    /**
     * Gets the current item being edited.
     * 
     * @return The current LineItem, or null if none set
     */
    public LineItem getItem() {
        return item;
    }
    
    /**
     * Checks if there are unsaved changes.
     * 
     * @return true if there are unsaved changes, false otherwise
     */
    public boolean hasUnsavedChanges() {
        return hasUnsavedChanges;
    }
    
    /**
     * Programmatically saves the current changes.
     * 
     * @return true if save was initiated successfully, false otherwise
     */
    public boolean save() {
        if (validateInput()) {
            button_saveStartBal(new ActionEvent());
            return true;
        }
        return false;
    }
    
    // ========================= CLEANUP =========================
    
    /**
     * Cleanup method to shutdown executor service.
     * Should be called when the controller is no longer needed.
     */
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            LOGGER.info("EditItemController cleanup completed");
        }
    }
}