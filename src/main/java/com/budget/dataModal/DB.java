package com.budget.dataModal;

/**
 * Database constants and SQL query definitions for the Budget application.
 * This class provides a centralized location for all database-related constants,
 * table definitions, column names, and SQL queries.
 */
public final class DB {

    // Prevent instantiation
    private DB() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // ========================= FILE CONFIGURATION =========================
    
    /** Base path for CSV files */
    public static final String CSV_FILE_PATH = "D:\\VSCwork\\budget\\";
    
    /** CSV file name for budget data */
    public static final String CSV_FILE_NAME = "budgetPrint.csv";
    
    /** Complete CSV file path */
    public static final String CSV_FILE = CSV_FILE_PATH + CSV_FILE_NAME;

    // ========================= BUSINESS CONSTANTS =========================
    
    /** Income type identifier */
    public static final int INCOME = 0;
    
    /** Mandatory expense type identifier */
    public static final int MANDATORY = 1;
    
    /** Discretionary expense type identifier */
    public static final int DISCRETIONARY = 2;

    // ========================= CATEGORY TABLE =========================
    
    public static final String CAT_TABLE = "category";
    
    // Column names
    public static final String CAT_COL_ID = "id";
    public static final String CAT_COL_INCLUDE_IN_TOTAL = "include_in_total";
    public static final String CAT_COL_HIDE = "hide";
    public static final String CAT_COL_TYPE = "type";
    public static final String CAT_COL_PARENT = "parent";
    public static final String CAT_COL_MAIN_CATEGORY = "main_category";
    public static final String CAT_COL_CATEGORY = "category";
    public static final String CAT_COL_ACCT = "acct";
    public static final String CAT_COL_BALANCE = "balance";
    
    // Column indices (1-based for JDBC)
    public static final int CAT_COL_ID_INDEX = 1;
    public static final int CAT_COL_INCLUDE_IN_TOTAL_INDEX = 2;
    public static final int CAT_COL_HIDE_INDEX = 3;
    public static final int CAT_COL_TYPE_INDEX = 4;
    public static final int CAT_COL_PARENT_INDEX = 5;
    public static final int CAT_COL_MAIN_CATEGORY_INDEX = 6;
    public static final int CAT_COL_CATEGORY_INDEX = 7;
    public static final int CAT_COL_ACCT_INDEX = 8;
    public static final int CAT_COL_BALANCE_INDEX = 9;

    // ========================= ACCOUNTS TABLE =========================
    
    public static final String ACCOUNTS_TABLE = "accounts";
    public static final String ACCOUNTS_COL_ID = "id";
    public static final String ACCOUNTS_COL_ACCTNAME = "acctName";
    
    public static final int ACCOUNTS_COL_ID_INDEX = 1;
    public static final int ACCOUNTS_COL_ACCTNAME_INDEX = 2;

    // ========================= ACTUAL TABLE =========================
    
    public static final String ACTUAL_TABLE = "actual";
    
    // Column names
    public static final String ACTUAL_COL_ID = "id";
    public static final String ACTUAL_COL_CATEGORY = "category";
    public static final String ACTUAL_COL_DATE = "date";
    public static final String ACTUAL_COL_ACTUAL = "actual";
    public static final String ACTUAL_COL_BUDGET = "budget";
    public static final String ACTUAL_COL_STARTBAL = "startBal";
    public static final String ACTUAL_COL_ENDBAL = "endBal";
    
    // Column indices
    public static final int ACTUAL_COL_ID_INDEX = 1;
    public static final int ACTUAL_COL_CATEGORY_INDEX = 2;
    public static final int ACTUAL_COL_DATE_INDEX = 3;
    public static final int ACTUAL_COL_ACTUAL_INDEX = 4;
    public static final int ACTUAL_COL_BUDGET_INDEX = 5;

    // ========================= CATEGORY TABLE QUERIES =========================
    
    /**
     * Insert a new category record.
     * Parameters: type, parent, main_category, category
     */
    public static final String CAT_INSERT_CATEGORY = buildInsertQuery(
        CAT_TABLE,
        CAT_COL_TYPE, CAT_COL_PARENT, CAT_COL_MAIN_CATEGORY, CAT_COL_CATEGORY
    );
    
