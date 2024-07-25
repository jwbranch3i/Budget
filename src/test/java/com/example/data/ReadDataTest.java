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

        item.setId(483);
        item.setDate(LocalDate.now());

        // Call the method being tested
        int result = ReadData.actualFindCategory(item);

        // Add assertions if needed
        assertNotEquals(-1, result);

    }


    @Test
    public void testCategoryFindRecord() {
            // Create a test LineItemCSV object
            LineItemCSV item = new LineItemCSV();
            item.setParent("Parent");
            item.setCategory("Category");
    
            // Call the method being tested
            int result = ReadData.categoryFindRecord(item);
    
            // Assert the expected result
            assertEquals(485, result);
        }
    
     
    
}
