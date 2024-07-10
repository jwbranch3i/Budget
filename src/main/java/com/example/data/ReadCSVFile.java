package com.example.data;

import java.util.ArrayList;
import java.io.FileReader;

import com.opencsv.CSVReader;

public class ReadCSVFile {

    public static ArrayList<Item> readDataLineByLine(String file) {
        ArrayList<Item> items = new ArrayList<Item>();

        try {

            // Create an object of filereader
            // class with CSV file as a parameter.
            FileReader filereader = new FileReader(file);

            // create csvReader object passing
            // file reader as a parameter
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;

            // we are going to read data line by line
            while ((nextRecord = csvReader.readNext()) != null) {
                Item newItem = new Item(nextRecord[0], nextRecord[1], Double.parseDouble(nextRecord[2]));
                items.add(newItem);

               System.out.println(newItem.getType() + " " + newItem.getCategory() + " " + newItem.getCost());
            }
            csvReader.close();
            return items;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }
}