    /**
     * Delete all category records.
     */
    public static final String DELETE_ALL_CATEGORY = "DELETE FROM " + CAT_TABLE;
    
    /**
     * Find a category by parent and category name.
     * Parameters: parent, category
     */
    public static final String CAT_FIND_CATEGORY = buildSelectQuery(
        new String[]{CAT_COL_ID, CAT_COL_TYPE, CAT_COL_PARENT, CAT_COL_CATEGORY},
        CAT_TABLE,
        CAT_COL_PARENT + " = ? AND " + CAT_COL_CATEGORY + " = ?"
    );
    
    /**
     * Get all categories ordered by parent, type, and category.
     */
    public static final String CAT_GET_CATEGORIES = buildSelectQuery(
        new String[]{CAT_COL_ID, CAT_COL_INCLUDE_IN_TOTAL, CAT_COL_HIDE, 
                    CAT_COL_TYPE, CAT_COL_PARENT, CAT_COL_CATEGORY},
        CAT_TABLE,
        null,
        CAT_COL_PARENT + ", " + CAT_COL_TYPE + ", " + CAT_COL_CATEGORY
    );
    
    /**
     * Update category record.
     * Parameters: type, parent, category, include_in_total, hide, acct, id
     */
    public static final String CATEGORY_UPDATE = buildUpdateQuery(
        CAT_TABLE,
        new String[]{CAT_COL_TYPE, CAT_COL_PARENT, CAT_COL_CATEGORY, 
                    CAT_COL_INCLUDE_IN_TOTAL, CAT_COL_HIDE, CAT_COL_ACCT},
        CAT_COL_ID + " = ?"
    );

    // ========================= ACTUAL TABLE QUERIES =========================
    
    /**
     * Find actual record by category and date.
     * Parameters: category, month, year
     */
    public static final String ACTUAL_FIND_CATEGORY = buildSelectQuery(
        new String[]{ACTUAL_COL_ID, ACTUAL_COL_CATEGORY, ACTUAL_COL_DATE, ACTUAL_COL_ACTUAL},
        ACTUAL_TABLE,
        ACTUAL_COL_CATEGORY + " = ? AND " + buildDateFilter(ACTUAL_COL_DATE, "month") + 
        " = ? AND " + buildDateFilter(ACTUAL_COL_DATE, "year") + " = ?"
    );
    
    /**
     * Insert new actual record.
     * Parameters: category, date, actual
     */
    public static final String ACTUAL_INSERT_RECORD = 
        "INSERT INTO " + ACTUAL_TABLE + " (" + 
        ACTUAL_COL_CATEGORY + ", " + ACTUAL_COL_DATE + ", " + 
        ACTUAL_COL_ACTUAL + ", " + ACTUAL_COL_BUDGET + ") VALUES(?, ?, ?, 0)";
    
    /**
     * Update actual amount.
     * Parameters: actual, id
     */
    public static final String ACTUAL_UPDATE_ACTUAL = buildUpdateQuery(
        ACTUAL_TABLE,
        new String[]{ACTUAL_COL_ACTUAL},
        ACTUAL_COL_ID + " = ?"
    );
    
    /**
     * Update actual record with all fields.
     * Parameters: date, actual, budget, startBal, id
     */
    public static final String ACTUAL_UPDATE = buildUpdateQuery(
        ACTUAL_TABLE,
        new String[]{ACTUAL_COL_DATE, ACTUAL_COL_ACTUAL, ACTUAL_COL_BUDGET, ACTUAL_COL_STARTBAL},
        ACTUAL_COL_ID + " = ?"
    );
    
