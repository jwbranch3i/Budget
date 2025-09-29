package com.budget.dataModel;

import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import com.budget.GlobalVariables;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javafx.application.Platform;

public class WriteDataTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        GlobalVariables.enableDebugLogging();

        if (!DataSource.getInstance().open()) {
            System.out.println("FATAL ERROR: Couldn't connect to database");
            Platform.exit();
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        DataSource.getInstance().close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateLineItemBudget_ActualTable_NullItem_ThrowsException() {
        WriteData.updateLineItem(null);
    }

  @Test
    public void testUpdateLineItemBudget_ActualTable_ValidItem_ReturnsTrue() throws Exception {

        LineItemCSV item = new LineItemCSV(0, LocalDate.now(), "Amazon", "Shopping", 50.00);

        LineItemCSV result = WriteData.actualInsertRecord(item);

        LineItem newItem = result.toLineItem();
        newItem.setBudget(25.00);
        System.out.println(newItem.toString());

        boolean answ = WriteData.updateLineItem(newItem);

        assertTrue(answ);
        
    }

    @Test
       public void testUpdateCategoryType() {
        LineItem testItem = new LineItem(1, LocalDate.now(), "Test Store", "Test Category", 100.00, 100.00);
        testItem.setId(1057);

        boolean result = WriteData.updateCategoryType(testItem);

        assertTrue(result);
       }
}
