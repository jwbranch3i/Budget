package com.budget.dataModel;

import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;
import java.util.List;

import com.budget.Util;
import com.budget.dataModel.DataSource;
import com.budget.dataModel.LineItem;
import com.budget.dataModel.ReadData;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;

public class ReadDataTest {
  
   @BeforeClass
    public static void setUpBeforeClass() throws Exception {
      //  DataSource.setDatabaseName("testBudget.db"); 
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
    public void testGetTableAmountsTree() {
        int tableType = 2;

        // LocalDate inDate = LocalDate.of(2025, 5, 1);
        LocalDate inDate = LocalDate.of(2025, 5, 1);

        TreeItem<LineItem> result;

      //  String test = DB.GET_ACTUAL_AND_BUDGET_AMOUNTS;

        result = ReadData.getTableAmountsTree(tableType, inDate,  true);

        // print each item in result
        Util.printTreeItems(result);

        // Assert the expected result
        assertNotNull(result);

    }


 

    @Test
    public void testgetYears() {
        List<String> result;

        result = ReadData.getYears();

        // print each item in result
        System.out.println("Years:");
        for (String item : result) {
            System.out.println(item);
            assertNotNull(item);
        }

    }
}
