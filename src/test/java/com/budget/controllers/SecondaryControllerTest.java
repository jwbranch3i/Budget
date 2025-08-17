package com.budget.controllers;

import java.util.List;

import com.budget.dataModal.Categories;
import com.budget.dataModal.DataSource;
import com.budget.dataModal.ReadData;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;

public class SecondaryControllerTest {

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
    public void testbuildCategoryTree() {
        // This method should be tested to ensure it builds the category tree correctly.
        // You can create a mock or a test case that checks the structure of the tree.
        SecondaryController controller = new SecondaryController();

        List<Categories> result = ReadData.getCategories();
        
        TreeItem<Categories> resultBuild = controller.buildCategoryTree(result);
        
        System.out.println(resultBuild);
       
    }
}
