package com.example.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;

public class ReadData {

    /**
     * Finds the category of a LineItemCSV in the actual database table.
     * 
     * @param item The LineItemCSV object to find the category for.
     * @return The category ID if found, or -1 if not found.
     */
    public static LineItemCSV actualFindCategory(LineItemCSV item) {
        LineItemCSV returnItem = new LineItemCSV();
        // copy the item to returnItem
        returnItem.setId(-1);
        returnItem.setAmount(item.getAmount());
        returnItem.setDate(item.getDate());
        returnItem.setCategory(item.getCategory());
        returnItem.setParent(item.getParent());
        returnItem.setType(item.getType());

        try {
            PreparedStatement findRecord = DataSource.getConn().prepareStatement(DB.ACTUAL_FIND_CATEGORY);
            findRecord.setInt(1, item.getId()); // category id in category table
            findRecord.setInt(2, item.getDate().getMonthValue());
            findRecord.setInt(3, item.getDate().getYear());

            ResultSet rs = findRecord.executeQuery();
            if (rs.next()) {
                returnItem.setId(rs.getInt(DB.ACTUAL_COL_ID));
                return returnItem;
            }
            else {
                returnItem.setId(-1);
                return returnItem;
            }
        }
        catch (Exception e) {
            System.out.println("Error actualFindCategory: " + e.getMessage());
            return item;
        }
    }

    public static LineItemCSV budgetFindCategory(LineItemCSV item) {
        LineItemCSV returnItem = new LineItemCSV();
        returnItem.setId(-1);
        // copy the item to returnItem
        returnItem.setId(-1);
        returnItem.setAmount(item.getAmount());
        returnItem.setDate(item.getDate());
        returnItem.setCategory(item.getCategory());
        returnItem.setParent(item.getParent());
        returnItem.setType(item.getType());

        try {
            PreparedStatement findRecord = DataSource.getConn().prepareStatement(DB.BUDGET_FIND_CATEGORY);
            findRecord.setInt(1, item.getId());
            findRecord.setInt(2, item.getDate().getMonthValue());
            findRecord.setInt(3, item.getDate().getYear());

            ResultSet rs = findRecord.executeQuery();
            if (rs.next()) {
                returnItem.setId(rs.getInt(DB.BUDGET_COL_ID));
                return returnItem;
            }
            else {
                returnItem.setId(-1);
                return returnItem;
            }
        }
        catch (Exception e) {
            System.out.println("Error actualFindCategory: " + e.getMessage());
            return returnItem;
        }
    }

    /**
     * Finds the record index of a LineItemCSV in the category database table.
     * 
     * @param item The LineItemCSV object to find the record index for.
     * @return The record index if found, or -1 if not found.
     */
    public static LineItemCSV categoryFindRecord(LineItemCSV item) {
        LineItemCSV returnItem = new LineItemCSV();
        returnItem.setId(-1);
        // copy the item to returnItem
        returnItem.setId(-1);
        returnItem.setAmount(item.getAmount());
        returnItem.setDate(item.getDate());
        returnItem.setCategory(item.getCategory());
        returnItem.setParent(item.getParent());
        returnItem.setType(item.getType());

        PreparedStatement psFindRecord = null;
        ResultSet rs = null;
        try {
            psFindRecord = DataSource.getConn().prepareStatement(DB.CAT_FIND_CATEGORY);
            psFindRecord.setString(1, item.getParent());
            psFindRecord.setString(2, item.getCategory());

            rs = psFindRecord.executeQuery();
            if (rs.next()) {
                returnItem.setId(rs.getInt(DB.CAT_COL_ID));
                returnItem.setType(rs.getInt(DB.CAT_COL_TYPE));
                returnItem.setCategory(rs.getString(DB.CAT_COL_CATEGORY));

                return returnItem;
            }
            else {
                returnItem.setId(-1);
                return returnItem;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error categoryFindRecord: " + e.getMessage());
            return returnItem;
        }
    }

    public static ArrayList<LineItem> getTableAmounts(int type, LocalDate date) {
        ArrayList<LineItem> items = new ArrayList<LineItem>();
        try {
            PreparedStatement ps = DataSource.getConn().prepareStatement(DB.GET_ACTUAL_AND_BUDGET_AMOUNTS);
            ps.setInt(1, date.getMonthValue());
            ps.setInt(2, date.getYear());
            ps.setInt(3, type);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LineItem newItem = new LineItem();
                newItem.setDate(rs.getDate("DATE").toLocalDate());
                newItem.setCategory(rs.getString("CATEGORY"));
                newItem.setActual(rs.getDouble("ACTUAL"));

                items.add(newItem);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getTableAmounts: " + e.getMessage());
        }

        return items;
    }

    public static LineItem getTotals(int type, LocalDate date) {
        LineItem newItem = new LineItem();
        try {
            PreparedStatement ps = DataSource.getConn().prepareStatement(DB.GET_TOTALS);
            ps.setInt(1, date.getMonthValue());
            ps.setInt(2, date.getYear());
            ps.setInt(3, type);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                newItem.setActual(rs.getDouble("ATOTAL"));
                newItem.setBudget(rs.getDouble("BTOTAL"));
                newItem.setCategory("TOTAL");
                
                return newItem;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getTotals: " + e.getMessage());
        }

        return newItem;
    }


    // method to create test data for year choicebox
    public static ArrayList<Integer> getYears(){
        ArrayList<Integer> years = new ArrayList<Integer>();

        try {
            PreparedStatement ps = DataSource.getConn().prepareStatement(DB.ACTUAL_GET_YEARS);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                years.add(rs.getInt("YEAR"));
            }
            
            if (years.size() == 0) {
                years.add(LocalDate.now().getYear());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getYears: " + e.getMessage());
        }
        return years;
    }

 
}
