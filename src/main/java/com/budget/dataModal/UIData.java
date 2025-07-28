package com.budget.dataModal;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

/**
 * Utility class for managing UI data operations and table total calculations.
 * Provides methods for updating table totals and handling different table types.
 */
public final class UIData {
    
    private static final Logger LOGGER = Logger.getLogger(UIData.class.getName());
    
    // ========================= CONSTANTS =========================
    
    /** Category type constants */
    private static final int INCOME_TYPE = 0;
    private static final int MANDATORY_TYPE = 1;
    private static final int DISCRETIONARY_TYPE = 2;
    
    /** Category display names */
    private static final String INCOME_TOTAL_LABEL = "Total Income";
    private static final String MANDATORY_TOTAL_LABEL = "Total Mandatory";
    private static final String DISCRETIONARY_TOTAL_LABEL = "Total Discretionary";
    private static final String OVERALL_TOTAL_LABEL = "Total";
    
    // Prevent instantiation
    private UIData() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    // ========================= PUBLIC API METHODS =========================
    
    /**
     * Updates table totals using an ArrayList of tables.
     * 
     * @param tables ArrayList containing [income, mandatory, discretionary, total] tables
     * @throws IllegalArgumentException if tables list doesn't have exactly 4 elements
     */
    public static void updateTableTotal(ArrayList<TableView<LineItem>> tables) {
        validateTablesInput(tables);
        updateTableTotal(tables.get(0), tables.get(1), tables.get(2), tables.get(3));
    }
    
    /**
     * Updates table totals for income, mandatory, discretionary, and overall totals.
     * Handles both TableView and TreeTableView types for flexible UI support.
     * 
     * @param incomeTable The income table (TableView)
     * @param mandatoryTable The mandatory table (TableView or TreeTableView)
     * @param discretionaryTable The discretionary table (TableView)
     * @param totalTable The totals table where results are displayed
     */
    public static void updateTableTotal(Object incomeTable, Object mandatoryTable, 
                                      Object discretionaryTable, TableView<LineItem> totalTable) {
        
        try {
            // Validate inputs
            validateTableInputs(incomeTable, mandatoryTable, discretionaryTable, totalTable);
            
            // Clear existing totals
            totalTable.getItems().clear();
            
            // Extract data from each table type
            List<LineItem> incomeItems = extractTableViewItems(incomeTable);
            List<LineItem> mandatoryItems = extractItems(mandatoryTable);
            List<LineItem> discretionaryItems = extractTableViewItems(discretionaryTable);
            
            // Calculate totals for each category
            LineItem incomeTotal = calculateCategoryTotal(incomeItems, INCOME_TOTAL_LABEL, INCOME_TYPE);
            LineItem mandatoryTotal = calculateCategoryTotal(mandatoryItems, MANDATORY_TOTAL_LABEL, MANDATORY_TYPE);
            LineItem discretionaryTotal = calculateCategoryTotal(discretionaryItems, DISCRETIONARY_TOTAL_LABEL, DISCRETIONARY_TYPE);
            
            // Calculate overall total (income - expenses)
            LineItem overallTotal = calculateOverallTotal(incomeTotal, mandatoryTotal, discretionaryTotal);
            
            // Add all totals to the total table
            totalTable.getItems().addAll(List.of(incomeTotal, mandatoryTotal, discretionaryTotal, overallTotal));
            
            LOGGER.fine("Table totals updated successfully");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating table totals", e);
            // Ensure total table is cleared even if calculation fails
            if (totalTable != null) {
                totalTable.getItems().clear();
            }
        }
    }
    
    // ========================= PRIVATE HELPER METHODS =========================
    
    /**
     * Validates the tables ArrayList input.
     */
    private static void validateTablesInput(ArrayList<TableView<LineItem>> tables) {
        if (tables == null) {
            throw new IllegalArgumentException("Tables list cannot be null");
        }
        if (tables.size() != 4) {
            throw new IllegalArgumentException("Tables list must contain exactly 4 elements: [income, mandatory, discretionary, total]");
        }
        for (int i = 0; i < tables.size(); i++) {
            if (tables.get(i) == null) {
                throw new IllegalArgumentException("Table at index " + i + " cannot be null");
            }
        }
    }
    
