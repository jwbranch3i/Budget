package com.example.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReadData {



    public static int actualFindCategory(LineItemCSV item) {
        try {
            PreparedStatement findRecord = DataSource.getConn().prepareStatement(DB.ACTUAL_FIND_CATEGORY,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            findRecord.setInt(1, item.getId());
            findRecord.setInt(2, item.getDate().getMonthValue());
            findRecord.setInt(3, item.getDate().getYear());

            ResultSet rs = findRecord.executeQuery();
            if (rs.next()) {
                return rs.getInt(DB.ACTUAL_COL_ID_INDEX);
            } else {
                return -1;
            }
        } catch (Exception e) {
            System.out.println("Error actualFindCategory: " + e.getMessage());
        }
        return -1;
    }


    public static int categoryFindRecord(LineItemCSV item) {
        PreparedStatement psFindRecord = null;
        ResultSet rs = null;
        try {
            psFindRecord = DataSource.getConn().prepareStatement(DB.CAT_FIND_CATEGORY);
            psFindRecord.setString(1, item.getParent());
            psFindRecord.setString(2, item.getCategory());

            rs = psFindRecord.executeQuery();
            if (rs.next()) {
                int lineIndex = rs.getInt(DB.CAT_COL_ID_INDEX);
                return lineIndex;

            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error categoryFindRecord: " + e.getMessage());
        }
        return -1;
    }


}
