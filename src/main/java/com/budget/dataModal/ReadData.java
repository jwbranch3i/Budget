package com.budget.dataModal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.control.TreeItem;

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

    /**
     * Reads items from the database using DB.GET_ACTUAL_AND_BUDGET_AMOUNTS,
     * grouping the results by PARENT into a TreeItem<LineItem> tree.
     *
     * @param type The type of items to retrieve.
     * @param date The date to filter the items.
     * @return The root TreeItem containing grouped LineItems by PARENT.
     */
    public static TreeItem<LineItem> getTableAmountsTree(int type, LocalDate date) {
        TreeItem<LineItem> rootNode = new TreeItem<>(new LineItem());
        try {
            String monthString = String.format("%02d", date.getMonthValue());
            String yearString = String.format("%04d", date.getYear());

            PreparedStatement ps = DataSource.getConn().prepareStatement(DB.GET_ACTUAL_AND_BUDGET_AMOUNTS);
            ps.setString(1, monthString);
            ps.setString(2, yearString);
            ps.setInt(3, type);

            ResultSet rs = ps.executeQuery();

            // Map to hold parent nodes

            Map<String, TreeItem<LineItem>> parentMap = new HashMap<>();

            while (rs.next()) {
                LineItem newItem = new LineItem();
                newItem.setId(rs.getInt("ID"));
                newItem.setType(type);
                newItem.hide(rs.getBoolean("HIDE"));
                newItem.setDate(LocalDate.parse(rs.getString("DATE")));
                newItem.setIsCategory(rs.getBoolean("MAIN_CATEGORY"));
                newItem.setCategory(rs.getString("CATEGORY"));
                newItem.setActual(rs.getDouble("ACTUAL"));
                newItem.setBudget(rs.getDouble("BUDGET"));
                newItem.setStartBal(rs.getDouble("STARTBAL"));
                String parent = rs.getString("PARENT");

                // Get or create parent node
                TreeItem<LineItem> parentNode = parentMap.get(parent);
                if (parentNode == null) {
                    LineItem parentItem = new LineItem();
                    parentItem.setCategory(parent);
                    parentNode = new TreeItem<>(parentItem);
                    parentMap.put(parent, parentNode);
                    rootNode.getChildren().add(parentNode);
                }
                else {
                    parentNode.getChildren().add(new TreeItem<>(newItem));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getTableAmountsTreeGroupedByParent: " + e.getMessage());
        }
        return rootNode;
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
                newItem.setType(type);
                newItem.hide(rs.getBoolean("HIDE"));
                newItem.include_in_total(rs.getBoolean("INCLUDE_IN_TOTAL"));
                newItem.setDate(LocalDate.parse(rs.getString("DATE")));
                newItem.setIsCategory(rs.getBoolean("MAIN_CATEGORY"));
                newItem.setCategory(rs.getString("CATEGORY"));
                newItem.setActual(rs.getDouble("ACTUAL"));
                newItem.setBudget(rs.getDouble("BUDGET"));
                newItem.setStartBal(rs.getDouble("STARTBAL"));

                if (newItem.hide() == false) {
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

    // find catagories not in actual table for a given month
    // and add to the actual table and return list of added categories
    public static ArrayList<LineItemCSV> findMissingCategories(LocalDate date) {
        ArrayList<LineItemCSV> items = new ArrayList<LineItemCSV>();
        try {
            String monthString = String.format("%02d", date.getMonthValue());
            String yearString = String.format("%04d", date.getYear());

            PreparedStatement ps = DataSource.getConn().prepareStatement(DB.FIND_MISSING_CATEGORIES);
            ps.setString(1, monthString);
            ps.setString(2, yearString);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LineItemCSV newItem = new LineItemCSV();
                newItem.setId(rs.getInt("ID"));
                newItem.setDate(date);
                newItem.setType(rs.getInt("TYPE"));
                newItem.setParent(rs.getString("PARENT"));
                newItem.setCategory(rs.getString("CATEGORY"));

                newItem = WriteData.actualInsertRecord(newItem);
                if (newItem.getHide() == false) {
                    items.add(newItem);
                }

            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error*** findMissingCategories: " + e.getMessage());
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
                newItem.setType(type);

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
    public static ArrayList<String> getYears() {
        ArrayList<String> years = new ArrayList<String>();

        try {
            PreparedStatement ps = DataSource.getConn().prepareStatement(DB.ACTUAL_GET_YEARS);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                years.add(rs.getString("YEAR"));
            }

            if (years.isEmpty()) {
                years.add(String.valueOf(LocalDate.now().getYear())); // Convert
                                                                      // Integer
                                                                      // to
                                                                      // String
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
                newItem.include_in_total(rs.getBoolean("INCLUDE_IN_TOTAL"));
                newItem.hide(rs.getBoolean("HIDE"));
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