    /**
     * Get table amounts with category join.
     * Parameters: type
     */
    public static final String ACTUAL_GET_TABLE_AMOUNTS = 
        "SELECT " + CAT_TABLE + "." + CAT_COL_CATEGORY + " AS CATEGORY, " +
        ACTUAL_TABLE + "." + ACTUAL_COL_ACTUAL + " AS ACTUAL " +
        "FROM " + CAT_TABLE + " INNER JOIN " + ACTUAL_TABLE + 
        " ON " + ACTUAL_TABLE + "." + ACTUAL_COL_CATEGORY + " = " + CAT_TABLE + "." + CAT_COL_ID +
        " AND " + CAT_TABLE + "." + CAT_COL_TYPE + " = ?";
    
    /**
     * Get distinct years from actual table.
     */
    public static final String ACTUAL_GET_YEARS = 
        "SELECT DISTINCT " + buildDateFilter(ACTUAL_COL_DATE, "year") + " AS YEAR " +
        "FROM " + ACTUAL_TABLE + " ORDER BY " + ACTUAL_COL_DATE + " ASC";

    // ========================= COMPLEX QUERIES =========================
    
    /**
     * Get actual and budget amounts with category details.
     * Parameters: month, year, type
     */
    public static final String GET_ACTUAL_AND_BUDGET_AMOUNTS = buildComplexSelectQuery();
    
    /**
     * Find categories not present in actual table for given month/year.
     * Parameters: month, year
     */
    public static final String FIND_MISSING_CATEGORIES = 
        "SELECT " + String.join(", ", CAT_COL_ID, CAT_COL_TYPE, CAT_COL_PARENT, CAT_COL_CATEGORY) +
        " FROM " + CAT_TABLE +
        " WHERE " + CAT_COL_ID + " NOT IN (" +
        "SELECT " + ACTUAL_COL_CATEGORY + " FROM " + ACTUAL_TABLE +
        " WHERE " + buildDateFilter(ACTUAL_COL_DATE, "month") + " = ? AND " +
        buildDateFilter(ACTUAL_COL_DATE, "year") + " = ?)";
    
    /**
     * Get totals for specific type and date.
     * Parameters: month, year, type
     */
    public static final String GET_TOTALS = 
        "SELECT SUM(" + ACTUAL_TABLE + "." + ACTUAL_COL_ACTUAL + ") AS ATOTAL, " +
        "SUM(" + ACTUAL_TABLE + "." + ACTUAL_COL_BUDGET + ") AS BTOTAL " +
        "FROM " + CAT_TABLE + " INNER JOIN " + ACTUAL_TABLE +
        " ON " + ACTUAL_TABLE + "." + ACTUAL_COL_CATEGORY + " = " + CAT_TABLE + "." + CAT_COL_ID +
        " WHERE " + buildDateFilter(ACTUAL_TABLE + "." + ACTUAL_COL_DATE, "month") + " = ? AND " +
        buildDateFilter(ACTUAL_TABLE + "." + ACTUAL_COL_DATE, "year") + " = ? AND " +
        CAT_TABLE + "." + CAT_COL_TYPE + " = ? AND " +
        CAT_TABLE + "." + CAT_COL_HIDE + " = 0";

    // ========================= UPDATE QUERIES =========================
    
    /**
     * Update budget amounts to match previous month.
     * Parameters: previous_month, current_month
     */
    public static final String UPDATE_TO_LAST_MONTH_BUDGET = 
        "UPDATE " + ACTUAL_TABLE + " SET " + ACTUAL_COL_BUDGET +
        " = COALESCE((" +
        "SELECT " + ACTUAL_COL_BUDGET + " FROM " + ACTUAL_TABLE + " AS a " +
        "WHERE a." + ACTUAL_COL_CATEGORY + " = " + ACTUAL_TABLE + "." + ACTUAL_COL_CATEGORY +
        " AND " + buildYearMonthFilter("a." + ACTUAL_COL_DATE) + " = ?), 0) " +
        "WHERE " + buildYearMonthFilter(ACTUAL_COL_DATE) + " = ?";
    
