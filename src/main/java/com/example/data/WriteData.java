package com.example.data;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class WriteData {
    public static LineItemCSV categoryInsertRecord(LineItemCSV item) {
        try {
            PreparedStatement insertRecord = DataSource.getConn().prepareStatement(DB.CAT_INSERT_CATEGORY,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            insertRecord.setInt(1, item.getType());
            insertRecord.setString(2, item.getParent());
            insertRecord.setString(3, item.getCategory());

            insertRecord.executeUpdate();
            ResultSet rs = insertRecord.getGeneratedKeys();
            if (rs.next()) {
                item.setId(rs.getInt(1));
            }
            return item;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return item;
    }

    public static void categoryUpdateAmount(LineItemCSV item) {
        try {
            PreparedStatement updateRecord = DataSource.getConn().prepareStatement(DB.CAT_UPDATE_AMOUNT);
            updateRecord.setDouble(1, item.getAmount());
            updateRecord.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void actualInsertRecord(LineItemCSV category, LocalDate inDate) {
        try {
            PreparedStatement findRecord = DataSource.getConn().prepareStatement(DB.ACTUAL_FIND_CATEGORY);
            findRecord.setInt(1, category.getId());
            findRecord.setInt(2, inDate.getMonthValue());
            findRecord.setInt(3, inDate.getYear());

            ResultSet rs = findRecord.executeQuery();
            if (rs.next()) {
                // update amount in actual table
                int foundRecord = rs.getInt(DB.ACTUAL_COL_ID_INDEX);
                double newAmount = category.getAmount();

                PreparedStatement updateRecord = DataSource.getConn().prepareStatement(DB.ACTUAL_UPDATE_AMOUNT);
                updateRecord.setDouble(1, newAmount);
                updateRecord.setInt(2, foundRecord);
                updateRecord.executeUpdate();
            } else {
                // insert new record in actual table
                PreparedStatement insertRecord = DataSource.getConn().prepareStatement(DB.ACTUAL_INSERT_RECORD);
                insertRecord.setInt(1, category.getId());
                insertRecord.setDate(2, Date.valueOf(inDate));
                insertRecord.setDouble(3, category.getAmount());
                insertRecord.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

}
