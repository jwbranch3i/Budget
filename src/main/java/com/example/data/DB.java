package com.example.data;

public class DB {

    public static final int INFLOW = 0;
    public static final int OUTFLOW = 1;

    /* table - catogery */
    public static final String CAT_TABLE = "category";
    public static final String CAT_COL_ID = "_id";
    public static final String CAT_COL_TYPE = "type";
    public static final String CAT_COL_PARENT = "parent";
    public static final String CAT_COL_CATEGORY = "category";

    public static final int CAT_COL_ID_INDEX = 1;
    public static final int CAT_COL_TYPE_INDEX = 2;
    public static final int CAT_COL_PARENT_INDEX = 3;
    public static final int CAT_COL_CATEGORY_INDEX = 4;

    /* table - transactions */
    public static final INSERT_CATEGORY = "INSERT INTO " + CAT_TABLE + 
        " (" + CAT_COL_TYPE + ", " + CAT_COL_PARENT + ", " + CAT_COL_CATEGORY +
        ") VALUES(?, ?, ?)";


    
}
