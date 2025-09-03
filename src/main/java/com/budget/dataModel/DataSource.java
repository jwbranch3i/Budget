package com.budget.dataModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.budget.GlobalVariables;

/**
 * Singleton DataSource class for managing database connections and operations.
 * Provides centralized database access for the Budget application using SQLite.
 */
public class DataSource {

    private static final Logger LOGGER = Logger.getLogger(DataSource.class.getName());

    // ========================= DATABASE CONFIGURATION
    // =========================

    /** SQLite database file name */
    public static final String DB_NAME = "Budget.db";
    public static final String DB_TEST = "testBudget.db";

    static String tempName = GlobalVariables.isDebugMode() ? DB_TEST : DB_NAME;

    static final String CONNECTION_STRING = "jdbc:sqlite:C:\\Dropbox\\JAVA\\budget\\" + DB_NAME;

    // ========================= SINGLETON INSTANCE =========================

    private static volatile DataSource instance;
    private static Connection conn;

    /**
     * Private constructor to prevent direct instantiation.
     */
    private DataSource() {
        // Private constructor for singleton pattern
    }

    /**
     * Gets the singleton instance of DataSource using double-checked locking.
     * 
     * @return The DataSource instance
     */
    public static DataSource getInstance() {
        if (instance == null) {
            synchronized (DataSource.class) {
                if (instance == null) {
                    instance = new DataSource();
                }
            }
        }
        return instance;
    }

    /**
     * Gets the current database connection.
     * 
     * @return The database connection, or null if not connected
     */
    public static Connection getConn() {
        return conn;
    }

    // ========================= CONNECTION MANAGEMENT =========================

    /**
     * Opens a connection to the SQLite database.
     * 
     * @return true if connection was successful, false otherwise
     */
    public boolean open() {
        try {
            if (conn != null && !conn.isClosed()) {
                return true;
            }

            conn = DriverManager.getConnection(CONNECTION_STRING);
            conn.setAutoCommit(true); // Explicitly set auto-commit mode

            return true;

        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not connect to database", e);
            return false;
        }
    }

    /**
     * Closes the database connection safely.
     */
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                conn = null;
            }
        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not close database connection", e);
        }
    }

    /**
     * Checks if the database connection is open and valid.
     * 
     * @return true if connection is valid, false otherwise
     */
    public boolean isConnected() {
        try {
            return conn != null && !conn.isClosed() && conn.isValid(2);
        }
        catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error checking connection validity", e);
            return false;
        }
    }

    /**
     * Reopens the database connection if it's closed or invalid.
     * 
     * @return true if connection is available after attempt, false otherwise
     */
    public boolean ensureConnection() {
        if (!isConnected()) {
            return open();
        }
        return true;
    }

    // ========================= CATEGORY OPERATIONS =========================

    /**
     * Inserts a category record into the database.
     * 
     * @param item The LineItemCSV object representing the category record to be
     *             inserted
     * @return true if the record was successfully inserted, false otherwise
     */
    public boolean insertCategoryRecord(LineItemCSV item) {
        if (!ensureConnection()) {
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(DB.CAT_INSERT_CATEGORY)) {
            ps.setInt(1, item.getType());
            ps.setString(2, item.getParent());
            ps.setBoolean(3, false); // main_category default to false
            ps.setString(4, item.getCategory());

            int rowsAffected = ps.executeUpdate();
            boolean success = rowsAffected > 0;

            return success;

        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting category record: " + item.getCategory(), e);
            return false;
        }
    }

    /**
     * Finds a category record in the database by parent and category name.
     * 
     * @param item The LineItemCSV object containing search criteria
     * @return The found LineItemCSV object with database ID, or the original
     *         item if not found
     */
    public LineItemCSV findCategoryRecord(LineItemCSV item) {
        if (!ensureConnection()) {
            return item;
        }

        try (PreparedStatement ps = conn.prepareStatement(DB.CAT_FIND_CATEGORY)) {
            ps.setString(1, item.getParent());
            ps.setString(2, item.getCategory());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LineItemCSV foundItem = new LineItemCSV();
                    foundItem.setId(rs.getInt(DB.CAT_COL_ID));
                    foundItem.setType(rs.getInt(DB.CAT_COL_TYPE));
                    foundItem.setParent(rs.getString(DB.CAT_COL_PARENT));
                    foundItem.setCategory(rs.getString(DB.CAT_COL_CATEGORY));

                    LOGGER.fine("Found category record: " + foundItem.getCategory());
                    return foundItem;
                }
                else {
                    LOGGER.fine("Category not found: " + item.getCategory());
                    return item;
                }
            }

        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching for category record: " + item.getCategory(), e);
            return item;
        }
    }

    /**
     * Deletes all category records from the database.
     * 
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteAllCategoryRecords() {
        if (!ensureConnection()) {
            return false;
        }

        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate(DB.DELETE_ALL_CATEGORY);

            return true;
        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting all category records", e);
            return false;
        }
    }

    // ========================= TRANSACTION SUPPORT =========================

    /**
     * Begins a database transaction by disabling auto-commit.
     * 
     * @return true if transaction was started successfully, false otherwise
     */
    public boolean beginTransaction() {
        if (!ensureConnection()) {
            return false;
        }

        try {
            conn.setAutoCommit(false);
            LOGGER.fine("Transaction started");
            return true;
        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error starting transaction", e);
            return false;
        }
    }

    /**
     * Commits the current transaction and re-enables auto-commit.
     * 
     * @return true if commit was successful, false otherwise
     */
    public boolean commitTransaction() {
        if (!isConnected()) {
            return false;
        }

        try {
            conn.commit();
            conn.setAutoCommit(true);
            LOGGER.fine("Transaction committed");
            return true;
        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error committing transaction", e);
            rollbackTransaction();
            return false;
        }
    }

    /**
     * Rolls back the current transaction and re-enables auto-commit.
     * 
     * @return true if rollback was successful, false otherwise
     */
    public boolean rollbackTransaction() {
        if (!isConnected()) {
            return false;
        }

        try {
            conn.rollback();
            conn.setAutoCommit(true);
            LOGGER.warning("Transaction rolled back");
            return true;
        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error rolling back transaction", e);
            return false;
        }
    }

    // ========================= UTILITY METHODS =========================

    /**
     * Executes a query and returns the number of rows in the result set. Useful
     * for count operations.
     * 
     * @param query The SQL query to execute
     * @return The number of rows, or -1 if an error occurred
     */
    public int getRowCount(String query) {
        if (!ensureConnection()) {
            return -1;
        }

        try (Statement statement = conn.createStatement(); ResultSet rs = statement.executeQuery(query)) {

            int count = 0;
            while (rs.next()) {
                count++;
            }
            return count;

        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error executing row count query: " + query, e);
            return -1;
        }
    }
}
