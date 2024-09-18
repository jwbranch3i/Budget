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
            String monthString = String.format("%02d", item.getDate().getMonthValue());
            String yearString = String.format("%04d", item.getDate().getYear());

            PreparedStatement findRecord = DataSource.getConn().prepareStatement(DB.ACTUAL_FIND_CATEGORY);
            findRecord.setInt(1, item.getId());
            findRecord.setString(2, monthString);
            findRecord.setString(3, yearString);

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
            String monthString = String.format("%02d", date.getMonthValue());
            String yearString = String.format("%04d", date.getYear());

            PreparedStatement ps = DataSource.getConn().prepareStatement(DB.GET_ACTUAL_AND_BUDGET_AMOUNTS);
            ps.setString(1, monthString);
            ps.setString(2, yearString);
            ps.setInt(3, type);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LineItem newItem = new LineItem();
                newItem.setId(rs.getInt("ID"));
                newItem.setHide(rs.getBoolean("HIDE"));
                newItem.setDate(LocalDate.parse(rs.getString("DATE")));
                newItem.setCategory(rs.getString("CATEGORY"));
                newItem.setActual(rs.getDouble("ACTUAL"));
                newItem.setBudget(rs.getDouble("BUDGET"));

                if (newItem.getHide() == false) {
                    items.add(newItem);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error*** getTableAmounts: " + e.getMessage());
        }

        return items;
    }

    public static LineItem getTotals(int type, LocalDate date) {
        LineItem newItem = new LineItem();
        try {
            String monthString = String.format("%02d", date.getMonthValue());
            String yearString = String.format("%04d", date.getYear());

            PreparedStatement ps = DataSource.getConn().prepareStatement(DB.GET_TOTALS);
            ps.setString(1, monthString);
            ps.setString(2, yearString);
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
            System.out.println("Error*** getTotals: " + e.getMessage());
        }

        return newItem;
    }

    // method to available years in database
    public static ArrayList<Integer> getYears() {
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

    // method to get categories from the database
    public static ArrayList<Categories> getCategories() {
        ArrayList<Categories> categories = new ArrayList<Categories>();

        try {
            PreparedStatement ps = DataSource.getConn().prepareStatement(DB.CAT_GET_CATEGORIES);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Categories newItem = new Categories();
                newItem.setId(rs.getInt("ID"));
                newItem.setHide(rs.getBoolean("HIDE"));
                newItem.setType(rs.getInt("TYPE"));
                newItem.setParent(rs.getString("PARENT"));
                newItem.setCategory(rs.getString("CATEGORY"));

                categories.add(newItem);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getCategories: " + e.getMessage());
        }
        return categories;
    }

}
