package com.budget.dataModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

/**
 * Handles CSV file importing for budget data. Uses modern Java practices for
 * file reading and data processing.
 */
public class CSVimporter {
    private static final Logger LOGGER = Logger.getLogger(CSVimporter.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
    private static final String CSV_DELIMITER = ",";

    /**
     * Reads and processes a CSV file, converting its contents to LineItemCSV
     * objects.
     * 
     * @param file       The CSV file to read
     * @param importDate The date to associate with imported items
     * @return List of LineItemCSV objects
     * @throws IOException If there's an error reading the file
     */
    public static List<LineItemCSV> importCsvFile(File file, LocalDate importDate) throws IOException {
        if (file == null || !file.exists()) {
            LOGGER.warning("Invalid or non-existent file provided for CSV import");
            return new ArrayList<>();
        }

        List<LineItemCSV> parsedItems = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            List<String[]> allRows;
            try {
                allRows = reader.readAll();
            }
            catch (CsvException e) {
                LOGGER.log(Level.SEVERE, "Error reading CSV file: " + file.getName(), e);
                return parsedItems;
            }

            parsedItems = parseCSVLines(allRows, importDate);

           // updateDatabaseWithParsedItems(parsedItems);

            for (LineItemCSV item : parsedItems) {
                System.out.println(item);
            }
            return parsedItems;
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading CSV file: " + file.getName(), e);
            throw e;
        }
    }

    /**
     * Parses a single CSV line into a LineItemCSV object.
     * 
     * @param line        The CSV line to parse
     * @param defaultDate The default date to use if not specified in CSV
     * @return A new LineItemCSV object, or null if parsing fails
     */
    private static List<LineItemCSV> parseCSVLines(List<String[]> lines, LocalDate defaultDate) {
        List<LineItemCSV> newItems = new ArrayList<>();
        LineItemCSV newLineItem;

        int type = DB.INCOME;
        int newRecordType = DB.INCOME;

        String category = "";
        String parent = "";
        String workingType = "";

        int lineCounter = 0;
        for (String[] line : lines) {
            // String[] fields = line.split(CSV_DELIMITER);
            lineCounter++;

            if (line.length <= 1) {
                continue;
            }

            int leadingSpaces = line[1].length() - line[1].trim().length();
            String trimmedValue = line[1].trim();

            if ("INFLOWS".equals(trimmedValue)) {
                type = DB.INCOME;
                continue;
            }
            else if ("OUTFLOWS".equals(trimmedValue)) {
                type = DB.MANDATORY;
                continue;
            }

            category = trimmedValue;
            if (category.contains("TOTAL") || line.length < 3) {
                continue;
            }

            Double amount = 0.0;
            try {
                amount = Double.parseDouble(line[2].replaceAll(",", ""));
            }
            catch (NumberFormatException e) {
                amount = 0.0;
            }

            switch (leadingSpaces) {
            case 4: // Category level
                parent = category;
                workingType = category;
                newLineItem = new LineItemCSV(type, defaultDate, category, category, amount);
                newLineItem.isMainCategory(true);
                processLineItem(newLineItem);
                break;

            case 8: // Sub-category level
                parent = workingType;
                newLineItem = new LineItemCSV(type, defaultDate, parent, category, amount);
                newLineItem.isMainCategory(false);
                processLineItem(newLineItem);
                break;

            default:
                break;
            }
        }
        ReadData.findMissingCategories(defaultDate);
        return newItems;
    }

    private static void processLineItem(LineItemCSV newLineItem) {
        try {
            // Find or insert category
            LineItemCSV existingCategory = ReadData.categoryFindRecord(newLineItem);
            if (existingCategory.getId() == -1) {
                existingCategory = WriteData.categoryInsertRecord(newLineItem);
            }

            // Find or insert actual record
            LineItemCSV existingActual = ReadData.actualFindCategory(existingCategory);
            if (existingActual.getId() == -1) {
                WriteData.actualInsertRecord(existingCategory);
            }
            else {
                WriteData.actualUpdateAmount(existingActual);
            }
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing line item: " + newLineItem.getCategory(), e);
        }
    }

    private static void updateDatabaseWithParsedItems(List<LineItemCSV> parsedItems) {
        LineItemCSV existingCategory = new LineItemCSV();
        LineItemCSV existingActual = new LineItemCSV();

        for (LineItemCSV newLineItem : parsedItems) {
            // if category not in category table insert it
            existingCategory = ReadData.categoryFindRecord(newLineItem);
            if ((existingCategory.getId() == -1)) {
                existingCategory = WriteData.categoryInsertRecord(newLineItem);
            }

            // if the category is not in the actual
            // table, insert it
            existingActual = ReadData.actualFindCategory(existingCategory);
            if (existingActual.getId() == -1) {
                WriteData.actualInsertRecord(existingActual);
            }
            else {
                WriteData.actualUpdateAmount(existingActual);
            }
        }
    }

    /**
     * Parses type string to integer type.
     */
    private static int parseType(String type) {
        return switch (type.toLowerCase().trim()) {
        case "income" -> 0;
        case "mandatory" -> 1;
        case "discretionary" -> 2;
        default -> -1;
        };
    }

    /**
     * Parses date string to LocalDate.
     */
    private static LocalDate parseDate(String dateStr, LocalDate defaultDate) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        }
        catch (Exception e) {
            LOGGER.warning("Invalid date format: " + dateStr + ". Using default date.");
            return defaultDate;
        }
    }
}
