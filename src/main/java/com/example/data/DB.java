package com.example.data;

public class DB {

        /* CVS file */
        public static final String CSV_FILE_PATH = "D:\\VSCwork\\budget\\";

        public static final String CSV_FILE_NAME = "budgetPrint.csv";
        // public static final String CSV_FILE_NAME = "budgetPrintData.csv";
        public static final String CSV_FILE = CSV_FILE_PATH + CSV_FILE_NAME;

        public static final int INFLOW = 0;
        public static final int OUTFLOW = 1;

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
        public static final String CAT_INSERT_CATEGORY = "INSERT INTO " + CAT_TABLE +
                        " (" + CAT_COL_TYPE + ", " + CAT_COL_PARENT + ", " + CAT_COL_CATEGORY +
                        ") VALUES(?, ?, ?)";

        public static final String DELETE_ALL_CATEGORY = "DELETE FROM " + CAT_TABLE;

        public static final String CAT_FIND_CATEGORY = "SELECT " + CAT_COL_ID + ", " + CAT_COL_TYPE +
                        ", " + CAT_COL_PARENT + ", " + CAT_COL_CATEGORY + " FROM " + CAT_TABLE +
                        " WHERE " + CAT_COL_PARENT + " = ? AND " + CAT_COL_CATEGORY + " = ?";

 
        

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

        public static final String ACTUAL_FIND_CATEGORY = "SELECT * FROM " + ACTUAL_TABLE +
                        " WHERE " + ACTUAL_COL_CATEGORY + " = ?"
                        + " AND MONTH(" + ACTUAL_COL_DATE + ") = ?"
                        + " AND YEAR(" + ACTUAL_COL_DATE + ") = ?";

        public static final String ACTUAL_INSERT_RECORD = "INSERT INTO " + ACTUAL_TABLE +
                        " (" + ACTUAL_COL_CATEGORY + ", " + ACTUAL_COL_DATE + ", " + ACTUAL_COL_AMOUNT +
                        ") VALUES(?, ?, ?)";

        public static final String ACTUAL_UPDATE_AMOUNT = "UPDATE " + ACTUAL_TABLE +
                        " SET " + ACTUAL_COL_AMOUNT + " = ? WHERE " + ACTUAL_COL_ID + " = ?";

    
}
