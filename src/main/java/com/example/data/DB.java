package com.example.data;

public class DB {

        /* CVS file */
        public static final String CSV_FILE_PATH = "D:\\VSCwork\\budget\\";

        public static final String CSV_FILE_NAME = "budgetPrint.csv";
        // public static final String CSV_FILE_NAME = "budgetPrintData.csv";
        public static final String CSV_FILE = CSV_FILE_PATH + CSV_FILE_NAME;

        public static final int INCOME = 0;
        public static final int MANDITORY = 1;
        public static final int DISCRETIONARY = 2;

        /* table - catogery */
        public static final String CAT_TABLE = "category";
        public static final String CAT_COL_ID = "id";
        public static final String CAT_COL_TYPE = "type";
        public static final String CAT_COL_PARENT = "parent";
        public static final String CAT_COL_CATEGORY = "category";

        public static final int CAT_COL_ID_INDEX = 1;
        public static final int CAT_COL_TYPE_INDEX = 2;
        public static final int CAT_COL_PARENT_INDEX = 3;
        public static final int CAT_COL_CATEGORY_INDEX = 4;

        /* table - category */
        public static final String CAT_INSERT_CATEGORY = "INSERT INTO " + CAT_TABLE + " (" + CAT_COL_TYPE + ", "
                        + CAT_COL_PARENT + ", " + CAT_COL_CATEGORY + ") VALUES(?, ?, ?)";

        public static final String DELETE_ALL_CATEGORY = "DELETE FROM " + CAT_TABLE;

        public static final String CAT_FIND_CATEGORY = "SELECT " + CAT_COL_ID + ", " + CAT_COL_TYPE + ", "
                        + CAT_COL_PARENT + ", " + CAT_COL_CATEGORY + " FROM " + CAT_TABLE + " WHERE " + CAT_COL_PARENT
                        + " = ? AND " + CAT_COL_CATEGORY + " = ?";

        /* table - budget */
        public static final String BUDGET_TABLE = "budget";
        public static final String BUDGET_COL_ID = "id";
        public static final String BUDGET_COL_CATEGORY = "category";
        public static final String BUDGET_COL_DATE = "date";
        public static final String BUDGET_COL_AMOUNT = "amount";

        public static final int BUDGET_COL_ID_INDEX = 1;
        public static final int BUDGET_COL_CATEGORY_INDEX = 2;
        public static final int BUDGET_COL_DATE_INDEX = 3;
        public static final int BUDGET_COL_AMOUNT_INDEX = 4;

        public static final String BUDGET_INSERT_RECORD = "INSERT INTO " + BUDGET_TABLE + " (" + BUDGET_COL_CATEGORY
                        + ", " + BUDGET_COL_DATE + ", " + BUDGET_COL_AMOUNT + ") VALUES(?, ?, ?)";

        public static final String BUDGET_FIND_CATEGORY = "SELECT " + BUDGET_COL_ID + ", " + BUDGET_COL_CATEGORY + ", "
                        + BUDGET_COL_DATE + ", " + BUDGET_COL_AMOUNT + " FROM " + BUDGET_TABLE + " WHERE "
                        + BUDGET_COL_CATEGORY + " = ?" + " AND MONTH(" + BUDGET_COL_DATE + ") = ?" + " AND YEAR("
                        + BUDGET_COL_DATE + ") = ?";

        /* table - actual */
        public static final String ACTUAL_TABLE = "actual";
        public static final String ACTUAL_COL_ID = "id";
        public static final String ACTUAL_COL_CATEGORY = "category";
        public static final String ACTUAL_COL_DATE = "date";
        public static final String ACTUAL_COL_AMOUNT = "amount";

        public static final int ACTUAL_COL_ID_INDEX = 1;
        public static final int ACTUAL_COL_CATEGORY_INDEX = 2;
        public static final int ACTUAL_COL_DATE_INDEX = 3;
        public static final int ACTUAL_COL_AMOUNT_INDEX = 4;

        public static final String ACTUAL_FIND_CATEGORY = "SELECT " + ACTUAL_COL_ID + ", " + ACTUAL_COL_CATEGORY + ", "
                        + ACTUAL_COL_DATE + ", " + ACTUAL_COL_AMOUNT + " FROM " + ACTUAL_TABLE + " WHERE "
                        + ACTUAL_COL_CATEGORY + " = ?" + " AND MONTH(" + ACTUAL_COL_DATE + ") = ?" + " AND YEAR("
                        + ACTUAL_COL_DATE + ") = ?";

