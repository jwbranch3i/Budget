package com.example.data;

import java.io.FileReader;
import java.util.ArrayList;

import com.opencsv.CSVReader;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class ReadData {

    public static ArrayList<LineItemCSV> readActual(String file, LocalDate inDate) {

        ArrayList<LineItemCSV> items = new ArrayList<LineItemCSV>();

        LineItemCSV newLineItem = new LineItemCSV();
        LineItemCSV existingCategory = new LineItemCSV();

        String[] nextRecord;
        String category = "";
        String parent = "";
        String workingType = "";

        Double amount = 0.0;

        int leadingSpaces = 0;
        int type = 0;
        int newRecordType = 0;

        int lineCount = 0;

        try {
            FileReader filereader = new FileReader(file);
            CSVReader csvReader = new CSVReader(filereader);

            // we are going to read data line by line
            while ((nextRecord = csvReader.readNext()) != null) {
                // for debugging
                lineCount++;

                // if the line is empty, skip it
                if (nextRecord.length <= 1) {
                    continue;
                }

                leadingSpaces = nextRecord[1].length() - nextRecord[1].trim().length();
                if (nextRecord[1].trim().equals("INFLOWS")) {
                    type = DB.INFLOW;
                    continue;
                } else if (nextRecord[1].trim().equals("OUTFLOWS")) {
                    type = DB.OUTFLOW;
                    continue;
                }

                category = nextRecord[1].trim();
                // if category contains the string 'TOTAL' then skip it
                if (category.contains("TOTAL")) {
                    continue;
                }

                if (nextRecord.length < 3) {
                    continue;
                }

                try {
                    amount = Double.parseDouble(nextRecord[2].replaceAll(",", ""));
                } catch (NumberFormatException e) {
                    amount = 0.0;
                }

                switch (leadingSpaces) {
                    case 4: // if the line is a category

                        newRecordType = type;
                        parent = "";
                        workingType = category;
                        newLineItem = new LineItemCSV(newRecordType, parent, category, amount);
                        existingCategory = categoryFindRecord(newLineItem);

                        if (existingCategory == null) {
                            existingCategory = WriteData.categoryInsertRecord(newLineItem);
                        }
                        break;

                    case 8:
                        parent = workingType;
                        newLineItem = new LineItemCSV(type, parent, category, amount);
                        existingCategory = categoryFindRecord(newLineItem);

                        if (existingCategory == null) {
                            existingCategory = WriteData.categoryInsertRecord(newLineItem);
                        }
                        break;

                    default:
                        // Handle any other number of leading spaces
                        break;
                }

                WriteData.actualInsertRecord(existingCategory, inDate);

            }
            csvReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public static LineItemCSV categoryFindRecord(LineItemCSV item) {

        LineItemCSV lineItem = new LineItemCSV();

        PreparedStatement psFindRecord = null;
        ResultSet rs = null;
        try {
            psFindRecord = DataSource.getConn().prepareStatement(DB.CAT_FIND_CATEGORY);
            psFindRecord.setString(1, item.getParent());
            psFindRecord.setString(2, item.getCategory());

            rs = psFindRecord.executeQuery();
            if (rs.next()) {
                lineItem.setId(rs.getInt(DB.CAT_COL_ID_INDEX));
                lineItem.setType(rs.getInt(DB.CAT_COL_TYPE_INDEX));
                lineItem.setParent(rs.getString(DB.CAT_COL_PARENT_INDEX));
                lineItem.setCategory(rs.getString(DB.CAT_COL_CATEGORY_INDEX));
                return lineItem;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