    /**
     * Update balance based on previous month's data.
     * Parameters: previous_month, current_month
     */
    public static final String UPDATE_BALANCE = 
        "UPDATE " + ACTUAL_TABLE + " SET " + ACTUAL_COL_STARTBAL +
        " = COALESCE((" +
        "SELECT " + ACTUAL_COL_STARTBAL + " - " + ACTUAL_COL_ACTUAL + " FROM " + ACTUAL_TABLE + " AS a " +
        "WHERE a." + ACTUAL_COL_CATEGORY + " = " + ACTUAL_TABLE + "." + ACTUAL_COL_CATEGORY +
        " AND " + buildYearMonthFilter("a." + ACTUAL_COL_DATE) + " = ?), 0) " +
        "WHERE " + buildYearMonthFilter(ACTUAL_COL_DATE) + " = ?";

    // ========================= UTILITY METHODS =========================
    
    /**
     * Builds a SELECT query with specified columns, table, and optional conditions.
     */
    private static String buildSelectQuery(String[] columns, String table, String whereClause) {
        return buildSelectQuery(columns, table, whereClause, null);
    }
    
    /**
     * Builds a SELECT query with specified columns, table, conditions, and order.
     */
    private static String buildSelectQuery(String[] columns, String table, String whereClause, String orderBy) {
        StringBuilder query = new StringBuilder("SELECT ");
        query.append(String.join(", ", columns));
        query.append(" FROM ").append(table);
        
        if (whereClause != null && !whereClause.trim().isEmpty()) {
            query.append(" WHERE ").append(whereClause);
        }
        
        if (orderBy != null && !orderBy.trim().isEmpty()) {
            query.append(" ORDER BY ").append(orderBy);
        }
        
        return query.toString();
    }
    
    /**
     * Builds an INSERT query for the specified table and columns.
     */
    private static String buildInsertQuery(String table, String... columns) {
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(table).append(" (");
        query.append(String.join(", ", columns));
        query.append(") VALUES(");
        
        // Add placeholders
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) query.append(", ");
            query.append("?");
        }
        query.append(")");
        
        return query.toString();
    }
    
    /**
     * Builds an UPDATE query for the specified table, columns, and condition.
     */
    private static String buildUpdateQuery(String table, String[] columns, String whereClause) {
        StringBuilder query = new StringBuilder("UPDATE ");
        query.append(table).append(" SET ");
        
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) query.append(", ");
            query.append(columns[i]).append(" = ?");
        }
        
        if (whereClause != null && !whereClause.trim().isEmpty()) {
            query.append(" WHERE ").append(whereClause);
        }
        
        return query.toString();
    }
    
    /**
     * Builds a date filter using SQLite's STRFTIME function.
     */
    private static String buildDateFilter(String dateColumn, String format) {
        String formatString = format.equals("month") ? "%m" : 
                             format.equals("year") ? "%Y" : format;
        return "STRFTIME('" + formatString + "', " + dateColumn + ")";
    }
    
    /**
     * Builds a year-month filter for date comparisons.
     */
    private static String buildYearMonthFilter(String dateColumn) {
        return "STRFTIME('%Y-%m', " + dateColumn + ")";
    }
    
    /**
     * Builds the complex query for getting actual and budget amounts.
     */
    private static String buildComplexSelectQuery() {
        StringBuilder query = new StringBuilder("SELECT ");
        
        // Select columns with aliases
        String[] selectColumns = {
            ACTUAL_TABLE + "." + ACTUAL_COL_ID + " AS ID",
            CAT_TABLE + "." + CAT_COL_HIDE + " AS HIDE",
            CAT_TABLE + "." + CAT_COL_INCLUDE_IN_TOTAL,
            CAT_TABLE + "." + CAT_COL_MAIN_CATEGORY + " AS MAIN_CATEGORY",
            CAT_TABLE + "." + CAT_COL_PARENT + " AS PARENT",
            CAT_TABLE + "." + CAT_COL_CATEGORY + " AS CATEGORY",
            ACTUAL_TABLE + "." + ACTUAL_COL_DATE + " AS DATE",
            ACTUAL_TABLE + "." + ACTUAL_COL_ACTUAL + " AS ACTUAL",
            ACTUAL_TABLE + "." + ACTUAL_COL_BUDGET + " AS BUDGET",
            ACTUAL_TABLE + "." + ACTUAL_COL_STARTBAL + " AS STARTBAL"
        };
        
        query.append(String.join(", ", selectColumns));
        
        // FROM clause with JOIN
        query.append(" FROM ").append(CAT_TABLE)
             .append(" INNER JOIN ").append(ACTUAL_TABLE)
             .append(" ON ").append(ACTUAL_TABLE).append(".").append(ACTUAL_COL_CATEGORY)
             .append(" = ").append(CAT_TABLE).append(".").append(CAT_COL_ID);
        
        // WHERE clause
        query.append(" WHERE ")
             .append(buildDateFilter(ACTUAL_TABLE + "." + ACTUAL_COL_DATE, "month")).append(" = ? AND ")
             .append(buildDateFilter(ACTUAL_TABLE + "." + ACTUAL_COL_DATE, "year")).append(" = ? AND ")
             .append(CAT_TABLE).append(".").append(CAT_COL_TYPE).append(" = ?");
        
        // ORDER BY clause
        query.append(" ORDER BY ")
             .append(CAT_TABLE).append(".").append(CAT_COL_PARENT).append(", ")
             .append(CAT_TABLE).append(".").append(CAT_COL_MAIN_CATEGORY).append(" DESC");
        
        return query.toString();
    }
}










