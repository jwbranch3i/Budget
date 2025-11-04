package com.budget.dataModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.budget.Util;
import com.budget.controllers.PrimaryController;

/**
 * Utility class for database write operations. Provides methods for inserting
 * and updating budget-related data in the database.
 */
public final class WriteData {

    private static final Logger LOGGER = Logger.getLogger(WriteData.class.getName());

    // ========================= CONSTANTS =========================

    // Prevent instantiation
    private WriteData() {
        throw new UnsupportedOperationException("This is a Utility class and cannot be instantiated");
    }

    // ========================= CATEGORY OPERATIONS =========================

    /**
     * Inserts a new category record into the database.
     * 
     * @param item The LineItemCSV object representing the category to be
     *             inserted
     * @return The LineItemCSV object with the generated ID set, or null if
     *         insertion failed
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

        try (PreparedStatement insertRecord = DataSource.getConn().prepareStatement(DB.CAT_INSERT_CATEGORY,
                PreparedStatement.RETURN_GENERATED_KEYS)) {

            insertRecord.setInt(1, item.getType());
            insertRecord.setString(2, item.getParent());
            insertRecord.setBoolean(3, item.isMainCategory());
            insertRecord.setString(4, item.getCategory());

            int rowsAffected = insertRecord.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = insertRecord.getGeneratedKeys()) {
                    if (rs.next()) {
                        returnItem.setId(rs.getInt(1));
                    }
                    else {
                        LOGGER.warning("Category insert succeeded but no generated key returned");
                        returnItem.setId(-1);
                    }
                }
            }
            else {
                LOGGER.warning("Category insert failed - no rows affected");
                returnItem.setId(-1);
            }

            return returnItem;

        }
        catch (SQLException e) {
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
            }
            else {
                LOGGER.warning("Category update failed - no rows affected for ID: " + item.getId());
            }

            return success;

        }
        catch (SQLException e) {
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
            }
            else {
                LOGGER.warning("Actual amount update failed - no rows affected for ID: " + item.getId());
            }

            return success;

        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating actual amount for ID: " + item.getId(), e);
            return false;
        }
    }

    /**
     * Updates existing LineItem budget in Actual table.
     * 
     * @param item The LineItem object representing the line item to update
     * @return true if the update was successful, false otherwise
     * @throws IllegalArgumentException if item is null
     */
    public static boolean updateLineItem(LineItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }

        if (!DataSource.getInstance().ensureConnection()) {
            LOGGER.severe("Database connection not available for Line item update");
            return false;
        }

