package com.budget.controllers;

import java.util.logging.Logger;

import com.budget.Util;
import com.budget.dataModal.LineItem;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Extension helper class for PrimaryController that handles table configuration.
 * This class provides centralized table column setup and formatting for the total tables.
 */
public final class PrimaryControllerExtend {
    
    private static final Logger LOGGER = Logger.getLogger(PrimaryControllerExtend.class.getName());
    
    // ========================= INSTANCE VARIABLES =========================
    
    private final TableView<LineItem> tableViewTotal;
    private final TableColumn<LineItem, String> totalTableCategory;
    private final TableColumn<LineItem, Double> totalTableActual;
    private final TableColumn<LineItem, Double> totalTableBudget;
    private final TableColumn<LineItem, Double> totalTableDiff;
    
    // ========================= CONSTRUCTOR =========================
    
    /**
     * Creates a new PrimaryControllerExtend with the specified table components.
     * Automatically initializes the table columns upon construction.
     * 
     * @param tableViewTotal The total table view
     * @param totalTableCategory The category column
     * @param totalTableActual The actual amount column
     * @param totalTableBudget The budget amount column
     * @param totalTableDiff The difference amount column
     * @throws IllegalArgumentException if any parameter is null
     */
    public PrimaryControllerExtend(TableView<LineItem> tableViewTotal,
                                 TableColumn<LineItem, String> totalTableCategory, 
                                 TableColumn<LineItem, Double> totalTableActual,
                                 TableColumn<LineItem, Double> totalTableBudget, 
                                 TableColumn<LineItem, Double> totalTableDiff) {
        
        // Validate inputs
        validateTableComponents(tableViewTotal, totalTableCategory, totalTableActual, totalTableBudget, totalTableDiff);
        
        // Assign final fields
        this.tableViewTotal = tableViewTotal;
        this.totalTableCategory = totalTableCategory;
        this.totalTableActual = totalTableActual;
        this.totalTableBudget = totalTableBudget;
        this.totalTableDiff = totalTableDiff;
        
        // Initialize table configuration
        initialize();
        
        LOGGER.info("PrimaryControllerExtend initialized successfully");
    }
    
    // ========================= INITIALIZATION METHODS =========================
    
    /**
     * Initializes all table columns with proper cell value factories and formatting.
     */
    private void initialize() {
        try {
            setupCategoryColumn();
            setupNumericColumns();
            configureTableAppearance();
            
            LOGGER.fine("Table columns configured successfully");
            
        } catch (Exception e) {
            LOGGER.severe("Error initializing table columns: " + e.getMessage());
            throw new RuntimeException("Failed to initialize table columns", e);
        }
    }
    
    /**
     * Sets up the category column with proper cell value factory.
     */
    private void setupCategoryColumn() {
        totalTableCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        
        // Make category column non-sortable for totals table
        totalTableCategory.setSortable(false);
        
        // Set preferred width for category column
        totalTableCategory.setPrefWidth(150);
    }
    
    /**
     * Sets up all numeric columns (actual, budget, diff) with currency formatting.
     */
    private void setupNumericColumns() {
        setupNumericColumn(totalTableActual, "actual");
        setupNumericColumn(totalTableBudget, "budget");
        setupNumericColumn(totalTableDiff, "diff");
    }
    
    /**
     * Sets up a single numeric column with proper formatting and alignment.
     * 
     * @param column The column to setup
     * @param propertyName The property name for the cell value factory
     */
    private void setupNumericColumn(TableColumn<LineItem, Double> column, String propertyName) {
        // Set cell value factory
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        
        // Apply currency formatting and right alignment
        column.setCellFactory(Util.getRightAlignedCellFactory(Util.getCurrencyConverter()));
        
        // Configure column properties
        column.setSortable(false);
        column.setPrefWidth(100);
        column.setMinWidth(80);
    }
    
    /**
     * Configures general table appearance and behavior.
     */
    private void configureTableAppearance() {
        if (tableViewTotal != null) {
            // Disable column reordering for totals table
            tableViewTotal.getColumns().forEach(column -> column.setReorderable(false));
            
            // Set table selection mode
            tableViewTotal.getSelectionModel().setCellSelectionEnabled(false);
            
            // Add style class for total tables
            tableViewTotal.getStyleClass().add("total-table");
            
            // Set fixed height for total table (assuming single row usage)
            tableViewTotal.setMaxHeight(50);
            tableViewTotal.setPrefHeight(50);
        }
    }
    
    // ========================= VALIDATION METHODS =========================
    
    /**
     * Validates that all table components are non-null.
     * 
     * @param tableView The table view to validate
     * @param categoryColumn The category column to validate
     * @param actualColumn The actual column to validate
     * @param budgetColumn The budget column to validate
     * @param diffColumn The diff column to validate
     * @throws IllegalArgumentException if any component is null
     */
    private void validateTableComponents(TableView<LineItem> tableView,
                                       TableColumn<LineItem, String> categoryColumn,
                                       TableColumn<LineItem, Double> actualColumn,
                                       TableColumn<LineItem, Double> budgetColumn,
                                       TableColumn<LineItem, Double> diffColumn) {
        
        if (tableView == null) {
            throw new IllegalArgumentException("TableView cannot be null");
        }
        if (categoryColumn == null) {
            throw new IllegalArgumentException("Category column cannot be null");
        }
        if (actualColumn == null) {
            throw new IllegalArgumentException("Actual column cannot be null");
        }
        if (budgetColumn == null) {
            throw new IllegalArgumentException("Budget column cannot be null");
        }
        if (diffColumn == null) {
            throw new IllegalArgumentException("Diff column cannot be null");
        }
    }
    
