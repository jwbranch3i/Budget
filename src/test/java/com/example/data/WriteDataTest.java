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
        // Call the method being tested
        boolean result = WriteData.autualUpdateAmount(14, 12.36);

        assertTrue(result);
    }

    @Test
    public void testActualInsertRecord() {
        // Create a test LineItemCSV object
        LineItemCSV category = new LineItemCSV();
        category.setId(483);
        category.setType(1);
        category.setDate(LocalDate.now());
        category.setParent("Parent");
        category.setCategory("Category");
        category.setAmount(100.0);

        // Create a test LocalDate object
        LocalDate inDate = LocalDate.now();

        // Call the method being tested
        LineItemCSV result = WriteData.actualInsertRecord(category);

        // Assert the expected result
        assertNotNull(result);
        assertEquals(1, result.getType());
        assertEquals(inDate, result.getDate());
        assertEquals("Parent", result.getParent());
        assertEquals("Category", result.getCategory());
        assertEquals(100.0, result.getAmount(), 0.0);

    }
}
