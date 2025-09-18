package com.budget.dataModel;

import java.time.LocalDate;

import com.budget.controllers.PrimaryController;
import com.budget.dataModel.DataSource;
import com.budget.dataModel.UIData;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javafx.application.Platform;

public class UIDataTest {
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
    public void testUpdateTableTotal() {

        LocalDate inDate = LocalDate.of(2024, 10, 1);

        PrimaryController controller = new PrimaryController();
        controller.initialize();
        controller.readFromDatabase(inDate);

        UIData.updateGrandTotalFromTotalTables(
            controller.getTableIncomeTotal(),
            controller.getTableMandatoryTotal(),
            controller.getTableDiscretionaryTotal(),
            controller.getTableGrandTotal());
        
        System.out.println("testUpdateTableTotal - *** finish ***");
    }
}
