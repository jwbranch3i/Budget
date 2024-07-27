package com.example.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.time.LocalDate;

import org.junit.After;
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

    @After
    public void tearDown() throws Exception {
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
        assertEquals(1, result.getId());
    }


    @Test
    public void testCategoryFindRecord() {
        // Create a test LineItemCSV object
        LineItemCSV item = new LineItemCSV();
        item.setParent("Test Parent");
        item.setCategory("Test Category");

        // Call the method being tested
        LineItemCSV result = ReadData.categoryFindRecord(item);

        // Assert the expected result
        assertNotEquals(-1, result.getId());
    }
     
    
}
