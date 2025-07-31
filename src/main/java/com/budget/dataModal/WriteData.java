package com.budget.dataModal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for database write operations.
 * Provides methods for inserting and updating budget-related data in the database.
 */
public final class WriteData {
    
    private static final Logger LOGGER = Logger.getLogger(WriteData.class.getName());
    
    // ========================= CONSTANTS =========================
    
    /** Date formatter for database operations */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    
    // Prevent instantiation
    private WriteData() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    // ========================= CATEGORY OPERATIONS =========================
    
    /**
     * Inserts a new category record into the database.
     * 
     * @param item The LineItemCSV object representing the category to be inserted
     * @return The LineItemCSV object with the generated ID set, or null if insertion failed
     * @throws IllegalArgumentException if item is null
     */
    public static LineItemCSV categoryInsertRecord(LineItemCSV item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        
        if (!DataSource.getInstance().ensureConnection()) {
            LOGGER.severe("Database connection not available for category insert");
            return null;
        }
        
        LineItemCSV returnItem = new LineItemCSV(item); // Use copy constructor
        returnItem.setId(-1); // Reset ID for new record
        
        try (PreparedStatement insertRecord = DataSource.getConn().prepareStatement(
                DB.CAT_INSERT_CATEGORY, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            insertRecord.setInt(1, item.getType());
            insertRecord.setString(2, item.getParent());
            insertRecord.setBoolean(3, item.isMainCategory());
            insertRecord.setString(4, item.getCategory());
            
            int rowsAffected = insertRecord.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = insertRecord.getGeneratedKeys()) {
                    if (rs.next()) {
                        returnItem.setId(rs.getInt(1));
                    } else {
                        LOGGER.warning("Category insert succeeded but no generated key returned");
                        returnItem.setId(-1);
                    }
                }
            } else {
                LOGGER.warning("Category insert failed - no rows affected");
                returnItem.setId(-1);
            }
            
            return returnItem;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting category record: " + item.getCategory(), e);
            returnItem.setId(-1);
            return returnItem;
        }
    }
    
    /**
     * Updates a category record in the database.
     * 
     * @param item The Categories object with updated information
     * @return true if the update was successful, false otherwise
     * @throws IllegalArgumentException if item is null
     */
    public static boolean categoryUpdate(Categories item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        
        if (!DataSource.getInstance().ensureConnection()) {
            LOGGER.severe("Database connection not available for category update");
            return false;
        }
        
        try (PreparedStatement updateRecord = DataSource.getConn().prepareStatement(DB.CATEGORY_UPDATE)) {
            
            updateRecord.setInt(1, item.getType());
            updateRecord.setString(2, item.getParent());
            updateRecord.setString(3, item.getCategory());
            updateRecord.setBoolean(4, item.isIncludeInTotal());
            updateRecord.setBoolean(5, item.isHide());
            updateRecord.setInt(6, item.getAcct());
            updateRecord.setInt(7, item.getId());
            
            int rowsAffected = updateRecord.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                LOGGER.info("Successfully updated category: " + item.getCategory());
            } else {
                LOGGER.warning("Category update failed - no rows affected for ID: " + item.getId());
            }
            
            return success;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating category: " + item.getCategory(), e);
            return false;
        }
    }
    
    // ========================= ACTUAL OPERATIONS =========================
    
    /**
     * Updates the actual amount for a given item in the database.
     * 
     * @param item The LineItemCSV object with the updated amount
     * @return true if the update was successful, false otherwise
     * @throws IllegalArgumentException if item is null
     */
    public static boolean actualUpdateAmount(LineItemCSV item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        
        if (!DataSource.getInstance().ensureConnection()) {
            LOGGER.severe("Database connection not available for actual amount update");
            return false;
        }
        
        try (PreparedStatement updateRecord = DataSource.getConn().prepareStatement(DB.ACTUAL_UPDATE_ACTUAL)) {
            
            updateRecord.setDouble(1, item.getAmount());
            updateRecord.setInt(2, item.getId());
            
            int rowsAffected = updateRecord.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                LOGGER.fine("Successfully updated actual amount for ID: " + item.getId());
            } else {
                LOGGER.warning("Actual amount update failed - no rows affected for ID: " + item.getId());
            }
            
            return success;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating actual amount for ID: " + item.getId(), e);
            return false;
        }
    }
    
    /**
     * Updates a complete actual record in the database.
     * 
     * @param item The LineItem object representing the line item to update
     * @return true if the update was successful, false otherwise
     * @throws IllegalArgumentException if item is null
     */
    public static boolean actualUpdate(LineItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        
        if (item.getDate() == null) {
            throw new IllegalArgumentException("Item date cannot be null");
        }
        
        if (!DataSource.getInstance().ensureConnection()) {
            LOGGER.severe("Database connection not available for actual update");
            return false;
        }
        
        try (PreparedStatement updateRecord = DataSource.getConn().prepareStatement(DB.ACTUAL_UPDATE)) {
            
            updateRecord.setString(1, item.getDate().toString());
            updateRecord.setDouble(2, item.getActual());
            updateRecord.setDouble(3, item.getBudget());
            updateRecord.setDouble(4, item.getStartBal());
            updateRecord.setInt(5, item.getId());
            
            int rowsAffected = updateRecord.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                LOGGER.fine("Successfully updated actual record for ID: " + item.getId());
            } else {
                LOGGER.warning("Actual record update failed - no rows affected for ID: " + item.getId());
            }
            
            return success;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating actual record for ID: " + item.getId(), e);
            return false;
        }
    }
    
    /**
     * Inserts a new record into the actual table in the database.
     * 
     * @param existingCategory The LineItemCSV object representing the record to be inserted
     * @return The LineItemCSV object with the generated ID set, or null if insertion failed
     * @throws IllegalArgumentException if existingCategory is null
     */
    public static LineItemCSV actualInsertRecord(LineItemCSV existingCategory) {
        if (existingCategory == null) {
            throw new IllegalArgumentException("Existing category cannot be null");
        }
        
        if (existingCategory.getDate() == null) {
            throw new IllegalArgumentException("Category date cannot be null");
        }
        
        if (!DataSource.getInstance().ensureConnection()) {
            LOGGER.severe("Database connection not available for actual insert");
            return null;
        }
        
        LineItemCSV returnActual = new LineItemCSV();
        returnActual.setId(existingCategory.getId());
        returnActual.setDate(existingCategory.getDate());
        returnActual.setAmount(existingCategory.getAmount());
        
        try (PreparedStatement insertRecord = DataSource.getConn().prepareStatement(
                DB.ACTUAL_INSERT_RECORD, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            insertRecord.setInt(1, existingCategory.getId());
            insertRecord.setString(2, existingCategory.getDate().toString());
            insertRecord.setDouble(3, existingCategory.getAmount());
            
            int rowsAffected = insertRecord.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = insertRecord.getGeneratedKeys()) {
                    if (rs.next()) {
                        returnActual.setId(rs.getInt(1));
                    } else {
                        LOGGER.warning("Actual insert succeeded but no generated key returned");
                    }
                }
            } else {
                LOGGER.warning("Actual insert failed - no rows affected");
            }
            
            return returnActual;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting actual record for category ID: " + existingCategory.getId(), e);
            return returnActual;
        }
    }
    
    // ========================= BUDGET OPERATIONS =========================
    
    /**
     * Updates budget amounts to match the previous month's budget.
     * 
     * @param currentDate The date for which to update budget amounts
     * @return true if the operation was successful, false otherwise
     * @throws IllegalArgumentException if currentDate is null
     */
    public static boolean copyLastMonthBudget(LocalDate currentDate) {
        if (currentDate == null) {
            throw new IllegalArgumentException("Current date cannot be null");
        }
        
        if (!DataSource.getInstance().ensureConnection()) {
            LOGGER.severe("Database connection not available for budget copy");
            return false;
        }
        
        try (PreparedStatement getLastBudget = DataSource.getConn().prepareStatement(DB.UPDATE_TO_LAST_MONTH_BUDGET)) {
            
            String currentDateStr = currentDate.format(DATE_FORMATTER);
            String lastMonthDateStr = currentDate.minusMonths(1).format(DATE_FORMATTER);
            
            getLastBudget.setString(1, lastMonthDateStr);
            getLastBudget.setString(2, currentDateStr);
            
            int rowsAffected = getLastBudget.executeUpdate();
            
            LOGGER.info("Copied budget from " + lastMonthDateStr + " to " + currentDateStr + 
                       " - " + rowsAffected + " rows affected");
            
            return true;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error copying last month's budget for date: " + currentDate, e);
            return false;
        }
    }
    
    /**
     * Legacy method name for backward compatibility.
     * 
     * @deprecated Use {@link #copyLastMonthBudget(LocalDate)} instead
     */
    @Deprecated
    public static void getLastBudget(LocalDate indate) {
        copyLastMonthBudget(indate);
    }
    
    // ========================= BALANCE OPERATIONS =========================
    
    /**
     * Updates balance amounts based on the previous month's data.
     * 
     * @param currentDate The date for which to update balance amounts
     * @return true if the operation was successful, false otherwise
     * @throws IllegalArgumentException if currentDate is null
     */
    public static boolean updateBalance(LocalDate currentDate) {
        if (currentDate == null) {
            throw new IllegalArgumentException("Current date cannot be null");
        }
        
        if (!DataSource.getInstance().ensureConnection()) {
            LOGGER.severe("Database connection not available for balance update");
            return false;
        }
        
        try (PreparedStatement updateBalance = DataSource.getConn().prepareStatement(DB.UPDATE_BALANCE)) {
            
            String currentDateStr = currentDate.format(DATE_FORMATTER);
            String lastMonthDateStr = currentDate.minusMonths(1).format(DATE_FORMATTER);
            
            updateBalance.setString(1, lastMonthDateStr);
            updateBalance.setString(2, currentDateStr);
            
            int rowsAffected = updateBalance.executeUpdate();
            
            LOGGER.info("Updated balances from " + lastMonthDateStr + " to " + currentDateStr + 
                       " - " + rowsAffected + " rows affected");
            
            return true;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating balance for date: " + currentDate, e);
            return false;
        }
    }
    
    // ========================= BATCH OPERATIONS =========================
    
    /**
     * Performs monthly setup operations: copies last month's budget and updates balances.
     * 
     * @param currentDate The date for the new month setup
     * @return true if both operations were successful, false otherwise
     * @throws IllegalArgumentException if currentDate is null
     */
    public static boolean performMonthlySetup(LocalDate currentDate) {
        if (currentDate == null) {
            throw new IllegalArgumentException("Current date cannot be null");
        }
        
        LOGGER.info("Starting monthly setup for: " + currentDate);
        
        boolean budgetCopySuccess = copyLastMonthBudget(currentDate);
        boolean balanceUpdateSuccess = updateBalance(currentDate);
        
        boolean overallSuccess = budgetCopySuccess && balanceUpdateSuccess;
        
        if (overallSuccess) {
            LOGGER.info("Monthly setup completed successfully for: " + currentDate);
        } else {
            LOGGER.warning("Monthly setup completed with errors for: " + currentDate + 
                          " (Budget copy: " + budgetCopySuccess + ", Balance update: " + balanceUpdateSuccess + ")");
        }
        
        return overallSuccess;
    }
    
    /**
     * Performs a transaction-safe batch operation.
     * 
     * @param operation The batch operation to perform
     * @return true if the operation was successful, false otherwise
     */
    public static boolean performTransactionSafeBatch(Runnable operation) {
        if (operation == null) {
            throw new IllegalArgumentException("Operation cannot be null");
        }
        
        DataSource dataSource = DataSource.getInstance();
        
        if (!dataSource.ensureConnection()) {
            LOGGER.severe("Database connection not available for batch operation");
            return false;
        }
        
        if (!dataSource.beginTransaction()) {
            LOGGER.severe("Failed to begin transaction");
            return false;
        }
        
        try {
            operation.run();
            
            if (dataSource.commitTransaction()) {
                LOGGER.fine("Batch operation completed successfully");
                return true;
            } else {
                LOGGER.severe("Failed to commit transaction");
                return false;
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during batch operation", e);
            dataSource.rollbackTransaction();
            return false;
        }
    }

    // Add these methods to your WriteData class

// ========================= RUNNING TOTAL OPERATIONS =========================

/**
 * Updates or inserts a running total for a category in a specific month.
 * 
 * @param categoryId The category ID
 * @param monthDate The month date
 * @param budgetAmount The budget amount for this month
 * @param actualAmount The actual amount for this month
 * @param maximumAmount Optional maximum amount for this category
 * @return The updated RunningTotal object, or null if operation failed
 */
public static RunningTotal updateRunningTotal(int categoryId, LocalDate monthDate, 
                                            double budgetAmount, double actualAmount, 
                                            Double maximumAmount) {
    if (monthDate == null) {
        throw new IllegalArgumentException("Month date cannot be null");
    }
    
    if (!DataSource.getInstance().ensureConnection()) {
        LOGGER.severe("Database connection not available for running total update");
        return null;
    }
    
    try {
        // Get previous month's running total
        double previousBalance = getPreviousRunningTotal(categoryId, monthDate.minusMonths(1));
        
        // Calculate current difference (budget - actual)
        double currentDifference = budgetAmount - actualAmount;
        
        // Calculate new running total
        double runningTotal = previousBalance + currentDifference;
        
        // Check if warning needed
        boolean warningNeeded = runningTotal < 0;
        
        RunningTotal result = new RunningTotal(categoryId, monthDate);
        result.setPreviousBalance(previousBalance);
        result.setCurrentDifference(currentDifference);
        result.setRunningTotal(runningTotal);
        result.setMaximumAmount(maximumAmount);
        result.setWarningIssued(false); // Will be set to true when warning is shown
        
        // Insert or update the record
        try (PreparedStatement stmt = DataSource.getConn().prepareStatement(DB.RUNNING_TOTAL_INSERT)) {
            stmt.setInt(1, categoryId);
            stmt.setString(2, monthDate.format(DATE_FORMATTER));
            stmt.setDouble(3, previousBalance);
            stmt.setDouble(4, currentDifference);
            stmt.setDouble(5, runningTotal);
            stmt.setDouble(6, maximumAmount != null ? maximumAmount : 0.0);
            stmt.setBoolean(7, false);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                LOGGER.fine("Updated running total for category " + categoryId + 
                           " in " + monthDate.format(DATE_FORMATTER) + ": " + runningTotal);
                return result;
            } else {
                LOGGER.warning("Failed to update running total for category " + categoryId);
                return null;
            }
        }
        
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error updating running total for category " + categoryId, e);
        return null;
    }
}

