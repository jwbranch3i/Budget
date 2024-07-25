package com.example.data;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
            System.out.println("Error categoryInsertRecord: " + e.getMessage());
        }
        return null;
    }

    public static boolean autualUpdateAmount(int id, Double amount) {
        try {
            PreparedStatement updateRecord = DataSource.getConn().prepareStatement(DB.ACTUAL_UPDATE_AMOUNT);
            updateRecord.setDouble(1, amount);
            updateRecord.setInt(2, id);
            updateRecord.executeUpdate();
          } catch (Exception e) {

            System.out.println("Error autualUpdateAmount: " + e.getMessage());

            return false;
        }
        return true;
    }

    public static LineItemCSV actualInsertRecord(LineItemCSV category) {
        try {
  
            // insert new record in actual table
            PreparedStatement insertRecord = DataSource.getConn().prepareStatement(DB.ACTUAL_INSERT_RECORD,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            insertRecord.setInt(1, category.getId());
            insertRecord.setDate(2, Date.valueOf(category.getDate()));
            insertRecord.setDouble(3, category.getAmount());
            insertRecord.executeUpdate();

            ResultSet rs = insertRecord.getGeneratedKeys();
            if (rs.next()) {
                category.setId(rs.getInt(1));
                return category;
            }

        } catch (Exception e) {
            System.out.println("Error actualInsertRecord: " + e.getMessage());
        }

        return null;
    }


 }
