package com.budget.controllers;

import com.budget.dataModal.DataSource;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import javafx.application.Platform;

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


}