    /**
     * Validates individual table inputs.
     */
    private static void validateTableInputs(Object incomeTable, Object mandatoryTable, 
                                          Object discretionaryTable, TableView<LineItem> totalTable) {
        if (incomeTable == null) {
            throw new IllegalArgumentException("Income table cannot be null");
        }
        if (mandatoryTable == null) {
            throw new IllegalArgumentException("Mandatory table cannot be null");
        }
        if (discretionaryTable == null) {
            throw new IllegalArgumentException("Discretionary table cannot be null");
        }
        if (totalTable == null) {
            throw new IllegalArgumentException("Total table cannot be null");
        }
        
        // Validate table types
        if (!(incomeTable instanceof TableView)) {
            throw new IllegalArgumentException("Income table must be a TableView");
        }
        if (!(mandatoryTable instanceof TableView) && !(mandatoryTable instanceof TreeTableView)) {
            throw new IllegalArgumentException("Mandatory table must be a TableView or TreeTableView");
        }
        if (!(discretionaryTable instanceof TableView)) {
            throw new IllegalArgumentException("Discretionary table must be a TableView");
        }
    }
    
    /**
     * Extracts items from either TableView or TreeTableView.
     */
    @SuppressWarnings("unchecked")
    private static List<LineItem> extractItems(Object table) {
        if (table instanceof TreeTableView<?>) {
            // Safe cast after instanceof check
            return extractTreeTableViewItems((TreeTableView<LineItem>) table);
        } else {
            return extractTableViewItems(table);
        }
    }
    
    /**
     * Extracts items from a TableView.
     */
    @SuppressWarnings("unchecked")
    private static List<LineItem> extractTableViewItems(Object table) {
        if (!(table instanceof TableView)) {
            throw new IllegalArgumentException("Expected TableView but got: " + table.getClass().getSimpleName());
        }
        
        TableView<LineItem> tableView = (TableView<LineItem>) table;
        return new ArrayList<>(tableView.getItems());
    }
    
    /**
     * Extracts items from a TreeTableView, collecting from all child nodes.
     */
    private static List<LineItem> extractTreeTableViewItems(TreeTableView<LineItem> treeTable) {
        List<LineItem> items = new ArrayList<>();
        
        TreeItem<LineItem> root = treeTable.getRoot();
        if (root != null) {
            // Collect items from all children (parent categories)
            items.addAll(root.getChildren().stream()
                    .filter(child -> child.getValue() != null)
                    .map(TreeItem::getValue)
                    .collect(Collectors.toList()));
        }
        
        return items;
    }
    
    /**
     * Calculates totals for a category of items.
     */
    private static LineItem calculateCategoryTotal(List<LineItem> items, String categoryLabel, int type) {
        LineItem total = new LineItem();
        total.setCategory(categoryLabel);
        total.setType(type);
        
        if (!items.isEmpty()) {
            // Calculate sums, filtering out items that shouldn't be included in totals
            double actualSum = items.stream()
                    .filter(item -> item != null && item.includeInTotal())
                    .mapToDouble(LineItem::getActual)
                    .sum();
            
            double budgetSum = items.stream()
                    .filter(item -> item != null && item.includeInTotal())
                    .mapToDouble(LineItem::getBudget)
                    .sum();
            
            total.setActual(actualSum);
            total.setBudget(budgetSum);
        } else {
            total.setActual(0.0);
            total.setBudget(0.0);
        }
        
        return total;
    }
    
    /**
     * Calculates the overall total (income - expenses).
     */
    private static LineItem calculateOverallTotal(LineItem incomeTotal, LineItem mandatoryTotal, LineItem discretionaryTotal) {
        LineItem overallTotal = new LineItem();
        overallTotal.setCategory(OVERALL_TOTAL_LABEL);
        overallTotal.setType(INCOME_TYPE);
        
        double totalExpenseActual = mandatoryTotal.getActual() + discretionaryTotal.getActual();
        double totalExpenseBudget = mandatoryTotal.getBudget() + discretionaryTotal.getBudget();
        
        overallTotal.setActual(incomeTotal.getActual() - totalExpenseActual);
        overallTotal.setBudget(incomeTotal.getBudget() - totalExpenseBudget);
        
        return overallTotal;
    }
    
