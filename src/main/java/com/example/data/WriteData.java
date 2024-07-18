package com.example.data;

import java.io.FileReader;
import java.util.ArrayList;

import com.opencsv.CSVReader;

public class WriteData {
    private static int rejectedRecordCount = 0;
    private static int addedRecordCount = 0;
 
  
    public static int getAddedRecordCount() {
        return addedRecordCount;
    }

    public static int getRejectedRecordCount() {
        return rejectedRecordCount;
    }


    public static ArrayList<LineItemCSV> readHeadings(String file, Boolean clearRecords) {

        ArrayList<LineItemCSV> items = new ArrayList<LineItemCSV>();

        LineItemCSV newLineItem;

        String[] nextRecord;
        String category = "";
        String parent = "";
        String workingType = "";

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

                switch (leadingSpaces) {
                    case 4: // if the line is a category

                        newRecordType = type;
                        parent = "";
                        workingType = category;
                        newLineItem = new LineItemCSV(newRecordType, parent, category);
                        items.add(newLineItem);
                        break;

                    case 8:
                        parent = workingType;
                        newLineItem = new LineItemCSV(type, parent, category);
                        items.add(newLineItem);
                        break;

                    default:
                        // Handle any other number of leading spaces
                        break;
                }
            }
            csvReader.close();

            if (clearRecords) {
                DataSource.getInstance().deleteAllCategeryRecords();
            }

            rejectedRecordCount = 0;
            addedRecordCount = 0;
           for (int i = 0; i < items.size(); i++){
                
                int matchingRecords = DataSource.getInstance().findCategeryRecords(items.get(i));
                if (matchingRecords == 0) {
                    Boolean goodWrite = DataSource.getInstance().insertCatogeoryRecord(items.get(i));
                    addedRecordCount++;
                    if (!goodWrite) {
                        System.out.println("Error writing to database -- " + items.get(i).toString());
                    }
                }else {
                    rejectedRecordCount++;
                }
            }
            return items;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }
}
