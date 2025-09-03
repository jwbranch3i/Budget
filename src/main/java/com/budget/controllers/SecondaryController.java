package com.budget.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.budget.dataModel.Categories;
import com.budget.dataModel.ReadData;
import com.budget.dataModel.WriteData;

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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.stage.Stage;

/**
 * Controller for the category editing window. Provides functionality to edit
 * category properties including type, parent, and visibility.
 */
public class SecondaryController {

    private static final Logger LOGGER = Logger.getLogger(SecondaryController.class.getName());

    // ========================= CONSTANTS =========================

    private static final String INCOME_TYPE = "Income";
    private static final String MANDATORY_TYPE = "Mandatory";
    private static final String DISCRETIONARY_TYPE = "Discretionary";

    // ========================= FIELDS =========================

    // ========================= THREAD POOL =========================

    private final ExecutorService executorService = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.setName("SecondaryController-Worker");
        return t;
    });

    // ========================= FXML COMPONENTS =========================

    @FXML
    private TreeTableColumn<Categories, Boolean> catColumnHide;
    @FXML
    private TreeTableColumn<Categories, String> catColumnCategory;
    @FXML
    private TreeTableColumn<Categories, String> catColumnParent;
    @FXML
    private TreeTableColumn<Categories, Integer> catColumnType;
    @FXML
    private TreeTableView<Categories> catTable;
    @FXML
    private Button btn_finishEdit;

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
            loadCategoryTreeData();

        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during SecondaryController initialization", e);
            showErrorAlert("Initialization Error", "Failed to initialize the category editor properly.");
        }

    }

    private void setupWindowCloseHandler() {
        // Wait for the button to be attached to a scene and window
        btn_finishEdit.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                // Once we have a scene, get its window
                newScene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        // Now we can safely set the close handler
                        Stage stage = (Stage) newWindow;
                        stage.setOnCloseRequest(event -> handleFinishEdit());
                        LOGGER.info("Window close handler setup completed");
                    }
                });
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

        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finishing edit", e);
            showErrorAlert("Save Error", "Error occurred while saving changes.");
        }
    }

    // ========================= TABLE COLUMN SETUP =========================

    private void setupTableColumns() {
        setupHideColumn();
        setupTypeColumn();
        // setupParentColumn();
        // setupCategoryColumn();

        // Hide the root node
        catTable.setShowRoot(false);
        catTable.setEditable(true);
    }

    private void setupHideColumn() {
        catColumnHide.setCellValueFactory(new TreeItemPropertyValueFactory<>("hide"));
        catColumnHide.setCellFactory(this::createHideCheckBoxCell);
        catColumnHide.setSortable(true);
        catColumnHide.setPrefWidth(80);
    }

    private void setupTypeColumn() {
        catColumnType.setCellValueFactory(new TreeItemPropertyValueFactory<>("Type"));
        catColumnType.setCellFactory(this::createTypeComboBoxCell);
        catColumnType.setSortable(true);
        catColumnType.setPrefWidth(120);

        catColumnParent.setCellValueFactory(new TreeItemPropertyValueFactory<>("Parent"));
        catColumnParent.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        catColumnParent.setOnEditCommit(this::handleParentEdit);
        catColumnParent.setSortable(true);
        catColumnParent.setPrefWidth(150);
        catColumnParent.setPrefWidth(150);

        catColumnCategory.setCellValueFactory(new TreeItemPropertyValueFactory<>("Category"));
        catColumnCategory.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        catColumnCategory.setOnEditCommit(this::handleCategoryEdit);
        catColumnCategory.setSortable(true);
        catColumnCategory.setPrefWidth(200);
        catColumnCategory.setPrefWidth(200);
    }

    // ========================= CELL FACTORIES =========================

    private TreeTableCell<Categories, Boolean> createHideCheckBoxCell(TreeTableColumn<Categories, Boolean> column) {
        return new TreeTableCell<Categories, Boolean>() {
            private CheckBox checkBox;

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                // FIX: Use getTreeTableRow() instead of getTableRow()
                TreeItem<Categories> treeItem = getTreeTableView() != null ? getTreeTableView().getTreeItem(getIndex())
                        : null;
                if (empty || treeItem == null || treeItem.getValue() == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                Categories category = treeItem.getValue();

                // Only show checkbox for root items (no parent)
                if (isRootItem(treeItem)) {
                    if (checkBox == null) {
                        checkBox = new CheckBox();
                        setupCheckBoxListener(checkBox, category);
                    }

                    checkBox.setSelected(category.isHide());
                    setGraphic(checkBox);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setAlignment(Pos.CENTER);
                }
                else {
                    setGraphic(null);
                    setText(null);
                }
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

    private TreeTableCell<Categories, Integer> createTypeComboBoxCell(TreeTableColumn<Categories, Integer> column) {
        return new TreeTableCell<Categories, Integer>() {
            private ComboBox<String> comboBox;

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                TreeItem<Categories> treeItem = getTreeTableView() != null ? getTreeTableView().getTreeItem(getIndex())
                        : null;
                if (empty || treeItem == null || treeItem.getValue() == null) {
                    setGraphic(null);
                    return;
                }

                Categories category = treeItem.getValue();

                // Only show combo box for root items (no parent)
                if (isRootItem(treeItem)) {
                    if (comboBox == null) {
                        comboBox = createTypeComboBox();
                        setupComboBoxListener(comboBox, category);
                    }

                    comboBox.getSelectionModel().select(category.getType());
                    setGraphic(comboBox);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                }
                else {
                    setGraphic(null);
                    setText(typeMap.get(category.getType())); // Show as text
                                                              // for child items
                }
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

    private void handleParentEdit(TreeTableColumn.CellEditEvent<Categories, String> event) {
        Categories category = event.getRowValue().getValue();
        String newParent = event.getNewValue();

        if (newParent != null && !newParent.equals(category.getParent())) {
            category.setParent(newParent);
            updateCategoryInDatabase(category, "parent");
            // Reload data to rebuild tree structure
        }
    }

    private void handleCategoryEdit(TreeTableColumn.CellEditEvent<Categories, String> event) {
        Categories category = event.getRowValue().getValue();
        String newCategoryName = event.getNewValue();

        if (newCategoryName != null && !newCategoryName.trim().isEmpty()
                && !newCategoryName.equals(category.getCategory())) {
            category.setCategory(newCategoryName.trim());
            updateCategoryInDatabase(category, "category name");
        }
    }

    // ========================= DATABASE UPDATE METHODS
    // =========================

    private void updateCategoryHideStatus(Categories category, boolean hideStatus) {
        category.setHide(hideStatus);
        updateCategoryInDatabase(category, "hide");
    }

    private void updateCategoryType(Categories category, Integer newType) {
        category.setType(newType);
        updateCategoryInDatabase(category, "type");
    }

    private void updateCategoryInDatabase(Categories category, String fieldName) {
        executeAsyncTask(() -> WriteData.categoryUpdate(category), () -> {
            // Refresh table if needed
            catTable.refresh();
        }, "Error updating category " + fieldName + " for: " + category.getCategory());
    }

    // ========================= DATA LOADING =========================

    private void loadCategoryTreeData() {
        Task<Void> initTask = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                                return null; // Data processing would go here
                        }
                        @Override
                        protected void succeeded() {
                            Platform.runLater(() -> {
                                updateTables();
                            });

                        }

                        @Override
                        protected void failed() {
                                LOGGER.log(Level.SEVERE, "Error loading categories", getException());
                                showErrorAlert("Data Load Error", "Failed to load categories: " + getException().getMessage());
                        }

        };    
        executorService.submit(initTask);           
    }



    //     executeAsyncTask(() -> {
    //         // Background thread - just load data
    //         return ReadData.getCategories();
    //     }, (categories) -> {
    //         // UI thread - build and display tree
    //         try {
    //             // Remove the extra Platform.runLater - it's not needed here
    //             // since executeAsyncTask already handles UI thread switching
    //             TreeItem<Categories> root = buildCategoryTreeStructure(categories);
    //             catTable.setRoot(root);
    //             catTable.setShowRoot(false);
    //             expandAllNodes(root);
    //             LOGGER.info("Loaded " + categories.size() + " categories in tree structure");
    //         } catch (Exception e) {
    //             LOGGER.log(Level.SEVERE, "Error building category tree", e);
    //             showErrorAlert("Tree Error", "Failed to build category tree: " + e.getMessage());
    //         }
    //     }, "Error loading categories");
    // }


    /** Updates tables with latest data */
    public void updateTables() {
        executeAsyncTask(null, this::loadCategoryTask,
                "Error updating category tables");
    }


    /** Update Category table data on UI thread. */
    public void loadCategoryTask() {
        readFromDatabase();
    }

    /** Reads data from database for Categories */
    public void readFromDatabase() {
             CompletableFuture.supplyAsync(() -> loadDatabaseData(), executorService).thenAcceptAsync(data -> {
                        Platform.runLater(() -> {
                            catTable.setRoot(data);
                        });
                }).exceptionally(throwable -> {
                        LOGGER.log(Level.SEVERE, "Error reading data from database", throwable);
                        Platform.runLater(() -> {
                                showErrorAlert("Database Error",
                                                "Failed to load data from database: " + throwable.getMessage());
                        });
                        return null;
                });   
    }

private TreeItem<Categories> loadDatabaseData() {
    return ReadData.getCategories();
}



    private void expandAllNodes(TreeItem<?> item) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(true);
            for (TreeItem<?> child : item.getChildren()) {
                expandAllNodes(child);
            }
        }
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
        return typeMap.entrySet().stream().filter(entry -> entry.getValue().equals(value)).map(Map.Entry::getKey)
                .findFirst().orElse(null);
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
     * Gets the currently selected category.
     * 
     * @return The selected category, or null if none selected
     */
    public Categories getSelectedCategory() {
        TreeItem<Categories> selectedItem = catTable.getSelectionModel().getSelectedItem();
        return selectedItem != null ? selectedItem.getValue() : null;
    }

    /**
     * Sets the selection to a specific category.
     * 
     * @param category The category to select
     */
    public void selectCategory(Categories category) {
        if (category != null) {
            TreeItem<Categories> itemToSelect = findTreeItem(catTable.getRoot(), category);
            if (itemToSelect != null) {
                catTable.getSelectionModel().select(itemToSelect);
                catTable.scrollTo(catTable.getRow(itemToSelect));
            }
        }
    }

    private TreeItem<Categories> findTreeItem(TreeItem<Categories> root, Categories target) {
        if (root == null || target == null) {
            return null;
        }

        if (root.getValue() != null && root.getValue().equals(target)) {
            return root;
        }

        for (TreeItem<Categories> child : root.getChildren()) {
            TreeItem<Categories> found = findTreeItem(child, target);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    // ========================= CLEANUP =========================

    /**
     * Cleanup method to shutdown executor service. Should be called when the
     * controller is no longer needed.
     */
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            LOGGER.info("SecondaryController cleanup completed");
        }
    }

    private boolean isRootItem(TreeItem<Categories> treeItem) {
        if (treeItem == null || treeItem.getValue() == null) {
            return false;
        }

        Categories category = treeItem.getValue();
        return category.getParent() == null || category.getParent().trim().isEmpty();
    }

}