        public static final String ACTUAL_INSERT_RECORD = "INSERT INTO " + ACTUAL_TABLE + " (" + ACTUAL_COL_CATEGORY
                        + ", " + ACTUAL_COL_DATE + ", " + ACTUAL_COL_AMOUNT + ") VALUES(?, ?, ?)";

        public static final String ACTUAL_UPDATE_AMOUNT = "UPDATE " + ACTUAL_TABLE + " SET " + ACTUAL_COL_AMOUNT
                        + " = ? WHERE " + ACTUAL_COL_ID + " = ?";

        public static final String ACTUAL_GET_TABLE_AMOUNTS = "SELECT " + CAT_TABLE + "." + CAT_COL_CATEGORY
                        + " AS CATEGORY, " + ACTUAL_TABLE + "." + ACTUAL_COL_AMOUNT + " AS ACTUAL FROM " + CAT_TABLE
                        + " INNER JOIN " + ACTUAL_TABLE + " ON " + ACTUAL_TABLE + "." + ACTUAL_COL_CATEGORY + " = "
                        + CAT_TABLE + "." + CAT_COL_ID + " AND " + CAT_TABLE + "." + CAT_COL_TYPE + " = ?";

        public static final String ACTUAL_GET_YEARS = "SELECT DISTINCT YEAR(" + ACTUAL_COL_DATE + ") AS YEAR FROM "
                        + ACTUAL_TABLE + " ORDER BY YEAR(" + ACTUAL_COL_DATE + ") ASC";

        /*
         * SELECT category.category AS CATEGORY, actual.date AS DATE,
         * actual.amount AS ACTUAL, budget.amount AS BUDGET FROM category INNER
         * JOIN actual ON actual.category = category.id INNER JOIN budget ON
         * budget.category = category.id WHERE MONTH(actual.date) = ? AND
         * YEAR(actual.date) = ? AND category.type = ?"
         */
        public static final String GET_ACTUAL_AND_BUDGET_AMOUNTS = "SELECT " + CAT_TABLE + "." + CAT_COL_CATEGORY
                        + " AS CATEGORY, " + ACTUAL_TABLE + "." + ACTUAL_COL_DATE + " AS DATE, " + ACTUAL_TABLE + "."
                        + ACTUAL_COL_AMOUNT + " AS ACTUAL, " + BUDGET_TABLE + "." + BUDGET_COL_AMOUNT
                        + " AS BUDGET FROM " + CAT_TABLE + " INNER JOIN " + ACTUAL_TABLE + " ON " + ACTUAL_TABLE + "."
                        + ACTUAL_COL_CATEGORY + " = " + CAT_TABLE + "." + CAT_COL_ID + " INNER JOIN " + BUDGET_TABLE
                        + " ON " + BUDGET_TABLE + "." + BUDGET_COL_CATEGORY + " = " + CAT_TABLE + "." + CAT_COL_ID
                        + " WHERE MONTH(" + ACTUAL_TABLE + "." + ACTUAL_COL_DATE + ") = ? AND YEAR(" + ACTUAL_TABLE
                        + "." + ACTUAL_COL_DATE + ") = ? AND " + CAT_TABLE + "." + CAT_COL_TYPE + " = ?";

        /*
         * SELECT SUM(ACTUAL.amount) AS ATOTAL, SUM(BUDGET.AMOUNT) AS BTOTAL
         * FROM category INNER JOIN actual ON actual.category = category.id
         * INNER JOIN budget ON budget.category = category.id WHERE
         * MONTH(actual.date) = ? AND YEAR(actual.date) = ? AND category.type =
         * ?
         */
        public static final String GET_TOTALS = "SELECT SUM(" + ACTUAL_TABLE + "." + ACTUAL_COL_AMOUNT + ") AS ATOTAL, "
                        + "SUM(" + BUDGET_TABLE + "." + BUDGET_COL_AMOUNT + ") AS BTOTAL FROM " + CAT_TABLE
                        + " INNER JOIN " + ACTUAL_TABLE + " ON " + ACTUAL_TABLE + "." + ACTUAL_COL_CATEGORY + " = "
                        + CAT_TABLE + "." + CAT_COL_ID + " INNER JOIN " + BUDGET_TABLE + " ON " + BUDGET_TABLE + "."
                        + BUDGET_COL_CATEGORY + " = " + CAT_TABLE + "." + CAT_COL_ID + " WHERE MONTH(" + ACTUAL_TABLE
                        + "." + ACTUAL_COL_DATE + ") = ? AND YEAR(" + ACTUAL_TABLE + "." + ACTUAL_COL_DATE + ") = ?"
                        + " AND " + CAT_TABLE + "." + CAT_COL_TYPE + " = ?";
}