// package com.budget.dataModal;

// // import org.apache.commons.beanutils.converters.StringArrayConverter;

// public class DB {

//     /* CVS file */
//     public static final String CSV_FILE_PATH = "D:\\VSCwork\\budget\\";

//     public static final String CSV_FILE_NAME = "budgetPrint.csv";
//     // public static final String CSV_FILE_NAME = "budgetPrintData.csv";
//     public static final String CSV_FILE = CSV_FILE_PATH + CSV_FILE_NAME;

//     public static final int INCOME = 0;
//     public static final int MANDATORY = 1;
//     public static final int DISCRETIONARY = 2;

//     /* table - catogery */
//     public static final String CAT_TABLE = "category";
//     public static final String CAT_COL_ID = "id";
//     public static final String CAT_COL_INCLUDE_IN_TOTAL = "include_in_total";
//     public static final String CAT_COL_HIDE = "hide";
//     public static final String CAT_COL_TYPE = "type";
//     public static final String CAT_COL_PARENT = "parent";
//     public static final String CAT_COL_MAIN_CATEGORY = "main_category";
//     public static final String CAT_COL_CATEGORY = "category";
//     public static final String CAT_COL_ACCT = "acct";
//     public static final String CAT_COL_BALANCE = "balance";

//     public static final int CAT_COL_ID_INDEX = 1;
//     public static final int CAT_COL_INCLUDE_IN_TOTAL_INDEX = 2;
//     public static final int CAT_COL_HIDE_INDEX = 3;
//     public static final int CAT_COL_TYPE_INDEX = 4;
//     public static final int CAT_COL_PARENT_INDEX = 5;
//     public static final int CAT_COL_MAIN_CATEGORY_INDEX = 6;
//     public static final int CAT_COL_CATEGORY_INDEX = 7;
//     public static final int CAT_COL_ACCT_INDEX = 8;
//     public static final int CAT_COL_BALANCE_INDEX = 9;

//     /* table - Accounts */
//     public static final String ACCOUNTS_TABLE = "accounts";
//     public static final String ACCOUNTS_COL_ID = "id";
//     public static final String ACCOUNTS_COL_ACCTNAME = "acctName";

//     public static final int ACCOUNTS_COL_ID_INDEX = 1;
//     public static final int ACCOUNTS_COL_ACCTNAME_INDEX = 2;

//     /* table - category */
//     /*
//      * INSERT INTO category (type, parent, main_category, category) VALUES(?, ?,
//      * ?, ?)
//      */
//     public static final String CAT_INSERT_CATEGORY = "INSERT INTO " + CAT_TABLE + " (" + CAT_COL_TYPE + ", "
//             + CAT_COL_PARENT + ", " + CAT_COL_MAIN_CATEGORY + ", " + CAT_COL_CATEGORY + ") VALUES(?, ?, ?, ?)";

//     public static final String DELETE_ALL_CATEGORY = "DELETE FROM " + CAT_TABLE;

