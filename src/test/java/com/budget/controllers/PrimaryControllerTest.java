package com.budget.controllers;

import java.time.LocalDate;

import com.budget.dataModel.DataSource;
import com.budget.dataModel.DatabaseDataResult;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javafx.application.Platform;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

public class PrimaryControllerTest {
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
    public void testGetTableRows() {

        LocalDate inDate = LocalDate.of(2025, 6, 1);
        // LocalDate inDate = LocalDate.of(2024, 9, 1);

        PrimaryController controller = new PrimaryController();
      //  controller.initialize();

        controller.readFromDatabase(inDate);
    }



    @Test
    public void testLoadDatabaseData_withValidDate() {
        PrimaryController controller = new PrimaryController();
        LocalDate date = LocalDate.of(2025, 5, 1);

        // loadDatabaseData is private, so use reflection to invoke it
        try {
            java.lang.reflect.Method method = PrimaryController.class.getDeclaredMethod("loadDatabaseData", LocalDate.class);
            method.setAccessible(true);
            Object result = method.invoke(controller, date);

            assert result != null : "loadDatabaseData should return a non-null DatabaseDataResult";
            assert result.getClass().getSimpleName().equals("DatabaseDataResult") : "Result should be of type DatabaseDataResult";

            DatabaseDataResult dataResult = (DatabaseDataResult) result;
            System.out.println("incomeTotals: " + dataResult.getIncomeTotals());
            System.out.println("mandatoryTotals: " + dataResult.getMandatoryTotals());
            System.out.println("discretionaryTotals: " + dataResult.getDiscretionaryTotals());


           
        } catch (Exception e) {
            assert false : "Exception thrown during loadDatabaseData: " + e.getMessage();
        }
    }









}
