package com.example.data;

import java.io.FileReader;
import java.util.ArrayList;

import com.opencsv.CSVReader;

public class ReadData {

    public static ArrayList<CatName> readHeadings(String file) {

        ArrayList<CatName> items = new ArrayList<CatName>();

        CatName newCatName;

        String[] nextRecord;
        String category = "";
        String parent = "";
        String workingType = "";

        int leadingSpaces = 0;
        int lineCount = 0;
        int type = 0;
        int newRecordType = 0;

        try {
            FileReader filereader = new FileReader(file);
            CSVReader csvReader = new CSVReader(filereader);

            // we are going to read data line by line
            while ((nextRecord = csvReader.readNext()) != null) {
                // for degugging
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

                switch (leadingSpaces) {
                    case 4: // if the line is a category

                        newRecordType = type;
                        parent = "";
                        workingType = category;
                        newCatName = new CatName(newRecordType, parent, category);
                        items.add(newCatName);
                        break;

                    case 8:
                        parent = workingType;
                        newCatName = new CatName(type, parent, category);
                        items.add(newCatName);
                        break;

                    default:
                        // Handle any other number of leading spaces
                        break;
                }
            }
            csvReader.close();
            return items;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }
}
