package com.budget.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.budget.dataModal.Categories;
import com.budget.dataModal.ReadData;
import com.budget.dataModal.WriteData;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

/**
 * Controller for the category editing window.
 * Provides functionality to edit category properties including type, parent, and visibility.
 */
public class SecondaryController {
    
    private static final Logger LOGGER = Logger.getLogger(SecondaryController.class.getName());
    
    // ========================= CONSTANTS =========================
    
    private static final String INCOME_TYPE = "Income";
    private static final String MANDATORY_TYPE = "Mandatory";
    private static final String DISCRETIONARY_TYPE = "Discretionary";
    
    // ========================= THREAD POOL =========================
    
    private final ExecutorService executorService = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.setName("SecondaryController-Worker");
        return t;
    });
    
    // ========================= FXML COMPONENTS =========================
    
    @FXML private TableColumn<Categories, Boolean> catColumnHide;
    @FXML private TableColumn<Categories, String> catColumnCategory;
    @FXML private TableColumn<Categories, String> catColumnParent;
    @FXML private TableColumn<Categories, Integer> catColumnType;
    @FXML private TableView<Categories> catTable;
    @FXML private Button btn_finishEdit;
    
    // ========================= DATA STRUCTURES =========================
    
    /** Type mapping for converting between integer types and display names */
    private final Map<Integer, String> typeMap = new HashMap<>();
    
    // ========================= INITIALIZATION =========================
    
    /**
     * Initializes the controller after FXML loading.
     */
    public void initialize() {
        try {
            LOGGER.info("Initializing SecondaryController");
            
            setupWindowCloseHandler();
            initializeTypeMap();
            setupTableColumns();
            loadCategoriesData();
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during SecondaryController initialization", e);
            showErrorAlert("Initialization Error", "Failed to initialize the category editor properly.");
        }
    }
    
    private void setupWindowCloseHandler() {
        Platform.runLater(() -> {
            try {
                Stage stage = (Stage) btn_finishEdit.getScene().getWindow();
                stage.setOnCloseRequest(event -> handleFinishEdit());
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error setting up window close handler", e);
            }
        });
    }
    
    private void initializeTypeMap() {
        typeMap.put(0, INCOME_TYPE);
        typeMap.put(1, MANDATORY_TYPE);
        typeMap.put(2, DISCRETIONARY_TYPE);
    }
    
    // ========================= EVENT HANDLERS =========================
    
    /**
     * Handles the finish edit button click.
     */
    @FXML
    void button_finishEdit(ActionEvent event) {
        handleFinishEdit();
    }
    
    private void handleFinishEdit() {
        try {
            // Validate any pending changes
            validatePendingChanges();
            
            // Close the window
            Stage stage = (Stage) btn_finishEdit.getScene().getWindow();
            stage.close();
             
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finishing edit", e);
            showErrorAlert("Save Error", "Error occurred while saving changes.");
        }
    }
    
    // ========================= TABLE COLUMN SETUP =========================
    
    private void setupTableColumns() {
        setupHideColumn();
        setupTypeColumn();
        setupParentColumn();
        setupCategoryColumn();
    }
    
    private void setupHideColumn() {
        catColumnHide.setCellValueFactory(new PropertyValueFactory<>("Hide"));
        catColumnHide.setCellFactory(this::createHideCheckBoxCell);
        catColumnHide.setSortable(true);
        catColumnHide.setPrefWidth(80);
    }
    
    private void setupTypeColumn() {
        catColumnType.setCellValueFactory(new PropertyValueFactory<>("Type"));
        catColumnType.setCellFactory(this::createTypeComboBoxCell);
        catColumnType.setSortable(true);
        catColumnType.setPrefWidth(120);
    }
    
    private void setupParentColumn() {
        catColumnParent.setCellValueFactory(new PropertyValueFactory<>("Parent"));
        catColumnParent.setCellFactory(TextFieldTableCell.forTableColumn());
        catColumnParent.setOnEditCommit(this::handleParentEdit);
        catColumnParent.setSortable(true);
        catColumnParent.setPrefWidth(150);
    }
    
    private void setupCategoryColumn() {
        catColumnCategory.setCellValueFactory(new PropertyValueFactory<>("Category"));
        catColumnCategory.setCellFactory(TextFieldTableCell.forTableColumn());
        catColumnCategory.setOnEditCommit(this::handleCategoryEdit);
        catColumnCategory.setSortable(true);
        catColumnCategory.setPrefWidth(200);
    }
    
    // ========================= CELL FACTORIES =========================
    
    private TableCell<Categories, Boolean> createHideCheckBoxCell(TableColumn<Categories, Boolean> column) {
        return new TableCell<Categories, Boolean>() {
            private CheckBox checkBox;
            
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                
                Categories category = getTableRow().getItem();
                
                if (checkBox == null) {
                    checkBox = new CheckBox();
                    setupCheckBoxListener(checkBox, category);
                }
                
                checkBox.setSelected(category.isHide());
                setGraphic(checkBox);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setAlignment(Pos.CENTER);
            }
            
            private void setupCheckBoxListener(CheckBox checkBox, Categories category) {
                checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null && category.isHide() != newValue) {
                        updateCategoryHideStatus(category, newValue);
                    }
                });
            }
        };
    }
    
    private TableCell<Categories, Integer> createTypeComboBoxCell(TableColumn<Categories, Integer> column) {
        return new TableCell<Categories, Integer>() {
            private ComboBox<String> comboBox;
            
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                
                Categories category = getTableRow().getItem();
                
                if (comboBox == null) {
                    comboBox = createTypeComboBox();
                    setupComboBoxListener(comboBox, category);
                }
                
                comboBox.getSelectionModel().select(category.getType());
                setGraphic(comboBox);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
            
            private ComboBox<String> createTypeComboBox() {
                ComboBox<String> cb = new ComboBox<>();
                cb.setItems(FXCollections.observableArrayList(getTypeDisplayNames()));
                cb.setPrefWidth(100);
                return cb;
            }
            
            private void setupComboBoxListener(ComboBox<String> comboBox, Categories category) {
                comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        Integer newTypeValue = getTypeKeyByValue(newValue);
                        if (newTypeValue != null && !newTypeValue.equals(category.getType())) {
                            updateCategoryType(category, newTypeValue);
                        }
                    }
                });
            }
        };
    }
    
    // ========================= EDIT COMMIT HANDLERS =========================
    
    private void handleParentEdit(TableColumn.CellEditEvent<Categories, String> event) {
        Categories category = event.getRowValue();
        String newParent = event.getNewValue();
        
        if (newParent != null && !newParent.equals(category.getParent())) {
            category.setParent(newParent);
            updateCategoryInDatabase(category, "parent");
        }
    }
    
    private void handleCategoryEdit(TableColumn.CellEditEvent<Categories, String> event) {
        Categories category = event.getRowValue();
        String newCategoryName = event.getNewValue();
        
        if (newCategoryName != null && !newCategoryName.trim().isEmpty() && 
            !newCategoryName.equals(category.getCategory())) {
            category.setCategory(newCategoryName.trim());
            updateCategoryInDatabase(category, "category name");
        }
    }
    
    // ========================= DATABASE UPDATE METHODS =========================
    
    private void updateCategoryHideStatus(Categories category, boolean hideStatus) {
        category.setHide(hideStatus);
        updateCategoryInDatabase(category, "hide status");
    }
    
    private void updateCategoryType(Categories category, Integer newType) {
        category.setType(newType);
        updateCategoryInDatabase(category, "type");
    }
    
    private void updateCategoryInDatabase(Categories category, String fieldName) {
        executeAsyncTask(
            () -> WriteData.categoryUpdate(category),
            () -> {
                // Refresh table if needed
                catTable.refresh();
            },
            "Error updating category " + fieldName + " for: " + category.getCategory()
        );
    }
    
    // ========================= DATA LOADING =========================
    
    private void loadCategoriesData() {
        executeAsyncTask(
            () -> ReadData.getCategories(),
            (categories) -> {
                catTable.setItems(FXCollections.observableArrayList(categories));
                LOGGER.info("Loaded " + categories.size() + " categories");
            },
            "Error loading categories from database"
        );
    }
    
    // ========================= UTILITY METHODS =========================
    
    private List<String> getTypeDisplayNames() {
        List<String> typeNames = new ArrayList<>();
        for (int i = 0; i < typeMap.size(); i++) {
            typeNames.add(typeMap.get(i));
        }
        return typeNames;
    }
    
    private Integer getTypeKeyByValue(String value) {
        return typeMap.entrySet().stream()
            .filter(entry -> entry.getValue().equals(value))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(null);
    }
    
    private void validatePendingChanges() {
        // Check if table is in edit mode and commit any pending edits
        if (catTable.getEditingCell() != null) {
            catTable.getColumns().forEach(column -> {
                if (column.isEditable()) {
                    catTable.edit(-1, null); // Cancel any ongoing edits
                }
            });
        }
    }
    
    // ========================= ASYNC TASK UTILITIES =========================
    
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
    
    private <T> void executeAsyncTask(java.util.concurrent.Callable<T> backgroundTask, 
                                     java.util.function.Consumer<T> uiTask, String errorMessage) {
        Task<T> task = new Task<T>() {
            @Override
            protected T call() throws Exception {
                return backgroundTask.call();
            }
            
            @Override
            protected void succeeded() {
                if (uiTask != null) {
                    Platform.runLater(() -> uiTask.accept(getValue()));
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
    
    // ========================= PUBLIC API =========================
    
    /**
     * Refreshes the categories table with latest data from database.
     */
    public void refreshCategories() {
        loadCategoriesData();
    }
    
    /**
     * Gets the currently selected category.
     * 
     * @return The selected category, or null if none selected
     */
    public Categories getSelectedCategory() {
        return catTable.getSelectionModel().getSelectedItem();
    }
    
    /**
     * Sets the selection to a specific category.
     * 
     * @param category The category to select
     */
    public void selectCategory(Categories category) {
        if (category != null) {
            catTable.getSelectionModel().select(category);
            catTable.scrollTo(category);
        }
    }
    
    // ========================= CLEANUP =========================
    
    /**
     * Cleanup method to shutdown executor service.
     * Should be called when the controller is no longer needed.
     */
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            LOGGER.info("SecondaryController cleanup completed");
        }
    }
}