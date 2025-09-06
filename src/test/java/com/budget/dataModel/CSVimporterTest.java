package com.budget.dataModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.junit.Test;

public class CSVimporterTest {

    @Test
    public void testImportCsvFile_ValidFile_ReturnsList() throws IOException {
        // Arrange
        File file = new File ("C:\\Dropbox\\JAVA\\budget\\rawData\\May2025.csv");
        LocalDate importDate = LocalDate.of(2025, 05, 01);

        // Act
        List<LineItemCSV> items = CSVimporter.importCsvFile(file, importDate);

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