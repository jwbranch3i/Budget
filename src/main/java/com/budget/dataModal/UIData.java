package com.budget.dataModal;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
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
    
 
    /**
     * Updates table totals for income, mandatory, discretionary, and overall totals.
     * Handles both TableView and TreeTableView types for flexible UI support.
     * 
     * @param incomeTable The income table (TableView)
     * @param mandatoryTable The mandatory table (TableView or TreeTableView)
     * @param discretionaryTable The discretionary table (TableView)
     * @param totalTable The totals table where results are displayed
     */
    public static void updateTableGrandTotal(TableView<LineItem> incomeTable, TableView<LineItem> mandatoryTable,
                                      TableView<LineItem> discretionaryTable, TableView<LineItem> totalTable) {
        try {
            // Get single items from each table
            LineItem incomeTotal = getSingleItemFromTable(incomeTable, INCOME_TOTAL_LABEL);
            LineItem mandatoryTotal = getSingleItemFromTable(mandatoryTable, MANDATORY_TOTAL_LABEL);
            LineItem discretionaryTotal = getSingleItemFromTable(discretionaryTable, DISCRETIONARY_TOTAL_LABEL);
            
            // Clear existing totals
            totalTable.getItems().clear();
            
            // Calculate overall total
            LineItem overallTotal = calculateOverallTotal(incomeTotal, mandatoryTotal, discretionaryTotal);
            
            // Add all totals to the total table
            List<LineItem> totals = List.of(incomeTotal, mandatoryTotal, discretionaryTotal, overallTotal);
            totalTable.getItems().addAll(totals);
            
            // Log the items being added
            LOGGER.fine("Table totals updated successfully with " + totals.size() + " items:");
            totals.forEach(item -> LOGGER.fine(" - " + item.getCategory() + ": " + 
                String.format("Actual=%.2f, Budget=%.2f", item.getActual(), item.getBudget())));
            
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
    
    // ========================= TOTAL TABLE OPERATIONS =========================

    /**
     * Updates the grand total table using the single line items from each total table.
     * This method assumes each total table (income, mandatory, discretionary) has exactly one item.
     *
     * @param incomeTotal The income total table
     * @param mandatoryTotal The mandatory expenses total table
     * @param discretionaryTotal The discretionary expenses total table
     * @param grandTotal The grand total table to update
     */
    public static void updateGrandTotalFromTotalTables(
            TableView<LineItem> incomeTotal, 
            TableView<LineItem> mandatoryTotal,
            TableView<LineItem> discretionaryTotal,
            TableView<LineItem> grandTotal) {
        
        try {
            // Clear the grand total table
            grandTotal.getItems().clear();
            
            // Get the single items from each total table
            LineItem incomeItem = getSingleItemFromTable(incomeTotal, INCOME_TOTAL_LABEL);
            LineItem mandatoryItem = getSingleItemFromTable(mandatoryTotal, MANDATORY_TOTAL_LABEL);
            LineItem discretionaryItem = getSingleItemFromTable(discretionaryTotal, DISCRETIONARY_TOTAL_LABEL);
            
            // Calculate overall total
            LineItem overallTotal = calculateOverallTotal(incomeItem, mandatoryItem, discretionaryItem);
            
            // Add all items to grand total table
            grandTotal.getItems().addAll(
                incomeItem,
                mandatoryItem,
                discretionaryItem,
                overallTotal
            );
            
            LOGGER.fine("Grand total table updated successfully from total tables");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating grand total table", e);
            // Ensure grand total table is cleared even if operation fails
            if (grandTotal != null) {
                grandTotal.getItems().clear();
            }
        }
    }
    
    /**
     * Updates the grand total table with totals from all tree tables
     */
    public static void updateGrandTotalFromTreeTables(
        TreeTableView<LineItem> incomeTree,
        TreeTableView<LineItem> mandatoryTree, 
        TreeTableView<LineItem> discretionaryTree,
        TableView<LineItem> grandTotalTable) {

        // Clear existing items
        grandTotalTable.getItems().clear();
        
        // Get root items from trees
        TreeItem<LineItem> incomeRoot = incomeTree.getRoot();
        TreeItem<LineItem> mandatoryRoot = mandatoryTree.getRoot();
        TreeItem<LineItem> discretionaryRoot = discretionaryTree.getRoot();

        // Create total line items
        List<LineItem> totals = new ArrayList<>();
        
        // Add income total
        if (incomeRoot != null) {
            LineItem incomeTotal = new LineItem();
            incomeTotal.setCategory("Total Income");
            incomeTotal.setActual(calculateTreeTotal(incomeRoot, LineItem::getActual));
            incomeTotal.setBudget(calculateTreeTotal(incomeRoot, LineItem::getBudget));
            totals.add(incomeTotal);
        }

        // Add mandatory total  
        if (mandatoryRoot != null) {
            LineItem mandatoryTotal = new LineItem();
            mandatoryTotal.setCategory("Total Mandatory");
            mandatoryTotal.setActual(calculateTreeTotal(mandatoryRoot, LineItem::getActual));
            mandatoryTotal.setBudget(calculateTreeTotal(mandatoryRoot, LineItem::getBudget));
            totals.add(mandatoryTotal);
        }

        // Add discretionary total
        if (discretionaryRoot != null) {
            LineItem discretionaryTotal = new LineItem();
            discretionaryTotal.setCategory("Total Discretionary"); 
            discretionaryTotal.setActual(calculateTreeTotal(discretionaryRoot, LineItem::getActual));
            discretionaryTotal.setBudget(calculateTreeTotal(discretionaryRoot, LineItem::getBudget));
            totals.add(discretionaryTotal);
        }

        // Add to grand total table
         grandTotalTable.setItems(FXCollections.observableArrayList(totals)); 
         grandTotalTable.setItems(FXCollections.observableArrayList(totals));

         System.out.println(grandTotalTable.getItems().size() + " items added to grand total table from tree tables"       );

    }

    /**
     * Calculates total for a tree using the specified value extractor
     */
    private static double calculateTreeTotal(TreeItem<LineItem> root, 
        java.util.function.Function<LineItem, Double> valueExtractor) {
        
        if (root == null) return 0.0;
        
        // Only sum leaf nodes (items without children)
        if (root.isLeaf()) {
            LineItem item = root.getValue();
            return valueExtractor.apply(item);
        }
        
        // Recursively sum children
        return root.getChildren().stream()
            .mapToDouble(child -> calculateTreeTotal(child, valueExtractor))
            .sum();
    }
    
    /**
     * Gets the single item from a total table. If the table is empty or has multiple items,
     * returns a new LineItem with the specified label.
     *
     * @param table The table to get the item from
     * @param defaultLabel The default label to use if no item is found
     * @return The LineItem from the table or a new default item
     */
    private static LineItem getSingleItemFromTable(TableView<LineItem> table, String defaultLabel) {
        if (table != null && !table.getItems().isEmpty()) {
            // Get the last item from the table (which should be the total)
            return table.getItems().get(table.getItems().size() - 1);
        }
        
        // Return default item if table is null or empty
        LineItem defaultItem = new LineItem();
        defaultItem.setCategory(defaultLabel);
        defaultItem.setActual(0.0);
        defaultItem.setBudget(0.0);
        return defaultItem;
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
