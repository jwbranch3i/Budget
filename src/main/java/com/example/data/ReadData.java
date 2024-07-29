package com.example.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        returnItem.setId(item.getId());
        returnItem.setAmount(item.getAmount());
        returnItem.setDate(item.getDate());
        returnItem.setCategory(item.getCategory());
        returnItem.setParent(item.getParent());
        returnItem.setType(item.getType());

        try {
            PreparedStatement findRecord = DataSource.getConn().prepareStatement(DB.ACTUAL_FIND_CATEGORY,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            findRecord.setInt(1, item.getId());
            findRecord.setInt(2, item.getDate().getMonthValue());
            findRecord.setInt(3, item.getDate().getYear());

            ResultSet rs = findRecord.executeQuery();
            if (rs.next()) {
                returnItem.setId(rs.getInt(DB.ACTUAL_COL_ID));
                return returnItem;
            } else {
                returnItem.setId(-1);
                return returnItem;
            }
        } catch (Exception e) {
            System.out.println("Error actualFindCategory: " + e.getMessage());
            return item;
        }
    }

    public static LineItemCSV budgetFindCategory(LineItemCSV item) {
        LineItemCSV returnItem = new LineItemCSV();
        // copy the item to returnItem
        returnItem.setId(item.getId());
        returnItem.setAmount(item.getAmount());
        returnItem.setDate(item.getDate());
        returnItem.setCategory(item.getCategory());
        returnItem.setParent(item.getParent());
        returnItem.setType(item.getType());

        try {
            PreparedStatement findRecord = DataSource.getConn().prepareStatement(DB.BUDGET_FIND_CATEGORY,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            findRecord.setInt(1, item.getId());
            findRecord.setInt(2, item.getDate().getMonthValue());
            findRecord.setInt(3, item.getDate().getYear());

            ResultSet rs = findRecord.executeQuery();
            if (rs.next()) {
                returnItem.setId(rs.getInt(DB.BUDGET_COL_ID));
                return returnItem;
            } else {
                returnItem.setId(-1);
                return returnItem;
            }
        } catch (Exception e) {
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
        // copy the item to returnItem
        returnItem.setId(item.getId());
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
                returnItem.setId(rs.getInt(DB.CAT_COL_ID_INDEX));
                returnItem.setType(rs.getInt(DB.CAT_COL_TYPE));
                returnItem.setCategory(rs.getString(DB.CAT_COL_CATEGORY));

                return returnItem;
            } else {
                returnItem.setId(-1);
                return returnItem;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error categoryFindRecord: " + e.getMessage());
            return item;
        }
    }

    public static List<LineItem> getTableAmounts(int type, LocalDate date) {
        List<LineItem> items = new ArrayList<>();
        try {
            PreparedStatement ps = DataSource.getConn().prepareStatement(DB.GET_ACTUAL_AND_BUDGET_AMOUNTS);
            ps.setInt(1, date.getMonthValue());
            ps.setInt(2, date.getYear());
            ps.setInt(3, type);

            System.out.println(ps.toString());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LineItem newItem = new LineItem();
                newItem.setDate(rs.getDate("DATE").toLocalDate());
                newItem.setCategory(rs.getString("CATEGORY"));
                newItem.setActual(rs.getDouble("ACTUAL"));

                items.add(newItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getTableAmounts: " + e.getMessage());
        }
        return items;
    }
}