    // ========================= ADDITIONAL UTILITY METHODS =========================
    
    /**
     * Checks if a table contains valid data.
     * 
     * @param table The table to check
     * @return true if table has valid data, false otherwise
     */
    public static boolean hasValidData(Object table) {
        try {
            List<LineItem> items = extractItems(table);
            return !items.isEmpty() && items.stream().anyMatch(item -> item != null);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error checking table data validity", e);
            return false;
        }
    }
    
    /**
     * Gets the count of items in a table that are included in totals.
     * 
     * @param table The table to count
     * @return The number of items included in totals
     */
    public static int getIncludedItemCount(Object table) {
        try {
            List<LineItem> items = extractItems(table);
            return (int) items.stream()
                    .filter(item -> item != null && item.includeInTotal())
                    .count();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error counting included items", e);
            return 0;
        }
    }
    
    /**
     * Creates a summary of table totals without updating UI.
     * 
     * @param incomeTable The income table
     * @param mandatoryTable The mandatory table
     * @param discretionaryTable The discretionary table
     * @return A summary object containing all calculated totals
     */
    public static TableTotalSummary calculateTotalsSummary(Object incomeTable, Object mandatoryTable, Object discretionaryTable) {
        try {
            List<LineItem> incomeItems = extractTableViewItems(incomeTable);
            List<LineItem> mandatoryItems = extractItems(mandatoryTable);
            List<LineItem> discretionaryItems = extractTableViewItems(discretionaryTable);
            
            LineItem incomeTotal = calculateCategoryTotal(incomeItems, INCOME_TOTAL_LABEL, INCOME_TYPE);
            LineItem mandatoryTotal = calculateCategoryTotal(mandatoryItems, MANDATORY_TOTAL_LABEL, MANDATORY_TYPE);
            LineItem discretionaryTotal = calculateCategoryTotal(discretionaryItems, DISCRETIONARY_TOTAL_LABEL, DISCRETIONARY_TYPE);
            LineItem overallTotal = calculateOverallTotal(incomeTotal, mandatoryTotal, discretionaryTotal);
            
            return new TableTotalSummary(incomeTotal, mandatoryTotal, discretionaryTotal, overallTotal);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating totals summary", e);
            return new TableTotalSummary(); // Return empty summary
        }
    }
    
    // ========================= INNER CLASS =========================
    
    /**
     * Summary class for table totals calculation results.
     */
    public static class TableTotalSummary {
        private final LineItem incomeTotal;
        private final LineItem mandatoryTotal;
        private final LineItem discretionaryTotal;
        private final LineItem overallTotal;
        
        public TableTotalSummary() {
            this.incomeTotal = new LineItem();
            this.mandatoryTotal = new LineItem();
            this.discretionaryTotal = new LineItem();
            this.overallTotal = new LineItem();
        }
        
        public TableTotalSummary(LineItem incomeTotal, LineItem mandatoryTotal, 
                               LineItem discretionaryTotal, LineItem overallTotal) {
            this.incomeTotal = incomeTotal != null ? incomeTotal : new LineItem();
            this.mandatoryTotal = mandatoryTotal != null ? mandatoryTotal : new LineItem();
            this.discretionaryTotal = discretionaryTotal != null ? discretionaryTotal : new LineItem();
            this.overallTotal = overallTotal != null ? overallTotal : new LineItem();
        }
        
        public LineItem getIncomeTotal() { return incomeTotal; }
        public LineItem getMandatoryTotal() { return mandatoryTotal; }
        public LineItem getDiscretionaryTotal() { return discretionaryTotal; }
        public LineItem getOverallTotal() { return overallTotal; }
        
        @Override
        public String toString() {
            return String.format("TableTotalSummary{income=%.2f, mandatory=%.2f, discretionary=%.2f, overall=%.2f}",
                               incomeTotal.getActual(), mandatoryTotal.getActual(), 
                               discretionaryTotal.getActual(), overallTotal.getActual());
        }
    }
}
