package com.example.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class WriteData {
    /**
     * Inserts a new category record into the database.
     * 
     * @param item The LineItemCSV object representing the category to be
     *             inserted.
     * @return The LineItemCSV object with the generated ID set.
     */
    public static LineItemCSV categoryInsertRecord(LineItemCSV item) {
        LineItemCSV returnItem = new LineItemCSV();

        // copy the item to returnItem
        returnItem.setType(item.getType());
        returnItem.setDate(item.getDate());
        returnItem.setParent(item.getParent());
        returnItem.setCategory(item.getCategory());
        returnItem.setAmount(item.getAmount());

        returnItem.setId(-1);
        try {
            PreparedStatement insertRecord = DataSource.getConn().prepareStatement(DB.CAT_INSERT_CATEGORY,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            insertRecord.setInt(1, item.getType());
            insertRecord.setString(2, item.getParent());
            insertRecord.setString(3, item.getCategory());

            insertRecord.executeUpdate();
            ResultSet rs = insertRecord.getGeneratedKeys();
            if (rs.next()) {
                returnItem.setId(rs.getInt(1));
            }
            else {
                returnItem.setId(-1);
            }
            return returnItem;
        }
        catch (Exception e) {
            System.out.println("Error categoryInsertRecord: " + e.getMessage());
        }
        return returnItem;
    }

    /**
     * Updates the amount for a given category ID in the database.
     * 
     * @param id     The ID of the category to update.
     * @param amount The new amount value.
     * @return true if the update was successful, false otherwise.
     */
    public static boolean autualUpdateAmount(LineItemCSV item) {
        try {
            PreparedStatement updateRecord = DataSource.getConn().prepareStatement(DB.ACTUAL_UPDATE_ACTUAL);
            updateRecord.setDouble(1, item.getAmount());
            updateRecord.setInt(2, item.getId());
            updateRecord.executeUpdate();
        }
        catch (Exception e) {
            System.out.println("Error autualUpdateAmount: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Updates the budget amount for a specific line item.
     * 
     * @param item The LineItem object representing the line item to update.
     * @return true if the budget amount was successfully updated, false
     *         otherwise.
     */
    public static boolean actualUpdateBudget(LineItem item) {
        try {
            PreparedStatement updateRecord = DataSource.getConn().prepareStatement(DB.ACTUAL_UPDATE_BUDGET);
            updateRecord.setDouble(1, item.getBudget());
            updateRecord.setInt(2, item.getId());
            updateRecord.executeUpdate();
        }
        catch (Exception e) {
            System.out.println("Error budgetUpdateAmount: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Inserts a new record into the actual table in the database.
     * 
     * @param newActual The LineItemCSV object representing the record to be
     *                  inserted.
     * @return The LineItemCSV object with the generated ID set.
     */
    public static LineItemCSV actualInsertRecord(LineItemCSV newActual, LineItemCSV existingCategory) {
        LineItemCSV returnActual = new LineItemCSV();
        // copy the item to returnItem
        returnActual.setId(newActual.getId());
        returnActual.setAmount(newActual.getAmount());
        returnActual.setDate(newActual.getDate());
        returnActual.setCategory(newActual.getCategory());
        returnActual.setParent(newActual.getParent());
        returnActual.setType(newActual.getType());

        try {
            PreparedStatement insertRecord = DataSource.getConn().prepareStatement(DB.ACTUAL_INSERT_RECORD,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            insertRecord.setInt(1, existingCategory.getId());

            String dateString = newActual.getDate().toString();
            insertRecord.setString(2, dateString);

            insertRecord.setDouble(3, newActual.getAmount());
            insertRecord.setInt(4, 0);

            insertRecord.executeUpdate();

            ResultSet rs = insertRecord.getGeneratedKeys();
            if (rs.next()) {
                returnActual.setId(rs.getInt(1));
                return returnActual;
            }

        }
        catch (Exception e) {
            System.out.println("Error actualInsertRecord: " + e.getMessage());
        }

        return newActual;
    }

    public static Boolean categoryUpdateType(Categories item) {
        try {
            PreparedStatement updateRecord = DataSource.getConn().prepareStatement(DB.CAT_UPDATE_TYPE);
            updateRecord.setInt(1, item.getType());
            updateRecord.setInt(2, item.getId());
            updateRecord.executeUpdate();
        }
        catch (Exception e) {
            System.out.println("Error categoryUpdateType: " + e.getMessage());
            return false;
        }
        return true;
    }

    public static Boolean categoryUpdateHide(Categories item) {
        try {
            PreparedStatement updateRecord = DataSource.getConn().prepareStatement(DB.CAT_UPDATE_HIDE);
            updateRecord.setBoolean(1, item.getHide());
            updateRecord.setInt(2, item.getId());
            updateRecord.executeUpdate();
        }
        catch (Exception e) {
            System.out.println("Error categoryUpdateHide: " + e.getMessage());
            return false;
        }
        return true;
    }

}