//     public static final String CAT_FIND_CATEGORY = "SELECT " + CAT_COL_ID + ", " + CAT_COL_TYPE + ", " + CAT_COL_PARENT
//             + ", " + CAT_COL_CATEGORY + " FROM " + CAT_TABLE + " WHERE " + CAT_COL_PARENT + " = ? AND "
//             + CAT_COL_CATEGORY + " = ?";

//     /*
//      * SELECT id, include_in_total, hide, type, parent, category FROM category
//      * ORDER BY parent, type, category
//      */
//     public static final String CAT_GET_CATEGORIES = "SELECT " + CAT_COL_ID + ", " + CAT_COL_INCLUDE_IN_TOTAL + ", "
//             + CAT_COL_HIDE + ", " + CAT_COL_TYPE + ", " + CAT_COL_PARENT + ", " + CAT_COL_CATEGORY + " FROM "
//             + CAT_TABLE + " ORDER BY " + CAT_COL_PARENT + ", " + CAT_COL_TYPE + ", " + CAT_COL_CATEGORY;

//     /*
//      * UPDATE category SET type = ?, parent = ?, category = ?, include_in_total
//      * = ?, hide = ?, acct = ? WHERE id = ?
//      */
//     public static final String CATEGORY_UPDATE = "UPDATE " + CAT_TABLE + " SET " + CAT_COL_TYPE + " = ?, "
//             + CAT_COL_PARENT + " = ?, " + CAT_COL_CATEGORY + " = ?, " + CAT_COL_INCLUDE_IN_TOTAL + " = ?, "
//             + CAT_COL_HIDE + " = ?, " + CAT_COL_ACCT + " = ? WHERE " + CAT_COL_ID + " = ?";

//     /* table - actual */
//     public static final String ACTUAL_TABLE = "actual";
//     public static final String ACTUAL_COL_ID = "id";
//     public static final String ACTUAL_COL_CATEGORY = "category";
//     public static final String ACTUAL_COL_DATE = "date";
//     public static final String ACTUAL_COL_ACTUAL = "actual";
//     public static final String ACTUAL_COL_BUDGET = "budget";
//     public static final String ACTUAL_COL_STARTBAL = "startBal";
//     public static final String ACTUAL_COL_ENDBAL = "endBal";

//     public static final int ACTUAL_COL_ID_INDEX = 1;
//     public static final int ACTUAL_COL_CATEGORY_INDEX = 2;
//     public static final int ACTUAL_COL_DATE_INDEX = 3;
//     public static final int ACTUAL_COL_ACTUAL_INDEX = 4;
//     public static final int ACTUAL_COL_BUDGET_INDEX = 5;

//     public static final String ACTUAL_FIND_CATEGORY = "SELECT " + ACTUAL_COL_ID + ", " + ACTUAL_COL_CATEGORY + ", "
//             + ACTUAL_COL_DATE + ", " + ACTUAL_COL_ACTUAL + " FROM " + ACTUAL_TABLE + " WHERE " + ACTUAL_COL_CATEGORY
//             + " = ?" + " AND STRFTIME('%m', " + ACTUAL_COL_DATE + ") = ?" + " AND STRFTIME('%Y', " + ACTUAL_COL_DATE
//             + ") = ?";

//     public static final String ACTUAL_INSERT_RECORD = "INSERT INTO " + ACTUAL_TABLE + " (" + ACTUAL_COL_CATEGORY + ", "
//             + ACTUAL_COL_DATE + ", " + ACTUAL_COL_ACTUAL + ", " + ACTUAL_COL_BUDGET + ") VALUES(?, ?, ?, 0)";

//     public static final String ACTUAL_UPDATE_ACTUAL = "UPDATE " + ACTUAL_TABLE + " SET " + ACTUAL_COL_ACTUAL
//             + " = ? WHERE " + ACTUAL_COL_ID + " = ?";

