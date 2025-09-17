package com.budget.dataModel;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javafx.application.Platform;

public class CSVimporterTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DataSource.setDatabaseName("testBudget.db"); 
        if (!DataSource.getInstance().open()) {
            System.out.println("FATAL ERROR: Couldn't connect to database");
            Platform.exit();
        }
        System.out.println("*** Connected to " + DataSource.getDatabaseName());
    }

    @AfterClass
    public static void tearDown() throws Exception {
        DataSource.getInstance().close();
    }

    @Test
    public void testImportCsvFile_ValidFile_ReturnsList() throws IOException {
        // Arrange
        File file = new File("C:\\Dropbox\\JAVA\\budget\\rawData\\May2025.csv");
        LocalDate importDate = LocalDate.of(2025, 05, 01);

        // Act
        List<LineItemCSV> items = CSVimporter.importCsvFile(file, importDate);

        for (LineItemCSV item : items) {
            System.out.println(item);
        }
        // Assert
        assertNotNull(items);
        assertTrue(items.size() > 0);
    }

    @Test
    public void testImportCsvFile_InvalidFile_ReturnsEmptyList() throws IOException {
        // Arrange
        File file = new File("path/to/invalid/csv/file.csv");
        LocalDate importDate = LocalDate.now();

        // Act
        List<LineItemCSV> items = CSVimporter.importCsvFile(file, importDate);

        // Assert
        assertNotNull(items);
        assertTrue(items.isEmpty());
    }

    @Test
    public void testImportCsvFile_NullFile_ReturnsEmptyList() throws IOException {
        // Arrange
        File file = null;
        LocalDate importDate = LocalDate.now();

        // Act
        List<LineItemCSV> items = CSVimporter.importCsvFile(file, importDate);

        // Assert
        assertNotNull(items);
        assertTrue(items.isEmpty());
    }

}