package com.budget.dataModal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
            insertRecord.setBoolean(3, item.isMainCategory());
            insertRecord.setString(4, item.getCategory());

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
    public static boolean actualUpdate(LineItem item) {
        try {
            PreparedStatement updateRecord = DataSource.getConn().prepareStatement(DB.ACTUAL_UPDATE);

            String dateString = item.getDate().toString();
            updateRecord.setString(1, dateString);
            updateRecord.setDouble(2, item.getActual());
            updateRecord.setDouble(3, item.getBudget());
            updateRecord.setDouble(4, item.getStartBal());
            updateRecord.setInt(5, item.getId());
            updateRecord.executeUpdate();
        }
        catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
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
    public static LineItemCSV actualInsertRecord(LineItemCSV existingCategory) {
        LineItemCSV returnActual = new LineItemCSV();
        // copy the item to returnItem
        returnActual.setId(existingCategory.getId());
        returnActual.setDate(existingCategory.getDate());
        returnActual.setAmount(existingCategory.getAmount());

        try {
            PreparedStatement insertRecord = DataSource.getConn().prepareStatement(DB.ACTUAL_INSERT_RECORD,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            insertRecord.setInt(1, existingCategory.getId());

            String dateString = existingCategory.getDate().toString();
            insertRecord.setString(2, dateString);

            insertRecord.setDouble(3, existingCategory.getAmount());

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

        return returnActual;
    }

    public static Boolean categoryUpdate(Categories item){
        try {
            PreparedStatement updateRecord = DataSource.getConn().prepareStatement(DB.CATEGORY_UPDATE);
            updateRecord.setInt(1, item.getType());
            updateRecord.setString(2, item.getParent());
            updateRecord.setString(3, item.getCategory());
            updateRecord.setBoolean(4, item.isIncludeInTotal());
            updateRecord.setBoolean(5, item.isHide());
            updateRecord.setInt(6, item.getAcct());
            updateRecord.setInt(7, item.getId());
           
            updateRecord.executeUpdate();
        }
        catch (Exception e) {
            System.out.println("Error categoryUpdate: " + e.getMessage());
            return false;
        }
        return true;
    }

    public static void getLastBudget(LocalDate indate) {
        try {
            String currentDate = indate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            String lastMonthDate = indate.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"));

            PreparedStatement getLastBudget = DataSource.getConn().prepareStatement(DB.UPDATE_TO_LAST_MONTH_BUDGET);
            getLastBudget.setString(1, lastMonthDate);
            getLastBudget.setString(2, currentDate);
            getLastBudget.executeUpdate();

        }
        catch (Exception e) {
            System.out.println("Error getLastBudget: " + e.getMessage());
        }
    }

    public static void updateBalance(LocalDate inDate) {
        try {
            String currentDate = inDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            String lastMonthDate = inDate.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"));

            PreparedStatement updateBalance = DataSource.getConn().prepareStatement(DB.UPDATE_BALANCE);
            updateBalance.setString(1, lastMonthDate);
            updateBalance.setString(2, currentDate);
            updateBalance.executeUpdate();
        }
        catch (Exception e) {
            System.out.println("Error updateBalance: " + e.getMessage());
        }
    }
}
