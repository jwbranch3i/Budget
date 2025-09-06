package com.budget.dataModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

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

        List<LineItemCSV> items = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Skip header row
            reader.readLine();

            //Process each line
            reader.lines().filter(line -> line != null && !line.trim().isEmpty())
                    .map(line -> parseCsvLine(line, importDate))
                    .filter(item -> item != null && item.isValid())
                    .forEach(items::add);


            LOGGER.info(String.format("Successfully imported %d items from CSV file", items.size()));
            return items;
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
    private static LineItemCSV parseCsvLine(String line, LocalDate defaultDate) {
        try {
            String[] fields = line.split(CSV_DELIMITER);
            if (fields.length < 4) {
                LOGGER.warning("Invalid CSV line format: " + line);
                return null;
            }

            LineItemCSV item = new LineItemCSV();

            // Set required fields
            item.setCategory(fields[0].trim());
            item.setParent(fields[1].trim());
            item.setAmount(parseAmount(fields[2].trim()));
            item.setType(parseType(fields[3].trim()));

            // Set date (use defaultDate if not provided in CSV)
            item.setDate(fields.length > 4 ? parseDate(fields[4].trim(), defaultDate) : defaultDate);

            // Set optional fields with defaults
            item.setIncludeInTotal(fields.length > 5 ? Boolean.parseBoolean(fields[5].trim()) : true);
            item.setHide(fields.length > 6 ? Boolean.parseBoolean(fields[6].trim()) : false);

            return item;
        }
        catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error parsing CSV line: " + line, e);
            return null;
        }
    }

    /**
     * Parses amount string to double, handling currency symbols and commas.
     */
    private static double parseAmount(String amount) {
        try {
            return Double.parseDouble(amount.replaceAll("[^\\d.-]", ""));
        }
        catch (NumberFormatException e) {
            LOGGER.warning("Invalid amount format: " + amount);
            return 0.0;
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