//     /*
//      * UPDATE actual SET date = ?, actual = ?, budget = ?, startBal = ? WHERE id
//      * = ?
//      */
//     public static final String ACTUAL_UPDATE = "UPDATE " + ACTUAL_TABLE + " SET " + ACTUAL_COL_DATE + " = ?, "
//             + ACTUAL_COL_ACTUAL + " = ?, " + ACTUAL_COL_BUDGET + " = ?, " + ACTUAL_COL_STARTBAL + " = ? WHERE "
//             + ACTUAL_COL_ID + " = ?";

//     public static final String ACTUAL_GET_TABLE_AMOUNTS = "SELECT " + CAT_TABLE + "." + CAT_COL_CATEGORY
//             + " AS CATEGORY, " + ACTUAL_TABLE + "." + ACTUAL_COL_ACTUAL + " AS ACTUAL FROM " + CAT_TABLE
//             + " INNER JOIN " + ACTUAL_TABLE + " ON " + ACTUAL_TABLE + "." + ACTUAL_COL_CATEGORY + " = " + CAT_TABLE
//             + "." + CAT_COL_ID + " AND " + CAT_TABLE + "." + CAT_COL_TYPE + " = ?";

//     public static final String ACTUAL_GET_YEARS = "SELECT DISTINCT STRFTIME('%Y', " + ACTUAL_COL_DATE
//             + ") AS YEAR FROM " + ACTUAL_TABLE + " ORDER BY " + ACTUAL_COL_DATE + " ASC";

//     /*
//      * SELECT actual.id AS ID, category.hide AS HIDE, category.include_in_total,
//      * category.main_category AS MAIN_CATEGORY, category.category AS CATEGORY,
//      * actual.date AS DATE, actual.actual AS ACTUAL, actual.budget AS BUDGET,
//      * actual.startBal as STARTBAL FROM category INNER JOIN actual ON
//      * actual.category = category.id WHERE STRFTIME('%m', actual.date) = ? AND
//      * STRFTIME('%Y', actual.date) = ? AND category.type = ? ORDER BY PARENT,
//      * MAIN_CATEGORY DESC
//      */
//     public static final String GET_ACTUAL_AND_BUDGET_AMOUNTS = "SELECT " + ACTUAL_TABLE + "." + ACTUAL_COL_ID
//             + " AS ID, " + CAT_TABLE + "." + CAT_COL_HIDE + " AS HIDE, " + CAT_TABLE + "." + CAT_COL_INCLUDE_IN_TOTAL
//             + ", " + CAT_TABLE + "." + CAT_COL_MAIN_CATEGORY + " AS MAIN_CATEGORY, " +  CAT_TABLE + "." + CAT_COL_PARENT + " AS PARENT, "
//             + CAT_TABLE + "." + CAT_COL_CATEGORY + " AS CATEGORY, " + ACTUAL_TABLE + "." + ACTUAL_COL_DATE
//             + " AS DATE, " + ACTUAL_TABLE + "." + ACTUAL_COL_ACTUAL + " AS ACTUAL, " + ACTUAL_TABLE + "."
//             + ACTUAL_COL_BUDGET + " AS BUDGET, " + ACTUAL_TABLE + "." + ACTUAL_COL_STARTBAL + " as STARTBAL FROM "
//             + CAT_TABLE + " INNER JOIN " + ACTUAL_TABLE + " ON " + ACTUAL_TABLE + "." + ACTUAL_COL_CATEGORY + " = "
//             + CAT_TABLE + "." + CAT_COL_ID + " WHERE STRFTIME('%m', " + ACTUAL_TABLE + "." + ACTUAL_COL_DATE
//             + ") = ? AND STRFTIME('%Y', " + ACTUAL_TABLE + "." + ACTUAL_COL_DATE + ") = ? AND " + CAT_TABLE + "."
//             + CAT_COL_TYPE + " = ? ORDER BY " + CAT_TABLE + "." + CAT_COL_PARENT + ", " + CAT_TABLE + "."
//             + CAT_COL_MAIN_CATEGORY + " DESC";