        try (PreparedStatement updateRecord = DataSource.getConn().prepareStatement(DB.UPDATE_LINEITEM)) {

            updateRecord.setDouble(1, item.getActual());
            updateRecord.setDouble(2, item.getBudget());
            updateRecord.setInt(3, item.getId());

            int rowsAffected = updateRecord.executeUpdate();
            boolean success = rowsAffected > 0;

            if (!success) {
                LOGGER.warning("Budget update in Table Actual failed - no rows affected for ID: " + item.getId());
            }

            return success;

        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating budget tin Table Actual ID: " + item.getId(), e);
            return false;
        }
    }

    public static boolean updateItemAcrossTables(LineItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }

        DataSource.getInstance().beginTransaction();
        boolean updateResult = updateCategoryType(item)
            && updateHideField(item)
            && updateLineItem(item);

        if (updateResult) {
          DataSource.getInstance().commitTransaction();
        } else {
          DataSource.getInstance().rollbackTransaction();
        }
        
        if (!updateResult) {
            LOGGER.warning("Item update failed for ID: " + item.getId());
        }
        
        return updateResult;
    }

    public static boolean updateCategoryType(LineItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (!DataSource.getInstance().ensureConnection()) {
            LOGGER.severe("Database connection not available for Category Type update");
            return false;
        }
        try (PreparedStatement updateRecord = DataSource.getConn().prepareStatement(DB.UPDATE_CATEGORY_TYPE)) {

            updateRecord.setInt(1, item.getType());
            updateRecord.setString(2, item.getParent());

            int rowsAffected = updateRecord.executeUpdate();
            boolean success = rowsAffected > 0;

            if (!success) {
                LOGGER.warning(
                        "Category Type update in Table Category failed - no rows affected for ID: " + item.getId());
            }

            return success;

        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating Category Type in Table Actual ID: " + item.getId(), e);
            return false;
        }

    }

    public static boolean updateHideField(LineItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (!DataSource.getInstance().ensureConnection()) {
            LOGGER.severe("Database connection not available for Category Type update");
            return false;
        }
        try (PreparedStatement updateRecord = DataSource.getConn().prepareStatement(DB.UPDATE_HIDE_FIELD)) {

            updateRecord.setBoolean(1, item.hide());
            updateRecord.setString(2, item.getParent());

            int rowsAffected = updateRecord.executeUpdate();
            boolean success = rowsAffected > 0;

            if (!success) {
                LOGGER.warning("Hide field update in Table Category failed - no rows affected for ID: " + item.getId());
            }

            return success;

        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating hide field in Table Category ID: " + item.getId(), e);
            return false;
        }

    }

    /**
     * Inserts a new record into the actual table in the database.
     * 
     * @param existingCategory The LineItemCSV object representing the record to
     *                         be inserted
     * @return The LineItemCSV object with the generated ID set, or null if
     *         insertion failed
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

        try (PreparedStatement insertRecord = DataSource.getConn().prepareStatement(DB.ACTUAL_INSERT_RECORD,
                PreparedStatement.RETURN_GENERATED_KEYS)) {

            insertRecord.setInt(1, existingCategory.getId());
            // Use Util.formatDateForDatabase instead of local method
            insertRecord.setString(2, Util.formatDateForDatabase(existingCategory.getDate()));
            insertRecord.setDouble(3, existingCategory.getAmount());

            int rowsAffected = insertRecord.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = insertRecord.getGeneratedKeys()) {
                    if (rs.next()) {
                        returnActual.setId(rs.getInt(1));
                    }
                    else {
                        LOGGER.warning("Actual insert succeeded but no generated key returned");
                    }
                }
            }
            else {
                LOGGER.warning("Actual insert failed - no rows affected");
            }

            return returnActual;

        }
        catch (SQLException e) {
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

            String currentDateStr = Util.formatDateForDatabase(currentDate);
            String lastMonthDateStr = Util.formatDateForDatabase(currentDate.minusMonths(1));

            getLastBudget.setString(1, lastMonthDateStr);
            getLastBudget.setString(2, currentDateStr);

            int rowsAffected = getLastBudget.executeUpdate();

            LOGGER.info("Copied budget from " + lastMonthDateStr + " to " + currentDateStr + " - " + rowsAffected
                    + " rows affected");

            return true;

        }
        catch (SQLException e) {
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

            String currentDateStr = Util.formatDateForDatabase(currentDate);
            String lastMonthDateStr = Util.formatDateForDatabase(currentDate.minusMonths(1));

            updateBalance.setString(1, lastMonthDateStr);
            updateBalance.setString(2, currentDateStr);

            int rowsAffected = updateBalance.executeUpdate();

            LOGGER.info("Updated balances from " + lastMonthDateStr + " to " + currentDateStr + " - " + rowsAffected
                    + " rows affected");

            return true;

        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating balance for date: " + currentDate, e);
            return false;
        }
    }

    // ========================= BATCH OPERATIONS =========================

    /**
     * Performs monthly setup operations: copies last month's budget and updates
     * balances.
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
        }
        else {
            LOGGER.warning("Monthly setup completed with errors for: " + currentDate + " (Budget copy: "
                    + budgetCopySuccess + ", Balance update: " + balanceUpdateSuccess + ")");
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
            }
            else {
                LOGGER.severe("Failed to commit transaction");
                return false;
            }

        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during batch operation", e);
            dataSource.rollbackTransaction();
            return false;
        }
    }

    // Add these methods to your WriteData class

    // ========================= RUNNING TOTAL OPERATIONS
    // =========================

    /**
     * Updates or inserts a running total for a category in a specific month.
     * 
     * @param categoryId    The category ID
     * @param monthDate     The month date
     * @param budgetAmount  The budget amount for this month
     * @param actualAmount  The actual amount for this month
     * @param maximumAmount Optional maximum amount for this category
     * @return The updated RunningTotal object, or null if operation failed
     */
    public static RunningTotal updateRunningTotal(int categoryId, LocalDate monthDate, double budgetAmount,
            double actualAmount, Double maximumAmount) {
        if (!DataSource.getInstance().ensureConnection()) {
            LOGGER.severe("Database connection not available for running total update");
            return null;
        }

        try {
            // Get previous month's effective running total (modified if
            // available, otherwise calculated)
            double previousBalance = getPreviousEffectiveRunningTotal(categoryId, monthDate);

            // Calculate current difference and new running total
            double currentDifference = budgetAmount - actualAmount;
            double newRunningTotal = previousBalance + currentDifference;

            // Create RunningTotal object
            RunningTotal runningTotal = new RunningTotal();
            runningTotal.setCategoryId(categoryId);
            runningTotal.setMonthDate(monthDate);
            runningTotal.setPreviousBalance(previousBalance);
            runningTotal.setCurrentDifference(currentDifference);
            runningTotal.setRunningTotal(newRunningTotal);
            runningTotal.setMaximumAmount(maximumAmount);
            runningTotal.setWarningIssued(false);
            // Don't set modifiedRunningTotal - let it be null for calculated
            // values

            // Insert/update the record
            try (PreparedStatement stmt = DataSource.getConn()
                    .prepareStatement(DB.RUNNING_TOTAL_INSERT_WITH_MODIFIED)) {
                stmt.setInt(1, categoryId);
                stmt.setString(2, Util.formatDateForDatabase(monthDate));
                stmt.setDouble(3, previousBalance);
                stmt.setDouble(4, currentDifference);
                stmt.setDouble(5, newRunningTotal);

                // Keep existing modified value if it exists, otherwise set to
                // null
                Double existingModified = getExistingModifiedRunningTotal(categoryId, monthDate);
                if (existingModified != null) {
                    stmt.setDouble(6, existingModified);
                    runningTotal.setModifiedRunningTotal(existingModified);
                }
                else {
                    stmt.setNull(6, java.sql.Types.REAL);
                }

                if (maximumAmount != null) {
                    stmt.setDouble(7, maximumAmount);
                }
                else {
                    stmt.setNull(7, java.sql.Types.REAL);
                }

                stmt.setBoolean(8, false);

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    LOGGER.fine("Successfully updated running total for category " + categoryId + " in month "
                            + Util.formatDateForDatabase(monthDate));
                    return runningTotal;
                }
                else {
                    LOGGER.warning("Running total update failed - no rows affected");
                    return null;
                }

            }
        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating running total for category " + categoryId, e);
            return null;
        }
    }

    /**
     * Gets the effective running total from the previous month (modified if
     * available, otherwise calculated).
     */
    private static double getPreviousEffectiveRunningTotal(int categoryId, LocalDate monthDate) {
        try (PreparedStatement stmt = DataSource.getConn()
                .prepareStatement(DB.RUNNING_TOTAL_GET_PREVIOUS_WITH_MODIFIED)) {
            stmt.setInt(1, categoryId);
            stmt.setString(2, Util.formatDateForDatabase(monthDate));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("effective_total");
                }
            }
        }
        catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error getting previous effective running total for category " + categoryId, e);
        }

        return 0.0; // Default to 0 if no previous data
    }

    /**
     * Gets existing modified running total for a category/month, or null if not
     * modified.
     */
    private static Double getExistingModifiedRunningTotal(int categoryId, LocalDate monthDate) {
        String query = "SELECT modified_running_total FROM category_running_totals "
                + "WHERE category_id = ? AND month_date = ?";

        try (PreparedStatement stmt = DataSource.getConn().prepareStatement(query)) {
            stmt.setInt(1, categoryId);
            stmt.setString(2, Util.formatDateForDatabase(monthDate));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getObject("modified_running_total", Double.class);
                }
            }
        }
        catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error getting existing modified running total", e);
        }

        return null;
    }

    /**
     * Manually sets a modified running total for a category in a specific
     * month.
     */
    public static boolean setModifiedRunningTotal(int categoryId, LocalDate monthDate, Double modifiedTotal) {
        if (!DataSource.getInstance().ensureConnection()) {
            LOGGER.severe("Database connection not available for modified running total update");
            return false;
        }

        try (PreparedStatement stmt = DataSource.getConn().prepareStatement(DB.RUNNING_TOTAL_UPDATE_MODIFIED)) {
            if (modifiedTotal != null) {
                stmt.setDouble(1, modifiedTotal);
            }
            else {
                stmt.setNull(1, java.sql.Types.REAL);
            }
            stmt.setInt(2, categoryId);
            stmt.setString(3, Util.formatDateForDatabase(monthDate));

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.info("Successfully updated modified running total for category " + categoryId + " in month "
                        + Util.formatDateForDatabase(monthDate));

                // Cascade update to future months since their calculations
                // depend on this change
                PrimaryController.cascadeRunningTotalsUpdate(monthDate.plusMonths(1));

                return true;
            }
            else {
                LOGGER.warning("Modified running total update failed - no rows affected");
                return false;
            }

        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating modified running total", e);
            return false;
        }
    }
}