package com.example.data;

import org.junit.Test;

import javafx.application.Platform;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.After;
import org.junit.BeforeClass;

public class WriteDataTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        if (!DataSource.getInstance().open()) {
            System.out.println("FATAL ERROR: Couldn't connect to database");
            Platform.exit();
        }
    }

    @After
    public void tearDown() throws Exception {
        DataSource.getInstance().close();
    }

    @Test
    public void testCategoryInsertRecord() {
        // Create a test LineItemCSV object

        LineItemCSV item = new LineItemCSV();
        item.setType(1);
        item.setParent("Parent");
        item.setCategory("Category");

        // Call the method being tested
        LineItemCSV result = WriteData.categoryInsertRecord(item);

        // Assert the expected result
        assertNotNull(result);
        assertEquals(1, result.getType());
        assertEquals("Parent", result.getParent());
        assertEquals("Category", result.getCategory());
        // Add more assertions if needed
    }

    @Test
    public void testAutualUpdateAmount() {
        // Create a test LineItemCSV object
        LineItemCSV item = new LineItemCSV();
        item.setId(1);
        item.setAmount(100.0);

        // Call the method being tested
        boolean result = WriteData.autualUpdateAmount(item);

        // Assert the expected result
        assertTrue(result);
    }

    @Test
    public void testActualInsertRecord() {

        // Create a test LineItemCSV object
        LineItemCSV newActual = new LineItemCSV();
        newActual.setId(1);
        newActual.setAmount(100.0);
        newActual.setDate(LocalDate.now());
        newActual.setCategory("Category");
        newActual.setParent("Parent");
        newActual.setType(1);

        LineItemCSV existingCategory = new LineItemCSV();
        existingCategory.setId(1);
        existingCategory.setAmount(100.0);
        existingCategory.setDate(LocalDate.now());
        existingCategory.setCategory("Category");
        existingCategory.setParent("Parent");
        existingCategory.setType(1);

        // Call the method being tested
        LineItemCSV result = WriteData.actualInsertRecord(newActual, existingCategory);

        // Assert the expected result
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(100.0, result.getAmount(), 0.0);
        assertEquals(LocalDate.now(), result.getDate());
        assertEquals("Category", result.getCategory());
        assertEquals("Parent", result.getParent());
        assertEquals(1, result.getType());
        // Add more assertions if needed
    }
}
