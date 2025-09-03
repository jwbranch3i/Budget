package com.budget.controllers;

import java.io.File;
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
    public void testReadActual() {
        File csvfile = new File("C:\\Dropbox\\JAVA\\budget\\rawData\\Oct2024.csv");

        PrimaryController.readCSVFile(csvfile, LocalDate.now());
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
    public void testReadCSVFile_withValidFile() {
        File csvFile = new File("C:\\Dropbox\\JAVA\\budget\\rawData\\aug2025.csv");
        LocalDate date = LocalDate.of(2025, 8, 1);

        // Should not throw any exceptions
        PrimaryController.readCSVFile(csvFile, date);
    }

    @Test
    public void testReadCSVFile_withNonExistentFile() {
        File csvFile = new File("C:\\Dropbox\\JAVA\\budget\\rawData\\NonExistent.csv");
        LocalDate date = LocalDate.of(2024, 10, 1);

        try {
            PrimaryController.readCSVFile(csvFile, date);
        } catch (Exception e) {
            // Should handle internally, not throw
            assert false : "readCSVFile should not throw exception for non-existent file";
        }
    }

    @Test
    public void testReadCSVFile_withNullFile() {
        LocalDate date = LocalDate.of(2024, 10, 1);

        try {
            PrimaryController.readCSVFile(null, date);
        } catch (Exception e) {
            // Should handle internally, not throw
            assert false : "readCSVFile should not throw exception for null file";
        }
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

@Test
public void testHandleBudgetEditCommit_incomeTable() {
    PrimaryController controller = new PrimaryController();

    // Prepare a mock event for income table
    TreeTableView<com.budget.dataModel.LineItem> treeTable = new TreeTableView<>();
    TableView<com.budget.dataModel.LineItem> totalTable = new TableView<>();
    int dbType = 0; // assuming 0 = income

    // Create a sample LineItem and TreeItem
    com.budget.dataModel.LineItem lineItem = new com.budget.dataModel.LineItem();
    lineItem.setBudget(100.0);
    TreeItem<com.budget.dataModel.LineItem> treeItem = new TreeItem<>(lineItem);

    // Add to treeTable
    TreeItem<com.budget.dataModel.LineItem> root = new TreeItem<>(new com.budget.dataModel.LineItem());
    root.getChildren().add(treeItem);
    treeTable.setRoot(root);

    // Simulate edit commit event
    TreeTableColumn<com.budget.dataModel.LineItem, Double> column = new TreeTableColumn<>("Budget");
    TreeTableColumn.CellEditEvent<com.budget.dataModel.LineItem, Double> event =
        new TreeTableColumn.CellEditEvent<>(treeTable, null, TreeTableColumn.editCommitEvent(), 200.0);

    // Use reflection to call private method
    try {
        java.lang.reflect.Method method = PrimaryController.class.getDeclaredMethod(
            "handleBudgetEditCommit",
            TreeTableColumn.CellEditEvent.class,
            TreeTableView.class,
            TableView.class,
            int.class
        );
        method.setAccessible(true);
        method.invoke(controller, event, treeTable, totalTable, dbType);
    } catch (Exception e) {
        assert false : "Exception thrown during handleBudgetEditCommit: " + e.getMessage();
    }
}

@Test
public void testHandleBudgetEditCommit_mandatoryTable() {
    PrimaryController controller = new PrimaryController();

    TreeTableView<com.budget.dataModel.LineItem> treeTable = new TreeTableView<>();
    TableView<com.budget.dataModel.LineItem> totalTable = new TableView<>();
    int dbType = 1; // assuming 1 = mandatory

    com.budget.dataModel.LineItem lineItem = new com.budget.dataModel.LineItem();
    lineItem.setBudget(150.0);
    TreeItem<com.budget.dataModel.LineItem> treeItem = new TreeItem<>(lineItem);

    TreeItem<com.budget.dataModel.LineItem> root = new TreeItem<>(new com.budget.dataModel.LineItem());
    root.getChildren().add(treeItem);
    treeTable.setRoot(root);

    TreeTableColumn<com.budget.dataModel.LineItem, Double> column = new TreeTableColumn<>("Budget");
    TreeTableColumn.CellEditEvent<com.budget.dataModel.LineItem, Double> event =
        new TreeTableColumn.CellEditEvent<>(treeTable, null, TreeTableColumn.editCommitEvent(), 300.0);

    try {
        java.lang.reflect.Method method = PrimaryController.class.getDeclaredMethod(
            "handleBudgetEditCommit",
            TreeTableColumn.CellEditEvent.class,
            TreeTableView.class,
            TableView.class,
            int.class
        );
        method.setAccessible(true);
        method.invoke(controller, event, treeTable, totalTable, dbType);
    } catch (Exception e) {
        assert false : "Exception thrown during handleBudgetEditCommit: " + e.getMessage();
    }
}

@Test
public void testHandleBudgetEditCommit_discretionaryTable() {
    PrimaryController controller = new PrimaryController();

    TreeTableView<com.budget.dataModel.LineItem> treeTable = new TreeTableView<>();
    TableView<com.budget.dataModel.LineItem> totalTable = new TableView<>();
    int dbType = 2; // assuming 2 = discretionary

    com.budget.dataModel.LineItem lineItem = new com.budget.dataModel.LineItem();
    lineItem.setBudget(50.0);
    TreeItem<com.budget.dataModel.LineItem> treeItem = new TreeItem<>(lineItem);

    TreeItem<com.budget.dataModel.LineItem> root = new TreeItem<>(new com.budget.dataModel.LineItem());
    root.getChildren().add(treeItem);
    treeTable.setRoot(root);

    TreeTableColumn<com.budget.dataModel.LineItem, Double> column = new TreeTableColumn<>("Budget");
    TreeTableColumn.CellEditEvent<com.budget.dataModel.LineItem, Double> event =
        new TreeTableColumn.CellEditEvent<>(treeTable, null, TreeTableColumn.editCommitEvent(), 75.0);

    try {
        java.lang.reflect.Method method = PrimaryController.class.getDeclaredMethod(
            "handleBudgetEditCommit",
            TreeTableColumn.CellEditEvent.class,
            TreeTableView.class,
            TableView.class,
            int.class
        );
        method.setAccessible(true);
        method.invoke(controller, event, treeTable, totalTable, dbType);
    } catch (Exception e) {
        assert false : "Exception thrown during handleBudgetEditCommit: " + e.getMessage();
    }
}



}