/**
 * Gets the running total for a category from the previous month.
 */
private static double getPreviousRunningTotal(int categoryId, LocalDate monthDate) {
    try (PreparedStatement stmt = DataSource.getConn().prepareStatement(DB.RUNNING_TOTAL_GET_PREVIOUS)) {
        stmt.setInt(1, categoryId);
        stmt.setString(2, monthDate.format(DATE_FORMATTER));
        
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("running_total");
            }
        }
    } catch (SQLException e) {
        LOGGER.log(Level.WARNING, "Error getting previous running total for category " + categoryId, e);
    }
    
    return 0.0; // Default to 0 if no previous data
}

/**
 * Updates running totals for all categories in a given month.
 * This should be called after actual amounts are updated.
 */
public static boolean updateAllRunningTotals(LocalDate monthDate) {
    if (monthDate == null) {
        throw new IllegalArgumentException("Month date cannot be null");
    }
    
    if (!DataSource.getInstance().ensureConnection()) {
        LOGGER.severe("Database connection not available for running totals update");
        return false;
    }
    
    String monthStr = monthDate.format(DATE_FORMATTER);
    int updatedCount = 0;
    int warningCount = 0;
    
    // Get all line items for this month
    //TODO: shoud be in db.java
    String query = "SELECT a.id, a.budget, a.actual, c.default_maximum_amount, c.category " +
                   "FROM actual a " +
                   "JOIN category c ON a.id = c.id " +
                   "WHERE a.date LIKE ? || '%'";
    
    try (PreparedStatement stmt = DataSource.getConn().prepareStatement(query)) {
        stmt.setString(1, monthStr);
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int categoryId = rs.getInt("category_id");
                double budget = rs.getDouble("budget");
                double actual = rs.getDouble("actual");
                Double maxAmount = rs.getObject("default_maximum_amount", Double.class);
                String categoryName = rs.getString("category");
                
                RunningTotal result = updateRunningTotal(categoryId, monthDate, budget, actual, maxAmount);
                
                if (result != null) {
                    updatedCount++;
                    
                    // Check for warnings
                    if (result.needsWarning()) {
                        warningCount++;
                        LOGGER.warning("NEGATIVE RUNNING TOTAL: Category '" + categoryName + 
                                     "' has a negative running total of " + result.getRunningTotal());
                    }
                    
                    if (result.isOverMaximum()) {
                        LOGGER.warning("OVER MAXIMUM: Category '" + categoryName + 
                                     "' running total (" + result.getRunningTotal() + 
                                     ") exceeds maximum (" + result.getMaximumAmount() + ")");
                    }
                }
            }
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error updating all running totals for " + monthStr, e);
        return false;
    }
    
    LOGGER.info("Updated " + updatedCount + " running totals for " + monthStr + 
               " (" + warningCount + " warnings issued)");
    
    return true;
}

/**
 * Marks warning as issued for a specific running total.
 */
public static boolean markWarningIssued(int categoryId, LocalDate monthDate) {
    if (!DataSource.getInstance().ensureConnection()) {
        return false;
    }
    
    String updateWarning = "UPDATE category_running_totals SET warning_issued = TRUE " +
                          "WHERE category_id = ? AND month_date = ?";
    
    try (PreparedStatement stmt = DataSource.getConn().prepareStatement(updateWarning)) {
        stmt.setInt(1, categoryId);
        stmt.setString(2, monthDate.format(DATE_FORMATTER));
        
        return stmt.executeUpdate() > 0;
        
    } catch (SQLException e) {
        LOGGER.log(Level.WARNING, "Error marking warning as issued", e);
        return false;
    }
}
}