//     /*
//      * SELECT id, type, parent, category FROM category WHERE id NOT IN (SELECT
//      * category FROM actual WHERE strftime('%m', date) = ? AND STRFTIME('%Y',
//      * actual.date) = ?);
//      */
//     public static final String FIND_MISSING_CATEGORIES = "SELECT " + CAT_COL_ID + ", " + CAT_COL_TYPE + ", "
//             + CAT_COL_PARENT + ", " + CAT_COL_CATEGORY + " FROM " + CAT_TABLE + " WHERE " + CAT_COL_ID
//             + " NOT IN (SELECT " + ACTUAL_COL_CATEGORY + " FROM " + ACTUAL_TABLE + " WHERE STRFTIME('%m', "
//             + ACTUAL_COL_DATE + ") = ? AND STRFTIME('%Y', " + ACTUAL_COL_DATE + ") = ?)";

//     /*
//      * SELECT SUM(ACTUAL.amount) AS ATOTAL, SUM(AMOUNT.BUDGET) AS BTOTAL FROM
//      * category INNER JOIN actual ON actual.category = category.id WHERE
//      * STRFTIME('%m', actual.date) = ? AND STRFTIME('%Y', actual.date) = ? AND
//      * category.type = ? AND category.hide = 0
//      */
//     public static final String GET_TOTALS = "SELECT SUM(" + ACTUAL_TABLE + "." + ACTUAL_COL_ACTUAL + ") AS ATOTAL, "
//             + "SUM(" + ACTUAL_TABLE + "." + ACTUAL_COL_BUDGET + ") AS BTOTAL FROM " + CAT_TABLE + " INNER JOIN "
//             + ACTUAL_TABLE + " ON " + ACTUAL_TABLE + "." + ACTUAL_COL_CATEGORY + " = " + CAT_TABLE + "." + CAT_COL_ID
//             + " WHERE STRFTIME('%m', " + ACTUAL_TABLE + "." + ACTUAL_COL_DATE + ") = ? AND STRFTIME('%Y', "
//             + ACTUAL_TABLE + "." + ACTUAL_COL_DATE + ") = ? AND " + CAT_TABLE + "." + CAT_COL_TYPE + " = ?" + " AND "
//             + CAT_TABLE + "." + CAT_COL_HIDE + " = 0";

//     /*
//      * UPDATE actual SET budget = COALESCE(( SELECT budget FROM actual AS a
//      * WHERE a.category = actual.category AND strftime('%Y-%m', a.date) = ? ),
//      * 0) WHERE strftime('%Y-%m', actual.date) = ?;
//      */
//     public static final String UPDATE_TO_LAST_MONTH_BUDGET = "UPDATE " + ACTUAL_TABLE + " SET " + ACTUAL_COL_BUDGET
//             + " = COALESCE(( SELECT " + ACTUAL_COL_BUDGET + " FROM " + ACTUAL_TABLE + " AS a WHERE a."
//             + ACTUAL_COL_CATEGORY + " = " + ACTUAL_TABLE + "." + ACTUAL_COL_CATEGORY + " AND STRFTIME('%Y-%m', a."
//             + ACTUAL_COL_DATE + ") = ? ), 0) WHERE STRFTIME('%Y-%m', " + ACTUAL_COL_DATE + ") = ?";

//     /*
//      * UPDATE actual SET startBal = COALESCE(( SELECT startBal - actual FROM
//      * actual AS a WHERE a.category = actual.category AND strftime('%Y-%m',
//      * a.date) = ? ), 0) WHERE strftime('%Y-%m', actual.date) = ?
//      */
//     public static final String UPDATE_BALANCE = "UPDATE " + ACTUAL_TABLE + " SET " + ACTUAL_COL_STARTBAL
//             + " = COALESCE(( SELECT " + ACTUAL_COL_STARTBAL + " - " + ACTUAL_COL_ACTUAL + " FROM " + ACTUAL_TABLE
//             + " AS a WHERE a." + ACTUAL_COL_CATEGORY + " = " + ACTUAL_TABLE + "." + ACTUAL_COL_CATEGORY
//             + " AND STRFTIME('%Y-%m', a." + ACTUAL_COL_DATE + ") = ? ), 0) WHERE STRFTIME('%Y-%m', " + ACTUAL_COL_DATE
//             + ") = ?";
// }