    // ========================= PUBLIC UTILITY METHODS =========================
    
    /**
     * Refreshes all column formatting and configurations.
     * Useful when table properties need to be reapplied.
     */
    public void refreshColumnConfiguration() {
        try {
            setupNumericColumns();
            configureTableAppearance();
            LOGGER.fine("Column configuration refreshed");
        } catch (Exception e) {
            LOGGER.severe("Error refreshing column configuration: " + e.getMessage());
        }
    }
    
    /**
     * Updates column widths based on content.
     * 
     * @param categoryWidth Width for category column
     * @param numericWidth Width for numeric columns
     */
    public void updateColumnWidths(double categoryWidth, double numericWidth) {
        if (categoryWidth > 0) {
            totalTableCategory.setPrefWidth(categoryWidth);
        }
        
        if (numericWidth > 0) {
            totalTableActual.setPrefWidth(numericWidth);
            totalTableBudget.setPrefWidth(numericWidth);
            totalTableDiff.setPrefWidth(numericWidth);
        }
        
        LOGGER.fine("Column widths updated: category=" + categoryWidth + ", numeric=" + numericWidth);
    }
    
    /**
     * Enables or disables column sorting for all columns.
     * 
     * @param sortable true to enable sorting, false to disable
     */
    public void setColumnsSortable(boolean sortable) {
        totalTableCategory.setSortable(sortable);
        totalTableActual.setSortable(sortable);
        totalTableBudget.setSortable(sortable);
        totalTableDiff.setSortable(sortable);
        
        LOGGER.fine("Column sorting set to: " + sortable);
    }
    
    /**
     * Applies a custom style class to all columns.
     * 
     * @param styleClass The CSS style class to apply
     */
    public void applyColumnStyleClass(String styleClass) {
        if (styleClass != null && !styleClass.trim().isEmpty()) {
            totalTableCategory.getStyleClass().add(styleClass);
            totalTableActual.getStyleClass().add(styleClass);
            totalTableBudget.getStyleClass().add(styleClass);
            totalTableDiff.getStyleClass().add(styleClass);
            
            LOGGER.fine("Style class applied: " + styleClass);
        }
    }
    
    // ========================= GETTERS =========================
    
    /**
     * Gets the table view instance.
     * 
     * @return The table view
     */
    public TableView<LineItem> getTableView() {
        return tableViewTotal;
    }
    
    /**
     * Gets the category column instance.
     * 
     * @return The category column
     */
    public TableColumn<LineItem, String> getCategoryColumn() {
        return totalTableCategory;
    }
    
    /**
     * Gets the actual amount column instance.
     * 
     * @return The actual column
     */
    public TableColumn<LineItem, Double> getActualColumn() {
        return totalTableActual;
    }
    
    /**
     * Gets the budget amount column instance.
     * 
     * @return The budget column
     */
    public TableColumn<LineItem, Double> getBudgetColumn() {
        return totalTableBudget;
    }
    
    /**
     * Gets the difference amount column instance.
     * 
     * @return The diff column
     */
    public TableColumn<LineItem, Double> getDiffColumn() {
        return totalTableDiff;
    }
    
    // ========================= STATIC FACTORY METHODS =========================
    
    /**
     * Creates a new PrimaryControllerExtend instance with default configuration.
     * 
     * @param tableView The table view to configure
     * @param categoryColumn The category column
     * @param actualColumn The actual column
     * @param budgetColumn The budget column
     * @param diffColumn The diff column
     * @return A new configured PrimaryControllerExtend instance
     */
    public static PrimaryControllerExtend createWithDefaults(TableView<LineItem> tableView,
                                                           TableColumn<LineItem, String> categoryColumn,
                                                           TableColumn<LineItem, Double> actualColumn,
                                                           TableColumn<LineItem, Double> budgetColumn,
                                                           TableColumn<LineItem, Double> diffColumn) {
        return new PrimaryControllerExtend(tableView, categoryColumn, actualColumn, budgetColumn, diffColumn);
    }
    
    /**
     * Creates a new PrimaryControllerExtend instance with custom column widths.
     * 
     * @param tableView The table view to configure
     * @param categoryColumn The category column
     * @param actualColumn The actual column
     * @param budgetColumn The budget column
     * @param diffColumn The diff column
     * @param categoryWidth Width for the category column
     * @param numericWidth Width for numeric columns
     * @return A new configured PrimaryControllerExtend instance
     */
    public static PrimaryControllerExtend createWithCustomWidths(TableView<LineItem> tableView,
                                                               TableColumn<LineItem, String> categoryColumn,
                                                               TableColumn<LineItem, Double> actualColumn,
                                                               TableColumn<LineItem, Double> budgetColumn,
                                                               TableColumn<LineItem, Double> diffColumn,
                                                               double categoryWidth,
                                                               double numericWidth) {
        PrimaryControllerExtend extender = new PrimaryControllerExtend(tableView, categoryColumn, actualColumn, budgetColumn, diffColumn);
        extender.updateColumnWidths(categoryWidth, numericWidth);
        return extender;
    }
    
    // ========================= TOSTRING FOR DEBUGGING =========================
    
    @Override
    public String toString() {
        return String.format("PrimaryControllerExtend{table=%s, columns=[category, actual, budget, diff]}",
                           tableViewTotal != null ? tableViewTotal.getClass().getSimpleName() : "null");
    }
}