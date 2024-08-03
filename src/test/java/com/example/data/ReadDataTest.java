package com.example.data;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javafx.application.Platform;

public class ReadDataTest {
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        if (!DataSource.getInstance().open()) {
            System.out.println("FATAL ERROR: Couldn't connect to database");
            Platform.exit();
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        DataSource.getInstance().close();
    }

    @Test
    public void testActualFindCategory() {
        // Create a test LineItemCSV object
        LineItemCSV item = new LineItemCSV();
        item.setId(1);
        item.setDate(LocalDate.of(2021, 1, 1));

        // Call the method being tested
        LineItemCSV result = ReadData.actualFindCategory(item);

        // Assert the expected result
        assertEquals(-1, result.getId());

        // Create a test LineItemCSV object
        item.setId(640);
        item.setDate(LocalDate.of(2024, 7, 1));

        // Call the method being tested
        result = ReadData.actualFindCategory(item);

        // Assert the expected result
        assertEquals(1130, result.getId());

    }

    @Test
    public void testBudgetFindCategory() {
        // Create a test LineItemCSV object
        LineItemCSV item = new LineItemCSV();
        item.setId(1);
        item.setDate(LocalDate.of(2021, 1, 1));

        // Call the method being tested
        LineItemCSV result = ReadData.budgetFindCategory(item);

        // Assert the expected result
        assertEquals(-1, result.getId());

        // Create a test LineItemCSV object
        item.setId(640);
        item.setDate(LocalDate.of(2024, 7, 1));

        // Call the method being tested
        result = ReadData.budgetFindCategory(item);

        // Assert the expected result
        assertEquals(58, result.getId());
    }

    @Test
    public void testCategoryFindRecord() {
        // Create a test LineItemCSV object
        LineItemCSV item = new LineItemCSV();

        item.setParent("");
        item.setCategory("Groceries");

        // Call the method being tested
        LineItemCSV result = ReadData.categoryFindRecord(item);

        // Assert the expected result
        assertEquals(-1, result.getId());

        // Create a test LineItemCSV object
       item.setParent("");
        item.setCategory("Auto");

        // Call the method being tested
        result = ReadData.categoryFindRecord(item);

        // Assert the expected result
        assertEquals(641, result.getId());

        // Create a test LineItemCSV object
        item.setParent("Auto");
        item.setCategory("Fuel");

        // Call the method being tested
        result = ReadData.categoryFindRecord(item);

        // Assert the expected result
        assertEquals(642, result.getId());
    }

}
